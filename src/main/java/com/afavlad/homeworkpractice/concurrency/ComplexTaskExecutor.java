package com.afavlad.homeworkpractice.concurrency;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Оркестратор выполнения "сложных задач".
 * <p>
 * Для каждой серии задач метод {@link #executeTasks(int)}:
 * <ul>
 *     <li>создаёт пул потоков,</li>
 *     <li>создаёт {@link CyclicBarrier} для синхронизации задач,</li>
 *     <li>запускает указное количество задач {@link ComplexTask},</li>
 *     <li>ждёт завершения всех задач и объединяет их результаты в barrier-action.</li>
 * </ul>
 */
public class ComplexTaskExecutor {

  private final int poolSize;

  public ComplexTaskExecutor(int poolSize) {
    this.poolSize = poolSize;
  }

  /**
   * Запускает указанное количество задач, синхронизируя их с помощью {@link CyclicBarrier}.
   * <p>
   * Алгоритм:
   * <ol>
   *     <li>Создаётся массив для частичных результатов всех задач.</li>
   *     <li>Создаётся барьер, ожидающий numberOfTasks задач и выполняющий
   *         объединение результатов (barrier action).</li>
   *     <li>Создаётся пул потоков ограниченного размера.</li>
   *     <li>Каждой задаче выдаётся свой индекс и входные данные, задача отправляется в пул.</li>
   *     <li>После отправки всех задач пул закрывается для новых задач и ожидается
   *         завершение всех текущих.</li>
   * </ol>
   *
   * @param numberOfTasks количество задач, которые нужно запустить параллельно
   */
  public void executeTasks(int numberOfTasks) {
    System.out.println(Thread.currentThread().getName() + " started executeTasks with " + numberOfTasks + " tasks.");

    int[] partialResults = createPartialResultsArray(numberOfTasks);
    CyclicBarrier barrier = createBarrier(numberOfTasks, partialResults);
    ExecutorService executor = createExecutor(numberOfTasks);

    try {
      submitTasks(numberOfTasks, partialResults, barrier, executor);
    } finally {
      executor.shutdown();
    }
    waitForCompletion(executor);

    System.out.println(Thread.currentThread().getName() + " finished executeTasks.");
  }

  /**
   * Создаёт массив для хранения частичных результатов всех задач.
   * Индекс элемента массива соответствует идентификатору задачи.
   *
   * @param numberOfTasks количество задач
   * @return массив нужного размера, проинициализированный нулями
   */
  private int[] createPartialResultsArray(int numberOfTasks) {
    return new int[numberOfTasks];
  }

  /**
   * Создаёт {@link CyclicBarrier}, который:
   * <ul>
   *     <li>ожидает numberOfTasks задач, вызывающих {@code await()},</li>
   *     <li>после прихода последней задачи выполняет barrier-action
   *         {@link #combineResults(int[])} для объединения результатов.</li>
   * </ul>
   *
   * @param numberOfTasks  количество задач-участников барьера
   * @param partialResults общий массив с частичными результатами всех задач
   * @return новый экземпляр {@link CyclicBarrier}
   */
  private CyclicBarrier createBarrier(int numberOfTasks, int[] partialResults) {
    return new CyclicBarrier(numberOfTasks, () -> combineResults(partialResults));
  }

  /**
   * Создаёт пул потоков для выполнения задач.
   * Реальный размер пула ограничивается минимумом из:
   * <ul>
   *     <li>глобального параметра poolSize,</li>
   *     <li>количества задач numberOfTasks.</li>
   * </ul>
   *
   * @param numberOfTasks количество задач в текущем запуске
   * @return настроенный {@link ExecutorService} с фиксированным числом потоков
   */
  private ExecutorService createExecutor(int numberOfTasks) {
    int actualPoolSize = Math.min(poolSize, numberOfTasks);
    return Executors.newFixedThreadPool(actualPoolSize);
  }

  /**
   * Создаёт и отправляет задачи в пул потоков.
   * <p>
   * Для каждой задачи:
   * <ul>
   *     <li>задаётся уникальный идентификатор taskId (0..numberOfTasks-1),</li>
   *     <li>формируются входные данные (в примере просто i+1),</li>
   *     <li>создаётся {@link ComplexTask},</li>
   *     <li>задача отправляется на исполнение через {@link ExecutorService#submit(Runnable)}.</li>
   * </ul>
   *
   * @param numberOfTasks  общее количество задач
   * @param partialResults общий массив для записи частичных результатов
   * @param barrier        барьер, с которым синхронизируются задачи
   * @param executor       пул потоков, в который отправляются задачи
   */
  private void submitTasks(int numberOfTasks,
      int[] partialResults,
      CyclicBarrier barrier,
      ExecutorService executor) {
    for (int i = 0; i < numberOfTasks; i++) {
      int taskId = i;
      int inputData = i + 1;
      ComplexTask task = new ComplexTask(taskId, inputData, partialResults, barrier);
      executor.submit(task);
    }
  }

  /**
   * Ожидает завершения всех задач в пуле потоков.
   * <p>
   * Использует {@link ExecutorService#awaitTermination(long, TimeUnit)} для ожидания
   * в течение ограниченного времени. Если задачи не успели завершиться,
   * выводит диагностическое сообщение.
   *
   * @param executor пул потоков, который был ранее закрыт методом {@link ExecutorService#shutdown()}
   */
  private void waitForCompletion(ExecutorService executor) {
    try {
      boolean finished = executor.awaitTermination(5, TimeUnit.MINUTES);
      if (!finished) {
        System.out.println(Thread.currentThread().getName() + " timeout while waiting for tasks to finish.");
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  /**
   * Объединяет все частичные результаты задач.
   * <p>
   * В текущем примере просто суммирует все элементы массива и печатает итоговую сумму.
   * Вызывается как barrier-action в {@link CyclicBarrier}, то есть гарантированно
   * только после того, как все задачи записали свои результаты в массив.
   *
   * @param partialResults массив, содержащий результаты всех задач
   */
  private void combineResults(int[] partialResults) {
    int sum = 0;
    for (int value : partialResults) {
      sum += value;
    }
    System.out.println(Thread.currentThread().getName() + " barrier action: combined sum = " + sum);
  }
}
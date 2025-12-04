package com.afavlad.homeworkpractice.concurrency;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ComplexTaskExecutor {

  private final int poolSize;

  public ComplexTaskExecutor(int poolSize) {
    this.poolSize = poolSize;
  }

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

  private int[] createPartialResultsArray(int numberOfTasks) {
    return new int[numberOfTasks];
  }

  private CyclicBarrier createBarrier(int numberOfTasks, int[] partialResults) {
    return new CyclicBarrier(numberOfTasks, () -> combineResults(partialResults));
  }

  private ExecutorService createExecutor(int numberOfTasks) {
    int actualPoolSize = Math.min(poolSize, numberOfTasks);
    return Executors.newFixedThreadPool(actualPoolSize);
  }

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

  private void combineResults(int[] partialResults) {
    int sum = 0;
    for (int value : partialResults) {
      sum += value;
    }
    System.out.println(Thread.currentThread().getName() + " barrier action: combined sum = " + sum);
  }
}
package com.afavlad.homeworkpractice.concurrency;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class ComplexTask implements Runnable {

  private final int taskId;
  private final int inputData;
  private final int[] partialResults;
  private final CyclicBarrier barrier;

  public ComplexTask(int taskId, int inputData, int[] partialResults, CyclicBarrier barrier) {
    this.taskId = taskId;
    this.inputData = inputData;
    this.partialResults = partialResults;
    this.barrier = barrier;
  }

  public void execute() {
    try {
      System.out.println(Thread.currentThread().getName() + " executing task " + taskId + " with input " + inputData);
      Thread.sleep(300 + (int) (Math.random() * 700));
      int result = inputData * inputData;
      partialResults[taskId] = result;
      System.out.println(Thread.currentThread().getName() + " finished task " + taskId + " with result " + result);
      barrier.await();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    } catch (BrokenBarrierException e) {
      System.out.println(Thread.currentThread().getName() + " barrier is broken for task " + taskId);
    }
  }

  @Override
  public void run() {
    execute();
  }
}

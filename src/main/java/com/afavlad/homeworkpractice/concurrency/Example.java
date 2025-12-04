package com.afavlad.homeworkpractice.concurrency;

import com.afavlad.homeworkpractice.concurrency.impl.CustomBlockingQueueImpl;

public class Example {

  public static void main(String[] args) {
    CustomBlockingQueue<Runnable> blockingQueue = new CustomBlockingQueueImpl<>(5);

    for (int i = 1; i < 3; i++) {
      int workerId = i;
      new Thread(() -> {
        try {
          while (true) {
            Runnable task = blockingQueue.dequeue();
            System.out.println("Worker " + workerId + " got task");
            task.run();
          }
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }
      }).start();
    }

    for (int i = 1; i < 11; i++) {
      int taskId = i;
      try {
        blockingQueue.enqueue(() -> {
          System.out.println("  Running task " + taskId);
        });
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    }
  }

}

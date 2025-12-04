package com.afavlad.homeworkpractice.concurrency.impl;

import com.afavlad.homeworkpractice.concurrency.CustomBlockingQueue;
import java.util.ArrayDeque;
import java.util.Queue;

public class CustomBlockingQueueImpl<T> implements CustomBlockingQueue<T> {

  private final Queue<T> queue = new ArrayDeque<>();
  private final int capacity;

  public CustomBlockingQueueImpl(int capacity) {
    if(capacity <= 0) {
      throw new IllegalArgumentException("Capacity must be greater than zero");
    }
    this.capacity = capacity;
  }

  @Override
  public synchronized void enqueue(T element) throws InterruptedException {
    while (queue.size() == capacity) {
      wait();
    }
    queue.add(element);
    notifyAll();
  }

  @Override
  public synchronized T dequeue() throws InterruptedException {
    while (queue.isEmpty()) {
      wait();
    }
    T item = queue.poll();
    notifyAll();
    return item;
  }

  @Override
  public synchronized int size() {
    return queue.size();
  }
}

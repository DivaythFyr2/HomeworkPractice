package com.afavlad.homeworkpractice.concurrency;

public interface CustomBlockingQueue<T> {

  void enqueue(T element) throws InterruptedException;

  T dequeue() throws InterruptedException;

  int size();

}

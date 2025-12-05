package com.afavlad.homeworkpractice.stream;

import java.util.concurrent.ForkJoinPool;

public class ForkJoinPoolExample {

  public static void main(String[] args) {
    int n = 10;

    ForkJoinPool forkJoinPool = new ForkJoinPool();
    FactorialTask factorialTask = new FactorialTask(1, n);

    long result = forkJoinPool.invoke(factorialTask);

    System.out.println("Факториал " + n + "! = " + result);
  }
}

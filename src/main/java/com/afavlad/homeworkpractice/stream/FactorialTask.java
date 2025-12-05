package com.afavlad.homeworkpractice.stream;

import java.util.concurrent.RecursiveTask;

public class FactorialTask extends RecursiveTask<Long> {

  private final int start;
  private final int end;
  private static final int THRESHOLD = 5;

  public FactorialTask(int start, int end) {
    this.start = start;
    this.end = end;
  }

  @Override
  protected Long compute() {
    if(end - start <= THRESHOLD) {
      long result = 1;
      for (int i = start; i <=end ; i++) {
        result *= i;
      }
      return result;
    }

    int mid = (start + end) / 2;

    FactorialTask leftTask = new FactorialTask(start, mid);
    FactorialTask rightTask = new FactorialTask(mid + 1, end);

    leftTask.fork();
    long rightResult = rightTask.compute();
    long leftResult = leftTask.join();

    return leftResult * rightResult;
  }
}

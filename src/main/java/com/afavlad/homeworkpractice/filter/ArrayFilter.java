package com.afavlad.homeworkpractice.filter;

import java.util.Arrays;

public class ArrayFilter {

  public static <T> T[] filter(T[] array, Filter<T> filter) {
    if(array == null) {
      throw new IllegalArgumentException("Array is null");
    }

    T[] result = Arrays.copyOf(array, array.length);

    for(int i = 0; i < array.length; i++) {
      result[i] = filter.apply(result[i]);
    }

    return result;
  }

}

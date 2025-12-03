package com.afavlad.homeworkpractice.count;

import java.util.HashMap;
import java.util.Map;

public class ElementsCounter {

  public static <T> Map<T, Integer> countOfElements(T[] array) {
    Map<T, Integer> map = new HashMap<>();
    for(int i = 0; i < array.length; i++) {
      map.put(array[i], map.getOrDefault(array[i], 0) + 1);
    }
    return map;
  }

}

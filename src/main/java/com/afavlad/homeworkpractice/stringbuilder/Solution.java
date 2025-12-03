package com.afavlad.homeworkpractice.stringbuilder;

import com.afavlad.homeworkpractice.filter.ArrayFilter;

public class Solution {

  public static void main(String[] args) {
    System.out.println("========== Задание 1  ==========");
    CustomStringBuilder builder = new CustomStringBuilder("Hello World");
    System.out.println(builder);
    System.out.println("====== builder перед append() - " + builder);
    builder.append(" Perfect");
    System.out.println("====== builder перед undo()   - " + builder);
    builder.undo();
    System.out.println("====== builder после undo()   - " + builder);

    System.out.println("========== Задание 2  ==========");
    String[] strings = {"rimac","aspark","oracle","vegas"};
    String[] result = ArrayFilter.filter(strings, String::toUpperCase);
    for(String i: result) {
      System.out.println(i);
    }
  }

}

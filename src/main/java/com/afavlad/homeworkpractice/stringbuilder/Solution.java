package com.afavlad.homeworkpractice.stringbuilder;

public class Solution {

  public static void main(String[] args) {
    CustomStringBuilder builder = new CustomStringBuilder("Hello World");
    System.out.println(builder);
    System.out.println("====== builder перед append() - " + builder);
    builder.append(" Perfect");
    System.out.println("====== builder перед undo()   - " + builder);
    builder.undo();
    System.out.println("====== builder после undo()   - " + builder);
  }

}

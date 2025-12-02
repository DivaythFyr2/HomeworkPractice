package com.afavlad.homeworkpractice.stringbuilder;

import java.util.ArrayDeque;
import java.util.Deque;

public final class CustomStringBuilder implements CharSequence {
  private StringBuilder builder;
  private final Deque<String> snapshots = new ArrayDeque<>();

  public CustomStringBuilder() {
    this.builder = new StringBuilder();
  }

  public CustomStringBuilder(String string) {
    this.builder = new StringBuilder(string);
  }

  @Override
  public int length() {
    return builder.length();
  }

  private StringBuilder getBuilder() {
    return builder;
  }

  @Override
  public char charAt(int index) {
    return builder.charAt(index);
  }

  @Override
  public CharSequence subSequence(int start, int end) {
    return builder.subSequence(start, end);
  }

  @Override
  public String toString() {
    return builder.toString();
  }

  /**
   * Сохраняем текущее состояние перед изменениями.
   */
  private void saveSnapshot() {
    snapshots.push(builder.toString());
  }

  /**
   * Откатываем к предыдущему состоянию
   */
  public void undo() {
    if (snapshots.isEmpty()) {
      return;
    }
    String previous = snapshots.pop();
    this.builder = new  StringBuilder(previous);
  }

  /**
   * Проверка, есть ли что откатывать.
   *
   */
  public boolean canUndo() {
    return !snapshots.isEmpty();
  }

  /**
   * Очистка всей истории снимков.
   */
  public void clearHistory() {
    snapshots.clear();
  }

  public CustomStringBuilder append(String str) {
    saveSnapshot();
    builder.append(str);
    return this;
  }

  public CustomStringBuilder append(char ch) {
    saveSnapshot();
    builder.append(ch);
    return this;
  }

  public CustomStringBuilder append(int i) {
    saveSnapshot();
    builder.append(i);
    return this;
  }

  public CustomStringBuilder append(Object obj) {
    saveSnapshot();
    builder.append(obj);
    return this;
  }

  public CustomStringBuilder insert(int offset, String str) {
    saveSnapshot();
    builder.insert(offset, str);
    return this;
  }

  public CustomStringBuilder insert(int offset, char ch) {
    saveSnapshot();
    builder.insert(offset, ch);
    return this;
  }

  public CustomStringBuilder delete(int start, int end) {
    saveSnapshot();
    builder.delete(start, end);
    return  this;
  }

  public CustomStringBuilder deleteCharAt(int index) {
    saveSnapshot();
    builder.deleteCharAt(index);
    return this;
  }

  public CustomStringBuilder replace(int start, int end, String str) {
    saveSnapshot();
    builder.replace(start, end, str);
    return this;
  }

  public CustomStringBuilder reverse() {
    saveSnapshot();
    builder.reverse();
    return this;
  }

  public void setCharAt(int index, char ch) {
    saveSnapshot();
    builder.setCharAt(index, ch);
  }
}

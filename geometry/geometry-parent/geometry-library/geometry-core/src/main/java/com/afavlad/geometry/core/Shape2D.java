package com.afavlad.geometry.core;

public interface Shape2D {

  double area();

  double perimeter();

  default String description() {
    return getClass().getSimpleName() +
        " [area=" + area() + ", perimeter=" + perimeter() + "]";
  }
}

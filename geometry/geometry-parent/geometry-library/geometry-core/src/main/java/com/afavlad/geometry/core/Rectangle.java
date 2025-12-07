package com.afavlad.geometry.core;

public class Rectangle implements Shape2D {

  private final double width;
  private final double height;

  public Rectangle(double width, double height) {
    if(width <= 0 || height <= 0) {
      throw new IllegalArgumentException("Width and height must be positive");
    }
    this.width = width;
    this.height = height;
  }

  public double getWidth() {
    return width;
  }

  public double getHeight() {
    return height;
  }

  @Override
  public double area() {
    return width * height;
  }

  @Override
  public double perimeter() {
    return 2 * (width + height);
  }

  @Override
  public String toString() {
    return description();
  }
}

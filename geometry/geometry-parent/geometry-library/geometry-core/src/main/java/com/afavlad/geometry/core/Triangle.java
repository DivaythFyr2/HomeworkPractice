package com.afavlad.geometry.core;

public class Triangle implements Shape2D {

  private final double a;
  private final double b;
  private final double c;

  public Triangle(double a, double b, double c) {
    if (a <= 0 || b <= 0 || c <= 0) {
      throw new IllegalArgumentException("Sides must be positive");
    }
    if (a + b <= c || a + c <= b || b + c <= a) {
      throw new IllegalArgumentException("Triangle inequality violated");
    }
    this.a = a;
    this.b = b;
    this.c = c;
  }

  public double getA() {
    return a;
  }

  public double getB() {
    return b;
  }

  public double getC() {
    return c;
  }

  @Override
  public double area() {
    double p = perimeter() / 2.0;
    return Math.sqrt(p * (p - a) * (p - b) * (p - c));
  }

  @Override
  public double perimeter() {
    return a + b + c;
  }

  @Override
  public String toString() {
    return description();
  }
}

package com.afvlad.geometry.utils;

import com.afavlad.geometry.core.Shape2D;

public class ShapeUtils {

  private ShapeUtils() {
  }

  public static int compareByArea(Shape2D s1, Shape2D s2) {
    return Double.compare(s1.area(), s2.area());
  }

  public static Shape2D maxByArea(Shape2D s1, Shape2D s2) {
    return compareByArea(s1, s2) >= 0 ? s1 : s2;
  }

  public static double squareMetersToSquareCentimeters(double sqm) {
    return sqm * 10_000;
  }

  public static int compareByPerimeter(Shape2D s1, Shape2D s2) {
    return Double.compare(s1.perimeter(), s2.perimeter());
  }

}

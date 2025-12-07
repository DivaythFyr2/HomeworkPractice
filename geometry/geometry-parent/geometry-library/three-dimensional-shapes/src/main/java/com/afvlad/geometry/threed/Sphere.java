package com.afvlad.geometry.threed;

public class Sphere implements Shape3D {

  private final double radius;

  public Sphere(double radius) {
    if(radius <= 0) {
      throw new IllegalArgumentException("Radius must be positive");
    }
    this.radius = radius;
  }

  public double getRadius() {
    return radius;
  }

  @Override
  public double volume() {
    return 4.0 / 3.0 * Math.PI * radius * radius * radius;
  }

  @Override
  public double surfaceArea() {
    return 4 * Math.PI * radius * radius;
  }

  @Override
  public String toString() {
    return "Sphere [radius=" + radius + "]";
  }
}

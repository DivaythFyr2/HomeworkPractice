package com.afvlad.geometry.threed;

public class Cube implements Shape3D {

  private final double edge;

  public Cube(double edge) {
    if(edge <= 0) {
      throw new IllegalArgumentException("Edge must be positive");
    }
    this.edge = edge;
  }

  public double getEdge() {
    return edge;
  }

  @Override
  public double volume() {
    return edge * edge * edge;
  }

  @Override
  public double surfaceArea() {
    return 6 * edge * edge;
  }

  @Override
  public String toString() {
    return "Cube [edge=" + edge + "]";
  }
}

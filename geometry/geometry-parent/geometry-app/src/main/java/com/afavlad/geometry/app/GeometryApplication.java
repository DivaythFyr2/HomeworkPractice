package com.afavlad.geometry.app;

import com.afavlad.geometry.core.Circle;
import com.afavlad.geometry.core.Rectangle;
import com.afavlad.geometry.core.Shape2D;
import com.afavlad.geometry.core.Triangle;
import com.afvlad.geometry.threed.Cube;
import com.afvlad.geometry.threed.Shape3D;
import com.afvlad.geometry.threed.Sphere;
import com.afvlad.geometry.utils.ShapeUtils;

public class GeometryApplication {

  public static void main(String[] args) {
    Shape2D circle = new Circle(5);
    Shape2D rectangle = new Rectangle(4, 6);
    Shape2D triangle = new Triangle(3, 4, 5);

    printShapeInfo(circle);
    printShapeInfo(rectangle);
    printShapeInfo(triangle);

    Shape2D bigger = ShapeUtils.maxByArea(circle, rectangle);
    System.out.println("Фигура с большей площадью (между circle и rectangle): " + bigger);
    double areaInSqm = bigger.area() / 10_000;
    System.out.println("Эта площадь в м²: " + areaInSqm);
    double inSqCm = ShapeUtils.squareMetersToSquareCentimeters(areaInSqm);
    System.out.println("И обратно в см² через утилиту: " + inSqCm);

    Shape2D biggerPerimeter = ShapeUtils.compareByPerimeter(circle, rectangle) >= 0
        ? circle : rectangle;
    System.out.println("Фигура с большим периметром (между circle и rectangle): " + biggerPerimeter);

    Shape3D cube = new Cube(2);
    Shape3D sphere = new Sphere(3);

    printShape3DInfo(cube);
    printShape3DInfo(sphere);
  }

  private static void printShapeInfo(Shape2D shape) {
    System.out.println("Фигура: " + shape);
    System.out.println("  Площадь:   " + shape.area());
    System.out.println("  Периметр:  " + shape.perimeter());
    System.out.println();
  }

  private static void printShape3DInfo(Shape3D shape) {
    System.out.println();
    System.out.println("3D фигура: " + shape);
    System.out.println("  Объём:              " + shape.volume());
    System.out.println("  Площадь поверхности: " + shape.surfaceArea());
    System.out.println();
  }
}

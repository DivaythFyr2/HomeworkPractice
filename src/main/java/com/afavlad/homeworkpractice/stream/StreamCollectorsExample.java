package com.afavlad.homeworkpractice.stream;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StreamCollectorsExample {

  public static void main(String[] args) {
    List<Order> orders = List.of(
        new Order("Laptop", 1200.0),
        new Order("Smartphone", 800.0),
        new Order("Laptop", 1500.0),
        new Order("Tablet", 500.0),
        new Order("Smartphone", 900.0)
    );

    Map<String, Double> totalCost = orders.stream()
        .collect(Collectors.groupingBy(
            Order::getProduct,
            Collectors.summingDouble(Order::getCost)));

    List<Map.Entry<String, Double>> mostExpensive =totalCost.entrySet().stream()
        .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
        .limit(3)
        .toList();

    mostExpensive.forEach(System.out::println);
  }
}

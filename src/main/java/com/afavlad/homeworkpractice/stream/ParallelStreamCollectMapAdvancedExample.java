package com.afavlad.homeworkpractice.stream;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ParallelStreamCollectMapAdvancedExample {

  public static void main(String[] args) {
    List<Student> students = Arrays.asList(
        new Student("Student1", Map.of("Math", 90, "Physics", 85)),
        new Student("Student2", Map.of("Math", 95, "Physics", 88)),
        new Student("Student3", Map.of("Math", 88, "Chemistry", 92)),
        new Student("Student4", Map.of("Physics", 78, "Chemistry", 85)));

    Map<String, Double> grades = students.parallelStream()
        .flatMap(student -> student.getGrades().entrySet().stream()).collect(
            Collectors.groupingBy(Map.Entry::getKey, Collectors.averagingInt(Map.Entry::getValue)));

    grades.forEach((key, value) -> System.out.println(key + ": AVG value --> " + value));
  }
}

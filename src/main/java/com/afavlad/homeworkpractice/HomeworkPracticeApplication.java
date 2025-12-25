package com.afavlad.homeworkpractice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class HomeworkPracticeApplication {

  public static void main(String[] args) {
    SpringApplication.run(HomeworkPracticeApplication.class, args);
  }

}

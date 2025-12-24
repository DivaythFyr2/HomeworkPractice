package com.afavlad.homeworkpractice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class HomeworkPracticeApplication {

  public static void main(String[] args) {
    SpringApplication.run(HomeworkPracticeApplication.class, args);
  }

}

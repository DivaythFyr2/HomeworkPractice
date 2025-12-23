package com.afavlad.homeworkpractice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

  @GetMapping("/home")
  public String home() {
    return "public home";
  }

  @GetMapping("/private")
  public String privatePage() {
    return "private page";
  }

}

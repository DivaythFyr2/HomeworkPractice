package com.afavlad.homeworkpractice.entity;


import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Book {
  private UUID id;
  private String title;
  private String author;
  private Integer publicationYear;

}

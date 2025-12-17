package com.afavlad.homeworkpractice.repository;

import com.afavlad.homeworkpractice.entity.Book;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookRepository {
  Book save(Book book);
  Optional<Book> findById(UUID id);
  List<Book> findAll();
  Book update(UUID id, Book book);
  void delete(UUID id);
  boolean existsById(UUID id);

}

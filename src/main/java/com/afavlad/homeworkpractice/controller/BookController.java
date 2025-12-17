package com.afavlad.homeworkpractice.controller;

import com.afavlad.homeworkpractice.dto.request.CreateBookRequest;
import com.afavlad.homeworkpractice.dto.request.UpdateBookRequest;
import com.afavlad.homeworkpractice.dto.response.BookResponse;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@RequestMapping("/api/v1/books")
public interface BookController {

  @GetMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  BookResponse getById(@PathVariable UUID id);

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  List<BookResponse> getAll();

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  BookResponse create(@Valid @RequestBody CreateBookRequest request);

  @PutMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  BookResponse update(@PathVariable UUID id, @Valid @RequestBody UpdateBookRequest request);

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  void delete(@PathVariable UUID id);
}
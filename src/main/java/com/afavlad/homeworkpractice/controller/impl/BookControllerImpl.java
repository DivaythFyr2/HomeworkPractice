package com.afavlad.homeworkpractice.controller.impl;

import com.afavlad.homeworkpractice.controller.BookController;
import com.afavlad.homeworkpractice.dto.request.CreateBookRequest;
import com.afavlad.homeworkpractice.dto.request.UpdateBookRequest;
import com.afavlad.homeworkpractice.dto.response.BookResponse;
import com.afavlad.homeworkpractice.service.impl.BookServiceImpl;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BookControllerImpl implements BookController {

  private final BookServiceImpl bookService;

  @Override
  public BookResponse getById(@PathVariable UUID id) {
    return bookService.getById(id);
  }

  @Override
  public List<BookResponse> getAll() {
    return bookService.getAll();
  }

  @Override
  public BookResponse create(@Valid @RequestBody CreateBookRequest request) {
    return bookService.create(request);
  }

  @Override
  public BookResponse update(@PathVariable UUID id, @Valid @RequestBody UpdateBookRequest request) {
    return bookService.update(id, request);
  }

  @Override
  public void delete(@PathVariable UUID id) {
    bookService.delete(id);
  }
}

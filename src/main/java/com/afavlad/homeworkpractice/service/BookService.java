package com.afavlad.homeworkpractice.service;

import com.afavlad.homeworkpractice.dto.request.CreateBookRequest;
import com.afavlad.homeworkpractice.dto.request.UpdateBookRequest;
import com.afavlad.homeworkpractice.dto.response.BookResponse;
import java.util.List;
import java.util.UUID;

public interface BookService {
  BookResponse create(CreateBookRequest request);
  BookResponse getById(UUID id);
  List<BookResponse> getAll();
  BookResponse update(UUID id, UpdateBookRequest request);
  void delete(UUID id);
}

package com.afavlad.homeworkpractice.service.impl;

import com.afavlad.homeworkpractice.dto.request.CreateBookRequest;
import com.afavlad.homeworkpractice.dto.request.UpdateBookRequest;
import com.afavlad.homeworkpractice.dto.response.BookResponse;
import com.afavlad.homeworkpractice.entity.Book;
import com.afavlad.homeworkpractice.exception.NotFoundException;
import com.afavlad.homeworkpractice.mapper.BookMapper;
import com.afavlad.homeworkpractice.repository.BookRepository;
import com.afavlad.homeworkpractice.service.BookService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

  private final BookRepository bookRepository;
  private final BookMapper bookMapper;

  @Override
  @Transactional
  public BookResponse create(CreateBookRequest request) {
    Book saved = bookRepository.save(bookMapper.toEntityCreate(request));
    return bookMapper.toResponse(saved);
  }

  @Override
  public BookResponse getById(UUID id) {
    Book book = bookRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Book with id " + id + " not found!"));
    return bookMapper.toResponse(book);
  }

  @Override
  public List<BookResponse> getAll() {
    return bookRepository.findAll().stream()
        .map(bookMapper::toResponse)
        .toList();
  }

  @Override
  @Transactional
  public BookResponse update(UUID id, UpdateBookRequest request) {
    if (!bookRepository.existsById(id)) {
      throw new NotFoundException("Book with id " + id + " not found!");
    }
    Book updated = bookRepository.update(id, bookMapper.toEntityUpdate(request));
    return bookMapper.toResponse(updated);
  }

  @Override
  @Transactional
  public void delete(UUID id) {
    if (!bookRepository.existsById(id)) {
      throw new NotFoundException("Book with id " + id + " not found!");
    }
    bookRepository.delete(id);
  }
}

package com.afavlad.homeworkpractice.mapper;

import com.afavlad.homeworkpractice.dto.request.CreateBookRequest;
import com.afavlad.homeworkpractice.dto.request.UpdateBookRequest;
import com.afavlad.homeworkpractice.dto.response.BookResponse;
import com.afavlad.homeworkpractice.entity.Book;
import org.springframework.stereotype.Component;

@Component
public class BookMapper {

  public Book toEntityCreate(CreateBookRequest dto) {
    return new Book(null, dto.title(), dto.author(), dto.publicationYear());
  }

  public Book toEntityUpdate(UpdateBookRequest dto) {
    return new Book(null, dto.title(), dto.author(), dto.publicationYear());
  }

  public BookResponse toResponse(Book book) {
    return new BookResponse(book.getId(), book.getTitle(), book.getAuthor(), book.getPublicationYear());
  }
}

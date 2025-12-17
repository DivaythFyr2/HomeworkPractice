package com.afavlad.homeworkpractice.repository.impl;

import com.afavlad.homeworkpractice.entity.Book;
import com.afavlad.homeworkpractice.repository.BookRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class BookRepositoryImpl implements BookRepository {

  private static final String INSERT_BOOK =
      "INSERT INTO BOOKS(id, title, author, publication_year) VALUES (?, ?, ?, ?) RETURNING ID";

  private static final String SELECT_BY_ID =
      "SELECT id, title, author, publication_year FROM BOOKS WHERE id = ?";

  private static final String SELECT_ALL =
      "SELECT id, title, author, publication_year FROM BOOKS ORDER BY title, author";

  private static final String UPDATE_BY_ID =
      "UPDATE BOOKS SET TITLE = ?, author = ?, publication_year = ? WHERE id = ?";

  private static final String DELETE_BY_ID =
      "DELETE FROM BOOKS WHERE id = ?";

  private static final String EXISTS_BY_ID =
      "SELECT EXISTS (SELECT 1 FROM BOOKS WHERE id = ?)";

  private final JdbcTemplate jdbcTemplate;

  private static final RowMapper<Book> BOOK_ROW_MAPPER = (rs, rowNum) -> new Book(
      rs.getObject("id", UUID.class),
      rs.getString("title"),
      rs.getString("author"),
      rs.getInt("publication_year")
  );

  @Override
  public Book save(Book book) {
    UUID id = (book.getId() != null) ? book.getId() : UUID.randomUUID();

    UUID generatedId = jdbcTemplate.queryForObject(
        INSERT_BOOK,
        UUID.class,
        id,
        book.getTitle(),
        book.getAuthor(),
        book.getPublicationYear()
    );

    book.setId(generatedId);
    return book;
  }

  @Override
  public Optional<Book> findById(UUID id) {
    List<Book> result = jdbcTemplate.query(SELECT_BY_ID, BOOK_ROW_MAPPER, id);
    return result.stream().findFirst();
  }

  @Override
  public List<Book> findAll() {
    return jdbcTemplate.query(SELECT_ALL, BOOK_ROW_MAPPER);
  }

  @Override
  public Book update(UUID id, Book book) {
    jdbcTemplate.update(
        UPDATE_BY_ID,
        book.getTitle(),
        book.getAuthor(),
        book.getPublicationYear(),
        id
    );
    return new Book(id, book.getTitle(), book.getAuthor(), book.getPublicationYear());
  }

  @Override
  public void delete(UUID id) {
    jdbcTemplate.update(DELETE_BY_ID, id);
  }

  @Override
  public boolean existsById(UUID id) {
    Boolean exists = jdbcTemplate.queryForObject(EXISTS_BY_ID, Boolean.class, id);
    return exists != null && exists;
  }
}

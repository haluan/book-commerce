package com.bookcommerce.catalogservice.persistence;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;

import com.bookcommerce.catalogservice.domain.Book;
import com.bookcommerce.catalogservice.domain.BookRepository;

@Repository
public class InMemoryBookRepository implements BookRepository {
  private static final Map<String, Book> books = new ConcurrentHashMap<>();

  @Override
  public Iterable<Book> findAll() {
    return books.values();
  }

  @Override
  public Optional<Book> findByIsbn(String isbn) {
    return existsByIsbn(isbn) ? Optional.of(books.get(isbn)) : Optional.empty();
  }

  @Override
  public boolean existsByIsbn(String isbn) {
    return books.get(isbn) != null;
  }

  @Override
  public Book save(Book book) {
    books.put(book.isbn(), book);
    return book;
  }

  @Override
  public String deleteByIsbn(String isbn) {
    books.remove(isbn);
    return isbn;
  }
}

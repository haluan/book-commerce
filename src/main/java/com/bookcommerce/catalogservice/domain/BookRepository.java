package com.bookcommerce.catalogservice.domain;

import java.util.Optional;

public interface BookRepository {
  Iterable<Book> findAll();

  Optional<Book> findByIsbn(String isbn);

  boolean existsByIsbn(String isbn);

  Book save(Book book);

  String deleteByIsbn(String isbn);
}

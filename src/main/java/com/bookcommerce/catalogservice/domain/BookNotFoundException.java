package com.bookcommerce.catalogservice.domain;

public class BookNotFoundException extends RuntimeException {
  public BookNotFoundException(String isbn) {
    super("Book with ISBN " + isbn + " not found in the catalog.");
  }
}

package com.bookcommerce.catalogservice.domain;

import org.springframework.stereotype.Service;

@Service
public class BookService {
  private final BookRepository bookRepository;

  public BookService(BookRepository bookRepository) {
    this.bookRepository = bookRepository;
  }

  public Iterable<Book> findAll() {
    return bookRepository.findAll();
  }

  public Book findByIsbn(String isbn) {
    return bookRepository.findByIsbn(isbn).orElseThrow(() -> new BookNotFoundException(isbn));
  }

  public Book addBookToCatalog(Book book) {
    if (bookRepository.existsByIsbn(book.isbn())) {
      throw new BookAlreadyExistsException(book.isbn());
    }
    return bookRepository.save(book);
  }

  public String removeBookFromCatalog(String isbn) {
    return bookRepository.deleteByIsbn(isbn);
  }

  public Book editBookDetails(String isbn, Book book) {
    return bookRepository
        .findByIsbn(isbn)
        .map(
            existingBook -> {
              var bookToUpdate =
                  new Book(existingBook.isbn(), book.title(), book.author(), book.price());
              return bookRepository.save(bookToUpdate);
            })
        .orElseGet(() -> addBookToCatalog(book));
  }
}

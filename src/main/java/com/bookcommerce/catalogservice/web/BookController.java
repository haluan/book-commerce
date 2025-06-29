package com.bookcommerce.catalogservice.web;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.bookcommerce.catalogservice.domain.Book;
import com.bookcommerce.catalogservice.domain.BookService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("books")
public class BookController {
  private final BookService bookService;

  public BookController(BookService bookService) {
    this.bookService = bookService;
  }

  @GetMapping
  public Iterable<Book> getAllBooks() {
    return bookService.findAll();
  }

  @GetMapping("{isbn}")
  public Book getBookByIsbn(@PathVariable String isbn) {
    return bookService.findByIsbn(isbn);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Book addBookToCatalog(@Valid @RequestBody Book book) {
    return bookService.addBookToCatalog(book);
  }

  @DeleteMapping("{isbn}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public String removeBookFromCatalog(@PathVariable String isbn) {
    return bookService.removeBookFromCatalog(isbn);
  }

  @PutMapping("{isbn}")
  public Book updateBook(@PathVariable String isbn, @Valid @RequestBody Book book) {
    return bookService.editBookDetails(isbn, book);
  }
}

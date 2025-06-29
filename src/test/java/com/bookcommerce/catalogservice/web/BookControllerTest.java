package com.bookcommerce.catalogservice.web;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.bookcommerce.catalogservice.domain.BookNotFoundException;
import com.bookcommerce.catalogservice.domain.BookService;

@WebMvcTest(BookController.class)
class BookControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private BookService bookService;

  @Test
  void whenGetBookNotExistingThenShouldReturnNotFound() throws Exception {
    String isbn = "1234567890";

    given(bookService.findByIsbn(isbn)).willThrow(new BookNotFoundException(isbn));

    mockMvc.perform(get("/books/{isbn}", isbn)).andExpect(status().isNotFound());
  }
}

package com.bookcommerce.catalogservice;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.bookcommerce.catalogservice.domain.Book;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CatalogServiceApplicationTests {

  private final Book initialBook = new Book("1234567890", "Title Book", "Author Franklin", 9.99);

  @Autowired private WebTestClient webTestClient;

  @AfterEach
  void tearDown() {
    webTestClient
        .delete()
        .uri("/books/{isbn}", initialBook.isbn())
        .exchange()
        .expectStatus()
        .isNoContent();
  }

  @Test
  void whenPostRequestThenBookCreated() {
    webTestClient
        .post()
        .uri("/books")
        .bodyValue(initialBook)
        .exchange()
        .expectStatus()
        .isCreated()
        .expectBody(Book.class)
        .value(
            actualBook -> {
              assertThat(actualBook.isbn()).isEqualTo(initialBook.isbn());
              assertThat(actualBook.title()).isEqualTo(initialBook.title());
              assertThat(actualBook.author()).isEqualTo(initialBook.author());
              assertThat(actualBook.price()).isEqualTo(initialBook.price());
            });
  }

  @ParameterizedTest
  @CsvSource({
    "'12345678901234', 'Title Book', 'Author Franklin', 9.99, '$.isbn'",
    "'12345678901', 'Title Book', 'Author Franklin', 9.99, '$.isbn'",
    "'', 'Title Book', 'Author Franklin', 9.99, '$.isbn'",
    ", 'Title Book', 'Author Franklin', 9.99, '$.isbn'",
    "'1234567890', '', 'Author Franklin', 9.99, '$.title'",
    "'1234567890', 'Title Book', '', 9.99, '$.author'",
    "'1234567890', 'Title Book', 'Author Franklin', , '$.price'",
    "'1234567890', 'Title Book', 'Author Franklin', -1.00, '$.price'",
  })
  void whenPostRequestWithInvalidDataThenBadRequest(
      String isbn, String title, String author, Double price, String jsonPath) {
    var book = new Book(isbn, title, author, price);
    webTestClient
        .post()
        .uri("/books")
        .bodyValue(book)
        .exchange()
        .expectStatus()
        .isBadRequest()
        .expectBody()
        .jsonPath(jsonPath)
        .isNotEmpty();
  }

  @Test
  void whenGetRequestThenBookFound() {
    webTestClient.post().uri("/books").bodyValue(initialBook).exchange().expectStatus().isCreated();

    webTestClient
        .get()
        .uri("/books/{isbn}", initialBook.isbn())
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(Book.class)
        .value(
            actualBook -> {
              assertThat(actualBook.isbn()).isEqualTo(initialBook.isbn());
              assertThat(actualBook.title()).isEqualTo(initialBook.title());
              assertThat(actualBook.author()).isEqualTo(initialBook.author());
              assertThat(actualBook.price()).isEqualTo(initialBook.price());
            });
  }

  @Test
  void whenUpdateRequestThenBookUpdated() {
    webTestClient.post().uri("/books").bodyValue(initialBook).exchange().expectStatus().isCreated();

    var updatedBook = new Book(initialBook.isbn(), "Updated Title", "Updated Author", 29.99);
    webTestClient
        .put()
        .uri("/books/{isbn}", initialBook.isbn())
        .bodyValue(updatedBook)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(Book.class)
        .value(
            actualBook -> {
              assertThat(actualBook.isbn()).isEqualTo(updatedBook.isbn());
              assertThat(actualBook.title()).isEqualTo(updatedBook.title());
              assertThat(actualBook.author()).isEqualTo(updatedBook.author());
              assertThat(actualBook.price()).isEqualTo(updatedBook.price());
            });
  }

  @ParameterizedTest
  @CsvSource({
    "'', 'Updated Author', 29.99, '$.title'",
    ", 'Updated Author', 29.99, '$.title'",
    "'Updated Title', '', 29.99, '$.author'",
    "'Updated Title', , 29.99, '$.author'",
    "'Updated Title', 'Updated Author', , '$.price'",
    "'Updated Title', 'Updated Author', -1.00, '$.price'",
  })
  void whenUpdateRequestWithInvalidDataThenBadRequest(
      String title, String author, Double price, String jsonPath) {
    var book = new Book(initialBook.isbn(), title, author, price);
    webTestClient
        .put()
        .uri("/books/{isbn}", initialBook.isbn())
        .bodyValue(book)
        .exchange()
        .expectStatus()
        .isBadRequest()
        .expectBody()
        .jsonPath(jsonPath)
        .isNotEmpty();
  }

  @Test
  void whenDeleteRequestThenBookDeleted() {
    webTestClient.post().uri("/books").bodyValue(initialBook).exchange().expectStatus().isCreated();

    webTestClient
        .delete()
        .uri("/books/{isbn}", initialBook.isbn())
        .exchange()
        .expectStatus()
        .isNoContent();
  }

  @Test
  void whenGetAllBooksThenBooksFound() {
    var book1 = new Book("1234567894", "Title Book 1", "Author Franklin", 9.99);
    var book2 = new Book("1234567895", "Title Book 2", "Author Franklin", 19.99);

    webTestClient.post().uri("/books").bodyValue(book1).exchange().expectStatus().isCreated();

    webTestClient.post().uri("/books").bodyValue(book2).exchange().expectStatus().isCreated();

    webTestClient
        .get()
        .uri("/books")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBodyList(Book.class)
        .hasSize(2)
        .value(
            books -> {
              assertThat(books)
                  .extracting(Book::isbn)
                  .containsExactlyInAnyOrder(book1.isbn(), book2.isbn());
              assertThat(books)
                  .extracting(Book::title)
                  .containsExactlyInAnyOrder(book1.title(), book2.title());
              assertThat(books)
                  .extracting(Book::author)
                  .containsExactlyInAnyOrder(book1.author(), book2.author());
              assertThat(books)
                  .extracting(Book::price)
                  .containsExactlyInAnyOrder(book1.price(), book2.price());
            });
  }
}

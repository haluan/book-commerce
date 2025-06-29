package com.bookcommerce.catalogservice.domain;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import java.util.Set;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class BookTest {
  private static Validator validator;
  private static ValidatorFactory validatorFactory;

  @BeforeAll
  static void setUp() {
    validatorFactory = Validation.buildDefaultValidatorFactory();
    validator = validatorFactory.getValidator();
  }

  @AfterAll
  static void tearDown() {
    validatorFactory.close();
  }

  @Test
  void whenAllFieldsCorrectThenValidationSucceeds() {
    var book = new Book("1234567890", "Title Book", "Author Franklin", 9.99);
    Set<ConstraintViolation<Book>> constraintViolations = validator.validate(book);

    assertThat(constraintViolations).isEmpty();
  }

  @ParameterizedTest
  @CsvSource({
    "12345678901234, 1, 'The book ISBN must be a valid 10 or 13 digit number.', ''",
    "12345678901, 1, 'The book ISBN must be a valid 10 or 13 digit number.', ''",
    "'',2 , 'The book ISBN must be provided.', 'The book ISBN must be a valid 10 or 13 digit number.'",
    ",1 , 'The book ISBN must be provided.', ''",
  })
  void whenIsbnDefinedButIncorrectThenValidationFails(
      String badIsbn, Integer expectedSize, String expectedMessage1, String expectedMessage2) {
    var book = new Book(badIsbn, "Title Book", "Author Franklin", 9.99);
    Set<ConstraintViolation<Book>> constraintViolations = validator.validate(book);

    assertThat(constraintViolations).hasSize(expectedSize);
    if (expectedSize > 1) {
      assertThat(constraintViolations)
          .extracting(ConstraintViolation::getMessage)
          .containsExactlyInAnyOrder(expectedMessage1, expectedMessage2);
    } else {
      assertThat(constraintViolations.iterator().next().getMessage()).isEqualTo(expectedMessage1);
    }
  }

  @ParameterizedTest
  @CsvSource({
    "'', 'The book title must be provided.'",
    ", 'The book title must be provided.'",
  })
  void whenTitleNotDefinedThenValidationFails(String badTitle, String expectedMessage) {
    var book = new Book("1234567890", badTitle, "Author Franklin", 9.99);
    Set<ConstraintViolation<Book>> constraintViolations = validator.validate(book);

    assertThat(constraintViolations).hasSize(1);
    assertThat(constraintViolations.iterator().next().getMessage()).isEqualTo(expectedMessage);
  }

  @ParameterizedTest
  @CsvSource({
    "'', 'The book author must be provided.'",
    ", 'The book author must be provided.'",
  })
  void whenAuthorNotDefinedThenValidationFails(String badAuthor, String expectedMessage) {
    var book = new Book("1234567890", "Title Book", badAuthor, 9.99);
    Set<ConstraintViolation<Book>> constraintViolations = validator.validate(book);

    assertThat(constraintViolations).hasSize(1);
    assertThat(constraintViolations.iterator().next().getMessage()).isEqualTo(expectedMessage);
  }

  @ParameterizedTest
  @CsvSource({
    ", 'The book price must be provided.'",
    "-9.99, 'The book price must be a positive number.'",
  })
  void whenPriceNotDefinedThenValidationFails(Double badPrice, String expectedMessage) {
    var book = new Book("1234567890", "Title Book", "Author Franklin", badPrice);
    Set<ConstraintViolation<Book>> constraintViolations = validator.validate(book);

    assertThat(constraintViolations).hasSize(1);
    assertThat(constraintViolations.iterator().next().getMessage()).isEqualTo(expectedMessage);
  }
}

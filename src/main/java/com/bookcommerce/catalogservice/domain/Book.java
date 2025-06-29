package com.bookcommerce.catalogservice.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

public record Book(
    @NotBlank(message = "The book ISBN must be provided.")
        @Pattern(
            regexp = "^([0-9]{10}|[0-9]{13})$",
            message = "The book ISBN must be a valid 10 or 13 digit number.")
        String isbn,
    @NotBlank(message = "The book title must be provided.") String title,
    @NotBlank(message = "The book author must be provided.") String author,
    @NotNull(message = "The book price must be provided.")
        @Positive(message = "The book price must be a positive number.")
        Double price) {}

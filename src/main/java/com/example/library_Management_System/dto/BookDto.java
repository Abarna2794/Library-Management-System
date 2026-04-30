package com.example.library_Management_System.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;


public class BookDto {



        @NotBlank(message = "Title is required")
        private String title;

        @NotBlank(message = "Author is required")
        private String author;

        @NotBlank(message = "ISBN is required")
        private String isbn;

        @Min(value = 1, message = "At least 1 copy required")
        private int availableCopies;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public int getAvailableCopies() {
        return availableCopies;
    }

    public void setAvailableCopies(int availableCopies) {
        this.availableCopies = availableCopies;
    }
}

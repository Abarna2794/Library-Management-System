package com.example.library_Management_System.controller;

import com.example.library_Management_System.dto.BookDto;
import com.example.library_Management_System.service.BookService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController

public class BookController {

    @Autowired
    BookService bookService;


    @PostMapping("/admin/books")
    public BookDto addBook(@Valid@RequestBody BookDto bookDto) {
        return bookService.addBook(bookDto);
    }
    @GetMapping("/books")
    public Page<BookDto> getBooks(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size) {
        return bookService.getBooks(page, size);
    }
    @GetMapping("/books/search")
    public List<BookDto> searchBooks(@RequestParam String title) {
        return bookService.searchByTitle(title);
    }
    @GetMapping("/books/searchByAuthor")
    public List<BookDto> searchAuthor(@RequestParam String author) {
        return bookService.searchByAuthor(author);
    }
    @GetMapping("/books/available")
    public List<BookDto> getAvailableBooks() {
        return bookService.getAvailableBooks();
    }

    @DeleteMapping("/admin/books/{id}")
    public String deleteBook(@PathVariable long id){
    bookService.deleteBook(id);
        return "Deleted successfully";
    }
    @GetMapping("/user/books/{id}")
    public BookDto getBookByid(@PathVariable long id){
        BookDto bookDto=bookService.getById(id);
        return bookDto;
    }
    @GetMapping("/user/books")
    public List<BookDto> getAllBook(){

       return bookService.getAllBooks();
    }
    @PutMapping("/admin/books/{id}")
    public BookDto updateBook(@Valid @PathVariable Long id, @RequestBody BookDto bookDto){
        return bookService.updateBook(id,bookDto);
    }
}
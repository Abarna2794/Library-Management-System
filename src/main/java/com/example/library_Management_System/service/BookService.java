package com.example.library_Management_System.service;

import com.example.library_Management_System.dto.BookDto;
import com.example.library_Management_System.entity.Book;
import com.example.library_Management_System.exceptionHandler.DuplicateIsbnException;
import com.example.library_Management_System.repo.BookRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class BookService {

    @Autowired
    private BookRepo bookRepo;


    public BookDto addBook(BookDto bookDto) {

        // Validation
        if (bookDto.getTitle() == null || bookDto.getTitle().isEmpty()) {
            throw new RuntimeException("Title cannot be empty");
        }

        if (bookDto.getAuthor() == null || bookDto.getAuthor().isEmpty()) {
            throw new RuntimeException("Author cannot be empty");
        }

        if (bookDto.getAvailableCopies() < 0) {
            throw new RuntimeException("Available copies cannot be negative");
        }
        if (bookRepo.existsByIsbn(bookDto.getIsbn())) {
            throw new DuplicateIsbnException("ISBN already exists");
        }

        Book book = new Book();
        book.setTitle(bookDto.getTitle());
        book.setAuthor(bookDto.getAuthor());
        book.setIsbn(bookDto.getIsbn());
        book.setAvailableCopies(bookDto.getAvailableCopies());

        Book savedBook = bookRepo.save(book);

        BookDto responseDto = new BookDto();
        responseDto.setId(savedBook.getId());
        responseDto.setTitle(savedBook.getTitle());
        responseDto.setAuthor(savedBook.getAuthor());
        responseDto.setIsbn(savedBook.getIsbn());
        responseDto.setAvailableCopies(savedBook.getAvailableCopies());

        return responseDto;
    }
    public Page<BookDto> getBooks(int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<Book> bookPage = bookRepo.findAll(pageable);

        return bookPage.map(book -> {
            BookDto dto = new BookDto();
            dto.setId(book.getId());
            dto.setTitle(book.getTitle());
            dto.setAuthor(book.getAuthor());
            dto.setIsbn(book.getIsbn());
            dto.setAvailableCopies(book.getAvailableCopies());
            return dto;
        });
    }
    public List<BookDto> searchByTitle(String title) {

        List<Book> books = bookRepo.findByTitleContainingIgnoreCase(title);

        return books.stream().map(book -> {
            BookDto dto = new BookDto();
            dto.setId(book.getId());
            dto.setTitle(book.getTitle());
            dto.setAuthor(book.getAuthor());
            dto.setIsbn(book.getIsbn());
            dto.setAvailableCopies(book.getAvailableCopies());
            return dto;
        }).toList();
    }
    public List<BookDto> searchByAuthor(String author) {

        List<Book> books = bookRepo.findByAuthorContainingIgnoreCase(author);

        return books.stream().map(book -> {
            BookDto dto = new BookDto();
            dto.setId(book.getId());
            dto.setTitle(book.getTitle());
            dto.setAuthor(book.getAuthor());
            dto.setIsbn(book.getIsbn());
            dto.setAvailableCopies(book.getAvailableCopies());
            return dto;
        }).toList();
    }
    public List<BookDto> getAvailableBooks() {

        List<Book> books = bookRepo.findByAvailableCopiesGreaterThan(0);

        return books.stream().map(book -> {
            BookDto dto = new BookDto();
            dto.setId(book.getId());
            dto.setTitle(book.getTitle());
            dto.setAuthor(book.getAuthor());
            dto.setIsbn(book.getIsbn());
            dto.setAvailableCopies(book.getAvailableCopies());
            return dto;
        }).toList();
    }
    public BookDto getById(Long id) {

        Book book = bookRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + id));

        BookDto dto = new BookDto();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        dto.setAuthor(book.getAuthor());
        dto.setIsbn(book.getIsbn());
        dto.setAvailableCopies(book.getAvailableCopies());

        return dto;
    }
    public BookDto updateBook(Long id, BookDto newBookDto) {


        Book book = bookRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + id));


        if (newBookDto.getTitle() == null || newBookDto.getTitle().isEmpty()) {
            throw new RuntimeException("Title cannot be empty");
        }

        if (newBookDto.getAuthor() == null || newBookDto.getAuthor().isEmpty()) {
            throw new RuntimeException("Author cannot be empty");
        }

        if (newBookDto.getAvailableCopies() < 0) {
            throw new IllegalArgumentException("Available copies cannot be negative");
        }


        book.setTitle(newBookDto.getTitle());
        book.setAuthor(newBookDto.getAuthor());
        book.setIsbn(newBookDto.getIsbn());
        book.setAvailableCopies(newBookDto.getAvailableCopies());


        Book updatedBook = bookRepo.save(book);


        BookDto responseDto = new BookDto();
        responseDto.setId(updatedBook.getId());
        responseDto.setTitle(updatedBook.getTitle());
        responseDto.setAuthor(updatedBook.getAuthor());
        responseDto.setIsbn(updatedBook.getIsbn());
        responseDto.setAvailableCopies(updatedBook.getAvailableCopies());

        return responseDto;
    }
    public List<BookDto> getAllBooks() {

        List<Book> books = bookRepo.findAll();
        List<BookDto> bookDtos = new ArrayList<>();

        for (Book book : books) {

            BookDto dto = new BookDto();
            dto.setId(book.getId());
            dto.setTitle(book.getTitle());
            dto.setAuthor(book.getAuthor());
            dto.setIsbn(book.getIsbn());
            dto.setAvailableCopies(book.getAvailableCopies());

            bookDtos.add(dto);
        }

        return bookDtos;
    }
    public void deleteBook(Long id){
        Book book=bookRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + id));
    bookRepo.delete(book);
    }
    public void updateBookEntity(Book book) {
        bookRepo.save(book);
    }
    public Book getBookEntityById(Long id) {
        return bookRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + id));
    }

}




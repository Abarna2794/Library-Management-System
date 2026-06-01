package com.example.library_Management_System.service;

import com.example.library_Management_System.dto.BookDto;
import com.example.library_Management_System.entity.Book;
import com.example.library_Management_System.exceptionHandler.BookNotFoundException;
import com.example.library_Management_System.exceptionHandler.DuplicateIsbnException;
import com.example.library_Management_System.repo.BookRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepo bookRepo;

    @InjectMocks
    private BookService bookService;

    private Book getSampleBook() {
        return new Book(1L, "Clean Code", "Robert Martin", "9780132350884", 5);
    }

    private BookDto getSampleBookDto() {
        BookDto dto = new BookDto();
        dto.setTitle("Clean Code");
        dto.setAuthor("Robert Martin");
        dto.setIsbn("9780132350884");
        dto.setAvailableCopies(5);
        return dto;
    }

    @Test
    void addBook_ShouldSave_WhenValid() {
        BookDto dto = getSampleBookDto();
        Book saved = getSampleBook();
        when(bookRepo.existsByIsbn(dto.getIsbn())).thenReturn(false);
        when(bookRepo.save(any(Book.class))).thenReturn(saved);

        BookDto result = bookService.addBook(dto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Clean Code", result.getTitle());
        verify(bookRepo, times(1)).existsByIsbn(dto.getIsbn());
        verify(bookRepo, times(1)).save(any(Book.class));
    }

    @Test
    void addBook_ShouldThrowException_WhenTitleEmpty() {
        BookDto dto = getSampleBookDto();
        dto.setTitle("");

        assertThrows(RuntimeException.class, () -> bookService.addBook(dto));
        verify(bookRepo, never()).save(any());
    }

    @Test
    void addBook_ShouldThrowException_WhenISBNExists() {
        BookDto dto = getSampleBookDto();
        when(bookRepo.existsByIsbn(dto.getIsbn())).thenReturn(true);

        assertThrows(DuplicateIsbnException.class, () -> bookService.addBook(dto));
        verify(bookRepo, never()).save(any());
    }

    @Test
    void addBook_ShouldThrowException_WhenNegativeCopies() {
        BookDto dto = getSampleBookDto();
        dto.setAvailableCopies(-1);

        assertThrows(RuntimeException.class, () -> bookService.addBook(dto));
    }

    @Test
    void getBooks_ShouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 2);
        List<Book> books = Arrays.asList(getSampleBook());
        Page<Book> page = new PageImpl<>(books, pageable, 1);
        when(bookRepo.findAll(pageable)).thenReturn(page);

        Page<BookDto> result = bookService.getBooks(0, 2);

        assertEquals(1, result.getTotalElements());
        assertEquals("Clean Code", result.getContent().get(0).getTitle());
        verify(bookRepo).findAll(pageable);
    }

    @Test
    void getAllBooks_ShouldReturnList() {
        when(bookRepo.findAll()).thenReturn(Arrays.asList(getSampleBook()));

        List<BookDto> result = bookService.getAllBooks();

        assertEquals(1, result.size());
        assertEquals("Clean Code", result.get(0).getTitle());
        verify(bookRepo, times(1)).findAll();
    }

    @Test
    void getById_ShouldReturnBookDto_WhenExists() {
        when(bookRepo.findById(1L)).thenReturn(Optional.of(getSampleBook()));

        BookDto result = bookService.getById(1L);

        assertEquals("Clean Code", result.getTitle());
        verify(bookRepo).findById(1L);
    }

    @Test
    void getById_ShouldThrowException_WhenNotFound() {
        when(bookRepo.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> bookService.getById(99L));
        verify(bookRepo).findById(99L);
    }

    @Test
    void updateBook_ShouldUpdate_WhenValid() {
        Book existing = getSampleBook();
        BookDto newDto = getSampleBookDto();
        newDto.setTitle("Updated Title");

        when(bookRepo.findById(1L)).thenReturn(Optional.of(existing));
        when(bookRepo.save(any(Book.class))).thenReturn(existing);

        BookDto result = bookService.updateBook(1L, newDto);

        assertEquals("Updated Title", result.getTitle());
        verify(bookRepo).findById(1L);
        verify(bookRepo).save(existing);
    }

    @Test
    void updateBook_ShouldThrowException_WhenNotFound() {
        when(bookRepo.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> bookService.updateBook(99L, getSampleBookDto()));
        verify(bookRepo, never()).save(any());
    }

    @Test
    void updateBook_ShouldThrowException_WhenNegativeCopies() {
        BookDto dto = getSampleBookDto();
        dto.setAvailableCopies(-5);
        when(bookRepo.findById(1L)).thenReturn(Optional.of(getSampleBook()));

        assertThrows(IllegalArgumentException.class, () -> bookService.updateBook(1L, dto));
    }

    @Test
    void deleteBook_ShouldDelete_WhenExists() {
        when(bookRepo.findById(1L)).thenReturn(Optional.of(getSampleBook()));

        bookService.deleteBook(1L);

        verify(bookRepo).findById(1L);
        verify(bookRepo).delete(any(Book.class));
    }

    @Test
    void deleteBook_ShouldThrowException_WhenNotFound() {
        when(bookRepo.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> bookService.deleteBook(99L));
        verify(bookRepo, never()).delete(any());
    }

    @Test
    void searchByTitle_ShouldReturnList() {
        when(bookRepo.findByTitleContainingIgnoreCase("clean"))
                .thenReturn(Arrays.asList(getSampleBook()));

        List<BookDto> result = bookService.searchByTitle("clean");

        assertEquals(1, result.size());
        verify(bookRepo).findByTitleContainingIgnoreCase("clean");
    }

    @Test
    void searchByAuthor_ShouldReturnList() {
        when(bookRepo.findByAuthorContainingIgnoreCase("martin"))
                .thenReturn(Arrays.asList(getSampleBook()));

        List<BookDto> result = bookService.searchByAuthor("martin");

        assertEquals(1, result.size());
        verify(bookRepo).findByAuthorContainingIgnoreCase("martin");
    }

    @Test
    void getAvailableBooks_ShouldReturnList() {
        when(bookRepo.findByAvailableCopiesGreaterThan(0))
                .thenReturn(Arrays.asList(getSampleBook()));

        List<BookDto> result = bookService.getAvailableBooks();

        assertEquals(1, result.size());
        assertTrue(result.get(0).getAvailableCopies() > 0);
        verify(bookRepo).findByAvailableCopiesGreaterThan(0);
    }

    @Test
    void getBookEntityById_ShouldReturnEntity_WhenExists() {
        when(bookRepo.findById(1L)).thenReturn(Optional.of(getSampleBook()));

        Book result = bookService.getBookEntityById(1L);

        assertEquals("Clean Code", result.getTitle());
    }

    @Test
    void getBookEntityById_ShouldThrowBookNotFoundException_WhenNotExists() {
        when(bookRepo.findById(99L)).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class, () -> bookService.getBookEntityById(99L));
    }
}
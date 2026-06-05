package com.example.library_Management_System.service;



import com.example.library_Management_System.dto.BorrowDto;
import com.example.library_Management_System.entity.Book;
import com.example.library_Management_System.entity.BorrowRecord;
import com.example.library_Management_System.entity.User;
import com.example.library_Management_System.exceptionHandler.BookNotAvailableException;
import com.example.library_Management_System.repo.BorrowRecordRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BorrowServiceTest {

    @Mock UserService userService;
    @Mock BookService bookService;
    @Mock BorrowRecordRepo borrowRecordRepo;

    @InjectMocks BorrowService borrowService;

    private User user;
    private Book book;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("john");

        book = new Book();
        book.setId(1L);
        book.setTitle("Dune");
        book.setAvailableCopies(3);
    }

    @Test
    void borrowBook_success_returnsDtoAndDecrementsStock() {
        when(userService.getUserEntityById(1L)).thenReturn(user);
        when(bookService.getBookEntityById(1L)).thenReturn(book);
        when(borrowRecordRepo.countByUserAndStatus(user, "BORROWED")).thenReturn(1L);
        when(borrowRecordRepo.save(any(BorrowRecord.class))).thenAnswer(inv -> inv.getArgument(0));

        BorrowDto result = borrowService.borrowBook(1L, 1L);

        assertThat(result.getUserId()).isEqualTo(1L);
        assertThat(result.getBookId()).isEqualTo(1L);
        assertThat(result.getStatus()).isEqualTo("BORROWED");
        assertThat(result.getFine()).isZero();
        assertThat(book.getAvailableCopies()).isEqualTo(2); // 3-1

        verify(bookService).updateBookEntity(book);
        verify(borrowRecordRepo).save(any(BorrowRecord.class));
    }

    @Test
    void borrowBook_noAvailableCopies_throwsBookNotAvailableException() {
        book.setAvailableCopies(0);
        when(userService.getUserEntityById(1L)).thenReturn(user);
        when(bookService.getBookEntityById(1L)).thenReturn(book);

        assertThatThrownBy(() -> borrowService.borrowBook(1L, 1L))
                .isInstanceOf(BookNotAvailableException.class)
                .hasMessage("Book not available");

        verify(borrowRecordRepo, never()).save(any());
        verify(bookService, never()).updateBookEntity(any());
    }

    @Test
    void borrowBook_limitReached_throwsRuntimeException() {
        when(userService.getUserEntityById(1L)).thenReturn(user);
        when(bookService.getBookEntityById(1L)).thenReturn(book);
        when(borrowRecordRepo.countByUserAndStatus(user, "BORROWED")).thenReturn(3L);

        assertThatThrownBy(() -> borrowService.borrowBook(1L, 1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Borrow limit reached");

        verify(borrowRecordRepo, never()).save(any());
    }

    @Test
    void returnRecord_onTime_setsFineZeroAndIncrementsStock() {
        BorrowRecord record = new BorrowRecord();
        record.setId(10L);
        record.setUser(user);
        record.setBook(book);
        record.setStatus("BORROWED");

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 1); // due tomorrow
        record.setDueDate(cal.getTime());

        when(borrowRecordRepo.findById(10L)).thenReturn(Optional.of(record));
        when(borrowRecordRepo.save(any(BorrowRecord.class))).thenAnswer(inv -> inv.getArgument(0));

        BorrowDto result = borrowService.returnRecord(10L);

        assertThat(result.getStatus()).isEqualTo("RETURNED");
        assertThat(result.getFine()).isZero();
        assertThat(book.getAvailableCopies()).isEqualTo(4); // 3+1
        assertThat(result.getReturnDate()).isNotNull();

        verify(bookService).updateBookEntity(book);
    }

    @Test
    void returnRecord_late_calculatesFine() {
        BorrowRecord record = new BorrowRecord();
        record.setId(10L);
        record.setUser(user);
        record.setBook(book);
        record.setStatus("BORROWED");

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -3); // due 3 days ago
        record.setDueDate(cal.getTime());

        when(borrowRecordRepo.findById(10L)).thenReturn(Optional.of(record));
        when(borrowRecordRepo.save(any(BorrowRecord.class))).thenAnswer(inv -> inv.getArgument(0));

        BorrowDto result = borrowService.returnRecord(10L);

        assertThat(result.getFine()).isEqualTo(30.0); // 3 days * 10
        assertThat(result.getStatus()).isEqualTo("RETURNED");
    }

    @Test
    void returnRecord_alreadyReturned_throwsIllegalStateException() {
        BorrowRecord record = new BorrowRecord();
        record.setStatus("RETURNED");
        when(borrowRecordRepo.findById(10L)).thenReturn(Optional.of(record));

        assertThatThrownBy(() -> borrowService.returnRecord(10L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Book already returned");

        verify(bookService, never()).updateBookEntity(any());
    }

    @Test
    void returnRecord_notFound_throwsRuntimeException() {
        when(borrowRecordRepo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> borrowService.returnRecord(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Can't find a book ");
    }
}


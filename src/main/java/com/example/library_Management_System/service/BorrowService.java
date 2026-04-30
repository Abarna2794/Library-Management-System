package com.example.library_Management_System.service;

import com.example.library_Management_System.dto.BorrowDto;
import com.example.library_Management_System.entity.Book;
import com.example.library_Management_System.entity.BorrowRecord;
import com.example.library_Management_System.entity.User;
import com.example.library_Management_System.exceptionHandler.BookNotAvailableException;
import com.example.library_Management_System.repo.BorrowRecordRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
@Service
public class BorrowService {
    @Autowired
    private UserService userService;
    @Autowired
    private BookService bookService;
    @Autowired
    private BorrowRecordRepo borrowRecordRepo;

    private BorrowDto convertToDto(BorrowRecord record) {

        BorrowDto dto = new BorrowDto();

        dto.setId(record.getId());
        dto.setUserId(record.getUser().getId());
        dto.setBookId(record.getBook().getId());
        dto.setBookTitle(record.getBook().getTitle());
        dto.setBorrowDate(record.getBorrowDate());
        dto.setReturnDate(record.getReturnDate());
        dto.setStatus(record.getStatus());
        dto.setDueDate(record.getDueDate());
        dto.setFine(record.getFine());

        return dto;
    }

    public BorrowDto borrowBook(Long userId, Long bookId) {

        User user = userService.getUserEntityById(userId);
        Book book=bookService.getBookEntityById(bookId);

        if (book.getAvailableCopies() <= 0) {
            throw new BookNotAvailableException("Book not available");
        }

        long count = borrowRecordRepo.countByUserAndStatus(user, "BORROWED");

        if (count >= 3) {
            throw new RuntimeException("Borrow limit reached");
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_MONTH, 7); // 7 days limit


        BorrowRecord record = new BorrowRecord();
        record.setUser(user);
        record.setBook(book);
        record.setBorrowDate(new Date());
        record.setStatus("BORROWED");
        record.setDueDate(calendar.getTime());

        book.setAvailableCopies(book.getAvailableCopies() - 1);

        borrowRecordRepo.save(record);
        bookService.updateBookEntity(book); // better approach

        return convertToDto(record);
    }
    public BorrowDto returnRecord(Long recordId){
        BorrowRecord borrowRecord=borrowRecordRepo.findById(recordId)
                .orElseThrow(() ->new RuntimeException("Can't find a book "));
        if ("RETURNED".equals(borrowRecord.getStatus())){
            throw new IllegalStateException("Book already returned");
        }

        borrowRecord.setStatus("RETURNED");
        borrowRecord.setReturnDate(new Date());
        Book book=borrowRecord.getBook();
      book.setAvailableCopies(book.getAvailableCopies()+1);

        Date returnDate = new Date();
        borrowRecord.setReturnDate(returnDate);
        Date dueDate = borrowRecord.getDueDate();

        if (returnDate.after(dueDate)) {
            long diff = returnDate.getTime() - dueDate.getTime();
            long daysLate = diff / (1000 * 60 * 60 * 24);
            double fine = daysLate * 10; // ₹10 per day
            borrowRecord.setFine(fine);
        } else {
            borrowRecord.setFine(0);
        }

        bookService.updateBookEntity( book);
        borrowRecordRepo.save(borrowRecord);
        return convertToDto(borrowRecord);

    }
}

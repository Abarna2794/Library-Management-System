package com.example.library_Management_System.repo;

import com.example.library_Management_System.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface BookRepo extends JpaRepository<Book,Long> {
    boolean existsByIsbn(String isbn);
    List<Book> findByTitleContainingIgnoreCase(String title);

    List<Book> findByAuthorContainingIgnoreCase(String author);
    List<Book> findByAvailableCopiesGreaterThan(int copies);
}

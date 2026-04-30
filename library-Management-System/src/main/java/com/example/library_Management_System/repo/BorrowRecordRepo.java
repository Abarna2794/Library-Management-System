package com.example.library_Management_System.repo;

import com.example.library_Management_System.entity.BorrowRecord;
import com.example.library_Management_System.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BorrowRecordRepo extends JpaRepository<BorrowRecord,Long> {
 long countByUserAndStatus(User user, String status);
}

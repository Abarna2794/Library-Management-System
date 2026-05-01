package com.example.library_Management_System.controller;

import com.example.library_Management_System.dto.BorrowDto;
import com.example.library_Management_System.service.BorrowService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@SecurityRequirement(name = "bearerAuth")
public class BorrowController {
    @Autowired
    private BorrowService borrowService;
    @PostMapping("/user/borrow/{userId}/{bookId}")
    public BorrowDto borrowBook (@Valid @PathVariable Long userId, @PathVariable long bookId){
        return  borrowService.borrowBook(userId,bookId);
    }
    @PostMapping("/user/return/{recordId}")
    public BorrowDto returnBook (@Valid @PathVariable Long recordId){
        return  borrowService.returnRecord(recordId);
    }
}

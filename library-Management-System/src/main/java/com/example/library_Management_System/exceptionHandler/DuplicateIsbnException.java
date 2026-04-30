package com.example.library_Management_System.exceptionHandler;

public class DuplicateIsbnException extends RuntimeException {
    public DuplicateIsbnException(String msg) {
        super(msg);
    }
}

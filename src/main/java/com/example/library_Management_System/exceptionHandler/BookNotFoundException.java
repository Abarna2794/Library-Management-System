package com.example.library_Management_System.exceptionHandler;

public class BookNotFoundException extends RuntimeException{
    public BookNotFoundException(Long id){
        super("Book not found with id"+ id);
    }
}

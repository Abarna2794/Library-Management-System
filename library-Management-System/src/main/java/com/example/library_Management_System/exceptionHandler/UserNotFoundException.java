package com.example.library_Management_System.exceptionHandler;

public class UserNotFoundException extends RuntimeException{

        public UserNotFoundException(String message) {
            super(message);

    }
}

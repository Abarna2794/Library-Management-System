package com.example.library_Management_System.service;

import com.example.library_Management_System.dto.BookDto;
import com.example.library_Management_System.dto.RegisterDto;
import com.example.library_Management_System.dto.UserDto;
import com.example.library_Management_System.entity.Book;
import com.example.library_Management_System.entity.Role;
import com.example.library_Management_System.entity.User;
import com.example.library_Management_System.exceptionHandler.EmailAlreadyExistsException;
import com.example.library_Management_System.exceptionHandler.UserNotFoundException;
import com.example.library_Management_System.repo.UserRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepo userRepo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void registerUser_ShouldThrow_WhenEmailAlreadyExists() {
        // Arrange - create sample input
        RegisterDto inputDto = new RegisterDto();
        inputDto.setName("John");
        inputDto.setEmail("john@gmail.com");
        inputDto.setPassword("pass123");

        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setEmail("john@gmail.com");

        when(userRepo.findByEmail("john@gmail.com")).thenReturn(Optional.of(existingUser));

        // Act + Assert
        EmailAlreadyExistsException ex = assertThrows(
                EmailAlreadyExistsException.class,
                () -> userService.registerUser(inputDto)
        );

        assertEquals("Email already registered: john@gmail.com", ex.getMessage());
        verify(userRepo).findByEmail("john@gmail.com");
        verify(passwordEncoder, never()).encode(any());
        verify(userRepo, never()).save(any()); // Make sure we never save
    }

    @Test
    void registerUser_ShouldReturnUserDto_WhenEmailNotTaken() {
        RegisterDto inputDto = new RegisterDto();
        inputDto.setName("John");
        inputDto.setEmail("john@gmail.com");
        inputDto.setPassword("plainPass123");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setName("John");
        savedUser.setEmail("john@gmail.com");
        savedUser.setPassword("encodedPass");
        savedUser.setRole(Role.USER);

        when(userRepo.findByEmail("john@gmail.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("plainPass123")).thenReturn("encodedPass");
        when(userRepo.save(any(User.class))).thenReturn(savedUser);

        UserDto result = userService.registerUser(inputDto);

        // More assertions
        assertEquals(1L, result.getId());
        assertEquals("John", result.getName());
        assertEquals("john@gmail.com", result.getEmail());
        assertEquals(Role.USER, result.getRole());

        verify(userRepo).findByEmail("john@gmail.com");
        verify(passwordEncoder).encode("plainPass123");
        // Verify the user passed to save() has the encoded password
        verify(userRepo).save(argThat(user ->
                user.getPassword().equals("encodedPass") &&
                        user.getRole() == Role.USER
        ));
    }
    @Test
    void registerUser_ShouldThrow_WhenEmailEmpty() {
        RegisterDto dto =new RegisterDto();
        dto.setEmail(" ");
        dto.setPassword("plainPass123");
        assertThrows(RuntimeException.class, ()-> userService.registerUser(dto));
    }

    @Test
    void registerUser_ShouldThrow_WhenPasswordEmpty() {
        RegisterDto dto = new RegisterDto();
        dto.setEmail("test@gmail.com");
        dto.setPassword("");

        assertThrows(RuntimeException.class, () -> userService.registerUser(dto));
    }

    @Test
    void getUserById_ShouldReturnDto_WhenFound() {
        User user = new User();
        user.setId(1L);
        user.setName("John");
        user.setEmail("john@gmail.com");
        user.setRole(Role.USER);

        when(userRepo.findById(1L)).thenReturn(Optional.of(user));

        UserDto result = userService.getUserById(1L);

        assertEquals("John", result.getName());
        assertEquals(1L, result.getId());
    }

    @Test
    void getUserById_ShouldThrow_WhenNotFound() {
        when(userRepo.findById(99L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.getUserById(99L));
    }

}






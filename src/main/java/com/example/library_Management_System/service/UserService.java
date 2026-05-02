package com.example.library_Management_System.service;

import com.example.library_Management_System.dto.UserDto;
import com.example.library_Management_System.dto.RegisterDto;
import com.example.library_Management_System.entity.Role;
import com.example.library_Management_System.entity.User;
import com.example.library_Management_System.exceptionHandler.UserNotFoundException;
import com.example.library_Management_System.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;


    private UserDto convertToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        return dto;
    }


    private User convertToEntity(RegisterDto dto) {
        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(Role.ADMIN);
        return user;
    }


    public UserDto registerUser(RegisterDto registerDto) {

        // simple validation
        if (registerDto.getEmail() == null || registerDto.getEmail().isEmpty()) {
            throw new RuntimeException("Email cannot be empty");
        }

        if (registerDto.getPassword() == null || registerDto.getPassword().isEmpty()) {
            throw new RuntimeException("Password cannot be empty");
        }

        // convert + save
        User user = convertToEntity(registerDto);
        User savedUser = userRepo.save(user);

        return convertToDto(savedUser);
    }


    public UserDto getUserById(Long id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        return convertToDto(user);
    }


    public User getUserByEmail(String email) {
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new  UserNotFoundException("User not found with email: " + email));
    }
    public User getUserEntityById(Long id) {
        return userRepo.findById(id)
                .orElseThrow(() -> new  UserNotFoundException("User not found with id: " + id));
    }
}
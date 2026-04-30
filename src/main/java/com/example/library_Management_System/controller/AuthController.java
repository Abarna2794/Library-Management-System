package com.example.library_Management_System.controller;

import com.example.library_Management_System.AuthRequest;
import com.example.library_Management_System.JwtUtil;
import com.example.library_Management_System.dto.RegisterDto;
import com.example.library_Management_System.dto.UserDto;
import com.example.library_Management_System.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;


    @PostMapping("/register")
    public UserDto register(@Valid @RequestBody RegisterDto registerDto) {
        return userService.registerUser(registerDto);
    }


    @PostMapping("/login")
    public Map<String, String> login(@Valid@RequestBody AuthRequest request) {


        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        String token = jwtUtil.generateToken(request.getEmail());


        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        response.put("type", "Bearer");

        return response;
    }
}
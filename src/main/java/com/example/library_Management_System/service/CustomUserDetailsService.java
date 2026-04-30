package com.example.library_Management_System.service;

import com.example.library_Management_System.entity.User;
import com.example.library_Management_System.repo.UserRepo;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;



@Getter
@Setter
@Service
public class CustomUserDetailsService implements UserDetailsService {

        @Autowired
        private UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String email){

        System.out.println("login email"+email);

            User user = userRepo.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            return new org.springframework.security.core.userdetails.User(
                    user.getEmail(),
                    user.getPassword(),
                    List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
            );

        }
    }


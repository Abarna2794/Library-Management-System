package com.example.library_Management_System.service;


import com.example.library_Management_System.entity.User;
import com.example.library_Management_System.entity.Role; // your enum
import com.example.library_Management_System.repo.UserRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock UserRepo userRepo;
    @InjectMocks CustomUserDetailsService service;

    @Test
    void loadUserByUsername_emailExists_returnsUserDetailsWithRole() {
        User user = new User();
        user.setId(1L);
        user.setEmail("john@test.com");
        user.setPassword("encodedPw");
        user.setRole(Role.USER); // assuming Role is enum: USER, ADMIN

        when(userRepo.findByEmail("john@test.com")).thenReturn(Optional.of(user));

        UserDetails result = service.loadUserByUsername("john@test.com");

        assertThat(result.getUsername()).isEqualTo("john@test.com");
        assertThat(result.getPassword()).isEqualTo("encodedPw");
        assertThat(result.getAuthorities())
                .extracting(GrantedAuthority::getAuthority)
                .containsExactly("ROLE_USER");
        assertThat(result.isEnabled()).isTrue();
    }

    @Test
    void loadUserByUsername_emailNotFound_throwsUsernameNotFoundException() {
        when(userRepo.findByEmail("ghost@test.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.loadUserByUsername("ghost@test.com"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User not found");
    }

    @Test
    void loadUserByUsername_adminRole_mapsToRoleAdmin() {
        User user = new User();
        user.setEmail("admin@test.com");
        user.setPassword("pw");
        user.setRole(Role.ADMIN);

        when(userRepo.findByEmail("admin@test.com")).thenReturn(Optional.of(user));

        UserDetails result = service.loadUserByUsername("admin@test.com");

        assertThat(result.getAuthorities())
                .extracting("authority")
                .containsExactly("ROLE_ADMIN");
    }
}

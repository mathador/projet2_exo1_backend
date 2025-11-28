package com.openclassrooms.etudiant.configuration.security;

import com.openclassrooms.etudiant.entities.User;
import com.openclassrooms.etudiant.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailService customUserDetailService;

    @Test
    void testLoadUserByUsername_UserFound() {
        // GIVEN
        String login = "testuser";
        User user = new User();
        user.setLogin(login);
        user.setPassword("password");

        // WHEN
        when(userRepository.findByLogin(login)).thenReturn(Optional.of(user));
        UserDetails userDetails = customUserDetailService.loadUserByUsername(login);

        // THEN
        assertNotNull(userDetails);
        assertEquals(login, userDetails.getUsername());
        assertEquals("password", userDetails.getPassword());
    }

    @Test
    void testLoadUserByUsername_UserNotFound() {
        // GIVEN
        String login = "nonexistentuser";

        // WHEN
        when(userRepository.findByLogin(login)).thenReturn(Optional.empty());

        // THEN
        assertThrows(UsernameNotFoundException.class, () -> {
            customUserDetailService.loadUserByUsername(login);
        });
    }

    @Test
    void testLoadUserByUsername_UserNotFound_WithMessage() {
        // GIVEN
        String login = "nonexistentuser";
        when(userRepository.findByLogin(login)).thenReturn(Optional.empty());

        // WHEN
        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> customUserDetailService.loadUserByUsername(login));

        // THEN
        String expectedMessage = "User Not Found with username: " + login;
        assertEquals(expectedMessage, exception.getMessage());
    }
}
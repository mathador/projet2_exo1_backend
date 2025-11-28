package com.openclassrooms.etudiant.entities;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.time.LocalDateTime;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testUserGettersAndSetters() {
        // GIVEN
        User user = new User();
        Long id = 1L;
        String firstName = "John";
        String lastName = "Doe";
        String login = "johndoe";
        String password = "password123";
        LocalDateTime now = LocalDateTime.now();

        // WHEN
        user.setId(id);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setLogin(login);
        user.setPassword(password);
        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        // THEN
        assertEquals(id, user.getId());
        assertEquals(firstName, user.getFirstName());
        assertEquals(lastName, user.getLastName());
        assertEquals(login, user.getLogin());
        assertEquals(password, user.getPassword());
        assertEquals(now, user.getCreatedAt());
        assertEquals(now, user.getUpdatedAt());
    }

    @Test
    void testUserDetailsMethods() {
        // GIVEN
        User user = new User();
        user.setLogin("testuser");

        // WHEN
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
        String username = user.getUsername();
        boolean isAccountNonExpired = user.isAccountNonExpired();
        boolean isAccountNonLocked = user.isAccountNonLocked();
        boolean isCredentialsNonExpired = user.isCredentialsNonExpired();
        boolean isEnabled = user.isEnabled();

        // THEN
        assertNotNull(authorities);
        assertTrue(authorities.isEmpty());
        assertEquals("testuser", username);
        assertTrue(isAccountNonExpired);
        assertTrue(isAccountNonLocked);
        assertTrue(isCredentialsNonExpired);
        assertTrue(isEnabled);
    }

    @Test
    void testConstructors() {
        // GIVEN
        Long id = 1L;
        String firstName = "Jane";
        String lastName = "Doe";
        String login = "janedoe";
        String password = "password456";
        LocalDateTime now = LocalDateTime.now();

        // WHEN
        User noArgsConstructor = new User();
        User allArgsConstructor = new User(id, firstName, lastName, login, password, now, now);

        // THEN
        assertNotNull(noArgsConstructor);
        assertNull(noArgsConstructor.getId());

        assertEquals(id, allArgsConstructor.getId());
        assertEquals(firstName, allArgsConstructor.getFirstName());
        assertEquals(lastName, allArgsConstructor.getLastName());
        assertEquals(login, allArgsConstructor.getLogin());
        assertEquals(password, allArgsConstructor.getPassword());
        assertEquals(now, allArgsConstructor.getCreatedAt());
        assertEquals(now, allArgsConstructor.getUpdatedAt());
    }
}
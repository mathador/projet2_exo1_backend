package com.openclassrooms.etudiant.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtService, "jwtSecret", "testsecret");
    }

    @Test
    void testGenerateAndExtractUsername() {
        // GIVEN
        UserDetails userDetails = User
                .withUsername("testuser")
                .password("password")
                .authorities("USER")
                .build();
        // WHEN
        String token = jwtService.generateToken(userDetails);
        String username = jwtService.extractUsername(token);
        // THEN
        assertThat(username).isEqualTo("testuser");
    }

    @Test
    void testValidateToken() {
        // GIVEN
        UserDetails userDetails = User
                .withUsername("testuser")
                .password("password")
                .authorities("USER")
                .build();
        // WHEN
        String token = jwtService.generateToken(userDetails);
        boolean isValid = jwtService.validateToken(token);
        // THEN
        assertThat(isValid).isTrue();
    }

    @Test
    void testValidateToken_Expired() throws InterruptedException {
        // GIVEN
        ReflectionTestUtils.setField(jwtService, "jwtSecret", "testsecret");
        // token generated with 1 sec expiration for testing
        long now = System.currentTimeMillis() / 1000L;
        long exp = now + 1; // 1 second
        String headerJson = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
        String payloadJson = String.format("{\"sub\":\"%s\",\"iat\":%d,\"exp\":%d}", "testuser", now, exp);
        String header = ReflectionTestUtils.invokeMethod(jwtService, "base64UrlEncode", headerJson.getBytes());
        String payload = ReflectionTestUtils.invokeMethod(jwtService, "base64UrlEncode", payloadJson.getBytes());
        String unsignedToken = String.format("%s.%s", header, payload);
        String signature = ReflectionTestUtils.invokeMethod(jwtService, "hmacSha256", unsignedToken, "testsecret");
        String token = unsignedToken + "." + signature;
        // WHEN
        Thread.sleep(2000); // Wait for token to expire

        boolean isValid = jwtService.validateToken(token);
        // THEN
        assertThat(isValid).isFalse();
    }

    @Test
    void testValidateToken_InvalidSignature() {
        // GIVEN
        UserDetails userDetails = User
                .withUsername("testuser")
                .password("password")
                .authorities("USER")
                .build();
        String token = jwtService.generateToken(userDetails);
        // WHEN
        // On falsifie la signature du token
        String tamperedToken = token.substring(0, token.length() - 5) + "abcde";
        boolean isValid = jwtService.validateToken(tamperedToken);
        // THEN
        assertThat(isValid).isFalse();
    }

    @Test
    void testExtractUsername_InvalidToken() {
        // GIVEN
        String invalidToken = "invalid.token";
        // WHEN & THEN
        assertThatThrownBy(() -> jwtService.extractUsername(invalidToken))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Last unit does not have enough valid bits");
    }
}

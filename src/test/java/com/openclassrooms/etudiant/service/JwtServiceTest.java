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
public class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(jwtService, "jwtSecret", "testsecret");
    }

    @Test
    public void testGenerateAndExtractUsername() {
        UserDetails userDetails = User.withUsername("testuser").password("password").authorities("USER").build();
        String token = jwtService.generateToken(userDetails);
        String username = jwtService.extractUsername(token);
        assertThat(username).isEqualTo("testuser");
    }

    @Test
    public void testValidateToken() {
        UserDetails userDetails = User.withUsername("testuser").password("password").authorities("USER").build();
        String token = jwtService.generateToken(userDetails);
        boolean isValid = jwtService.validateToken(token);
        assertThat(isValid).isTrue();
    }

    @Test
    public void testValidateToken_Expired() throws InterruptedException {
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

        Thread.sleep(2000); // Wait for token to expire

        boolean isValid = jwtService.validateToken(token);
        assertThat(isValid).isFalse();
    }


    @Test
    public void testValidateToken_InvalidSignature() {
        UserDetails userDetails = User.withUsername("testuser").password("password").authorities("USER").build();
        String token = jwtService.generateToken(userDetails);
        // Tamper with the token
        String tamperedToken = token.substring(0, token.length() - 5) + "abcde";
        boolean isValid = jwtService.validateToken(tamperedToken);
        assertThat(isValid).isFalse();
    }

    @Test
    public void testExtractUsername_InvalidToken() {
        String invalidToken = "invalid.token";
        assertThatThrownBy(() -> jwtService.extractUsername(invalidToken))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Last unit does not have enough valid bits");
    }
}

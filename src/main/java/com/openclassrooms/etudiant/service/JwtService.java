package com.openclassrooms.etudiant.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class JwtService {

    @Value("${JWT_SECRET:changeit}")
    private String jwtSecret;

    // generate a minimal JWT (HS256) without external libraries
    public String generateToken(UserDetails userDetails) {
        long now = System.currentTimeMillis() / 1000L;
        long exp = now + 3600; // 1 hour

        String headerJson = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
        String payloadJson = String.format("{\"sub\":\"%s\",\"iat\":%d,\"exp\":%d}",
                userDetails.getUsername(), now, exp);

        String header = base64UrlEncode(headerJson.getBytes(StandardCharsets.UTF_8));
        String payload = base64UrlEncode(payloadJson.getBytes(StandardCharsets.UTF_8));

        String unsignedToken = String.format("%s.%s", header, payload);
        String signature = hmacSha256(unsignedToken, jwtSecret);

        return MessageFormat.format("{0}.{1}", unsignedToken, signature);
    }

    private String base64UrlEncode(byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hmacSha256(String data, String secret) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec keySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(keySpec);
            byte[] sig = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return base64UrlEncode(sig);
        } catch (Exception e) {
            throw new RuntimeException("Unable to sign JWT token", e);
        }
    }

    public String extractUsername(String token) {
        String payload = getPayloadFromToken(token);
        Pattern pattern = Pattern.compile("\\\"sub\\\":\\\"([^\\\"]+)\\\"");
        Matcher matcher = pattern.matcher(payload);
        if (matcher.find()) {
            return matcher.group(1);
        }
        throw new RuntimeException("Unable to extract username from token");
    }

    public boolean validateToken(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                return false;
            }
            String payload = getPayloadFromToken(token);
            long exp = extractExpirationFromPayload(payload);
            long now = System.currentTimeMillis() / 1000L;
            if (exp < now) {
                return false; // Token expired
            }
            // Verify signature
            String unsignedToken = parts[0] + "." + parts[1];
            String expectedSignature = hmacSha256(unsignedToken, jwtSecret);
            return parts[2].equals(expectedSignature);
        } catch (Exception e) {
            return false;
        }
    }

    private String getPayloadFromToken(String token) {
        String[] parts = token.split("\\.");
        if (parts.length < 2) {
            throw new RuntimeException("Invalid token format");
        }
        byte[] decodedBytes = Base64.getUrlDecoder().decode(parts[1]);
        return new String(decodedBytes, StandardCharsets.UTF_8);
    }

    private long extractExpirationFromPayload(String payload) {
        Pattern pattern = Pattern.compile("\\\"exp\\\":(\\d+)");
        Matcher matcher = pattern.matcher(payload);
        if (matcher.find()) {
            return Long.parseLong(matcher.group(1));
        }
        throw new RuntimeException("Unable to extract expiration from token");
    }

}

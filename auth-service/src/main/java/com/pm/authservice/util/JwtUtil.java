package com.pm.authservice.util;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Date;

@Component
public class JwtUtil {

    private final Key secretKey;

    public JwtUtil(@Value("${jwt.secret}") String secret) {
        // HMAC-SHA256 requires key size >= 256 bits (32 bytes). Derive a 32-byte key from the secret.
        byte[] raw = secret.getBytes(StandardCharsets.UTF_8);
        try {
            byte[] keyBytes = MessageDigest.getInstance("SHA-256").digest(raw);
            this.secretKey = Keys.hmacShaKeyFor(keyBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }

    public String generateToken(String email, String role) {

        return Jwts.builder().subject(email).claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000*60*60*10)) //10 hours
                .signWith(secretKey)
                .compact();
    }


    public void validateToken(String token) {

        try{

            Jwts.parser().verifyWith((SecretKey) secretKey)
                    .build().parseSignedClaims(token);

        } catch (JwtException e){

            throw new JwtException("Invalid JWT");

        }


    }


}

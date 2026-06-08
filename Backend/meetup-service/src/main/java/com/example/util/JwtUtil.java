package com.example.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.MacAlgorithm;

import javax.crypto.SecretKey;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.Date;

public class JwtUtil {
    MacAlgorithm alg = Jwts.SIG.HS256;
    String base64Secret = "R7z6kM2s4h1JcYvWQ9nL8eTfA3xP0uDbG5rKsNwZyHU=";
    SecretKey key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(base64Secret));

    public String createToken(Long id) {
        byte[] rawKeyBytes = key.getEncoded();

        String base64Key = Base64.getEncoder().encodeToString(rawKeyBytes);
        System.out.println("Base64 Key: " + base64Key);

        return Jwts.builder()
                .header()
                    .type("JWT")
                    .and()
                .subject(id.toString())
                .issuedAt(new Date())
                .expiration(Date.from(LocalDate.now().plusDays(7).atTime(LocalTime.now()).toInstant(ZoneOffset.UTC)))
                .signWith(key, alg)
                .compact();
    }

    public String verifyToken(String token) {
        Claims content;
        try {
            content = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
        } catch (JwtException ex) {
            return ex.getMessage();
        }

        return content.getSubject();
    }
}

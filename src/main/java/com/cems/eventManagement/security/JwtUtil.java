package com.cems.eventManagement.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    // Properties file se value aayegi
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration:86400000}")
    private String expiration;

    private Key key;

    @PostConstruct
    private void initKey(){
        this.key=Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String email, String role){

        return Jwts.builder()
                .claim("role", role) // Token ke andar role chupa diya
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(
                        new Date(System.currentTimeMillis()+86400000)
                )
                .signWith(key)
                .compact();
    }

    public String extractEmail(String token){

        Claims claims=Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();

    }

    public boolean validateToken(String  token){

        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String extractRole(String token){

        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("role",String.class);
    }
}

//token valid 24 hours

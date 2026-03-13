package com.cems.eventManagement.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

public class JwtUtil {


    private static final String SECRET="RohanSingh001010@$#&qwertyasdfeggihfiojsdnconsvdonhsokvdno";

    private static final Key key= Keys.hmacShaKeyFor(SECRET.getBytes());

    public static String generateToken(String email, String role){

        return Jwts.builder()
                .claim("role", role) // Token ke andar role chupa diya
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(
                        new Date(System.currentTimeMillis()+3600000)
                )
                .signWith(key)
                .compact();
    }

    public static String extractEmail(String token){

        Claims claims=Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();

    }

    public static boolean validateToken(String  token){

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

    public static String extractRole(String token){

        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("role",String.class);
    }
}

//token valid 24 hours

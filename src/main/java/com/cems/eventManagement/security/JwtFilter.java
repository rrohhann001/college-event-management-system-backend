package com.cems.eventManagement.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    )throws ServletException, IOException {

        // 1. Header se "Authorization" nikaalo
        String authHeader=request.getHeader("Authorization");
        String token=null;
        String email=null;

        // 2. Check karo ki token 'Bearer ' se shuru hota hai ya nahi
        if(authHeader!=null&&authHeader.startsWith("Bearer ")){
            token=authHeader.substring(7);
            try {
                email=JwtUtil.extractEmail(token);
            } catch (Exception e){
                System.out.println("Invalid Token");
            }

        }

        // 3. Agar token sahi hai aur user abhi login nahi hai, toh usko login (authenticate) kar do
        if(email!=null && SecurityContextHolder.getContext().getAuthentication()==null){
            if(JwtUtil.validateToken(token)){

                String role=JwtUtil.extractRole(token);

                List<GrantedAuthority> authorities= Collections.singletonList(new SimpleGrantedAuthority(role));

                // Spring Security ko bata do ki "Haan yeh user valid hai, isko andar aane do"
                UsernamePasswordAuthenticationToken authToken=
                        new UsernamePasswordAuthenticationToken(email,null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 4. Request ko aage Controller ke paas bhej do
        filterChain.doFilter(request,response);


    }
}

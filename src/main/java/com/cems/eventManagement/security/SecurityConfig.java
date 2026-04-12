package com.cems.eventManagement.security;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{


        http.cors(Customizer.withDefaults())
        .csrf(csrf->csrf.disable()) // Postman se data accept karne ke liye CSRF disable karna zaroori hai
                .authorizeHttpRequests(auth -> auth

                        // 1. Inko bina token ke allow karo (Login karne ke liye aur Swagger UI dekhne ke liye
                        .requestMatchers("/api/auth/login", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/student/register","/api/student/verify").permitAll()

                        .requestMatchers("/api/student/my-profile").authenticated()
                        .requestMatchers("/api/registrations/my-events").authenticated()

                        .requestMatchers(HttpMethod.DELETE, "/api/student/**").authenticated()

                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/events").hasAuthority("ADMIN")
                        //.requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/events/**").hasAuthority("ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/events/**").hasAuthority("ADMIN")

                        .requestMatchers("/api/student", "/api/student/**").hasAnyAuthority("ADMIN")

                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/registrations/event/**").hasAuthority("ADMIN")

                        // 2. Baaki SAARI APIs par taala laga do (Token zaroori hai)
                        .anyRequest().authenticated()
                )

                // 3. Spring ko bolo ki ab hum apna custom JwtFilter use karenge
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class );

        return http.build();
    }
}

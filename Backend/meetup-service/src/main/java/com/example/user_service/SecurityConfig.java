package com.example.user_service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Disable CSRF (Crucial for REST APIs / Postman POST requests to work)
                .csrf(csrf -> csrf.disable())

                // 2. Configure endpoint permissions
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/signup").permitAll()
                        .requestMatchers("/auth/login").permitAll()
                        .requestMatchers("/auth/verify").permitAll()
                        .requestMatchers("/backend/meetup").permitAll()
                        .requestMatchers("/error").permitAll()
                        .anyRequest().authenticated()               // Protects all other endpoints
                );

        return http.build();
    }
}

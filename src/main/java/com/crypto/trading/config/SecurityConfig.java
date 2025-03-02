package com.crypto.trading.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Disable CSRF for H2 Console
                .headers(headers -> headers
                        .frameOptions(frame -> frame.sameOrigin()) // Allow frames from the same origin (for H2 Console)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/wallets/**").permitAll()
                        .anyRequest().permitAll() // Secure other requests
                )
                .formLogin(withDefaults());

        return http.build();
    }
}
package com.project.codewithmark.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                // Disable CSRF for APIs/testing
                                .csrf(csrf -> csrf.disable())
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                // Authorization rules
                                .authorizeHttpRequests(auth -> auth
                                                // Landing page and static resources accessible without login
                                                .requestMatchers("/api/v1/auth/**").permitAll()

                                                // Open API endpoints (like user registration)
                                                .requestMatchers("/api/v1/users/**").permitAll()

                                                // Everything else requires authentication
                                                .anyRequest().authenticated())
                                // Enable form login for browser UI
                                .formLogin(form -> form.disable())
                                .httpBasic(basic -> basic.disable());

                return http.build();
        }
}

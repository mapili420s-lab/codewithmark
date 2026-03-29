package com.project.codewithmark.config;

import org.hibernate.validator.internal.util.stereotypes.Lazy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.project.codewithmark.config.security.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

        @Lazy
        private final JwtAuthenticationFilter jwtAuthenticationFilter;

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                // Disable CSRF for APIs/testing
                                .csrf(csrf -> csrf.disable())
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                // Authorization rules
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/api/v1/users/auth/**").permitAll()
                                                .requestMatchers("/api/v1/therapist/auth/**").permitAll()
                                                .requestMatchers("/api/v1/users/**").permitAll()
                                                .requestMatchers("/api/v1/therapist/**").permitAll()
                                                .requestMatchers("/api/v1/service_type/**").permitAll()
                                                .requestMatchers("/api/v1/appointments/**").authenticated()
                                                .anyRequest().authenticated())
                                // Form login disabled
                                .formLogin(form -> form.disable())
                                .httpBasic(basic -> basic.disable());

                // Add JWT filter before UsernamePasswordAuthenticationFilter
                http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }

        // Password encoder for Spring Security
        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
                return config.getAuthenticationManager();
        }

}
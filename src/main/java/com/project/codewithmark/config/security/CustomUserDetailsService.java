package com.project.codewithmark.config.security;

import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.project.codewithmark.model.entity.Therapist;
import com.project.codewithmark.model.entity.User;
import com.project.codewithmark.repository.TherapistRepository;
import com.project.codewithmark.repository.UserRepository; // You need this!

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final TherapistRepository therapistRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 1. Try to find the email in the User table first
        Optional<User> user = userRepository.findByEmailIgnoreCase(email);
        if (user.isPresent()) {
            return buildUserDetails(user.get().getEmail(), user.get().getPassword(), user.get().getRoles());
        }

        // 2. If not found, try the Therapist table
        Optional<Therapist> therapist = therapistRepository.findByEmailIgnoreCase(email);
        if (therapist.isPresent()) {
            return buildUserDetails(therapist.get().getEmail(), therapist.get().getPassword(),
                    therapist.get().getRoles());
        }

        // 3. Still nothing? Throw the error
        throw new UsernameNotFoundException("No user or therapist found with email: " + email);
    }

    // Helper method to keep code clean
    private UserDetails buildUserDetails(String email, String password,
            java.util.Set<? extends com.project.codewithmark.model.enums.Role> roles) {
        return new org.springframework.security.core.userdetails.User(
                email,
                password,
                roles.stream()
                        .map(role -> new SimpleGrantedAuthority(role.name()))
                        .collect(Collectors.toSet()));
    }
}

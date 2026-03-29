package com.project.codewithmark.service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.crypto.SecretKey;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.project.codewithmark.dto.mapper.UserMapper;
import com.project.codewithmark.dto.user_dto.LoginResponse;
import com.project.codewithmark.dto.user_dto.UserRequest;
import com.project.codewithmark.dto.user_dto.UserResponse;
import com.project.codewithmark.model.entity.User;
import com.project.codewithmark.model.enums.AccountStatus;
import com.project.codewithmark.model.enums.Role;
import com.project.codewithmark.repository.UserRepository;

import io.jsonwebtoken.Jwts;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final SecretKey key;
    private final long jwtExpirationMs = 86400000; // 1 day
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.key = Jwts.SIG.HS512.key().build();
    }

    // Service method to get all users
    public List<UserResponse> getAllUsers() {

        return userMapper.toUserResponseList(userRepository.findAll());
    }

    // Service method to get user by username
    public UserResponse getUserByUsername(String username) {
        User user = userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));

        return userMapper.toUserResponse(user);
    }

    // Service method to get user by ID
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        return userMapper.toUserResponse(user);
    }

    // Service just returns a DTO or throws exception
    public UserResponse registerUser(UserRequest userRequest) {

        if (userRepository.findByUsernameIgnoreCase(userRequest.getUsername()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
        }

        if (userRepository.findByEmailIgnoreCase(userRequest.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }

        if (userRepository.findByPhoneNumber(userRequest.getPhoneNumber()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Phonenumber already exists");
        }

        User user = userMapper.toUserEntity(userRequest);
        user.setPassword(BCrypt.hashpw(userRequest.getPassword(), BCrypt.gensalt()));
        user.setAccountStatus(AccountStatus.ACTIVE);
        user.setRoles(Collections.singleton(Role.CLIENT));

        return userMapper.toUserResponse(userRepository.save(user));
    }

    // Service method to update user details
    public UserResponse updateUser(Long id, UserRequest userRequest) {
        if (userRequest.getPassword() != null && !userRequest.getPassword().isEmpty()) {
            throw new RuntimeException("Password cannot be updated through this endpoint");
        }
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        LocalDateTime currentDate = LocalDateTime.now();
        user.setUpdatedAt(currentDate);
        userMapper.updateUserFromRequest(userRequest, user);

        return userMapper.toUserResponse(userRepository.save(user));
    }

    // Service method to partially update user details
    public UserResponse partiallyUpdateUser(Long id, UserRequest userRequest) {
        if (userRequest.getPassword() != null && !userRequest.getPassword().isEmpty()) {
            throw new RuntimeException("Password cannot be updated through this endpoint");
        }
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        userMapper.partiallyUpdateUserFromRequest(userRequest, user);

        return userMapper.toUserResponse(userRepository.save(user));
    }

    // Service method to delete user by ID
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    // Service method to authenticate user
    public LoginResponse authenticateUser(String email, String password) {
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        if (!BCrypt.checkpw(password, user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        String token = generateJwtToken(user);

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(token);
        loginResponse.setUsername(user.getUsername());
        loginResponse.setEmail(user.getEmail());
        loginResponse.setRoles(user.getRoles());

        return loginResponse;
    }

    public String generateJwtToken(User user) {
        return Jwts.builder()
                .subject(user.getEmail())
                .claim("roles", user.getRoles())
                .issuedAt(new Date())
                .expiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key, Jwts.SIG.HS512)
                .compact();
    }

    public List<UserResponse> searchUsers(String keyword) {
        // If the search bar is empty, return an empty list instead of "everyone"
        if (keyword == null || keyword.isBlank()) {
            return Collections.emptyList(); // Returns [] in JSON
        }

        Specification<User> spec = (root, query, cb) -> {
            String match = "%" + keyword.toLowerCase() + "%";

            return cb.or(
                    cb.like(cb.lower(root.get("username")), match),
                    cb.like(cb.lower(root.get("firstName")), match),
                    cb.like(cb.lower(root.get("lastName")), match),
                    cb.like(cb.lower(root.get("email")), match),
                    cb.equal(root.get("phoneNumber"), keyword));

        };
        return userMapper.toUserResponseList(userRepository.findAll(spec));
    }

}
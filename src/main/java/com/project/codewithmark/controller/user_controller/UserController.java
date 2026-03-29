package com.project.codewithmark.controller.user_controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.codewithmark.dto.user_dto.LoginRequest;
import com.project.codewithmark.dto.user_dto.LoginResponse;
import com.project.codewithmark.dto.user_dto.UserRequest;
import com.project.codewithmark.dto.user_dto.UserResponse;
import com.project.codewithmark.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        // Logic to retrieve all users from the database
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/users/search")
    public ResponseEntity<List<UserResponse>> searchUsers(@RequestParam String keyword) {
        // Logic to search users based on query parameters
        return ResponseEntity.ok(userService.searchUsers(keyword));
    }

    @PostMapping("/auth/register")
    public ResponseEntity<UserResponse> registerUser(
            @Valid @RequestBody UserRequest userRequest) {
        UserResponse createdUser = userService.registerUser(userRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @PostMapping("/auth/login")
    public ResponseEntity<LoginResponse> loginUser(@RequestBody LoginRequest loginRequest) {
        LoginResponse loginResponse = userService.authenticateUser(loginRequest.getEmail(), loginRequest.getPassword());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(loginResponse);
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id,
            @Valid @RequestBody UserRequest userRequest) {
        // Logic to update the user with the given ID
        return ResponseEntity.ok(userService.updateUser(id, userRequest));
    }

    @PatchMapping("/users/{id}")
    public ResponseEntity<UserResponse> partiallyUpdateUser(
            @PathVariable Long id,
            @RequestBody UserRequest userRequest) {
        // Logic to partially update the user with the given ID
        return ResponseEntity.ok(userService.partiallyUpdateUser(id, userRequest));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        // Logic to delete the user with the given ID
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}

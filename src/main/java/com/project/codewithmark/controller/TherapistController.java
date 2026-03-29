package com.project.codewithmark.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.codewithmark.dto.therapist_dto.LoginRequest;
import com.project.codewithmark.dto.therapist_dto.LoginResponse;
import com.project.codewithmark.dto.therapist_dto.TherapistRequest;
import com.project.codewithmark.dto.therapist_dto.TherapistResponse;
import com.project.codewithmark.service.TherapistService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
@PreAuthorize("hasAuthority('ROLE_THERAPIST')")
public class TherapistController {

    private final TherapistService therapistService;

    public TherapistController(TherapistService therapistService) {
        this.therapistService = therapistService;
    }

    @GetMapping("/therapist")
    public ResponseEntity<List<TherapistResponse>> getAllTherapists() {
        // Logic to retrieve all therapists from the database
        return ResponseEntity.ok(therapistService.getAllTherapists());
    }

    @PostMapping("/therapist/auth/register")
    public ResponseEntity<TherapistResponse> registerTherapist(@Valid @RequestBody TherapistRequest therapistRequest) {
        // Logic to register the therapist
        return ResponseEntity.status(200).body(therapistService.registerTherapist(therapistRequest));
    }

    @PostMapping("/therapist/auth/login")
    public ResponseEntity<LoginResponse> loginTherapist(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity
                .ok(therapistService.authenticateTherapist(loginRequest.getEmail(), loginRequest.getPassword()));
    }

    @PutMapping("/therapist/{id}")
    public ResponseEntity<TherapistResponse> updateTherapist(@PathVariable Long id,
            @Valid @RequestBody TherapistRequest therapistRequest) {
        // Logic to update the therapist with the given ID
        return ResponseEntity.ok(therapistService.updateTherapist(id, therapistRequest));
    }

    @PatchMapping("/therapist/{id}")
    public ResponseEntity<TherapistResponse> partiallyUpdateTherapist(@PathVariable Long id,
            @RequestBody TherapistRequest therapistRequest) {
        // Logic to update the therapist with the given ID
        return ResponseEntity.ok(therapistService.partiallyUpdateTherapist(id, therapistRequest));
    }

    @DeleteMapping("/therapist/{id}")
    public ResponseEntity<Void> deleteTherapist(@PathVariable Long id) {
        // Logic to delete the therapist with the given ID
        therapistService.deleteTherapist(id);
        return ResponseEntity.noContent().build();
    }

}
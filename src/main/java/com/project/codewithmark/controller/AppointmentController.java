package com.project.codewithmark.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.codewithmark.dto.appointment_dto.AppointmentRequest;
import com.project.codewithmark.dto.appointment_dto.AppointmentResponse;
import com.project.codewithmark.model.entity.Therapist;
import com.project.codewithmark.model.entity.User;
import com.project.codewithmark.model.enums.AppointmentStatus;
import com.project.codewithmark.repository.TherapistRepository;
import com.project.codewithmark.repository.UserRepository;
import com.project.codewithmark.service.AppointmentService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class AppointmentController {

    private final TherapistRepository therapistRepository;
    private final AppointmentService appointmentService;
    private final UserRepository userRepository;

    public AppointmentController(AppointmentService appointmentService, UserRepository userRepository,
            TherapistRepository therapistRepository) {
        this.appointmentService = appointmentService;
        this.userRepository = userRepository;
        this.therapistRepository = therapistRepository;
    }

    @GetMapping("/appointments")
    public ResponseEntity<List<AppointmentResponse>> getAllAppointments() {
        return ResponseEntity.ok(appointmentService.getAllAppointments());
    }

    @PostMapping("appointments/create")
    @PreAuthorize("hasAuthority('ROLE_CLIENT')")
    public ResponseEntity<AppointmentResponse> createAppointment(
            @Valid @RequestBody AppointmentRequest appointmentRequest, @AuthenticationPrincipal User user) {

        if (user == null) {
            user = userRepository.findById(1L)
                    .orElseThrow(() -> new RuntimeException("Database is empty! Create a user first."));
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(appointmentService.createAppointment(appointmentRequest,
                        user));
    }

    @PostMapping("/appointments/{id}/claim")
    public ResponseEntity<AppointmentResponse> claimAppointment(@PathVariable Long id,
            @AuthenticationPrincipal Therapist therapist) {

        Therapist therapistdb = therapistRepository.findByEmailIgnoreCase(therapist.getEmail())
                .orElseThrow(() -> new RuntimeException("Therapist not found in database."));

        return ResponseEntity.ok(appointmentService.claimAppointment(id, therapistdb));
    }

    @PatchMapping("/appointments/{id}/status")
    public ResponseEntity<AppointmentResponse> updateStatus(
            @PathVariable Long id,
            @RequestParam AppointmentStatus status) {
        return ResponseEntity.ok(appointmentService.updateStatus(id, status));
    }

    @GetMapping("/appointments/unclaimed")
    public ResponseEntity<List<AppointmentResponse>> unclaimedAppointment() {
        return ResponseEntity.ok(appointmentService.unclaimedAppointment());
    }

    @GetMapping("/appointments/my-appointments")
    public ResponseEntity<List<AppointmentResponse>> myAppointment(@AuthenticationPrincipal Therapist therapist) {
        Therapist therapistdb = therapistRepository.findByEmailIgnoreCase(therapist.getEmail())
                .orElseThrow(() -> new RuntimeException("Therapist not found"));
        return ResponseEntity.ok(appointmentService.myAppointments(therapistdb));
    }

}

package com.project.codewithmark.dto.appointment_dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.project.codewithmark.dto.serviceType_dto.ServiceTypeResponse;
import com.project.codewithmark.dto.therapist_dto.TherapistResponse;
import com.project.codewithmark.dto.user_dto.UserResponse;
import com.project.codewithmark.model.enums.AppointmentStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentResponse {

    private Long id;

    private TherapistResponse therapist;
    private UserResponse user;
    private ServiceTypeResponse serviceType;

    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;

    private AppointmentStatus appointmentStatus;

    private String notes;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime canceledAt;
}

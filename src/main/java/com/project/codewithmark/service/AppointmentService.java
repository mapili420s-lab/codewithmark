package com.project.codewithmark.service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.project.codewithmark.dto.appointment_dto.AppointmentRequest;
import com.project.codewithmark.dto.appointment_dto.AppointmentResponse;
import com.project.codewithmark.dto.mapper.AppointmentMapper;
import com.project.codewithmark.exception.ResourceNotFoundException;
import com.project.codewithmark.model.entity.Appointment;
import com.project.codewithmark.model.entity.ServiceType;
import com.project.codewithmark.model.entity.Therapist;
import com.project.codewithmark.model.entity.User;
import com.project.codewithmark.model.enums.AppointmentStatus;
import com.project.codewithmark.repository.AppointmentRepository;
import com.project.codewithmark.repository.ServiceTypeRepository;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final ServiceTypeRepository serviceTypeRepository;
    private final AppointmentMapper appointmentMapper;

    public AppointmentService(AppointmentRepository appointmentRepository,
            ServiceTypeRepository serviceTypeRepository,
            AppointmentMapper appointmentMapper) {
        this.appointmentRepository = appointmentRepository;
        this.serviceTypeRepository = serviceTypeRepository;
        this.appointmentMapper = appointmentMapper;
    }

    // Return all appointments
    public List<AppointmentResponse> getAllAppointments() {
        return appointmentMapper.toAppointmentResponseList(appointmentRepository.findAll());
    }

    // Create an appointments
    public AppointmentResponse createAppointment(AppointmentRequest appointmentRequest, User userDetails) {

        ServiceType serviceType = serviceTypeRepository.findById(appointmentRequest.getServiceTypeId())
                .orElseThrow(() -> new RuntimeException("Service Type not found"));

        Appointment appointment = appointmentMapper.toAppointmentEntity(appointmentRequest);

        appointment.setUser(userDetails);
        appointment.setServiceType(serviceType);
        appointment.setPrice((double) serviceType.getType().getPrice());
        appointment.setTherapist(null);
        appointment.setAppointmentStatus(AppointmentStatus.PENDING);

        if (appointmentRequest.getStartTime() != null) {
            appointment.setStartTime(appointmentRequest.getStartTime());

            int duration = serviceType.getType().getDuration();
            LocalTime endTime = appointment.getStartTime().plusMinutes(duration);
            appointment.setEndTime(endTime);
        }

        Appointment saved = appointmentRepository.save(appointment);

        return appointmentMapper.toAppointmentResponse(saved);
    }

    public AppointmentResponse claimAppointment(Long appointmentId, Therapist therapist) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        boolean isBusy = appointmentRepository.existsByTherapistAndDateAndStartTimeBetween(therapist,
                appointment.getDate(), appointment.getStartTime(), appointment.getEndTime());

        if (isBusy) {
            throw new RuntimeException("You already have an appointment during this time slot.");
        }

        if (appointment.getTherapist() != null) {
            throw new RuntimeException("This appointment has already been claimed by another therapist.");
        }

        appointment.setTherapist(therapist);
        appointment.setAppointmentStatus(AppointmentStatus.CONFIRMED);

        Appointment saved = appointmentRepository.save(appointment);

        return appointmentMapper.toAppointmentResponse(saved);
    }

    public AppointmentResponse updateStatus(Long id, AppointmentStatus newStatus) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

        if (appointment.getAppointmentStatus() == AppointmentStatus.COMPLETED) {
            throw new RuntimeException("Cannot change status of a completed appointment.");
        }

        switch (newStatus) {
            case ONGOING -> {
                // Optional: Ensure it's currently CONFIRMED before starting
                appointment.setAppointmentStatus(AppointmentStatus.ONGOING);
            }
            case COMPLETED -> {
                // Optional: Ensure it was ONGOING first
                appointment.setAppointmentStatus(AppointmentStatus.COMPLETED);
                // You could also set an actualEndTime here if needed
            }
            case CANCELED -> {
                appointment.setAppointmentStatus(AppointmentStatus.CANCELED);
                appointment.setCanceledAt(LocalDateTime.now()); // Audit timestamp
            }
            case CONFIRMED -> {
                appointment.setAppointmentStatus(AppointmentStatus.CONFIRMED);
            }
            default -> appointment.setAppointmentStatus(newStatus);
        }

        Appointment saved = appointmentRepository.save(appointment);
        return appointmentMapper.toAppointmentResponse(saved);
    }

    public List<AppointmentResponse> unclaimedAppointment() {
        return appointmentMapper.toAppointmentResponseList(
                appointmentRepository.findByTherapistIsNullAndAppointmentStatus(AppointmentStatus.PENDING));
    }

    public List<AppointmentResponse> myAppointments(Therapist therapist) {
        return appointmentMapper.toAppointmentResponseList(
                appointmentRepository.findByTherapistId(therapist.getId()));
    }

}
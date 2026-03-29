package com.project.codewithmark.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import com.project.codewithmark.dto.mapper.TherapistMapper;
import com.project.codewithmark.dto.mapper.UserMapper;
import com.project.codewithmark.dto.therapist_dto.TherapistRequest;
import com.project.codewithmark.dto.therapist_dto.TherapistResponse;
import com.project.codewithmark.model.entity.Therapist;
import com.project.codewithmark.model.enums.AppointmentStatus;
import com.project.codewithmark.model.enums.TherapistStatus;
import com.project.codewithmark.repository.AppointmentRepository;
import com.project.codewithmark.repository.TherapistRepository;

@Service
public class TherapistService {
        private final TherapistRepository therapistRepository;
        private final AppointmentRepository appointmentRepository;
        private final TherapistMapper therapistMapper;

        public TherapistService(TherapistRepository therapistRepository, AppointmentRepository appointmentRepository,
                        TherapistMapper therapistMapper) {
                this.therapistRepository = therapistRepository;
                this.appointmentRepository = appointmentRepository;
                this.therapistMapper = therapistMapper;
        }

        public List<TherapistResponse> getAllTherapists() {
                return therapistMapper.toTherapistResponseList(therapistRepository.findAll());
        }

        // Service method to get therapist by ID
        public TherapistResponse getTherapistById(Long id) {
                Therapist therapist = therapistRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Therapist not found with id: " + id));

                return therapistMapper.toTherapistResponse(therapist);

        }

        // Service method to get therapist by email
        public TherapistResponse getTherapistByEmail(String email) {
                Therapist therapist = therapistRepository.findByEmail(email)
                                .orElseThrow(() -> new RuntimeException("Therapist not found with email: " + email));

                return therapistMapper.toTherapistResponse(therapist);
        }

        // Service method to get therapists by status
        public List<TherapistResponse> getTherapistsByStatus(String status) {
                return therapistRepository.findByStatusIgnoreCase(status)
                                .stream()
                                .map(therapistMapper::toTherapistResponse)
                                .toList();
        }

        // Service method to return Status only
        public TherapistStatus getTherapistStatus(Therapist therapist) {

                List<AppointmentStatus> ACTIVE_STATUSES = List.of(AppointmentStatus.CONFIRMED,
                                AppointmentStatus.PENDING, AppointmentStatus.ONGOING);

                if (therapist.getStatus() == TherapistStatus.OFFLINE) {
                        return TherapistStatus.OFFLINE;
                }

                boolean hasActiveAppointment = appointmentRepository.existsByTherapistAndAppointmentStatusIn(therapist,
                                ACTIVE_STATUSES);

                if (hasActiveAppointment) {
                        return TherapistStatus.BUSY;
                }

                return TherapistStatus.AVAILABLE;
        }

        // Service method to register a new therapist
        public TherapistResponse registerTherapist(TherapistRequest therapistRequest) {

                if (therapistRepository.findByEmail(therapistRequest.getEmail()).isPresent()) {
                        throw new RuntimeException(
                                        "Therapist with email " + therapistRequest.getEmail() + " already exists");
                }

                if (therapistRepository.findByFirstNameAndLastName(therapistRequest.getFirstName(),
                                therapistRequest.getLastName()).isPresent()) {
                        throw new RuntimeException("Therapist with name " + therapistRequest.getFirstName() + " "
                                        + therapistRequest.getLastName() + " already exists");
                }

                if (therapistRepository.findByPhoneNumber(therapistRequest.getPhoneNumber()).isPresent()) {
                        throw new RuntimeException("Therapist with phone number " + therapistRequest.getPhoneNumber()
                                        + " already exists");
                }

                Therapist therapist = therapistMapper.toTherapistEntity(therapistRequest);
                therapist.setPassword(BCrypt.hashpw(therapistRequest.getPassword(), BCrypt.gensalt()));

                return therapistMapper.toTherapistResponse(therapistRepository.save(therapist));
        }

        // Service method to update therapist by ID
        public TherapistResponse updateTherapist(Long id, TherapistRequest therapistRequest) {
                if (therapistRequest.getPassword() != null && !therapistRequest.getPassword().isEmpty()) {
                        throw new RuntimeException("Password cannot be updated");
                }
                Therapist therapist = therapistRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Therapist not found with id: " + id));

                therapistMapper.updateTherapistFromRequest(therapistRequest, therapist);

                return therapistMapper.toTherapistResponse(therapistRepository.save(therapist));

        }

        // Service method to partially update therapist by ID
        public TherapistResponse partiallyUpdateTherapist(Long id, TherapistRequest therapistRequest) {
                if (therapistRequest.getPassword() != null && !therapistRequest.getPassword().isEmpty()) {
                        throw new RuntimeException("Password cannot be updated");
                }
                Therapist therapist = therapistRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Therapist not found with id: " + id));

                therapistMapper.partiallyUpdateTherapistFromRequest(therapistRequest, therapist);

                return therapistMapper.toTherapistResponse(therapistRepository.save(therapist));

        }

        // Service method to delete therapist by ID
        public void deleteTherapist(Long id) {
                if (!therapistRepository.existsById(id)) {
                        throw new RuntimeException("Therapist not found with id: " + id);
                }

                therapistRepository.deleteById(id);
        }
}

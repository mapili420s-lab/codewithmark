package com.project.codewithmark.service;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.crypto.SecretKey;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.project.codewithmark.dto.mapper.TherapistMapper;
import com.project.codewithmark.dto.therapist_dto.LoginResponse;
import com.project.codewithmark.dto.therapist_dto.TherapistRequest;
import com.project.codewithmark.dto.therapist_dto.TherapistResponse;
import com.project.codewithmark.model.entity.Therapist;
import com.project.codewithmark.model.enums.AccountStatus;
import com.project.codewithmark.model.enums.AppointmentStatus;
import com.project.codewithmark.model.enums.Role;
import com.project.codewithmark.model.enums.TherapistStatus;
import com.project.codewithmark.repository.AppointmentRepository;
import com.project.codewithmark.repository.TherapistRepository;

import io.jsonwebtoken.Jwts;

@Service
public class TherapistService {
        private final TherapistRepository therapistRepository;
        private final AppointmentRepository appointmentRepository;
        private final TherapistMapper therapistMapper;
        private final SecretKey key;
        private final long jwtExpirationMs = 86400000; // 1 day

        public TherapistService(TherapistRepository therapistRepository, AppointmentRepository appointmentRepository,
                        TherapistMapper therapistMapper) {
                this.therapistRepository = therapistRepository;
                this.appointmentRepository = appointmentRepository;
                this.therapistMapper = therapistMapper;
                this.key = Jwts.SIG.HS512.key().build();
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
                Therapist therapist = therapistRepository.findByEmailIgnoreCase(email)
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

                if (therapistRepository.findByEmailIgnoreCase(therapistRequest.getEmail()).isPresent()) {
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
                therapist.setAccountStatus(AccountStatus.ACTIVE);
                therapist.setRoles(Collections.singleton(Role.THERAPIST));

                return therapistMapper.toTherapistResponse(therapistRepository.save(therapist));
        }

        public LoginResponse authenticateTherapist(String email, String password) {
                Therapist therapist = therapistRepository.findByEmailIgnoreCase(email)
                                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

                if (BCrypt.checkpw(password, therapist.getPassword())) {
                        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");

                }

                String token = generateJwtToken(therapist);

                LoginResponse loginResponse = new LoginResponse();
                loginResponse.setToken(token);
                loginResponse.setFirstName(therapist.getFirstName());
                loginResponse.setEmail(therapist.getEmail());
                loginResponse.setRoles(therapist.getRoles());

                return loginResponse;
        }

        public String generateJwtToken(Therapist therapist) {
                return Jwts.builder()
                                .subject(therapist.getEmail())
                                .claim("roles", therapist.getRoles())
                                .issuedAt(new Date())
                                .expiration(new Date((new Date()).getTime() + jwtExpirationMs))
                                .signWith(key, Jwts.SIG.HS512)
                                .compact();
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

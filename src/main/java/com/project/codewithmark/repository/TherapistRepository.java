package com.project.codewithmark.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.project.codewithmark.model.entity.Therapist;

public interface TherapistRepository extends JpaRepository<Therapist, Long>, JpaSpecificationExecutor<Therapist> {
    Optional<Therapist> findByEmailIgnoreCase(String email);

    Optional<Therapist> findByFirstNameAndLastName(String firstName, String lastName);

    Optional<Therapist> findByPhoneNumber(String phoneNumber);

    Optional<Therapist> findByStatusIgnoreCase(String status);
}

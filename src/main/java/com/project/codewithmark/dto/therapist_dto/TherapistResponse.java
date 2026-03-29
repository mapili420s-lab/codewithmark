package com.project.codewithmark.dto.therapist_dto;

import java.time.LocalDateTime;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.project.codewithmark.model.enums.AccountStatus;
import com.project.codewithmark.model.enums.Role;
import com.project.codewithmark.model.enums.TherapistStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TherapistResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private AccountStatus accountStatus;
    private TherapistStatus status;
    private Set<Role> roles;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}

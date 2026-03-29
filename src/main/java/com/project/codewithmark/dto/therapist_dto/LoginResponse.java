package com.project.codewithmark.dto.therapist_dto;

import java.util.Set;

import com.project.codewithmark.model.enums.Role;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    private String token;
    private String type = "Bearer";
    private String firstName;
    private String email;
    private Set<Role> roles;
}

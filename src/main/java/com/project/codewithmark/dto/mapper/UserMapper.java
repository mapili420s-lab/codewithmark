package com.project.codewithmark.dto.mapper;

import java.util.List;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import com.project.codewithmark.dto.appointment_dto.AppointmentRequest;
import com.project.codewithmark.dto.appointment_dto.AppointmentResponse;
import com.project.codewithmark.dto.serviceType_dto.ServiceTypeRequest;
import com.project.codewithmark.dto.serviceType_dto.ServiceTypeResponse;
import com.project.codewithmark.dto.therapist_dto.TherapistRequest;
import com.project.codewithmark.dto.therapist_dto.TherapistResponse;
import com.project.codewithmark.dto.user_dto.UserRequest;
import com.project.codewithmark.dto.user_dto.UserResponse;
import com.project.codewithmark.dto.user_dto.LoginResponse;
import com.project.codewithmark.model.entity.Appointment;
import com.project.codewithmark.model.entity.ServiceType;
import com.project.codewithmark.model.entity.Therapist;
import com.project.codewithmark.model.entity.User;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

        LoginResponse toLoginResponse(User user);

        UserResponse toUserResponse(User user);

        TherapistResponse toTherapistResponse(Therapist therapist);

        @Mapping(source = "type.description", target = "description")
        @Mapping(source = "type.duration", target = "duration")
        @Mapping(source = "type.price", target = "price")
        @Mapping(source = "type.code", target = "code")
        @Mapping(source = "id", target = "id")
        @Mapping(source = "type", target = "type")
        ServiceTypeResponse toServiceTypeResponse(ServiceType serviceType);

        @Mapping(source = "user", target = "user")
        @Mapping(source = "therapist", target = "therapist")
        @Mapping(source = "serviceType", target = "serviceType")
        AppointmentResponse toAppointmentResponse(Appointment appointment);

        // <----------------------------------->

        @Mapping(target = "password", ignore = true)
        User toUserEntity(UserRequest userRequest);

        @Mapping(target = "password", ignore = true)
        Therapist toTherapistEntity(TherapistRequest therapistRequest);

        @Mapping(target = "id", ignore = true)
        ServiceType toServiceTypeEntity(ServiceTypeRequest serviceTypeRequest);

        @Mapping(target = "id", ignore = true)
        @Mapping(target = "therapist", ignore = true) // Handled in Service
        @Mapping(target = "user", ignore = true) // Handled in Service
        @Mapping(target = "serviceType", ignore = true) // Handled in Service
        @Mapping(target = "appointmentStatus", constant = "PENDING") // Example default
        Appointment toAppoinmentEntity(AppointmentRequest appointmentRequest);

        // <----------------------------------->

        List<UserResponse> toUserResponseList(List<User> users);

        List<TherapistResponse> toTherapistResponseList(List<Therapist> therapists);

        List<ServiceTypeResponse> toServiceTypeResponseList(List<ServiceType> serviceTypes);

        List<AppointmentResponse> toAppointmentResponseList(List<Appointment> appointment);

        // <----------------------------------->

        @BeanMapping(nullValuePropertyMappingStrategy = org.mapstruct.NullValuePropertyMappingStrategy.IGNORE)
        @Mapping(target = "password", ignore = true)
        void updateUserFromRequest(UserRequest userRequest, @org.mapstruct.MappingTarget User user);

        @BeanMapping(nullValuePropertyMappingStrategy = org.mapstruct.NullValuePropertyMappingStrategy.IGNORE)
        @Mapping(target = "password", ignore = true)
        void updateTherapistFromRequest(TherapistRequest therapistRequest,
                        @org.mapstruct.MappingTarget Therapist therapist);

        @BeanMapping(nullValuePropertyMappingStrategy = org.mapstruct.NullValuePropertyMappingStrategy.IGNORE)
        @Mapping(target = "password", ignore = true)
        void partiallyUpdateUserFromRequest(UserRequest userRequest, @org.mapstruct.MappingTarget User user);

        @BeanMapping(nullValuePropertyMappingStrategy = org.mapstruct.NullValuePropertyMappingStrategy.IGNORE)
        @Mapping(target = "password", ignore = true)
        void partiallyUpdateTherapistFromRequest(TherapistRequest therapistRequest,
                        @org.mapstruct.MappingTarget Therapist therapist);
}

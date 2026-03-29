package com.project.codewithmark.dto.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import com.project.codewithmark.dto.appointment_dto.AppointmentRequest;
import com.project.codewithmark.dto.appointment_dto.AppointmentResponse;
import com.project.codewithmark.model.entity.Appointment;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AppointmentMapper {

    @Mapping(source = "user", target = "user")
    @Mapping(source = "therapist", target = "therapist")
    @Mapping(source = "serviceType", target = "serviceType")
    AppointmentResponse toAppointmentResponse(Appointment appointment);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "therapist", ignore = true) // Handled in Service
    @Mapping(target = "user", ignore = true) // Handled in Service
    @Mapping(target = "serviceType", ignore = true) // Handled in Service
    @Mapping(target = "endTime", ignore = true) // Calculated in Service logic
    Appointment toAppointmentEntity(AppointmentRequest appointmentRequest);

    List<AppointmentResponse> toAppointmentResponseList(List<Appointment> appointment);

}

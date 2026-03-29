package com.project.codewithmark.dto.mapper;

import java.util.List;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import com.project.codewithmark.dto.therapist_dto.TherapistRequest;
import com.project.codewithmark.dto.therapist_dto.TherapistResponse;
import com.project.codewithmark.model.entity.Therapist;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TherapistMapper {

        TherapistResponse toTherapistResponse(Therapist therapist);

        @Mapping(target = "password", ignore = true)
        Therapist toTherapistEntity(TherapistRequest therapistRequest);

        List<TherapistResponse> toTherapistResponseList(List<Therapist> therapists);

        @BeanMapping(nullValuePropertyMappingStrategy = org.mapstruct.NullValuePropertyMappingStrategy.IGNORE)
        @Mapping(target = "password", ignore = true)
        void updateTherapistFromRequest(TherapistRequest therapistRequest,
                        @org.mapstruct.MappingTarget Therapist therapist);

        @BeanMapping(nullValuePropertyMappingStrategy = org.mapstruct.NullValuePropertyMappingStrategy.IGNORE)
        @Mapping(target = "password", ignore = true)
        void partiallyUpdateTherapistFromRequest(TherapistRequest therapistRequest,
                        @org.mapstruct.MappingTarget Therapist therapist);
}

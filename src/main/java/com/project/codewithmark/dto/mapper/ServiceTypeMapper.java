package com.project.codewithmark.dto.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import com.project.codewithmark.dto.serviceType_dto.ServiceTypeRequest;
import com.project.codewithmark.dto.serviceType_dto.ServiceTypeResponse;
import com.project.codewithmark.model.entity.ServiceType;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ServiceTypeMapper {

    @Mapping(source = "type.description", target = "description")
    @Mapping(source = "type.duration", target = "duration")
    @Mapping(source = "type.price", target = "price")
    @Mapping(source = "type.code", target = "code")
    @Mapping(source = "id", target = "id")
    @Mapping(source = "type", target = "type")
    ServiceTypeResponse toServiceTypeResponse(ServiceType serviceType);

    @Mapping(target = "id", ignore = true)
    ServiceType toServiceTypeEntity(ServiceTypeRequest serviceTypeRequest);

    List<ServiceTypeResponse> toServiceTypeResponseList(List<ServiceType> serviceTypes);

}

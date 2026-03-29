package com.project.codewithmark.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.project.codewithmark.dto.mapper.ServiceTypeMapper;
import com.project.codewithmark.dto.serviceType_dto.ServiceTypeResponse;
import com.project.codewithmark.model.entity.ServiceType;
import com.project.codewithmark.model.enums.ServiceTypeEnum;
import com.project.codewithmark.repository.ServiceTypeRepository;

@Service
public class ServiceTypeService {

    private final ServiceTypeRepository serviceTypeRepository;
    private final ServiceTypeMapper serviceTypeMapper;

    public ServiceTypeService(ServiceTypeRepository serviceTypeRepository, ServiceTypeMapper serviceTypeMapper) {
        this.serviceTypeRepository = serviceTypeRepository;
        this.serviceTypeMapper = serviceTypeMapper;
    }

    public List<ServiceTypeResponse> getAllServiceTypes() {

        return serviceTypeMapper.toServiceTypeResponseList(serviceTypeRepository.findAll());
    }

    public ServiceTypeResponse getServiceTypeById(Long id) {

        ServiceType serviceType = serviceTypeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Service type not found with id: " + id));

        return serviceTypeMapper.toServiceTypeResponse(serviceType);
    }

    public ServiceTypeResponse getServiceTypeByType(ServiceTypeEnum type) {

        ServiceType serviceType = serviceTypeRepository.findByType(type)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "Service type not found with type: " + type));

        return serviceTypeMapper.toServiceTypeResponse(serviceType);
    }

}
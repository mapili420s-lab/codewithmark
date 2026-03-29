package com.project.codewithmark.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.codewithmark.dto.serviceType_dto.ServiceTypeResponse;
import com.project.codewithmark.model.enums.ServiceTypeEnum;
import com.project.codewithmark.service.ServiceTypeService;

@RestController
@RequestMapping("/api/v1")
public class ServiceTypeController {

    private final ServiceTypeService serviceTypeService;

    public ServiceTypeController(ServiceTypeService serviceTypeService) {
        this.serviceTypeService = serviceTypeService;
    }

    @GetMapping("/service_types")
    public ResponseEntity<List<ServiceTypeResponse>> getAllServiceTypes() {
        return ResponseEntity.ok(serviceTypeService.getAllServiceTypes());
    }

    @GetMapping("/service_types/{id}")
    public ResponseEntity<ServiceTypeResponse> getServiceTypeById(@PathVariable Long id) {
        return ResponseEntity.ok(serviceTypeService.getServiceTypeById(id));
    }

    @GetMapping("/service_types/type/{type}")
    public ResponseEntity<ServiceTypeResponse> getServiceTypeByType(
            @PathVariable ServiceTypeEnum type) {
        return ResponseEntity.ok(serviceTypeService.getServiceTypeByType(type));
    }

}
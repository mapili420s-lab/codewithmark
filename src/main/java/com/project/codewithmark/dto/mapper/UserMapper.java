package com.project.codewithmark.dto.mapper;

import java.util.List;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import com.project.codewithmark.dto.user_dto.LoginResponse;
import com.project.codewithmark.dto.user_dto.UserRequest;
import com.project.codewithmark.dto.user_dto.UserResponse;
import com.project.codewithmark.model.entity.User;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

        LoginResponse toLoginResponse(User user);

        UserResponse toUserResponse(User user);

        @Mapping(target = "password", ignore = true)
        User toUserEntity(UserRequest userRequest);

        List<UserResponse> toUserResponseList(List<User> users);

        @BeanMapping(nullValuePropertyMappingStrategy = org.mapstruct.NullValuePropertyMappingStrategy.IGNORE)
        @Mapping(target = "password", ignore = true)
        void updateUserFromRequest(UserRequest userRequest, @org.mapstruct.MappingTarget User user);

        @BeanMapping(nullValuePropertyMappingStrategy = org.mapstruct.NullValuePropertyMappingStrategy.IGNORE)
        @Mapping(target = "password", ignore = true)
        void partiallyUpdateUserFromRequest(UserRequest userRequest, @org.mapstruct.MappingTarget User user);

}

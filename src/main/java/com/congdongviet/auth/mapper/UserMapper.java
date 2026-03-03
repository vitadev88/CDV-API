package com.congdongviet.auth.mapper;

import com.congdongviet.auth.dto.UserResponse;
import com.congdongviet.auth.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "role", expression = "java(user.getRole().name())")
    UserResponse toUserResponse(User user);
}

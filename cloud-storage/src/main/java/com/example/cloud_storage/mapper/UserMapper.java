package com.example.cloud_storage.mapper;

import com.example.cloud_storage.dtos.RegisterRequestDto;
import com.example.cloud_storage.entity.User;

public class UserMapper {
    public static User toUser(RegisterRequestDto request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        return user;
    }
}

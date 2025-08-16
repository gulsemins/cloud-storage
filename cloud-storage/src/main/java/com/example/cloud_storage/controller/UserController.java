package com.example.cloud_storage.controller;
import com.example.cloud_storage.dtos.AuthResponseDto;
import com.example.cloud_storage.dtos.LoginRequestDto;
import com.example.cloud_storage.dtos.UserResponseDto;
import com.example.cloud_storage.repository.UserRepository;
import com.example.cloud_storage.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.cloud_storage.dtos.RegisterRequestDto;

@RestController
@AllArgsConstructor
public class UserController {
    private final UserRepository userRepository;
    private final UserService userService;

    @PostMapping("/signup")
    public UserResponseDto signup(@RequestBody RegisterRequestDto registerRequestDto){
        //return userRepository.save(user);

        return userService.signup(registerRequestDto);

    }
    @PostMapping("/login")
    public AuthResponseDto login(@RequestBody LoginRequestDto loginRequestDto){

        return userService.login(loginRequestDto);

    }
}

package com.example.cloud_storage.controller;
import com.example.cloud_storage.entity.User;
import com.example.cloud_storage.repository.UserRepository;
import com.example.cloud_storage.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.cloud_storage.dtos.LoginRequestDto;
import com.example.cloud_storage.dtos.RegisterRequestDto;
import com.example.cloud_storage.dtos.AuthResponseDto;
@RestController
@AllArgsConstructor
public class UserController {
    private final UserRepository userRepository;
    private final UserService userService;

    @PostMapping("/signup")
    public User signup(@RequestBody User user){
        //return userRepository.save(user);
        return userService.signup(user);

    }
    @PostMapping("/login")
    public String login(@RequestBody User user){

        return userService.verify(user);

    }
}

package com.example.cloud_storage.service;
import com.example.cloud_storage.dtos.AuthResponseDto;
import com.example.cloud_storage.dtos.LoginRequestDto;
import com.example.cloud_storage.dtos.RegisterRequestDto;
import com.example.cloud_storage.entity.UserEntity;
import com.example.cloud_storage.mapper.UserMapper;
import com.example.cloud_storage.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@AllArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
private final UserMapper userMapper;

    public UserEntity signup(RegisterRequestDto registerRequestDto) {
        if (userRepository.existsByUsername(registerRequestDto.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        UserEntity user = userMapper.toEntity(registerRequestDto);
        user.setPassword(bCryptPasswordEncoder
                .encode(registerRequestDto.getPassword())); //önce user passwoedunu encrypted yapıyoruz sonra kaydediyoruz
        return userRepository.save(user);
    }

    public AuthResponseDto login(LoginRequestDto loginRequestDto) {
        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequestDto.getUsername(), loginRequestDto.getPassword()
                )
        );
        //     var u =  userRepository.findByUserName(user.getUserName());
        if (authenticate.isAuthenticated()){

            String token = jwtService.generateToken(loginRequestDto.getUsername());
            return new AuthResponseDto(token);
        }
        throw new RuntimeException("Invalid login credentials");
    }

    public UserEntity getUserById(String id){
        return userRepository.findById(id).get();
    }
    public UserEntity getUserByUsername(String username){
        return userRepository.findByUsername(username);
    }
}

package com.example.cloud_storage.service;
import com.example.cloud_storage.entity.User;
import com.example.cloud_storage.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@AllArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;


    public User signup(User user) {
        user.setPassword(bCryptPasswordEncoder
                .encode(user.getPassword())); //önce user passwoedunu encrypted yapıyoruz sonra kaydediyoruz
        return userRepository.save(user);
    }

    public String verify(User user) {
        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getUsername(), user.getPassword()
                )
        );
        //     var u =  userRepository.findByUserName(user.getUserName());
        if (authenticate.isAuthenticated())
            return jwtService.generateToken(user);
        return  "failure";
    }
}

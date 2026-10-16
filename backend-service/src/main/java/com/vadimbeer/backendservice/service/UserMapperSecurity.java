package com.vadimbeer.backendservice.service;

import com.vadimbeer.backendservice.models.User;
import com.vadimbeer.backendservice.DTO.UserRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserMapperSecurity {

    private final PasswordEncoder passwordEncoder;

    public UserMapperSecurity(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public User toUser(UserRequest userRequest) {
        return new User(
            null,
            userRequest.username(),
            userRequest.email(),
            passwordEncoder.encode(userRequest.password()),
            User.Role.USER
        );
    }
}

package com.vadimbeer.backendservice.service;

import com.vadimbeer.backendservice.models.User;
import com.vadimbeer.backendservice.models.UserRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserMapperSecurity {

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User toUser(UserRequest userRequest) {
        return new User(
            null,
            userRequest.email(),
            userRequest.username(),
            passwordEncoder.encode(userRequest.password()),
            User.Role.USER
        );
    }
}

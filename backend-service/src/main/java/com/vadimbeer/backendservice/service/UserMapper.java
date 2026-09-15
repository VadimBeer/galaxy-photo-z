package com.vadimbeer.backendservice.service;

import com.vadimbeer.backendservice.models.User;
import com.vadimbeer.backendservice.models.UserRequest;
import org.springframework.stereotype.Service;

@Service
public class UserMapper {

    public User toUser(UserRequest userRequest) {
        return new User(
            null,
            userRequest.email(),
            userRequest.username(),
            userRequest.password(),
            User.Role.USER
        );
    }
}

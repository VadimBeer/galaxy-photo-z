package com.vadimbeer.backendservice.service;

import com.vadimbeer.backendservice.DTO.UserLoginResponse;
import com.vadimbeer.backendservice.DTO.UserResponse;
import com.vadimbeer.backendservice.models.User;
import org.springframework.stereotype.Service;

@Service
public class UserMapper {

    public UserLoginResponse toUserResponse(User user) {
        UserLoginResponse userResponse = new UserLoginResponse
                (user.getId(), user.getEmail());

        return userResponse;
    }
}

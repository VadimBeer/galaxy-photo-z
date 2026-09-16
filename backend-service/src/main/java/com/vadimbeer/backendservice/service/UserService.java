package com.vadimbeer.backendservice.service;

import com.vadimbeer.backendservice.jpa.UserRepository;
import com.vadimbeer.backendservice.models.User;
import com.vadimbeer.backendservice.models.UserRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapperSecurity userMapper;

    public String save(UserRequest userRequest) {
        User newUser = userMapper.toUser(userRequest);
        newUser = userRepository.save(newUser);
        return newUser.getId().toString();
    }

    public User findByUsername(String username) {
        return userRepository.getUserByUsername(username);
    }
}

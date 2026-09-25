package com.vadimbeer.backendservice.service;

import com.vadimbeer.backendservice.DTO.UserLoginResponse;
import com.vadimbeer.backendservice.DTO.UserResponse;
import com.vadimbeer.backendservice.jpa.UserRepository;
import com.vadimbeer.backendservice.models.User;
import com.vadimbeer.backendservice.DTO.UserRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final UserMapperSecurity userMapperSecurity;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public String save(UserRequest userRequest) {
        User newUser = userMapperSecurity.toUser(userRequest);
        newUser = userRepository.save(newUser);

        log.info("email {}, password {}, passwordhash {}", newUser.getEmail(),
                userRequest.password(), newUser.getPassword());

        return newUser.getId().toString();
    }

    public User findByUsername(String username) {
        return userRepository.getUserByUsername(username);
    }

    public User findByEmail(String email) {
        return userRepository.getUserByEmail(email);
    }

    public boolean existsByPassword(User user, String password) {
        return passwordEncoder.matches(password, user.getPassword());
    }

    public UserLoginResponse toUserLoginResponse(User user) {
        return userMapper.toUserResponse(user);
    }
}

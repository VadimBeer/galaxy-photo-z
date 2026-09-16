package com.vadimbeer.backendservice.controller;

import com.vadimbeer.backendservice.models.UserRequest;
import com.vadimbeer.backendservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/registration")
@RequiredArgsConstructor
@Slf4j
public class RegistrationRestController {

    private final UserService userService;
    private Logger logger = LoggerFactory.getLogger(RegistrationRestController.class);

    @PostMapping
    public ResponseEntity<?> login(@RequestBody UserRequest userRequest) {
        var id = userService.save(userRequest);
        logger.info("User with id {} has been registered successfully", id);
        return ResponseEntity.ok().body(id);
    }
}

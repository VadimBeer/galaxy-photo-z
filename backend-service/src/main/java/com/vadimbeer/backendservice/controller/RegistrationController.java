package com.vadimbeer.backendservice.controller;

import com.vadimbeer.backendservice.models.UserRequest;
import com.vadimbeer.backendservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/registration")
@RequiredArgsConstructor
public class RegistrationController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<?> login(@RequestBody UserRequest userRequest) {
        var id = userService.save(userRequest);
        return ResponseEntity.ok().body(id);
    }
}

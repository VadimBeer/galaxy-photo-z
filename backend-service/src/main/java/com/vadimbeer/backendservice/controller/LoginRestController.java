package com.vadimbeer.backendservice.controller;

import com.vadimbeer.backendservice.DTO.UserLoginRequest;
import com.vadimbeer.backendservice.DTO.UserLoginResponse;
import com.vadimbeer.backendservice.models.User;
import com.vadimbeer.backendservice.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//Пользователь email vadimgij11@gmail.com
//пароль 12341234

@RestController
@RequestMapping("/api/v1/login")
@RequiredArgsConstructor
public class LoginRestController {

    private final UserService userService;
    private final SecurityContextRepository securityContextRepository;

    @PostMapping
    public ResponseEntity<UserLoginResponse> login(
            @RequestBody UserLoginRequest request,
            HttpServletRequest servletRequest,
            HttpServletResponse servletResponse
    ) {
        User user = userService.findByEmail(request.email());

        if (user == null ||
                !userService.existsByPassword(user, request.password()))
        {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .build();
        }

        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(
                        user,
                        null,
                        user.getAuthorities()
                );

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authToken);
        SecurityContextHolder.setContext(context);
        securityContextRepository.saveContext(
                context,
                servletRequest,
                servletResponse
        );

        return ResponseEntity.ok(
                userService.toUserLoginResponse(user)
        );
    }
}
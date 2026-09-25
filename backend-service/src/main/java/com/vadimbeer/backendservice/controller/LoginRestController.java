package com.vadimbeer.backendservice.controller;

import com.vadimbeer.backendservice.DTO.UserLoginRequest;
import com.vadimbeer.backendservice.DTO.UserLoginResponse;
import com.vadimbeer.backendservice.models.User;
import com.vadimbeer.backendservice.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//Пользователь email vadimgij11@gmail.com
//пароль 12341234

@Slf4j
@RestController
@RequestMapping("/api/v1/login")
public class LoginRestController {

    private final UserService userService;
    // Достаёт SecurityContext в HTTP-сессию (JSESSIONID-cookie), чтобы
    // на следующих запросах @AuthenticationPrincipal User увидел пользователя.
    // Начиная со Spring Security 6 это нужно делать явно — SecurityContextHolderFilter
    // сам сохраняет контекст только для того, что было установлено ДО контроллера.
    private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

    public LoginRestController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserLoginResponse> login(
            @RequestBody UserLoginRequest request,
            HttpServletRequest servletRequest,
            HttpServletResponse servletResponse
    ) {
        User user = userService.findByEmail(request.email());

        log.info("Email from request: '{}'", request.email());
        log.info("User found: {}", user != null);

        if (user == null) {
            log.warn("User NOT found for email '{}'", request.email());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        log.info("User id: {}, email: {}", user.getId(), user.getEmail());

        if (!userService.existsByPassword(user, request.password())) {
            log.warn("Password is incorrect for user '{}'", request.email());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authToken);
        SecurityContextHolder.setContext(context);

        // Явно сохраняем контекст в сессию — без этого он будет виден только
        // до конца текущего запроса и потеряется сразу после ответа.
        securityContextRepository.saveContext(context, servletRequest, servletResponse);

        UserLoginResponse response = userService.toUserLoginResponse(user);


        return ResponseEntity.ok(response);
    }
}
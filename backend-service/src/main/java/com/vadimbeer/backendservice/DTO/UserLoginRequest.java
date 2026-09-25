package com.vadimbeer.backendservice.DTO;

public record UserLoginRequest (
        String email,
        String password
) {
}

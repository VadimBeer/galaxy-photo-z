package com.vadimbeer.backendservice.DTO;

public record UserRequest(
        String username,
        String email,
        String password
) {
}

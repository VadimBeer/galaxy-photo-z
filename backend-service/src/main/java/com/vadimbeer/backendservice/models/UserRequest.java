package com.vadimbeer.backendservice.models;

public record UserRequest(
        String username,
        String email,
        String password
) {
}

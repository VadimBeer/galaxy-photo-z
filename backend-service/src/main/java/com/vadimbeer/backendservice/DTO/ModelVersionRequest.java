package com.vadimbeer.backendservice.DTO;

public record ModelVersionRequest (
        Integer id,
        String name,
        String version
) {
}

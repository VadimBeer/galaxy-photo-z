package com.vadimbeer.backendservice.DTO;

public record TaskCreatedEvent(
        Long taskId,
        Long userId,
        Integer modelVersionId,
        String objectKey
) {
}

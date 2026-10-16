package com.vadimbeer.backendservice.DTO;

//Сущность для передачи объекта задачи
public record AnalysisTaskResponse(
        Long id,
        String filename,
        String status
) {
}

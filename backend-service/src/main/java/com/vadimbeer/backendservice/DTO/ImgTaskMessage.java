package com.vadimbeer.backendservice.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

//DTO для передачи на сторону Python
public record ImgTaskMessage(
        @JsonProperty("task_id") Long taskId,
        @JsonProperty("user_id") Long userId,
        @JsonProperty("model_version_id") Integer modelVersionId,
        @JsonProperty("object_key") String objectKey
) {
}

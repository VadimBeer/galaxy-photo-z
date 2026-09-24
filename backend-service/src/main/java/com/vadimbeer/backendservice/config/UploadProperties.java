package com.vadimbeer.backendservice.config;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.Set;

@Data
@Component
@ConfigurationProperties("app.upload")
@Validated
public class UploadProperties {

    private Long maxFileSize;
    private String storagePath;
    private String bucketName;

    @NotEmpty
    private Set<String> allowedFileTypes;
}

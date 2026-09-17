package com.vadimbeer.backendservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties("app.upload")
public class UploadProperties {

    private Long maxFileSize;
    private String storagePath;
    private String bucketName;
    private List<String> allowedFileTypes;
}

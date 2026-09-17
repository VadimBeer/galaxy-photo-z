package com.vadimbeer.backendservice.config;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.errors.MinioException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class MinioConfig {

    private final MinioProperties minioProperties;
    private final UploadProperties uploadProperties;


    @Bean
    public MinioClient minioClient(){
        MinioClient minioClient = MinioClient.builder()
                .endpoint(minioProperties.getEndpoint())
                .credentials(minioProperties.getAccessKey(), minioProperties.getSecretKey())
                .build();

        try{
            boolean found = minioClient.bucketExists(
                    BucketExistsArgs.builder()
                            .bucket(uploadProperties.getBucketName())
                            .build()
            );

            if(!found){
                minioClient.makeBucket(
                        MakeBucketArgs.builder().bucket(uploadProperties.getBucketName()).build()
                );
            }

        } catch (Exception e) {
            throw new RuntimeException("Не удалось инициализировать MinIO bucket", e);
        }

        return minioClient;
    }
}

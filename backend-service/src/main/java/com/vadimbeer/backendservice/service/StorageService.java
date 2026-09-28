package com.vadimbeer.backendservice.service;

import com.vadimbeer.backendservice.props.UploadProperties;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.MinioException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Slf4j
@Service
public class StorageService {

    private MinioClient minioClient;
    private UploadProperties uploadProperties;
    private String bucketName;


    @Autowired
    public StorageService(MinioClient minioClient,  UploadProperties uploadProperties) {
        this.minioClient = minioClient;
        this.uploadProperties = uploadProperties;
        this.bucketName = uploadProperties.getBucketName();
    }

    public void saveImageInStorage(InputStream file, String objectKey,
                                   long size, String contentType
    ) {
        if(!typeImage(contentType)) {
            return;
        }

        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectKey)
                            .stream(file, size, -1l)
                            .contentType(contentType)
                            .build()
            );

            log.info("Фото загружено в хранилище");

        }catch (MinioException e) {
           throw  new RuntimeException("Ошибка сохранения файла в хранилище");
        }
    }

    private boolean typeImage(String contentType){
        return uploadProperties.getAllowedFileTypes().contains(contentType);
    }
}

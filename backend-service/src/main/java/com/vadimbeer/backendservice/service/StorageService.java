package com.vadimbeer.backendservice.service;

import com.vadimbeer.backendservice.props.UploadProperties;
import io.minio.*;
import io.minio.errors.MinioException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

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

    public long sizeFileInStorage(String objectKey) {
        try{
            StatObjectResponse  statObjectResponse = minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectKey)
                            .build()
            );

            long size = statObjectResponse.size();

            return size;
        } catch (MinioException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public String getReadUrl(String objectKey) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Http.Method.GET)
                            .bucket(bucketName)
                            .object(objectKey)
                            .expiry(15, TimeUnit.MINUTES)
                            .build()
            );
        } catch (MinioException e) {
            throw new IllegalStateException(
                    "Не удалось получить ссылку на файл " + objectKey, e
            );
        }
    }
}

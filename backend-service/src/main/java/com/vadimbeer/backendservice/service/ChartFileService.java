package com.vadimbeer.backendservice.service;

import com.vadimbeer.backendservice.DTO.ChartDtoOnDashboard;
import com.vadimbeer.backendservice.DTO.ChartTaskConsume;
import com.vadimbeer.backendservice.jpa.AnalysisTaskRepository;
import com.vadimbeer.backendservice.jpa.FileRepository;
import com.vadimbeer.backendservice.models.AnalysisTask;
import com.vadimbeer.backendservice.models.FilesStorage;
import com.vadimbeer.backendservice.models.ModelVersion;
import com.vadimbeer.backendservice.models.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChartFileService {

    private final FileRepository fileRepository;
    private final StorageService storageService;
    private final UserService userService;
    private final FileService fileService;
    private final AnalysisTaskRepository analysisTaskRepository;

//    private final String prefixOriginalName = "charts";

    public FilesStorage saveChartFile(ChartTaskConsume chartTaskConsume) {
        long size = 0;

        try{
           size = storageService.sizeFileInStorage(chartTaskConsume.chartObjectKey());
            if(size == 0){
                log.error("File not found");
                throw new FileNotFoundException("File not found");
            }
        }catch (Exception e){
            log.error(e.getMessage());
        }

        User user = userService.findById(chartTaskConsume.userId());

        FilesStorage chart = fileService.saveFileMetadata(
                chartTaskConsume.chartObjectKey(),
                chartTaskConsume.chartObjectKey(),
                chartTaskConsume.contentType(),
                size,
                user
        );

        return fileRepository.save(chart);
    }

    public ChartDtoOnDashboard getChartDtoOnDashboard(Long userId) {
        FilesStorage chart = fileRepository.findFirstByUserIdOrderByCreatedAtDesc(userId)
                .orElseThrow(() ->
                        new RuntimeException("Файл пользователя не найден")
                );

        FilesStorage img = fileRepository.findByObjectKey(
                withoutPrefix(chart.getObjectKey()))
                .orElseThrow(
                        () -> new RuntimeException("File not found")
                );

        AnalysisTask task = analysisTaskRepository.findByFilesStorageId(img.getId());
        ModelVersion modelVersion = task.getModelVersion();

        return new ChartDtoOnDashboard(
                chart.getId(),
                storageService.getReadUrl(chart.getObjectKey()),
                chart.getOriginalFileName(),
                modelVersion.getName(),
                modelVersion.getVersion()
        );
    }

    private String withoutPrefix(String objectKey) {
        return objectKey.substring(objectKey.lastIndexOf("/") + 1);
    }
}

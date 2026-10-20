package com.vadimbeer.backendservice.service;

import com.vadimbeer.backendservice.DTO.AnalysisTaskResponse;
import com.vadimbeer.backendservice.DTO.UploadResponse;
import com.vadimbeer.backendservice.models.AnalysisTask;
import com.vadimbeer.backendservice.models.FilesStorage;
import com.vadimbeer.backendservice.models.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalysisService {

    private final AnalysisTaskService analysisTaskService;
    private final FileStorageService fileStorageService;
    private final FileService fileService;

    public AnalysisTaskResponse submit(User user, Integer modelId, MultipartFile file) {
        UploadResponse response = fileStorageService.saveImage(user, file);
        FilesStorage filesStorage = fileService.findById(response.id());
        AnalysisTask analysisTask = analysisTaskService.create(modelId, filesStorage);

        log.info("Analysis task создан с id {}", analysisTask.getId());
        return new AnalysisTaskResponse(
                analysisTask.getId(),
                filesStorage.getOriginalFileName(),
                analysisTask.getState().toString()
        );
    }
}

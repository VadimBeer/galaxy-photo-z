package com.vadimbeer.backendservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalysisService {

    private final RabbitTemplate rabbitTemplate;
    private final AnalysisTaskService analysisTaskService;
    private final FileUploadService fileUploadService;



}

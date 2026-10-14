package com.vadimbeer.backendservice.messages;

import com.vadimbeer.backendservice.DTO.ChartTaskConsume;
import com.vadimbeer.backendservice.models.AnalysisResult;
import com.vadimbeer.backendservice.models.AnalysisTask;
import com.vadimbeer.backendservice.models.FilesStorage;
import com.vadimbeer.backendservice.props.RabbitProperties;
import com.vadimbeer.backendservice.service.AnalysisResultService;
import com.vadimbeer.backendservice.service.AnalysisTaskService;
import com.vadimbeer.backendservice.service.ChartFileService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class TaskServiceConsumer {

    private final AnalysisTaskService analysisTaskService;
    private final ChartFileService chartFileService;
    private final AnalysisResultService analysisResultService;

    //получение сообщения со стороны Python
    //изменение статуса задачи
    //сохранение графика в БД

    @RabbitListener(queues = "${app.rabbitmq.chart-queue-name}")
    @Transactional
    public void consume(ChartTaskConsume message) {
        log.info("Received a task consume message: {}", message);

        try {
            AnalysisTask task = analysisTaskService.updateTask(message);
            log.info("Updated task: {}", task);

            AnalysisResult result = analysisResultService.save(task, message);
            log.info("Saved analysisResult: {}", result);

            FilesStorage filesStorage = chartFileService.saveChartFile(message);
            log.info("Saved chart file: {}", filesStorage);
            //todo реализовать сохранение данных о квантилях

        }catch (EntityNotFoundException e){
            log.error("Task with id {} not found", message.taskId());
        }
    }
}

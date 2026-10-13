package com.vadimbeer.backendservice.messages;

import com.vadimbeer.backendservice.DTO.ChartTaskConsume;
import com.vadimbeer.backendservice.models.AnalysisTask;
import com.vadimbeer.backendservice.props.RabbitProperties;
import com.vadimbeer.backendservice.service.AnalysisTaskService;
import com.vadimbeer.backendservice.service.ChartFileService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class TaskServiceConsumer {

    private final AnalysisTaskService analysisTaskService;
    private final ChartFileService chartFileService;

    @RabbitListener(queues = "${app.rabbitmq.chart-queue-name}")
    public void consume(ChartTaskConsume message) {
        log.info("Received a task consume message: {}", message);

        try {
            AnalysisTask task = analysisTaskService.updateTask(message);


        }catch (EntityNotFoundException e){
            log.error("Task with id {} not found", message.taskId());
        }


    }
}

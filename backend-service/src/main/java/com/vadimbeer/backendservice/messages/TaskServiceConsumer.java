package com.vadimbeer.backendservice.messages;

import com.vadimbeer.backendservice.DTO.ChartTaskConsume;
import com.vadimbeer.backendservice.models.AnalysisResult;
import com.vadimbeer.backendservice.models.AnalysisTask;
import com.vadimbeer.backendservice.models.FilesStorage;
import com.vadimbeer.backendservice.service.AnalysisResultService;
import com.vadimbeer.backendservice.service.AnalysisTaskService;
import com.vadimbeer.backendservice.service.ChartFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
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
    //сохранение analysis Result сущности
    //сохранение графика в БД

    @RabbitListener(queues = "${app.rabbitmq.chart-queue-name}")
    @Transactional
    public void consume(ChartTaskConsume message) {
        log.info("Получено сообщение по задаче {}", message.taskId());

        AnalysisTask task = analysisTaskService.updateTask(message);

        if (message.state() != AnalysisTask.State.COMPLETED) {
            log.info("Статус задачи {} обновлён на {}",
                    task.getId(),
                    message.state()
            );
            return;
        }

        // Сохраняет AnalysisResult и все пары tau/value.
        AnalysisResult result = analysisResultService.save(task, message);

        FilesStorage chart = chartFileService.saveChartFile(message);

        log.info("Задача {}: сохранены результат {}, график {}, квантилей {}",
                task.getId(),
                result.getId(),
                chart.getId(),
                message.quantiles().size()
        );
    }
}

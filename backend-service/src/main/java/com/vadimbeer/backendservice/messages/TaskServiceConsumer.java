package com.vadimbeer.backendservice.messages;

import com.vadimbeer.backendservice.DTO.ChartTaskConsume;
import com.vadimbeer.backendservice.jpa.AnalysisTaskRepository;
import com.vadimbeer.backendservice.models.AnalysisTask;
import com.vadimbeer.backendservice.service.AnalysisResultService;
import com.vadimbeer.backendservice.service.AnalysisTaskService;
import com.vadimbeer.backendservice.service.ChartFileService;
import jakarta.persistence.EntityNotFoundException;
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
    private final AnalysisTaskRepository taskRepository;

    //получение сообщения со стороны Python
    //изменение статуса задачи
    //сохранение analysis Result сущности
    //сохранение графика в БД

    @RabbitListener(queues = "${app.rabbitmq.chart-queue-name}")
    @Transactional
    public void consume(ChartTaskConsume message) {
        AnalysisTask task = taskRepository
                .findByIdForUpdate(message.taskId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Задача не найдена: " + message.taskId()
                ));

        // Проверяем сохранённое состояние до любых изменений.
        if (task.getState() == AnalysisTask.State.COMPLETED
                || task.getState() == AnalysisTask.State.FAILED) {
            log.info("Повторный результат задачи {} пропущен", task.getId());
            return;
        }

        // Этот обработчик принимает итоговые результаты.
        if (message.state() != AnalysisTask.State.COMPLETED
                && message.state() != AnalysisTask.State.FAILED) {
            throw new IllegalArgumentException(
                    "Ожидался итоговый статус, получен: " + message.state()
            );
        }

        if (message.state() == AnalysisTask.State.COMPLETED) {
            analysisResultService.save(task, message);
            chartFileService.saveChartFile(message);
            task.setError(null);
        } else {
            task.setError(message.errorMessage());
        }

        task.setState(message.state());
        taskRepository.save(task);
    }
}

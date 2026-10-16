package com.vadimbeer.backendservice.messages;

import com.vadimbeer.backendservice.DTO.ImgTaskMessage;
import com.vadimbeer.backendservice.DTO.TaskCreatedEvent;
import com.vadimbeer.backendservice.props.RabbitProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class TaskServicePublisher {

    private final RabbitTemplate rabbitTemplate;
    private final RabbitProperties props;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onTaskCreated(TaskCreatedEvent event) {
        ImgTaskMessage message = new ImgTaskMessage(
                event.taskId(),
                event.userId(),
                event.modelVersionId(),
                event.objectKey()
        );

        log.info("Публикую задачу {} в {} с ключом '{}'",
                event.taskId(), props.getExchangeImg(), props.getImgRoutingKey());

        rabbitTemplate.convertAndSend(
                props.getExchangeImg(),
                props.getImgRoutingKey(),
                message
        );
    }

}

package com.vadimbeer.backendservice.props;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("app.rabbitmq")
public class RabbitProperties {

    private String imgQueueName;
    private String chartQueueName;
    private String exchangeImg;
    private String exchangeChart;
    private String imgRoutingKey;
    private String chartRoutingKey;

}

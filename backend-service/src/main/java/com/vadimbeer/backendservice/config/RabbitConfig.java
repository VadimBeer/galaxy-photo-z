package com.vadimbeer.backendservice.config;

import com.vadimbeer.backendservice.props.RabbitProperties;
import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    private RabbitProperties props;

    @Bean
    public DirectExchange imgExchange() {
        return new DirectExchange(props.getExchangeImg(), true, false);
    }

    @Bean
    public Queue imgQueue() {
        return QueueBuilder
                .durable(props.getImgQueueName())
                .build();
    }

    @Bean
    public Binding imgBinding() {
        return BindingBuilder
                .bind(imgQueue())
                .to(imgExchange())
                .with(props.getImgRoutingKey());
    }

    // --- Python -> Java: графики ---
    @Bean
    public DirectExchange chartExchange() {
        return new DirectExchange(props.getExchangeChart(), true, false);
    }

    @Bean
    public Queue chartQueue() {
        return QueueBuilder
                .durable(props.getChartQueueName())
                .build();
    }

    @Bean
    public Binding chartBinding() {
        return BindingBuilder
                .bind(chartQueue())
                .to(chartExchange())
                .with(props.getChartRoutingKey());
    }

    @Bean
    public JacksonJsonMessageConverter messageConverter() {
        return new JacksonJsonMessageConverter();
    }
}

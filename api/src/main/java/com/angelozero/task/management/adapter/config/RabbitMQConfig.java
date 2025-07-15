package com.angelozero.task.management.adapter.config;

import lombok.Getter;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.topic.exchange.name}")
    private String exchangeName;

    @Value("${rabbitmq.routing_key}")
    private String routingKey;

    @Value("${rabbitmq.queue.name}")
    private String queueName;

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(getExchangeName());
    }

    @Bean
    public Queue customEventQueue() {
        // Creates a DURABLE (true), NON-EXCLUSIVE (false), NOT AUTO-DELETABLE (false) queue
        return new Queue(queueName, true, false, false);
    }

    @Bean
    public Binding binding(Queue myEventQueue, TopicExchange exchange) {
        return BindingBuilder.bind(myEventQueue)
                .to(exchange)
                .with(routingKey);
    }
}

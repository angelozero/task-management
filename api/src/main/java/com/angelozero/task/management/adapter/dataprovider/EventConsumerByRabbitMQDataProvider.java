package com.angelozero.task.management.adapter.dataprovider;

import com.angelozero.task.management.entity.Event;
import com.angelozero.task.management.usecase.exception.EventConsumerException;
import com.angelozero.task.management.usecase.gateway.event.EventConsumerInputBoundaryGateway;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class EventConsumerByRabbitMQDataProvider {

    private static final ObjectMapper objectMapper;
    private EventConsumerInputBoundaryGateway eventConsumerInputBoundaryGateway;

    static {
        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    }

    @RabbitListener(queues = "${rabbitmq.queue.name}")
    public void consumer(String message) {
        log.info("[Consumer] Received message: {}", message);

        try {
            var receivedEvent = objectMapper.readValue(message, Event.class);
            eventConsumerInputBoundaryGateway.execute(receivedEvent);

        } catch (Exception ex) {
            log.error("[Consumer] Error deserializing message: {}", ex.getMessage());
            throw new EventConsumerException("[Consumer] Error deserializing message: " + ex.getMessage());
        }
    }
}

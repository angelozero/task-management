package com.angelozero.task.management.entity.unit.dataprovider;


import com.angelozero.task.management.adapter.config.RabbitMQConfig;
import com.angelozero.task.management.adapter.dataprovider.EventPublisherByRabbitMQDataProvider;
import com.angelozero.task.management.entity.Event;
import com.angelozero.task.management.usecase.exception.EventPublisherException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.AmqpTemplate;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventPublisherByRabbitMQDataProviderTest {

    @Mock
    private RabbitMQConfig rabbitMQConfig;

    @Mock
    private AmqpTemplate rabbitTemplate;

    @InjectMocks
    private EventPublisherByRabbitMQDataProvider eventPublisherByRabbitMQDataProvider;

    @Test
    @DisplayName("Should publish an event with success")
    void shouldPublishEventWithSuccess() {
        var eventMock = getEventMock();

        when(rabbitMQConfig.getExchangeName()).thenReturn("name");
        when(rabbitMQConfig.getRoutingKey()).thenReturn("key");
        doNothing().when(rabbitTemplate).convertAndSend(any(), any(), any(String.class));

        assertDoesNotThrow(() -> eventPublisherByRabbitMQDataProvider.publish(eventMock));
    }

    @Test
    @DisplayName("Should fail to publish an event")
    void shouldFailToPublishEvent() {
        var eventMock = getEventMock();
        var errorMessage = "[Producer] Error sending message: test - fail publish";

        when(rabbitMQConfig.getExchangeName()).thenReturn("name");
        when(rabbitMQConfig.getRoutingKey()).thenReturn("key");
        doThrow(new RuntimeException("test - fail publish")).when(rabbitTemplate).convertAndSend(any(), any(), any(String.class));

        var exception = assertThrows(EventPublisherException.class, () -> eventPublisherByRabbitMQDataProvider.publish(eventMock));

        assertNotNull(exception);
        assertEquals(errorMessage, exception.getMessage());
    }

    private Event getEventMock() {
        return new Event(0,
                "eventType",
                "taskId",
                "personId",
                LocalDateTime.now(),
                false,
                "message");
    }

}

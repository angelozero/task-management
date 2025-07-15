package com.angelozero.task.management.entity.unit.dataprovider;

import com.angelozero.task.management.adapter.dataprovider.EventConsumerByRabbitMQDataProvider;
import com.angelozero.task.management.entity.Event;
import com.angelozero.task.management.usecase.exception.EventConsumerException;
import com.angelozero.task.management.usecase.gateway.event.EventConsumerInputBoundaryGateway;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventConsumerByRabbitMQDataProviderTest {

    @Mock
    private EventConsumerInputBoundaryGateway eventConsumerInputBoundaryGateway;

    @InjectMocks
    private EventConsumerByRabbitMQDataProvider eventConsumerByRabbitMQDataProvider;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    }

    @Test
    @DisplayName("Should consume a message with success")
    void shouldConsumeMessageWithSuccess() throws JsonProcessingException {
        var eventMock = getEventMock();
        var message = objectMapper.writeValueAsString(eventMock);

        doNothing().when(eventConsumerInputBoundaryGateway).execute(any(Event.class));

        assertDoesNotThrow(() -> eventConsumerByRabbitMQDataProvider.consumer(message));
        verify(eventConsumerInputBoundaryGateway, times(1)).execute(any(Event.class));
    }

    @Test
    @DisplayName("Should fail to consume a message due to deserialization error")
    void shouldFailToConsumeMessageDueToDeserializationError() {
        var invalidMessage = "invalid json";
        var errorMessage = "[Consumer] Error deserializing message: Unrecognized token 'invalid': was expecting (JSON String, Number, Array, Object or token 'null', 'true' or 'false')";

        var exception = assertThrows(EventConsumerException.class, () -> eventConsumerByRabbitMQDataProvider.consumer(invalidMessage));

        assertNotNull(exception);
        assertTrue(exception.getMessage().contains(errorMessage));
        verifyNoInteractions(eventConsumerInputBoundaryGateway);
    }

    @Test
    @DisplayName("Should fail to consume a message when consumer input boundary fails")
    void shouldFailToConsumeMessageWhenConsumerInputBoundaryFails() throws JsonProcessingException {
        var eventMock = getEventMock();
        var message = objectMapper.writeValueAsString(eventMock);
        var expectedErrorMessage = "[Consumer] Error deserializing message: Simulate use case failure";

        doThrow(new RuntimeException("Simulate use case failure")).when(eventConsumerInputBoundaryGateway).execute(any(Event.class));

        var exception = assertThrows(EventConsumerException.class, () -> eventConsumerByRabbitMQDataProvider.consumer(message));

        assertNotNull(exception);
        assertEquals(expectedErrorMessage, exception.getMessage());
        verify(eventConsumerInputBoundaryGateway, times(1)).execute(any(Event.class));
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
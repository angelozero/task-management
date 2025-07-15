package com.angelozero.task.management.entity.unit.usecase.event;

import com.angelozero.task.management.entity.Event;
import com.angelozero.task.management.usecase.gateway.event.EventReaderGateway;
import com.angelozero.task.management.usecase.services.event.EventConsumerUseCase;
import com.angelozero.task.management.usecase.services.notification.HandleNotificationUseCase;
import com.angelozero.task.management.usecase.services.notification.factory.NotificationFactory;
import com.angelozero.task.management.usecase.services.notification.factory.NotificationTaskUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventConsumerUseCaseTest {

    @Mock
    private EventReaderGateway eventReaderGateway;

    @Mock
    private HandleNotificationUseCase handleNotificationUseCase;

    @InjectMocks
    private EventConsumerUseCase eventConsumerUseCase;

    @Test
    @DisplayName("Should consume and save an event with success")
    void shouldConsumeAndSaveAnEventWithSuccess() {
        var eventMock = getEventMock();

        when(eventReaderGateway.save(any(Event.class))).thenReturn(eventMock);
        doNothing().when(handleNotificationUseCase).execute(anyString());

        assertDoesNotThrow(() -> eventConsumerUseCase.execute(eventMock));

        verify(eventReaderGateway, times(1)).save(any(Event.class));
    }

    private Event getEventMock() {
        return new Event(0,
                "task-completed",
                "taskId-example",
                "personId-example",
                LocalDateTime.now(),
                false,
                "Task completed successfully");
    }
}
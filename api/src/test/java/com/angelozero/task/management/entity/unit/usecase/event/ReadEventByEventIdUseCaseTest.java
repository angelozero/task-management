package com.angelozero.task.management.entity.unit.usecase.event;

import com.angelozero.task.management.usecase.exception.BusinessException;
import com.angelozero.task.management.usecase.gateway.event.EventReaderGateway;
import com.angelozero.task.management.usecase.services.event.ReadEventByEventIdUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReadEventByEventIdUseCaseTest {

    @Mock
    private EventReaderGateway eventReaderGateway;

    @InjectMocks
    private ReadEventByEventIdUseCase readEventByEventIdUseCase;

    @Test
    @DisplayName("Should set read status of an event by event ID with success")
    public void shouldSetReadStatusByEventIdWithSuccess() {
        doNothing().when(eventReaderGateway).setReadInfoByEventId(anyInt(), anyBoolean());

        readEventByEventIdUseCase.execute(123, true);

        verify(eventReaderGateway, times(1)).setReadInfoByEventId(123, true);
    }

    @Test
    @DisplayName("Should fail if no event ID is informed")
    public void shouldFailIfNoEventIdIsProvided() {
        var exception = assertThrows(BusinessException.class,
                () -> readEventByEventIdUseCase.execute(null, true));

        assertNotNull(exception);
        assertEquals("No event id was informed to change read status", exception.getMessage());
        verify(eventReaderGateway, never()).setReadInfoByEventId(anyInt(), anyBoolean());
    }

    @Test
    @DisplayName("Should call setReadInfoByEventId with false when isRead is false")
    public void shouldCallSetReadInfoByEventIdWithFalse() {
        doNothing().when(eventReaderGateway).setReadInfoByEventId(anyInt(), anyBoolean());

        readEventByEventIdUseCase.execute(456, false);

        verify(eventReaderGateway, times(1)).setReadInfoByEventId(456, false);
    }
}
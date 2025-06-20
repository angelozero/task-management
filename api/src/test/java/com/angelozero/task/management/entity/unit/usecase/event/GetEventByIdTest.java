package com.angelozero.task.management.entity.unit.usecase.event;

import com.angelozero.task.management.entity.Event;
import com.angelozero.task.management.usecase.exception.BusinessException;
import com.angelozero.task.management.usecase.gateway.event.EventReaderGateway;
import com.angelozero.task.management.usecase.services.event.GetEventById;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GetEventByIdTest {

    @Mock
    private EventReaderGateway eventReaderGateway;

    @InjectMocks
    private GetEventById getEventById;

    @Test
    @DisplayName("Should find an event by id with success")
    public void shouldFindEventByIdWithSuccess() {
        var eventMock = new Event(1, "Event Type", "Entity Id", "User Id", LocalDateTime.now(), false, "message");

        when(eventReaderGateway.getById(anyInt())).thenReturn(eventMock);

        var response = getEventById.execute(123);

        assertNotNull(response);
        assertEquals(eventMock, response);
    }

    @Test
    @DisplayName("Should not find an event by id")
    public void shouldNotFindEventById() {
        when(eventReaderGateway.getById(anyInt())).thenReturn(null);

        var response = getEventById.execute(123);

        assertNull(response);
    }

    @Test
    @DisplayName("Should fail to find an event without id")
    public void shouldFailToFindEventWithoutId() {
        var exception = assertThrows(BusinessException.class,
                () -> getEventById.execute(null));

        verify(eventReaderGateway, never()).getById(anyInt());

        assertNotNull(exception);
        assertEquals("No Event ID was informed to be found", exception.getMessage());
    }
}
package com.angelozero.task.management.entity.unit.usecase.event;

import com.angelozero.task.management.entity.Event;
import com.angelozero.task.management.entity.Person;
import com.angelozero.task.management.usecase.exception.BusinessException;
import com.angelozero.task.management.usecase.gateway.PersonGateway;
import com.angelozero.task.management.usecase.gateway.event.EventReaderGateway;
import com.angelozero.task.management.usecase.services.event.GetEventByPersonEmail;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GetEventByPersonEmailTest {

    @Mock
    private EventReaderGateway eventReaderGateway;

    @Mock
    private PersonGateway personGateway;

    @InjectMocks
    private GetEventByPersonEmail getEventByPersonEmail;

    @Test
    @DisplayName("Should find an event by person email with success")
    public void shouldFindEventByPersonEmailWithSuccess() {
        var personMock = new Person("person-id", "name", "test@example.com", "profile", null);
        var eventMock = new Event(1, "eventType", "entityId", "person-id", LocalDateTime.now(), false, "message");

        when(personGateway.findByEmail(anyString())).thenReturn(personMock);
        when(eventReaderGateway.getByPersonId(anyString())).thenReturn(eventMock);

        var response = getEventByPersonEmail.execute("test@example.com");

        assertNotNull(response);
        assertEquals(eventMock, response);
        verify(personGateway, times(1)).findByEmail(anyString());
        verify(eventReaderGateway, times(1)).getByPersonId(anyString());
    }

    @Test
    @DisplayName("Should not find an event if person is not found by email")
    public void shouldNotFindEventIfPersonNotFound() {
        when(personGateway.findByEmail(anyString())).thenReturn(null);

        var response = getEventByPersonEmail.execute("nonexistent@example.com");

        assertNull(response);
        verify(personGateway, times(1)).findByEmail(anyString());
        verify(eventReaderGateway, never()).getByPersonId(anyString());
    }

    @Test
    @DisplayName("Should not find an event if event is not found by person id")
    public void shouldNotFindEventIfEventNotFound() {
        var personMock = new Person("person-id", "name", "test@example.com", "profile", null);

        when(personGateway.findByEmail(anyString())).thenReturn(personMock);
        when(eventReaderGateway.getByPersonId(anyString())).thenReturn(null);

        var response = getEventByPersonEmail.execute("test@example.com");

        assertNull(response);
        verify(personGateway, times(1)).findByEmail(anyString());
        verify(eventReaderGateway, times(1)).getByPersonId(anyString());
    }

    @Test
    @DisplayName("Should fail to find an event without an email")
    public void shouldFailToFindEventWithoutEmail() {
        var exception = assertThrows(BusinessException.class,
                () -> getEventByPersonEmail.execute(null));

        assertNotNull(exception);
        assertEquals("No Person ID was informed to found an Event", exception.getMessage()); // Note: the exception message matches your service class
        verify(personGateway, never()).findByEmail(anyString());
        verify(eventReaderGateway, never()).getByPersonId(anyString());
    }
}
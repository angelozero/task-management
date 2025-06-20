package com.angelozero.task.management.entity.unit.usecase.event;

import com.angelozero.task.management.entity.Person;
import com.angelozero.task.management.usecase.exception.BusinessException;
import com.angelozero.task.management.usecase.gateway.PersonGateway;
import com.angelozero.task.management.usecase.gateway.event.EventReaderGateway;
import com.angelozero.task.management.usecase.services.event.ReadEventByEventPersonEmailUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReadEventByEventPersonEmailUseCaseTest {

    @Mock
    private PersonGateway personGateway;

    @Mock
    private EventReaderGateway eventReaderGateway;

    @InjectMocks
    private ReadEventByEventPersonEmailUseCase readEventByEventPersonEmailUseCase;

    @Test
    @DisplayName("Should set read status of an event by person email with success")
    public void shouldSetReadStatusByPersonEmailWithSuccess() {
        var personMock = new Person("person-id", "name", "test@example.com", "profile", null);

        when(personGateway.findByEmail(anyString())).thenReturn(personMock);
        doNothing().when(eventReaderGateway).setReadInfoByPersonId(anyString(), anyBoolean());

        readEventByEventPersonEmailUseCase.execute("test@example.com", true);

        verify(personGateway, times(1)).findByEmail(anyString());
        verify(eventReaderGateway, times(1)).setReadInfoByPersonId("person-id", true);
    }

    @Test
    @DisplayName("Should fail if no event person email is informed")
    public void shouldFailIfNoEventPersonEmailIsProvided() {
        var exception = assertThrows(BusinessException.class,
                () -> readEventByEventPersonEmailUseCase.execute(null, true));

        assertNotNull(exception);
        assertEquals("No event person email was informed to change read status", exception.getMessage());
        verify(personGateway, never()).findByEmail(anyString());
        verify(eventReaderGateway, never()).setReadInfoByPersonId(anyString(), anyBoolean());
    }

    @Test
    @DisplayName("Should fail if person is not found by email")
    public void shouldFailIfPersonNotFoundByEmail() {
        when(personGateway.findByEmail(anyString())).thenReturn(null);

        var email = "nonexistent@example.com";
        var exception = assertThrows(BusinessException.class,
                () -> readEventByEventPersonEmailUseCase.execute(email, false));

        assertNotNull(exception);
        assertEquals("A person with email " + email + " was not found to set read status", exception.getMessage());
        verify(personGateway, times(1)).findByEmail(anyString());
        verify(eventReaderGateway, never()).setReadInfoByPersonId(anyString(), anyBoolean());
    }

    @Test
    @DisplayName("Should call setReadInfoByPersonId with false when isRead is false")
    public void shouldCallSetReadInfoByPersonIdWithFalse() {
        var personMock = new Person("person-id", "name", "test@example.com", "profile", null);

        when(personGateway.findByEmail(anyString())).thenReturn(personMock);
        doNothing().when(eventReaderGateway).setReadInfoByPersonId(anyString(), anyBoolean());

        readEventByEventPersonEmailUseCase.execute("test@example.com", false);

        verify(personGateway, times(1)).findByEmail(anyString());
        verify(eventReaderGateway, times(1)).setReadInfoByPersonId("person-id", false);
    }
}
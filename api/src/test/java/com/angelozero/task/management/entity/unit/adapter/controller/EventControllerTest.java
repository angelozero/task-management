package com.angelozero.task.management.entity.unit.adapter.controller;

import com.angelozero.task.management.adapter.controller.EventController;
import com.angelozero.task.management.adapter.controller.mapper.EventRequestMapper;
import com.angelozero.task.management.adapter.controller.rest.request.EventRequest;
import com.angelozero.task.management.adapter.controller.rest.response.EventResponse;
import com.angelozero.task.management.entity.Event;
import com.angelozero.task.management.usecase.services.event.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventControllerTest {

    @Mock
    private EventPublisherUseCase eventPublisherUseCase;

    @Mock
    private GetEventById getEventById;

    @Mock
    private GetEventByPersonEmail getEventByPersonEmail;

    @Mock
    private ReadEventByEventPersonEmailUseCase readEventByEventPersonEmailUseCase;

    @Mock
    private ReadEventByEventIdUseCase readEventByEventIdUseCase;

    @Mock
    private EventRequestMapper eventRequestMapper;

    @InjectMocks
    private EventController eventController;

    @Test
    @DisplayName("Should save event with success")
    public void shouldSaveEventWithSuccess() {
        var eventRequest = new EventRequest("1", "test@example.com", "Test Message");

        var response = eventController.saveEvent(eventRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(eventPublisherUseCase).execute(eventRequest.taskId(), eventRequest.email(), eventRequest.message());
    }

    @Test
    @DisplayName("Should get event by id with success")
    public void shouldGetEventByIdWithSuccess() {
        var id = 1;
        var event = new Event(1, "1", "entityId", "userId", LocalDateTime.now(), true, "message");
        var eventResponse = new EventResponse(1, "1", "entityId", "userId", LocalDateTime.now(), true, "message");

        when(getEventById.execute(id)).thenReturn(event);
        when(eventRequestMapper.toEventResponse(event)).thenReturn(eventResponse);

        var response = eventController.getEventById(id);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(eventResponse, response.getBody());
        verify(getEventById).execute(id);
        verify(eventRequestMapper).toEventResponse(event);
    }

    @Test
    @DisplayName("Should get event by person email with success")
    public void shouldGetEventByPersonEmailWithSuccess() {
        var email = "test@example.com";
        var event = new Event(1, "1", "entityId", "userId", LocalDateTime.now(), true, "message");
        var eventResponse = new EventResponse(1, "1", "entityId", "userId", LocalDateTime.now(), true, "message");

        when(getEventByPersonEmail.execute(email)).thenReturn(event);
        when(eventRequestMapper.toEventResponse(event)).thenReturn(eventResponse);

        var response = eventController.getEventByPersonEmail(email);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(eventResponse, response.getBody());
        verify(getEventByPersonEmail).execute(email);
        verify(eventRequestMapper).toEventResponse(event);
    }

    @Test
    @DisplayName("Should update read status by event id with success")
    public void shouldUpdateReadStatusByEventIdWithSuccess() {
        var eventId = 1;
        var isRead = true;

        var response = eventController.updateReadStatus(eventId, null, isRead);

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(readEventByEventIdUseCase).execute(eventId, isRead);
        verifyNoInteractions(readEventByEventPersonEmailUseCase);
    }

    @Test
    @DisplayName("Should update read status by event person email with success")
    public void shouldUpdateReadStatusByEventPersonEmailWithSuccess() {
        var eventPersonEmail = "test@example.com";
        var isRead = true;

        var response = eventController.updateReadStatus(null, eventPersonEmail, isRead);

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(readEventByEventPersonEmailUseCase).execute(eventPersonEmail, isRead);
        verifyNoInteractions(readEventByEventIdUseCase);
    }

    @Test
    @DisplayName("Should return bad request if both event id and event person email are null")
    public void shouldReturnBadRequestIfBothEventIdAndEventPersonEmailAreNull() {
        var isRead = true;

        var response = eventController.updateReadStatus(null, null, isRead);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("the values \"event id\" or \"event id\" should not be null or empty", response.getHeaders().getFirst("error message"));
        verifyNoInteractions(readEventByEventIdUseCase);
        verifyNoInteractions(readEventByEventPersonEmailUseCase);
    }

    @Test
    @DisplayName("Should return bad request if event person email is blank")
    public void shouldReturnBadRequestIfEventPersonEmailIsBlank() {
        var isRead = true;

        var response = eventController.updateReadStatus(null, "", isRead);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("the values \"event id\" or \"event id\" should not be null or empty", response.getHeaders().getFirst("error message"));
        verifyNoInteractions(readEventByEventIdUseCase);
        verifyNoInteractions(readEventByEventPersonEmailUseCase);
    }
}
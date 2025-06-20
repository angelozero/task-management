package com.angelozero.task.management.entity.unit.dataprovider;

import com.angelozero.task.management.adapter.dataprovider.EventReaderByPostgresDataProvider;
import com.angelozero.task.management.adapter.dataprovider.jpa.entity.EventEntity;
import com.angelozero.task.management.adapter.dataprovider.jpa.repository.postgres.reader.EventReaderDataBaseRepository;
import com.angelozero.task.management.adapter.dataprovider.mapper.EventDataProviderMapper;
import com.angelozero.task.management.entity.Event;
import com.angelozero.task.management.usecase.exception.DataBaseDataProviderException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventReaderByPostgresDataProviderTest {

    @Mock
    private EventReaderDataBaseRepository eventReaderDataBaseRepository;

    @Mock
    private EventDataProviderMapper eventDataProviderMapper;

    @InjectMocks
    private EventReaderByPostgresDataProvider eventReaderByPostgresDataProvider;

    @Test
    @DisplayName("Should get an event by id with success")
    void shouldGetEventByIdWithSuccess() {
        var eventId = 1;
        var eventMock = getEventMock();
        var eventEntityMock = getEventEntityMock();

        when(eventReaderDataBaseRepository.findById(any(Integer.class))).thenReturn(Optional.of(eventEntityMock));
        when(eventDataProviderMapper.toEvent(any())).thenReturn(eventMock);

        var response = eventReaderByPostgresDataProvider.getById(eventId);

        assertNotNull(response);
        assertEquals(eventMock, response);
        verify(eventReaderDataBaseRepository, times(1)).findById(eventId);
        verify(eventDataProviderMapper, times(1)).toEvent(eventEntityMock);
    }

    @Test
    @DisplayName("Should return null when event not found by id")
    void shouldReturnNullWhenEventNotFoundById() {
        var eventId = 1;

        when(eventReaderDataBaseRepository.findById(any(Integer.class))).thenReturn(Optional.empty());
        when(eventDataProviderMapper.toEvent(any())).thenReturn(null);

        var response = eventReaderByPostgresDataProvider.getById(eventId);

        assertNull(response);
        verify(eventReaderDataBaseRepository, times(1)).findById(eventId);
    }

    @Test
    @DisplayName("Should fail to get an event by id")
    void shouldFailToGetEventById() {
        var eventId = 1;
        var errorMessage = "Fail to get an Event into the reader database by id - Fail: test - get by id failure";

        when(eventReaderDataBaseRepository.findById(any(Integer.class))).thenThrow(new RuntimeException("test - get by id failure"));

        var exception = assertThrows(DataBaseDataProviderException.class, () -> eventReaderByPostgresDataProvider.getById(eventId));

        assertNotNull(exception);
        assertEquals(errorMessage, exception.getMessage());
        verify(eventDataProviderMapper, never()).toEvent(any());
    }

    /**
     *
     */
    @Test
    @DisplayName("Should get an event by person id with success")
    void shouldGetEventByPersonIdWithSuccess() {
        var personId = "1";
        var eventMock = getEventMock();
        var eventEntityMock = getEventEntityMock();

        when(eventReaderDataBaseRepository.findByUserId(any(String.class))).thenReturn(Optional.of(eventEntityMock));
        when(eventDataProviderMapper.toEvent(any())).thenReturn(eventMock);

        var response = eventReaderByPostgresDataProvider.getByPersonId(personId);

        assertNotNull(response);
        assertEquals(eventMock, response);
        verify(eventReaderDataBaseRepository, times(1)).findByUserId(personId);
        verify(eventDataProviderMapper, times(1)).toEvent(eventEntityMock);
    }

    @Test
    @DisplayName("Should return null when event not found by person id")
    void shouldReturnNullWhenEventNotFoundByPersonId() {
        var personId = "1";
        var eventEntityMock = getEventEntityMock();

        when(eventReaderDataBaseRepository.findByUserId(any(String.class))).thenReturn(Optional.empty());
        when(eventDataProviderMapper.toEvent(any())).thenReturn(null);

        var response = eventReaderByPostgresDataProvider.getByPersonId(personId);

        assertNull(response);
        verify(eventReaderDataBaseRepository, times(1)).findByUserId(personId);
    }

    @Test
    @DisplayName("Should fail to get an event by person id")
    void shouldFailToGetEventByPersonId() {
        var personId = "1";
        var errorMessage = "Fail to get an Event into the reader database by Person id - Fail: test - get by person id failure";

        when(eventReaderDataBaseRepository.findByUserId(any(String.class))).thenThrow(new RuntimeException("test - get by person id failure"));

        var exception = assertThrows(DataBaseDataProviderException.class, () -> eventReaderByPostgresDataProvider.getByPersonId(personId));

        assertNotNull(exception);
        assertEquals(errorMessage, exception.getMessage());
        verify(eventDataProviderMapper, never()).toEvent(any());
    }

    /**
     *
     */

    @Test
    @DisplayName("Should save an event with success")
    void shouldSaveEventWithSuccess() {
        var eventMock = getEventMock();
        var eventEntityMock = getEventEntityMock();

        when(eventDataProviderMapper.toEventEntity(any())).thenReturn(eventEntityMock);
        when(eventReaderDataBaseRepository.save(any())).thenReturn(eventEntityMock);
        when(eventDataProviderMapper.toEvent(any())).thenReturn(eventMock);

        var response = eventReaderByPostgresDataProvider.save(eventMock);

        assertNotNull(response);
        assertEquals(eventMock, response);
        verify(eventDataProviderMapper, times(1)).toEventEntity(eventMock);
        verify(eventReaderDataBaseRepository, times(1)).save(eventEntityMock);
        verify(eventDataProviderMapper, times(1)).toEvent(eventEntityMock);
    }

    @Test
    @DisplayName("Should fail to save an event")
    void shouldFailToSaveEvent() {
        var eventMock = getEventMock();
        var eventEntityMock = getEventEntityMock();
        var errorMessage = "Fail to save an Event into the reader database - Fail: test - save failure";

        when(eventDataProviderMapper.toEventEntity(any())).thenReturn(eventEntityMock);
        when(eventReaderDataBaseRepository.save(any())).thenThrow(new RuntimeException("test - save failure"));

        var exception = assertThrows(DataBaseDataProviderException.class, () -> eventReaderByPostgresDataProvider.save(eventMock));

        assertNotNull(exception);
        assertEquals(errorMessage, exception.getMessage());

        verify(eventDataProviderMapper, never()).toEvent(any());
    }

    @Test
    @DisplayName("Should set read info by event ID with success")
    void shouldSetReadInfoByEventIdWithSuccess() {
        var eventId = 1;
        var isRead = true;
        var eventMock = getEventMock();
        var eventEntityMock = getEventEntityMock();

        when(eventReaderDataBaseRepository.findById(eventId)).thenReturn(Optional.of(eventEntityMock));
        when(eventDataProviderMapper.toEvent(eventEntityMock)).thenReturn(eventMock);
        when(eventDataProviderMapper.toEventEntity(any(Event.class))).thenReturn(eventEntityMock);
        when(eventReaderDataBaseRepository.save(any(EventEntity.class))).thenReturn(eventEntityMock);
        when(eventDataProviderMapper.toEvent(eventEntityMock)).thenReturn(eventMock);


        eventReaderByPostgresDataProvider.setReadInfoByEventId(eventId, isRead);

    }

    @Test
    @DisplayName("Should fail to set read info by event ID if getById fails")
    void shouldFailSetReadInfoByEventIdWhenGetByIdFails() {
        var eventId = 1;
        var isRead = true;
        var errorMessage = "Fail to update Event read status by event id into the reader database - Fail: Fail to get an Event into the reader database by id - Fail: test - get by id failure";

        when(eventReaderDataBaseRepository.findById(eventId)).thenThrow(new RuntimeException("test - get by id failure"));

        var exception = assertThrows(DataBaseDataProviderException.class,
                () -> eventReaderByPostgresDataProvider.setReadInfoByEventId(eventId, isRead));

        assertNotNull(exception);
        assertEquals(errorMessage, exception.getMessage());
        verify(eventReaderDataBaseRepository, times(1)).findById(eventId);
        verify(eventDataProviderMapper, never()).toEvent(any(EventEntity.class));
        verify(eventDataProviderMapper, never()).toEventEntity(any(Event.class));
        verify(eventReaderDataBaseRepository, never()).save(any(EventEntity.class));
    }

    @Test
    @DisplayName("Should fail to set read info by event ID if save fails")
    void shouldFailSetReadInfoByEventIdWhenSaveFails() {
        var eventId = 1;
        var isRead = true;
        var eventMock = getEventMock();
        var eventEntityMock = getEventEntityMock();
        var errorMessage = "Fail to update Event read status by event id into the reader database - Fail: Fail to save an Event into the reader database - Fail: test - save failure";


        when(eventReaderDataBaseRepository.findById(eventId)).thenReturn(Optional.of(eventEntityMock));
        when(eventDataProviderMapper.toEvent(eventEntityMock)).thenReturn(eventMock);
        when(eventDataProviderMapper.toEventEntity(any(Event.class))).thenReturn(eventEntityMock);
        when(eventReaderDataBaseRepository.save(any(EventEntity.class))).thenThrow(new RuntimeException("test - save failure"));


        var exception = assertThrows(DataBaseDataProviderException.class,
                () -> eventReaderByPostgresDataProvider.setReadInfoByEventId(eventId, isRead));

        assertNotNull(exception);
        assertEquals(errorMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Should set read info by person ID with success")
    void shouldSetReadInfoByPersonIdWithSuccess() {
        var personId = "person-id";
        var isRead = true;
        var eventMock = getEventMock();
        var eventEntityMock = getEventEntityMock();

        when(eventReaderDataBaseRepository.findByUserId(personId)).thenReturn(Optional.of(eventEntityMock));
        when(eventDataProviderMapper.toEvent(eventEntityMock)).thenReturn(eventMock);
        when(eventDataProviderMapper.toEventEntity(any(Event.class))).thenReturn(eventEntityMock);
        when(eventReaderDataBaseRepository.save(any(EventEntity.class))).thenReturn(eventEntityMock);
        when(eventDataProviderMapper.toEvent(eventEntityMock)).thenReturn(eventMock);

        eventReaderByPostgresDataProvider.setReadInfoByPersonId(personId, isRead);

    }

    @Test
    @DisplayName("Should fail to set read info by person ID if getByPersonId fails")
    void shouldFailSetReadInfoByPersonIdWhenGetByPersonIdFails() {
        var personId = "person-id";
        var isRead = true;
        var errorMessage = "Fail to update Event read status by event person id into the reader database - Fail: Fail to get an Event into the reader database by Person id - Fail: test - get by person id failure";

        when(eventReaderDataBaseRepository.findByUserId(personId)).thenThrow(new RuntimeException("test - get by person id failure"));

        var exception = assertThrows(DataBaseDataProviderException.class,
                () -> eventReaderByPostgresDataProvider.setReadInfoByPersonId(personId, isRead));

        assertNotNull(exception);
        assertEquals(errorMessage, exception.getMessage());
        verify(eventReaderDataBaseRepository, times(1)).findByUserId(personId);
        verify(eventDataProviderMapper, never()).toEvent(any(EventEntity.class));
        verify(eventDataProviderMapper, never()).toEventEntity(any(Event.class));
        verify(eventReaderDataBaseRepository, never()).save(any(EventEntity.class));
    }

    @Test
    @DisplayName("Should fail to set read info by person ID if save fails")
    void shouldFailSetReadInfoByPersonIdWhenSaveFails() {
        var personId = "person-id";
        var isRead = true;
        var eventMock = getEventMock();
        var eventEntityMock = getEventEntityMock();
        var errorMessage = "Fail to update Event read status by event person id into the reader database - Fail: Fail to save an Event into the reader database - Fail: test - save failure";

        when(eventReaderDataBaseRepository.findByUserId(personId)).thenReturn(Optional.of(eventEntityMock));
        when(eventDataProviderMapper.toEvent(eventEntityMock)).thenReturn(eventMock);
        when(eventDataProviderMapper.toEventEntity(any(Event.class))).thenReturn(eventEntityMock);
        when(eventReaderDataBaseRepository.save(any(EventEntity.class))).thenThrow(new RuntimeException("test - save failure"));

        var exception = assertThrows(DataBaseDataProviderException.class,
                () -> eventReaderByPostgresDataProvider.setReadInfoByPersonId(personId, isRead));

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

    private EventEntity getEventEntityMock() {
        return new EventEntity(0,
                "eventType",
                "taskId",
                "personId",
                LocalDateTime.now(),
                false,
                "message");
    }
}
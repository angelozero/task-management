package com.angelozero.task.management.entity.unit.dataprovider;

import com.angelozero.task.management.adapter.dataprovider.EventWriterByPostgresDataProvider;
import com.angelozero.task.management.adapter.dataprovider.jpa.entity.EventEntity;
import com.angelozero.task.management.adapter.dataprovider.jpa.repository.postgres.reader.EventReaderDataBaseRepository;
import com.angelozero.task.management.adapter.dataprovider.jpa.repository.postgres.writer.EventWriterDataBaseRepository;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventWriterByPostgresDataProviderTest {

    @Mock
    private EventWriterDataBaseRepository eventWriterDataBaseRepository;

    @Mock
    private EventDataProviderMapper eventDataProviderMapper;

    @InjectMocks
    private EventWriterByPostgresDataProvider eventByPostgresDataProvider;

    @Test
    @DisplayName("Should save an event with success")
    void shouldSaveEventWithSuccess() {
        var eventMock = getEventMock();
        var eventEntityMock = getEventEntityMock();

        when(eventDataProviderMapper.toEventEntity(any())).thenReturn(eventEntityMock);
        when(eventWriterDataBaseRepository.save(any())).thenReturn(eventEntityMock);
        when(eventDataProviderMapper.toEvent(any())).thenReturn(eventMock);

        var response = eventByPostgresDataProvider.save(eventMock);

        assertNotNull(response);
    }

    @Test
    @DisplayName("Should fail to save an event")
    void shouldFailToSaveEvent() {
        var eventMock = getEventMock();
        var eventEntityMock = getEventEntityMock();
        var errorMessage = "Fail to save an Event into the database - fail: test - fail to save event";

        when(eventDataProviderMapper.toEventEntity(any())).thenReturn(eventEntityMock);
        when(eventWriterDataBaseRepository.save(any())).thenThrow(new RuntimeException("test - fail to save event"));

        var exception = assertThrows(DataBaseDataProviderException.class, () -> eventByPostgresDataProvider.save(eventMock));

        assertNotNull(exception);
        assertEquals(errorMessage, exception.getMessage());

        verify(eventDataProviderMapper, never()).toEvent(any());
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

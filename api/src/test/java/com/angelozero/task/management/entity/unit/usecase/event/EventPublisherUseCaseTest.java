package com.angelozero.task.management.entity.unit.usecase.event;

import com.angelozero.task.management.entity.Event;
import com.angelozero.task.management.entity.Person;
import com.angelozero.task.management.entity.Task;
import com.angelozero.task.management.entity.status.Completed;
import com.angelozero.task.management.usecase.exception.BusinessException;
import com.angelozero.task.management.usecase.gateway.event.EventWriterGateway;
import com.angelozero.task.management.usecase.gateway.PersonGateway;
import com.angelozero.task.management.usecase.gateway.event.EventPublishGateway;
import com.angelozero.task.management.usecase.gateway.TaskGateway;
import com.angelozero.task.management.usecase.services.event.EventPublisherUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventPublisherUseCaseTest {

    @Mock
    private EventPublishGateway eventPublishGateway;

    @Mock
    private EventWriterGateway eventWriterGateway;

    @Mock
    private PersonGateway personGateway;

    @Mock
    private TaskGateway taskGateway;

    @InjectMocks
    private EventPublisherUseCase eventPublisherUseCase;

    @Test
    @DisplayName("Should publish an event with success")
    void shouldPublishAnEventWithSuccess() {
        var personMock = getPersonMock();
        var taskMock = getTaskMock();
        var eventMock = getEventMock();

        when(personGateway.findByEmail(any())).thenReturn(personMock);
        when(taskGateway.findById(any())).thenReturn(taskMock);
        when(eventWriterGateway.save(any())).thenReturn(eventMock);
        doNothing().when(eventPublishGateway).publish(any());

        assertDoesNotThrow(() -> eventPublisherUseCase.execute("taskId", "email",  "message"));
    }

    @Test
    @DisplayName("Should fail to publish an event with no person")
    void shouldFailToPublishAnEventWithNoPerson() {
        var errorMessage = "A person with email email-test was not found to publish an event";

        when(personGateway.findByEmail(any())).thenReturn(null);

        var exception = assertThrows(BusinessException.class, () -> eventPublisherUseCase.execute("taskId", "email-test",  "message"));

        assertNotNull(exception);
        assertEquals(errorMessage, exception.getMessage());

        verify(taskGateway, never()).findById(any());
        verify(eventWriterGateway, never()).save(any());
        verify(eventPublishGateway, never()).publish(any());
    }

    @Test
    @DisplayName("Should fail to publish an event with no task")
    void shouldFailToPublishAnEventWithNoTask() {
        var errorMessage = "A task with id taskId was not found to publish an event";

        when(personGateway.findByEmail(any())).thenReturn(getPersonMock());
        when(taskGateway.findById(any())).thenReturn(null);

        var exception = assertThrows(BusinessException.class, () -> eventPublisherUseCase.execute("taskId", "email-test",  "message"));

        assertNotNull(exception);
        assertEquals(errorMessage, exception.getMessage());

        verify(eventWriterGateway, never()).save(any());
        verify(eventPublishGateway, never()).publish(any());
    }

    private Event getEventMock() {
        return new Event(0,
                
                "taskId",
                "personId",
                "user-id",
                LocalDateTime.now(),
                false,
                "message");
    }

    private Person getPersonMock() {
        return new Person("1",
                "name",
                "email",
                "profile-info",
                List.of(getTaskMock()));
    }

    private Task getTaskMock() {
        return new Task("1", "description", true, new Completed());
    }

}

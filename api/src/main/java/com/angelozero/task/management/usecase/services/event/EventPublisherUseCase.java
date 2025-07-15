package com.angelozero.task.management.usecase.services.event;

import com.angelozero.task.management.entity.Event;
import com.angelozero.task.management.entity.status.EventStatusType;
import com.angelozero.task.management.usecase.exception.BusinessException;
import com.angelozero.task.management.usecase.gateway.event.EventWriterGateway;
import com.angelozero.task.management.usecase.gateway.PersonGateway;
import com.angelozero.task.management.usecase.gateway.event.EventPublishGateway;
import com.angelozero.task.management.usecase.gateway.TaskGateway;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@AllArgsConstructor
public class EventPublisherUseCase {

    private final EventPublishGateway eventPublishGateway;
    private final EventWriterGateway eventWriterGateway;
    private final PersonGateway personGateway;
    private final TaskGateway taskGateway;

    public void execute(String taskId, String email, String message) {
        log.info("Publishing an Event with message - : {}", message);

        var person = personGateway.findByEmail(email);
        if (person == null) {
            log.error("A person with email {} was not found to publish an event", email);
            throw new BusinessException("A person with email " + email + " was not found to publish an event");
        }

        var task = taskGateway.findById(taskId);
        if (task == null) {
            log.error("A task with id {} was not found to publish an event", taskId);
            throw new BusinessException("A task with id " + taskId + " was not found to publish an event");
        }

        var eventType = task.status().getType().getDescription();

        var event = new Event(null,
                eventType,
                task.id(),
                person.id(),
                LocalDateTime.now(),
                false,
                message);

        var eventSaved = eventWriterGateway.save(event);
        log.info("Event {} saved with success - writer", eventSaved.id());

        eventPublishGateway.publish(eventSaved);
        log.info("Event {} published with success", eventSaved.id());
    }
}

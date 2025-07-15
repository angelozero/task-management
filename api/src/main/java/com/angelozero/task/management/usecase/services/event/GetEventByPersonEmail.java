package com.angelozero.task.management.usecase.services.event;

import com.angelozero.task.management.entity.Event;
import com.angelozero.task.management.entity.Person;
import com.angelozero.task.management.usecase.exception.BusinessException;
import com.angelozero.task.management.usecase.gateway.PersonGateway;
import com.angelozero.task.management.usecase.gateway.event.EventReaderGateway;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class GetEventByPersonEmail {

    private final EventReaderGateway eventReaderGateway;
    private final PersonGateway personGateway;

    public Event execute(String email) {
        if (email == null) {
            log.error("No Person email was informed to found an Event");
            throw new BusinessException("No Person ID was informed to found an Event");
        }

        log.info("Find a Person by email {}", email);
        var person = personGateway.findByEmail(email);

        if (person == null) {
            log.info("Person was not found with the email: {} to find a Event", email);
            return null;
        }

        log.info("Getting an Event by Person email - : {}", email);
        var event = eventReaderGateway.getByPersonId(person.id());

        if (event == null) {
            log.info("Event was not found with the Person email: {}", email);
            return null;
        }

        log.info("Event was found by Person email: {} with success", email);
        return event;
    }
}

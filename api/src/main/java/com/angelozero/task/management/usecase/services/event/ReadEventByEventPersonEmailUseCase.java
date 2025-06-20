package com.angelozero.task.management.usecase.services.event;

import com.angelozero.task.management.usecase.exception.BusinessException;
import com.angelozero.task.management.usecase.gateway.PersonGateway;
import com.angelozero.task.management.usecase.gateway.event.EventReaderGateway;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class ReadEventByEventPersonEmailUseCase {

    private final PersonGateway personGateway;
    private final EventReaderGateway eventReaderGateway;

    public void execute(String eventPersonEmail, boolean isRead) {
        if (eventPersonEmail == null) {
            log.error("No event person email was informed to change read status");
            throw new BusinessException("No event person email was informed to change read status");
        }

        var person = personGateway.findByEmail(eventPersonEmail);
        if (person == null) {
            log.error("A person with email {} was not found to set read status", eventPersonEmail);
            throw new BusinessException("A person with email " + eventPersonEmail + " was not found to set read status");
        }

        log.info("Read status set to {} by event person id: {}", isRead, person.id());

        eventReaderGateway.setReadInfoByPersonId(person.id(), isRead);

        log.info("Read status changed with success - event person email");
    }
}

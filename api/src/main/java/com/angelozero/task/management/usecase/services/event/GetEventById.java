package com.angelozero.task.management.usecase.services.event;

import com.angelozero.task.management.entity.Event;
import com.angelozero.task.management.usecase.exception.BusinessException;
import com.angelozero.task.management.usecase.gateway.event.EventReaderGateway;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class GetEventById {

    private final EventReaderGateway eventReaderGateway;

    public Event execute(Integer id) {
        if (id == null) {
            log.error("No Event ID was informed to be found");
            throw new BusinessException("No Event ID was informed to be found");
        }

        log.info("Getting an Event by id - : {}", id);
        var event = eventReaderGateway.getById(id);

        if (event == null) {
            log.info("Event was not found with the ID: {}", id);
            return null;
        }

        log.info("Event was found by ID: {} with success", id);
        return event;
    }
}

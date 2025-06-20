package com.angelozero.task.management.usecase.services.event;

import com.angelozero.task.management.usecase.exception.BusinessException;
import com.angelozero.task.management.usecase.gateway.event.EventReaderGateway;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class ReadEventByEventIdUseCase {

    private final EventReaderGateway eventReaderGateway;

    public void execute(Integer eventId, boolean isRead) {
        if (eventId == null) {
            log.error("No event id was informed to change read status");
            throw new BusinessException("No event id was informed to change read status");
        }

        log.info("Read status set to {} by event id: {}", isRead, eventId);

        eventReaderGateway.setReadInfoByEventId(eventId, isRead);

        log.info("Read status changed with success - event id");
    }
}

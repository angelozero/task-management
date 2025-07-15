package com.angelozero.task.management.usecase.gateway.event;

import com.angelozero.task.management.entity.Event;

public interface EventReaderGateway {

    Event getById(Integer id);

    Event getByPersonId(String id);

    Event save(Event event);

    void setReadInfoByEventId(Integer eventId, boolean isRead);

    void setReadInfoByPersonId(String personId, boolean isRead);
}

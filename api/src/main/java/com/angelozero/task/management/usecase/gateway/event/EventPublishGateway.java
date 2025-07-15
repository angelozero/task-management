package com.angelozero.task.management.usecase.gateway.event;

import com.angelozero.task.management.entity.Event;

public interface EventPublishGateway {

    void publish(Event event);
}

package com.angelozero.task.management.usecase.gateway.event;

import com.angelozero.task.management.entity.Event;

public interface EventConsumerInputBoundaryGateway {

    void execute(Event event);
}

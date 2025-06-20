package com.angelozero.task.management.usecase.services.event;

import com.angelozero.task.management.entity.Event;
import com.angelozero.task.management.usecase.gateway.event.EventConsumerInputBoundaryGateway;
import com.angelozero.task.management.usecase.gateway.event.EventReaderGateway;
import com.angelozero.task.management.usecase.services.notification.NotificationFactory;
import com.angelozero.task.management.usecase.services.notification.NotificationTaskType;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class EventConsumerUseCase implements EventConsumerInputBoundaryGateway {

    private final EventReaderGateway eventReaderGateway;
    private final NotificationFactory notificationFactory;

    @Override
    public void execute(Event event) {
        log.info("Receiving an Event - : {}", event);

        var eventToSave = new Event(null,
                event.eventType(),
                event.entityId(),
                event.userId(),
                event.localDateTime(),
                event.read(),
                event.message());

        var eventSaved = eventReaderGateway.save(eventToSave);
        log.info("Event {} saved with success - reader", eventSaved.id());

        notificationFactory.createNotification(NotificationTaskType.SMS).execute(event.message());
        notificationFactory.createNotification(NotificationTaskType.LOG).execute(event.message());
        notificationFactory.createNotification(NotificationTaskType.EMAIL).execute(event.message());
        log.info("Notifications sent with success");
    }
}

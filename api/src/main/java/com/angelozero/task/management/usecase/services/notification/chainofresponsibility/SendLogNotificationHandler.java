package com.angelozero.task.management.usecase.services.notification.chainofresponsibility;

import com.angelozero.task.management.entity.Notification;
import com.angelozero.task.management.usecase.services.notification.factory.NotificationTaskType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SendLogNotificationHandler extends AbstractNotificationHandler {
    @Override
    protected Notification processNotification(Notification notification) {

        // default handler "fallback"
        if (notification.type() == NotificationTaskType.LOG || !notification.handled()) {
            log.info("Notification by LOG with message '{}' handled with success", notification.message());
            return notification.hasHandled(true);
        }
        return notification;
    }
}
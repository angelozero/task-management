package com.angelozero.task.management.usecase.services.notification.chainofresponsibility;

import com.angelozero.task.management.entity.Notification;
import com.angelozero.task.management.usecase.services.notification.factory.NotificationTaskType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SendSmsNotificationHandler extends AbstractNotificationHandler {
    @Override
    protected Notification processNotification(Notification notification) {
        if (notification.type() == NotificationTaskType.SMS) {
            log.info("Notification by SMS with message '{}' handled with success", notification.message());
            return notification.hasHandled(true);
        }
        log.info("The notification handled is not of SMS type");
        return notification;
    }
}
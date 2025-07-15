package com.angelozero.task.management.usecase.services.notification.chainofresponsibility;

import com.angelozero.task.management.entity.Notification;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractNotificationHandler implements NotificationHandler {

    protected NotificationHandler nextHandler;

    @Override
    public void setNextHandler(NotificationHandler nextHandler) {
        this.nextHandler = nextHandler;
    }

    protected abstract Notification processNotification(Notification notification);

    @Override
    public Notification handleNotification(Notification notification) {
        Notification processedNotification = processNotification(notification);

        log.info("Handling with the notification - Type: \"{}\", Message: \"{}\"", notification.type(), notification.message());

        if (processedNotification.handled()) {
            return processedNotification;

        } else if (nextHandler != null) {
            return nextHandler.handleNotification(processedNotification);
        }
        return processedNotification;
    }
}

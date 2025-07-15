package com.angelozero.task.management.usecase.services.notification.chainofresponsibility;

import com.angelozero.task.management.entity.Notification;

public interface NotificationHandler {
    void setNextHandler(NotificationHandler nextHandler);

    Notification handleNotification(Notification notification);
}

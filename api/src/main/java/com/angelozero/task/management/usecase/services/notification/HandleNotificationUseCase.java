package com.angelozero.task.management.usecase.services.notification;

import com.angelozero.task.management.entity.Notification;
import com.angelozero.task.management.usecase.services.notification.chainofresponsibility.NotificationHandler;
import com.angelozero.task.management.usecase.services.notification.factory.NotificationFactory;
import com.angelozero.task.management.usecase.services.notification.factory.NotificationTaskType;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class HandleNotificationUseCase {

    private final NotificationFactory notificationFactory;
    private final NotificationHandler notificationHandler;

    public void execute(String notificationMessage) {
        notificationFactory.createNotification(NotificationTaskType.SMS).execute(notificationMessage);
        notificationFactory.createNotification(NotificationTaskType.LOG).execute(notificationMessage);
        notificationFactory.createNotification(NotificationTaskType.EMAIL).execute(notificationMessage);
        log.info("Notifications sent with success");

        var smsNotification = mockNotification(notificationMessage, NotificationTaskType.SMS);
        var emailNotification = mockNotification(notificationMessage, NotificationTaskType.EMAIL);
        var logNotification = mockNotification(notificationMessage, NotificationTaskType.LOG);

        notificationHandler.handleNotification(smsNotification);
        notificationHandler.handleNotification(emailNotification);
        notificationHandler.handleNotification(logNotification);
        log.info("Notifications handled with success");
    }

    private Notification mockNotification(String message, NotificationTaskType type) {
        return new Notification(message, type, false);
    }
}

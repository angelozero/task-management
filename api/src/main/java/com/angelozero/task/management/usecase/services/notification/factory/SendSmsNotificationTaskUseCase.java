package com.angelozero.task.management.usecase.services.notification.factory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SendSmsNotificationTaskUseCase implements NotificationTaskUseCase {
    @Override
    public void execute(String message) {
        log.info("Notification by SMS with message {} sent with success", message);
    }

    @Override
    public NotificationTaskType getType() {
        return NotificationTaskType.SMS;
    }
}

package com.angelozero.task.management.usecase.services.notification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
class SendLogNotificationTaskUseCase implements NotificationTaskUseCase {
    @Override
    public void execute(String message) {
        log.info("Notification by LOG with message {} sent with success", message);
    }

    @Override
    public NotificationTaskType getType() {
        return NotificationTaskType.LOG;
    }
}

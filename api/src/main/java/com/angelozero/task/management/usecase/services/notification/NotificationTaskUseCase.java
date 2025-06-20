package com.angelozero.task.management.usecase.services.notification;

public interface NotificationTaskUseCase {
    void execute(String message);
    NotificationTaskType getType();
}

package com.angelozero.task.management.usecase.services.notification.factory;

public interface NotificationTaskUseCase {
    void execute(String message);
    NotificationTaskType getType();
}

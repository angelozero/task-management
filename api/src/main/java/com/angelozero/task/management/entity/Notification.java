package com.angelozero.task.management.entity;

import com.angelozero.task.management.usecase.services.notification.factory.NotificationTaskType;

public record Notification(String message, NotificationTaskType type, Boolean handled) {

    public Notification hasHandled(Boolean handled) {
        return new Notification(this.message, this.type, handled);
    }
}

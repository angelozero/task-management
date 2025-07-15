package com.angelozero.task.management.entity.status;

sealed public interface EventStatusTask permits Blocked, Completed, CustomEventStatusTask, InProgress, Pending {
    EventStatusType getType();
    String getName();
    int getCode();
}
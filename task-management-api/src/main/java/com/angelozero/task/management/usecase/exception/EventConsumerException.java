package com.angelozero.task.management.usecase.exception;

public class EventConsumerException extends RuntimeException {
    public EventConsumerException(String message) {
        super(message);
    }
}

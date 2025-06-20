package com.angelozero.task.management.adapter.controller.rest.request;

public record EventRequest(String taskId,
                           String email,
                           String message) {
}

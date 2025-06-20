package com.angelozero.task.management.adapter.controller.rest.response;

public record TaskResponse(String id,
                           String description,
                           Boolean completed,
                           String statusDescription,
                           int statusCode) {
}

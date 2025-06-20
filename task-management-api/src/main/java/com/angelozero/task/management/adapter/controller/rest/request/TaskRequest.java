package com.angelozero.task.management.adapter.controller.rest.request;

public record TaskRequest(String description,
                          Boolean completed,
                          int statusCode) {
}

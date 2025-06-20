package com.angelozero.task.management.adapter.controller.datatransfer;

public record TaskInput(String id,
                        String description,
                        Boolean completed,
                        int statusCode) {
}

package com.angelozero.task.management.adapter.controller.datatransfer;

public record TaskOutput(String id,
                         String description,
                         Boolean completed,
                         String statusDescription,
                         int statusCode) {
}

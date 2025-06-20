package com.angelozero.task.management.adapter.controller.rest.response;

import java.time.LocalDateTime;


public record EventResponse(Integer id,
                            String eventType,
                            String entityId,
                            String userId,
                            LocalDateTime localDateTime,
                            Boolean read,
                            String message) {
}

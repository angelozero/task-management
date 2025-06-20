package com.angelozero.task.management.usecase.services.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct; // Import correto para @PostConstruct no Spring Boot 3+

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class NotificationFactory {

    private final List<NotificationTaskUseCase> notificationTasks; // Spring injeta todas as implementações
    private Map<NotificationTaskType, NotificationTaskUseCase> notificationMap;


    // @PostConstruct is called after all dependencies were injected
    @PostConstruct
    public void init() {
        notificationMap = notificationTasks.stream()
                .collect(Collectors.toMap(NotificationTaskUseCase::getType, Function.identity()));
    }

    public NotificationTaskUseCase createNotification(NotificationTaskType type) {
        var task = notificationMap.get(type);
        if (task == null) {
            throw new IllegalArgumentException("Invalid notification type: " + type + ". No notification task found for this type.");
        }
        return task;
    }
}

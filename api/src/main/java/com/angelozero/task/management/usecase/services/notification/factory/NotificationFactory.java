package com.angelozero.task.management.usecase.services.notification.factory;

import com.angelozero.task.management.usecase.exception.BusinessException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class NotificationFactory {

    private final List<NotificationTaskUseCase> notificationTasks;
    private Map<NotificationTaskType, NotificationTaskUseCase> notificationMap;


    @PostConstruct
    public void init() {
        notificationMap = notificationTasks.stream()
                .collect(Collectors.toMap(NotificationTaskUseCase::getType, Function.identity()));
    }

    public NotificationTaskUseCase createNotification(NotificationTaskType type) {
        var task = notificationMap.get(type);
        if (task == null) {
            throw new BusinessException("Invalid notification type: " + type + ". No notification task found for this type.");
        }
        return task;
    }
}

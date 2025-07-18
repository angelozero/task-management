package com.angelozero.task.management.entity.unit.usecase.notification;

import com.angelozero.task.management.entity.Notification;
import com.angelozero.task.management.usecase.services.notification.HandleNotificationUseCase;
import com.angelozero.task.management.usecase.services.notification.chainofresponsibility.NotificationHandler;
import com.angelozero.task.management.usecase.services.notification.factory.NotificationFactory;
import com.angelozero.task.management.usecase.services.notification.factory.NotificationTaskType;
import com.angelozero.task.management.usecase.services.notification.factory.NotificationTaskUseCase;
import org.aspectj.weaver.ast.Not;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HandleNotificationUseCaseTest {

    @Mock
    private NotificationFactory notificationFactory;

    @Mock
    private NotificationHandler notificationHandler;

    @Mock
    private NotificationTaskUseCase smsNotificationService;

    @Mock
    private NotificationTaskUseCase logNotificationService;

    @Mock
    private NotificationTaskUseCase emailNotificationService;

    @InjectMocks
    private HandleNotificationUseCase handleNotificationUseCase;

    @Test
    @DisplayName("Should send and handle notifications with success")
    public void shouldSendAndHandleNotificationsWithSuccess() {
        String notificationMessage = "Test message";

        when(notificationFactory.createNotification(NotificationTaskType.SMS)).thenReturn(smsNotificationService);
        when(notificationFactory.createNotification(NotificationTaskType.LOG)).thenReturn(logNotificationService);
        when(notificationFactory.createNotification(NotificationTaskType.EMAIL)).thenReturn(emailNotificationService);

        when(notificationHandler.handleNotification(any(Notification.class))).thenReturn(new Notification("message", NotificationTaskType.SMS, false));

        assertDoesNotThrow(() -> handleNotificationUseCase.execute(notificationMessage));
    }
}

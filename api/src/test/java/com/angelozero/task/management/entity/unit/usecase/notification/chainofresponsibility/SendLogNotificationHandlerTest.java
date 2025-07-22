package com.angelozero.task.management.entity.unit.usecase.notification.chainofresponsibility;

import com.angelozero.task.management.entity.Notification;
import com.angelozero.task.management.usecase.services.notification.chainofresponsibility.SendLogNotificationHandler;
import com.angelozero.task.management.usecase.services.notification.chainofresponsibility.NotificationHandler;
import com.angelozero.task.management.usecase.services.notification.factory.NotificationTaskType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
public class SendLogNotificationHandlerTest {

    private SendLogNotificationHandler sendLogNotificationHandler;

    @Mock
    private NotificationHandler nextHandler;

    @BeforeEach
    void setUp() {
        sendLogNotificationHandler = new SendLogNotificationHandler();
        sendLogNotificationHandler.setNextHandler(nextHandler);
    }

    @Test
    @DisplayName("Should handle LOG notification")
    public void shouldHandleLogNotification() {
        var notification = new Notification(NotificationTaskType.LOG.name(), NotificationTaskType.LOG, false);
        var result = sendLogNotificationHandler.handleNotification(notification);
        assertTrue(result.handled());
        verifyNoInteractions(nextHandler);
    }

    @Test
    @DisplayName("Should handle notification if not handled by previous handlers")
    public void shouldHandleNotificationIfNotHandledByPreviousHandlers() {
        var notification = new Notification(NotificationTaskType.SMS.name(), NotificationTaskType.SMS, false);
        var result = sendLogNotificationHandler.handleNotification(notification);
        assertTrue(result.handled());
        verifyNoInteractions(nextHandler);
    }

    @Test
    @DisplayName("Should not handle notification if already handled")
    public void shouldNotHandleNotificationIfAlreadyHandled() {
        var notification = new Notification(NotificationTaskType.EMAIL.name(), NotificationTaskType.EMAIL, true);
        var result = sendLogNotificationHandler.handleNotification(notification);
        assertTrue(result.handled());
        verifyNoInteractions(nextHandler);
    }
}
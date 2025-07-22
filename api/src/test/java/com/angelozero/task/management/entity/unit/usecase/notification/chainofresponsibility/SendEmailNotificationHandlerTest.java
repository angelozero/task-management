package com.angelozero.task.management.entity.unit.usecase.notification.chainofresponsibility;

import com.angelozero.task.management.entity.Notification;
import com.angelozero.task.management.usecase.services.notification.chainofresponsibility.SendEmailNotificationHandler;
import com.angelozero.task.management.usecase.services.notification.chainofresponsibility.NotificationHandler;
import com.angelozero.task.management.usecase.services.notification.factory.NotificationTaskType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
public class SendEmailNotificationHandlerTest {

    private SendEmailNotificationHandler sendEmailNotificationHandler;

    @Mock
    private NotificationHandler nextHandler;

    @BeforeEach
    void setUp() {
        sendEmailNotificationHandler = new SendEmailNotificationHandler();
        sendEmailNotificationHandler.setNextHandler(nextHandler);
    }

    @Test
    @DisplayName("Should handle email notification")
    public void shouldHandleEmailNotification() {
        var notification = new Notification(NotificationTaskType.EMAIL.name(), NotificationTaskType.EMAIL, false);
        var result = sendEmailNotificationHandler.handleNotification(notification);
        assertTrue(result.handled());
        verifyNoInteractions(nextHandler);
    }

    @Test
    @DisplayName("Should not handle non-email notification and pass to next handler")
    public void shouldNotHandleNonEmailNotificationAndPassToNextHandler() {
        var notification = new Notification(NotificationTaskType.SMS.name(), NotificationTaskType.SMS, false);
        sendEmailNotificationHandler.handleNotification(notification);
        verify(nextHandler).handleNotification(notification);
        assertFalse(notification.handled());
    }

    @Test
    @DisplayName("Should return notification if already handled")
    public void shouldReturnNotificationIfAlreadyHandled() {
        var notification = new Notification(NotificationTaskType.EMAIL.name(), NotificationTaskType.EMAIL, true);
        var result = sendEmailNotificationHandler.handleNotification(notification);
        assertTrue(result.handled());
        verifyNoInteractions(nextHandler);
    }
}
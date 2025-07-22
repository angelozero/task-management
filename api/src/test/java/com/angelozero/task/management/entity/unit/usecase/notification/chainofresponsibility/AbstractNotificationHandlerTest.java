package com.angelozero.task.management.entity.unit.usecase.notification.chainofresponsibility;

import com.angelozero.task.management.entity.Notification;
import com.angelozero.task.management.usecase.services.notification.chainofresponsibility.AbstractNotificationHandler;
import com.angelozero.task.management.usecase.services.notification.chainofresponsibility.NotificationHandler;
import com.angelozero.task.management.usecase.services.notification.factory.NotificationTaskType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AbstractNotificationHandlerTest {

    @Mock
    private NotificationHandler nextHandler;

    private static class ConcreteNotificationHandler extends AbstractNotificationHandler {
        @Override
        protected Notification processNotification(Notification notification) {
            return notification.hasHandled(true);
        }
    }

    @Test
    @DisplayName("Should set next handler")
    public void shouldSetNextHandler() {
        var handler = new ConcreteNotificationHandler();
        handler.setNextHandler(nextHandler);
        assertNotNull(handler.nextHandler);
    }

    @Test
    @DisplayName("Should handle notification and return if handled")
    public void shouldHandleNotificationAndReturnIfHandled() {
        var handler = new ConcreteNotificationHandler();
        var notification = new Notification(NotificationTaskType.EMAIL.name(), NotificationTaskType.EMAIL, false);
        var result = handler.handleNotification(notification);
        assertTrue(result.handled());
        verifyNoInteractions(nextHandler);
    }

    @Test
    @DisplayName("Should pass to next handler if not handled")
    public void shouldPassToNextHandlerIfNotHandled() {
        var handler = new ConcreteNotificationHandler() {
            @Override
            protected Notification processNotification(Notification notification) {
                return notification.hasHandled(false);
            }
        };
        handler.setNextHandler(nextHandler);
        var notification = new Notification(NotificationTaskType.EMAIL.name(), NotificationTaskType.EMAIL, false);
        when(nextHandler.handleNotification(any(Notification.class))).thenReturn(notification.hasHandled(true));

        var result = handler.handleNotification(notification);

        assertTrue(result.handled());
        verify(nextHandler, times(1)).handleNotification(notification.hasHandled(false));
    }

    @Test
    @DisplayName("Should return notification if not handled and no next handler")
    public void shouldReturnNotificationIfNotHandledAndNoNextHandler() {
        var handler = new ConcreteNotificationHandler() {
            @Override
            protected Notification processNotification(Notification notification) {
                return notification.hasHandled(false);
            }
        };
        var notification = new Notification(NotificationTaskType.EMAIL.name(), NotificationTaskType.EMAIL, false);
        var result = handler.handleNotification(notification);
        assertFalse(result.handled());
    }
}
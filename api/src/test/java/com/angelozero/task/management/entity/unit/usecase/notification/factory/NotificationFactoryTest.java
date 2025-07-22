package com.angelozero.task.management.entity.unit.usecase.notification.factory;

import com.angelozero.task.management.usecase.exception.BusinessException;
import com.angelozero.task.management.usecase.services.notification.factory.NotificationFactory;
import com.angelozero.task.management.usecase.services.notification.factory.NotificationTaskType;
import com.angelozero.task.management.usecase.services.notification.factory.NotificationTaskUseCase;
import com.angelozero.task.management.usecase.services.notification.factory.SendEmailNotificationTaskUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class NotificationFactoryTest {

    @Mock
    private List<NotificationTaskUseCase> notificationTasks;

    private NotificationFactory notificationFactory;

    @BeforeEach
    void setUp() {
        notificationTasks = Collections.singletonList(mock(SendEmailNotificationTaskUseCase.class));
        notificationFactory = new NotificationFactory(notificationTasks);
        notificationFactory.init();
    }

    @Test
    @DisplayName("Should create notification factory with success")
    public void shouldCreateNotificationFactoryWithSuccess() {
        NotificationTaskType expectedType = NotificationTaskType.EMAIL;
        NotificationTaskUseCase mockTaskUseCase = mock(SendEmailNotificationTaskUseCase.class);
        when(mockTaskUseCase.getType()).thenReturn(expectedType);

        notificationTasks = Collections.singletonList(mockTaskUseCase);
        notificationFactory = new NotificationFactory(notificationTasks);
        notificationFactory.init();

        var response = notificationFactory.createNotification(expectedType);

        assertNotNull(response);
    }

    @Test
    @DisplayName("Should throw an exception without task")
    public void shouldThrowAnExceptionWithoutTask() {
        NotificationTaskType nonExistentType = NotificationTaskType.SMS; // A type not expected to be in our mock factory

        var exception = assertThrows(BusinessException.class, () ->
                notificationFactory.createNotification(nonExistentType));

        assertNotNull(exception);
        assertEquals("Invalid notification type: " + nonExistentType + ". No notification task found for this type.", exception.getMessage());
    }
}

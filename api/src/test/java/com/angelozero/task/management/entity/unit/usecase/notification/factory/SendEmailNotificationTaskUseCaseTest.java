package com.angelozero.task.management.entity.unit.usecase.notification.factory;

import com.angelozero.task.management.usecase.services.notification.factory.NotificationTaskType;
import com.angelozero.task.management.usecase.services.notification.factory.SendEmailNotificationTaskUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class SendEmailNotificationTaskUseCaseTest {

    private final SendEmailNotificationTaskUseCase sendEmailNotificationTaskUseCase = new SendEmailNotificationTaskUseCase();

    @Test
    @DisplayName("Should execute email notification with success")
    public void shouldExecuteEmailNotificationWithSuccess() {
        var message = "Test Email Message";
        sendEmailNotificationTaskUseCase.execute(message);
    }

    @Test
    @DisplayName("Should return EMAIL type")
    public void shouldReturnEmailType() {
        NotificationTaskType type = sendEmailNotificationTaskUseCase.getType();
        assertNotNull(type);
        assertEquals(NotificationTaskType.EMAIL, type);
    }
}
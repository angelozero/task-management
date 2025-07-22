package com.angelozero.task.management.entity.unit.usecase.notification.factory;

import com.angelozero.task.management.usecase.services.notification.factory.NotificationTaskType;
import com.angelozero.task.management.usecase.services.notification.factory.SendSmsNotificationTaskUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class SendSmsNotificationTaskUseCaseTest {

    private final SendSmsNotificationTaskUseCase sendSmsNotificationTaskUseCase = new SendSmsNotificationTaskUseCase();

    @Test
    @DisplayName("Should execute SMS notification with success")
    public void shouldExecuteSmsNotificationWithSuccess() {
        String message = "Test SMS Message";
        sendSmsNotificationTaskUseCase.execute(message);
    }

    @Test
    @DisplayName("Should return SMS type")
    public void shouldReturnSmsType() {
        NotificationTaskType type = sendSmsNotificationTaskUseCase.getType();
        assertNotNull(type);
        assertEquals(NotificationTaskType.SMS, type);
    }
}
package com.angelozero.task.management.entity.unit.usecase.notification.factory;

import com.angelozero.task.management.usecase.services.notification.factory.NotificationTaskType;
import com.angelozero.task.management.usecase.services.notification.factory.SendLogNotificationTaskUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class SendLogNotificationTaskUseCaseTest {

    private final SendLogNotificationTaskUseCase sendLogNotificationTaskUseCase = new SendLogNotificationTaskUseCase();

    @Test
    @DisplayName("Should execute log notification with success")
    public void shouldExecuteLogNotificationWithSuccess() {
        var message = "Test Log Message";
        sendLogNotificationTaskUseCase.execute(message);
    }

    @Test
    @DisplayName("Should return LOG type")
    public void shouldReturnLogType() {
        NotificationTaskType type = sendLogNotificationTaskUseCase.getType();
        assertNotNull(type);
        assertEquals(NotificationTaskType.LOG, type);
    }
}
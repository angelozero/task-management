package com.angelozero.task.management.entity.unit.usecase.task;

import com.angelozero.task.management.entity.Task;
import com.angelozero.task.management.entity.status.Completed;
import com.angelozero.task.management.usecase.services.task.SaveTaskUseCase;
import com.angelozero.task.management.usecase.exception.BusinessException;
import com.angelozero.task.management.usecase.gateway.TaskGateway;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SaveTaskUseCaseTest {

    @Mock
    private TaskGateway taskGateway;

    @InjectMocks
    private SaveTaskUseCase saveTaskUseCase;

    @Test
    @DisplayName("Should save a Task with success")
    public void shouldSaveTaskWithSuccess() {
        var taskMock = new Task("", "description", false, new Completed());

        doNothing().when(taskGateway).save(any(Task.class));

        Assertions.assertDoesNotThrow(() -> saveTaskUseCase.execute(taskMock));
    }

    @Test
    @DisplayName("Should fail to save a task without data")
    public void shouldFailToSaveTaskWithoutData() {
        var exception = assertThrows(BusinessException.class,
                () -> saveTaskUseCase.execute(null));

        verify(taskGateway, never()).save(any(Task.class));

        assertNotNull(exception);
        assertEquals("No Task data was informed to be saved", exception.getMessage());
    }
}

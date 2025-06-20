package com.angelozero.task.management.entity.unit.usecase.task;

import com.angelozero.task.management.entity.Task;
import com.angelozero.task.management.entity.status.Completed;
import com.angelozero.task.management.usecase.services.task.UpdateTaskUseCase;
import com.angelozero.task.management.usecase.exception.BusinessException;
import com.angelozero.task.management.usecase.gateway.TaskGateway;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UpdateTaskUseCaseTest {

    @Mock
    private TaskGateway taskGateway;

    @InjectMocks
    private UpdateTaskUseCase updateTaskUseCase;

    @Test
    @DisplayName("Should update a Task with success - same description - same isCompleted")
    public void shouldUpdateTaskWithSuccessSameDescriptionSameIsCompleted() {
        var taskMock = new Task("task-id", "description", true, new Completed());

        when(taskGateway.findById(anyString())).thenReturn(taskMock);
        when(taskGateway.update(any(Task.class))).thenReturn(taskMock);

        var response = updateTaskUseCase.execute("task-id", taskMock);

        assertNotNull(response);
    }

    @Test
    @DisplayName("Should update a Task with success - different description - different isCompleted")
    public void shouldUpdateTaskWithSuccessDifferentDescriptionDifferentIsCompleted() {
        var savedTaskMock = new Task("task-id", "description", true, new Completed());
        var taskMock = new Task("task-id", "description-test", false, new Completed());

        when(taskGateway.findById(anyString())).thenReturn(savedTaskMock);
        when(taskGateway.update(any(Task.class))).thenReturn(taskMock);

        var response = updateTaskUseCase.execute("task-id", taskMock);

        assertNotNull(response);
    }

    @Test
    @DisplayName("Should fail to update a task without id")
    public void shouldFailToUpdateTaskWithoutId() {
        var exception = assertThrows(BusinessException.class,
                () -> updateTaskUseCase.execute("", null));

        verify(taskGateway, never()).findById(anyString());
        verify(taskGateway, never()).update(any(Task.class));

        assertNotNull(exception);
        assertEquals("No Task ID was informed to be updated", exception.getMessage());
    }

    @Test
    @DisplayName("Should fail to update a task without data")
    public void shouldFailToUpdateTaskWithoutData() {
        var exception = assertThrows(BusinessException.class,
                () -> updateTaskUseCase.execute("task-id", null));

        verify(taskGateway, never()).findById(anyString());
        verify(taskGateway, never()).update(any(Task.class));

        assertNotNull(exception);
        assertEquals("No Task data was informed to be updated", exception.getMessage());
    }
}

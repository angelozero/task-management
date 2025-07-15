package com.angelozero.task.management.entity.unit.usecase.task;

import com.angelozero.task.management.entity.Task;
import com.angelozero.task.management.entity.status.Completed;
import com.angelozero.task.management.usecase.services.task.DeleteTaskUseCase;
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
public class DeleteTaskUseCaseTest {

    @Mock
    private TaskGateway taskGateway;

    @InjectMocks
    private DeleteTaskUseCase deleteTaskUseCase;

    @Test
    @DisplayName("Should delete a task with success")
    public void shouldDeleteTaskWithSuccess() {
        var taskMock = new Task("", "description", true, new Completed());

        when(taskGateway.findById(anyString())).thenReturn(taskMock);
        doNothing().when(taskGateway).delete(any(Task.class));

        assertDoesNotThrow(() -> deleteTaskUseCase.execute("task-id"));
    }

    @Test
    @DisplayName("Should not delete a task")
    public void shouldNotDeleteTask() {
        when(taskGateway.findById(anyString())).thenReturn(null);

        assertDoesNotThrow(() -> deleteTaskUseCase.execute("task-id"));

        verify(taskGateway, never()).delete(any(Task.class));
    }

    @Test
    @DisplayName("Should fail to delete a task without id")
    public void shouldFailToUpdateTaskWithoutId() {
        var exception = assertThrows(BusinessException.class,
                () -> deleteTaskUseCase.execute(null));

        verify(taskGateway, never()).findById(anyString());
        verify(taskGateway, never()).delete(any(Task.class));

        assertNotNull(exception);
        assertEquals("No Task ID was informed to be deleted", exception.getMessage());
    }
}

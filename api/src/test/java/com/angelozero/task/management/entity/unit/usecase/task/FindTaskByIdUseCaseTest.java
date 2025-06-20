package com.angelozero.task.management.entity.unit.usecase.task;

import com.angelozero.task.management.entity.Task;
import com.angelozero.task.management.entity.status.Completed;
import com.angelozero.task.management.usecase.services.task.FindTaskByIdUseCase;
import com.angelozero.task.management.usecase.exception.BusinessException;
import com.angelozero.task.management.usecase.gateway.TaskGateway;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FindTaskByIdUseCaseTest {

    @Mock
    private TaskGateway taskGateway;

    @InjectMocks
    private FindTaskByIdUseCase findTaskByIdUseCase;

    @Test
    @DisplayName("Should find a task by id with success")
    public void shouldFindTaskByIdWithSuccess() {
        var taskMock = new Task("", "description", true, new Completed());

        when(taskGateway.findById(anyString())).thenReturn(taskMock);

        var response = findTaskByIdUseCase.execute("task-id");

        assertNotNull(response);
    }

    @Test
    @DisplayName("Should not find a task by id")
    public void shouldNotFindTaskById() {
        when(taskGateway.findById(anyString())).thenReturn(null);

        var response = findTaskByIdUseCase.execute("task-id");

        assertNull(response);
    }

    @Test
    @DisplayName("Should fail to find a person without id")
    public void shouldFailToFindPersonWithoutId() {
        var exception = assertThrows(BusinessException.class,
                () -> findTaskByIdUseCase.execute(null));

        verify(taskGateway, never()).findById(anyString());

        assertNotNull(exception);
        assertEquals("No Task ID was informed to be found", exception.getMessage());
    }
}

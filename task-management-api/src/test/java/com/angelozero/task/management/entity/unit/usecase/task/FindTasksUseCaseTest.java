package com.angelozero.task.management.entity.unit.usecase.task;

import com.angelozero.task.management.entity.Task;
import com.angelozero.task.management.entity.status.Completed;
import com.angelozero.task.management.usecase.services.task.FindTasksUseCase;
import com.angelozero.task.management.usecase.gateway.TaskGateway;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FindTasksUseCaseTest {

    @Mock
    private TaskGateway taskGateway;

    @InjectMocks
    private FindTasksUseCase findTasksUseCase;

    @Test
    @DisplayName("Should find paged tasks with success - no sortedField")
    public void shouldFindPagedTasksWithSuccessNoSortedField() {
        var taskMock = new Task("", "description", false, new Completed());
        var taskListMock = List.of(taskMock);
        var pageRequestMock = PageRequest.of(0, 1);
        var taskPagedMock = new PageImpl<>(taskListMock, pageRequestMock, taskListMock.size());

        when(taskGateway.findAll(anyInt(), anyInt(), any(), anyBoolean())).thenReturn(taskPagedMock);

        var response = findTasksUseCase.execute(0, 1, null, false);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
    }

    @Test
    @DisplayName("Should find paged tasks with success - with sortedField")
    public void shouldFindPagedTasksWithSuccessWithSortedField() {
        var taskMock = new Task("", "description", true, new Completed());
        var taskListMock = List.of(taskMock);
        var pageRequestMock = PageRequest.of(0, 1);
        var taskPagedMock = new PageImpl<>(taskListMock, pageRequestMock, taskListMock.size());

        when(taskGateway.findAll(anyInt(), anyInt(), anyString(), anyBoolean())).thenReturn(taskPagedMock);

        var response = findTasksUseCase.execute(0, 1, "description", true);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
    }
}

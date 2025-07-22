package com.angelozero.task.management.entity.unit.adapter.controller;

import com.angelozero.task.management.adapter.controller.TaskController;
import com.angelozero.task.management.adapter.controller.mapper.PagedRequestMapper;
import com.angelozero.task.management.adapter.controller.mapper.TaskRequestMapper;
import com.angelozero.task.management.adapter.controller.rest.request.TaskRequest;
import com.angelozero.task.management.adapter.controller.rest.response.PagedResponse;
import com.angelozero.task.management.adapter.controller.rest.response.TaskResponse;
import com.angelozero.task.management.entity.Task;
import com.angelozero.task.management.entity.status.Blocked;
import com.angelozero.task.management.entity.status.EventStatusTask;
import com.angelozero.task.management.entity.status.EventStatusType;
import com.angelozero.task.management.usecase.services.task.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskControllerTest {

    @Mock
    private FindTaskByIdUseCase findTaskByIdUseCase;

    @Mock
    private FindTasksUseCase findTasksUseCase;

    @Mock
    private SaveTaskUseCase saveTaskUseCase;

    @Mock
    private UpdateTaskUseCase updateTaskUseCase;

    @Mock
    private DeleteTaskUseCase deleteTaskUseCase;

    @Mock
    private TaskRequestMapper taskRequestMapper;

    @InjectMocks
    private TaskController taskController;

    @Test
    @DisplayName("Should find task by id with success")
    public void shouldFindTaskByIdWithSuccess() {
        var id = "123";
        var task = new Task("123", "Task Title", false, new Blocked());
        var taskResponse = new TaskResponse("123", "Task Title", false, "status desc", 1);

        when(findTaskByIdUseCase.execute(id)).thenReturn(task);
        when(taskRequestMapper.toTaskResponse(task)).thenReturn(taskResponse);

        var response = taskController.findTasks(id);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(taskResponse, response.getBody());
    }

    @Test
    @DisplayName("Should return no content when task by id not found")
    public void shouldReturnNoContentWhenTaskByIdNotFound() {
        var id = "nonExistentId";

        when(findTaskByIdUseCase.execute(id)).thenReturn(null);
        when(taskRequestMapper.toTaskResponse(null)).thenReturn(null);

        var response = taskController.findTasks(id);

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    @DisplayName("Should find tasks with pagination and filters with success")
    public void shouldFindTasksWithPaginationAndFiltersWithSuccess() {
        var page = 0;
        var size = 10;
        var sortField = "title";
        var isCompleted = false;

        var taskList = Collections.singletonList(new Task("123", "Task Title", false, new Blocked()));
        var taskResponseList = Collections.singletonList(new TaskResponse("123", "Task Title", false, "status desc", 1));
        var pagedTasks = new PageImpl<>(taskList, PageRequest.of(page, size), 1);
        var pagedResponse = new PagedResponse<>(null, taskResponseList, 0, 1, 10, 1, true, true);

        when(findTasksUseCase.execute(page, size, sortField, isCompleted)).thenReturn(pagedTasks);
        when(taskRequestMapper.toTaskResponseList(taskList)).thenReturn(taskResponseList);

        try (MockedStatic<PagedRequestMapper> mockedStatic = mockStatic(PagedRequestMapper.class)) {
            mockedStatic.when(() -> PagedRequestMapper.toPagedResponse(anyList(), any(Page.class)))
                    .thenReturn(pagedResponse);

            var response = taskController.findTasks(page, size, sortField, isCompleted);

            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(pagedResponse, response.getBody());
            verify(findTasksUseCase).execute(page, size, sortField, isCompleted);
            verify(taskRequestMapper).toTaskResponseList(taskList);
        }
    }

    @Test
    @DisplayName("Should save task with success")
    public void shouldSaveTaskWithSuccess() {
        var taskRequest = new TaskRequest("New Task", false, 0);
        var task = new Task("123", "Task Title", false, new Blocked());

        when(taskRequestMapper.toTask(taskRequest)).thenReturn(task);
        doNothing().when(saveTaskUseCase).execute(task);

        var response = taskController.saveTask(taskRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(taskRequestMapper).toTask(taskRequest);
        verify(saveTaskUseCase).execute(task);
    }

    @Test
    @DisplayName("Should update task with success")
    public void shouldUpdateTaskWithSuccess() {
        var id = "123";
        var taskRequest = new TaskRequest("Updated Task", true, 0);
        var taskToUpdate = new Task("123", "Task Title", false, new Blocked());
        var updatedTask = new Task("123", "Task Title", false, new Blocked());
        var taskResponse = new TaskResponse(id, "Updated Task", true, "Updated Description", 0);

        when(taskRequestMapper.toTask(taskRequest)).thenReturn(taskToUpdate);
        when(updateTaskUseCase.execute(id, taskToUpdate)).thenReturn(updatedTask);
        when(taskRequestMapper.toTaskResponse(updatedTask)).thenReturn(taskResponse);

        var response = taskController.updateTasks(id, taskRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(taskResponse, response.getBody());
        verify(taskRequestMapper).toTask(taskRequest);
        verify(updateTaskUseCase).execute(id, taskToUpdate);
        verify(taskRequestMapper).toTaskResponse(updatedTask);
    }

    @Test
    @DisplayName("Should return no content when updating non-existent task")
    public void shouldReturnNoContentWhenUpdatingNonExistentTask() {
        var id = "nonExistentId";
        var taskRequest = new TaskRequest("Updated Task", true, 0);
        var taskToUpdate = new Task("123", "Task Title", false, new Blocked());

        when(taskRequestMapper.toTask(taskRequest)).thenReturn(taskToUpdate);
        when(updateTaskUseCase.execute(id, taskToUpdate)).thenReturn(null);
        when(taskRequestMapper.toTaskResponse(null)).thenReturn(null);

        var response = taskController.updateTasks(id, taskRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    @DisplayName("Should delete task with success")
    public void shouldDeleteTaskWithSuccess() {
        var id = "123";
        doNothing().when(deleteTaskUseCase).execute(id);

        var response = taskController.deleteTask(id);

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(deleteTaskUseCase).execute(id);
    }
}
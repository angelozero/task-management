package com.angelozero.task.management.adapter.controller;

import com.angelozero.task.management.adapter.controller.mapper.PagedRequestMapper;
import com.angelozero.task.management.adapter.controller.mapper.TaskRequestMapper;
import com.angelozero.task.management.adapter.controller.rest.request.TaskRequest;
import com.angelozero.task.management.adapter.controller.rest.response.PagedResponse;
import com.angelozero.task.management.adapter.controller.rest.response.TaskResponse;
import com.angelozero.task.management.usecase.services.task.*;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/task")
@AllArgsConstructor
public class TaskController {

    private final FindTaskByIdUseCase findTaskByIdUseCase;
    private final FindTasksUseCase findTasksUseCase;
    private final SaveTaskUseCase saveTaskUseCase;
    private final UpdateTaskUseCase updateTaskUseCase;
    private final DeleteTaskUseCase deleteTaskUseCase;

    private final TaskRequestMapper taskRequestMapper;

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> findTasks(@PathVariable String id) {
        var task = findTaskByIdUseCase.execute(id);
        var taskResponse = taskRequestMapper.toTaskResponse(task);

        return Optional.ofNullable(taskResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    @GetMapping
    public ResponseEntity<PagedResponse<TaskResponse>> findTasks(@RequestParam(defaultValue = "0") int page,
                                                                 @RequestParam(defaultValue = "10") int size,
                                                                 @RequestParam(required = false) String sortField,
                                                                 @RequestParam(required = false) Boolean isCompleted) {
        var pagedTasks = findTasksUseCase.execute(page, size, sortField, isCompleted);
        var taskResponseList = taskRequestMapper.toTaskResponseList(pagedTasks.getContent());
        var pagedResponse = PagedRequestMapper.toPagedResponse(taskResponseList, pagedTasks);

        return ResponseEntity.ok(pagedResponse);
    }

    @PostMapping
    public ResponseEntity<Void> saveTask(@RequestBody TaskRequest taskRequest) {
        var task = taskRequestMapper.toTask(taskRequest);
        saveTaskUseCase.execute(task);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTasks(@PathVariable String id, @RequestBody TaskRequest taskRequest) {
        var taskToUpdate = taskRequestMapper.toTask(taskRequest);
        var task = updateTaskUseCase.execute(id, taskToUpdate);
        var taskResponse = taskRequestMapper.toTaskResponse(task);

        return Optional.ofNullable(taskResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable String id) {
        deleteTaskUseCase.execute(id);

        return ResponseEntity.noContent().build();
    }
}

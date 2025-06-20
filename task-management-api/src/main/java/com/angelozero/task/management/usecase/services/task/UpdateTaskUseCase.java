package com.angelozero.task.management.usecase.services.task;

import com.angelozero.task.management.entity.Task;
import com.angelozero.task.management.usecase.exception.BusinessException;
import com.angelozero.task.management.usecase.gateway.TaskGateway;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class UpdateTaskUseCase {

    private final TaskGateway taskGateway;

    public Task execute(String id, Task task) {
        if (StringUtils.isBlank(id)) {
            log.error("No Task ID was informed to be updated");
            throw new BusinessException("No Task ID was informed to be updated");
        }

        if (task == null) {
            log.error("No Task data was informed to be updated");
            throw new BusinessException("No Task data was informed to be updated");
        }

        log.info("Updating the Task - ID: {}, with values {}", id, task);

        var taskFounded = taskGateway.findById(id);

        if (taskFounded == null) {
            log.info("No Task was found to be updated");
            return null;
        }

        var taskToUpdate = updateTaskValues(id, task, taskFounded);

        var taskUpdated = taskGateway.update(taskToUpdate);

        log.info("Task was updated with success");
        return taskUpdated;
    }

    private Task updateTaskValues(String id, Task task, Task taskFounded) {
        var isSameTaskDescription = task.description().equals(taskFounded.description());

        var description = isSameTaskDescription ? taskFounded.description() : task.description();
        var completed = task.completed() == null ? taskFounded.completed() : task.completed();

        return new Task(id, description, completed, task.status());
    }
}

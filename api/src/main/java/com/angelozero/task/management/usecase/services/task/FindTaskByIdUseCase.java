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
public class FindTaskByIdUseCase {

    private final TaskGateway taskGateway;

    public Task execute(String id) {
        if (StringUtils.isBlank(id)) {
            log.error("No Task ID was informed to be found");
            throw new BusinessException("No Task ID was informed to be found");
        }

        log.info("Getting a Task by ID: {}", id);

        var task = taskGateway.findById(id);

        if (task == null) {
            log.info("Task was not found with the ID: {}", id);
            return null;
        }

        log.info("Task was found by ID: {} with success", id);
        return task;
    }
}

package com.angelozero.task.management.usecase.services.task;

import com.angelozero.task.management.usecase.exception.BusinessException;
import com.angelozero.task.management.usecase.gateway.TaskGateway;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class DeleteTaskUseCase {

    private final TaskGateway taskGateway;

    public void execute(String id) {
        if (StringUtils.isBlank(id)) {
            log.error("No Task ID was informed to be deleted");
            throw new BusinessException("No Task ID was informed to be deleted");
        }

        log.info("Deleting the Task - ID: {}", id);

        var taskFound = taskGateway.findById(id);

        if (taskFound == null) {
            log.info("No Task was found to be deleted");
            return;
        }

        taskGateway.delete(taskFound);

        log.info("Task deleted with success");
    }
}

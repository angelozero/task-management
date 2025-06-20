package com.angelozero.task.management.usecase.services.task;

import com.angelozero.task.management.entity.Task;
import com.angelozero.task.management.usecase.exception.BusinessException;
import com.angelozero.task.management.usecase.gateway.TaskGateway;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class SaveTaskUseCase {

    private final TaskGateway taskGateway;

    public void execute(Task task) {
        if (task == null) {
            log.error("No Task data was informed to be saved");
            throw new BusinessException("No Task data was informed to be saved");
        }

        log.info("Saving a Task");

        taskGateway.save(task);

        log.info("Task saved with success");
    }
}

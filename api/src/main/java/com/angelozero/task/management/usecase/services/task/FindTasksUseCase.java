package com.angelozero.task.management.usecase.services.task;

import com.angelozero.task.management.entity.Task;
import com.angelozero.task.management.usecase.gateway.TaskGateway;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class FindTasksUseCase {

    private final TaskGateway taskGateway;

    public Page<Task> execute(int page, int size, String sortField, Boolean isCompleted) {
        log.info("Getting pageable Tasks");

        var validField = StringUtils.isBlank(sortField) ? null : Task.validateFieldName(sortField);

        var pagedTasks = taskGateway.findAll(page, size, validField, isCompleted);

        log.info("Paged Tasks returned with success");
        return pagedTasks;
    }
}

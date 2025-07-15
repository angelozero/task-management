package com.angelozero.task.management.usecase.gateway;

import com.angelozero.task.management.entity.Task;
import org.springframework.data.domain.Page;

import java.util.List;

public interface TaskGateway {

    Task findById(String id);

    List<Task> findByIds(List<String> ids);

    Page<Task> findAll(int page, int size, String sortField, Boolean isCompleted);

    void save(Task task);

    void saveAll(List<Task> taskList);

    Task update(Task task);

    void delete(Task task);


}

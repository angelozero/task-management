package com.angelozero.task.management.adapter.dataprovider;

import com.angelozero.task.management.adapter.dataprovider.jpa.document.TaskEntity;
import com.angelozero.task.management.adapter.dataprovider.jpa.repository.mongo.TaskRepository;
import com.angelozero.task.management.adapter.dataprovider.mapper.TaskDataProviderMapper;
import com.angelozero.task.management.entity.Task;
import com.angelozero.task.management.usecase.exception.DataBaseDataProviderException;
import com.angelozero.task.management.usecase.gateway.TaskGateway;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class TasksByMongoDataProvider implements TaskGateway {

    private final TaskRepository taskRepository;
    private final TaskDataProviderMapper taskDataProviderMapper;

    @Override
    public Task findById(String id) {
        TaskEntity taskEntity;
        try {
            taskEntity = taskRepository.findById(id).orElse(null);

        } catch (Exception ex) {
            log.error("Fail to find a task by id {} - fail: {}", id, ex.getMessage());
            throw new DataBaseDataProviderException("Fail to find a task by id " + id + " - fail: " + ex.getMessage());
        }

        return taskDataProviderMapper.toTask(taskEntity);
    }

    @Override
    public List<Task> findByIds(List<String> ids) {
        List<TaskEntity> taskEntityList;
        try {
            taskEntityList = taskRepository.findByIdIn(ids);

        } catch (Exception ex) {
            log.error("Fail to find tasks by ids {} - fail: {}", ids, ex.getMessage());
            throw new DataBaseDataProviderException("Fail to find tasks by ids " + ids + " - fail: " + ex.getMessage());
        }

        return taskDataProviderMapper.toTaskList(taskEntityList);
    }


    @Override
    public Page<Task> findAll(int page, int size, String sortField, Boolean isCompleted) {
        Page<TaskEntity> pagedTasksEntity;
        var pageable = StringUtils.isBlank(sortField) ? PageRequest.of(page, size) : PageRequest.of(page, size, Sort.by(sortField));

        if (isCompleted != null) {
            try {
                pagedTasksEntity = taskRepository.findByCompleted(isCompleted, pageable);

            } catch (Exception ex) {
                log.error("Fail to find all completed tasks - fail: {}", ex.getMessage());
                throw new DataBaseDataProviderException("Fail to find all completed tasks - fail: " + ex.getMessage());
            }
        } else {
            try {
                pagedTasksEntity = taskRepository.findAll(pageable);
            } catch (Exception ex) {
                log.error("Fail to find pageable tasks - fail: {}", ex.getMessage());
                throw new DataBaseDataProviderException("Fail to find pageable tasks - fail: " + ex.getMessage());
            }
        }

        var tasks = taskDataProviderMapper.toTaskList(pagedTasksEntity.getContent());

        return new PageImpl<>(tasks, pageable, pagedTasksEntity.getTotalElements());
    }

    @Override
    public void save(Task task) {
        try {
            var taskEntity = taskDataProviderMapper.toTaskEntity(task);
            taskRepository.save(taskEntity);

        } catch (Exception ex) {
            log.error("Fail to save a task - fail: {}", ex.getMessage());
            throw new DataBaseDataProviderException("Fail to save a task - fail: " + ex.getMessage());
        }
    }

    @Override
    public void saveAll(List<Task> taskList) {
        try {
            var taskEntityList = taskDataProviderMapper.toTaskEntityList(taskList);
            taskRepository.saveAll(taskEntityList);

        } catch (Exception ex) {
            log.error("Fail to save a all tasks - fail: {}", ex.getMessage());
            throw new DataBaseDataProviderException("Fail to save all tasks - fail: " + ex.getMessage());
        }
    }

    @Override
    public Task update(Task task) {
        TaskEntity taskEntityUpdated;
        try {
            var taskEntity = taskDataProviderMapper.toTaskEntity(task);
            taskEntityUpdated = taskRepository.save(taskEntity);

        } catch (Exception ex) {
            log.error("Fail to update a task - fail: {}", ex.getMessage());
            throw new DataBaseDataProviderException("Fail to update a task - fail: " + ex.getMessage());
        }

        return taskDataProviderMapper.toTask(taskEntityUpdated);
    }

    @Override
    public void delete(Task task) {
        try {
            var taskEntity = taskDataProviderMapper.toTaskEntity(task);
            taskRepository.delete(taskEntity);

        } catch (Exception ex) {
            log.error("Fail to delete a task - fail: {}", ex.getMessage());
            throw new DataBaseDataProviderException("Fail to delete a task - fail: " + ex.getMessage());
        }
    }
}

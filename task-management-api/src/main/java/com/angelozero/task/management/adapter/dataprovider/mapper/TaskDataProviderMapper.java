package com.angelozero.task.management.adapter.dataprovider.mapper;

import com.angelozero.task.management.adapter.dataprovider.jpa.document.TaskEntity;
import com.angelozero.task.management.entity.Task;
import com.angelozero.task.management.entity.status.*;
import com.angelozero.task.management.usecase.exception.StatusTypeException;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TaskDataProviderMapper {

    List<Task> toTaskList(List<TaskEntity> taskEntityList);

    List<TaskEntity> toTaskEntityList(List<Task> taskList);

    @Mapping(target = "statusType", source = "status", qualifiedByName = "statusTaskToName")
    @Mapping(target = "statusCode", source = "status", qualifiedByName = "statusTaskToCode")
    TaskEntity toTaskEntity(Task task);

    @Mapping(target = "status", source = ".", qualifiedByName = "mapTaskEntityToStatusTask")
    Task toTask(TaskEntity taskEntity);

    @Named("statusTaskToName")
    default String mapStatusTaskToName(EventStatusTask EventStatusTask) {
        return EventStatusTask.getName();
    }

    @Named("statusTaskToCode")
    default int mapStatusTaskToCode(EventStatusTask EventStatusTask) {
        return EventStatusTask.getCode();
    }

    @Named("mapTaskEntityToStatusTask")
    default EventStatusTask mapTaskEntityToStatusTask(TaskEntity taskEntity) {
        return switch (taskEntity.statusCode()) {
            case 0 -> new CustomEventStatusTask();
            case 1 -> new Pending();
            case 2 -> new InProgress();
            case 3 -> new Completed();
            case 4 -> new Blocked();
            default -> throw new StatusTypeException("Invalid status type code: " + taskEntity.statusCode());
        };
    }
}

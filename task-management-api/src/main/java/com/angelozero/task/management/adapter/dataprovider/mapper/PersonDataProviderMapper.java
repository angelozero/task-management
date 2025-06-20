package com.angelozero.task.management.adapter.dataprovider.mapper;

import com.angelozero.task.management.adapter.dataprovider.jpa.document.PersonEntity;
import com.angelozero.task.management.adapter.dataprovider.jpa.document.TaskEntity;
import com.angelozero.task.management.entity.Person;
import com.angelozero.task.management.entity.Task;
import com.angelozero.task.management.entity.status.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface PersonDataProviderMapper {

    @Mapping(target = "taskIdsList", source = "taskIdsList")
    PersonEntity toPersonEntity(Person person, List<String> taskIdsList);

    default List<String> mapTaskListToTaskIdsList(List<Task> taskList) {
        return taskList.stream().map(Task::id).collect(Collectors.toList());
    }

    @Mapping(target = "taskList", source = "taskEntityList", qualifiedByName = "toTaskFromEntity")
    Person toPerson(PersonEntity personEntity, List<TaskEntity> taskEntityList);

    @Named("toTaskFromEntity")
    default Task toTaskFromEntity(TaskEntity taskEntity) {
        var statusType = EventStatusType.fromCode(taskEntity.statusCode());
        var statusTask = switch (statusType) {
            case CUSTOM -> new CustomEventStatusTask();
            case PENDING -> new Pending();
            case IN_PROGRESS -> new InProgress();
            case COMPLETED -> new Completed();
            case BLOCKED -> new Blocked();
        };
        return new Task(taskEntity.id(), taskEntity.description(), taskEntity.completed(), statusTask);
    }

    default List<Task> mapTaskEntityListToTaskList(List<TaskEntity> taskEntityList) {
        return taskEntityList.stream()
                .map(this::toTaskFromEntity)
                .collect(Collectors.toList());
    }
}

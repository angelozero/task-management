package com.angelozero.task.management.adapter.controller.mapper;

import com.angelozero.task.management.adapter.controller.datatransfer.PersonInput;
import com.angelozero.task.management.adapter.controller.datatransfer.PersonOutput;
import com.angelozero.task.management.adapter.controller.datatransfer.TaskInput;
import com.angelozero.task.management.adapter.controller.datatransfer.TaskOutput;
import com.angelozero.task.management.entity.Person;
import com.angelozero.task.management.entity.Task;
import com.angelozero.task.management.entity.status.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface PersonDataTransferMapper {

    @Mapping(target = "id", expression = "java(null)")
    @Mapping(target = "taskList", source = "taskList", qualifiedByName = "toTask")
    Person toPerson(PersonInput personInput);

    @Mapping(target = "taskList", source = "taskList", qualifiedByName = "toTaskOutput")
    PersonOutput toPersonOutput(Person person);

    @Named("toTask")
    default Task toTask(TaskInput taskInput) {
        var statusType = EventStatusType.fromCode(taskInput.statusCode());
        var statusTask = switch (statusType) {
            case CUSTOM -> new CustomEventStatusTask();
            case PENDING -> new Pending();
            case IN_PROGRESS -> new InProgress();
            case COMPLETED -> new Completed();
            case BLOCKED -> new Blocked();
        };
        return new Task(taskInput.id(), taskInput.description(), taskInput.completed(), statusTask);
    }

    @Named("toTaskOutput")
    default TaskOutput toTaskOutput(Task task) {
        return new TaskOutput(
                task.id(),
                task.description(),
                task.completed(),
                task.status().getName(),
                task.status().getCode()
        );
    }
}

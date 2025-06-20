package com.angelozero.task.management.adapter.controller.mapper;

import com.angelozero.task.management.adapter.controller.rest.request.TaskRequest;
import com.angelozero.task.management.adapter.controller.rest.response.TaskResponse;
import com.angelozero.task.management.entity.Task;
import com.angelozero.task.management.entity.status.*;
import com.angelozero.task.management.usecase.exception.StatusTypeException;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TaskRequestMapper {

    @Mapping(target = "id", expression = "java(null)")
    @Mapping(target = "status", expression = "java(mapStatusCodeToStatusTask(taskRequest.statusCode()))")
    Task toTask(TaskRequest taskRequest);

    @Mapping(target = "statusDescription", source = "status.name")
    @Mapping(target = "statusCode", source = "status.code")
    TaskResponse toTaskResponse(Task task);

    List<TaskResponse> toTaskResponseList(List<Task> taskList);

    default EventStatusTask mapStatusCodeToStatusTask(int statusCode) {
        return switch (statusCode) {
            case 0 -> new CustomEventStatusTask();
            case 1 -> new Pending();
            case 2 -> new InProgress();
            case 3 -> new Completed();
            case 4 -> new Blocked();
            default -> throw new StatusTypeException("Invalid status code: " + statusCode);
        };
    }
}

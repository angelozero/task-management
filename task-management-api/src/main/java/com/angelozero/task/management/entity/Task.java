package com.angelozero.task.management.entity;

import com.angelozero.task.management.entity.status.EventStatusTask;
import com.angelozero.task.management.usecase.exception.TaskException;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.RecordComponent;
import java.util.Arrays;

public record Task(String id,
                   String description,
                   Boolean completed,
                   EventStatusTask status) {

    public Task {
        if (StringUtils.isBlank(description)) {
            throw new TaskException("A Task must have a 'description' value");
        }
    }

    public static String validateFieldName(String fieldName) {
        return Arrays.stream(Task.class.getRecordComponents())
                .map(RecordComponent::getName)
                .filter(name -> name.equalsIgnoreCase(fieldName))
                .findFirst()
                .orElseThrow(() -> new TaskException("The field '" + fieldName + "' does not exist in the " + Task.class.getSimpleName() + " class."));
    }
}

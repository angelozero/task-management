package com.angelozero.task.management.entity.unit.entity;

import com.angelozero.task.management.entity.Task;
import com.angelozero.task.management.entity.status.Completed;
import com.angelozero.task.management.usecase.exception.TaskException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TaskTest {

    @Test
    @DisplayName("Should validate a Task creation without description")
    public void shouldValidateTaskCreationWithoutDescription() {

        var exception = assertThrows(TaskException.class, () -> new Task("123", "", false, new Completed()));

        assertEquals("A Task must have a 'description' value", exception.getMessage());
    }

    @Test
    @DisplayName("Should validate a field name")
    public void shouldValidateFieldName() {
        assertDoesNotThrow(() -> Task.validateFieldName("description"));
    }

    @Test
    @DisplayName("Should validate an invalid field name")
    public void shouldValidateAnInvalidFieldName() {
        var exception = assertThrows(TaskException.class,
                () -> Task.validateFieldName("test"));

        assertEquals("The field 'test' does not exist in the Task class.", exception.getMessage());
    }
}

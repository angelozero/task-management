package com.angelozero.task.management.usecase.util;

import com.angelozero.task.management.usecase.exception.FieldValidatorException;

import java.lang.reflect.RecordComponent;
import java.util.Arrays;

@Deprecated
public class FieldValidator {
    public static <T> String validateFieldName(Class<T> clazz, String fieldName) {
        return Arrays.stream(clazz.getRecordComponents())
                .map(RecordComponent::getName)
                .filter(name -> name.equalsIgnoreCase(fieldName))
                .findFirst()
                .orElseThrow(() -> new FieldValidatorException("The field '" + fieldName + "' does not exist in the " + clazz.getSimpleName() + " class."));
    }
}

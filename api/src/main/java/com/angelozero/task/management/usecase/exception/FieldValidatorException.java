package com.angelozero.task.management.usecase.exception;

@Deprecated
public class FieldValidatorException extends RuntimeException {
    public FieldValidatorException(String message) {
        super(message);
    }
}

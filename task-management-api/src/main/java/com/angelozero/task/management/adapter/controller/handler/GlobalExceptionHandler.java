package com.angelozero.task.management.adapter.controller.handler;

import com.angelozero.task.management.usecase.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Object> handleResourceBusinessException(BusinessException ex) {
        return generateUnprocessableEntityResponse(ex);
    }

    @ExceptionHandler(CacheDataProviderException.class)
    public ResponseEntity<Object> handleCacheDataProviderException(CacheDataProviderException ex) {
        return generateUnprocessableEntityResponse(ex);
    }

    @ExceptionHandler(DataBaseDataProviderException.class)
    public ResponseEntity<Object> handleDataBaseDataProviderException(DataBaseDataProviderException ex) {
        return generateUnprocessableEntityResponse(ex);
    }

    @ExceptionHandler(RestDataProviderException.class)
    public ResponseEntity<Object> handleResourceRestDataProviderException(RestDataProviderException ex) {
        return generateUnprocessableEntityResponse(ex);
    }

    @ExceptionHandler(TaskException.class)
    public ResponseEntity<Object> handleResourceTaskException(TaskException ex) {
        return generateUnprocessableEntityResponse(ex);
    }

    @ExceptionHandler(StatusTypeException.class)
    public ResponseEntity<Object> handleResourceStatusTypeException(StatusTypeException ex) {
        return generateUnprocessableEntityResponse(ex);
    }

    @ExceptionHandler(DynamicBeanTypeException.class)
    public ResponseEntity<Object> handleDynamicBeanTypeException(DynamicBeanTypeException ex) {
        return generateUnprocessableEntityResponse(ex);
    }

    @ExceptionHandler(FieldValidatorException.class)
    public ResponseEntity<Object> handleResourceFieldValidatorException(FieldValidatorException ex) {
        return generateUnprocessableEntityResponse(ex);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGeneralException(Exception ex) {
        return generateInternalServerErrorResponse(ex);
    }

    private ResponseEntity<Object> generateUnprocessableEntityResponse(Exception ex) {
        return new ResponseEntity<>(
                new ErrorData(ex.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY.value()),
                HttpStatus.UNPROCESSABLE_ENTITY
        );
    }

    private ResponseEntity<Object> generateInternalServerErrorResponse(Exception ex) {
        return new ResponseEntity<>(
                new ErrorData(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value()),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}

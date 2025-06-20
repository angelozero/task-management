package com.angelozero.task.management.adapter.controller.rest.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

public record PagedResponse<T>(@JsonInclude(JsonInclude.Include.NON_NULL)
                               T content,
                               @JsonInclude(JsonInclude.Include.NON_NULL)
                               List<T> contents,
                               int pageNumber,
                               int totalPages,
                               long totalElements,
                               int size,
                               boolean hasNext,
                               Boolean hasPrevious) {
}

package com.angelozero.task.management.adapter.controller.mapper;

import com.angelozero.task.management.adapter.controller.rest.response.PagedResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public class PagedRequestMapper {

    public static <T> PagedResponse<T> toPagedResponse(T content, Page page) {
        if (content == null || page == null) {
            return null;
        }
        return new PagedResponse<>(
                content,
                null,
                page.getNumber(),
                page.getTotalPages(),
                page.getTotalElements(),
                page.getSize(),
                page.hasNext(),
                page.hasPrevious());
    }

    public static <T> PagedResponse<T> toPagedResponse(List<T> contents, Page page) {
        if (contents == null || contents.isEmpty() || page == null) {
            return null;
        }
        return new PagedResponse<>(
                null,
                contents,
                page.getNumber(),
                page.getTotalPages(),
                page.getTotalElements(),
                page.getSize(),
                page.hasNext(),
                page.hasPrevious());
    }

}

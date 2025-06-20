package com.angelozero.task.management.adapter.controller.mapper;

import com.angelozero.task.management.adapter.controller.rest.response.EventResponse;
import com.angelozero.task.management.entity.Event;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EventRequestMapper {


    EventResponse toEventResponse(Event event);
}

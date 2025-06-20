package com.angelozero.task.management.adapter.dataprovider.mapper;

import com.angelozero.task.management.adapter.dataprovider.jpa.entity.EventEntity;
import com.angelozero.task.management.entity.Event;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EventDataProviderMapper {

    EventEntity toEventEntity(Event event);

    Event toEvent(EventEntity eventEntity);
}

package com.angelozero.task.management.adapter.dataprovider;

import com.angelozero.task.management.adapter.dataprovider.jpa.repository.postgres.writer.EventWriterDataBaseRepository;
import com.angelozero.task.management.adapter.dataprovider.mapper.EventDataProviderMapper;
import com.angelozero.task.management.entity.Event;
import com.angelozero.task.management.usecase.exception.DataBaseDataProviderException;
import com.angelozero.task.management.usecase.gateway.event.EventWriterGateway;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class EventWriterByPostgresDataProvider implements EventWriterGateway {

    private final EventWriterDataBaseRepository eventWriterDataBaseRepository;
    private final EventDataProviderMapper eventDataProviderMapper;

    @Override
    public Event save(Event event) {
        try {
            var eventEntity = eventDataProviderMapper.toEventEntity(event);
            var eventSaved = eventWriterDataBaseRepository.save(eventEntity);

            return eventDataProviderMapper.toEvent(eventSaved);

        } catch (Exception ex) {
            log.error("Fail to save an Event into the database - fail: {}", ex.getMessage());
            throw new DataBaseDataProviderException("Fail to save an Event into the database - fail: " + ex.getMessage());
        }
    }
}

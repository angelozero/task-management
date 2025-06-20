package com.angelozero.task.management.adapter.dataprovider.jpa.repository.postgres.writer;

import com.angelozero.task.management.adapter.dataprovider.jpa.entity.EventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventWriterDataBaseRepository extends JpaRepository<EventEntity, Integer> {
}

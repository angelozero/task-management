package com.angelozero.task.management.adapter.dataprovider.jpa.repository.postgres.reader;

import com.angelozero.task.management.adapter.dataprovider.jpa.entity.EventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EventReaderDataBaseRepository extends JpaRepository<EventEntity, Integer> {

    Optional<EventEntity> findByUserId(String userId);
}

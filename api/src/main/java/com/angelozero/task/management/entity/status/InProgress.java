package com.angelozero.task.management.entity.status;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public final class InProgress implements EventStatusTask {

    private String info;

    @Override
    public EventStatusType getType() {
        return EventStatusType.IN_PROGRESS;
    }

    @Override
    public String getName() {
        return EventStatusType.IN_PROGRESS.getDescription();
    }

    @Override
    public int getCode() {
        return EventStatusType.IN_PROGRESS.getCode();
    }
}

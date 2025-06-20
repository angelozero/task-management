package com.angelozero.task.management.entity.status;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public final class Completed implements EventStatusTask {

    private String info;

    @Override
    public EventStatusType getType() {
        return EventStatusType.COMPLETED;
    }

    @Override
    public String getName() {
        return EventStatusType.COMPLETED.getDescription();
    }

    @Override
    public int getCode() {
        return EventStatusType.COMPLETED.getCode();
    }
}

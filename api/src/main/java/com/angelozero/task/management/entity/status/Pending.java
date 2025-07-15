package com.angelozero.task.management.entity.status;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public final class Pending implements EventStatusTask {

    private String info;

    @Override
    public EventStatusType getType() {
        return EventStatusType.PENDING;
    }

    @Override
    public String getName() {
        return EventStatusType.PENDING.getDescription();
    }

    @Override
    public int getCode() {
        return EventStatusType.PENDING.getCode();
    }
}

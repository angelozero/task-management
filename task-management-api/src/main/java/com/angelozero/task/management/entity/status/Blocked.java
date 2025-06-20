package com.angelozero.task.management.entity.status;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public final class Blocked implements EventStatusTask {

    private String info;

    @Override
    public EventStatusType getType() {
        return EventStatusType.BLOCKED;
    }

    @Override
    public String getName() {
        return EventStatusType.BLOCKED.getDescription();
    }

    @Override
    public int getCode() {
        return EventStatusType.BLOCKED.getCode();
    }
}

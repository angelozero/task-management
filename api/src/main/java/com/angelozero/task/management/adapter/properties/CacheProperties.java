package com.angelozero.task.management.adapter.properties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class CacheProperties {
    protected String key;
    protected Integer ttl;
}

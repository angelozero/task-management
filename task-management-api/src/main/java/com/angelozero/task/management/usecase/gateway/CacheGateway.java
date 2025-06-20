package com.angelozero.task.management.usecase.gateway;

public interface CacheGateway {

    <T> void save(String key, T value, long timeoutSeconds);

    <T> T get(String key, Class<T> clazz);
}

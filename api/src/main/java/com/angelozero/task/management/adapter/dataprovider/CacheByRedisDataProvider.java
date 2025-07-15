package com.angelozero.task.management.adapter.dataprovider;

import com.angelozero.task.management.usecase.exception.CacheDataProviderException;
import com.angelozero.task.management.usecase.gateway.CacheGateway;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@AllArgsConstructor
public class CacheByRedisDataProvider implements CacheGateway {

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public <T> void save(String key, T value, long timeoutSeconds) {
        try {
            redisTemplate.opsForValue().set(key, value, timeoutSeconds, TimeUnit.SECONDS);

        } catch (Exception ex) {
            log.error("Fail to save cache - fail: {}", ex.getMessage());
            throw new CacheDataProviderException("Fail to save cache: " + ex.getMessage());
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> clazz) {
        try {
            var value = redisTemplate.opsForValue().get(key);
            return clazz.isInstance(value) ? (T) value : null;

        } catch (Exception ex) {
            log.error("Fail to get cache data - fail: {}", ex.getMessage());
            throw new CacheDataProviderException("Fail to get cache data: " + ex.getMessage());
        }
    }
}

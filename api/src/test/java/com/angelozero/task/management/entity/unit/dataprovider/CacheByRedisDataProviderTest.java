package com.angelozero.task.management.entity.unit.dataprovider;

import com.angelozero.task.management.adapter.dataprovider.CacheByRedisDataProvider;
import com.angelozero.task.management.usecase.exception.CacheDataProviderException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CacheByRedisDataProviderTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @InjectMocks
    private CacheByRedisDataProvider cacheByRedisDataProvider;

    @Test
    @DisplayName("Should save data to cache with success")
    void shouldSaveDataToCacheWithSuccess() {
        var key = "testKey";
        var value = "testValue";
        var timeoutSeconds = 60L;

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        doNothing().when(valueOperations).set(key, value, timeoutSeconds, TimeUnit.SECONDS);

        assertDoesNotThrow(() -> cacheByRedisDataProvider.save(key, value, timeoutSeconds));

        verify(redisTemplate, times(1)).opsForValue();
        verify(valueOperations, times(1)).set(key, value, timeoutSeconds, TimeUnit.SECONDS);
    }

    @Test
    @DisplayName("Should fail to save data to cache")
    void shouldFailToSaveDataToCache() {
        var key = "testKey";
        var value = "testValue";
        var timeoutSeconds = 60L;
        var errorMessage = "Fail to save cache: Simulate Redis save error";

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        doThrow(new RuntimeException("Simulate Redis save error"))
                .when(valueOperations).set(key, value, timeoutSeconds, TimeUnit.SECONDS);

        var exception = assertThrows(CacheDataProviderException.class,
                () -> cacheByRedisDataProvider.save(key, value, timeoutSeconds));

        assertNotNull(exception);
        assertEquals(errorMessage, exception.getMessage());

        verify(redisTemplate, times(1)).opsForValue();
        verify(valueOperations, times(1)).set(key, value, timeoutSeconds, TimeUnit.SECONDS);
    }

    @Test
    @DisplayName("Should get data from cache with success")
    void shouldGetDataFromCacheWithSuccess() {
        var key = "testKey";
        var expectedValue = "testValue";

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(key)).thenReturn(expectedValue);

        var result = cacheByRedisDataProvider.get(key, String.class);

        assertNotNull(result);
        assertEquals(expectedValue, result);

        verify(redisTemplate, times(1)).opsForValue();
        verify(valueOperations, times(1)).get(key);
    }

    @Test
    @DisplayName("Should get null when data type does not match")
    void shouldGetNullWhenDataTypeDoesNotMatch() {
        var key = "testKey";
        Integer storedValue = 123; // Stored as Integer, but expecting String

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(key)).thenReturn(storedValue);

        var result = cacheByRedisDataProvider.get(key, String.class);

        assertNull(result);

        verify(redisTemplate, times(1)).opsForValue();
        verify(valueOperations, times(1)).get(key);
    }

    @Test
    @DisplayName("Should get null when key does not exist")
    void shouldGetNullWhenKeyDoesNotExist() {
        var key = "nonExistentKey";

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(key)).thenReturn(null);

        var result = cacheByRedisDataProvider.get(key, String.class);

        assertNull(result);

        verify(redisTemplate, times(1)).opsForValue();
        verify(valueOperations, times(1)).get(key);
    }

    @Test
    @DisplayName("Should fail to get data from cache")
    void shouldFailToGetDataFromCache() {
        var key = "testKey";
        var errorMessage = "Fail to get cache data: Simulate Redis get error";

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        doThrow(new RuntimeException("Simulate Redis get error"))
                .when(valueOperations).get(key);

        CacheDataProviderException exception = assertThrows(CacheDataProviderException.class,
                () -> cacheByRedisDataProvider.get(key, String.class));

        assertNotNull(exception);
        assertEquals(errorMessage, exception.getMessage());

        verify(redisTemplate, times(1)).opsForValue();
        verify(valueOperations, times(1)).get(key);
    }
}

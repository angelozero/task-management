package com.angelozero.task.management.entity.unit.usecase.dynamicbean;

import com.angelozero.task.management.entity.dynamicbean.DynamicBeanType;
import com.angelozero.task.management.usecase.exception.BusinessException;
import com.angelozero.task.management.usecase.exception.DynamicBeanTypeException;
import com.angelozero.task.management.usecase.gateway.DynamicBeanGateway;
import com.angelozero.task.management.usecase.services.dynamicbean.DynamicBeanUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DynamicBeanUseCaseTest {

    @Mock
    private DynamicBeanGateway mockDynamicBeanGateway1;

    @Mock
    private DynamicBeanGateway mockDynamicBeanGateway2;

    @Mock
    private DynamicBeanGateway mockDynamicBeanGateway3;

    @InjectMocks
    private DynamicBeanUseCase dynamicBeanUseCase;

    private Map<String, DynamicBeanGateway> dynamicBeanGatewayMap;

    @BeforeEach
    public void setup() {
        dynamicBeanGatewayMap = new HashMap<>();
        dynamicBeanGatewayMap.put(DynamicBeanType.DYNAMIC_BEAN_TYPE_1.getDescription(), mockDynamicBeanGateway1);
        dynamicBeanGatewayMap.put(DynamicBeanType.DYNAMIC_BEAN_TYPE_2.getDescription(), mockDynamicBeanGateway2);
        dynamicBeanGatewayMap.put(DynamicBeanType.DYNAMIC_BEAN_TYPE_3.getDescription(), mockDynamicBeanGateway3);

        dynamicBeanUseCase = new DynamicBeanUseCase(dynamicBeanGatewayMap);
    }

    @Test
    @DisplayName("Should execute dynamic bean for DYNAMIC_BEAN_TYPE_1 with success")
    public void shouldExecuteDynamicBeanType1WithSuccess() {
        String expectedResponse = "Response from Dynamic Bean Type 1";
        when(mockDynamicBeanGateway1.execute()).thenReturn(expectedResponse);

        String response = dynamicBeanUseCase.execute(DynamicBeanType.DYNAMIC_BEAN_TYPE_1.getCode());

        assertNotNull(response);
        assertEquals(expectedResponse, response);
        verify(mockDynamicBeanGateway1, times(1)).execute();
        verify(mockDynamicBeanGateway2, never()).execute();
        verify(mockDynamicBeanGateway3, never()).execute();
    }

    @Test
    @DisplayName("Should execute dynamic bean for DYNAMIC_BEAN_TYPE_2 with success")
    public void shouldExecuteDynamicBeanType2WithSuccess() {
        String expectedResponse = "Response from Dynamic Bean Type 2";
        when(mockDynamicBeanGateway2.execute()).thenReturn(expectedResponse);

        String response = dynamicBeanUseCase.execute(DynamicBeanType.DYNAMIC_BEAN_TYPE_2.getCode());

        assertNotNull(response);
        assertEquals(expectedResponse, response);
        verify(mockDynamicBeanGateway2, times(1)).execute();
        verify(mockDynamicBeanGateway1, never()).execute();
        verify(mockDynamicBeanGateway3, never()).execute();
    }

    @Test
    @DisplayName("Should execute dynamic bean for DYNAMIC_BEAN_TYPE_3 with success")
    public void shouldExecuteDynamicBeanType3WithSuccess() {
        String expectedResponse = "Response from Dynamic Bean Type 3";
        when(mockDynamicBeanGateway3.execute()).thenReturn(expectedResponse);

        String response = dynamicBeanUseCase.execute(DynamicBeanType.DYNAMIC_BEAN_TYPE_3.getCode());

        assertNotNull(response);
        assertEquals(expectedResponse, response);
        verify(mockDynamicBeanGateway3, times(1)).execute();
        verify(mockDynamicBeanGateway1, never()).execute();
        verify(mockDynamicBeanGateway2, never()).execute();
    }

    @Test
    @DisplayName("Should throw BusinessException when id is null")
    public void shouldThrowBusinessExceptionWhenIdIsNull() {
        BusinessException exception = assertThrows(BusinessException.class,
                () -> dynamicBeanUseCase.execute(null));

        assertNotNull(exception);
        assertEquals("The id value for Dynamic Bean must not be null", exception.getMessage());
        verify(mockDynamicBeanGateway1, never()).execute();
        verify(mockDynamicBeanGateway2, never()).execute();
        verify(mockDynamicBeanGateway3, never()).execute();
    }

    @Test
    @DisplayName("Should throw DynamicBeanTypeException when id is invalid")
    public void shouldThrowDynamicBeanTypeExceptionWhenIdIsInvalid() {
        int invalidCode = 999;

        DynamicBeanTypeException exception = assertThrows(DynamicBeanTypeException.class,
                () -> dynamicBeanUseCase.execute(invalidCode));

        assertNotNull(exception);
        assertEquals("Invalid dynamic type code: " + invalidCode, exception.getMessage());
        verify(mockDynamicBeanGateway1, never()).execute();
        verify(mockDynamicBeanGateway2, never()).execute();
        verify(mockDynamicBeanGateway3, never()).execute();
    }

    @Test
    @DisplayName("Should return null if the specific dynamic bean gateway returns null")
    public void shouldReturnNullIfSpecificDynamicBeanGatewayReturnsNull() {
        when(mockDynamicBeanGateway1.execute()).thenReturn(null);

        String response = dynamicBeanUseCase.execute(DynamicBeanType.DYNAMIC_BEAN_TYPE_1.getCode());

        assertNull(response);
        verify(mockDynamicBeanGateway1, times(1)).execute();
        verify(mockDynamicBeanGateway2, never()).execute();
        verify(mockDynamicBeanGateway3, never()).execute();
    }
}
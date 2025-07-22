package com.angelozero.task.management.entity.unit.adapter.controller;

import com.angelozero.task.management.adapter.controller.DynamicBeanController;
import com.angelozero.task.management.usecase.services.dynamicbean.DynamicBeanUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DynamicBeanControllerTest {

    @Mock
    private DynamicBeanUseCase dynamicBeanUseCase;

    @InjectMocks
    private DynamicBeanController dynamicBeanController;

    @Test
    @DisplayName("Should execute dynamic beans with success")
    public void shouldExecuteDynamicBeansWithSuccess() {
        var id = 1;
        var expectedResponse = "Dynamic bean executed for ID: " + id;
        when(dynamicBeanUseCase.execute(id)).thenReturn(expectedResponse);

        var response = dynamicBeanController.executeDynamicBeans(id);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
    }
}
package com.angelozero.task.management.entity.unit.adapter.dataprovider;

import com.angelozero.task.management.adapter.dataprovider.dynamicbean.DynamicBean2DataProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DynamicBean2DataProviderTest {

    @Test
    @DisplayName("Should return the correct response string for Dynamic Bean 2")
    public void shouldReturnCorrectResponse() {
        var dataProvider = new DynamicBean2DataProvider();

        var response = dataProvider.execute();

        assertNotNull(response);
        assertEquals("This is a response from Dynamic Bean 2 - Data Provider", response);
    }
}
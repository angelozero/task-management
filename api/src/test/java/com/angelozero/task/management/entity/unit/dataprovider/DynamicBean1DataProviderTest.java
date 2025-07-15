package com.angelozero.task.management.entity.unit.dataprovider;

import com.angelozero.task.management.adapter.dataprovider.dynamicbean.DynamicBean1DataProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DynamicBean1DataProviderTest {

    @Test
    @DisplayName("Should return the correct response string for Dynamic Bean 1")
    public void shouldReturnCorrectResponse() {
        var dataProvider = new DynamicBean1DataProvider();

        var response = dataProvider.execute();

        assertNotNull(response);
        assertEquals("This is a response from Dynamic Bean 1 - Data Provider", response);
    }
}
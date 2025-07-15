package com.angelozero.task.management.adapter.dataprovider.dynamicbean;

import com.angelozero.task.management.usecase.gateway.DynamicBeanGateway;
import org.springframework.stereotype.Service;

@Service("dynamic_bean_2")
public class DynamicBean2DataProvider implements DynamicBeanGateway {
    @Override
    public String execute() {
        return "This is a response from Dynamic Bean 2 - Data Provider";
    }
}

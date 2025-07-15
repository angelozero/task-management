package com.angelozero.task.management.usecase.services.dynamicbean;


import com.angelozero.task.management.entity.dynamicbean.DynamicBeanType;
import com.angelozero.task.management.usecase.exception.BusinessException;
import com.angelozero.task.management.usecase.gateway.DynamicBeanGateway;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@AllArgsConstructor
public class DynamicBeanUseCase {

    private final Map<String, DynamicBeanGateway> dynamicBeanGateway;

    public String execute(Integer id) {

        if (id == null) {
            log.error("The id value for Dynamic Bean must not be null");
            throw new BusinessException("The id value for Dynamic Bean must not be null");
        }

        var dynamicBeanType = DynamicBeanType.fromCode(id).getDescription();

        log.info("Getting the Dynamic Bean response from {}", dynamicBeanType);
        return dynamicBeanGateway.get(dynamicBeanType).execute();
    }
}

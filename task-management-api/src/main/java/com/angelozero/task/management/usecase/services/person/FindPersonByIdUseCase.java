package com.angelozero.task.management.usecase.services.person;

import com.angelozero.task.management.entity.Person;
import com.angelozero.task.management.usecase.exception.BusinessException;
import com.angelozero.task.management.usecase.gateway.PersonGateway;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class FindPersonByIdUseCase {

    private final PersonGateway personGateway;

    public Person execute(String id) {
        if (StringUtils.isBlank(id)) {
            log.error("No Person ID was informed to be found");
            throw new BusinessException("No Person ID was informed to be found");
        }

        log.info("Getting a Person by ID: {}", id);

        var person = personGateway.findById(id);

        if (person == null) {
            log.info("Person was not found with the ID: {}", id);
            return null;
        }

        log.info("person was found by ID: {} with success", id);
        return person;
    }
}

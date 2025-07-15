package com.angelozero.task.management.usecase.services.person;

import com.angelozero.task.management.entity.Person;
import com.angelozero.task.management.usecase.exception.BusinessException;
import com.angelozero.task.management.usecase.gateway.PersonGateway;
import com.angelozero.task.management.usecase.gateway.TaskGateway;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class FindPersonByEmailUseCase {

    private final PersonGateway personGateway;

    public Person execute(String email) {
        if (StringUtils.isBlank(email)) {
            log.error("No Person email was informed to be found");
            throw new BusinessException("No Person email was informed to be found");
        }

        log.info("Getting a Person by email: {}", email);

        var person = personGateway.findByEmail(email);

        if (person == null) {
            log.info("Person was not found with the email: {}", email);
            return null;
        }

        log.info("person was found by email: {} with success", email);
        return person;
    }
}

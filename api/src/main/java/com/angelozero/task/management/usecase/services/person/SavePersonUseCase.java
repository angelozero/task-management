package com.angelozero.task.management.usecase.services.person;

import com.angelozero.task.management.entity.Person;
import com.angelozero.task.management.usecase.exception.BusinessException;
import com.angelozero.task.management.usecase.gateway.PersonGateway;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class SavePersonUseCase {

    private final PersonGateway personGateway;

    public void execute(Person person) {
        if (person == null) {
            log.error("No Person data was informed to be saved");
            throw new BusinessException("No Person data was informed to be saved");
        }

        log.info("Saving a Person");
        personGateway.save(person);

        log.info("Person saved with success");
    }
}

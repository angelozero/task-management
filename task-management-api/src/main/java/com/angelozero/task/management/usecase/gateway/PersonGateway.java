package com.angelozero.task.management.usecase.gateway;

import com.angelozero.task.management.entity.Person;
import com.angelozero.task.management.entity.Task;
import org.springframework.data.domain.Page;

public interface PersonGateway {

    Person findById(String id);

    Person findByEmail(String email);

    void save(Person person);

    Person update(Person person);

    void delete(Person person);
}

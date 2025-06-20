package com.angelozero.task.management.adapter.controller;

import com.angelozero.task.management.adapter.controller.datatransfer.PersonInput;
import com.angelozero.task.management.adapter.controller.datatransfer.PersonOutput;
import com.angelozero.task.management.adapter.controller.mapper.PersonDataTransferMapper;
import com.angelozero.task.management.usecase.services.person.FindPersonByEmailUseCase;
import com.angelozero.task.management.usecase.services.person.FindPersonByIdUseCase;
import com.angelozero.task.management.usecase.services.person.SavePersonUseCase;
import lombok.AllArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
@AllArgsConstructor
public class PersonGrpcController {

    private final PersonDataTransferMapper personDataTransferMapper;
    private final SavePersonUseCase savePersonUseCase;
    private final FindPersonByIdUseCase findPersonByIdUseCase;
    private final FindPersonByEmailUseCase findPersonByEmailUseCase;

    @MutationMapping()
    PersonOutput savePerson(@Argument PersonInput personInput) {
        var personToSave = personDataTransferMapper.toPerson(personInput);
        savePersonUseCase.execute(personToSave);

        var person = findPersonByEmailUseCase.execute(personInput.email());
        return personDataTransferMapper.toPersonOutput(person);
    }

    @QueryMapping()
    PersonOutput personById(@Argument String id) {
        var person = findPersonByIdUseCase.execute(id);
        return personDataTransferMapper.toPersonOutput(person);
    }

    @QueryMapping()
    PersonOutput personByEmail(@Argument String email) {
        var person = findPersonByEmailUseCase.execute(email);
        return personDataTransferMapper.toPersonOutput(person);
    }
}

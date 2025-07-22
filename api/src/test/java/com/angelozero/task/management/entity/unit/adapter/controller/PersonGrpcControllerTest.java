package com.angelozero.task.management.entity.unit.adapter.controller;

import com.angelozero.task.management.adapter.controller.PersonGrpcController;
import com.angelozero.task.management.adapter.controller.datatransfer.PersonInput;
import com.angelozero.task.management.adapter.controller.datatransfer.PersonOutput;
import com.angelozero.task.management.adapter.controller.mapper.PersonDataTransferMapper;
import com.angelozero.task.management.entity.Person;
import com.angelozero.task.management.usecase.services.person.FindPersonByEmailUseCase;
import com.angelozero.task.management.usecase.services.person.FindPersonByIdUseCase;
import com.angelozero.task.management.usecase.services.person.SavePersonUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PersonGrpcControllerTest {

    @Mock
    private PersonDataTransferMapper personDataTransferMapper;

    @Mock
    private SavePersonUseCase savePersonUseCase;

    @Mock
    private FindPersonByIdUseCase findPersonByIdUseCase;

    @Mock
    private FindPersonByEmailUseCase findPersonByEmailUseCase;

    @InjectMocks
    private PersonGrpcController personGrpcController;

    @Test
    @DisplayName("Should save person with success")
    public void shouldSavePersonWithSuccess() {
        var personInput = new PersonInput("name", "email", "profileInfo", Collections.emptyList());
        var personToSave = new Person("1", "name", "email", "profileInfo", Collections.emptyList());
        var savedPerson = new Person("1", "name", "email", "profileInfo", Collections.emptyList());
        var personOutput = new PersonOutput("id", "name", "email", "profileInfo", Collections.emptyList());

        when(personDataTransferMapper.toPerson(personInput)).thenReturn(personToSave);
        doNothing().when(savePersonUseCase).execute(personToSave);
        when(findPersonByEmailUseCase.execute(personInput.email())).thenReturn(savedPerson);
        when(personDataTransferMapper.toPersonOutput(savedPerson)).thenReturn(personOutput);

        var result = personGrpcController.savePerson(personInput);

        assertNotNull(result);
        assertEquals(personOutput, result);
        verify(personDataTransferMapper).toPerson(personInput);
        verify(savePersonUseCase).execute(personToSave);
        verify(findPersonByEmailUseCase).execute(personInput.email());
        verify(personDataTransferMapper).toPersonOutput(savedPerson);
    }

    @Test
    @DisplayName("Should find person by id with success")
    public void shouldFindPersonByIdWithSuccess() {
        var id = "123";
        var person = new Person("1", "name", "email", "profileInfo", Collections.emptyList());
        var personOutput = new PersonOutput("id", "name", "email", "profileInfo", Collections.emptyList());

        when(findPersonByIdUseCase.execute(id)).thenReturn(person);
        when(personDataTransferMapper.toPersonOutput(person)).thenReturn(personOutput);

        var result = personGrpcController.personById(id);

        assertNotNull(result);
        assertEquals(personOutput, result);
        verify(findPersonByIdUseCase).execute(id);
        verify(personDataTransferMapper).toPersonOutput(person);
    }

    @Test
    @DisplayName("Should find person by email with success")
    public void shouldFindPersonByEmailWithSuccess() {
        var email = "email";
        var person = new Person("1", "name", "email", "profileInfo", Collections.emptyList());
        var personOutput = new PersonOutput("id", "name", "email", "profileInfo", Collections.emptyList());

        when(findPersonByEmailUseCase.execute(email)).thenReturn(person);
        when(personDataTransferMapper.toPersonOutput(person)).thenReturn(personOutput);

        var result = personGrpcController.personByEmail(email);

        assertNotNull(result);
        assertEquals(personOutput, result);
        verify(findPersonByEmailUseCase).execute(email);
        verify(personDataTransferMapper).toPersonOutput(person);
    }
}
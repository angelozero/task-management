package com.angelozero.task.management.entity.unit.usecase.person;

import com.angelozero.task.management.entity.Person;
import com.angelozero.task.management.entity.Task;
import com.angelozero.task.management.entity.status.Completed;
import com.angelozero.task.management.usecase.exception.BusinessException;
import com.angelozero.task.management.usecase.gateway.PersonGateway;
import com.angelozero.task.management.usecase.services.person.FindPersonByIdUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FindPersonByIdUseCaseTest {

    @Mock
    private PersonGateway personGateway;

    @InjectMocks
    private FindPersonByIdUseCase findPersonByIdUseCase;

    @Test
    @DisplayName("Should find a person by id with success")
    public void shouldFindPersonByIdWithSuccess() {
        var taskListMock = List.of(new Task("id", "description", true, new Completed()));
        var personMock = new Person("", "name", "email", "profileInfo", taskListMock);

        when(personGateway.findById(anyString())).thenReturn(personMock);

        var response = findPersonByIdUseCase.execute("person-id");

        assertNotNull(response);
    }

    @Test
    @DisplayName("Should not find a person by id")
    public void shouldNotFindPersonById() {
        when(personGateway.findById(anyString())).thenReturn(null);

        var response = findPersonByIdUseCase.execute("person-id");

        assertNull(response);
    }

    @Test
    @DisplayName("Should fail to find a person without id")
    public void shouldFailToFindPersonWithoutId() {
        var exception = assertThrows(BusinessException.class,
                () -> findPersonByIdUseCase.execute(null));

        verify(personGateway, never()).findById(anyString());

        assertNotNull(exception);
        assertEquals("No Person ID was informed to be found", exception.getMessage());
    }
}

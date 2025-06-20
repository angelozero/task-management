package com.angelozero.task.management.entity.unit.usecase.person;

import com.angelozero.task.management.entity.Person;
import com.angelozero.task.management.entity.Task;
import com.angelozero.task.management.entity.status.Completed;
import com.angelozero.task.management.usecase.exception.BusinessException;
import com.angelozero.task.management.usecase.gateway.PersonGateway;
import com.angelozero.task.management.usecase.services.person.FindPersonByEmailUseCase;
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
public class FindPersonByEmailUseCaseTest {

    @Mock
    private PersonGateway personGateway;

    @InjectMocks
    private FindPersonByEmailUseCase findPersonByEmailUseCase;

    @Test
    @DisplayName("Should find a person by email with success")
    public void shouldFindPersonByEmailWithSuccess() {
        var taskListMock = List.of(new Task("id", "description", true, new Completed()));
        var personMock = new Person("", "name", "email", "profileInfo", taskListMock);

        when(personGateway.findByEmail(anyString())).thenReturn(personMock);

        var response = findPersonByEmailUseCase.execute("email");

        assertNotNull(response);
    }

    @Test
    @DisplayName("Should not find a person by email")
    public void shouldNotFindPersonByEmail() {
        when(personGateway.findByEmail(anyString())).thenReturn(null);

        var response = findPersonByEmailUseCase.execute("email");

        assertNull(response);
    }

    @Test
    @DisplayName("Should fail to find a person without email")
    public void shouldFailToFindPersonWithoutEmail() {
        var exception = assertThrows(BusinessException.class,
                () -> findPersonByEmailUseCase.execute(null));

        verify(personGateway, never()).findByEmail(anyString());

        assertNotNull(exception);
        assertEquals("No Person email was informed to be found", exception.getMessage());
    }
}

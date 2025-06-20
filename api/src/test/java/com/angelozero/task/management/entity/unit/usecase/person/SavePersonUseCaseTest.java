package com.angelozero.task.management.entity.unit.usecase.person;

import com.angelozero.task.management.entity.Person;
import com.angelozero.task.management.entity.Task;
import com.angelozero.task.management.entity.status.Completed;
import com.angelozero.task.management.usecase.exception.BusinessException;
import com.angelozero.task.management.usecase.gateway.PersonGateway;
import com.angelozero.task.management.usecase.services.person.SavePersonUseCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SavePersonUseCaseTest {

    @Mock
    private PersonGateway personGateway;

    @InjectMocks
    private SavePersonUseCase savePersonUseCase;

    @Test
    @DisplayName("Should save a Person with success")
    public void shouldSavePersonWithSuccess() {
        var taskListMock = List.of(new Task("id", "description", true, new Completed()));
        var personMock = new Person("", "name", "email", "profileInfo", taskListMock);

        doNothing().when(personGateway).save(any(Person.class));

        Assertions.assertDoesNotThrow(() -> savePersonUseCase.execute(personMock));
    }

    @Test
    @DisplayName("Should fail to save a person without data")
    public void shouldFailToSavePersonWithoutData() {
        var exception = assertThrows(BusinessException.class,
                () -> savePersonUseCase.execute(null));

        verify(personGateway, never()).save(any(Person.class));

        assertNotNull(exception);
        assertEquals("No Person data was informed to be saved", exception.getMessage());
    }
}

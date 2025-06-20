package com.angelozero.task.management.entity.unit.dataprovider;

import com.angelozero.task.management.adapter.dataprovider.PersonByMongoDataProvider;
import com.angelozero.task.management.adapter.dataprovider.jpa.document.PersonEntity;
import com.angelozero.task.management.adapter.dataprovider.jpa.document.TaskEntity;
import com.angelozero.task.management.adapter.dataprovider.jpa.repository.mongo.PersonRepository;
import com.angelozero.task.management.adapter.dataprovider.jpa.repository.mongo.TaskRepository;
import com.angelozero.task.management.adapter.dataprovider.mapper.PersonDataProviderMapper;
import com.angelozero.task.management.adapter.dataprovider.mapper.TaskDataProviderMapper;
import com.angelozero.task.management.entity.Person;
import com.angelozero.task.management.entity.Task;
import com.angelozero.task.management.entity.status.Completed;
import com.angelozero.task.management.usecase.exception.DataBaseDataProviderException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PersonByMongoDataProviderTest {

    @Mock
    private PersonRepository personRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private PersonDataProviderMapper personDataProviderMapper;

    @Mock
    private TaskDataProviderMapper taskDataProviderMapper;

    @InjectMocks
    private PersonByMongoDataProvider personByMongoDataProvider;

    @Test
    @DisplayName("Should save a Person with success")
    void shouldSavePersonWithSuccess() {
        var personEntityMock = getPeronEntityMock();
        var tasEntityListMock = getTaskEntityListMock();
        var personMock = getPersonMock();

        when(taskDataProviderMapper.toTaskEntityList(anyList())).thenReturn(tasEntityListMock);
        when(taskRepository.saveAll(anyList())).thenReturn(tasEntityListMock);
        when(personDataProviderMapper.toPersonEntity(any(), any())).thenReturn(personEntityMock);
        when(personRepository.save(any())).thenReturn(personEntityMock);

        assertDoesNotThrow(() -> personByMongoDataProvider.save(personMock));
    }

    @Test
    @DisplayName("Should save a Person without a list with success")
    void shouldSavePersonWithoutListWithSuccess() {
        var personEntityMock = getPeronEntityMock();
        var personMock = getPersonMock();

        when(taskDataProviderMapper.toTaskEntityList(anyList())).thenReturn(null);

        when(personDataProviderMapper.toPersonEntity(any(), any())).thenReturn(personEntityMock);
        when(personRepository.save(any())).thenReturn(personEntityMock);

        assertDoesNotThrow(() -> personByMongoDataProvider.save(personMock));

        verify(taskRepository, never()).saveAll(anyList());
    }

    @Test
    @DisplayName("Should save a Person with a empty list with success")
    void shouldSavePersonWithEmptyListListWithSuccess() {
        var personEntityMock = getPeronEntityMock();
        var personMock = getPersonMock();

        when(taskDataProviderMapper.toTaskEntityList(anyList())).thenReturn(Collections.emptyList());

        when(personDataProviderMapper.toPersonEntity(any(), any())).thenReturn(personEntityMock);
        when(personRepository.save(any())).thenReturn(personEntityMock);

        assertDoesNotThrow(() -> personByMongoDataProvider.save(personMock));

        verify(taskRepository, never()).saveAll(anyList());
    }

    @Test
    @DisplayName("Should find a Person by id with success")
    void shouldFindPersonByIdWithSuccess() {
        var personEntityMock = getPeronEntityMock();
        var tasEntityListMock = getTaskEntityListMock();
        var taskMock = getTaskMock();
        var personMock = new Person("1",
                "name",
                "email",
                "profile-info",
                List.of(taskMock));


        when(personRepository.findById(anyString())).thenReturn(Optional.of(personEntityMock));
        when(taskRepository.findByIdIn(anyList())).thenReturn(tasEntityListMock);
        when(personDataProviderMapper.toPerson(any(), any())).thenReturn(personMock);

        var response = personByMongoDataProvider.findById("id");

        assertNotNull(response);
    }

    @Test
    @DisplayName("Should not find a Person by id with success")
    void shouldNotFindPersonByIdWithSuccess() {
        when(personRepository.findById(anyString())).thenReturn(Optional.empty());
        when(personDataProviderMapper.toPerson(any(), any())).thenReturn(null);

        var response = personByMongoDataProvider.findById("id");

        assertNull(response);

        verify(taskRepository, never()).findByIdIn(any());
    }

    @Test
    @DisplayName("Should find a Person by email with success")
    void shouldFindPersonByEmailWithSuccess() {
        var personEntityMock = getPeronEntityMock();
        var tasEntityListMock = getTaskEntityListMock();
        var personMock = getPersonMock();


        when(personRepository.findByEmail(anyString())).thenReturn(personEntityMock);
        when(taskRepository.findByIdIn(anyList())).thenReturn(tasEntityListMock);
        when(personDataProviderMapper.toPerson(any(), any())).thenReturn(personMock);

        var response = personByMongoDataProvider.findByEmail("email");

        assertNotNull(response);
    }

    @Test
    @DisplayName("Should not find a Person by email with success")
    void shouldNotFindPersonByEmailWithSuccess() {
        when(personRepository.findByEmail(anyString())).thenReturn(null);
        when(personDataProviderMapper.toPerson(any(), any())).thenReturn(null);

        var response = personByMongoDataProvider.findByEmail("email");

        assertNull(response);

        verify(taskRepository, never()).findByIdIn(any());
    }

    @Test
    @DisplayName("Should update a Person with success")
    void shouldUpdatePersonWithSuccess() {
        var personEntityMock = getPeronEntityMock();
        var tasEntityListMock = getTaskEntityListMock();
        var personMock = getPersonMock();

        when(taskDataProviderMapper.toTaskEntityList(anyList())).thenReturn(tasEntityListMock);
        when(taskRepository.saveAll(any())).thenReturn(tasEntityListMock);
        when(personDataProviderMapper.toPersonEntity(any(), any())).thenReturn(personEntityMock);
        when(personRepository.save(any())).thenReturn(personEntityMock);
        when(taskRepository.findByIdIn(anyList())).thenReturn(tasEntityListMock);
        when(personDataProviderMapper.toPerson(any(), any())).thenReturn(personMock);

        var response = personByMongoDataProvider.update(personMock);

        assertNotNull(response);
    }

    @Test
    @DisplayName("Should delete a Person with success")
    void shouldDeletePersonWithSuccess() {
        var personEntityMock = getPeronEntityMock();
        var personMock = getPersonMock();

        when(personDataProviderMapper.toPersonEntity(any(), any())).thenReturn(personEntityMock);
        doNothing().when(taskRepository).deleteAllById(any());
        doNothing().when(personRepository).delete(any());

        assertDoesNotThrow(() -> personByMongoDataProvider.delete(personMock));
    }

    @Test
    @DisplayName("Should fail to get all tasks")
    void shouldFailToGetAllTasks() {
        var errorMessage = "Fail to get all person tasks - fail: test fail all tasks id in";

        when(personRepository.findById(any())).thenReturn(Optional.of(getPeronEntityMock()));
        when(taskRepository.findByIdIn(any())).thenThrow(new RuntimeException("test fail all tasks id in"));

        var exception = assertThrows(DataBaseDataProviderException.class, () -> personByMongoDataProvider.findById("id"));

        assertNotNull(exception);
        assertEquals(errorMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Should fail to save a person - fail to save all tasks")
    void shouldFailToSavePersonFailToSaveAllTasks() {
        var personMock = getPersonMock();
        var errorMessage = "Fail to save all person tasks - fail: test fail save all tasks";

        when(taskDataProviderMapper.toTaskEntityList(any())).thenReturn(getTaskEntityListMock());
        when(taskRepository.saveAll(any())).thenThrow(new RuntimeException("test fail save all tasks"));

        var exception = assertThrows(DataBaseDataProviderException.class, () -> personByMongoDataProvider.save(personMock));

        assertNotNull(exception);
        assertEquals(errorMessage, exception.getMessage());

        verify(personDataProviderMapper, never()).toPersonEntity(any(), any());
        verify(personRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should fail to save a person")
    void shouldFailToSavePerson() {
        var personMock = getPersonMock();
        var errorMessage = "Fail to save a person - fail: test fail save a person";

        when(taskDataProviderMapper.toTaskEntityList(any())).thenReturn(getTaskEntityListMock());
        when(taskRepository.saveAll(any())).thenReturn(getTaskEntityListMock());
        when(personDataProviderMapper.toPersonEntity(any(), any())).thenReturn(getPeronEntityMock());
        when(personRepository.save(any())).thenThrow(new RuntimeException("test fail save a person"));

        var exception = assertThrows(DataBaseDataProviderException.class, () -> personByMongoDataProvider.save(personMock));

        assertNotNull(exception);
        assertEquals(errorMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Should fail to find a person by id")
    void shouldFailToFindPersonById() {
        var errorMessage = "Fail to find a person by id test-id - fail: test fail find person by id";

        when(personRepository.findById(any())).thenThrow(new RuntimeException("test fail find person by id"));

        var exception = assertThrows(DataBaseDataProviderException.class, () -> personByMongoDataProvider.findById("test-id"));

        assertNotNull(exception);
        assertEquals(errorMessage, exception.getMessage());

        verify(taskRepository, never()).findByIdIn(any());
    }

    @Test
    @DisplayName("Should fail to find a person by email")
    void shouldFailToFindPersonByEmail() {
        var errorMessage = "Fail to find a person by email test-email - fail: test fail find person by email";

        when(personRepository.findByEmail(any())).thenThrow(new RuntimeException("test fail find person by email"));

        var exception = assertThrows(DataBaseDataProviderException.class, () -> personByMongoDataProvider.findByEmail("test-email"));

        assertNotNull(exception);
        assertEquals(errorMessage, exception.getMessage());

        verify(taskRepository, never()).findByIdIn(any());
    }

    @Test
    @DisplayName("Should fail to update person - fail update all tasks")
    void shouldFailUpdatePersonFailUpdateAllTasks() {
        var personMock = getPersonMock();
        var errorMessage = "Fail to update all person tasks - fail: test fail update all tasks";

        when(taskDataProviderMapper.toTaskEntityList(any())).thenReturn(getTaskEntityListMock());
        when(taskRepository.saveAll(any())).thenThrow(new RuntimeException("test fail update all tasks"));

        var exception = assertThrows(DataBaseDataProviderException.class, () -> personByMongoDataProvider.update(personMock));

        assertNotNull(exception);
        assertEquals(errorMessage, exception.getMessage());

        verify(personDataProviderMapper, never()).toPersonEntity(any(), any());
        verify(personRepository, never()).save(any());
        verify(taskRepository, never()).findByIdIn(any());
        verify(personDataProviderMapper, never()).toPerson(any(), any());
    }

    @Test
    @DisplayName("Should fail to update person")
    void shouldFailUpdatePerson() {
        var personMock = getPersonMock();
        var errorMessage = "Fail to update a person - fail: test fail update person";

        when(taskDataProviderMapper.toTaskEntityList(any())).thenReturn(getTaskEntityListMock());
        when(taskRepository.saveAll(any())).thenReturn(getTaskEntityListMock());
        when(personDataProviderMapper.toPersonEntity(any(), any())).thenReturn(getPeronEntityMock());
        when(personRepository.save(any())).thenThrow(new RuntimeException("test fail update person"));

        var exception = assertThrows(DataBaseDataProviderException.class, () -> personByMongoDataProvider.update(personMock));

        assertNotNull(exception);
        assertEquals(errorMessage, exception.getMessage());

        verify(taskRepository, never()).findByIdIn(any());
        verify(personDataProviderMapper, never()).toPerson(any(), any());
    }

    @Test
    @DisplayName("Should fail to delete a person - delete task")
    void shouldFailToDeletePersonDeleteTask() {
        var personMock = getPersonMock();
        var errorMessage = "Fail to delete all person tasks - fail: test delete all tasks";

        when(personDataProviderMapper.toPersonEntity(any(), any())).thenReturn(getPeronEntityMock());
        doThrow(new RuntimeException("test delete all tasks")).when(taskRepository).deleteAllById(any());

        var exception = assertThrows(DataBaseDataProviderException.class, () -> personByMongoDataProvider.delete(personMock));

        assertNotNull(exception);
        assertEquals(errorMessage, exception.getMessage());

        verify(personRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Should fail to delete a person")
    void shouldFailToDeletePerson() {
        var personMock = getPersonMock();
        var errorMessage = "Fail to delete a person - fail: test delete person";

        when(personDataProviderMapper.toPersonEntity(any(), any())).thenReturn(getPeronEntityMock());
        doNothing().when(taskRepository).deleteAllById(any());
        doThrow(new RuntimeException("test delete person")).when(personRepository).delete(any());

        var exception = assertThrows(DataBaseDataProviderException.class, () -> personByMongoDataProvider.delete(personMock));

        assertNotNull(exception);
        assertEquals(errorMessage, exception.getMessage());
    }

    private PersonEntity getPeronEntityMock() {
        return new PersonEntity("1",
                "name",
                "email",
                "profile-info",
                List.of("1"));
    }

    private Person getPersonMock() {
        return new Person("1",
                "name",
                "email",
                "profile-info",
                List.of(getTaskMock()));
    }

    private List<TaskEntity> getTaskEntityListMock() {
        return List.of(new TaskEntity("1", "description", true, "statusTYpe", 0));
    }

    private Task getTaskMock() {
        return new Task("1", "description", true, new Completed());
    }
}

package com.angelozero.task.management.adapter.dataprovider;

import com.angelozero.task.management.adapter.dataprovider.jpa.document.PersonEntity;
import com.angelozero.task.management.adapter.dataprovider.jpa.document.TaskEntity;
import com.angelozero.task.management.adapter.dataprovider.jpa.repository.mongo.PersonRepository;
import com.angelozero.task.management.adapter.dataprovider.jpa.repository.mongo.TaskRepository;
import com.angelozero.task.management.adapter.dataprovider.mapper.PersonDataProviderMapper;
import com.angelozero.task.management.adapter.dataprovider.mapper.TaskDataProviderMapper;
import com.angelozero.task.management.entity.Person;
import com.angelozero.task.management.entity.Task;
import com.angelozero.task.management.usecase.exception.DataBaseDataProviderException;
import com.angelozero.task.management.usecase.gateway.PersonGateway;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class PersonByMongoDataProvider implements PersonGateway {

    private final PersonRepository personRepository;
    private final TaskRepository taskRepository;
    private final PersonDataProviderMapper personDataProviderMapper;
    private final TaskDataProviderMapper taskDataProviderMapper;

    @Override
    public void save(Person person) {
        List<String> taskEntityIdsList = null;
        var taskEntityList = taskDataProviderMapper.toTaskEntityList(person.taskList());

        if (taskEntityList != null && !taskEntityList.isEmpty()) {
            try {
                var taskEntitySavedList = taskRepository.saveAll(taskEntityList);
                taskEntityIdsList = taskEntitySavedList.stream().map(TaskEntity::id).toList();

            } catch (Exception ex) {
                log.error("Fail to save all person tasks - fail: {}", ex.getMessage());
                throw new DataBaseDataProviderException("Fail to save all person tasks - fail: " + ex.getMessage());
            }
        }

        var personEntity = personDataProviderMapper.toPersonEntity(person, taskEntityIdsList);

        try {
            personRepository.save(personEntity);

        } catch (Exception ex) {
            log.error("Fail to save a person - fail: {}", ex.getMessage());
            throw new DataBaseDataProviderException("Fail to save a person - fail: " + ex.getMessage());
        }
    }

    @Override
    public Person findById(String id) {
        PersonEntity personEntity = null;
        try {
            personEntity = personRepository.findById(id).orElse(null);

        } catch (Exception ex) {
            log.error("Fail to find a person by id {} - fail: {}", id, ex.getMessage());
            throw new DataBaseDataProviderException("Fail to find a person by id " + id + " - fail: " + ex.getMessage());
        }

        var taskEntityList = getTaskEntityList(personEntity);

        return personDataProviderMapper.toPerson(personEntity, taskEntityList);
    }

    @Override
    public Person findByEmail(String email) {
        PersonEntity personEntity = null;
        try {
            personEntity = personRepository.findByEmail(email);

        } catch (Exception ex) {
            log.error("Fail to find a person by email {} - fail: {}", email, ex.getMessage());
            throw new DataBaseDataProviderException("Fail to find a person by email " + email + " - fail: " + ex.getMessage());
        }

        var taskEntityList = getTaskEntityList(personEntity);

        return personDataProviderMapper.toPerson(personEntity, taskEntityList);
    }


    @Override
    public Person update(Person person) {
        List<String> taskEntityIdsList = null;
        var taskEntityList = taskDataProviderMapper.toTaskEntityList(person.taskList());

        if (taskEntityList != null && !taskEntityList.isEmpty()) {
            try {
                var taskEntitySavedList = taskRepository.saveAll(taskEntityList);
                taskEntityIdsList = taskEntitySavedList.stream().map(TaskEntity::id).toList();

            } catch (Exception ex) {
                log.error("Fail to update all person tasks - fail: {}", ex.getMessage());
                throw new DataBaseDataProviderException("Fail to update all person tasks - fail: " + ex.getMessage());
            }
        }

        var personEntity = personDataProviderMapper.toPersonEntity(person, taskEntityIdsList);

        PersonEntity personEntityUpdated = null;
        try {
            personEntityUpdated = personRepository.save(personEntity);

        } catch (Exception ex) {
            log.error("Fail to update a person - fail: {}", ex.getMessage());
            throw new DataBaseDataProviderException("Fail to update a person - fail: " + ex.getMessage());
        }

        var taskEntities = getTaskEntityList(personEntityUpdated);

        return personDataProviderMapper.toPerson(personEntityUpdated, taskEntities);
    }

    @Override
    public void delete(Person person) {
        var taskIdList = person.taskList() != null ? person.taskList().stream().map(Task::id).toList() : null;
        var personEntity = personDataProviderMapper.toPersonEntity(person, taskIdList);

        try {
            taskRepository.deleteAllById(personEntity.taskIdsList());

        } catch (Exception ex) {
            log.error("Fail to delete all person tasks - fail: {}", ex.getMessage());
            throw new DataBaseDataProviderException("Fail to delete all person tasks - fail: " + ex.getMessage());
        }

        try {
            personRepository.delete(personEntity);

        } catch (Exception ex) {
            log.error("Fail to delete a person - fail: {}", ex.getMessage());
            throw new DataBaseDataProviderException("Fail to delete a person - fail: " + ex.getMessage());
        }
    }

    private List<TaskEntity> getTaskEntityList(PersonEntity personEntity) {
        try {
            return personEntity != null ? taskRepository.findByIdIn(personEntity.taskIdsList()) : null;

        } catch (Exception ex) {
            log.error("Fail to get all person tasks - fail: {}", ex.getMessage());
            throw new DataBaseDataProviderException("Fail to get all person tasks - fail: " + ex.getMessage());
        }
    }
}

package com.angelozero.task.management.entity.integration.config;

import com.angelozero.task.management.adapter.dataprovider.jpa.document.PersonEntity;
import com.angelozero.task.management.adapter.dataprovider.jpa.document.TaskEntity;
import com.angelozero.task.management.adapter.dataprovider.jpa.repository.mongo.PersonRepository;
import com.angelozero.task.management.adapter.dataprovider.jpa.repository.mongo.TaskRepository;
import com.angelozero.task.management.entity.Person;
import lombok.extern.slf4j.Slf4j;
import org.junit.ClassRule;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.MongoDBContainer;

@Slf4j
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableConfigurationProperties
@ExtendWith(SpringExtension.class)
@ContextConfiguration(initializers = PersonIntegrationTestConfig.Initializer.class)
public class PersonIntegrationTestConfig {

    /**
     * Test Container - MongoDB
     */
    @ClassRule
    public static MongoDBContainer container = new MongoDBContainer("mongo:4.4")
            .withExposedPorts(27017);


    /**
     * Configuration initializer - application.properties
     */
    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    "spring.data.mongodb.uri=" + container.getConnectionString() + "/task-management-data-base-test",
                    "spring.data.mongodb.host=" + container.getHost(),
                    "spring.data.mongodb.port=" + container.getFirstMappedPort()
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }

    @Autowired
    protected PersonRepository personRepository;

    @Autowired
    protected TaskRepository taskRepository;

    public PersonEntity findPersonById(String id) {
        return personRepository.findById(id).orElse(null);
    }

    public PersonEntity savePerson(Person person) {
        var tasksEntityList = taskRepository.saveAll(person.taskList()
                .stream()
                .map(task -> new TaskEntity(null, task.description(), task.completed(), task.status().getName(), task.status().getCode()))
                .toList());
        var tasks = tasksEntityList.stream().map(TaskEntity::id).toList();
        return personRepository.save(new PersonEntity(null, person.name(), person.email(), person.profileInfo(), tasks));
    }

    public void deletePersonData() {
        taskRepository.deleteAll();
        personRepository.deleteAll();
        log.info("[PersonIntegrationTestConfig] - Person data deleted with success");
    }
}

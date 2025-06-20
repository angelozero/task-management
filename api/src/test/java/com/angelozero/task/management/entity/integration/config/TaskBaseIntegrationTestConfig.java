package com.angelozero.task.management.entity.integration.config;

import com.angelozero.task.management.adapter.dataprovider.jpa.document.TaskEntity;
import com.angelozero.task.management.adapter.dataprovider.jpa.repository.mongo.TaskRepository;
import com.angelozero.task.management.entity.status.EventStatusTask;
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
@ContextConfiguration(initializers = TaskBaseIntegrationTestConfig.Initializer.class)
public class TaskBaseIntegrationTestConfig {

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
    protected TaskRepository repository;

    public TaskEntity findTaskById(String id) {
        return repository.findById(id).orElse(null);
    }

    public TaskEntity saveTask(String description, Boolean isCompleted, EventStatusTask status) {
        return repository.save(new TaskEntity(null, description, isCompleted, status.getName(), status.getCode()));
    }

    public void deleteTaskData() {
        repository.deleteAll();
        log.info("[TaskIntegrationTestConfig] - Task data deleted with success");
    }
}

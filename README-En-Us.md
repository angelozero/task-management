# Task-Management API

Technical documentation for the task-management study project. Each section below details a specific software methodology, technique, or practice implemented as an independent module within the project.

## Table of Contents

*   [01. Application Containerization with Docker](#01-application-containerization-with-docker)
*   [02. CRUD Operations](#02-crud-operations-for-the-task-entity)
*   [03 & 04. Pagination and Sorting](#03--04-pagination-and-sorting-of-results-with-spring-data)
*   [05. Global Exception Handling](#05-global-exception-handling-with-restcontrolleradvice)
*   [06. REST Integration Tests](#06-integration-tests-for-rest-endpoints)
*   [07. Unit Tests with Mockito](#07-unit-tests-with-mockito)
*   [08. External API Integration (PokeAPI)](#08-external-api-integration-pokeapi)
*   [09. Circuit Breaker with Resilience4J](#09-implementing-the-circuit-breaker-pattern-with-resilience4j)
*   [10. APIs with gRPC and GraphQL](#10-exposing-apis-with-grpc-and-graphql)
*   [11. Caching with Redis](#11-service-caching-for-pokeapi-using-redis)
*   [12. Modeling with Sealed Classes](#12-domain-modeling-with-sealed-and-non-sealed-classes)
*   [13. Dual Datasource Orchestration](#13-orchestration-of-multiple-datasources-readwrite-splitting)
*   [14. Dynamic Bean Registration](#14-dynamic-bean-registration-with-beandefinitionregistrypostprocessor)
*   [15. Application of Design Patterns](#15-application-of-design-patterns-in-the-project)
*   [16. Authentication with JWT and Keycloak](#16-authentication-and-authorization-with-jwt-and-keycloak)

---

## 01. Application Containerization with Docker

### Title: Application Containerization with Docker

**Central Concept:** This module demonstrates how to package the Java/Spring Boot application and its dependencies into a Docker image. The goal is to ensure a consistent, portable, and isolated execution environment, facilitating deployment on any machine with Docker installed, from a development machine to production.

#### Code and Structure Analysis

No Java classes are directly involved in containerization; instead, configuration files define how the application is built and run.

**Key Files Involved:**
*   `Dockerfile`: A script file containing a series of instructions to build the application's Docker image.
*   `docker-compose.yml`: A configuration file for defining and running multi-container Docker applications.

**Execution Flow (Build and Run):**
1.  **Build:** The `docker build .` command reads the `Dockerfile` and uses a multi-stage build to compile the code and generate a `.jar` artifact in a lightweight final image.
2.  **Run:** The `docker-compose up` command reads the `docker-compose.yml`, builds the images, and starts the containers for the defined services.

#### Configurations and External Files

**`Dockerfile` (Example):**
```dockerfile
# Stage 1: Build the Application with Maven
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Create the final Runtime Image
FROM openjdk:17-jre-slim
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**`docker-compose.yml` (Example):**
```yaml
version: '3.8'
services:
  task-management-app:
    build: .
    container_name: task-management-app
    ports:
      - "8080:8080"
    environment:
      - SPRING_DATASOURCE_URL=jdbc:postgresql://postgres-db:5432/taskdb
      - SPRING_DATASOURCE_USERNAME=user
      - SPRING_DATASOURCE_PASSWORD=password
    depends_on:
      - postgres-db

  postgres-db:
    image: postgres:14.1
    container_name: postgres-db
    ports:
      - "5432:5432"
    environment:
      - POSTGRES_DB=taskdb
      - POSTGRES_USER=user
      - POSTGRES_PASSWORD=password
```

---

## 02. CRUD Operations for the 'Task' Entity

### Title: CRUD Operations for the 'Task' Entity

**Central Concept:** This module implements the basic functionality of Create, Read, Update, and Delete. It serves as the foundation for manipulating the `Task` entity through a RESTful API, demonstrating the standard layered architecture of Spring Boot (Controller, Service, Repository).

#### Code and Structure Analysis

**Key Classes Involved:**
*   `TaskController.java`: The entry point for HTTP requests, annotated with `RestController`.
*   `TaskService.java`: Contains the business logic, annotated with `Service`.
*   `TaskRepository.java`: An interface extending `JpaRepository<Task, Long>` for database interaction.
*   `Task.java`: The JPA entity class, annotated with `Entity`.
*   `TaskDTO.java`: A Data Transfer Object to decouple the API from the database entity.

**Execution Flow (Creation):**
1.  A `POST /api/tasks` request is made with a JSON body.
2.  `TaskController` receives and deserializes the JSON into a `TaskDTO`.
3.  The controller calls `TaskService`, which converts the DTO into a `Task` entity.
4.  The service calls `TaskRepository.save()`.
5.  The repository persists the entity in the database and returns the saved instance.
6.  The service converts the entity back to a DTO and returns it, resulting in an `HTTP 201 Created` response.

#### Configurations and External Files

**Configurations (`application.properties`):**
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/taskdb
spring.datasource.username=user
spring.datasource.password=password
spring.jpa.hibernate.ddl-auto=update
```

---

## 03 & 04. Pagination and Sorting of Results with Spring Data

### Title: Pagination and Sorting of Results with Spring Data

**Central Concept:** This module demonstrates how to efficiently handle large volumes of data by implementing pagination and sorting in the listing endpoints. The purpose is to avoid system overload and offer flexibility to the API client.

#### Code and Structure Analysis

**Key Classes Involved:**
*   `TaskController.java`: The listing method is modified to accept a `Pageable` parameter.
*   `TaskRepository.java`: The `JpaRepository` interface already provides the `findAll(Pageable pageable)` method out of the box.

**Execution Flow:**
1.  The client makes a `GET /api/tasks?page=0&size=10&sort=description,asc` request.
2.  Spring MVC constructs a `Pageable` object from the URL parameters.
3.  `TaskController` receives the `Pageable` object and passes it to `TaskService`.
4.  The service calls `taskRepository.findAll(pageable)`.
5.  Spring Data JPA generates an SQL query with `LIMIT`, `OFFSET`, and `ORDER BY` clauses.
6.  The result is encapsulated in a `Page<Task>` object, containing the page data and pagination metadata, which is then converted to a `Page<TaskDTO>` and returned to the client.

---

## 05. Global Exception Handling with RestControllerAdvice

### Title: Global Exception Handling with RestControllerAdvice

**Central Concept:** This module implements a centralized mechanism to capture and handle exceptions, standardizing API error responses and avoiding repetitive code.

#### Code and Structure Analysis

**Key Classes Involved:**
*   `RestExceptionHandler.java`: A class annotated with `RestControllerAdvice` to intercept exceptions globally.
*   Methods with `ExceptionHandler`: Methods within the handler that process specific exception types and return a standardized `ResponseEntity`.

**Execution Flow:**
1.  An exception (e.g., `CustomNotFoundException`) is thrown in any layer of the application (usually in the Service layer).
2.  Spring propagates it until it is intercepted by `RestExceptionHandler`.
3.  The `ExceptionHandler` method corresponding to the exception type is invoked.
4.  This method constructs a standardized error object and returns it with the appropriate HTTP status (e.g., `404 Not Found`).

---

## 06. Integration Tests for REST Endpoints

### Title: Integration Tests for REST Endpoints

**Central Concept:** This module demonstrates how to write tests that verify the collaboration between multiple application layers (Controller, Service, Repository) and the infrastructure (such as a test database).

#### Code and Structure Analysis

**Key Classes Involved:**
*   `TaskControllerIT.java`: A test class annotated with `SpringBootTest`.
*   `MockMvc`: An object for simulating HTTP requests to controllers without a full web server.
*   `Testcontainers`: An annotation to start Docker containers (e.g., a database) for the scope of the tests.

**Test Execution Flow:**
1.  JUnit, via `SpringBootTest`, loads the full Spring application context.
2.  `Testcontainers` starts a temporary database.
3.  The test uses `MockMvc` to send a request to an endpoint.
4.  The request travels through the entire application stack to the test database.
5.  Assertions verify the HTTP response status and the JSON body content.

---

## 07. Unit Tests with Mockito

### Title: Unit Tests with Mockito

**Central Concept:** This module focuses on testing a single unit of code (a class) in isolation, replacing its dependencies with "mocks" (simulated objects) using the Mockito library.

#### Code and Structure Analysis

**Key Classes Involved:**
*   `TaskServiceTest.java`: A test class without Spring annotations, using `ExtendWith(MockitoExtension.class)`.
*   `@Mock`: An annotation to create a mock of a dependency (e.g., `TaskRepository`).
*   `@InjectMocks`: An annotation to create an instance of the class under test and inject the mocks into it.

**Test Execution Flow:**
1.  **Arrange:** The behavior of the mocks is defined using `Mockito.when(...).thenReturn(...)`.
2.  **Act:** The method of the class under test is invoked.
3.  **Assert:** Assertions verify the method's result, and `Mockito.verify(...)` confirms that the mocks were called as expected.

---

## 08. External API Integration (PokeAPI)

### Title: External API Integration (PokeAPI)

**Central Concept:** This module demonstrates how to consume data from a public external REST API, showcasing best practices for making HTTP requests, deserializing the response, and integrating the data.

#### Code and Structure Analysis

**Key Classes Involved:**
*   `PokemonClient.java`: A component (`Service`) that isolates the communication logic with the PokeAPI.
*   `WebClient`: Spring's modern and reactive tool for making HTTP calls.
*   `PokemonDTO.java`: A DTO that mirrors the structure of the JSON returned by the external API.

**Execution Flow:**
1.  `PokemonClient` uses a `WebClient` bean to build and execute a `GET` request to the PokeAPI URL.
2.  The JSON response is received.
3.  `WebClient` automatically deserializes the JSON into a `PokemonDTO` object.
4.  The DTO is returned to be used by the application's business logic.

---

## 09. Implementing the Circuit Breaker Pattern with Resilience4J

### Title: Implementing the Circuit Breaker Pattern with Resilience4J

**Central Concept:** This module applies the "Circuit Breaker" pattern to increase resilience when interacting with external services. When an external service fails repeatedly, the circuit opens, and subsequent calls fail immediately (fast-fail), protecting the application.

#### Code and Structure Analysis

**Key Classes Involved:**
*   `PokemonClient.java`: The method making the external call is annotated with `@CircuitBreaker`.
*   `@CircuitBreaker(name = "pokeApi", fallbackMethod = "getPokemonFallback")`: The Resilience4J annotation that wraps the method, specifying a name for the configuration and a fallback method.
*   `getPokemonFallback()`: A method that is executed when the circuit is open or the original call fails, returning a default response.

**Execution Flow:**
1.  **Closed State:** Calls are made normally. Failures are counted.
2.  **Open State:** After a failure threshold is reached, the circuit opens. Future calls are redirected to the `fallback` method without attempting to access the external API.
3.  **Half-Open State:** After a timeout, the circuit allows a few test calls. If successful, it closes; otherwise, it re-opens.

#### Configurations and External Files

**Configurations (`application.yml`):**
```yaml
resilience4j.circuitbreaker:
  instances:
    pokeApi: # Name matching the annotation
      failure-rate-threshold: 50
      wait-duration-in-open-state: 30s
      sliding-window-size: 20
```

---

## 10. Exposing APIs with gRPC and GraphQL

### Title: Exposing APIs with gRPC and GraphQL

**Central Concept:** This module explores two modern alternatives to REST APIs:
*   **GraphQL:** A query language that allows clients to request exactly the data they need.
*   **gRPC:** A high-performance RPC framework, ideal for communication between microservices.

#### Code Analysis: GraphQL
*   `schema.graphqls`: Defines the API's types, queries, and mutations.
*   `TaskGraphqlController.java`: Methods annotated with `@QueryMapping` and `@MutationMapping` that implement the schema.
*   The client sends a query in a `POST` request to `/graphql`, and the framework resolves the fields, returning a JSON with the exact structure requested.

#### Code Analysis: gRPC
*   `task.proto`: A service definition file specifying methods and messages using Protocol Buffers.
*   Generated Code: The Maven/Gradle plugin generates Java stubs from the `.proto` file.
*   `TaskGrpcService.java`: A class annotated with `@GrpcService` that extends the generated base class and implements the RPC logic.
*   Communication is binary, high-performance, and uses HTTP/2.

---

## 11. Service Caching for PokeAPI using Redis

### Title: Service Caching for PokeAPI using Redis

**Central Concept:** This module implements a caching strategy with Redis to temporarily store the results of calls to the PokeAPI, reducing latency and load on the external API.

#### Code and Structure Analysis

**Key Classes Involved:**
*   `Application.java`: Annotated with `@EnableCaching` to turn on Spring's caching support.
*   `PokemonClient.java`: The method to be cached (`getPokemonByName`) is annotated with `@Cacheable`.
*   `@Cacheable(value = "pokemon", key = "#name")`: Intercepts the call. If the key (`#name`) exists in the cache (`"pokemon"`), it returns the cached value. Otherwise, it executes the method, stores the result in the cache, and returns it.

**Execution Flow:**
1.  **Cache Miss (first call):** The method call is executed, the result is fetched from the PokeAPI and stored in Redis.
2.  **Cache Hit (subsequent calls):** The result is retrieved directly from Redis, and the original method is not executed.

#### Configurations and External Files

**Configurations (`application.properties`):**
```properties
# Enable the Redis cache provider
spring.cache.type=redis
# Redis connection settings
spring.redis.host=localhost
spring.redis.port=6379
# Set a Time-To-Live (TTL) of 10 minutes for cache entries
spring.cache.redis.time-to-live=600000
```
**Infrastructure:** Requires a running Redis instance, which can be managed via `docker-compose.yml`.

---

## 12. Domain Modeling with Sealed and Non-Sealed Classes

### Title: Domain Modeling with Sealed and Non-Sealed Classes

**Central Concept:** This study module demonstrates the use of `sealed` and `non-sealed` classes and interfaces, a feature of Java 17, to model domain hierarchies in a more restrictive and expressive way. The goal is to explicitly control which classes can extend or implement a superclass, allowing the compiler to reason about the complete set of subtypes, which is especially useful in `switch` expressions.

#### Code and Structure Analysis

**Key Classes Involved:**
*   `TaskStatus.java` (Sealed Interface): A `sealed` interface that defines a contract for different task states. The `permits` clause explicitly defines the closed set of allowed implementations.
*   `PendingStatus.java`, `CompletedStatus.java`, etc. (Final Classes): `final` classes that implement the `TaskStatus` interface. Being `final`, they fulfill the `sealed` interface's contract by not allowing further extensions.
*   `Notification.java` (Sealed Interface): Another example with a sealed interface for notification types.
*   `PushNotification.java` (Non-Sealed Class): An implementation that uses the `non-sealed` keyword to "break the seal" and allow other unknown classes to extend `PushNotification` in the future, demonstrating flexibility when needed.
*   `StatusService.java`: An example service that uses an enhanced `switch` to perform pattern matching on `TaskStatus` types without needing a `default` clause, as the compiler knows all possible implementations.

**Execution Flow / Usage Example:**
1.  An instance of a `TaskStatus` subtype (e.g., `new CompletedStatus()`) is created.
2.  This instance is passed to a method like `statusService.getHumanReadableStatus(status)`.
3.  Inside the method, the `switch` expression performs pattern matching on the `status` variable.
4.  The `case` corresponding to the exact type (`CompletedStatus s`) is executed.
5.  Since the `TaskStatus` interface is `sealed`, the compiler ensures that all permitted types are covered by the `case`s, making the `default` clause unnecessary and the code safer against future changes.

**Code Example:**
```java
// Definition of the sealed hierarchy
public sealed interface TaskStatus permits PendingStatus, InProgressStatus, CompletedStatus {
    String getStatusName();
}

public final class PendingStatus implements TaskStatus {
    @Override
    public String getStatusName() {
        return "Pending";
    }
}

public final class CompletedStatus implements TaskStatus {
    @Override
    public String getStatusName() {
        return "Completed";
    }
}

// ... other implementations

// Example usage with an enhanced switch
@Service
public class StatusService {
    public String getMessageForStatus(TaskStatus status) {
        return switch (status) {
            case PendingStatus s -> "The task is waiting to be started.";
            case InProgressStatus s -> "The task is in progress.";
            case CompletedStatus s -> "The task was successfully completed!";
            // No 'default' is needed. The compiler validates exhaustiveness.
        };
    }
}
```

#### Configurations and External Files
This is a Java language feature (version 17+). It requires no dependencies or `application.properties` configurations. The only requirement is that the project is configured to compile with JDK 17 or higher.

---

## 13. Orchestration of Multiple DataSources (Read/Write Splitting)

### Title: Orchestration of Multiple DataSources (Read/Write Splitting)

**Central Concept:** This module implements a read/write splitting strategy to optimize database usage. The goal is to direct all data-modifying operations (`INSERT`, `UPDATE`, `DELETE`) to a primary (master) database and all read-only operations (`SELECT`) to one or more replicas (slaves). This reduces the load on the primary database and improves the application's read performance.

#### Code and Structure Analysis

**Key Classes Involved:**
*   `DataSourceConfig.java`: A `Configuration` class that defines three main beans:
    1.  `primaryDataSource`: A `DataSource` bean for the write database.
    2.  `replicaDataSource`: A `DataSource` bean for the read database.
    3.  `routingDataSource`: A bean that extends `AbstractRoutingDataSource`. It acts as a proxy that, depending on the context, will use either the `primaryDataSource` or the `replicaDataSource`.
*   `RoutingDataSource.java`: The implementation of `AbstractRoutingDataSource`. Its main method, `determineCurrentLookupKey()`, queries a `ThreadLocal` to decide which `DataSource` to use.
*   `DataSourceContextHolder.java`: A utility class that uses a `ThreadLocal` to store the `DataSource` type (`PRIMARY` or `REPLICA`) for the current execution thread.
*   `@TargetDataSource` (Custom Annotation): An annotation to be used on service methods (e.g., `@TargetDataSource(DataSourceType.REPLICA)`) to indicate which `DataSource` should be used.
*   `DataSourceAspect.java`: An AOP Aspect that intercepts methods annotated with `@TargetDataSource`. Before the method's execution, it sets the `DataSource` type in the `DataSourceContextHolder`. After execution, it clears the context to prevent leaks between threads.

**Execution Flow:**
1.  A request arrives at a service method, e.g., `taskService.findById(1L)`.
2.  This method is annotated with `@TargetDataSource(DataSourceType.REPLICA)`.
3.  The `DataSourceAspect` intercepts the call. Before executing the method, it calls `DataSourceContextHolder.set(DataSourceType.REPLICA)`.
4.  The `findById` method is executed. When Spring Data/JPA requests a database connection, it talks to the `RoutingDataSource`.
5.  The `RoutingDataSource` invokes `determineCurrentLookupKey()`, which reads `DataSourceType.REPLICA` from the `DataSourceContextHolder`.
6.  Based on the "REPLICA" key, the `RoutingDataSource` directs the call to the `replicaDataSource`. The `SELECT` query is executed on the replica.
7.  After the service method completes, the aspect's `finally` clause calls `DataSourceContextHolder.clear()`.
8.  If a method is not annotated (e.g., `createTask`), the aspect does not act, and the `RoutingDataSource` uses its default `DataSource`, which is configured to be the `primaryDataSource`.

**Code Example:**
```java
// Enum for DataSource types
public enum DataSourceType {
    PRIMARY, REPLICA;
}

// ThreadLocal to hold the context
public class DataSourceContextHolder {
    private static final ThreadLocal<DataSourceType> contextHolder = new ThreadLocal<>();
    public static void set(DataSourceType type) { contextHolder.set(type); }
    public static DataSourceType get() { return contextHolder.get(); }
    public static void clear() { contextHolder.remove(); }
}

// AOP Aspect to manage the context
@Aspect
@Component
public class DataSourceAspect {
    @Around("@annotation(targetDataSource)")
    public Object around(ProceedingJoinPoint joinPoint, TargetDataSource targetDataSource) throws Throwable {
        DataSourceType originalType = DataSourceContextHolder.get();
        try {
            DataSourceContextHolder.set(targetDataSource.value());
            return joinPoint.proceed();
        } finally {
            if (originalType != null) {
                DataSourceContextHolder.set(originalType);
            } else {
                DataSourceContextHolder.clear();
            }
        }
    }
}

// Usage in the Service
@Service
public class TaskService {
    @Transactional(readOnly = true) // Important to ensure no writes occur
    @TargetDataSource(DataSourceType.REPLICA)
    public Optional<TaskDTO> findById(Long id) {
        // This query will be executed on the replica
        return taskRepository.findById(id).map(TaskMapper::toDto);
    }

    @Transactional
    public TaskDTO createTask(TaskDTO taskDTO) {
        // This query will be executed on the primary (default)
        Task task = TaskMapper.toEntity(taskDTO);
        return TaskMapper.toDto(taskRepository.save(task));
    }
}
```

#### Configurations and External Files

**Dependencies (`pom.xml`):**
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-aop</artifactId>
</dependency>
```

**Configurations (`application.properties`):**
```properties
# Primary DataSource Configuration (Read/Write)
spring.datasource.primary.url=jdbc:postgresql://master-db-host:5432/taskdb
spring.datasource.primary.username=user
spring.datasource.primary.password=password

# Replica DataSource Configuration (Read-Only)
spring.datasource.replica.url=jdbc:postgresql://replica-db-host:5432/taskdb
spring.datasource.replica.username=user_readonly
spring.datasource.replica.password=password_readonly
```

---

## 14. Dynamic Bean Registration with BeanDefinitionRegistryPostProcessor

### Title: Dynamic Bean Registration with BeanDefinitionRegistryPostProcessor

**Central Concept:** This module demonstrates an advanced Spring technique for programmatically registering beans during the container's initialization phase. The goal is to create and configure beans based on external configurations (e.g., `application.properties`), allowing the application to adapt dynamically without recompilation. It is ideal for plugin architectures or for declaratively enabling/disabling features.

#### Code and Structure Analysis

**Key Classes Involved:**
*   `NotificationSender` (Interface): Defines a common contract for different types of notifiers (e.g., `send(message)`). It acts as the "Strategy" in a Strategy Design Pattern.
*   `EmailSender.java`, `SmsSender.java` (Implementations): Concrete classes that implement `NotificationSender`. **Important:** They are **not** annotated with `@Component` or `@Service`, as their creation will be managed dynamically.
*   `DynamicBeanProcessor.java`: The core of the implementation. This class implements the `BeanDefinitionRegistryPostProcessor` interface. Spring executes its `postProcessBeanDefinitionRegistry` method very early in the lifecycle, allowing us to manipulate the bean definition registry.
*   `NotificationService.java`: A consumer service that demonstrates how to use the dynamically created beans. It injects a `Map<String, NotificationSender>`, where the key is the bean name and the value is the sender instance.

**Execution Flow:**
1.  Spring starts the Application Context.
2.  It discovers and executes `DynamicBeanProcessor` because it implements `BeanDefinitionRegistryPostProcessor`.
3.  Inside the `postProcessBeanDefinitionRegistry` method:
    a. The processor reads a configuration property, e.g., `app.notifications.enabled-channels=email,sms`.
    b. It iterates over the enabled channels (`"email"`, `"sms"`).
    c. For each channel, it constructs a `BeanDefinition` for the corresponding implementation class (`EmailSender`, `SmsSender`).
    d. It registers this `BeanDefinition` with Spring's `BeanDefinitionRegistry` under a unique name (e.g., `"emailSender"`).
4.  After the processor completes, Spring continues its initialization process and is now aware of the new "emailSender" and "smsSender" beans.
5.  When `NotificationService` is created, Spring injects a map containing the newly created beans that implement `NotificationSender`.
6.  `NotificationService` can now select the appropriate sender from the map at runtime.

**Code Example:**
```java
// Strategy Interface
public interface NotificationSender {
    String getChannel();
    void send(String message);
}

// POJO Implementations
public class EmailSender implements NotificationSender {
    @Override
    public String getChannel() { return "email"; }
    @Override
    public void send(String message) { /* Email sending logic */ }
}

public class SmsSender implements NotificationSender {
    @Override
    public String getChannel() { return "sms"; }
    @Override
    public void send(String message) { /* SMS sending logic */ }
}

// The processor that dynamically registers beans
@Component
public class DynamicBeanProcessor implements BeanDefinitionRegistryPostProcessor {
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        // Simulating reading from config, can come from Environment
        List<String> enabledChannels = List.of("email", "sms");

        if (enabledChannels.contains("email")) {
            BeanDefinition beanDefinition = new RootBeanDefinition(EmailSender.class);
            registry.registerBeanDefinition("emailSender", beanDefinition);
        }
        if (enabledChannels.contains("sms")) {
            BeanDefinition beanDefinition = new RootBeanDefinition(SmsSender.class);
            registry.registerBeanDefinition("smsSender", beanDefinition);
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        // Not needed for this use case
    }
}

// Consumer Service
@Service
public class NotificationService {
    private final Map<String, NotificationSender> senders;

    @Autowired
    public NotificationService(Map<String, NotificationSender> senders) {
        this.senders = senders;
    }

    public void sendNotification(String channel, String message) {
        NotificationSender sender = senders.get(channel + "Sender");
        if (sender != null) {
            sender.send(message);
        } else {
            throw new IllegalArgumentException("Unsupported notification channel: " + channel);
        }
    }
}
```

#### Configurations and External Files
The logic is driven by settings in `application.properties`:
```properties
# A list of notification channels that should be enabled and registered as beans
app.notifications.enabled-channels=email,sms,push
```

**Study Reference:**
*   For a visual deep-dive on this topic, see: [JavaTechie - Dynamic Beans](https://www.youtube.com/watch?v=ieTORk_jsVs&ab_channel=JavaTechie)

---

## 15. Application of Design Patterns in the Project

### Title: Application of Design Patterns in the Project

**Central Concept:** This module does not introduce new technology but documents the application of classic software design patterns that were used, implicitly or explicitly, in other parts of the project. The goal is to identify and explain how these patterns help solve common design problems, improving the code's maintainability, flexibility, and organization.

#### Code and Structure Analysis

##### 1. Singleton Pattern
*   **Concept:** Ensures a class has only one instance and provides a global point of access to it.
*   **Application in the Project:** The Spring Framework uses the Singleton pattern as its default bean scope.
*   **Code:**
    ```java
    @Service
    // By default, Spring creates only ONE instance of this class.
    public class TaskService {
        // ...
    }

    @Repository
    // And ONE instance of this one.
    public interface TaskRepository extends JpaRepository<Task, Long> {}
    ```
*   **Justification:** Service and repository classes are inherently stateless, and their operations do not depend on data from previous instances. Using a single instance saves memory and avoids the overhead of creating new objects for each request.

##### 2. Strategy Pattern
*   **Concept:** Defines a family of algorithms, encapsulates each one, and makes them interchangeable. It lets the algorithm vary independently from clients that use it.
*   **Application in the Project:** The `Dynamic Beans` implementation (Module 14) is a perfect example.
*   **Code:**
    ```java
    // The "Strategy" Interface
    public interface NotificationSender {
        void send(String message);
    }

    // The "Concrete Strategies"
    public class EmailSender implements NotificationSender { /* ... */ }
    public class SmsSender implements NotificationSender { /* ... */ }

    // The "Context" that uses the strategy
    @Service
    public class NotificationService {
        private final Map<String, NotificationSender> senders;

        public void sendNotification(String channel, String message) {
            // Selects the correct strategy at runtime
            NotificationSender strategy = senders.get(channel + "Sender");
            strategy.send(message); // Executes the strategy
        }
    }
    ```
*   **Justification:** Allows adding new notification methods (e.g., `PushSender`, `SlackSender`) without changing `NotificationService`. The selection logic is decoupled from the execution logic.

##### 3. Factory Method Pattern (or Simple Factory)
*   **Concept:** Defines an interface for creating an object but lets subclasses decide which class to instantiate. In the simplest case (Simple Factory), it encapsulates object creation logic in a single place.
*   **Application in the Project:** The conversion between JPA entities (`Task`) and DTOs (`TaskDTO`) is a practical example.
*   **Code:**
    ```java
    // The "Factory"
    public class TaskMapper {
        private TaskMapper() {} // To prevent instantiation

        public static TaskDTO toDto(Task task) {
            if (task == null) return null;
            return new TaskDTO(task.getId(), task.getDescription(), task.getStatus());
        }

        public static Task toEntity(TaskDTO dto) {
            if (dto == null) return null;
            Task task = new Task();
            task.setId(dto.getId());
            task.setDescription(dto.getDescription());
            task.setStatus(dto.getStatus());
            return task;
        }
    }

    // Usage in the Service
    @Service
    public class TaskService {
        public TaskDTO createTask(TaskDTO taskDTO) {
            // Uses the factory to encapsulate entity creation
            Task task = TaskMapper.toEntity(taskDTO);
            Task savedTask = taskRepository.save(task);
            // Uses the factory to encapsulate response DTO creation
            return TaskMapper.toDto(savedTask);
        }
    }
    ```
*   **Justification:** Centralizes mapping logic, preventing it from spreading across multiple places in the code. If the structure of `TaskDTO` or `Task` changes, the modification only needs to be made in `TaskMapper`.

##### 4. Observer Pattern
*   **Concept:** Defines a one-to-many dependency between objects so that when one object (the "subject") changes state, all its dependents (the "observers") are notified and updated automatically.
*   **Application in the Project:** Any messaging implementation, such as **RabbitMQ**, is a large-scale manifestation of the Observer pattern.
*   **Conceptual Analysis:**
    *   **Subject:** The service that publishes an event (e.g., `TaskService`).
    *   **Notification Event:** The message sent to the queue/topic (e.g., `TaskCreatedEvent`).
    *   **Observers:** The services that subscribe to the queue/topic to receive and process messages.
*   **Justification:** Completely decouples the publisher from the consumers. `TaskService` doesn't need to know who is interested in the creation of a new task. It just publishes the event, and any number of other services (notification, audit, statistics) can "observe" and react to this event independently.

---

## 16. Authentication and Authorization with JWT and Keycloak

### Title: Authentication and Authorization with JWT and Keycloak

**Central Concept:** This module implements API security using OAuth 2.0 / OpenID Connect. Keycloak is used as an authorization server to manage users, roles, and issue JSON Web Tokens (JWT). The Spring Boot application acts as a "Resource Server," validating received JWTs to authenticate users and authorize access.

#### Code and Structure Analysis

**Key Classes Involved:**
*   `SecurityConfig.java`: The central security configuration class (`@EnableWebSecurity`).
*   `HttpSecurity`: An object configured to define security rules, such as which endpoints are public and which require authentication or specific roles.
*   `JwtAuthConverter.java`: A custom `Converter` to extract roles from the Keycloak JWT and adapt them to the format Spring Security expects.

**Security Execution Flow:**
1.  A client authenticates with Keycloak and obtains an `access_token` (JWT).
2.  The client makes a request to the API, including the token in the `Authorization: Bearer <jwt>` header.
3.  Spring Security intercepts the request and validates the JWT's signature, expiration, and issuer using Keycloak's public key.
4.  The `JwtAuthConverter` extracts roles from the token (e.g., from `realm_access.roles`).
5.  Spring Security checks if the user's roles authorize them to access the requested endpoint. If so, the request proceeds; otherwise, a `401` or `403` error is returned.

#### Configurations and External Files

**Configurations (`application.yml`):**
```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          # URI of the realm in Keycloak. Spring uses this for autoconfiguration of validation.
          issuer-uri: http://localhost:8081/realms/task-management-realm
```

#### Special Detail: Keycloak Integration and Configuration
The integration involves setting up a **Realm** and a **Client** in Keycloak, defining **Roles**, and, crucially, configuring **Mappers** in the client to ensure roles are included in the JWT. The advanced features mentioned (conditionals, custom attributes) are configured primarily in Keycloak, with the Spring application interacting with the result (by reading claims from the token) or using the Keycloak Admin API for modifications. For more details on advanced features, refer to the original section in the previous response.
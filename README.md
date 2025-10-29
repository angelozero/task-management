# Task-Management API

*(for english version click here (Readme-En.md)[https://github.com/angelozero/task-management/blob/master/README-En-Us.md])*

Documentação técnica para o projeto de estudo task-management. 
Cada seção abaixo detalha uma metodologia, técnica ou prática de software específica implementada como um módulo independente dentro do projeto.

## Índice

*   [01. Containerização com Docker](#01-containerização-da-aplicação-com-docker)
*   [02. Operações CRUD](#02-operações-crud-para-a-entidade-task)
*   [03 & 04. Paginação e Ordenação](#03--04-paginação-e-ordenação-de-resultados-com-spring-data)
*   [05. Tratamento Global de Exceções](#05-tratamento-global-de-exceções-com-restcontrolleradvice)
*   [06. Testes de Integração REST](#06-testes-de-integração-para-endpoints-rest)
*   [07. Testes Unitários com Mockito](#07-testes-unitários-com-mockito)
*   [08. Integração com API Externa (PokeAPI)](#08-integração-com-api-externa-pokeapi)
*   [09. Circuit Breaker com Resilience4J](#09-implementação-de-padrão-circuit-breaker-com-resilience4j)
*   [10. APIs com gRPC e GraphQL](#10-expondo-apis-com-grpc-e-graphql)
*   [11. Cache com Redis](#11-cache-de-serviço-para-pokeapi-usando-redis)
*   [12. Modelação com Sealed Classes](#12-modelação-de-domínio-com-sealed-e-non-sealed-classes)
*   [13. Orquestração de Dual Datasource](#13-orquestração-de-múltiplos-datasources-readwrite-splitting)
*   [14. Registro Dinâmico de Beans](#14-registro-dinâmico-de-beans-com-beandefinitionregistrypostprocessor)
*   [15. Aplicação de Design Patterns](#15-aplicação-de-design-patterns-no-projeto)
*   [16. Autenticação com JWT e Keycloak](#16-autenticação-e-autorização-com-jwt-e-keycloak)

---

## 01. Containerização da Aplicação com Docker

### Título: Containerização da Aplicação com Docker

**Conceito Central:** Este módulo demonstra como empacotar a aplicação Java/Spring Boot e suas dependências em uma imagem Docker. O objetivo é garantir um ambiente de execução consistente, portátil e isolado, facilitando o deploy em qualquer máquina que possua Docker instalado, da máquina de desenvolvimento à produção.

#### Análise de Código e Estrutura

Não há classes Java diretamente envolvidas na containerização, mas sim arquivos de configuração que definem como a aplicação será construída e executada.

**Arquivos Principais Envolvidos:**
*   `Dockerfile`: Um arquivo de script que contém uma série de instruções para construir a imagem Docker da aplicação.
*   `docker-compose.yml`: Um arquivo de configuração para definir e executar aplicações Docker multi-container.

**Fluxo de Execução (Build e Run):**
1.  **Build:** O comando `docker build .` lê o `Dockerfile` e usa uma build multi-stage para compilar o código e gerar um artefato `.jar` em uma imagem final leve.
2.  **Run:** O comando `docker-compose up` lê o `docker-compose.yml`, constrói as imagens e inicia os containers dos serviços definidos.

#### Configurações e Arquivos Externos

**`Dockerfile` (Exemplo):**
```dockerfile
# Estágio 1: Build da Aplicação com Maven
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Estágio 2: Criação da Imagem final de Runtime
FROM openjdk:17-jre-slim
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**`docker-compose.yml` (Exemplo):**
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

## 02. Operações CRUD para a Entidade 'Task'

### Título: Operações CRUD para a Entidade 'Task'

**Conceito Central:** Este módulo implementa a funcionalidade básica de Create (Criar), Read (Ler), Update (Atualizar) e Delete (Excluir). Ele serve como a base para a manipulação da entidade `Task` através de uma API RESTful, demonstrando a arquitetura em camadas padrão do Spring Boot (Controller, Service, Repository).

#### Análise de Código e Estrutura

**Classes Principais Envolvidas:**
*   `TaskController.java`: A porta de entrada para as requisições HTTP, anotada com `@RestController`.
*   `TaskService.java`: Contém a lógica de negócio, anotada com `@Service`.
*   `TaskRepository.java`: Interface que estende `JpaRepository<Task, Long>` para interação com o banco de dados.
*   `Task.java`: A classe de entidade JPA, anotada com `@Entity`.
*   `TaskDTO.java`: Objeto de Transferência de Dados para desacoplar a API da entidade de banco de dados.

**Fluxo de Execução (Criação):**
1.  Requisição `POST /api/tasks` com um JSON no corpo.
2.  `TaskController` recebe e desserializa o JSON para um `TaskDTO`.
3.  Controller chama `TaskService`, que converte o DTO para uma entidade `Task`.
4.  Service chama `TaskRepository.save()`.
5.  O repositório persiste a entidade no banco de dados e retorna a instância salva.
6.  Service converte a entidade de volta para DTO e a retorna, resultando em uma resposta `HTTP 201 Created`.

#### Configurações e Arquivos Externos

**Configurações (`application.properties`):**
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/taskdb
spring.datasource.username=user
spring.datasource.password=password
spring.jpa.hibernate.ddl-auto=update
```

---

## 03 & 04. Paginação e Ordenação de Resultados com Spring Data

### Título: Paginação e Ordenação de Resultados com Spring Data

**Conceito Central:** Este módulo demonstra como lidar com grandes volumes de dados de forma eficiente, implementando paginação e ordenação nos endpoints de listagem. A finalidade é evitar a sobrecarga do sistema e oferecer flexibilidade ao cliente da API.

#### Análise de Código e Estrutura

**Classes Principais Envolvidas:**
*   `TaskController.java`: O método de listagem é modificado para receber um parâmetro `Pageable`.
*   `TaskRepository.java`: A interface `JpaRepository` já fornece o método `findAll(Pageable pageable)` pronto para uso.

**Fluxo de Execução:**
1.  O cliente faz uma requisição `GET /api/tasks?page=0&size=10&sort=description,asc`.
2.  O Spring MVC constrói um objeto `Pageable` a partir dos parâmetros da URL.
3.  O `TaskController` recebe o `Pageable` e o repassa para o `TaskService`.
4.  O serviço chama `taskRepository.findAll(pageable)`.
5.  O Spring Data JPA gera uma query SQL com cláusulas `LIMIT`, `OFFSET` e `ORDER BY`.
6.  O resultado é encapsulado em um objeto `Page<Task>`, contendo os dados da página e metadados de paginação, que é então convertido para `Page<TaskDTO>` e retornado ao cliente.

---

## 05. Tratamento Global de Exceções com @RestControllerAdvice

### Título: Tratamento Global de Exceções com @RestControllerAdvice

**Conceito Central:** Este módulo implementa um mecanismo centralizado para capturar e tratar exceções, padronizando as respostas de erro da API e evitando código repetitivo.

#### Análise de Código e Estrutura

**Classes Principais Envolvidas:**
*   `RestExceptionHandler.java`: Classe anotada com `@RestControllerAdvice` para interceptar exceções globalmente.
*   Métodos com `@ExceptionHandler`: Métodos dentro do handler que tratam tipos específicos de exceções e retornam uma `ResponseEntity` padronizada.

**Fluxo de Execução:**
1.  Uma exceção (ex: `CustomNotFoundException`) é lançada em qualquer camada da aplicação (normalmente no Service).
2.  O Spring a propaga até que seja interceptada pelo `RestExceptionHandler`.
3.  O método `@ExceptionHandler` correspondente ao tipo da exceção é invocado.
4.  Este método constrói um objeto de erro padronizado e o retorna com o status HTTP apropriado (ex: `404 Not Found`).

---

## 06. Testes de Integração para Endpoints REST

### Título: Testes de Integração para Endpoints REST

**Conceito Central:** Este módulo demonstra como escrever testes que verificam a colaboração entre múltiplas camadas da aplicação (Controller, Service, Repository) e a infraestrutura (como um banco de dados de teste).

#### Análise de Código e Estrutura

**Classes Principais Envolvidas:**
*   `TaskControllerIT.java`: Classe de teste anotada com `@SpringBootTest`.
*   `MockMvc`: Objeto para simular requisições HTTP aos controllers sem um servidor web completo.
*   `@Testcontainers`: Anotação para iniciar containers Docker (ex: banco de dados) para o escopo dos testes.

**Fluxo de Execução de um Teste:**
1.  JUnit, via `@SpringBootTest`, carrega o contexto completo da aplicação Spring.
2.  `@Testcontainers` inicia um banco de dados temporário.
3.  O teste usa `MockMvc` para disparar uma requisição a um endpoint.
4.  A requisição percorre toda a stack da aplicação até o banco de dados de teste.
5.  Asserções verificam o status da resposta HTTP e o conteúdo do corpo JSON.

---

## 07. Testes Unitários com Mockito

### Título: Testes Unitários com Mockito

**Conceito Central:** Este módulo foca em testar uma única unidade de código (uma classe) de forma isolada, substituindo suas dependências por "mocks" (objetos simulados) usando a biblioteca Mockito.

#### Análise de Código e Estrutura

**Classes Principais Envolvidas:**
*   `TaskServiceTest.java`: Classe de teste sem anotações do Spring, usando `@ExtendWith(MockitoExtension.class)`.
*   `@Mock`: Anotação para criar um mock de uma dependência (ex: `TaskRepository`).
*   `@InjectMocks`: Anotação para criar uma instância da classe sob teste e injetar os mocks nela.

**Fluxo de Execução de um Teste:**
1.  **Arrange:** O comportamento dos mocks é definido usando `Mockito.when(...).thenReturn(...)`.
2.  **Act:** O método da classe sob teste é invocado.
3.  **Assert:** Asserções verificam o resultado do método, e `Mockito.verify(...)` confirma se os mocks foram chamados como esperado.

---

## 08. Integração com API Externa (PokeAPI)

### Título: Integração com API Externa (PokeAPI)

**Conceito Central:** Este módulo demonstra como consumir dados de uma API REST externa pública, mostrando as práticas para realizar requisições HTTP, desserializar a resposta e integrar os dados.

#### Análise de Código e Estrutura

**Classes Principais Envolvidas:**
*   `PokemonClient.java`: Um componente (`@Service`) que isola a lógica de comunicação com a PokeAPI.
*   `WebClient`: A ferramenta moderna e reativa do Spring para fazer chamadas HTTP.
*   `PokemonDTO.java`: DTO que espelha a estrutura do JSON retornado pela API externa.

**Fluxo de Execução:**
1.  `PokemonClient` usa um bean `WebClient` para construir e executar uma requisição `GET` para a URL da PokeAPI.
2.  A resposta JSON é recebida.
3.  `WebClient` automaticamente desserializa o JSON em um objeto `PokemonDTO`.
4.  O DTO é retornado para ser usado pela lógica de negócio da aplicação.

---

## 09. Implementação de Padrão Circuit Breaker com Resilience4J

### Título: Implementação de Padrão Circuit Breaker com Resilience4J

**Conceito Central:** Este módulo aplica o padrão "Circuit Breaker" para aumentar a resiliência ao interagir com serviços externos. Quando um serviço externo falha repetidamente, o circuito se abre, e as chamadas subsequentes falham imediatamente (fast-fail), protegendo a aplicação.

#### Análise de Código e Estrutura

**Classes Principais Envolvidas:**
*   `PokemonClient.java`: O método que faz a chamada externa é anotado com `@CircuitBreaker`.
*   `@CircuitBreaker(name = "pokeApi", fallbackMethod = "getPokemonFallback")`: A anotação do Resilience4J que envolve o método, especificando um nome para a configuração e um método de fallback.
*   `getPokemonFallback()`: Método que é executado quando o circuito está aberto ou a chamada original falha, retornando uma resposta padrão.

**Fluxo de Execução:**
1.  **Circuito Fechado:** Chamadas são feitas normalmente. Falhas são contadas.
2.  **Circuito Aberto:** Após um limiar de falhas ser atingido, o circuito abre. Chamadas futuras são redirecionadas para o método de `fallback` sem tentar acessar a API externa.
3.  **Circuito Meio-Aberto:** Após um tempo, o circuito permite algumas chamadas de teste. Se bem-sucedidas, ele fecha; senão, volta a abrir.

#### Configurações e Arquivos Externos

**Configurações (`application.yml`):**
```yaml
resilience4j.circuitbreaker:
  instances:
    pokeApi: # Nome correspondente à anotação
      failure-rate-threshold: 50
      wait-duration-in-open-state: 30s
      sliding-window-size: 20
```

---

## 10. Expondo APIs com gRPC e GraphQL

### Título: Expondo APIs com gRPC e GraphQL

**Conceito Central:** Este módulo explora duas alternativas modernas às APIs REST:
*   **GraphQL:** Uma linguagem de consulta que permite aos clientes solicitar exatamente os dados de que precisam.
*   **gRPC:** Um framework de RPC de alta performance, ideal para comunicação entre microserviços.

#### Análise de Código: GraphQL

*   `schema.graphqls`: Define os tipos, queries e mutações da API.
*   `TaskGraphqlController.java`: Métodos anotados com `@QueryMapping` e `@MutationMapping` que implementam o schema.
*   O cliente envia uma query em um `POST` para `/graphql`, e o framework resolve os campos, retornando um JSON com a estrutura exata solicitada.

#### Análise de Código: gRPC

*   `task.proto`: Arquivo de definição do serviço, métodos e mensagens usando Protocol Buffers.
*   Código Gerado: O plugin do Maven/Gradle gera stubs Java a partir do `.proto`.
*   `TaskGrpcService.java`: Classe anotada com `@GrpcService` que estende a base gerada e implementa a lógica dos RPCs.
*   A comunicação é binária, de alta performance e usa HTTP/2.

---

## 11. Cache de Serviço para PokeApi usando Redis

### Título: Cache de Serviço para PokeApi usando Redis

**Conceito Central:** Este módulo implementa uma estratégia de cache com Redis para armazenar temporariamente os resultados das chamadas à PokeAPI, reduzindo latência e carga na API externa.

#### Análise de Código e Estrutura

**Classes Principais Envolvidas:**
*   `Application.java`: Anotada com `@EnableCaching` para ligar o suporte a cache do Spring.
*   `PokemonClient.java`: O método a ser cacheado (`getPokemonByName`) é anotado com `@Cacheable`.
*   `@Cacheable(value = "pokemon", key = "#name")`: Intercepta a chamada. Se a chave (`#name`) existir no cache (`"pokemon"`), retorna o valor do cache. Senão, executa o método, armazena o resultado no cache e o retorna.

**Fluxo de Execução:**
1.  **Cache Miss (primeira chamada):** A chamada ao método é executada, o resultado é buscado da PokeAPI e armazenado no Redis.
2.  **Cache Hit (chamadas subsequentes):** O resultado é recuperado diretamente do Redis, e o método original não é executado.

#### Configurações e Arquivos Externos

**Configurações (`application.properties`):**
```properties
# Habilita o provedor de cache Redis
spring.cache.type=redis
# Configurações de conexão com o Redis
spring.redis.host=localhost
spring.redis.port=6379
# Define um tempo de vida (TTL) de 10 minutos para as entradas do cache
spring.cache.redis.time-to-live=600000
```
**Infraestrutura:** Requer uma instância Redis em execução, que pode ser gerenciada via `docker-compose.yml`.

---

## 12. Modelação de Domínio com Sealed e Non-Sealed Classes

### Título: Modelação de Domínio com Sealed e Non-Sealed Classes

**Conceito Central:** Este módulo de estudo demonstra o uso de `sealed` e `non-sealed` classes e interfaces, um recurso do Java 17, para modelar hierarquias de domínio de forma mais restrita e expressiva. O objetivo é controlar explicitamente quais classes podem estender ou implementar uma superclasse, permitindo que o compilador raciocine sobre o conjunto completo de subtipos, o que é especialmente útil em `switch` expressions.

#### Análise de Código e Estrutura

**Classes Principais Envolvidas:**
*   `TaskStatus.java` (Sealed Interface): Uma interface `sealed` que define um contrato para diferentes estados de uma tarefa. A cláusula `permits` define explicitamente o conjunto fechado de implementações permitidas.
*   `PendingStatus.java`, `CompletedStatus.java`, etc. (Final Classes): Classes `final` que implementam a interface `TaskStatus`. Sendo `final`, elas cumprem o contrato da interface `sealed` ao não permitirem novas extensões.
*   `Notification.java` (Sealed Interface): Outro exemplo com uma interface selada para tipos de notificação.
*   `PushNotification.java` (Non-Sealed Class): Uma implementação que usa a palavra-chave `non-sealed` para "quebrar o selo" e permitir que outras classes desconhecidas possam estender `PushNotification` no futuro, demonstrando flexibilidade quando necessário.
*   `StatusService.java`: Um serviço de exemplo que usa um `switch` aprimorado para realizar pattern matching nos tipos `TaskStatus`, sem a necessidade de uma cláusula `default`, pois o compilador conhece todas as implementações possíveis.

**Fluxo de Execução / Exemplo de Uso:**
1.  Uma instância de um subtipo de `TaskStatus` (ex: `new CompletedStatus()`) é criada.
2.  Esta instância é passada para um método como `statusService.getHumanReadableStatus(status)`.
3.  Dentro do método, a `switch` expression faz o "pattern matching" da variável `status`.
4.  O `case` correspondente ao tipo exato (`CompletedStatus s`) é executado.
5.  Como a interface `TaskStatus` é `sealed`, o compilador garante que todos os tipos permitidos estão cobertos pelos `case`s, tornando a cláusula `default` desnecessária e o código mais seguro contra mudanças futuras.

**Exemplo de Código:**
```java
// Definição da hierarquia selada
public sealed interface TaskStatus permits PendingStatus, InProgressStatus, CompletedStatus {
    String getStatusName();
}

public final class PendingStatus implements TaskStatus {
    @Override
    public String getStatusName() {
        return "Pendente";
    }
}

public final class CompletedStatus implements TaskStatus {
    @Override
    public String getStatusName() {
        return "Concluída";
    }
}
// ... outras implementações

// Exemplo de uso com switch aprimorado
@Service
public class StatusService {
    public String getMessageForStatus(TaskStatus status) {
        return switch (status) {
            case PendingStatus s -> "A tarefa está aguardando para ser iniciada.";
            case InProgressStatus s -> "A tarefa está em andamento.";
            case CompletedStatus s -> "A tarefa foi finalizada com sucesso!";
            // Nenhum 'default' é necessário. O compilador valida a exaustividade.
        };
    }
}
```

#### Configurações e Arquivos Externos
Esta é uma funcionalidade da linguagem Java (versão 17+). Não requer dependências ou configurações de `application.properties`. O único requisito é que o projeto esteja configurado para compilar com JDK 17 ou superior.

---

## 13. Orquestração de Múltiplos DataSources (Read/Write Splitting)

### Título: Orquestração de Múltiplos DataSources (Read/Write Splitting)

**Conceito Central:** Este módulo implementa uma estratégia de separação de leitura/escrita (Read/Write Splitting) para otimizar o uso do banco de dados. O objetivo é direcionar todas as operações que modificam dados (`INSERT`, `UPDATE`, `DELETE`) para um banco de dados primário (master) e todas as operações de apenas leitura (`SELECT`) para uma ou mais réplicas (slaves). Isso reduz a carga no banco primário e melhora a performance de leitura da aplicação.

#### Análise de Código e Estrutura

**Classes Principais Envolvidas:**
*   `DataSourceConfig.java`: Classe de configuração `@Configuration` que define três beans principais:
    1.  `primaryDataSource`: Bean `DataSource` para o banco de dados de escrita.
    2.  `replicaDataSource`: Bean `DataSource` para o banco de dados de leitura.
    3.  `routingDataSource`: Um bean que estende `AbstractRoutingDataSource`. Ele age como um proxy que, dependendo do contexto, usará o `primaryDataSource` ou o `replicaDataSource`.
*   `RoutingDataSource.java`: A implementação de `AbstractRoutingDataSource`. Seu método principal, `determineCurrentLookupKey()`, consulta um `ThreadLocal` para decidir qual `DataSource` usar.
*   `DataSourceContextHolder.java`: Uma classe utilitária que usa um `ThreadLocal` para armazenar o tipo de `DataSource` (`PRIMARY` ou `REPLICA`) para a thread de execução atual.
*   `@TargetDataSource` (Anotação customizada): Uma anotação para ser usada em métodos de serviço (ex: `@TargetDataSource(DataSourceType.REPLICA)`) para indicar qual `DataSource` deve ser usado.
*   `DataSourceAspect.java`: Um Aspecto AOP que intercepta métodos anotados com `@TargetDataSource`. Antes da execução do método, ele define o tipo de `DataSource` no `DataSourceContextHolder`. Após a execução, ele limpa o contexto para evitar vazamentos entre threads.

**Fluxo de Execução:**
1.  Uma requisição chega a um método de serviço, por exemplo, `taskService.findById(1L)`.
2.  Este método está anotado com `@TargetDataSource(DataSourceType.REPLICA)`.
3.  O `DataSourceAspect` intercepta a chamada. Antes de executar o método, ele chama `DataSourceContextHolder.set(DataSourceType.REPLICA)`.
4.  O método `findById` é executado. Quando o Spring Data/JPA solicita uma conexão com o banco, ele fala com o `RoutingDataSource`.
5.  O `RoutingDataSource` invoca `determineCurrentLookupKey()`, que lê `DataSourceType.REPLICA` do `DataSourceContextHolder`.
6.  Com base na chave "REPLICA", o `RoutingDataSource` direciona a chamada para o `replicaDataSource`. A query `SELECT` é executada na réplica.
7.  Após a conclusão do método do serviço, a cláusula `finally` do aspecto chama `DataSourceContextHolder.clear()`.
8.  Se um método não for anotado (ex: `createTask`), o aspecto não atua, e o `RoutingDataSource` usa seu `DataSource` padrão, que é configurado para ser o `primaryDataSource`.

**Exemplo de Código:**
```java
// Enum para os tipos de DataSource
public enum DataSourceType {
    PRIMARY, REPLICA;
}

// ThreadLocal para guardar o contexto
public class DataSourceContextHolder {
    private static final ThreadLocal<DataSourceType> contextHolder = new ThreadLocal<>();
    public static void set(DataSourceType type) { contextHolder.set(type); }
    public static DataSourceType get() { return contextHolder.get(); }
    public static void clear() { contextHolder.remove(); }
}

// Aspecto AOP para gerenciar o contexto
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

// Uso no Serviço
@Service
public class TaskService {
    @Transactional(readOnly = true) // Importante para garantir que não há escrita
    @TargetDataSource(DataSourceType.REPLICA)
    public Optional<TaskDTO> findById(Long id) {
        // Esta query será executada na réplica
        return taskRepository.findById(id).map(TaskMapper::toDto);
    }
    
    @Transactional
    public TaskDTO createTask(TaskDTO taskDTO) {
        // Esta query será executada no primário (padrão)
        Task task = TaskMapper.toEntity(taskDTO);
        return TaskMapper.toDto(taskRepository.save(task));
    }
}
```

#### Configurações e Arquivos Externos
**Dependências (`pom.xml`):**
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-aop</artifactId>
</dependency>
```
**Configurações (`application.properties`):**
```properties
# Configuração do DataSource Primário (Leitura/Escrita)
spring.datasource.primary.url=jdbc:postgresql://master-db-host:5432/taskdb
spring.datasource.primary.username=user
spring.datasource.primary.password=password

# Configuração do DataSource de Réplica (Apenas Leitura)
spring.datasource.replica.url=jdbc:postgresql://replica-db-host:5432/taskdb
spring.datasource.replica.username=user_readonly
spring.datasource.replica.password=password_readonly
```

---

## 14. Registro Dinâmico de Beans com BeanDefinitionRegistryPostProcessor

### Título: Registro Dinâmico de Beans com BeanDefinitionRegistryPostProcessor

**Conceito Central:** Este módulo demonstra uma técnica avançada do Spring para registrar beans programaticamente durante a fase de inicialização do container. O objetivo é criar e configurar beans com base em configurações externas (ex: `application.properties`), permitindo que a aplicação se adapte dinamicamente sem a necessidade de recompilação. É ideal para arquiteturas de plugins ou para habilitar/desabilitar funcionalidades de forma declarativa.

#### Análise de Código e Estrutura

**Classes Principais Envolvidas:**
*   `NotificationSender` (Interface): Define um contrato comum para diferentes tipos de notificadores (ex: `send(message)`). Atua como a "Strategy" em um Design Pattern Strategy.
*   `EmailSender.java`, `SmsSender.java` (Implementações): Classes concretas que implementam `NotificationSender`. **Importante:** Elas **não** são anotadas com `@Component` ou `@Service`, pois sua criação será gerenciada dinamicamente.
*   `DynamicBeanProcessor.java`: O coração da implementação. É uma classe que implementa a interface `BeanDefinitionRegistryPostProcessor`. O Spring executa seu método `postProcessBeanDefinitionRegistry` muito cedo no ciclo de vida, permitindo-nos manipular o registro de definições de beans.
*   `NotificationService.java`: Um serviço consumidor que demonstra como usar os beans criados dinamicamente. Ele injeta um `Map<String, NotificationSender>`, onde a chave é o nome do bean e o valor é a instância do sender.

**Fluxo de Execução:**
1.  O Spring inicia o Application Context.
2.  Ele descobre e executa o `DynamicBeanProcessor` porque ele implementa `BeanDefinitionRegistryPostProcessor`.
3.  Dentro do método `postProcessBeanDefinitionRegistry`:
    a. O processador lê uma propriedade de configuração, ex: `app.notifications.enabled-channels=email,sms`.
    b. Ele itera sobre os canais habilitados (`"email"`, `"sms"`).
    c. Para cada canal, ele constrói uma `BeanDefinition` para a classe de implementação correspondente (`EmailSender`, `SmsSender`).
    d. Ele registra essa `BeanDefinition` no `BeanDefinitionRegistry` do Spring com um nome único (ex: `"emailSender"`).
4.  Após a conclusão do processador, o Spring continua seu processo de inicialização e agora está ciente dos novos beans "emailSender" e "smsSender".
5.  Quando o `NotificationService` é criado, o Spring injeta um mapa contendo os beans recém-criados que implementam `NotificationSender`.
6.  O `NotificationService` pode agora selecionar o sender apropriado do mapa em tempo de execução.

**Exemplo de Código:**
```java
// Interface Strategy
public interface NotificationSender {
    String getChannel();
    void send(String message);
}

// Implementações POJO
public class EmailSender implements NotificationSender {
    @Override public String getChannel() { return "email"; }
    @Override public void send(String message) { /* Lógica de envio de e-mail */ }
}
public class SmsSender implements NotificationSender {
    @Override public String getChannel() { return "sms"; }
    @Override public void send(String message) { /* Lógica de envio de SMS */ }
}

// O processador que registra os beans dinamicamente
@Component
public class DynamicBeanProcessor implements BeanDefinitionRegistryPostProcessor {
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        // Simulação de leitura de config, pode vir do Environment
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
        // Não é necessário implementar para este caso de uso
    }
}

// Serviço consumidor
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
            throw new IllegalArgumentException("Canal de notificação não suportado: " + channel);
        }
    }
}
```

#### Configurações e Arquivos Externos
A lógica é impulsionada por configurações no `application.properties`:
```properties
# Uma lista de canais de notificação que devem ser habilitados e registrados como beans
app.notifications.enabled-channels=email,sms,push
```
**Referência de Estudo:**
*   Para um aprofundamento visual sobre o tema, consulte: [JavaTechie - Dynamic Beans](https://www.youtube.com/watch?v=ieTORk_jsVs&ab_channel=JavaTechie)

---

## 15. Aplicação de Design Patterns no Projeto

### Título: Aplicação de Design Patterns no Projeto

**Conceito Central:** Este módulo não introduz uma nova tecnologia, mas sim documenta a aplicação de padrões de projeto (Design Patterns) de software clássicos que foram utilizados, implícita ou explicitamente, em outras partes do projeto. O objetivo é identificar e explicar como esses padrões ajudam a resolver problemas comuns de design, melhorando a manutenibilidade, flexibilidade e organização do código.

#### Análise de Código e Estrutura

##### 1. Padrão Singleton
*   **Conceito:** Garante que uma classe tenha apenas uma instância e fornece um ponto de acesso global a ela.
*   **Aplicação no Projeto:** O Spring Framework utiliza o padrão Singleton como seu escopo de bean padrão.
*   **Código:**
    ```java
    @Service // Por padrão, o Spring cria apenas UMA instância desta classe.
    public class TaskService {
        // ...
    }
    
    @Repository // E UMA instância desta.
    public interface TaskRepository extends JpaRepository<Task, Long> {}
    ```
*   **Justificativa:** Classes de serviço e repositório são inerentemente sem estado (stateless) e suas operações não dependem de dados de instâncias anteriores. Usar uma única instância economiza memória e evita a sobrecarga de criar novos objetos para cada requisição.

##### 2. Padrão Strategy
*   **Conceito:** Define uma família de algoritmos, encapsula cada um deles e os torna intercambiáveis. Permite que o algoritmo varie independentemente dos clientes que o utilizam.
*   **Aplicação no Projeto:** A implementação de `Dynamic Beans` (Módulo 16) é um exemplo perfeito.
*   **Código:**
    ```java
    // A Interface "Strategy"
    public interface NotificationSender {
        void send(String message);
    }
    
    // As "Concrete Strategies"
    public class EmailSender implements NotificationSender { /* ... */ }
    public class SmsSender implements NotificationSender { /* ... */ }

    // O "Context" que usa a strategy
    @Service
    public class NotificationService {
        private final Map<String, NotificationSender> senders;
        
        public void sendNotification(String channel, String message) {
            // Seleciona a estratégia correta em tempo de execução
            NotificationSender strategy = senders.get(channel + "Sender");
            strategy.send(message); // Executa a estratégia
        }
    }
    ```
*   **Justificativa:** Permite adicionar novos métodos de notificação (ex: `PushSender`, `SlackSender`) sem alterar o `NotificationService`. A lógica de seleção está desacoplada da lógica de execução.

##### 3. Padrão Factory Method (ou Simple Factory)
*   **Conceito:** Define uma interface para criar um objeto, mas deixa as subclasses decidirem qual classe instanciar. No caso mais simples (Simple Factory), encapsula a lógica de criação de objetos em um único local.
*   **Aplicação no Projeto:** A conversão entre entidades JPA (`Task`) e DTOs (`TaskDTO`) é um exemplo prático.
*   **Código:**
    ```java
    // A "Factory"
    public class TaskMapper {
        
        private TaskMapper() {} // Para não ser instanciada

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
    
    // Uso no Serviço
    @Service
    public class TaskService {
        public TaskDTO createTask(TaskDTO taskDTO) {
            // Usa a factory para encapsular a criação da entidade
            Task task = TaskMapper.toEntity(taskDTO);
            Task savedTask = taskRepository.save(task);
            // Usa a factory para encapsular a criação do DTO de resposta
            return TaskMapper.toDto(savedTask);
        }
    }
    ```
*   **Justificativa:** Centraliza a lógica de mapeamento, evitando que ela se espalhe por vários locais no código. Se a estrutura do `TaskDTO` ou `Task` mudar, a alteração precisa ser feita apenas no `TaskMapper`.

##### 4. Padrão Observer
*   **Conceito:** Define uma dependência um-para-muitos entre objetos, de modo que, quando um objeto (o "subject") muda de estado, todos os seus dependentes (os "observers") são notificados e atualizados automaticamente.
*   **Aplicação no Projeto:** Qualquer implementação de mensageria, como **RabbitMQ** (Módulo 15), é uma manifestação em larga escala do padrão Observer.
*   **Análise Conceitual:**
    *   **Subject (Assunto):** O serviço que publica um evento (ex: `TaskService`).
    *   **Evento de Notificação:** A mensagem enviada para a fila/tópico (ex: `TaskCreatedEvent`).
    *   **Observers (Observadores):** Os serviços que se inscrevem na fila/tópico para receber e processar as mensagens.
*   **Justificativa:** Desacopla completamente o publicador dos consumidores. O `TaskService` não precisa saber quem está interessado na criação de uma nova tarefa. Ele apenas publica o fato, e qualquer número de outros serviços (notificação, auditoria, estatísticas) pode "observar" e reagir a esse evento de forma independente.

---

## 16. Autenticação e Autorização com JWT e Keycloak

### Título: Autenticação e Autorização com JWT e Keycloak

**Conceito Central:** Este módulo implementa segurança na API usando OAuth 2.0 / OpenID Connect. O Keycloak é utilizado como um servidor de autorização para gerenciar usuários, roles e emitir JSON Web Tokens (JWT). A aplicação Spring Boot atua como um "Resource Server", validando os JWTs recebidos para autenticar usuários e autorizar o acesso.

#### Análise de Código e Estrutura

**Classes Principais Envolvidas:**
*   `SecurityConfig.java`: Classe central de configuração de segurança (`@EnableWebSecurity`).
*   `HttpSecurity`: Objeto configurado para definir as regras de segurança, como quais endpoints são públicos e quais requerem autenticação ou roles específicas.
*   `JwtAuthConverter.java`: Um `Converter` customizado para extrair as roles do token JWT do Keycloak e adaptá-las para o formato que o Spring Security espera.

**Fluxo de Execução (Segurança):**
1.  Um cliente se autentica no Keycloak e obtém um `access_token` (JWT).
2.  O cliente faz uma requisição à API, incluindo o token no header `Authorization: Bearer <jwt>`.
3.  O Spring Security intercepta, valida a assinatura, expiração e emissor do JWT usando a chave pública do Keycloak.
4.  O `JwtAuthConverter` extrai as roles do token (ex: de `realm_access.roles`).
5.  O Spring Security verifica se as roles do usuário o autorizam a acessar o endpoint solicitado. Se sim, a requisição prossegue; senão, um erro `401` ou `403` é retornado.

#### Configurações e Arquivos Externos

**Configurações (`application.yml`):**
```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          # URI do realm no Keycloak. O Spring usa isso para autoconfigurar a validação.
          issuer-uri: http://localhost:8081/realms/task-management-realm
```

#### Detalhe Especial: Integração e Configuração Keycloak
A integração envolve configurar um **Realm** e um **Client** no Keycloak, definir **Roles** e, crucialmente, configurar **Mappers** no cliente para garantir que as roles sejam incluídas no JWT. As funcionalidades avançadas mencionadas (condicionais, atributos customizados) são configuradas principalmente no Keycloak, com a aplicação Spring interagindo com o resultado (lendo claims do token) ou usando a API Admin do Keycloak para modificações. Para mais detalhes sobre as funcionalidades avançadas, consulte a seção original da resposta anterior.

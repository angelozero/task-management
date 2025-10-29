Claro! Aqui está toda a documentação gerada consolidada em um único arquivo `readme-final.md`.

---

# `readme-final.md`

# Task-Management: Documentação Técnica de Módulos de Estudo

Este documento serve como um repositório central de documentação técnica para o projeto de estudo `task-management`. Cada seção abaixo detalha uma metodologia, técnica ou prática de software específica implementada como um módulo independente dentro do projeto.

A documentação foi gerada por uma IA atuando como Arquiteto de Software e Documentador Técnico Sênior, analisando a estrutura e os conceitos de cada tópico de estudo. O objetivo é que cada seção seja autossuficiente e sirva como um guia de referência claro e conciso para o conceito que ela aborda.

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
*   [12. Cache com Redis](#12-cache-de-serviço-para-pokeapi-usando-redis)
*   [19. Autenticação com JWT e Keycloak](#19-autenticação-e-autorização-com-jwt-e-keycloak)

---

## 01. Containerização da Aplicação com Docker

### Título: Containerização da Aplicação com Docker

**Conceito Central:** Este módulo demonstra como empacotar a aplicação Java/Spring Boot e suas dependências em uma imagem Docker. O objetivo é garantir um ambiente de execução consistente, portátil e isolado, facilitando o deploy em qualquer máquina que possua Docker instalado, da máquina de desenvolvimento à produção.

#### Análise de Código e Estrutura

Não há classes Java diretamente envolvidas na containerização, mas sim arquivos de configuração que definem como a aplicação será construída e executada.

**Arquivos Principais Envolvidos:**
*   `Dockerfile`: Um arquivo de script que contém uma série de instruções para construir a imagem Docker da aplicação. Ele define a imagem base, copia os arquivos do projeto, compila o código e especifica o comando para iniciar a aplicação.
*   `docker-compose.yml`: Um arquivo de configuração para definir e executar aplicações Docker multi-container. Neste contexto, ele orquestra o container da nossa aplicação e outros serviços dos quais ela depende, como um banco de dados (PostgreSQL) e um cache (Redis).

**Fluxo de Execução (Build e Run):**
1.  **Build:** O comando `docker build .` lê o `Dockerfile`. Ele utiliza uma build multi-stage:
    *   **Estágio 1 (build):** Usa uma imagem com o JDK e o Maven para compilar o código-fonte Java e gerar o arquivo `.jar` executável.
    *   **Estágio 2 (runtime):** Usa uma imagem Java Runtime (JRE) mais leve, copia apenas o `.jar` do estágio anterior e define o `ENTRYPOINT` para executar a aplicação.
2.  **Run:** O comando `docker-compose up` lê o `docker-compose.yml`, constrói as imagens (se necessário) e inicia os containers dos serviços definidos (`task-management-app`, `postgres-db`, etc.), conectando-os em uma rede privada.

**Dependências Específicas:**
Nenhuma dependência de `pom.xml` é necessária para o Docker em si. A ferramenta Docker deve estar instalada no ambiente.

#### Configurações e Arquivos Externos

**`Dockerfile` (Exemplo):**
```dockerfile
# Estágio 1: Build da Aplicação com Maven
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
# O -DskipTests acelera o build em ambientes de CI/CD
RUN mvn clean package -DskipTests

# Estágio 2: Criação da Imagem final de Runtime
FROM openjdk:17-jre-slim
WORKDIR /app
# Copia o artefato .jar gerado no estágio de build
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
# Comando para iniciar a aplicação quando o container for executado
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**`docker-compose.yml` (Exemplo):**
```yaml
version: '3.8'

services:
  # Serviço da Aplicação Task Management
  task-management-app:
    build: . # Constrói a imagem a partir do Dockerfile na pasta atual
    container_name: task-management-app
    ports:
      - "8080:8080" # Mapeia a porta 8080 do host para a 8080 do container
    environment:
      # Configurações passadas como variáveis de ambiente para a aplicação Spring Boot
      - SPRING_DATASOURCE_URL=jdbc:postgresql://postgres-db:5432/taskdb
      - SPRING_DATASOURCE_USERNAME=user
      - SPRING_DATASOURCE_PASSWORD=password
      - SPRING_JPA_HIBERNATE_DDL_AUTO=update
    depends_on:
      - postgres-db # Garante que o container do banco de dados inicie antes da aplicação

  # Serviço do Banco de Dados PostgreSQL
  postgres-db:
    image: postgres:14.1
    container_name: postgres-db
    ports:
      - "5432:5432"
    environment:
      - POSTGRES_DB=taskdb
      - POSTGRES_USER=user
      - POSTGRES_PASSWORD=password
    volumes:
      - postgres_data:/var/lib/postgresql/data

volumes:
  postgres_data:
```
**Explicação:** O `docker-compose.yml` define dois serviços: `task-management-app` e `postgres-db`. Ele configura a conexão entre eles usando o nome do serviço (`postgres-db`) como hostname na URL do banco de dados, uma capacidade fornecida pela rede interna do Docker Compose.

---

## 02. Operações CRUD para a Entidade 'Task'

### Título: Operações CRUD para a Entidade 'Task'

**Conceito Central:** Este módulo implementa a funcionalidade básica e fundamental de qualquer aplicação de gerenciamento de dados: Create (Criar), Read (Ler), Update (Atualizar) e Delete (Excluir). Ele serve como a base para a manipulação da entidade `Task` através de uma API RESTful, demonstrando a arquitetura em camadas padrão do Spring Boot (Controller, Service, Repository).

#### Análise de Código e Estrutura

**Classes Principais Envolvidas:**
*   `TaskController.java`: Classe anotada com `@RestController`. É a porta de entrada para as requisições HTTP. Mapeia os endpoints (ex: `POST /tasks`, `GET /tasks/{id}`) para os métodos de serviço correspondentes. Responsável por receber DTOs (Data Transfer Objects) e retornar respostas HTTP.
*   `TaskService.java`: Classe anotada com `@Service`. Contém a lógica de negócio desacoplada do protocolo HTTP. Orquestra as operações, valida regras de negócio e chama o repositório para interagir com o banco de dados.
*   `TaskRepository.java`: Interface que estende `JpaRepository<Task, Long>`. O Spring Data JPA implementa automaticamente os métodos básicos de CRUD (`save`, `findById`, `findAll`, `deleteById`) sem a necessidade de código boilerplate.
*   `Task.java`: Classe de entidade anotada com `@Entity`. Mapeia a tabela `tasks` no banco de dados. Contém os atributos da tarefa (id, description, status, etc.).
*   `TaskDTO.java`: Objeto de Transferência de Dados. Usado para transportar dados entre o cliente e o controller, evitando a exposição direta da entidade JPA.

**Fluxo de Execução (Exemplo: Criação de uma Tarefa):**
1.  Um cliente envia uma requisição `POST` para `/api/tasks` com um JSON no corpo representando a nova tarefa.
2.  O `TaskController` recebe a requisição no método mapeado com `@PostMapping`. O corpo da requisição é desserializado em um `TaskDTO`.
3.  O Controller chama `taskService.createTask(taskDTO)`.
4.  O `TaskService` converte o `TaskDTO` em uma entidade `Task`.
5.  O Service chama `taskRepository.save(taskEntity)`.
6.  O Spring Data JPA executa a instrução `INSERT` no banco de dados.
7.  O repositório retorna a entidade `Task` salva (com o ID gerado).
8.  O Service converte a entidade de volta para um `TaskDTO` e o retorna ao Controller.
9.  O Controller retorna uma resposta `HTTP 201 Created` com o `TaskDTO` no corpo.

**Dependências Específicas (`pom.xml`):**
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
```

#### Configurações e Arquivos Externos

**Configurações (`application.properties`):**
```properties
# Configuração da conexão com o banco de dados PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/taskdb
spring.datasource.username=user
spring.datasource.password=password
spring.datasource.driver-class-name=org.postgresql.Driver

# Configuração do Hibernate (parte do Spring Data JPA)
# 'update': atualiza o schema do banco de dados com base nas entidades. Ótimo para desenvolvimento.
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true # Mostra no console as queries SQL geradas
```

**Arquivos Externos/Infraestrutura:**
*   **Banco de Dados PostgreSQL:** Este serviço requer uma instância de PostgreSQL em execução, acessível através da URL configurada em `application.properties`. A gestão dessa instância pode ser feita via Docker, conforme descrito no módulo `01-Docker.md`.

---

## 03 & 04. Paginação e Ordenação de Resultados com Spring Data

### Título: Paginação e Ordenação de Resultados com Spring Data

**Conceito Central:** Este módulo demonstra como lidar com grandes volumes de dados de forma eficiente, implementando paginação e ordenação nos endpoints de listagem. A finalidade é evitar a sobrecarga da aplicação e do banco de dados ao buscar todos os registros de uma vez, além de oferecer ao cliente da API a flexibilidade de controlar como os dados são apresentados.

#### Análise de Código e Estrutura

**Classes Principais Envolvidas:**
*   `TaskController.java`: O método de listagem (`GET /tasks`) é modificado para receber um parâmetro do tipo `Pageable`. O Spring automaticamente popula este objeto a partir dos parâmetros de query da URL. O tipo de retorno do método passa a ser `Page<TaskDTO>`.
*   `TaskService.java`: O método correspondente no serviço também é ajustado para aceitar o objeto `Pageable` e repassá-lo para a camada de repositório. Ele retorna um objeto `Page<Task>`.
*   `TaskRepository.java`: Como esta interface estende `JpaRepository`, o método `findAll(Pageable pageable)` já está disponível e pronto para ser usado, sem necessidade de implementação manual.

**Fluxo de Execução:**
1.  O cliente faz uma requisição `GET` para `/api/tasks?page=0&size=10&sort=description,asc`.
2.  O Spring MVC detecta os parâmetros `page`, `size` e `sort` e constrói um objeto `Pageable` contendo:
    *   Número da página: 0
    *   Tamanho da página: 10
    *   Informação de ordenação: ordenar pelo campo "description" de forma ascendente.
3.  O `TaskController` recebe este objeto `Pageable` pré-construído como argumento do método.
4.  O Controller chama `taskService.findAll(pageable)`.
5.  O Service chama `taskRepository.findAll(pageable)`.
6.  O Spring Data JPA traduz o objeto `Pageable` para uma query SQL otimizada, incluindo cláusulas como `LIMIT`, `OFFSET` e `ORDER BY` (específicas do dialeto do banco de dados).
7.  O banco de dados executa a query e retorna apenas a "fatia" de dados solicitada.
8.  O repositório encapsula o resultado em um objeto `Page`, que contém a lista de tarefas da página atual, bem como metadados (número total de elementos, número total de páginas, etc.).
9.  O Service converte o `Page<Task>` para um `Page<TaskDTO>` e o retorna.
10. O Controller serializa o objeto `Page` em uma resposta JSON estruturada, que é enviada ao cliente.

**Dependências Específicas (`pom.xml`):**
A funcionalidade de Paginação e Ordenação é nativa do Spring Data JPA, portanto, a dependência principal é a mesma do módulo CRUD.
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
```

#### Configurações e Arquivos Externos

Não são necessárias configurações adicionais em `application.properties` para habilitar a paginação e a ordenação. Trata-se de uma funcionalidade do framework ativada pelo uso correto das interfaces e tipos de dados no código.

**Exemplo de Requisição do Cliente:**
*   `GET /api/tasks?page=1&size=20`: Retorna a segunda página (`page=1`) com 20 itens.
*   `GET /api/tasks?sort=createdAt,desc`: Retorna a primeira página (padrão) ordenada pela data de criação em ordem decrescente.
*   `GET /api/tasks?page=0&size=5&sort=priority,desc&sort=description,asc`: Retorna a primeira página com 5 itens, ordenados primeiro por prioridade (decrescente) e depois por descrição (ascendente).

---

## 05. Tratamento Global de Exceções com @RestControllerAdvice

### Título: Tratamento Global de Exceções com @RestControllerAdvice

**Conceito Central:** Este módulo implementa um mecanismo centralizado para capturar e tratar exceções lançadas pela aplicação. O objetivo é evitar blocos `try-catch` repetitivos nos controllers, padronizar as respostas de erro enviadas aos clientes da API e desacoplar a lógica de tratamento de erro da lógica de negócio.

#### Análise de Código e Estrutura

**Classes Principais Envolvidas:**
*   `RestExceptionHandler.java`: Uma classe anotada com `@RestControllerAdvice`. Esta anotação a transforma em um interceptador global para exceções ocorridas em qualquer `@RestController`.
*   Métodos com `@ExceptionHandler`: Dentro da classe `RestExceptionHandler`, são criados métodos específicos para tratar diferentes tipos de exceções. Cada método é anotado com `@ExceptionHandler(value = {NomeDaExcecao.class})`.
*   `CustomNotFoundException.java`: Um exemplo de exceção customizada (RuntimeException) que pode ser lançada pela camada de serviço quando um recurso não é encontrado.
*   `ErrorResponse.java`: Um POJO (Plain Old Java Object) usado para padronizar o corpo da resposta de erro. Geralmente contém campos como `timestamp`, `status`, `error`, `message` e `path`.

**Fluxo de Execução:**
1.  Um cliente faz uma requisição `GET` para `/api/tasks/999`, onde o ID 999 não existe.
2.  O `TaskController` chama `taskService.findById(999)`.
3.  O `TaskService` não encontra a tarefa e lança `new CustomNotFoundException("Task with ID 999 not found")`.
4.  Como a exceção não é tratada no controller, ela se propaga para cima.
5.  O componente `@RestControllerAdvice` (`RestExceptionHandler`) intercepta a exceção.
6.  Ele procura um método `@ExceptionHandler` que corresponda ao tipo da exceção lançada (`CustomNotFoundException`).
7.  O método encontrado é executado. Ele cria uma instância de `ErrorResponse`, preenche com os detalhes do erro e retorna um `ResponseEntity` com o status HTTP apropriado (ex: `HttpStatus.NOT_FOUND`).
8.  O cliente recebe uma resposta `HTTP 404 Not Found` com um corpo JSON padronizado.

**Dependências Específicas (`pom.xml`):**
A funcionalidade é parte do Spring Web, então a dependência principal é:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

#### Configurações e Arquivos Externos

Não são necessárias configurações em `application.properties` para esta funcionalidade. A "mágica" acontece através das anotações do Spring (`@RestControllerAdvice`, `@ExceptionHandler`).

**Exemplo de Implementação `RestExceptionHandler.java`:**
```java
@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(CustomNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(CustomNotFoundException ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                ex.getMessage(),
                request.getDescription(false)
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class) // Fallback para qualquer outra exceção
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "An unexpected error occurred.",
                request.getDescription(false)
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
```
Esta abordagem garante que, independentemente de onde o erro aconteça, a resposta para o cliente será sempre consistente e informativa, sem expor detalhes internos da implementação (stack traces).

---

## 06. Testes de Integração para Endpoints REST

### Título: Testes de Integração para Endpoints REST

**Conceito Central:** Este módulo demonstra como escrever e executar testes de integração para a API REST. Diferente dos testes unitários, os testes de integração verificam a colaboração entre múltiplas camadas da aplicação (Controller, Service, Repository) e, opcionalmente, com componentes de infraestrutura como um banco de dados real. O objetivo é garantir que os endpoints funcionem corretamente de ponta a ponta.

#### Análise de Código e Estrutura

**Classes Principais Envolvidas:**
*   `TaskControllerIT.java` (ou `TaskControllerIntegrationTest.java`): A classe de teste, localizada em `src/test/java`. Ela é anotada com `@SpringBootTest` para carregar o contexto completo da aplicação Spring.
*   `MockMvc`: Um objeto fornecido pelo Spring Test que permite simular requisições HTTP para os controllers sem a necessidade de um servidor web real em execução. É a ferramenta principal para testar os endpoints.
*   `@AutoConfigureMockMvc`: Anotação que configura automaticamente o bean `MockMvc`.
*   `@ActiveProfiles("test")`: Anotação para ativar um perfil de configuração específico para testes, permitindo, por exemplo, usar um banco de dados em memória ou um schema de banco de dados separado.
*   `@Testcontainers`: Anotação que integra o framework Testcontainers, permitindo iniciar containers Docker (ex: um banco de dados PostgreSQL) especificamente para a execução dos testes.

**Fluxo de Execução de um Teste:**
1.  O JUnit (framework de testes) inicia a execução da classe `TaskControllerIT`.
2.  A anotação `@SpringBootTest` carrega o ApplicationContext do Spring, inicializando todos os beans (controllers, services, etc.).
3.  Se `@Testcontainers` for usado, um container de banco de dados é iniciado antes de todos os testes. As propriedades de `datasource` são sobrescritas dinamicamente para apontar para este banco de dados temporário.
4.  Um método de teste, anotado com `@Test`, é executado.
5.  O teste usa o `MockMvc` para construir e disparar uma requisição HTTP (ex: `mockMvc.perform(post("/api/tasks")...)`).
6.  A requisição passa por toda a stack da aplicação Spring: `Controller` -> `Service` -> `Repository` -> Banco de Dados (de teste).
7.  A resposta HTTP é retornada ao `MockMvc`.
8.  O teste então usa métodos de asserção (ex: `andExpect(status().isCreated())`, `andExpect(jsonPath("$.description").value("New Task"))`) para verificar se o status da resposta e o corpo JSON estão corretos.
9.  Após a execução dos testes, o container do Testcontainers é desligado.

**Dependências Específicas (`pom.xml`):**
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <!-- Para usar Testcontainers com PostgreSQL -->
    <groupId>org.testcontainers</groupId>
    <artifactId>postgresql</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>junit-jupiter</artifactId>
    <scope>test</scope>
</dependency>
```

#### Configurações e Arquivos Externos

**Configurações (`src/test/resources/application-test.properties`):**
Quando `@ActiveProfiles("test")` é usado, o Spring procura por este arquivo para sobrescrever as configurações principais.
```properties
# Exemplo usando um banco de dados H2 em memória para testes rápidos
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=password
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
```

**Exemplo de Teste com `MockMvc`:**
```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TaskControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper; // Para converter objetos em JSON

    @Test
    void shouldCreateTaskSuccessfully() throws Exception {
        TaskDTO newTask = new TaskDTO(null, "Test creating a task", "PENDING");

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newTask)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.description").value("Test creating a task"));
    }
}
```

---

## 07. Testes Unitários com Mockito

### Título: Testes Unitários com Mockito

**Conceito Central:** Este módulo foca em escrever testes unitários, que verificam o comportamento de uma única unidade de código (uma classe ou método) de forma isolada. Diferente dos testes de integração, as dependências externas da unidade sob teste são substituídas por "mocks" (objetos simulados). O objetivo é validar a lógica interna de uma classe, como a classe de serviço, de forma rápida e precisa, sem depender de banco de dados, rede ou outras partes do sistema.

#### Análise de Código e Estrutura

**Classes Principais Envolvidas:**
*   `TaskServiceTest.java`: A classe de teste para `TaskService`. Não utiliza anotações do Spring como `@SpringBootTest`, pois não queremos carregar o contexto da aplicação.
*   `@InjectMocks`: Anotação do Mockito usada para criar uma instância da classe que queremos testar (`TaskService`). Mockito tentará injetar os mocks criados com `@Mock` nesta instância.
*   `@Mock`: Anotação do Mockito para criar um mock (um objeto falso) de uma dependência. No caso de `TaskServiceTest`, teríamos `@Mock TaskRepository taskRepository;`.
*   `@ExtendWith(MockitoExtension.class)`: Anotação do JUnit 5 que inicializa os mocks e injetações do Mockito antes de cada teste.
*   `Mockito.when(...).thenReturn(...)`: A sintaxe principal do Mockito para definir o comportamento de um mock. "Quando o método X for chamado com os parâmetros Y, então retorne Z".
*   `Mockito.verify(...)`: A sintaxe para verificar se um método de um mock foi chamado um determinado número de vezes com parâmetros específicos.

**Fluxo de Execução de um Teste Unitário (`createTask`):**
1.  A classe de teste `TaskServiceTest` é iniciada pelo JUnit.
2.  `@ExtendWith(MockitoExtension.class)` cria um mock de `TaskRepository` e uma instância real de `TaskService`, injetando o repositório mockado no serviço.
3.  O método de teste `@Test` é executado.
4.  **Arrange (Organização):**
    *   Um `TaskDTO` de entrada e uma entidade `Task` esperada são criados.
    *   O comportamento do mock é definido: `when(taskRepository.save(any(Task.class))).thenReturn(savedTask);`. Isso instrui o mock: "quando o método `save` for chamado com qualquer objeto `Task`, retorne o objeto `savedTask`".
5.  **Act (Ação):**
    *   O método a ser testado é chamado: `taskService.createTask(inputDto);`.
6.  **Assert (Verificação):**
    *   Usa-se asserções do JUnit (`Assertions.assertEquals(...)`) para verificar se o resultado retornado pelo serviço é o esperado.
    *   Usa-se `verify(taskRepository, times(1)).save(any(Task.class));` para garantir que a camada de persistência foi chamada exatamente uma vez.

**Dependências Específicas (`pom.xml`):**
O `spring-boot-starter-test` já inclui o JUnit 5 e o Mockito.
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
```

#### Configurações e Arquivos Externos

Testes unitários são autocontidos e não requerem arquivos de configuração (`application.properties`) ou infraestrutura externa (banco de dados, Docker). Eles são projetados para serem executados em qualquer ambiente, de forma extremamente rápida.

**Exemplo de Teste Unitário para `TaskService`:**
```java
@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    @Test
    void shouldCreateTaskWhenGivenValidDTO() {
        // Arrange
        TaskDTO inputDTO = new TaskDTO(null, "Unit test task", "PENDING");
        Task taskToSave = new Task();
        taskToSave.setDescription("Unit test task");
        taskToSave.setStatus("PENDING");

        Task savedTask = new Task();
        savedTask.setId(1L);
        savedTask.setDescription("Unit test task");
        savedTask.setStatus("PENDING");

        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);

        // Act
        TaskDTO resultDTO = taskService.createTask(inputDTO);

        // Assert
        assertNotNull(resultDTO);
        assertEquals(1L, resultDTO.getId());
        assertEquals("Unit test task", resultDTO.getDescription());

        // Verify interaction
        verify(taskRepository, times(1)).save(any(Task.class));
    }
}
```

---

## 08. Integração com API Externa (PokeAPI)

### Título: Integração com API Externa (PokeAPI)

**Conceito Central:** Este módulo demonstra como consumir dados de uma API REST externa pública, a PokeAPI. O objetivo é mostrar as práticas para realizar requisições HTTP a partir de uma aplicação Spring Boot, desserializar a resposta JSON em objetos Java e integrar esses dados externos na lógica de negócio da aplicação.

#### Análise de Código e Estrutura

**Classes Principais Envolvidas:**
*   `PokemonClient.java` ou `PokeApiClient.java`: Uma classe `@Service` ou `@Component` responsável exclusivamente por se comunicar com a PokeAPI. Isso isola a lógica de acesso externo.
*   `RestTemplate` ou `WebClient`: As ferramentas do Spring para fazer chamadas HTTP. `WebClient` (do Spring WebFlux) é a abordagem moderna e não-bloqueante, preferível para aplicações reativas ou que necessitam de alta concorrência. `RestTemplate` é a abordagem tradicional, síncrona.
*   `PokemonDTO.java`: Um DTO que espelha a estrutura do JSON retornado pela PokeAPI para um Pokémon específico. Apenas os campos de interesse são mapeados. A anotação `@JsonIgnoreProperties(ignoreUnknown = true)` é útil para ignorar campos do JSON que não foram mapeados no DTO.
*   `SomeService.java`: Um serviço de negócio que utiliza o `PokemonClient` para enriquecer seus próprios dados. Por exemplo, poderia haver um caso de uso para associar uma "Task" a um "Pokémon".
*   `PokemonController.java`: Um novo controller que expõe um endpoint (ex: `GET /pokemon/{name}`) para que os clientes da nossa API possam buscar dados de Pokémon através dela, agindo como um proxy.

**Fluxo de Execução (Buscando um Pokémon):**
1.  Uma requisição chega ao `PokemonController` em `GET /pokemon/ditto`.
2.  O controller chama `pokemonClient.getPokemonByName("ditto")`.
3.  Dentro do `PokemonClient`, o `WebClient` (ou `RestTemplate`) é usado para construir e executar uma requisição HTTP `GET` para `https://pokeapi.co/api/v2/pokemon/ditto`.
4.  A PokeAPI responde com um corpo JSON detalhado sobre o Pokémon "ditto".
5.  O `WebClient` / `RestTemplate`, com a ajuda do Jackson (biblioteca de (de)serialização), converte automaticamente a resposta JSON em uma instância de `PokemonDTO.java`.
6.  O `PokemonClient` retorna o objeto `PokemonDTO` preenchido.
7.  O `PokemonController` recebe o DTO e o retorna como resposta da sua própria API.

**Dependências Específicas (`pom.xml`):**
Para `WebClient` (recomendado):
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webflux</artifactId>
</dependency>
```
Para `RestTemplate` (tradicional), `spring-boot-starter-web` é suficiente.

#### Configurações e Arquivos Externos

**Configurações (`application.properties`):**
É uma boa prática externalizar a URL base da API externa para facilitar a mudança entre ambientes (desenvolvimento, produção) ou a substituição por mocks em testes.
```properties
# URL base da API externa
pokeapi.base-url=https://pokeapi.co/api/v2
```

**Exemplo de Implementação com `WebClient`:**
Primeiro, um bean de `WebClient` é configurado:
```java
@Configuration
public class WebClientConfig {

    @Value("${pokeapi.base-url}")
    private String pokeApiBaseUrl;

    @Bean
    public WebClient pokeApiWebClient() {
        return WebClient.builder()
                .baseUrl(pokeApiBaseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
```
Depois, o cliente o utiliza:
```java
@Service
public class PokemonClient {

    private final WebClient pokeApiWebClient;

    public PokemonClient(WebClient pokeApiWebClient) {
        this.pokeApiWebClient = pokeApiWebClient;
    }

    public Mono<PokemonDTO> getPokemonByName(String name) {
        return pokeApiWebClient.get()
                .uri("/pokemon/{name}", name)
                .retrieve()
                .bodyToMono(PokemonDTO.class);
    }
}
```
**Nota:** O uso de `Mono` indica a natureza assíncrona e reativa do `WebClient`.

---

## 09. Implementação de Padrão Circuit Breaker com Resilience4J

### Título: Implementação de Padrão Circuit Breaker com Resilience4J

**Conceito Central:** Este módulo demonstra a aplicação do padrão de projeto "Circuit Breaker" para aumentar a resiliência da aplicação ao interagir com serviços externos (como a PokeAPI do módulo anterior). O objetivo é evitar que falhas em cascata derrubem o sistema. Quando um serviço externo começa a falhar repetidamente, o "circuito" se abre, e as chamadas subsequentes falham imediatamente (fast-fail) sem tentar contatar o serviço, dando-lhe tempo para se recuperar.

#### Análise de Código e Estrutura

**Classes Principais Envolvidas:**
*   `PokemonClient.java`: A classe que faz a chamada para a API externa. O método que realiza a chamada HTTP (`getPokemonByName`) é anotado com `@CircuitBreaker`.
*   `@CircuitBreaker(name = "pokeApi", fallbackMethod = "getPokemonFallback")`: Esta anotação do Resilience4J "envolve" o método.
    *   `name`: Identificador da configuração do circuit breaker, definido no `application.yml`.
    *   `fallbackMethod`: Especifica um método a ser chamado caso o circuito esteja aberto ou a chamada original falhe.
*   `getPokemonFallback(...)`: O método de fallback. Ele deve ter a mesma assinatura do método original, com um parâmetro adicional opcional para receber a exceção que causou a falha. Ele retorna uma resposta padrão/cacheada, evitando que o erro se propague para o usuário final.

**Fluxo de Execução:**
1.  **Circuito Fechado (Estado Normal):** A aplicação chama `pokemonClient.getPokemonByName("pikachu")`. Resilience4J permite que a chamada prossiga para a PokeAPI. Se a chamada for bem-sucedida, nada muda. Se falhar, Resilience4J registra a falha.
2.  **Transição para Aberto:** Se a taxa de falhas em uma janela de tempo atinge um limiar configurado (ex: 50% de falhas nas últimas 20 chamadas), o circuit breaker transita para o estado **Aberto**.
3.  **Circuito Aberto:** Agora, qualquer chamada para `getPokemonByName` falhará imediatamente. Resilience4J nem sequer tentará contatar a PokeAPI. Em vez disso, ele redirecionará a execução diretamente para o método `getPokemonFallback`. Este método pode retornar um objeto `PokemonDTO` de um cache, um valor padrão, ou lançar uma exceção mais amigável.
4.  **Transição para Meio-Aberto:** Após um tempo de espera configurado (ex: 30 segundos), o circuito transita para **Meio-Aberto**.
5.  **Circuito Meio-Aberto:** Resilience4J permite que um número limitado de chamadas de teste passe para a PokeAPI.
    *   Se essas chamadas forem bem-sucedidas, o circuito volta para **Fechado**.
    *   Se elas falharem, o circuito volta para **Aberto**, e o tempo de espera recomeça.

**Dependências Específicas (`pom.xml`):**
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-circuitbreaker-resilience4j</artifactId>
</dependency>
<dependency>
    <!-- Necessário para usar anotações e aspectos -->
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-aop</artifactId>
</dependency>
```

#### Configurações e Arquivos Externos

**Configurações (`application.yml`):**
A configuração do Resilience4J é tipicamente feita em formato YAML por ser mais legível para estruturas aninhadas.
```yaml
resilience4j.circuitbreaker:
  instances:
    # Este nome deve corresponder ao 'name' na anotação @CircuitBreaker
    pokeApi:
      # Porcentagem de falhas para abrir o circuito. (Ex: 50%)
      failure-rate-threshold: 50
      # Duração em que o circuito permanece aberto antes de ir para meio-aberto.
      wait-duration-in-open-state: 30s
      # Número de chamadas permitidas no estado meio-aberto.
      permitted-number-of-calls-in-half-open-state: 5
      # Tamanho da janela deslizante usada para calcular a taxa de falha.
      sliding-window-size: 20
      # Tipo de janela deslizante (COUNT_BASED ou TIME_BASED).
      sliding-window-type: COUNT_BASED
      # Exceções que devem ser contadas como falha.
      record-exceptions:
        - java.io.IOException
        - java.util.concurrent.TimeoutException
        - org.springframework.web.client.ResourceAccessException

resilience4j.timelimiter:
  instances:
    pokeApi:
      # Define um timeout para a chamada.
      timeout-duration: 2s
```

**Exemplo de Implementação no Cliente:**
```java
@Service
public class PokemonClient {
    // ... construtor com WebClient

    @CircuitBreaker(name = "pokeApi", fallbackMethod = "getPokemonFallback")
    @TimeLimiter(name = "pokeApi") // Combina com um timeout
    public CompletableFuture<PokemonDTO> getPokemonByName(String name) {
        return CompletableFuture.supplyAsync(() ->
                pokeApiWebClient.get()
                        .uri("/pokemon/{name}", name)
                        .retrieve()
                        .bodyToMono(PokemonDTO.class)
                        .block() // .block() é usado aqui para o exemplo com CompletableFuture
        );
    }

    // Método de Fallback
    public CompletableFuture<PokemonDTO> getPokemonFallback(String name, Throwable t) {
        System.out.println("Fallback acionado para o pokemon: " + name + ". Erro: " + t.getMessage());
        // Retorna um objeto padrão ou de um cache
        PokemonDTO fallbackPokemon = new PokemonDTO();
        fallbackPokemon.setName("Fallback-mon");
        fallbackPokemon.setId(0);
        return CompletableFuture.completedFuture(fallbackPokemon);
    }
}
```
**Nota:** O uso de `CompletableFuture` junto com `@TimeLimiter` é uma prática comum para garantir que chamadas lentas também sejam tratadas como falhas.

---

## 10. Expondo APIs com gRPC e GraphQL

### Título: Expondo APIs com gRPC e GraphQL

**Conceito Central:** Este módulo explora duas alternativas modernas às APIs REST tradicionais: gRPC e GraphQL. O objetivo é estudar e implementar ambas as abordagens para entender suas vantagens e casos de uso.
*   **GraphQL:** Uma linguagem de consulta para APIs que permite aos clientes solicitar exatamente os dados de que precisam, e nada mais. Ideal para front-ends complexos e aplicações móveis.
*   **gRPC:** Um framework de RPC (Remote Procedure Call) de alta performance do Google. Usa Protocol Buffers para serialização e HTTP/2 para transporte, sendo excelente para comunicação de baixa latência entre microserviços.

#### Análise de Código e Estrutura: GraphQL

**Classes e Arquivos Principais Envolvidos:**
*   `schema.graphqls`: Arquivo de esquema localizado em `src/main/resources/graphql/`. Aqui se define a "forma" da API: os tipos de dados (`Task`), as consultas (`Query`) e as mutações (`Mutation`).
*   `TaskGraphqlController.java`: Uma classe Java anotada com `@Controller` (do Spring, não `@RestController`). Os métodos nesta classe são mapeados para os campos do esquema GraphQL.
    *   `@QueryMapping`: Anotação para métodos que respondem a consultas (`Query`). Ex: um método `taskById` é mapeado para a query `taskById(id: ID!)` no schema.
    *   `@MutationMapping`: Anotação para métodos que modificam dados (`Mutation`). Ex: `createTask`.
    *   `@SchemaMapping`: Usado para resolver campos aninhados. Por exemplo, se uma `Task` tem um campo `assignee` que é do tipo `User`, um método `@SchemaMapping` pode buscar os detalhes do `User` quando ele for solicitado na query.
*   `TaskService.java`: O mesmo serviço de negócio já existente é reutilizado para fornecer os dados para o controller GraphQL.

**Fluxo de Execução (GraphQL Query):**
1.  O cliente envia uma requisição `POST` para o endpoint padrão do GraphQL (`/graphql`) com a query no corpo. Ex: `{ "query": "{ taskById(id: 1) { id description } }" }`.
2.  O framework Spring for GraphQL recebe a requisição.
3.  Ele parseia a query e identifica que o campo `taskById` da `Query` raiz precisa ser resolvido.
4.  Ele invoca o método anotado com `@QueryMapping("taskById")` no `TaskGraphqlController`.
5.  O controller chama o `taskService` para buscar a tarefa.
6.  O framework recebe o objeto `Task` do controller e seleciona apenas os campos solicitados na query (`id` e `description`).
7.  Uma resposta JSON com a estrutura exata da query é montada e enviada de volta ao cliente.

**Dependências Específicas (`pom.xml` para GraphQL):**
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-graphql</artifactId>
</dependency>
```

#### Análise de Código e Estrutura: gRPC

**Classes e Arquivos Principais Envolvidos:**
*   `task.proto`: Arquivo de definição de serviço em `src/main/proto/`. Usa a sintaxe do Protocol Buffers para definir:
    *   **Serviços (Services):** `service TaskService {...}`.
    *   **Métodos (RPCs):** `rpc GetTask (TaskRequest) returns (TaskResponse);`.
    *   **Mensagens (Messages):** A estrutura dos dados de requisição e resposta (`TaskRequest`, `TaskResponse`).
*   **Código Gerado:** O plugin do Maven/Gradle para Protocol Buffers compila o arquivo `.proto` e gera automaticamente classes Java (stubs) para serviços, mensagens e clientes.
*   `TaskGrpcService.java`: Uma classe Java que **estende a classe base de serviço gerada** (ex: `TaskServiceImplBase`) e implementa a lógica dos métodos RPC. É anotada com `@GrpcService`.
*   `TaskService.java`: O serviço de negócio existente é reutilizado pelo `TaskGrpcService` para acessar os dados.

**Fluxo de Execução (gRPC Call):**
1.  Um cliente gRPC (que também usou o `.proto` para gerar seu código) cria uma mensagem de requisição (`TaskRequest`) e chama o método `getTask` no stub do cliente.
2.  O stub do cliente serializa a mensagem `TaskRequest` em formato binário (Protocol Buffers) e a envia via HTTP/2 para o servidor gRPC.
3.  O servidor gRPC na aplicação Spring recebe a chamada, desserializa a mensagem e invoca o método `getTask` correspondente na classe `TaskGrpcService`.
4.  O `TaskGrpcService` executa sua lógica (chamando o `taskService` de negócio), constrói uma mensagem de resposta (`TaskResponse`) e a retorna.
5.  O servidor serializa a `TaskResponse` e a envia de volta ao cliente.
6.  O stub do cliente recebe a resposta, a desserializa em um objeto Java e a entrega ao código do cliente.

**Dependências Específicas (`pom.xml` para gRPC):**
```xml
<dependency>
    <groupId>net.devh</groupId>
    <artifactId>grpc-server-spring-boot-starter</artifactId>
    <version>2.14.0.RELEASE</version> <!-- Usar a versão apropriada -->
</dependency>
<!-- Dependências do protobuf e gRPC para o código gerado -->
<dependency>
    <groupId>io.grpc</groupId>
    <artifactId>grpc-protobuf</artifactId>
    <version>${grpc.version}</version>
</dependency>
<dependency>
    <groupId>io.grpc</groupId>
    <artifactId>grpc-stub</artifactId>
    <version>${grpc.version}</version>
</dependency>
```
É necessário também configurar o `protobuf-maven-plugin` na seção `<build>` do `pom.xml` para gerar o código a partir dos arquivos `.proto`.

#### Configurações e Arquivos Externos

**GraphQL (`application.properties`):**
```properties
# Habilita o endpoint padrão /graphql
spring.graphql.path=/graphql
# Habilita a ferramenta GraphiQL para testar queries no browser
spring.graphql.graphiql.enabled=true
```

**gRPC (`application.properties`):**
```properties
# Porta em que o servidor gRPC irá rodar.
# É comum rodar em uma porta diferente da API REST.
grpc.server.port=9090
```
---

## 12. Cache de Serviço para PokeApi usando Redis

### Título: Cache de Serviço para PokeApi usando Redis

**Conceito Central:** Este módulo implementa uma estratégia de cache para otimizar a integração com a PokeAPI. O objetivo é armazenar temporariamente os resultados das chamadas à API externa em um cache rápido (Redis) para reduzir a latência, diminuir a carga na API externa e melhorar a resiliência da nossa aplicação caso a PokeAPI fique indisponível.

#### Análise de Código e Estrutura

**Classes Principais Envolvidas:**
*   `Application.java` (Classe principal): Deve ser anotada com `@EnableCaching` para habilitar a infraestrutura de cache do Spring.
*   `PokemonClient.java` (ou um `PokemonService` que o envolve): A classe cujo método será cacheado.
*   `@Cacheable`: Anotação colocada sobre o método que busca os dados da PokeAPI (ex: `getPokemonByName`).
    *   `value` ou `cacheNames`: Define o nome do cache (ex: `"pokemon"`). No Redis, isso pode se tornar um prefixo para as chaves.
    *   `key`: Uma expressão SpEL (Spring Expression Language) que define como a chave de cache será gerada a partir dos parâmetros do método. Ex: `"#name"` usará o valor do parâmetro `name` como chave.
*   `@CacheEvict`: Anotação para invalidar/remover entradas do cache. Útil se houvesse uma operação de atualização.
*   `@CachePut`: Anotação que sempre executa o método e atualiza o cache com o resultado. Útil para operações de update.

**Fluxo de Execução (Com Cache):**
1.  A primeira chamada é feita para `pokemonClient.getPokemonByName("pikachu")`.
2.  O aspecto de cache do Spring intercepta a chamada. Ele gera uma chave (ex: `"pikachu"`) e verifica se existe uma entrada com essa chave no cache `"pokemon"` no Redis.
3.  **Cache Miss:** Como é a primeira chamada, a chave não é encontrada no Redis.
4.  O método original `getPokemonByName` é executado, fazendo a chamada HTTP para a PokeAPI.
5.  A resposta da PokeAPI (`PokemonDTO`) é recebida.
6.  **Antes de retornar o resultado**, o aspecto de cache armazena o objeto `PokemonDTO` no Redis, associado à chave `"pikachu"`. O objeto é serializado (geralmente para JSON) antes de ser armazenado.
7.  O `PokemonDTO` é retornado ao chamador.
8.  Uma segunda chamada é feita para `pokemonClient.getPokemonByName("pikachu")`.
9.  O aspecto de cache intercepta novamente, gera a chave `"pikachu"` e verifica o cache no Redis.
10. **Cache Hit:** Desta vez, a chave é encontrada. O valor (o JSON do `PokemonDTO`) é lido do Redis, desserializado de volta para um objeto `PokemonDTO`.
11. **O método original `getPokemonByName` NÃO é executado.** A chamada à PokeAPI é pulada.
12. O `PokemonDTO` recuperado do cache é retornado imediatamente ao chamador.

**Dependências Específicas (`pom.xml`):**
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-cache</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```

#### Configurações e Arquivos Externos

**Configurações (`application.properties`):**
```properties
# Habilita o provedor de cache Redis
spring.cache.type=redis

# Configurações de conexão com o Redis
spring.redis.host=localhost
spring.redis.port=6379
# spring.redis.password= (se houver senha)

# Configurações específicas do cache Redis (opcional)
# Define um tempo de vida (Time To Live - TTL) de 10 minutos para o cache "pokemon"
spring.cache.redis.time-to-live=600000
# Prefixo para todas as chaves de cache (bom para evitar colisões em um Redis compartilhado)
spring.cache.redis.key-prefix=task-management::

# Para configurar TTLs por cache name, é necessário um @Bean de configuração
```
**Para TTLs específicos por cache, uma classe de configuração é melhor:**
```java
@Configuration
@EnableCaching
public class CacheConfig {
    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
        return (builder) -> builder
          .withCacheConfiguration("pokemon",
            RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(10)))
          .withCacheConfiguration("outraCoisa",
            RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(5)));
    }
}
```

**Arquivos Externos/Infraestrutura:**
*   **Servidor Redis:** A aplicação precisa de acesso a uma instância Redis. Isso pode ser gerenciado via Docker, adicionando um serviço `redis` ao `docker-compose.yml`.
    ```yaml
    # No docker-compose.yml
    services:
      redis:
        image: "redis:alpine"
        container_name: redis-cache
        ports:
          - "6379:6379"
    ```

---

## 19. Autenticação e Autorização com JWT e Keycloak

### Título: Autenticação e Autorização com JWT e Keycloak

**Conceito Central:** Este módulo implementa segurança na API usando o padrão OAuth 2.0 / OpenID Connect. O Keycloak é utilizado como um servidor de autorização centralizado para gerenciar usuários, roles (papéis) e emitir JSON Web Tokens (JWT). A aplicação Spring Boot atua como um "Resource Server", protegendo seus endpoints e validando os JWTs recebidos para autenticar usuários e autorizar o acesso com base em suas roles.

#### Análise de Código e Estrutura

**Classes Principais Envolvidas:**
*   `SecurityConfig.java`: A classe central de configuração de segurança. Anotada com `@Configuration` e `@EnableWebSecurity`.
*   `HttpSecurity`: Objeto configurado dentro de `SecurityConfig` para definir as regras de segurança.
    *   `.oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))`: Configura a aplicação para agir como um Resource Server que valida JWTs.
    *   `.authorizeHttpRequests(authz -> authz.requestMatchers(...).permitAll().requestMatchers(...).hasRole("ADMIN").anyRequest().authenticated())`: Define as regras de autorização para os endpoints.
*   `TaskController.java`: Os endpoints podem ser protegidos com anotações de segurança em nível de método, como `@PreAuthorize("hasRole('USER')")`. Requer a anotação `@EnableMethodSecurity` na classe `SecurityConfig`.
*   `JwtAuthConverter.java` (Customizado): Uma classe customizada que implementa `Converter<Jwt, AbstractAuthenticationToken>`. Essencial para extrair roles do local correto dentro do token do Keycloak (ex: `realm_access.roles`) e prefixá-las com `ROLE_` para que o Spring Security as entenda.

**Fluxo de Execução (Segurança):**
1.  Um cliente (ex: Postman, um front-end SPA) primeiro se autentica no Keycloak (fora da nossa API), fornecendo usuário e senha.
2.  O Keycloak valida as credenciais e retorna um `access_token` (um JWT).
3.  O cliente faz uma requisição para um endpoint protegido da nossa API (ex: `GET /api/tasks`), incluindo o token no header: `Authorization: Bearer <o_jwt_recebido>`.
4.  O `BearerTokenAuthenticationFilter` do Spring Security intercepta a requisição e extrai o JWT.
5.  O framework valida o token:
    *   Verifica a assinatura usando a chave pública do realm do Keycloak (que ele busca automaticamente a partir do `issuer-uri`).
    *   Verifica o `issuer` (emissor), `audience` (público) e a data de expiração do token.
6.  Se o token for válido, o `JwtAuthConverter` customizado é invocado. Ele lê as `roles` do token e cria um objeto `Authentication` (ex: `JwtAuthenticationToken`) com as "authorities" (ex: `ROLE_USER`).
7.  Este objeto `Authentication` é colocado no `SecurityContext`.
8.  A cadeia de filtros do Spring Security prossegue. O filtro de autorização verifica se as "authorities" no `SecurityContext` satisfazem a regra definida para o endpoint (`.anyRequest().authenticated()` ou `@PreAuthorize`).
9.  Se autorizado, a requisição chega ao controller. Se não, uma resposta `HTTP 401 Unauthorized` ou `HTTP 403 Forbidden` é retornada.

**Dependências Específicas (`pom.xml`):**
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
</dependency>
```

#### Configurações e Arquivos Externos

**Configurações (`application.yml`):**
```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          # URI do realm no Keycloak. O Spring usa isso para autoconfigurar
          # a validação, descobrindo o jwk-set-uri.
          issuer-uri: http://localhost:8081/realms/task-management-realm
          # Opcional, mas bom para especificar: o ID do cliente (audience) que pode usar este resource server.
          # O token JWT deve conter este valor na claim 'aud'.
          #audiences: task-api-client
```
#### Detalhe Especial: Integração e Configuração Keycloak

##### Finalidade da Integração
O Keycloak serve como um provedor de identidade e acesso (IAM) desacoplado. A finalidade é:
1.  **Autenticação:** Delegar a validação de credenciais de usuário ao Keycloak, tirando essa responsabilidade da API.
2.  **Autorização:** Utilizar as `roles` e `scopes` definidos no Keycloak para controlar o acesso aos endpoints da API, permitindo um gerenciamento centralizado de permissões.
3.  **Single Sign-On (SSO):** Permitir que múltiplos serviços confiem na mesma sessão de usuário gerenciada pelo Keycloak.

##### Configurações do Cliente (no Keycloak)
Para que a integração funcione, é preciso configurar o seguinte no painel de administração do Keycloak:
*   **Realm:** Um espaço isolado de configuração. Ex: `task-management-realm`.
*   **Client:** Uma representação da nossa aplicação (`task-management-app`) dentro do realm. Ex: `task-api-client`.
    *   **Access Type:** `confidential` (se a aplicação puder guardar um segredo) ou `public` (para SPAs). Para a API, `bearer-only` também é uma opção comum, indicando que ela apenas valida tokens.
    *   **Client ID:** `task-api-client`.
*   **Roles:** Papéis que podem ser atribuídos aos usuários. Ex: `USER`, `ADMIN`. Podem ser Realm Roles (globais) ou Client Roles (específicas do cliente).
*   **Mappers:** Para garantir que as roles sejam incluídas no JWT, é preciso configurar mappers no cliente (na aba "Client Scopes" ou "Mappers"), como "User Realm Role" para adicionar as `realm_access.roles` ao token.

##### Fluxo de Segurança (Abordando os pontos específicos)

*   **Validação do Token e Autorização:** O fluxo já foi descrito acima. A parte crucial é o `JwtAuthConverter` que lê as roles da claim `realm_access.roles` ou `resource_access.<client-id>.roles`.

    ```java
    // Exemplo de JwtAuthConverter
    @Component
    public class JwtAuthConverter implements Converter<Jwt, JwtAuthenticationToken> {
        @Override
        public JwtAuthenticationToken convert(Jwt jwt) {
            // Extrai as roles do token do Keycloak
            Map<String, Object> realmAccess = jwt.getClaim("realm_access");
            Collection<String> roles = (Collection<String>) realmAccess.get("roles");
            var grantedAuthorities = roles.stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                    .collect(Collectors.toSet());

            return new JwtAuthenticationToken(jwt, grantedAuthorities, jwt.getSubject());
        }
    }

    // Em SecurityConfig.java
    http.oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthConverter)));
    ```

*   **Custom Conditional in a Sub-flow:** Este é um recurso avançado do Keycloak, não do Spring Boot. Refere-se à criação de um autenticador condicional customizado no próprio Keycloak. O link do GitHub mostra como criar um `.jar` (um Service Provider Interface - SPI) para o Keycloak que permite, por exemplo, executar um fluxo de autenticação apenas se um determinado header HTTP estiver presente na requisição ao Keycloak. **Na documentação da nossa API Spring, isso é um detalhe de infraestrutura:** "A autenticação é condicionada por regras customizadas no Keycloak, como a presença do header 'X-Internal-Call', que é implementada via um SPI customizado no servidor de autenticação."

*   **Issue with object list in token:** Para incluir uma lista de objetos em um token, deve-se usar os "Mappers" do Keycloak. Crie um "Script Mapper" ou "User Attribute Mapper" que pega um atributo de usuário (que pode ser um JSON em formato string) e o adiciona ao token.
    *   **No Keycloak:** Adicione um atributo ao usuário, ex: `userProjects` com o valor `[{"id": 1, "name": "Project A"}, {"id": 2, "name": "Project B"}]`.
    *   **No Mapper:** Crie um "User Attribute" mapper para `userProjects`, defina o "Token Claim Name" (ex: `projects`), e configure o "Claim JSON Type" para `JSON`.
    *   **No Spring Boot:** Para acessar essa lista de objetos, você pode extraí-la diretamente do `Jwt` principal: `List<Map<String, Object>> projects = jwt.getClaim("projects");`.

*   **Override auth token:** Interpreto isso como a necessidade da API, após receber um token, obter um novo token em nome do usuário com permissões diferentes (Token Exchange) ou atualizar o token atual. A abordagem padrão seria o **Token Exchange**, uma extensão do OAuth 2.0. A API (atuando como cliente) faria uma requisição ao endpoint de token do Keycloak, trocando o token recebido por um novo, possivelmente para acessar outro serviço com um `audience` diferente. Isso requer configurar a política de permissão no Keycloak Admin Client.

*   **Request to Keycloak and update user attributes:** A API pode precisar interagir com a API Admin do Keycloak para, por exemplo, atualizar um atributo de um usuário após uma ação.
    *   **Dependência:** Adicionar `keycloak-admin-client`.
    *   **Configuração:** Criar um bean `Keycloak` configurado para se conectar à API Admin. Isso requer um cliente específico no Keycloak com role de `realm-admin` e o uso de `grant_type=client_credentials`.
    *   **Código:**
    ```java
    // Exemplo de serviço para interagir com o Keycloak Admin API
    @Service
    public class KeycloakAdminService {
        private final Keycloak keycloakAdminClient;

        // Construtor que injeta o bean do cliente admin

        public void updateUserAttribute(String userId, String attributeName, String attributeValue) {
            RealmResource realmResource = keycloakAdminClient.realm("task-management-realm");
            UserResource userResource = realmResource.users().get(userId);
            UserRepresentation user = userResource.toRepresentation();
            user.getAttributes().put(attributeName, Collections.singletonList(attributeValue));
            userResource.update(user);
        }
    }
    ```
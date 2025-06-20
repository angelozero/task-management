package com.angelozero.task.management.entity.integration;

import com.angelozero.task.management.entity.Person;
import com.angelozero.task.management.entity.Task;
import com.angelozero.task.management.entity.integration.config.PersonIntegrationTestConfig;
import com.angelozero.task.management.entity.integration.response.GraphQLResponse;
import com.angelozero.task.management.entity.status.Blocked;
import com.angelozero.task.management.entity.status.Pending;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
public class PersonIntegrationTest extends PersonIntegrationTestConfig {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @After
    public void after() {
        deletePersonData();
    }

    private static final String FIND_PERSON_BY_EMAIL_QUERY = """
            {
              "query": "query personByEmail($arg1: String!) { personByEmail(email: $arg1) { id name email profileInfo taskList { id description completed statusDescription statusCode } } }",
              "operationName": "personByEmail",
              "variables": { "arg1": "%s" }
            }
            """;

    private static final String FIND_PERSON_BY_ID_QUERY = """
            {
              "query": "query personById($arg1: String!) { personById(id: $arg1) { id name email profileInfo taskList { id description completed statusDescription statusCode } } }",
              "operationName": "personById",
              "variables": { "arg1": "%s" }
            }
            """;

    private static final String SAVE_PERSON_QUERY = """
            {
              "query": "mutation savePerson($input: PersonInput!) { savePerson(personInput: $input) { name email profileInfo taskList { id description completed statusDescription statusCode } } }",
              "operationName": "savePerson",
              "variables": {
                "input": {
                  "name": "name-1",
                  "email": "email-1",
                  "profileInfo": "profile-info-1",
                  "taskList": [
                    {
                      "description": "description-1",
                      "completed": true,
                      "statusCode": 2
                    }
                  ]
                }
              }
            }
            """;

    @Test
    @DisplayName("Integration Test - Should find a person by email")
    public void shouldFindAPersonByEmail() throws Exception {
        var taskList = List.of(new Task(null, "description-1", true, new Blocked()));
        var person = new Person(null, "name-1", "email-1", "profile-info-1", taskList);

        var savedPerson = savePerson(person);

        var response = mockMvc.perform(post("/graphql")
                        .content(String.format(FIND_PERSON_BY_EMAIL_QUERY, savedPerson.email()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        var graphQLResponse = objectMapper.readValue(response, GraphQLResponse.class);

        var personOutput = graphQLResponse.getData().getPersonByEmail();

        assertNotNull(personOutput);
        assertEquals(savedPerson.email(), personOutput.email());
        assertEquals(savedPerson.name(), personOutput.name());
        assertEquals("profile-info-1", personOutput.profileInfo());
        assertEquals(1, personOutput.taskList().size());
        assertEquals("description-1", personOutput.taskList().getFirst().description());
        assertEquals("Blocked", personOutput.taskList().getFirst().statusDescription());
        assertEquals(4, personOutput.taskList().getFirst().statusCode());
    }

    @Test
    @DisplayName("Integration Test - Should find a person by id")
    public void shouldFindAPersonById() throws Exception {
        var taskList = List.of(new Task(null, "description-1", true, new Pending()));
        var person = new Person(null, "name-1", "email-1", "profile-info-1", taskList);

        var savedPerson = savePerson(person);

        var response = mockMvc.perform(post("/graphql")
                        .content(String.format(FIND_PERSON_BY_ID_QUERY, savedPerson.id()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        var graphQLResponse = objectMapper.readValue(response, GraphQLResponse.class);

        var personOutput = graphQLResponse.getData().getPersonById();

        assertNotNull(personOutput);
        assertEquals(savedPerson.email(), personOutput.email());
        assertEquals(savedPerson.name(), personOutput.name());
        assertEquals("profile-info-1", personOutput.profileInfo());
        assertEquals(1, personOutput.taskList().size());
        assertEquals("description-1", personOutput.taskList().getFirst().description());
        assertEquals("Pending", personOutput.taskList().getFirst().statusDescription());
        assertEquals(1, personOutput.taskList().getFirst().statusCode());


    }

    @Test
    @DisplayName("Integration Test - Should save a person")
    public void shouldSavePerson() throws Exception {
        deletePersonData();
        var response = mockMvc.perform(post("/graphql")
                        .content(SAVE_PERSON_QUERY)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        var graphQLResponse = objectMapper.readValue(response, GraphQLResponse.class);

        var personOutput = graphQLResponse.getData().getSavePerson();

        assertNotNull(personOutput);
        assertEquals("email-1", personOutput.email());
        assertEquals("name-1", personOutput.name());
        assertEquals("profile-info-1", personOutput.profileInfo());
        assertEquals(1, personOutput.taskList().size());
        assertEquals("description-1", personOutput.taskList().getFirst().description());
        assertEquals(true, personOutput.taskList().getFirst().completed());
        assertEquals("In progress", personOutput.taskList().getFirst().statusDescription());
        assertEquals(2, personOutput.taskList().getFirst().statusCode());
    }
}

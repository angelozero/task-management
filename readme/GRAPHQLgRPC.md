- test in http://localhost:8080/graphiql?path=/graphql
  - Mutation
    ```javascript
      mutation {
        savePerson(personInput: {
          name: "angelo",
          email: "angelo@zero.com",
          profileInfo: "this is a test",
          taskList: [
            { id: null, description: "Task 1", completed: false, statusCode: 1 },
            { id: null, description: "Task 2", completed: true, statusCode: 2 },
            { id: null, description: "Task 3", completed: false, statusCode: 3 }
          ]}) {
          id
          email
        }
      }
    ```
    - response 
    ```json
    {
      "data": {
        "savePerson": {
          "id": "680e6afb9274711531393180",
          "email": "angelo@zero.com"
        }
      }
    }
    ```
  - Query
    ```javascript
    query {
      personByEmail(email: "angelo@zero.com"){
        profileInfo
        taskList {
          id
          description
          completed
          statusDescription
          statusCode
        }
      }
    }
    ```
    - response 
    ```json
    {
      "data": {
        "personByEmail": {
          "profileInfo": "this is a test",
          "taskList": [
            {
              "id": "682688edfeef93394b5a7921",
              "description": "Task 1",
              "completed": false,
              "statusDescription": "Pending",
              "statusCode": 1
            },
            {
              "id": "682688edfeef93394b5a7922",
              "description": "Task 2",
              "completed": true,
              "statusDescription": "In progress",
              "statusCode": 2
            },
            {
              "id": "682688edfeef93394b5a7923",
              "description": "Task 3",
              "completed": false,
              "statusDescription": "Completed",
              "statusCode": 3
            }
          ]
        }
      }
    }
    ```

- ![graphql.png](images/graphql.png)
- [Trying to use MockMvc to test a GraphQL API](https://github.com/spring-projects/spring-graphql/issues/779)
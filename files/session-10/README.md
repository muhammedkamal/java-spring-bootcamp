# Week 5, Session 5B: Layers, DTOs, Validation and Swagger

Slides: `../Session10_Layers_DTOs_Validation_and_Swagger.pptx`

Needs JDK 25 and Maven. Spring Boot 4.1.2, springdoc-openapi 3.2.0.

## Run the demo

```bash
cd files/session-10/task-api-v2
mvn spring-boot:run
mvn -q test            # 7 tests
```

| URL | What |
| --- | --- |
| <http://localhost:8080/api/tasks> | The API |
| <http://localhost:8080/swagger-ui.html> | Swagger UI |
| <http://localhost:8080/v3/api-docs> | Raw OpenAPI document |

`task-api-v2` is the Session 9 API split into controller, service and repository, with DTOs, Bean Validation, a `@RestControllerAdvice` returning RFC 9457 `ProblemDetail`, and springdoc. `requests.http` is included.

## Postman collection

Import `postman/Session10_TaskApi_v2.postman_collection.json`. It has 18 requests in three folders (happy path, validation errors, not found / conflict / wrong method). Every request has tests. Restart the app before each full run, because the happy path expects the 3 seed tasks.

## Lab: Library API

| Part | Task |
| --- | --- |
| `lab/starter/library-api` | TODO 1: validation on `CreateBookRequest`. TODO 2: `BookService`. TODO 3: `BookController`. TODO 4: `GlobalExceptionHandler` |

Rules: title not blank (max 120), author not blank, isbn 13 digits, year 1450 to 2100, copies zero or more. 404 for an unknown id, 409 for a duplicate ISBN, 400 for validation. Solution in `lab/solutions/library-api`. Use `requests.http` to check your work.

## Before the next session

1. Finish the Library API and make the Postman requests pass.
2. Explain every method of `GlobalExceptionHandler` out loud.
3. Next week: Spring Data JPA and a real PostgreSQL database.

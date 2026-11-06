# Week 5, Session 5A: Spring Core and the First REST API

Slides: `../Session09_Spring_Core_and_First_REST_API.pptx`

Needs JDK 25 and Maven. Spring Boot 4.1.2.

## Demos

| Path | Topic |
| --- | --- |
| `demos/DiByHand.java` | Dependency injection written by hand, no Spring (`java demos/DiByHand.java`) |
| `ioc-demo/` | The container: stereotypes, `@Configuration` / `@Bean`, `@ConfigurationProperties`, profiles, a `CommandLineRunner` |
| `task-api/` | The first REST API: Task model, GET, POST, PUT, DELETE, `ResponseEntity`, 4 tests |

```bash
cd files/session-09/ioc-demo
mvn spring-boot:run                                  # runs the demo and exits
mvn spring-boot:run -Dspring-boot.run.profiles=dev   # switch profile

cd ../task-api
mvn spring-boot:run     # http://localhost:8080/api/tasks
mvn -q test
```

`task-api/requests.http` has ready requests for IntelliJ.

## Labs

| Lab | Task |
| --- | --- |
| 1 | Refactor to dependency injection (`lab/starter/Lab1DiRefactor.java`) |
| 2 | Book API: seed, list with `author` and `sortBy`, get, count, create, replace, delete (`lab/starter/book-api`) |

Solutions are in `lab/solutions/`.

## Before the next session

1. Finish the Book API lab and commit it.
2. Re-read `TaskController` and be ready to explain why it does too many things.
3. Solve one LeetCode problem on hash maps.

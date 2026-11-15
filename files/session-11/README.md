# Week 6, Session 6A: Docker, PostgreSQL and Spring Data JPA

Slides: `../Session11_Docker_PostgreSQL_and_Spring_Data_JPA.pptx`
Study guide: [`STUDY-GUIDE.md`](STUDY-GUIDE.md)

Needs JDK 25, Maven and Docker (Docker Desktop, or colima on a Mac). Spring Boot 4.1.2, PostgreSQL 17, Flyway, springdoc-openapi 3.2.0. Run `../../scripts/check-env.sh` first. Setup help: [`../../environment/SETUP-WEEKS-6-8.md`](../../environment/SETUP-WEEKS-6-8.md).

## Run the demo

```bash
cd files/session-11/task-api-v3
mvn spring-boot:run      # starts PostgreSQL from compose.yaml for you, then runs Flyway V1..V5
mvn -q test              # 4 unit tests, no database needed
```

| URL | What |
| --- | --- |
| <http://localhost:8080/api/tasks?page=0&size=3&sort=dueDate,asc> | Paged, sorted tasks |
| <http://localhost:8080/api/tasks?done=false&priority=HIGH> | Optional filters |
| <http://localhost:8080/api/projects/1/tasks> | One project's tasks (JOIN FETCH) |
| <http://localhost:8080/api/tasks/stats> | Count per priority (a projection) |
| <http://localhost:8080/swagger-ui.html> | Swagger UI |
| <http://localhost:8081> | Adminer. System `PostgreSQL`, server `postgres`, user, password and database `tasks` |

Adminer starts with the rest of `compose.yaml`. Its `org.springframework.boot.ignore` label only tells Spring Boot not to treat it as a service to connect to.

`task-api-v3` is the Session 10 API with the in-memory repository replaced by PostgreSQL:

| Piece | Where |
| --- | --- |
| Entities and relations: `Project 1-* Task`, `Task *-* Tag` | `model/` |
| Auditing: `createdAt`, `updatedAt` filled for you | `JpaConfig`, `Task` |
| Schema and seed data as versioned SQL | `src/main/resources/db/migration/V1..V5` |
| Derived queries, JPQL, `@EntityGraph`, `JOIN FETCH`, a projection | `TaskRepository` |
| `Pageable` in, `PageResponse` out | `TaskController`, `dto/PageResponse` |
| `@Transactional(readOnly = true)` on the class, `@Transactional` on writes | `TaskService` |
| Database errors (unique, FK, check) become 409 | `GlobalExceptionHandler` |

The SQL log is on (`org.hibernate.SQL: DEBUG`). Keep the console open while you call the API: you should see one `select` per list call, not one per row.

## Lab: Library API on PostgreSQL

Start from `lab/starter/library-api`. It runs PostgreSQL on port **5433**, so it can run beside the demo.

| TODO | Task |
| --- | --- |
| 1 | `V4__create_loans.sql`: the `loans` table with two foreign keys |
| 2 | `Loan.java`: map it (`@Id`, two LAZY `@ManyToOne`, `@Column` names) |
| 3 | `LoanRepository`: two derived count queries and one `JOIN FETCH` query |
| 4 | `BookRepository.search`: optional author filter, paged, no N+1. Use it in `BookService` |
| 5 | `LoanService.borrow`: copies left, max 3 active loans, due date = today + 14 |
| 6 | `LoanService.giveBack`: 409 if already returned. No `save()` call needed |

The limits come from `application.yml` (`library.max-active-loans`, `library.loan-days`) through a `@ConfigurationProperties` record. When TODO 5 and 6 are done, remove `@Disabled` from the two test classes and run `mvn test`. Check the rest with `requests.http`. The instructor shares the solution after the session.

If the app does not start, read the first `Caused by:` line. `Schema validation: missing table` or `wrong column type` means your entity and your migration disagree.

## Bonus (Intern B track)

- Add `GET /api/loans/overdue` (due date before today, not returned) with a derived query.
- Add `V6__add_book_language.sql` with a column that has a default, and expose it in the DTOs.
- Turn on `spring.jpa.properties.hibernate.generate_statistics: true` and count the queries for `GET /api/books` before and after TODO 4.

## Before the next session

1. Finish the lab, including the tests.
2. Run `docker compose down -v`, then start the app again and watch Flyway rebuild the database from zero.
3. Work through the Session 11 part of the [study guide](STUDY-GUIDE.md).
4. Next session: Spring Security and JWT. Users will log in, and the API will know who is calling.

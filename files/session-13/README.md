# Week 7, Session 7A: Testing and Continuous Integration

Slides: `../Session13_Testing_and_CI.pptx`
Study guide: [`STUDY-GUIDE.md`](STUDY-GUIDE.md)

Needs JDK 25, Maven and Docker, same as Sessions 11 and 12. **Docker must be running for the tests.** Testcontainers starts a real PostgreSQL in Docker for every test run. Setup help, including the Colima environment variables: [`../../environment/SETUP-WEEKS-6-8.md`](../../environment/SETUP-WEEKS-6-8.md#testcontainers).

## Run the demo

```bash
cd files/session-13/task-api-v5
mvn test                 # unit tests and slice tests (fast, about 15 s)
mvn verify               # + integration tests (*IT) + coverage report + the 70% coverage gate
open target/site/jacoco/index.html
```

`task-api-v5` is the secured `task-api-v4` from Session 12, with no code changes. Only tests and build tooling are new:

| Test | Kind | What starts | What it proves |
| --- | --- | --- | --- |
| `service/TaskServiceTest` | Unit (Mockito) | Nothing: plain Java | The ownership rules in the service |
| `auth/TokenServiceTest` | Unit | Nothing | Refresh token rotation and reuse detection |
| `controller/TaskControllerTest` | Web slice `@WebMvcTest` | Controller, security, error handler. Service is a `@MockitoBean` | URLs, status codes, JSON, validation errors, 401 |
| `controller/AdminControllerTest` | Web slice | Same | USER gets 403 and ADMIN gets 200 on `/api/admin/users` |
| `repository/TaskRepositoryTest` | Data slice `@DataJpaTest` | JPA + Flyway on a **real PostgreSQL** (Testcontainers) | The JPQL queries, paging, the unique index |
| `AuthFlowIT` | Integration `@SpringBootTest` | The whole app + real PostgreSQL | Login, use the token, 404 for others' data, tampered token, refresh reuse |

The new build pieces:

| Piece | Where |
| --- | --- |
| Test dependencies: `spring-boot-starter-data-jpa-test`, `spring-boot-testcontainers`, `testcontainers-postgresql` | `pom.xml` |
| `*Test` runs in `mvn test` (Surefire), `*IT` runs in `mvn verify` (Failsafe) | `maven-failsafe-plugin` in `pom.xml` |
| Coverage report and a gate: the build fails below 70% line coverage | `jacoco-maven-plugin` in `pom.xml` |
| One PostgreSQL container for all tests, wired with `@ServiceConnection` | `src/test/.../TestcontainersConfiguration` |
| Run the app on a throw-away database, without compose | `TestTaskApiApplication` (`mvn spring-boot:test-run`) |
| GitHub Actions: `mvn verify` on every push and pull request | `.github/workflows/ci.yml` |

The test pyramid for this project, from many and fast at the bottom to few and slow at the top:

| Layer | Count | Time |
| --- | --- | --- |
| Integration (`*IT`) | 5 | about 9 s |
| Slices (web + data) | 13 | about 9 s, mostly starting the container |
| Unit | 10 | under 1 s |

Two things that surprise everyone:

1. `jwt()` from `spring-security-test` does **not** run your `JwtAuthenticationConverter`. It reads the `scope` claim, so `roles: [ADMIN]` gives you no `ROLE_ADMIN`. Pass `.authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))` yourself.
2. A slice test (`@WebMvcTest`, `@DataJpaTest`) does not run `@ConfigurationPropertiesScan`. When a bean you import needs a properties record, add `@EnableConfigurationProperties(CorsProperties.class)` to the test.

## Lab: test the Library API

Start from `lab/starter/library-api`. It is the Session 12 library solution with all the test tooling in the `pom.xml` and nine test methods to write. The code under test is finished, and you only write tests. The same accounts as Session 12 (password `secret123`):

| Account | Role | id | Has on loan |
| --- | --- | --- | --- |
| `sara@example.com` | MEMBER | 1 | "Animal Farm" (book 2, the only copy) |
| `omar@example.com` | MEMBER | 2 | nothing |
| `librarian@example.com` | LIBRARIAN | 3 | nothing |

| TODO | File | Test |
| --- | --- | --- |
| 1 | `controller/BookControllerTest` | A member reads a book: 200 and the JSON fields |
| 2 | same | No token: 401 and `WWW-Authenticate: Bearer` |
| 3 | same | A member adds a book: 403, and the service is never called |
| 4 | same | A librarian adds a book: 201 and the `Location` header |
| 5 | same | A bad ISBN: 400 with `errors.isbn` |
| 6 | `repository/BookRepositoryTest` | Search by author ignores case and sorts by year |
| 7 | same | A duplicate ISBN is rejected by the database (`saveAndFlush`) |
| 8 | `LoanFlowIT` | Log in, borrow, return, return again (409) |
| 9 | same | The last copy is out: 409 with the exact message |
| 10 | `pom.xml` + `.github/workflows/ci.yml` | Add the JaCoCo `check` (70% lines) and a CI workflow, push, and get a green check on GitHub |

Remove each `@Disabled` when its TODO is done. At the end, `mvn verify` must run **26 tests + 4 integration tests** with no skips and print `All coverage checks have been met`. The instructor shares the solution after the session.

Each test has a hint in its body. Read the finished tests next to it (`noFilterReturnsEveryBook`, `aMemberCannotBorrowForSomeoneElse`, ...) to see the style.

Make every test fail once on purpose. Break the code it tests, for example change `hasRole("LIBRARIAN")` to `authenticated()`, and check that the test goes red. A test that never fails proves nothing.

## Bonus (Intern B track)

- Add a `LoanControllerTest` (web slice) for `POST /api/loans`: 201, 400 when `bookId` is missing, 401 without a token.
- Add a test for the loan limit: a member with 3 active loans gets 409 on the 4th. Do it as an IT, and clean up after yourself so the other ITs still pass in any order.
- Add a status badge for your workflow to your repo's `README.md`.
- Use `@ParameterizedTest` with `@CsvSource` to check five invalid book bodies in one test.

## Before the next session

1. Finish the lab with a green CI run on GitHub. Send the link to your instructor.
2. Open the JaCoCo report and find one class under 50%. Write one test that covers a real rule in it, not just a line.
3. Work through the [study guide](STUDY-GUIDE.md).
4. Read the [final project guide](../final-project/README.md) and choose your project. Next session is the last one: caching, messaging, a gateway, Docker images and deployment, and the final project kick-off.

# Study guide: Session 13, Testing and Continuous Integration

Use this guide between Session 13 and Session 14. It takes about 2 hours a day for 3 days. Type the exercises yourself, and answer the self-check questions out loud before opening the answers.

## What you must be able to do after this guide

- [ ] Draw the test pyramid and place a unit test, a slice test and an integration test on it, with a rough speed for each.
- [ ] Write a Mockito unit test with Arrange, Act, Assert, using `when(...).thenReturn(...)`, `verify(...)` and `assertThatThrownBy(...)`.
- [ ] Write a `@WebMvcTest` that checks a status code, a header and a JSON field, with and without a token (`jwt()`).
- [ ] Write a `@DataJpaTest` on a real PostgreSQL with Testcontainers, and explain why we do not use H2.
- [ ] Write a `@SpringBootTest` integration test that logs in and uses the token.
- [ ] Explain Surefire and Failsafe (`mvn test` versus `mvn verify`) and read a JaCoCo report.
- [ ] Write a GitHub Actions workflow that runs `mvn verify` on every push, and read a failed run.

## Day 1: unit tests and the test pyramid (about 2 hours)

| # | Read | Why | Time |
| --- | --- | --- | --- |
| 1 | Martin Fowler, [The Practical Test Pyramid](https://martinfowler.com/articles/practical-test-pyramid.html), read up to "Contract Tests" | Why many small tests and few big ones, and what each layer is for | 40 min |
| 2 | JUnit 5 User Guide, [Writing Tests](https://docs.junit.org/current/user-guide/#writing-tests), read "Annotations", "Display Names", "Assertions" and "Parameterized Tests" | `@Test`, `@BeforeEach`, `@Disabled`, `@ParameterizedTest` | 25 min |
| 3 | AssertJ, [Core assertions guide](https://assertj.github.io/doc/#assertj-core-assertions-guide), read "Exception assertions" and "Iterable and array assertions" | `assertThat(list).extracting(...).containsExactly(...)`, `assertThatThrownBy` | 20 min |
| 4 | Mockito, [javadoc of the `Mockito` class](https://javadoc.io/doc/org.mockito/mockito-core/latest/org.mockito/org/mockito/Mockito.html), sections 1 to 4 and 12 | Stubbing, verifying, argument matchers, `doThrow` | 20 min |

**Practice (20 min).** In `task-api-v5`:

1. Run `mvn test` and time it. Then run only one class: `mvn test -Dtest=TaskServiceTest`. How long does that take?
2. In `TaskServiceTest`, break the rule under test: change `ProjectService.getVisible` so it skips the owner check. Which tests go red? Put it back.
3. Add a test: Omar asks for task 1 (Sara's) and gets `NotFoundException`. Use `assertThatThrownBy`.

## Day 2: slices and Testcontainers (about 2 hours)

| # | Read | Why | Time |
| --- | --- | --- | --- |
| 1 | Spring Boot reference, [Testing Spring Boot Applications](https://docs.spring.io/spring-boot/reference/testing/spring-boot-applications.html), read "Auto-configured Spring MVC Tests" and "Auto-configured Data JPA Tests" | What a slice loads and what it leaves out | 30 min |
| 2 | Spring Security reference, [Testing OAuth 2.0](https://docs.spring.io/spring-security/reference/servlet/test/mockmvc/oauth2.html), read "Testing JWT Authentication" | `jwt()`, `.jwt(j -> j.claim(...))`, `.authorities(...)` | 15 min |
| 3 | Testcontainers, [Getting started](https://java.testcontainers.org/) and [PostgreSQL module](https://java.testcontainers.org/modules/databases/postgres/) | A real database in Docker for each test run | 20 min |
| 4 | Spring Boot reference, [Testcontainers](https://docs.spring.io/spring-boot/reference/testing/testcontainers.html), read "Service Connections" and "Using Testcontainers at Development Time" | `@ServiceConnection` and `SpringApplication.from(...).with(...)` | 20 min |
| 5 | Spring Framework reference, [Test transactions](https://docs.spring.io/spring-framework/reference/testing/testcontext-framework/tx.html), read "Test-managed Transactions" | Why every `@DataJpaTest` rolls back, and why `saveAndFlush` matters | 15 min |

**Practice (20 min).**

1. In `TaskControllerTest`, remove `.authorities(...)` from the `sara()` helper. Run the tests. Which ones still pass? Why do they pass?
2. In `TaskRepositoryTest`, change `saveAndFlush` to `save` in the duplicate test. Run it. Why does it no longer throw?
3. While `TaskRepositoryTest` runs, run `docker ps` in another terminal. Find the `postgres:17-alpine` container and its random port.

## Day 3: integration tests, coverage and CI (about 2 hours)

| # | Read | Why | Time |
| --- | --- | --- | --- |
| 1 | Maven Failsafe Plugin, [Introduction](https://maven.apache.org/surefire/maven-failsafe-plugin/) | `*IT` classes, `integration-test` and `verify` phases | 10 min |
| 2 | JaCoCo, [Coverage counters](https://www.jacoco.org/jacoco/trunk/doc/counters.html) and [`check` goal](https://www.jacoco.org/jacoco/trunk/doc/check-mojo.html) | Lines versus branches, and how the 70% gate works | 20 min |
| 3 | GitHub Docs, [Understanding GitHub Actions](https://docs.github.com/en/actions/get-started/understand-github-actions) | Workflow, event, job, step, runner, action | 20 min |
| 4 | GitHub Docs, [Building and testing Java with Maven](https://docs.github.com/en/actions/tutorials/build-and-test-code/java-with-maven) | `setup-java`, Maven cache, uploading reports | 20 min |
| 5 | Martin Fowler, [Continuous Integration](https://martinfowler.com/articles/continuousIntegration.html), read "Practices of Continuous Integration" | Why "it works on my laptop" is not enough, and "fix a broken build first" | 25 min |

**Practice (30 min).**

1. Run `mvn verify` and open `target/site/jacoco/index.html`. Which package has the lowest coverage? Click into a class and find a red line.
2. Change the gate in `pom.xml` to `0.90` and run `mvn verify`. Read the error. Put it back.
3. Push your lab to GitHub with `.github/workflows/ci.yml`. Break one test on purpose in a branch, open a pull request, and see the red check. Fix it and see it go green.

## Self-check

<details><summary>1. What is the difference between a unit test, a slice test and an integration test?</summary>

A unit test checks one class with plain Java and mocks, with no Spring, in milliseconds. A slice test starts only one layer of Spring: `@WebMvcTest` starts the web layer (controllers, security, JSON, error handler), and `@DataJpaTest` starts the data layer (JPA, Flyway, repositories). An integration test (`@SpringBootTest`) starts the whole application with a real database and checks that the pieces work together. Going up the pyramid, there are fewer tests, each slower, but each catches more kinds of bugs.
</details>

<details><summary>2. Why do we use Testcontainers and not H2 for repository tests?</summary>

H2 is a different database. Our migrations use PostgreSQL features (`lower(title)` in a unique index, `CURRENT_DATE + 2`, `VARCHAR` checks), and our queries depend on PostgreSQL behaviour (types of `null` parameters, case-insensitive `like`). A test that passes on H2 can fail in production. Testcontainers runs the same `postgres:17` image as production, so a green test means it works on the real database.
</details>

<details><summary>3. What does <code>@MockitoBean</code> do in a <code>@WebMvcTest</code>?</summary>

It puts a Mockito mock in the Spring context in place of the real bean. The web slice does not load services or repositories, so the controller needs a stand-in for `TaskService`. We tell the mock what to return (`when(...).thenReturn(...)`) and check what the controller sent it (`verify(...)`).
</details>

<details><summary>4. Your <code>@WebMvcTest</code> uses <code>jwt().jwt(j -> j.claim("roles", List.of("ADMIN")))</code> but gets 403 on an admin URL. Why?</summary>

`jwt()` builds the authentication directly and does not run our `JwtAuthenticationConverter`. By default it takes authorities from the `scope` claim, so there is no `ROLE_ADMIN`. Add `.authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))`. The integration test does use the real converter, which is one reason to have both.
</details>

<details><summary>5. Why does the duplicate-title test call <code>saveAndFlush</code> and not <code>save</code>?</summary>

`save` only puts the entity in the persistence context. The `INSERT` is sent at flush time, which in a test is never, because the transaction is rolled back at the end. With no `INSERT`, the database never checks the unique index and nothing throws. `saveAndFlush` sends the `INSERT` now, so the database rejects it and we get `DataIntegrityViolationException`.
</details>

<details><summary>6. What is the difference between <code>mvn test</code> and <code>mvn verify</code> in our project?</summary>

`mvn test` runs Surefire: the `*Test` classes (unit and slices). `mvn verify` goes further: it packages the jar, runs Failsafe for the `*IT` classes (integration tests), merges the coverage data, writes the JaCoCo report and runs the coverage check. Failsafe lets integration tests fail without stopping the clean-up, and reports the failure at `verify`.
</details>

<details><summary>7. Coverage is 95%. Does that mean the code is well tested?</summary>

No. Coverage says which lines ran during the tests, not whether any test checked the result. A test with no assertions can reach 95%. Coverage is good for finding code that **no** test touches. To know if tests are good, break the code on purpose and check that a test fails (this is the idea behind mutation testing).
</details>

<details><summary>8. Your integration tests pass alone but fail when run together. What is the likely cause?</summary>

They share one database, and one test changes data that another expects. For example, one test borrows the last copy and another expects it to be free. Fixes: each test cleans up (return the book), uses data nobody else uses, or runs in a `@Transactional` test that rolls back. Never depend on the order of tests.
</details>

<details><summary>9. What are the parts of a GitHub Actions workflow?</summary>

A **workflow** (a YAML file in `.github/workflows/`) runs on **events** (`push`, `pull_request`). It has **jobs**, and each job runs on a **runner** (a fresh `ubuntu-latest` VM with Docker). A job is a list of **steps**: either a shell command (`run: mvn -B verify`) or a reusable **action** (`uses: actions/setup-java@v4`). A failed step makes the job red, and GitHub shows a red check on the commit and the pull request.
</details>

<details><summary>10. The build is green on your laptop and red in CI. Name three common causes.</summary>

1. A file that is not committed (check `git status`), or ignored by `.gitignore`.
2. Something only your machine has: a running database, an environment variable such as `JWT_SECRET`, a local Maven repository with a snapshot, or a different Java version.
3. Tests that depend on time zone, today's date, the order of tests or of a `HashSet`. Read the uploaded Surefire report to see the exact failure.
</details>

## Common mistakes

| Symptom | Cause | Fix |
| --- | --- | --- |
| `Could not find a valid Docker environment` | Docker is not running, or Testcontainers cannot find the Colima socket | Start Docker (`docker ps` must work). With Colima, add `docker.host` to `~/.testcontainers.properties`, see [the setup guide, section 5](../../environment/SETUP-WEEKS-6-8.md#testcontainers) |
| `Could not start container` for `testcontainers/ryuk` | Colima: Ryuk cannot mount the socket | `TESTCONTAINERS_DOCKER_SOCKET_OVERRIDE=/var/run/docker.sock` in `~/.zshrc` **and** in IntelliJ's JUnit template, see [section 5](../../environment/SETUP-WEEKS-6-8.md#testcontainers) |
| `@WebMvcTest` fails with `No qualifying bean of type ...Properties` | Slices do not run `@ConfigurationPropertiesScan` | Add `@EnableConfigurationProperties(TheProperties.class)` to the test |
| `@WebMvcTest` fails with `No qualifying bean of type JwtDecoder` | `JwtConfig` is not part of the slice | Add `@MockitoBean JwtDecoder jwtDecoder` |
| Admin test gets 403 with `roles: [ADMIN]` | `jwt()` ignores your roles converter | Add `.authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))` |
| `@DataJpaTest` tries to start H2 and fails | The slice replaces the datasource by default | `@AutoConfigureTestDatabase(replace = Replace.NONE)` and import `TestcontainersConfiguration` |
| `createdAt` is `null` in a `@DataJpaTest` | `@EnableJpaAuditing` is in a `@Configuration` the slice does not load | `@Import(JpaConfig.class)` |
| An expected exception never comes in a repository test | `save` without flush; the rollback hides it | Use `saveAndFlush` |
| `*IT` tests never run | Failsafe is not in the `pom.xml`, or you ran `mvn test` | Add `maven-failsafe-plugin` and run `mvn verify` |
| The CI job cannot find `pom.xml` | The project is in a sub-folder of the repo | `defaults: run: working-directory: ...` or move the project to the repo root |
| A test passes alone and fails in the suite | Shared data between tests | Clean up, or use data no other test touches |

## Mini-project (optional, for the weekend)

Write a **test suite for your final project before you write the code** (test-first). Pick three rules from your project's requirements, for example "a doctor cannot have two appointments at the same time". For each rule, write one unit test against a service that does not exist yet, see it fail (it does not compile), then write the code until it is green. Add the CI workflow on day one, so every commit of your final project is checked.

## Words to know

unit test, slice test, integration test, end-to-end test, test pyramid, Arrange-Act-Assert, assertion, mock, stub, spy, `@MockitoBean`, `@WebMvcTest`, `@DataJpaTest`, `@SpringBootTest`, MockMvc, Testcontainers, `@ServiceConnection`, rollback, flush, Surefire, Failsafe, coverage, line coverage, branch coverage, JaCoCo, quality gate, flaky test, continuous integration, workflow, job, step, runner, action, artifact, status check, regression.

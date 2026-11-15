# Study guide: Session 11, Docker, PostgreSQL and Spring Data JPA

Use this guide between Session 11 and Session 12. It takes about 2 hours a day for 3 days. Do the readings in order, type the exercises yourself (no copy-paste), and answer the self-check questions out loud before opening the answers.

## What you must be able to do after this guide

- [ ] Start, stop and reset a database with `docker compose up -d`, `docker compose down` and `docker compose down -v`, and explain the difference.
- [ ] Write a Flyway migration and explain why you never edit one that already ran.
- [ ] Map an entity with `@Id`, `@Column`, `@ManyToOne(fetch = LAZY)`, `@OneToMany(mappedBy)` and `@ManyToMany` + `@JoinTable`.
- [ ] Write a derived query, a JPQL `@Query`, and a paged query that takes `Pageable`.
- [ ] Spot an N+1 problem in the SQL log and fix it with `@EntityGraph` or `JOIN FETCH`.
- [ ] Explain what `@Transactional` does, why reads use `readOnly = true`, and why an update needs no `save()` call.

## Day 1: containers and the database (about 2 hours)

| # | Read or watch | Why | Time |
| --- | --- | --- | --- |
| 1 | Docker docs, [What is a container?](https://docs.docker.com/get-started/docker-concepts/the-basics/what-is-a-container/) and [What is an image?](https://docs.docker.com/get-started/docker-concepts/the-basics/what-is-an-image/) | The two words you will hear every day from now on | 20 min |
| 2 | Docker docs, [Persisting container data](https://docs.docker.com/get-started/docker-concepts/running-containers/persisting-container-data/) | Why our data survives `down` but not `down -v` | 15 min |
| 3 | Docker docs, [Compose file reference: services](https://docs.docker.com/reference/compose-file/services/), read only `image`, `ports`, `environment`, `volumes`, `labels` | Every line of our `compose.yaml` | 20 min |
| 4 | Spring Boot reference, [Docker Compose support](https://docs.spring.io/spring-boot/reference/features/dev-services.html#features.dev-services.docker-compose) | How `mvn spring-boot:run` starts PostgreSQL for you | 15 min |
| 5 | PostgreSQL tutorial, [Creating a new table](https://www.postgresql.org/docs/17/tutorial-table.html) and [Constraints](https://www.postgresql.org/docs/17/ddl-constraints.html) (sections 5.5.1 to 5.5.5) | `CHECK`, `NOT NULL`, `UNIQUE`, `PRIMARY KEY`, `FOREIGN KEY` | 30 min |

**Practice (20 min).** In `task-api-v3`:

1. `docker compose up -d`, then `docker compose ps`. Write down the port mapping you see.
2. Open a shell in the database: `docker compose exec postgres psql -U tasks -d tasks`. Run `\dt`, then `\d tasks`, then `select * from flyway_schema_history;`. Type `\q` to leave.
3. Insert a task with priority `URGENT` by hand in `psql`. Which constraint stops you? Read the error message.
4. `docker compose down`, then `up -d`. Is your data still there? Now `down -v` and `up -d`. What happened?

## Day 2: entities, migrations and repositories (about 2 hours)

| # | Read | Why | Time |
| --- | --- | --- | --- |
| 1 | Spring Boot reference, [SQL databases: JPA and Spring Data JPA](https://docs.spring.io/spring-boot/reference/data/sql.html#data.sql.jpa-and-spring-data) | What the starter configures for you | 15 min |
| 2 | Spring Boot how-to, [Use a higher-level database migration tool: Flyway](https://docs.spring.io/spring-boot/how-to/data-initialization.html#howto.data-initialization.migration-tool.flyway) | File names `V1__name.sql`, where they live, when they run | 10 min |
| 3 | Spring Data JPA reference, [Defining query methods](https://docs.spring.io/spring-data/jpa/reference/repositories/query-methods-details.html), read "Query creation" and "Property expressions" | How `findByDueDateBeforeAndDoneFalse` turns into SQL | 25 min |
| 4 | Spring Data JPA reference, [Using @Query](https://docs.spring.io/spring-data/jpa/reference/jpa/query-methods.html#jpa.query-methods.at-query) | JPQL uses entity and field names, not table and column names | 15 min |
| 5 | Baeldung, [JPA/Hibernate associations](https://www.baeldung.com/jpa-hibernate-associations) | One-to-many, many-to-one, many-to-many, the owning side | 25 min |
| 6 | Spring Data JPA reference, [Paging and sorting](https://docs.spring.io/spring-data/jpa/reference/repositories/query-methods-details.html#repositories.special-parameters) | `Pageable`, `Sort`, `Page` | 10 min |

**Practice (30 min).** In `task-api-v3`:

1. Add `V6__add_task_notes.sql` that adds a nullable `notes VARCHAR(500)` column. Restart. Check `flyway_schema_history`.
2. Add the `notes` field to `Task` but misspell the column name in `@Column`. Start the app and read the `Schema validation` error. Fix it.
3. Add `List<Task> findByTitleContainingIgnoreCase(String text)` to `TaskRepository` and an endpoint `GET /api/tasks/find?text=...`. Look at the SQL in the console.
4. Change `V6` after it ran (add a space). Start the app. Read the checksum error, then undo your change.

## Day 3: performance and transactions (about 2 hours)

| # | Read | Why | Time |
| --- | --- | --- | --- |
| 1 | Vlad Mihalcea, [N+1 query problem with JPA and Hibernate](https://vladmihalcea.com/n-plus-1-query-problem/) | The most common performance bug in JPA code | 25 min |
| 2 | Spring Data JPA reference, [Configuring fetch and load graphs](https://docs.spring.io/spring-data/jpa/reference/jpa/query-methods.html#jpa.entity-graph) | `@EntityGraph(attributePaths = ...)` | 10 min |
| 3 | Spring Framework reference, [Using @Transactional](https://docs.spring.io/spring-framework/reference/data-access/transaction/declarative/annotations.html) | Where it goes, `readOnly`, why calls inside the same class skip it | 25 min |
| 4 | Thorben Janssen, [Open Session in View is an anti-pattern](https://thorben-janssen.com/open-session-in-view-is-an-anti-pattern/) | Why we set `spring.jpa.open-in-view: false` | 15 min |
| 5 | Spring Boot reference, [Logging: log levels](https://docs.spring.io/spring-boot/reference/features/logging.html#features.logging.log-levels) | How `logging.level.org.hibernate.SQL: DEBUG` works | 10 min |

**Practice (35 min).**

1. In `TaskRepository.search`, remove the `@EntityGraph` line and set `default_batch_fetch_size` to `1`. Call `GET /api/tasks?size=6` and count the `select` lines. Put both back and count again.
2. In `TaskService.update`, remove `@Transactional` from the method and leave the class-level `readOnly = true`. Call `PUT /api/tasks/1`. Is the title changed in the database? Explain why, then put it back.
3. Write in your own words, in 5 lines, what happens between `@Transactional` method start and method end. Use the words "persistence context", "dirty checking", "flush" and "commit".

## Self-check

Answer each question out loud first. Then open the answer.

<details><summary>1. What is the difference between an image and a container?</summary>

An image is a read-only template (files plus a start command). A container is a running instance of an image with its own writable layer. One image (`postgres:17`) can run as many containers.
</details>

<details><summary>2. You ran <code>docker compose down -v</code>. What is gone, and what is still there?</summary>

The containers, the network and the named volumes are gone, so all database data is lost. The images stay on disk. On the next start, Flyway runs V1 to V5 again on an empty database.
</details>

<details><summary>3. Why <code>ddl-auto: validate</code> and not <code>update</code>?</summary>

With Flyway the SQL files own the schema. `validate` only checks that entities and tables match and fails fast at startup if they do not. `update` lets Hibernate change tables on its own: no history, no review, and it never drops or renames anything safely.
</details>

<details><summary>4. A teammate edited <code>V2__create_tasks.sql</code> after it ran. What happens on your machine?</summary>

Flyway stores a checksum for every migration that ran. On startup the checksum does not match, so Flyway stops with a validation error. The fix is a new migration (`V6__...`) for the change, never an edit to an old one.
</details>

<details><summary>5. Which side of <code>Project 1-* Task</code> owns the relation, and how can you tell?</summary>

`Task` owns it. It has `@ManyToOne` + `@JoinColumn(name = "project_id")`, and its table has the foreign key column. `Project` has `@OneToMany(mappedBy = "project")`, which only says "the other side owns this".
</details>

<details><summary>6. What is N+1, and name two fixes?</summary>

One query loads N rows, then a LAZY relation triggers one more query per row, so you get N+1 queries. Fixes: `@EntityGraph(attributePaths = "project")` on the repository method, `join fetch` in a JPQL query, or `default_batch_fetch_size` to load the relations in batches.
</details>

<details><summary>7. Why does <code>TaskService.update</code> not call <code>tasks.save(task)</code>?</summary>

The entity came from `findById` inside a transaction, so it is managed. At commit Hibernate compares it with the snapshot it took on load (dirty checking) and writes an `UPDATE` for the changed columns.
</details>

<details><summary>8. Why do we return <code>PageResponse</code> and not Spring's <code>Page</code>?</summary>

`Page` is an internal type whose JSON shape is not a stable contract. Our own record gives a small, stable JSON (`content`, `page`, `size`, `totalElements`, `totalPages`) that we control.
</details>

<details><summary>9. What does <code>@Transactional(readOnly = true)</code> on the class give us?</summary>

Every public method runs in a transaction, and read-only lets Hibernate skip dirty checking and lets the driver and database optimise. Write methods override it with a plain `@Transactional`.
</details>

<details><summary>10. Two users create a task with the same title at the same moment. Both pass <code>existsByTitleIgnoreCase</code>. What stops the duplicate?</summary>

The unique index on `lower(title)` in the database. The second insert fails with `DataIntegrityViolationException`, which the handler maps to 409. The service check gives a nice message, and the database constraint gives the guarantee.
</details>

## Common mistakes

| Symptom | Cause | Fix |
| --- | --- | --- |
| `Connection refused` on port 5432 | Docker is not running | Start Docker Desktop, or `colima start` on a Mac |
| `port is already allocated` | Another PostgreSQL (or another project) uses the port | `docker ps`, stop the other one, or change the host port (`"5433:5432"`) |
| `Schema validation: missing table [x]` | The migration did not run, or the table name in `@Table` is wrong | Check the file name: `V4__create_loans.sql` with **two** underscores, in `db/migration` |
| `wrong column type encountered in column [isbn]` | SQL type and Java type disagree, for example `CHAR(13)` vs `String` | Use `VARCHAR(n)` for `String` |
| `Validate failed: Migration checksum mismatch` | You edited a migration that already ran | Undo the edit and add a new version, or (local only) `docker compose down -v` |
| `LazyInitializationException: could not initialize proxy - no Session` | You touched a LAZY relation outside a transaction | Map to a DTO inside the service, or fetch it with `@EntityGraph` / `join fetch` |
| Endless JSON or `StackOverflowError` | You returned an entity with a two-way relation from a controller | Never return entities. Return DTO records |
| Update returns 200, but nothing changes in the database | The method runs inside the class-level `readOnly = true` transaction | Add `@Transactional` on the write method |
| `No property 'titel' found for type 'Task'` at startup | A typo in a derived query method name | The name must use the Java field names exactly |
| 20 `select` lines for one list call | N+1 | `@EntityGraph` or `join fetch` |
| `function lower(bytea) does not exist` when a filter is left out | `(:text is null or lower(x) like lower(:text))` with a null `String` reaches PostgreSQL with no type | Use `lower(x) like lower(concat('%', coalesce(:text, ''), '%'))` |

## Mini-project (optional, for the weekend)

Add **comments** to `task-api-v3`: a `comments` table (`V6`), a `Comment` entity (`@ManyToOne` to `Task`), `POST /api/tasks/{id}/comments` and `GET /api/tasks/{id}/comments?page=0&size=5` (newest first). A task with comments cannot be deleted (409). Check the SQL log: listing comments must be a single `select` plus one `count`.

## Words to know

container, image, volume, port mapping, Docker Compose, migration, Flyway, entity, persistence context, managed entity, dirty checking, flush, owning side, `mappedBy`, LAZY, EAGER, N+1, entity graph, `JOIN FETCH`, JPQL, derived query, projection, `Pageable`, transaction, commit, rollback, constraint.

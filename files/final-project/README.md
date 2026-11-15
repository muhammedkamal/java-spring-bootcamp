# Final project guide

For the two weeks after Session 14, you build one REST API on your own, from an empty folder to a tested, secured system that starts with one command. On day 10 you demo it and answer questions about your own code.

This guide is the contract. If it is not in here, it is not graded. If something is unclear, ask in the project channel before day 2.

- [1. The rules](#1-the-rules)
- [2. Choose a project](#2-choose-a-project)
- [3. What every project must have](#3-what-every-project-must-have)
- [4. Milestones](#4-milestones)
- [5. Rubric (100 points)](#5-rubric-100-points)
- [6. Repository layout and templates](#6-repository-layout-and-templates)
- [7. The demo (day 10)](#7-the-demo-day-10)
- [8. Defense questions](#8-defense-questions)
- [9. Submission checklist](#9-submission-checklist)
- [10. Where to look when you are stuck](#10-where-to-look-when-you-are-stuck)

## 1. The rules

1. **One person, one repository.** Work alone. Talking about ideas with other interns is fine. Copying their code is not.
2. **Public or private on GitHub.** If it is private, add your instructor as a collaborator on day 1.
3. **Commit every day.** Small commits with clear messages. A repo with three huge commits on day 9 loses points (see the rubric) and makes the defense hard.
4. **You must be able to explain every line.** You may use the course code, the official docs, Stack Overflow and AI assistants. But if you cannot explain a piece of code at the defense, it earns no points. When you copy something, write where it came from in the commit message.
5. **Stack.** Java 25, Spring Boot 4.x, Maven, PostgreSQL, Flyway, Spring Security with JWT, JUnit 5, Testcontainers, Docker Compose, GitHub Actions. The same as Sessions 9 to 14. Other libraries are fine if you can say why you chose them.
6. **Late work.** Each milestone is due at 18:00 on its day. Late milestones lose 2 points per day, up to 6 points each. The demo cannot be late.

## 2. Choose a project

Pick one of the four. Each one is about the size of the Task API, and each one has the same kinds of rules: ownership, a limit and a conflict. If you want your own idea, write it in the same format and get it approved before the end of day 1.

### ClinicBook: appointments at a small clinic

| Role | Can do |
| --- | --- |
| PATIENT | Register, see doctors and their free slots, book, see and cancel **their own** appointments |
| DOCTOR | See their own schedule, mark an appointment `DONE` or `NO_SHOW`, add a short note |
| ADMIN | Add doctors, set working hours, see every appointment |

Entities: `User`, `Doctor` (specialty, working hours), `Appointment` (doctor, patient, start time, status, note).

Business rules to implement and test:
- A slot is 30 minutes, inside the doctor's working hours, in the future. Otherwise 400.
- Two appointments for the same doctor at the same time: 409. (Enforce it with a unique constraint too, not only in Java.)
- A patient may hold at most 3 future appointments.
- Cancel up to 2 hours before the start, never after.
- A patient who reads another patient's appointment gets 404.

### ShopLite: a small online shop

| Role | Can do |
| --- | --- |
| CUSTOMER | Browse and search products, keep a cart, place an order, see **their own** orders |
| ADMIN | Manage products and stock, move orders through `PAID`, `SHIPPED`, `CANCELLED` |

Entities: `User`, `Product` (name, price, stock, category), `Cart` and `CartItem`, `Order` and `OrderLine` (price copied at order time).

Business rules:
- Placing an order checks the stock of every line in one transaction. Not enough stock for any line: 409, and nothing changes.
- Prices are `BigDecimal`. The order total is calculated on the server, never sent by the client.
- An order can be cancelled only before `SHIPPED`. Cancelling puts the stock back.
- Search by name and category, with paging and sorting.
- A customer who reads another customer's order gets 404.

### TaskHub: teams and their work

The closest to the Task API, so the bar is higher: you must do the "events" bonus.

| Role | Can do |
| --- | --- |
| MEMBER | Create a team (and become its OWNER), invite members, create and assign tasks in their teams |
| team OWNER | Remove members, delete the team |
| ADMIN | See everything, disable users |

Entities: `User`, `Team`, `Membership` (user, team, role in team), `Task` (assignee, due date, status `TODO`, `DOING`, `DONE`), `Comment`.

Business rules:
- Only members of a team can see its tasks. Others get 404.
- The assignee must be a member of the team.
- Status moves only forward (`TODO` to `DOING` to `DONE`), except the owner may reopen.
- A member can have at most 10 open tasks in one team.
- `task.assigned` and `task.done` events go to RabbitMQ, and a consumer stores notifications.

### EventPass: events and tickets

| Role | Can do |
| --- | --- |
| ATTENDEE | Browse upcoming events, book tickets, see and cancel **their own** tickets |
| ORGANIZER | Create and edit **their own** events, see who booked, check in a ticket by its code |
| ADMIN | Approve organizers, see everything |

Entities: `User`, `Event` (title, venue, start, capacity, organizer), `Ticket` (event, attendee, code, status `BOOKED`, `CANCELLED`, `USED`).

Business rules:
- Booking never sells more tickets than the capacity, even with two requests at the same moment. Use a lock or an atomic update and prove it with a test.
- At most 4 tickets per attendee per event.
- No booking after the event starts. No cancelling in the 24 hours before.
- Check-in only by the event's organizer, only once per ticket (second time: 409).
- An organizer who edits another organizer's event gets 404.

### Your own idea

Write it as one of the blocks above: roles, entities, at least 4 business rules, and one ownership rule. It needs at least 3 entities with relations and at least 2 roles. Ask before day 1 ends.

## 3. What every project must have

These are the minimum. Each one appears in the rubric.

| Area | Must have |
| --- | --- |
| API | REST resources with correct methods and status codes (200, 201 with `Location`, 204, 400, 401, 403, 404, 409). DTOs in and out, never entities. Paging on every list. |
| Validation and errors | Bean Validation on every request DTO. One `@RestControllerAdvice` that returns `ProblemDetail` for every error. No stack traces in responses. |
| Data | PostgreSQL. Every table created by Flyway migrations, with `ddl-auto: validate`. A seed migration (or a `dev` profile) with demo users and data. |
| Security | Register and login, JWT access tokens, BCrypt passwords. At least 2 roles. Ownership checks in the service layer. The secret from an environment variable. |
| Tests | Unit tests for the business rules (with Mockito), at least one `@WebMvcTest` that checks 401 and 403, at least one `@DataJpaTest` on PostgreSQL with Testcontainers, at least one `*IT` that logs in and uses the token. JaCoCo line coverage at least 70%, enforced by the build. |
| CI | GitHub Actions runs `mvn verify` on every push and pull request. The last commit is green. |
| Docker | A two-stage `Dockerfile` that runs as non-root, and a `compose.yaml` that starts the database and the app with health checks. `docker compose up` is all a reviewer needs. |
| Docs | `README.md` from the template below. Swagger UI works. A `requests.http` file or a Postman collection that runs the demo from top to bottom. |

Bonus ideas (count toward the bonus points, up to 10): a Redis cache with a test that proves eviction, events through RabbitMQ with a dead-letter queue, Actuator with protected details, refresh tokens, a gateway, images pushed to ghcr.io from CI, a real deployment with HTTPS, a small frontend.

## 4. Milestones

Day 1 is the first working day after Session 14.

| Milestone | Due | You send | Your instructor checks |
| --- | --- | --- | --- |
| **M1: Plan** | Day 2, 18:00 | The repo link. A `docs/PLAN.md` with the ER diagram, the endpoint table, the roles table and the business rules. GitHub issues for M2 and M3. | Is the size right? Are the rules testable? 15-minute call on day 3 to agree. |
| **M2: Core API** | Day 5, 18:00 | Entities, Flyway migrations, CRUD and the main business endpoints, validation and error handling, Swagger. Unit tests for the rules so far. CI running `mvn verify`. | Run it, read the code, leave comments on GitHub. |
| **M3: Secure, tested, shipped** | Day 9, 18:00 | Security and ownership, all the tests, coverage gate, Dockerfile and Compose, README. Tag `v1.0.0`. | Clone, `docker compose up`, run your `requests.http`, read the CI log. Written feedback on day 10 morning. |
| **Demo and defense** | Day 10 | A 10-minute demo, then 10 minutes of questions. | Section 7 and 8. |

On day 6 there is an optional 20-minute code review call. Book it. Interns who book it score higher, every time.

Suggested daily plan:

| Day | Do |
| --- | --- |
| 1 | Choose. Draw the entities. Write the endpoint table. Create the repo with Spring Initializr, `compose.yaml` with PostgreSQL, and an empty CI workflow that is green. |
| 2 | First migrations and entities. Finish `docs/PLAN.md` and the issues. **M1.** |
| 3 | Repositories, services, DTOs, controllers for the main resource. Error handling. |
| 4 | The other resources and the main business rules, with unit tests as you go. |
| 5 | Paging, search, Swagger, seed data. **M2.** |
| 6 | Users, register, login, JWT. Roles. Code review call. |
| 7 | Ownership in every service method. `@WebMvcTest` for 401, 403, 404. |
| 8 | `@DataJpaTest` and the `*IT` on Testcontainers. Coverage gate. Fix what the tests find. |
| 9 | Dockerfile, Compose with the app, README, `requests.http`. A bonus if there is time. Tag `v1.0.0`. **M3.** |
| 10 | Rehearse the demo twice. Demo and defense. |

## 5. Rubric (100 points)

Plus up to 10 bonus points. The total is capped at 100.

| # | Area | Points | Full marks | Half marks | Zero |
| --- | --- | --- | --- | --- | --- |
| 1 | **It runs** | 10 | `git clone`, `cp .env.example .env`, `docker compose up` works on the reviewer's machine first time. The seed data is there. | Works after a small fix the README mentions. | Does not start, or needs steps that are not written down. |
| 2 | **API design** | 12 | Resource URLs, correct methods and status codes, `Location` on 201, paging on lists, DTOs everywhere. | Mostly right, a few wrong codes or an entity in a response. | Everything is `POST` and `200`, or entities are returned. |
| 3 | **Business rules** | 15 | Every rule from your plan is implemented in the service layer and has a test. Database constraints back the important ones. | Most rules work, some untested or only in the controller. | The rules are missing or easy to break. |
| 4 | **Validation and errors** | 8 | Bean Validation on every input. One advice, `ProblemDetail`, clear messages, 400, 404 and 409 where they belong. | Some inputs unchecked, or mixed error formats. | 500 for bad input, or stack traces in responses. |
| 5 | **Data and migrations** | 8 | Flyway owns the schema, `validate` mode, sensible types and constraints, indexes on the columns you search, no N+1 on the main lists. | Works, but `ddl-auto: update`, or one obvious N+1. | No migrations. |
| 6 | **Security** | 15 | BCrypt, JWT, a secret from the environment, roles with `401` and `403` right, ownership in the service (another user's data is 404), nothing sensitive in responses or logs. | Roles work but an ownership hole exists, or the secret is in Git. | Endpoints open, or passwords stored in plain text. |
| 7 | **Tests** | 15 | Unit, web slice, data slice and integration tests, each at the right level. They check rules, not getters. Coverage at least 70% and enforced. | Tests exist but mostly happy paths, or coverage not enforced. | Fewer than 10 real tests. |
| 8 | **CI** | 5 | `mvn verify` on every push and PR, the last commit is green, test reports uploaded. | CI exists but is red, or runs only `mvn compile`. | No CI. |
| 9 | **Docker** | 5 | Two-stage image, non-root, health check, Compose with health checks and `depends_on`, secrets from `.env`. | Works, but runs as root or has no health checks. | No Dockerfile. |
| 10 | **Code quality and Git** | 7 | Clear packages and names, small methods, no dead code, constructor injection. Daily commits with useful messages. Issues closed by commits or PRs. | Readable but messy in places, or few large commits. | One commit, or code nobody can read. |
| | **Total** | **100** | | | |
| B | **Bonus** | up to +10 | Each bonus from section 3 that works and has a test: +3 to +5. A deployment with HTTPS: +5. | | |

The **defense** can lower any area. If you cannot explain how a part of your code works, that area loses up to half of its points.

Late milestones: minus 2 points per day, at most minus 6 per milestone.

## 6. Repository layout and templates

```
your-project/
├── .github/workflows/ci.yml
├── docs/
│   ├── PLAN.md                 # M1
│   └── er-diagram.png          # or a Mermaid diagram inside PLAN.md
├── src/main/java/com/<you>/<project>/
│   ├── config/  controller/  dto/  entity/  exception/
│   ├── repository/  security/  service/
├── src/main/resources/
│   ├── application.yml
│   └── db/migration/V1__create_tables.sql, V2__seed.sql, ...
├── src/test/java/...           # *Test and *IT
├── .env.example                # names only, no real secrets
├── .gitignore                  # target/, .env, .idea/
├── compose.yaml
├── Dockerfile
├── .dockerignore
├── requests.http               # or postman/collection.json
└── README.md
```

### Template: `docs/PLAN.md`

```markdown
# <Project name>: plan

## In one sentence
<Who uses it and what for.>

## Roles
| Role | Can do |
| --- | --- |

## Entities
<ER diagram: image or Mermaid. For each entity: fields, types, which ones are unique or required.>

## Endpoints
| Method | Path | Role | Request | Response | Errors |
| --- | --- | --- | --- | --- | --- |
| POST | /api/auth/register | public | RegisterRequest | 201 UserResponse | 400, 409 |

## Business rules
1. <Rule> → <what the API returns when it is broken> → <how I will test it>

## Out of scope
<What you will NOT build. This is as important as the rest.>
```

### Template: `README.md`

```markdown
# <Project name>

<One paragraph: what it does and for whom.>

![CI](https://github.com/<you>/<repo>/actions/workflows/ci.yml/badge.svg)

## Run it
    cp .env.example .env        # then set JWT_SECRET
    docker compose up -d --build --wait
    open http://localhost:8080/swagger-ui.html

Demo accounts (password `secret123`): admin@..., user1@..., user2@...

## Run the tests
    mvn verify                  # needs Docker running

## What it does
<The roles table and the business rules, short.>

## API
<A short endpoint table, or a link to Swagger.>

## Design decisions
<3 to 5 bullets: why this structure, this lock, this index, this status code.>

## What I would do next
<Honest: what is missing or would break first.>
```

### Template: `.env.example`

```
POSTGRES_PASSWORD=change-me
JWT_SECRET=put-a-long-random-value-here-openssl-rand-base64-48
```

For `compose.yaml`, the `Dockerfile` and `ci.yml`, start from `files/session-14/system/task-api` (and `files/session-14/system/.github/workflows/ci.yml` for CI). They are written to be copied. Change the names and the ports, and read every line before you keep it.

## 7. The demo (day 10)

You have **10 minutes**. Then 10 minutes of questions. Rehearse with a timer. Have everything running before your slot: containers up, Postman or `requests.http` open, the GitHub repo in a browser tab.

| Minute | Show |
| --- | --- |
| 0 to 1 | What the project is, for whom, the roles. One sentence each. |
| 1 to 2 | The ER diagram, and the one design decision you are proudest of. |
| 2 to 6 | The happy path, live: register or log in, create the main thing, do the main action (book, order, assign). |
| 6 to 8 | The rules, live: break each important one and show the answer. 401 without a token, 403 with the wrong role, 404 on someone else's data, 409 on a conflict, 400 on bad input. |
| 8 to 9 | GitHub: the green CI run, the coverage number, one test you are proud of (read its name out loud). |
| 9 to 10 | `docker compose ps`, and what you would do next. |

If something breaks during the demo, say what you think the cause is and move on. That is better than 3 minutes of silence. A demo that breaks and is explained well scores higher than a perfect demo you cannot explain.

## 8. Defense questions

These are the kinds of questions you will get. You will be asked about **your** code, with your repo open. Practise answering them out loud.

**Architecture and API**
1. Walk me through what happens, class by class, when `POST /api/<your main resource>` arrives.
2. Why does this endpoint return 201 and not 200? Why 404 and not 403 for someone else's data?
3. Why do you have DTOs? What would go wrong if this controller returned the entity?
4. Where would you add a new business rule, and where would you test it?

**Data**
5. Open your newest migration. What happens if I change `V1` after it has run?
6. Which query does this list page run? How many queries for 20 rows? How do you know?
7. Which constraints does the database enforce, and why not only Java?
8. Show me a `@Transactional` method. What happens if the second save fails?

**Security**
9. What is in your JWT? Where is the secret? What happens if it leaks?
10. A user changes the id in the URL to someone else's. Where exactly is that stopped?
11. What is the difference between 401 and 403? Show one of each in a test.
12. Why BCrypt and not SHA-256?

**Tests and CI**
13. Pick a test. What would break in the code to make it fail? Break it now and show me.
14. Why is this test a `@WebMvcTest` and not a `@SpringBootTest`?
15. Why Testcontainers and not H2?
16. Which part of your code is not covered, and is that OK?

**Docker and running it**
17. Why two stages in your Dockerfile? Why the non-root user?
18. Why does your app use `postgres` and not `localhost` as the database host?
19. What does `depends_on` with `service_healthy` add over plain `depends_on`?

**Your choices**
20. What would break first with 1,000 users at the same time? What would you change?
21. What did you take from somewhere else (the course, the docs, an AI tool)? Explain it.
22. If you started again, what would you do differently?

## 9. Submission checklist

Check every box before you tag `v1.0.0` on day 9.

**Runs**
- [ ] A fresh clone in a new folder: `cp .env.example .env`, `docker compose up -d --build --wait` works.
- [ ] Seed data and demo accounts are there, and the README lists them.
- [ ] `requests.http` (or Postman) runs from top to bottom without editing.
- [ ] Swagger UI opens and every endpoint is listed.

**Code**
- [ ] No entity is returned from a controller.
- [ ] Every request DTO has validation annotations.
- [ ] Every list endpoint is paged.
- [ ] Every business rule from `docs/PLAN.md` is implemented and has a test.
- [ ] Every service method that reads or changes user data checks ownership.

**Security**
- [ ] No secret in Git (`git log -p | grep -i secret` shows only `.env.example`).
- [ ] `.env` is in `.gitignore`.
- [ ] No password hash, token or stack trace in any response or log.
- [ ] 401 and 403 are tested.

**Tests and CI**
- [ ] `mvn verify` is green locally, with no `@Disabled` tests.
- [ ] Coverage at least 70%, and the build fails below it.
- [ ] The last commit on `main` has a green check.

**Docs and Git**
- [ ] README follows the template, with the CI badge.
- [ ] Commits on at least 8 of the 10 days.
- [ ] All M2 and M3 issues closed or moved to "next" with a reason.
- [ ] Tag `v1.0.0` pushed: `git tag v1.0.0 && git push --tags`.

## 10. Where to look when you are stuck

| Topic | Course material |
| --- | --- |
| Project setup, Spring Boot basics, the first controller | `files/session-09/`, its README |
| Layers, DTOs, validation, errors, Swagger | `files/session-10/` and its STUDY-GUIDE |
| PostgreSQL in Docker, JPA, relations, Flyway, paging, N+1 | `files/session-11/` and its STUDY-GUIDE |
| Users, BCrypt, JWT, roles, ownership | `files/session-12/` and its STUDY-GUIDE |
| Unit, slice and integration tests, coverage, CI | `files/session-13/` and its STUDY-GUIDE |
| Dockerfile, Compose, Actuator, cache, events, deployment | `files/session-14/`, its STUDY-GUIDE and `system/DEPLOYMENT.md` |
| Installing Docker, Colima, Testcontainers problems | `environment/SETUP-WEEKS-6-8.md` |
| A complete project of the same size, to compare with | `files/session-14/system/task-api` |

How to ask for help, so you get an answer fast:
1. What you are trying to do, in one sentence.
2. What you did (the command, the request, a link to the commit).
3. What happened: the **full** error message, or the first 30 lines of the stack trace.
4. What you already tried.

Good luck. Build something you will want to show in a job interview.

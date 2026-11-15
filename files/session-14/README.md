# Week 7, Session 7B: Caching, Messaging, a Gateway and Deployment

Slides: `../Session14_Caching_Messaging_and_Deployment.pptx`
Study guide: [`STUDY-GUIDE.md`](STUDY-GUIDE.md)
Deployment guide: [`system/DEPLOYMENT.md`](system/DEPLOYMENT.md)
Final project: [`../final-project/README.md`](../final-project/README.md)

This is the last session. The Task API stops being one application and becomes a small **system**: three services, three pieces of infrastructure, one entry point.

```
browser / Postman --> gateway :8000 --+--> task-api :8080 ----> PostgreSQL, Redis
                                      |         |
                                      |         +-- task.created / task.completed --> RabbitMQ
                                      |                                                  |
                                      +--> notification-service :8081 <------------------+
```

Needs JDK 25, Maven and Docker, same as Sessions 11 to 13. Docker must be running for the tests and for the system. Setup help: [`../../environment/SETUP-WEEKS-6-8.md`](../../environment/SETUP-WEEKS-6-8.md).

## Run the whole system

```bash
cd files/session-14/system
cp .env.example .env               # then put a long random JWT_SECRET in .env (openssl rand -base64 48)
docker compose up -d --build --wait
docker compose ps                  # 6 services, all healthy
```

The first build downloads Maven dependencies inside Docker and takes a few minutes. After that it takes seconds. Then open `system/requests.http` and run it from the top. It logs in as Sara, reads the stats twice (the second read comes from Redis), creates and completes a task, and reads Sara's two notifications. Everything goes through the gateway on port 8000.

RabbitMQ's management UI is at http://localhost:15672 (guest / guest). Open **Queues** and watch `notifications.task-events` while you complete a task.

Stop it with `docker compose down`. Add `-v` to also delete the database.

## What is in `system/`

| Folder | What it is | New in this session |
| --- | --- | --- |
| `task-api` | The Task API v6: `task-api-v5` from Session 13, plus the new pieces in the next table | Cache, events, Actuator, Dockerfile |
| `notification-service` | A new, small service. It listens to task events and keeps the last 50 notifications per user in memory. `GET /api/notifications` with the same JWT. | All of it |
| `gateway` | Spring Cloud Gateway. It routes `/api/notifications/**` to the notification service and everything else under `/api/**` to the Task API. | All of it |
| `compose.yaml` | PostgreSQL, Redis, RabbitMQ and the three services, with health checks and `depends_on` | All of it |
| `.github/workflows/ci.yml` | Tests every service in parallel, then builds and pushes the images to ghcr.io | All of it |
| `DEPLOYMENT.md` | From images to a server with HTTPS, with a checklist | All of it |

The Task API changes, file by file:

| Feature | Where | What to look at |
| --- | --- | --- |
| Redis cache | `config/CacheConfig`, `TaskService.stats` and the write methods, `application.yml` (`spring.cache`) | `@Cacheable(key = "#caller.ownerFilter() ?: 'all'")` on stats (one entry per user, one for the admin), `@CacheEvict(allEntries = true)` on every write, a 10 minute TTL as a safety net |
| Events | `events/TaskEvent`, `events/TaskEventPublisher`, `events/RabbitConfig` | The service publishes a Spring event. A `@TransactionalEventListener` sends it to RabbitMQ only **after commit**. |
| Actuator | `application.yml` (`management`), `SecurityConfig` | Health and info are public. Details and `/actuator/metrics` are for ADMIN only. |
| Docker image | `Dockerfile`, `.dockerignore` | Two stages, jar layers, a non-root user, a `HEALTHCHECK` on readiness |
| Build info | `spring-boot-maven-plugin` `build-info` goal in `pom.xml` | `/actuator/info` shows the version and build time |

The notification service shows the consumer side:

| Piece | Where |
| --- | --- |
| Its own queue, bound to the `tasks.events` exchange with `task.#` | `RabbitConfig` |
| Retry 3 times, then send the message to a dead-letter queue (`notifications.task-events.dlq`) | `application.yml` (`listener.simple.retry`), `RabbitConfig` |
| Reject a broken message at once, without retries | `TaskEventListener` throws `AmqpRejectAndDontRequeueException` |
| Read the producer's JSON into its **own** `TaskEvent` class | `setAlwaysConvertToInferredType(true)` in `RabbitConfig` |
| Check the Task API's tokens with the shared secret, without calling the Task API | `SecurityConfig` (`JwtDecoder`, HS256, issuer `task-api`) |

## Run and test the services one by one

```bash
cd files/session-14/system/task-api
mvn verify                 # 25 unit and slice tests + 8 integration tests on real PostgreSQL, Redis and RabbitMQ
mvn spring-boot:run        # starts its own compose.yaml: PostgreSQL 5432, Redis 6379, RabbitMQ 5672

cd ../notification-service
mvn verify                 # includes NotificationFlowIT: a broken message ends up in the dead-letter queue
mvn spring-boot:run        # port 8081, needs RabbitMQ (start the Task API first)

cd ../gateway
mvn verify                 # routes tested against two fake services, no Docker needed
mvn spring-boot:run        # port 8000
```

Three versions to know:

| Project | Spring Boot | Why |
| --- | --- | --- |
| `task-api`, `notification-service` | 4.1.2 | Same as the rest of the course |
| `gateway` | 4.0.9 | Spring Cloud 2025.1 supports Boot 4.0. Each service picks its own versions, so this is fine. |

Things that surprise everyone:

1. The cache stores Java objects in Redis with Java serialization, so a cached DTO must `implements Serializable`. Without it you get `SerializationException` on the first call, not at startup.
2. `@Cacheable` works only when the call comes from **another** bean. A method that calls its own `@Cacheable` method skips the cache (the same proxy rule as `@Transactional`).
3. RabbitMQ 4 no longer accepts transient queues that several connections share. Declare test queues as durable, and delete them after the test.
4. The JSON message carries a `__TypeId__` header with the producer's class name, `com.flairs.taskapi.events.TaskEvent`. The consumer does not have that class. `setAlwaysConvertToInferredType(true)` tells it to use the type of its own listener parameter instead.
5. `localhost` inside a container is that container. Services find each other by their compose service name: `jdbc:postgresql://postgres:5432/tasks`.

## Lab: ship the Library API

Start from `lab/starter/library-api`. It is the Session 13 solution with all the new dependencies in the `pom.xml`, a `compose.yaml` with Redis and RabbitMQ, a Testcontainers configuration with all three containers, and the integration tests already written. You write the main code. The tests tell you when it works.

Host ports differ from the Task API's, so both can run at the same time: PostgreSQL 5433, Redis 6380, RabbitMQ 5673 (UI on 15673), and the app container on 8090.

| TODO | File | What to do | Test that goes green |
| --- | --- | --- | --- |
| 1 | `application.yml`, `SecurityConfig` | Actuator: expose health and info, turn on the probes, make them public | `ShipItIT.healthIsPublicAndUp` |
| 2 | `CacheConfig`, `BookResponse`, `BookService` | Cache `findById` in the `books` cache, evict on `update` and `delete` | `ShipItIT.aBookIsCachedAndAnUpdateEvictsIt` |
| 3 | `LoanService` | Publish a `LoanEvent` (BORROWED, RETURNED) with `ApplicationEventPublisher` | `LoanServiceTest.borrowPublishesABorrowedEvent`, `aRefusedBorrowPublishesNothing` |
| 4 | `events/LoanEventPublisher` | After commit, send it to the `library.events` exchange with the key `loan.borrowed` or `loan.returned` | `ShipItIT.borrowAndReturnSendTwoEvents`, `aRefusedBorrowSendsNothing` |
| 5 | `Dockerfile` | A two-stage image, non-root, with a health check. `docker build -t library-api:1.0.0 .` | `docker inspect` shows `healthy` |
| 6 | `compose.app.yaml` | Run the image next to PostgreSQL, Redis and RabbitMQ | `curl localhost:8090/actuator/health` returns `UP` |
| 7 | `.github/workflows/ci.yml` | Your Session 13 workflow, plus a second job that builds the image after the tests (see the solution) | A green check with two jobs on GitHub |

Remove each `@Disabled` when its TODO is done. At the end, `mvn verify` must run **28 tests + 8 integration tests** with no skips and print `All coverage checks have been met`. Then:

```bash
export JWT_SECRET=$(openssl rand -base64 48)
docker compose -f compose.yaml -f compose.app.yaml up -d --build --wait
curl localhost:8090/actuator/health
docker compose -f compose.yaml -f compose.app.yaml down
```

The instructor shares the solution after the session.

## Bonus (Intern B track)

- Make the notification service also listen to `library.events` and keep "You borrowed 1984, due 2026-03-15" messages. You need a second binding and a second event record.
- Add a library route to the gateway: `/library/**` to the Library API, with a `RewritePath` filter that removes the `/library` prefix.
- Replace the in-memory `NotificationStore` with a Redis list (`RedisTemplate.opsForList()`), so notifications survive a restart. Keep the `NotificationStoreTest` green.
- Send a message that always fails (`ownerId` missing) and find it in the dead-letter queue in the RabbitMQ UI. Then write a small endpoint for admins that moves messages from the DLQ back to the main queue.
- Deploy the system to a real VM with HTTPS. Follow [`system/DEPLOYMENT.md`](system/DEPLOYMENT.md) and send the URL.

## After the course: your final project

Read the [final project guide](../final-project/README.md) today. Choose your project and send your instructor the repository link and your milestone 1 issue list within 2 days. The guide has the rubric, the milestones, a demo script and the questions you will be asked at the defense.

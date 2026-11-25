# Study guide: Session 14, Caching, Messaging, a Gateway and Deployment

Use this guide in the first week of your final project. It takes about 2 hours a day for 4 days. Type the exercises yourself, and answer the self-check questions out loud before opening the answers. Days 1 to 3 cover what you need for the project. Day 4 is for deployment.

## What you must be able to do after this guide

- [ ] Explain cache-aside, pick what to cache, and add `@Cacheable` and `@CacheEvict` with a TTL in Redis.
- [ ] Explain why a cache can return stale data, and two ways to limit it.
- [ ] Explain exchange, queue, binding and routing key, and draw how one message reaches two consumers.
- [ ] Publish an event only after the database commit, and say what happens to it when the broker is down.
- [ ] Write a consumer with retries and a dead-letter queue, and explain "at least once" delivery.
- [ ] Explain what an API gateway does and does not do, and add a route.
- [ ] Write a two-stage Dockerfile that runs as non-root and has a health check.
- [ ] Run several services with Compose, with secrets in `.env`, health checks and `depends_on`.
- [ ] Use Actuator's health, readiness and info endpoints, and protect the rest.

## Day 1: caching with Redis (about 2 hours)

| # | Read | Why | Time |
| --- | --- | --- | --- |
| 1 | Spring Framework reference, [Cache Abstraction](https://docs.spring.io/spring-framework/reference/integration/cache.html), read "Declarative Annotation-based Caching" up to `@CacheConfig` | `@Cacheable`, `@CacheEvict`, `@CachePut`, keys written in SpEL | 35 min |
| 2 | Spring Boot reference, [Caching](https://docs.spring.io/spring-boot/reference/io/caching.html), read "Redis" | `spring.cache.cache-names`, `spring.cache.redis.time-to-live` | 10 min |
| 3 | Redis, [Get started](https://redis.io/docs/latest/develop/get-started/) and [Strings](https://redis.io/docs/latest/develop/data-types/strings/) | What Redis is, `SET`, `GET`, `EXPIRE`, `TTL` | 20 min |
| 4 | AWS, [Caching best practices](https://aws.amazon.com/caching/best-practices/), read "Lazy loading" and "Adding TTL" | Cache-aside (the same idea as "lazy loading") and why every entry needs a TTL | 15 min |

**Practice (30 min).** Start the Task API (`cd system/task-api && mvn spring-boot:run`).

1. Log in as Sara and call `GET /api/tasks/stats` twice. Watch the log. The first call prints SQL, the second does not.
2. Open Redis: `docker compose exec redis redis-cli`. Run `KEYS *`, then `TTL "task-stats::2"`. What does the number mean?
3. Create a task, then run `KEYS *` again. Which method removed the key?
4. Remove `implements Serializable` from `PriorityCount` and call stats. Read the error. Put it back.

## Day 2: messaging with RabbitMQ (about 2 hours)

| # | Read | Why | Time |
| --- | --- | --- | --- |
| 1 | RabbitMQ, [AMQP 0-9-1 concepts](https://www.rabbitmq.com/tutorials/amqp-concepts), read "Exchanges and Exchange Types", "Queues" and "Bindings" | The vocabulary: exchange, queue, binding, routing key | 25 min |
| 2 | RabbitMQ tutorial 5, [Topics (Java)](https://www.rabbitmq.com/tutorials/tutorial-five-java) | `task.*` versus `task.#`, one message to many queues | 20 min |
| 3 | Spring Boot reference, [AMQP](https://docs.spring.io/spring-boot/reference/messaging/amqp.html) | `RabbitTemplate`, `@RabbitListener`, retry settings | 15 min |
| 4 | Spring Framework reference, [Transaction-bound Events](https://docs.spring.io/spring-framework/reference/data-access/transaction/event.html) | `@TransactionalEventListener` and its phases | 10 min |
| 5 | RabbitMQ, [Dead Letter Exchanges](https://www.rabbitmq.com/docs/dlx), read up to "Routing Dead-Lettered Messages" | Where a message goes when it is rejected | 15 min |
| 6 | microservices.io, [Transactional outbox](https://microservices.io/patterns/data/transactional-outbox.html) | The pattern to use when no event may ever be lost | 10 min |

**Practice (30 min).** Start the Task API and the notification service.

1. Open http://localhost:15672 (guest / guest). Find the `tasks.events` exchange and its binding to `notifications.task-events`.
2. Stop the notification service. Create three tasks. How many messages wait in the queue? Start the service again and call `GET /api/notifications`.
3. In the UI, publish a message to `tasks.events` with routing key `task.created` and the body `{"type":"CREATED"}`. Find it in `notifications.task-events.dlq`. Why was it not retried 3 times?
4. Stop RabbitMQ (`docker compose stop rabbitmq`) and create a task. Does the HTTP request fail? Read the Task API log. Start RabbitMQ again.

## Day 3: the gateway, Actuator and Docker images (about 2 hours)

| # | Read | Why | Time |
| --- | --- | --- | --- |
| 1 | Martin Fowler, [Microservices](https://martinfowler.com/articles/microservices.html), read up to "Decentralized Data Management" | When a system should be split, and what it costs | 30 min |
| 2 | Spring Cloud Gateway, [Server Web MVC](https://docs.spring.io/spring-cloud-gateway/reference/spring-cloud-gateway-server-webmvc.html), read "How It Works" and the `Path` predicate | Routes, predicates, filters | 15 min |
| 3 | Spring Boot reference, [Actuator endpoints](https://docs.spring.io/spring-boot/reference/actuator/endpoints.html), read "Exposing Endpoints", "Health" and "Kubernetes Probes" | What to expose, `show-details`, liveness versus readiness | 20 min |
| 4 | Docker, [Multi-stage builds](https://docs.docker.com/build/building/multi-stage/) | A big image to build, a small one to run | 10 min |
| 5 | Spring Boot reference, [Dockerfiles](https://docs.spring.io/spring-boot/reference/packaging/container-images/dockerfiles.html) | `-Djarmode=tools extract --layers`, the layer order | 15 min |

**Practice (30 min).**

1. `cd system/task-api && docker build -t task-api:dev .` Change one line in a controller and build again. Which steps say `CACHED`? How long did the second build take?
2. `docker history task-api:dev`. Find the four layer copies and their sizes.
3. Run the whole system. Call `GET http://localhost:8000/api/tasks` and then `/api/notifications` with Sara's token, and compare the `X-Served-By` header. Which route in `gateway/src/main/resources/application.yml` matched each one? What happens if you swap the two routes?
4. Log in as admin and call `/actuator/health` through the gateway with and without the token. What changes?

## Day 4: Compose, CI images and a server (about 2 hours)

| # | Read | Why | Time |
| --- | --- | --- | --- |
| 1 | Docker, [Control startup order](https://docs.docker.com/compose/how-tos/startup-order/) | `depends_on` with `condition: service_healthy` | 10 min |
| 2 | Docker, [Set environment variables](https://docs.docker.com/compose/how-tos/environment-variables/set-environment-variables/) and [Interpolation](https://docs.docker.com/compose/how-tos/environment-variables/variable-interpolation/) | `.env`, `${VAR:?error}`, `${VAR:-default}` | 15 min |
| 3 | [The Twelve-Factor App](https://12factor.net/), read III (Config), VI (Processes), X (Dev/prod parity) and XI (Logs) | The rules our compose file follows | 25 min |
| 4 | GitHub Docs, [Working with the Container registry](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-container-registry) | Pushing images from Actions with `GITHUB_TOKEN` | 15 min |
| 5 | [`system/DEPLOYMENT.md`](system/DEPLOYMENT.md), all of it | Our path from laptop to server | 20 min |

**Practice (30 min).**

1. Delete `JWT_SECRET` from `.env` and run `docker compose up`. Read the message. Put it back.
2. `docker compose stop postgres`. What does `docker compose ps` say about the Task API after a minute? What does `/actuator/health` return to the admin?
3. Push `system/` to a new GitHub repo with the workflow. Find your three images under **Packages** on your profile.

## Self-check

<details><summary>1. What is cache-aside, and what does <code>@Cacheable</code> do on each call?</summary>

Cache-aside means the application checks the cache first. On a miss it reads the database and puts the result in the cache. `@Cacheable` does exactly this around the method: it builds the key (for example `task-stats::2`), looks in Redis, and returns the stored value without running the method if it is there. On a miss it runs the method and stores the result. The method body runs only on a miss.
</details>

<details><summary>2. The cache can return old data. Why, and how do we limit it?</summary>

The cache does not know when the database changes. If a write does not evict the entry, readers see the old value. We limit this in two ways. First, every method that changes the data evicts (`@CacheEvict`). Second, a TTL (10 minutes for us) means a missed eviction is wrong for 10 minutes at most, not forever. Cache only what may be a little stale, like stats or a book's details. Never cache a bank balance or the number of copies left before a borrow.
</details>

<details><summary>3. Why is the stats method cached with <code>allEntries = true</code> on writes, and not one key?</summary>

The admin's stats (key `all`) count every user's tasks. When Sara creates a task, both `task-stats::2` and `task-stats::all` are wrong. Evicting everything is simple and correct. It costs one extra query per user the next time, which is fine for a small cache. For a big cache, you would evict the two exact keys with `@Caching(evict = {...})`.
</details>

<details><summary>4. What are an exchange, a queue, a binding and a routing key?</summary>

A producer sends a message to an **exchange** with a **routing key** (`task.completed`). The exchange does not store anything. It copies the message into every **queue** whose **binding** matches the key. A topic binding `task.#` matches every key that starts with `task.`. Consumers read from queues. Two services that both need every event each get their own queue bound to the same exchange.
</details>

<details><summary>5. Why do we send the event in a <code>@TransactionalEventListener</code>, and not in the service method itself?</summary>

In the service method the transaction is not committed yet. If we sent the message there and the commit then failed, the notification service would announce a task that does not exist. `@TransactionalEventListener` (phase `AFTER_COMMIT`) runs only after a successful commit. The other way round can still go wrong: the commit works, then RabbitMQ is down, and the event is lost. When no event may ever be lost, use the transactional outbox. Write the event to a table in the same transaction, and let a separate job send it.
</details>

<details><summary>6. What does "at least once" mean, and what must a consumer do about it?</summary>

RabbitMQ removes a message only when the consumer acknowledges it. If the consumer crashes after doing the work but before the ack, the message is delivered again. So a consumer may see the same message twice. It must be **idempotent**: handling a message twice must have the same result as handling it once. For example, store the event id and skip ids you have seen.
</details>

<details><summary>7. How does the notification service handle a message it cannot process?</summary>

Spring retries the listener 3 times, waiting 1 s, then 2 s. After the last failure, the message is rejected without requeue, and RabbitMQ sends it to the dead-letter exchange `notifications.dlx`, which puts it in `notifications.task-events.dlq`. A message that can never work, such as one with no `ownerId`, throws `AmqpRejectAndDontRequeueException`. That skips the retries and goes straight to the DLQ. Without a DLQ, a bad message is retried forever and blocks the queue, or it is silently dropped.
</details>

<details><summary>8. What does our gateway do, and what does it not do?</summary>

It is the single entry point. It chooses the service by the path and forwards the request, `Authorization` header included, and it adds `X-Served-By`. It does **not** check tokens. Each service checks its own, so a request that skips the gateway inside the network is still protected. Gateways can also do rate limiting, CORS, retries and request logging in one place.
</details>

<details><summary>9. Why does our Dockerfile have two stages and copy four layers?</summary>

The build stage needs Maven and a full JDK (about 500 MB). The run stage needs only a JRE, so the final image is smaller and has fewer tools an attacker could use. The jar is split into layers: dependencies, loader, snapshot dependencies, application. Our own classes change on every commit, but dependencies rarely change. Copying the stable layers first lets Docker reuse them, so a rebuild and a push move only a few hundred KB.
</details>

<details><summary>10. What is the difference between liveness and readiness?</summary>

**Liveness** asks "is the process alive, or is it stuck?". If it fails, the platform restarts the container. **Readiness** asks "can it serve traffic now?". If it fails, the platform sends no requests to it but does not restart it, for example while it is starting or while the database is down. Our `HEALTHCHECK` uses readiness, so Compose's `service_healthy` means "ready for requests".
</details>

<details><summary>11. Inside a container the Task API cannot reach <code>localhost:5432</code>. Why?</summary>

Each container has its own network namespace. `localhost` there is the container itself, and nothing listens on 5432 inside the Task API container. Compose puts all services on one network and gives each a DNS name equal to its service name, so the URL is `jdbc:postgresql://postgres:5432/tasks`. Port 5432 is the container port. A host port mapping such as `5433:5432` does not matter inside the network.
</details>

<details><summary>12. Name three things that make our compose file production-ready that a dev compose file usually lacks.</summary>

1. The secret comes from `.env` and Compose refuses to start without it (`${JWT_SECRET:?...}`). There is no default.
2. Health checks with `depends_on: condition: service_healthy`, and `restart: unless-stopped`.
3. Only the gateway publishes a port. The database and the services are reachable only inside the network.

There are others: the `prod` profile, a named volume for data, and fixed image tags such as `postgres:17` rather than `latest`.
</details>

## Common mistakes

| Symptom | Cause | Fix |
| --- | --- | --- |
| `SerializationException ... DefaultSerializer requires a Serializable payload` | A cached DTO is not `Serializable` | `implements Serializable` on the record and on every type inside it |
| The cache never hits | `@EnableCaching` missing, or the method is called from the same class | Add `@EnableCaching` in a `@Configuration`. Call through another bean. |
| Old data after an update | A write method has no `@CacheEvict`, or the key does not match | Evict on every write, and use the same key expression |
| `Failed to convert message`, `ClassNotFoundException: com.flairs.taskapi...` | The consumer tries to load the producer's class from `__TypeId__` | `converter.setAlwaysConvertToInferredType(true)` |
| `PRECONDITION_FAILED - inequivalent arg 'x-dead-letter-exchange'` | You changed a queue's arguments, and RabbitMQ already has it with the old ones | Delete the queue in the UI (or `docker compose down -v`) and restart |
| `transient_nonexcl_queues` deprecated | A non-durable shared queue on RabbitMQ 4 | `new Queue(name, true, false, false)` |
| A message is retried forever | No dead-letter exchange, and `default-requeue-rejected` is `true` | Set it to `false`, and add a DLX and a DLQ |
| `Connection refused: localhost/127.0.0.1:5672` in a container | `localhost` inside a container | `SPRING_RABBITMQ_HOST: rabbitmq` |
| The gateway returns 404 for a route you added | Wrong property prefix, or the more general route comes first | `spring.cloud.gateway.server.webmvc.routes`. Put specific routes before `/api/**`. |
| A container is `unhealthy` but the app works | The health check URL or port is wrong, or the start period is too short | `docker inspect --format '{{json .State.Health}}' <id>` shows each check's output |
| `exec format error` on the server | An `arm64` image (built on an Apple Silicon Mac) on an `amd64` server | Build in CI, or `docker buildx build --platform linux/amd64` |

## Mini-project (optional)

Add events to your final project. Choose one action that other parts of the system care about, such as "appointment booked", "order paid" or "ticket checked in". Publish it after commit. Write a consumer that does something visible, such as a notification, an email logged to the console, or a counter in Redis. Add an integration test that checks the message arrives, and give the consumer a dead-letter queue.

## Words to know

cache, cache hit, cache miss, cache-aside, TTL, eviction, stale data, serialization, Redis, message broker, producer, consumer, exchange, topic exchange, queue, binding, routing key, acknowledgement, at least once, idempotent, retry, dead-letter queue, transactional outbox, event, `@TransactionalEventListener`, microservice, API gateway, route, predicate, filter, Actuator, liveness, readiness, health check, Docker image, layer, multi-stage build, non-root, registry, tag, Compose, service name, `.env`, secret, reverse proxy, HTTPS, rollback.

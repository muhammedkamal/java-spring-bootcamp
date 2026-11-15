OUTPUT = "Session14_Caching_Messaging_and_Deployment.pptx"

SLIDES = [
    {"t": "title", "title": "Caching, Messaging and Deployment", "week": "WEEK 7  ·  SESSION 14",
     "notes": "The last session. Yesterday the Task API got tests and a green pipeline. Today it becomes a small system: a cache, a second service that "
              "reacts to events, a gateway in front, Docker images, and a path to a real server. We end with the final project kick-off. "
              "Check before class: Docker running, docker compose up -d --build --wait in files/session-14/system is all healthy, RabbitMQ UI open on localhost:15672."},
    {"t": "list", "title": "Today's plan",
     "items": ["Cache the slow reads in Redis",
               "Events with RabbitMQ: publish and consume",
               "A second service and a gateway",
               "Docker images, Compose, Actuator",
               "Deploy, then the final project kick-off"],
     "notes": "Morning: cache, then messaging (the biggest part). Late morning: the notification service and gateway. "
              "After lunch: images, compose, actuator, deployment. Lab 14:30. Final project kick-off at 16:30."},
    {"t": "list", "title": "By the end of today, you can",
     "items": ["Add a cache and know when it lies",
               "Send an event only after commit",
               "Retry a message, then dead-letter it",
               "Build a small, non-root image",
               "Run the whole system with one command"],
     "notes": "Checkpoint at 16:15: each intern shows curl localhost:8090/actuator/health returning UP from their library-api container."},

    {"t": "section", "title": "Caching", "num": "01", "sub": "Redis and @Cacheable",
     "notes": "Ask: which endpoint in the Task API does the most work per request? Stats: it counts every task by priority. Every dashboard refresh runs that query."},
    {"t": "cards3", "title": "Cache-aside in one picture",
     "cards": [("Hit", ["Key in Redis?", "Yes: return it", "No SQL at all", "~1 ms"]),
               ("Miss", ["Not in Redis:", "run the method,", "store the result", "with a TTL"]),
               ("Write", ["Data changed:", "evict the key", "Next read is a miss", "and gets fresh data"])],
     "notes": "Draw it on the board: app, Redis, PostgreSQL. The app always asks Redis first. Redis never talks to PostgreSQL. "
              "Cache only what is read much more than it is written and may be a little stale."},
    {"t": "code", "title": "Cache the stats", "label": "service/TaskService.java",
     "code": """@Cacheable(cacheNames = CacheConfig.TASK_STATS,
           key = "#caller.ownerFilter() ?: 'all'")
public List<PriorityCount> stats(AuthUser caller) {
  return tasks.countByPriority(caller.ownerFilter());
}

@Transactional
@CacheEvict(cacheNames = CacheConfig.TASK_STATS,
            allEntries = true)   // counts changed
public TaskResponse create(CreateTaskRequest r,
                           AuthUser caller) { ... }

# application.yml
spring:
  cache:
    cache-names: task-stats
    redis:
      time-to-live: 10m      # the safety net""",
     "card": "Pieces",
     "bullets": ["@EnableCaching once", "Key: SpEL, per user", "Evict on every write", "TTL: wrong for 10 min", "at most, never forever"],
     "notes": "Demo: call stats twice with show-sql on. SQL once. redis-cli KEYS * shows task-stats::2. TTL task-stats::2 shows seconds left. "
              "Create a task, KEYS * is empty. The admin's entry is 'all' because ownerFilter() is null for the admin."},
    {"t": "cards3", "title": "Cache traps",
     "cards": [("Serializable", ["Redis stores bytes", "DTO must implement", "Serializable, or", "error on 1st call"]),
               ("Self-call", ["this.stats() skips", "the proxy: no cache", "Same rule as", "@Transactional"]),
               ("Stale data", ["A write without evict", "Never cache: balance,", "copies left, stock", "before a decision"])],
     "notes": "Show the SerializationException live by removing implements Serializable from PriorityCount. It fails at the first call, not at startup. "
              "The self-call trap is the same proxy story from Session 10."},

    {"t": "section", "title": "Messaging", "num": "02", "sub": "Events with RabbitMQ",
     "notes": "Ask: the product owner wants a notification when a task is completed. Next month an email. Then a weekly report. Do we add all of that to TaskService?"},
    {"t": "cards3", "title": "Why a message broker",
     "cards": [("Decouple", ["Task API does not", "know who listens", "Add consumers with", "no change to it"]),
               ("Absorb", ["Consumer is down?", "Messages wait in", "the queue, nothing", "is lost"]),
               ("Cost", ["One more server", "Eventual: a delay", "Duplicates possible", "Harder to debug"])],
     "notes": "The honest part matters: a broker is not free. For one synchronous answer, an HTTP call is simpler. Use events for 'something happened, whoever cares can react'."},
    {"t": "code", "title": "Exchange, queue, binding", "label": "the words, with our names",
     "code": """producer: Task API
   |  convertAndSend("tasks.events", "task.completed", e)
   v
exchange  tasks.events   (topic: stores nothing)
   |
   |  binding "task.#"          binding "task.completed"
   v                            v
queue notifications.task-   queue reports.done (later)
      events
   |                            |
   v                            v
@RabbitListener             another service

 topic keys:  *  = exactly one word
              #  = zero or more words""",
     "card": "Words",
     "bullets": ["Exchange: routes", "Queue: stores", "Binding: a rule", "Routing key: label", "One queue per consumer"],
     "notes": "Open the RabbitMQ UI: Exchanges, tasks.events, Bindings. Then Queues. Stress: each consumer service owns its queue, so two services both get every message."},
    {"t": "code", "title": "Publish after commit", "label": "events/TaskEventPublisher.java",
     "code": """// TaskService.complete(...), inside the transaction:
publish(TaskEvent.Type.COMPLETED, task); // publishEvent

@Component
public class TaskEventPublisher {
  @TransactionalEventListener   // AFTER_COMMIT
  public void send(TaskEvent event) {
    try {
      rabbit.convertAndSend(RabbitConfig.EXCHANGE,
          event.routingKey(), event);
    } catch (AmqpException e) {
      log.error("Could not send {}", event.routingKey());
    }
  }
}""",
     "card": "Why",
     "bullets": ["Rolled back? No event", "Broker down? HTTP OK,", "event lost, logged", "Must never lose one:", "transactional outbox"],
     "notes": "Ask what goes wrong if we call rabbit.convertAndSend directly in complete() and the commit then fails. A notification for a task that is not done. "
              "Demo: docker compose stop rabbitmq, complete a task: 200, an error in the log. Start it again."},
    {"t": "code", "title": "Consume, retry, dead-letter", "label": "notification-service",
     "code": """@RabbitListener(queues = RabbitConfig.QUEUE)
public void on(TaskEvent event) {
  if (event.ownerId() == null)        // never gets better
    throw new AmqpRejectAndDontRequeueException("bad");
  store.add(event.ownerId(), Notification.from(event));
}                                     // returns = ACK

QueueBuilder.durable("notifications.task-events")
    .deadLetterExchange("notifications.dlx").build();

spring.rabbitmq.listener.simple:
  retry: { enabled: true, max-attempts: 3,
           initial-interval: 1s, multiplier: 2 }
  default-requeue-rejected: false""",
     "card": "Flow",
     "bullets": ["Throw: retry 1s, 2s", "Still fails: rejected", "Goes to the DLQ", "Bad data: DLQ at once", "A human looks there"],
     "notes": "Demo: in the UI, publish {\"type\":\"CREATED\"} to tasks.events with key task.created. It lands in notifications.task-events.dlq without retries. "
              "Explain at-least-once: a crash after the work but before the ACK means a second delivery. Consumers must be idempotent."},
    {"t": "cards3", "title": "Messaging traps",
     "cards": [("__TypeId__", ["Producer's class", "name in a header", "Consumer: set", "InferredType(true)"]),
               ("Queue args", ["Changed the DLX?", "PRECONDITION_FAILED", "Delete the queue", "and restart"]),
               ("RabbitMQ 4", ["No shared transient", "queues any more", "Tests: durable,", "delete afterwards"])],
     "notes": "All three cost us time while preparing this course. Show the ClassNotFoundException message once so they recognise it."},

    {"t": "section", "title": "A small system", "num": "03", "sub": "Two services and a gateway",
     "notes": "We now have two services. The browser should not need to know both addresses."},
    {"t": "code", "title": "The system", "label": "files/session-14/system",
     "code": """browser --> gateway :8000 --+--> task-api :8080
                            |       |  PostgreSQL, Redis
                            |       |
                            |       +-- task.* --> RabbitMQ
                            |                        |
                            +--> notification :8081 <+

task-api       owns tasks, users, projects (its DB)
notification   owns notifications (memory, last 50)
gateway        owns nothing: routes by path

Same JWT everywhere: one secret, issuer task-api.
Each service checks the token itself.""",
     "card": "Rules",
     "bullets": ["One DB per service", "Talk by events or API", "Never share tables", "Only the gateway is", "reachable from outside"],
     "notes": "Stress 'each service checks the token itself': the gateway does not, so a request that skips the gateway is still protected. "
              "Ask: when would you NOT split? Almost always at the start. A modular monolith first."},
    {"t": "code", "title": "Gateway routes", "label": "gateway/application.yml",
     "code": """spring.cloud.gateway.server.webmvc.routes:
  # top to bottom, first match wins
  - id: notifications
    uri: ${NOTIFICATIONS_URL:http://localhost:8081}
    predicates:
      - Path=/api/notifications/**, /api/notifications
    filters:
      - AddResponseHeader=X-Served-By, notification-service
  - id: task-api
    uri: ${TASK_API_URL:http://localhost:8080}
    predicates:
      - Path=/api/**, /swagger-ui/**, /v3/api-docs/**
    filters:
      - AddResponseHeader=X-Served-By, task-api""",
     "card": "Gateway",
     "bullets": ["One address", "Route by path", "Filters: headers,", "rewrite, rate limit", "No business logic"],
     "notes": "Demo: GET localhost:8000/actuator/gateway/routes, then /api/tasks and /api/notifications, and show X-Served-By. "
              "Swap the order of the two routes and show notifications going to the Task API (404)."},

    {"t": "section", "title": "Ship it", "num": "04", "sub": "Images, Compose and Actuator",
     "notes": "'Works on my machine' ends today. The image is the machine."},
    {"t": "code", "title": "A two-stage Dockerfile", "label": "task-api/Dockerfile",
     "code": """FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /src
COPY pom.xml .
RUN mvn -B -q dependency:go-offline     # cached
COPY src ./src
RUN mvn -B -q package -DskipTests && java \\
  -Djarmode=tools -jar target/task-api-1.0.0.jar \\
  extract --layers --launcher --destination target/x

FROM eclipse-temurin:25-jre-alpine
RUN addgroup -S app && adduser -S app -G app
USER app
WORKDIR /app
COPY --from=build /src/target/x/dependencies/ ./
COPY --from=build /src/target/x/application/ ./
HEALTHCHECK CMD wget -qO- \\
  localhost:8080/actuator/health/readiness || exit 1
ENTRYPOINT ["java",
  "org.springframework.boot.loader.launch.JarLauncher"]""",
     "card": "Why",
     "bullets": ["Build: JDK + Maven", "Run: JRE only", "Layers: rebuilds", "move a few KB", "Never run as root"],
     "notes": "Shortened: the real file copies four layers (dependencies, spring-boot-loader, snapshot-dependencies, application) and sets JAVA_TOOL_OPTIONS. "
              "Demo: build, change one line, build again: every step CACHED except the last ones. docker history shows the layer sizes."},
    {"t": "code", "title": "Compose the system", "label": "system/compose.yaml (part)",
     "code": """task-api:
  build: ./task-api
  image: task-api:1.0.0
  environment:
    SPRING_PROFILES_ACTIVE: prod
    SPRING_DATASOURCE_URL: jdbc:postgresql://postgres/tasks
    SPRING_DATA_REDIS_HOST: redis
    SPRING_RABBITMQ_HOST: rabbitmq
    JWT_SECRET: ${JWT_SECRET:?Set JWT_SECRET in .env}
  depends_on:
    postgres: { condition: service_healthy }
    redis: { condition: service_healthy }
    rabbitmq: { condition: service_healthy }
  restart: unless-stopped

# no "ports:" here. Only the gateway has 8000:8000""",
     "card": "Notice",
     "bullets": ["Hosts are names", "Secret from .env,", "no default", "Wait for healthy", "Ports: gateway only"],
     "notes": "Demo: docker compose up -d --build --wait, docker compose ps. Then remove JWT_SECRET from .env and show Compose refusing to start. "
              "Ask why localhost:5432 does not work inside the container."},
    {"t": "code", "title": "Actuator", "label": "task-api/application.yml",
     "code": """management:
  endpoints.web.exposure.include: health, info, metrics
  endpoint.health:
    probes.enabled: true     # /liveness, /readiness
    show-details: when-authorized
    roles: ADMIN
  info.env.enabled: true

GET /actuator/health            {"status":"UP"}  public
GET /actuator/health (admin)    db, redis, rabbit...
GET /actuator/health/readiness  used by HEALTHCHECK
GET /actuator/info              version, build time
GET /actuator/metrics           ADMIN only""",
     "card": "Probes",
     "bullets": ["Liveness: restart me", "Readiness: no traffic", "for now", "Expose little", "Protect the rest"],
     "notes": "Demo: docker compose stop postgres. Health is DOWN, readiness DOWN, the container turns unhealthy. Start it again and watch it recover. "
              "Never expose env, heapdump or beans to the internet."},
    {"t": "cards3", "title": "From laptop to server",
     "cards": [("CI", ["Tests in a matrix", "Build 3 images", "Push to ghcr.io", "Tag: commit SHA"]),
               ("Server", ["A Linux VM", "Docker + compose", ".env with secrets", "compose pull, up -d"]),
               ("In front", ["Caddy: HTTPS", "with Let's Encrypt", "Proxy to :8000", "Firewall: 22, 443"])],
     "notes": "Walk through system/DEPLOYMENT.md. Show the images job in system/.github/workflows/ci.yml. Rollback = run the previous SHA tag. "
              "We do not deploy live in class; it is a bonus."},

    {"t": "cards3", "title": "Afternoon lab: ship the library",
     "cards": [("Code", ["1 Actuator public", "2 cache books", "3 publish LoanEvent", "4 send after commit"]),
               ("Ship", ["5 Dockerfile", "6 compose.app.yaml", "7 CI image job", "app on :8090"]),
               ("Done when", ["28 tests + 8 ITs", "0 skipped", "health is UP", "from the container"])],
     "notes": "Lab 14:30 to 16:15. Tests are already written; remove @Disabled one by one. Ports differ from the Task API (5433, 6380, 5673, 8090) so both can run. "
              "Intern B track: library events in the notification service, a /library route, a Redis notification store."},
    {"t": "section", "title": "Final project", "num": "05", "sub": "Two weeks, your own API",
     "notes": "16:15. Open files/final-project/README.md on the projector."},
    {"t": "cards3", "title": "The final project",
     "cards": [("Choose", ["ClinicBook, ShopLite", "TaskHub, EventPass", "or your own idea", "(ask first)"]),
               ("Must have", ["JWT, roles, 401/403", "Tests + green CI", "Docker Compose", "README + Swagger"]),
               ("Milestones", ["M1 day 2: plan", "M2 day 5: core API", "M3 day 9: security,", "tests; day 10 demo"])],
     "notes": "Read the rubric together. 100 points. Show the defense questions: they will be asked to explain their own code. "
              "Send the repo link and M1 issue list within 2 days."},
    {"t": "materials", "title": "Materials for this session",
     "cards": [("Slides", ["Session14_Caching_Messaging", "_and_Deployment.pptx"]),
               ("Demo code", ["files/session-14/system/", "DEPLOYMENT.md"]),
               ("Lab starters", ["lab/starter/library-api"]),
               ("Lab solutions", ["lab/solutions/library-api", "STUDY-GUIDE.md"])],
     "notes": "See files/session-14/README.md and STUDY-GUIDE.md. The final project guide is files/final-project/README.md."},
    {"t": "cards3", "title": "Before the project starts",
     "cards": [("Review", ["Finish the lab", "Health UP from Docker", "Send the link"]),
               ("Practice", ["Study guide days 1 to 4", "Push images from CI", "Read DEPLOYMENT.md"]),
               ("Next up", ["Pick a project", "M1 plan in 2 days", "Demo on day 10"])],
     "notes": "Remind them: office hours twice a week during the project. Questions go in the project channel, with the error message and what they tried."},
    {"t": "list", "title": "Key takeaways",
     "items": ["Cache reads, evict on writes, add a TTL",
               "Send events after commit, expect duplicates",
               "Retry, then dead-letter: never loop forever",
               "One image, any machine, never as root",
               "Secrets in env, only the gateway exposed"],
     "notes": "Close the course: in seven weeks they went from Hello World to a tested, secured, containerised system with CI. Thank them. Then: go build."},
]

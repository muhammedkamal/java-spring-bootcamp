OUTPUT = "Session11_Docker_PostgreSQL_and_Spring_Data_JPA.pptx"

SLIDES = [
    {"t": "title", "title": "Docker, PostgreSQL and Spring Data JPA", "week": "WEEK 6  ·  SESSION 11",
     "notes": "First session of Week 6. Until now every restart lost our data, because the repository was a HashMap. "
              "Today the Task API gets a real PostgreSQL database running in Docker, managed with Flyway and used through Spring Data JPA. "
              "Check before class: everyone ran scripts/check-env.sh, Docker is running, postgres:17 is pulled."},
    {"t": "list", "title": "Today's plan",
     "items": ["Docker and Compose: PostgreSQL in one command",
               "Flyway: the schema as versioned SQL files",
               "Entities and relations with JPA",
               "Repositories: derived queries, JPQL, paging",
               "N+1, transactions, logging, then the lab"],
     "notes": "Morning: Docker, Flyway, entities. Late morning: repositories and paging. After lunch: N+1, transactions, then the lab from 14:30."},
    {"t": "list", "title": "By the end of today, you can",
     "items": ["Run and reset a database with docker compose",
               "Write a migration and say why you never edit it",
               "Map one-to-many and many-to-many relations",
               "Page, sort and filter with Pageable and @Query",
               "Find an N+1 in the SQL log and fix it"],
     "notes": "These five items are the checkpoint for the end of the day. Ask two interns at 16:45 to demo one each."},

    {"t": "section", "title": "Docker", "num": "01", "sub": "The database in a box",
     "notes": "Ask: who installed PostgreSQL by hand in Session 8? How long did it take? Docker gives everyone the same database in seconds."},
    {"t": "code", "title": "Images, containers, volumes", "label": "terminal",
     "code": """docker pull postgres:17          # download an image
docker run -d --name pg -p 5432:5432 \\
  -e POSTGRES_PASSWORD=secret postgres:17
docker ps                         # running containers
docker logs pg                    # what it printed
docker exec -it pg psql -U postgres
docker stop pg && docker rm pg    # stop, then delete

# image     = a template (files + start command)
# container = a running copy of an image
# volume    = a disk that outlives the container
# -p 5432:5432  =  host port : container port""",
     "card": "Words",
     "bullets": ["Image: read-only template", "Container: a running image", "Volume: data that survives",
                 "-p maps a port to your laptop", "-e sets an env variable"],
     "notes": "Live: run the docker run line, then docker ps, then psql into it. Then stop and rm, and show the data is gone. "
              "That is the reason for volumes. Mac users on colima: colima start first."},
    {"t": "code", "title": "Docker Compose", "label": "compose.yaml",
     "code": """services:
  postgres:
    image: postgres:17
    environment:
      POSTGRES_DB: tasks
      POSTGRES_USER: tasks
      POSTGRES_PASSWORD: tasks
    ports:
      - "5432:5432"
    volumes:
      - tasks-data:/var/lib/postgresql/data

volumes:
  tasks-data:

# docker compose up -d     start
# docker compose down      stop, keep data
# docker compose down -v   stop, DELETE data""",
     "card": "Why Compose",
     "bullets": ["The setup lives in git", "Same database for everyone", "One command up, one down",
                 "down -v resets everything"],
     "notes": "Compose turns the long docker run line into a file. Commit it with the code. "
              "Show docker compose up -d, docker compose ps, then down and down -v. Point out the named volume."},
    {"t": "code", "title": "Spring Boot starts it for you", "label": "pom.xml + application.yml",
     "code": """<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-docker-compose</artifactId>
  <scope>runtime</scope>
  <optional>true</optional>
</dependency>

spring:
  docker:
    compose:
      lifecycle-management: start-only
  jpa:
    open-in-view: false
    hibernate:
      ddl-auto: validate""",
     "card": "What happens",
     "bullets": ["mvn spring-boot:run", "finds compose.yaml", "runs docker compose up",
                 "waits until PostgreSQL is ready", "sets the datasource for you"],
     "notes": "Run mvn spring-boot:run with Docker stopped first, to show the error. Then start Docker and run again. "
              "In production the prod profile turns this off and the real URL comes from environment variables."},

    {"t": "section", "title": "Flyway", "num": "02", "sub": "The schema is code",
     "notes": "Who owns the tables? Not Hibernate. SQL files in git do, and Flyway runs them in order, once each."},
    {"t": "code", "title": "Versioned migrations", "label": "db/migration/V2__create_tasks.sql",
     "code": """CREATE TABLE tasks (
  id         BIGSERIAL PRIMARY KEY,
  title      VARCHAR(100) NOT NULL,
  priority   VARCHAR(10)  NOT NULL
             CHECK (priority IN ('LOW','MEDIUM','HIGH')),
  done       BOOLEAN NOT NULL DEFAULT FALSE,
  due_date   DATE,
  project_id BIGINT NOT NULL
             REFERENCES projects (id) ON DELETE CASCADE,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE UNIQUE INDEX ux_tasks_title ON tasks (lower(title));
CREATE INDEX ix_tasks_project ON tasks (project_id);""",
     "card": "Rules",
     "bullets": ["V<number>__<name>.sql", "Two underscores", "Runs once, in order",
                 "Never edit one that ran", "Fix with a new version"],
     "notes": "Open Adminer on 8081 and show flyway_schema_history: version, checksum, success. "
              "Demo the checksum error: add a space to V2 and restart. Undo it."},
    {"t": "code", "title": "Changing the schema later", "label": "V5__add_estimate_hours.sql",
     "code": """ALTER TABLE tasks
  ADD COLUMN estimate_hours INT
  CHECK (estimate_hours BETWEEN 1 AND 100);

UPDATE tasks SET estimate_hours = 2 WHERE done = FALSE;

-- ddl-auto: validate
--   Hibernate checks entity vs table at startup.
--   Mismatch?  The app refuses to start.
--
-- ddl-auto: update / create   (not in this course)
--   Hibernate changes tables by itself:
--   no history, no review, data loss risk.""",
     "card": "validate",
     "bullets": ["Flyway writes the schema", "Hibernate only checks it", "Fail fast at startup",
                 "Same steps in production"],
     "notes": "Show V5 and the matching estimateHours field in Task. Then break the field name and read the Schema validation error together."},

    {"t": "section", "title": "Entities", "num": "03", "sub": "Java objects, database rows",
     "notes": "Last week Task was a record. Now it is a class that Hibernate can create, track and update."},
    {"t": "code", "title": "An entity", "label": "model/Task.java",
     "code": """@Entity
@Table(name = "tasks")
@EntityListeners(AuditingEntityListener.class)
public class Task {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 100)
  private String title;

  @Enumerated(EnumType.STRING)   // "HIGH", not 2
  private Priority priority;

  @Column(name = "due_date")
  private LocalDate dueDate;

  @CreatedDate @Column(name = "created_at")
  private Instant createdAt;""",
     "card": "Must have",
     "bullets": ["@Entity and an @Id", "A no-arg constructor", "Not a record: it changes",
                 "STRING enums, never ORDINAL", "Auditing fills dates"],
     "notes": "Why not a record? Hibernate needs a no-arg constructor and mutable fields to track changes. "
              "Records stay as DTOs. Show JpaConfig with @EnableJpaAuditing."},
    {"t": "code", "title": "Relations", "label": "Task.java + Project.java",
     "code": """// Task: many tasks belong to one project (owning side)
@ManyToOne(fetch = FetchType.LAZY, optional = false)
@JoinColumn(name = "project_id")
private Project project;

// Task: many tasks, many tags, through a join table
@ManyToMany
@JoinTable(name = "task_tags",
    joinColumns = @JoinColumn(name = "task_id"),
    inverseJoinColumns = @JoinColumn(name = "tag_id"))
private Set<Tag> tags = new HashSet<>();

// Project: the other side, read-only view
@OneToMany(mappedBy = "project")
private List<Task> tasks = new ArrayList<>();""",
     "card": "Remember",
     "bullets": ["Owner has the FK column", "mappedBy = not the owner", "Always LAZY on @ManyToOne",
                 "Never return entities", "Map to DTOs in service"],
     "notes": "Draw the three tables and the join table on the board. The side with @JoinColumn owns the relation. "
              "Ask: what would happen if a controller returned a Project with its tasks, and each task its project? Infinite JSON."},

    {"t": "section", "title": "Repositories", "num": "04", "sub": "Queries without SQL strings",
     "notes": "Our Session 10 repository was 60 lines of HashMap code. Spring Data JPA writes the implementation for us."},
    {"t": "code", "title": "Derived queries and JPQL", "label": "repository/TaskRepository.java",
     "code": """public interface TaskRepository
    extends JpaRepository<Task, Long> {

  // the method NAME is the query
  boolean existsByTitleIgnoreCase(String title);

  List<Task> findByDueDateBeforeAndDoneFalse(LocalDate d);

  // JPQL: entity and field names, not tables
  @Query(\"\"\"
    select t from Task t
    where (:done is null or t.done = :done)
      and (:priority is null or t.priority = :priority)
    \"\"\")
  Page<Task> search(Boolean done, Priority priority,
                    Pageable pageable);
}""",
     "card": "Free methods",
     "bullets": ["findById, findAll", "save, delete, count", "existsById", "Typos fail at startup",
                 "null filter = skip it"],
     "notes": ""
              "Type a typo (findByTitel) live and read the startup error. Then show the JPQL search and the SQL it prints."},
    {"t": "code", "title": "Paging and sorting", "label": "TaskController + PageResponse",
     "code": """@GetMapping
public PageResponse<TaskResponse> list(
    @RequestParam(required = false) Boolean done,
    @RequestParam(required = false) Priority priority,
    @PageableDefault(size = 10, sort = "id")
    Pageable pageable) {
  return service.search(done, priority, pageable);
}

public record PageResponse<T>(List<T> content,
    int page, int size,
    long totalElements, int totalPages) { }

// GET /api/tasks?page=0&size=3&sort=dueDate,asc
// SQL: ... order by due_date offset 0 limit 3
//      + select count(*) ...""",
     "card": "Why",
     "bullets": ["Never return 10,000 rows", "page starts at 0", "sort=field,asc|desc",
                 "Our record, not Page", "Two queries: data + count"],
     "notes": "Call the URL in the browser with different page and size values. Show the limit/offset SQL and the count query."},
    {"t": "code", "title": "Projections", "label": "TaskRepository + PriorityCount",
     "code": """public record PriorityCount(Priority priority,
                            long count) { }

@Query(\"\"\"
  select new com.flairs.taskapi.dto.PriorityCount(
      t.priority, count(t))
  from Task t
  group by t.priority
  order by t.priority
  \"\"\")
List<PriorityCount> countByPriority();

// GET /api/tasks/stats
// [{"priority":"LOW","count":2}, ...]""",
     "card": "When",
     "bullets": ["Reports and counts", "Only the columns you need", "Straight into a DTO",
                 "No entity is loaded"],
     "notes": "Projections are how you build dashboards and summaries without loading full entities."},

    {"t": "section", "title": "Performance and transactions", "num": "05", "sub": "Watch the SQL log",
     "notes": "Turn on org.hibernate.SQL DEBUG if it is not already. From now on the console is part of the code review."},
    {"t": "code", "title": "The N+1 problem", "label": "console, GET /api/tasks?size=6",
     "code": """-- WITHOUT @EntityGraph: 1 + 6 queries
select t.* from tasks t limit 6
select p.* from projects p where p.id=1
select p.* from projects p where p.id=2
select p.* from projects p where p.id=1   ...

-- WITH @EntityGraph(attributePaths = "project")
select t.*, p.* from tasks t
  left join projects p on p.id = t.project_id
  limit 6

-- or in JPQL:
select t from Task t join fetch t.project p
where p.id = :projectId""",
     "card": "Fixes",
     "bullets": ["@EntityGraph", "join fetch in JPQL", "batch_fetch_size for sets", "Count selects per call"],
     "notes": "Live: remove @EntityGraph, set default_batch_fetch_size to 1, call the list, count the selects. Put it back and count again. "
              "This is the most common JPA performance bug in real projects."},
    {"t": "code", "title": "@Transactional", "label": "service/TaskService.java",
     "code": """@Service
@Transactional(readOnly = true)
public class TaskService {

  @Transactional
  public TaskResponse update(Long id, UpdateTaskRequest r) {
    Task task = get(id);          // managed entity
    task.setTitle(r.title().strip());
    task.setDone(r.done());
    // no save(): dirty checking writes the UPDATE
    tasks.flush();                // run it now
    return TaskMapper.toResponse(task);
  }                               // commit here
}
// Exception inside = rollback: nothing is saved""",
     "card": "Rules",
     "bullets": ["On the service, not controller", "readOnly for reads", "@Transactional for writes",
                 "Runtime exception = rollback", "Map to DTO inside it"],
     "notes": "Explain persistence context in one sentence: a cache of the entities this transaction loaded. "
              "At commit Hibernate compares each one with its snapshot. Show the UPDATE in the log without a save call."},
    {"t": "code", "title": "When the database says no", "label": "GlobalExceptionHandler.java",
     "code": """@ExceptionHandler(DataIntegrityViolationException.class)
public ProblemDetail handleConstraint(
    DataIntegrityViolationException ex) {
  log.warn("Constraint violation: {}",
      ex.getMostSpecificCause().getMessage());
  ProblemDetail p = ProblemDetail.forStatusAndDetail(
      HttpStatus.CONFLICT, "The data breaks a database rule");
  p.setTitle("Conflict");
  return p;
}

// unique index   -> duplicate title at the same time
// foreign key    -> delete a book with loan history
// check          -> priority 'URGENT' by hand""",
     "card": "Two layers",
     "bullets": ["Service check: nice message", "Constraint: the guarantee", "Log details, hide them",
                 "Client gets a 409"],
     "notes": "Two requests at the same moment both pass existsByTitle. Only the unique index stops the second. "
              "Log the database message for us, send a plain message to the client."},

    {"t": "list", "title": "Logging that helps",
     "items": ["Use SLF4J: private static final Logger log",
               "log.info(\"Loan {} returned\", id), not string +",
               "DEBUG for us, INFO for normal events, WARN, ERROR",
               "Levels per package in application.yml",
               "prod profile: SQL log off, app at INFO"],
     "notes": "Show the levels in application.yml and the prod profile. Never log passwords or tokens. That matters next session."},
    {"t": "cards3", "title": "Afternoon lab: Library API on PostgreSQL",
     "cards": [("What you build", ["Authors, books, members", "Loans: borrow and return", "PostgreSQL on port 5433",
                                   "Flyway V1 to V5"]),
               ("Starter TODOs", ["1 V4 loans migration", "2 Map the Loan entity", "3 Loan queries",
                                  "4 Paged search, no N+1", "5 borrow  6 return"]),
               ("Done when", ["App starts (validate)", "Max 3 loans: 409", "No copies left: 409",
                              "Tests green, 1 select per list"])],
     "notes": "Lab 14:30 to 16:45. The starter compiles but will not start until TODO 1 and 2 match. That is on purpose: read the error. "
              "Intern B track: overdue endpoint, V6 migration, Hibernate statistics."},
    {"t": "materials", "title": "Materials for this session",
     "cards": [("Slides", ["Session11_Docker_PostgreSQL", "_and_Spring_Data_JPA.pptx"]),
               ("Demo code", ["files/session-11/task-api-v3/", "compose.yaml, V1..V5"]),
               ("Lab starters", ["lab/starter/library-api"]),
               ("Lab solutions", ["lab/solutions/library-api", "STUDY-GUIDE.md"])],
     "notes": "See files/session-11/README.md and STUDY-GUIDE.md. Environment help: environment/SETUP-WEEKS-6-8.md."},
    {"t": "cards3", "title": "Before the next session",
     "cards": [("Review", ["Finish the lab and tests", "docker compose down -v", "and rebuild from zero"]),
               ("Practice", ["Study guide days 1 to 3", "Mini-project: comments", "Answer the self-check"]),
               ("Next up", ["Spring Security", "Login with JWT", "Roles and ownership"])],
     "notes": "Next session the API learns who is calling. Users, passwords, tokens and roles."},
    {"t": "list", "title": "Key takeaways",
     "items": ["Compose gives everyone the same database",
               "Flyway owns the schema. Never edit a migration",
               "Entities stay inside. DTOs go out",
               "Count the selects: fix N+1 with fetch",
               "Transactions on the service, constraints in the DB"],
     "notes": "Close: the Task API now looks like a real backend. Everything from today returns in the final project."},
]

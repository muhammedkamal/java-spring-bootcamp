OUTPUT = "Session13_Testing_and_CI.pptx"

SLIDES = [
    {"t": "title", "title": "Testing and Continuous Integration", "week": "WEEK 7  ·  SESSION 13",
     "notes": "First session of the last week. The Task API works and is secure, but every change could break it and we would only find out by hand. "
              "Today we write tests at three levels and make GitHub run them on every push. "
              "Check before class: Docker is running, task-api-v5 passes mvn verify, and you have a GitHub repo ready for the CI demo."},
    {"t": "list", "title": "Today's plan",
     "items": ["Why test, and the test pyramid",
               "Unit tests: JUnit 5, AssertJ, Mockito",
               "Slices: @WebMvcTest and @DataJpaTest",
               "Testcontainers and integration tests",
               "Coverage, GitHub Actions, then the lab"],
     "notes": "Morning: pyramid, unit tests, web slice. Late morning: data slice with Testcontainers. After lunch: integration tests, coverage and CI. Lab from 14:30."},
    {"t": "list", "title": "By the end of today, you can",
     "items": ["Choose unit, slice or integration for a rule",
               "Mock a dependency and verify the call",
               "Test status codes, JSON and security",
               "Test queries on a real PostgreSQL",
               "Get a green check on every push"],
     "notes": "Checkpoint at 16:45: each intern shows a green GitHub Actions run of their lab."},

    {"t": "section", "title": "Why we test", "num": "01", "sub": "The test pyramid",
     "notes": "Ask: how did you know your Session 12 lab worked? Clicking in Postman. How long would it take to check all 40 requests after every change?"},
    {"t": "cards3", "title": "What tests buy you",
     "cards": [("Confidence", ["Change code without fear", "Refactor on Friday", "Catch a broken rule in", "seconds, not in prod"]),
               ("Documentation", ["A test name is a rule:", "someoneElsesTaskIs404", "Always up to date,", "because it runs"]),
               ("Design", ["Hard to test means", "too many jobs in one", "class: split it", "Small, clear methods"])],
     "notes": "The real cost of no tests: fear. Teams without tests stop improving code because every change might break something. "
              "Read a test name aloud and show it explains a business rule better than a comment."},
    {"t": "code", "title": "The test pyramid", "label": "task-api-v5, from slow to fast",
     "code": """            /\\
           /  \\      Integration  (*IT)        5 tests
          / IT \\     whole app + real DB    ~9 s
         /------\\
        /        \\    Slices                  13 tests
       /  slices  \\   one layer of Spring    ~9 s
      /------------\\
     /              \\  Unit                   10 tests
    /     unit       \\ plain Java + mocks     < 1 s
   /------------------\\

 Up:   fewer, slower, catch wiring bugs
 Down: many, fast, pin down one rule
 Ice cream cone (all manual / all E2E) = slow + flaky""",
     "card": "Rule of thumb",
     "bullets": ["Most tests at the bottom", "A few that prove wiring", "Each rule tested once,", "at the lowest level", "that can see it"],
     "notes": "Draw it. Then ask where each check goes: 'priority is required' (web slice), 'only the owner can complete' (unit), "
              "'search by text ignores case' (data slice), 'login then use the token' (integration)."},

    {"t": "section", "title": "Unit tests", "num": "02", "sub": "JUnit 5, AssertJ and Mockito",
     "notes": "Plain Java. No Spring, no database. These are the tests you will write most."},
    {"t": "code", "title": "Arrange, act, assert", "label": "service/TaskServiceTest.java",
     "code": """@ExtendWith(MockitoExtension.class)
class TaskServiceTest {
  @Mock TaskRepository tasks;
  @Mock ProjectService projects;
  @Mock TagRepository tags;
  @InjectMocks TaskService service;   // real, with mocks

  @Test
  void createRejectsADuplicateTitleInTheSameProject() {
    // arrange
    when(projects.getVisible(10L, SARA))
        .thenReturn(projectOwnedBy(2));
    when(tasks.existsByProjectIdAndTitleIgnoreCase(
        10L, "Learn Spring")).thenReturn(true);
    // act + assert
    assertThatThrownBy(() -> service.create(req, SARA))
        .isInstanceOf(DuplicateException.class);
    verify(tasks, never()).save(any());
  }""",
     "card": "Mockito",
     "bullets": ["@Mock: a fake", "when(..).thenReturn(..)", "verify(..): was it called?", "never(), times(2)", "@InjectMocks wires them"],
     "notes": "Run it: under a second. Then break the rule on purpose in TaskService (remove the exists check) and show it goes red. "
              "Explain why we verify save is never called: the rule is 'reject AND do not save'."},
    {"t": "code", "title": "AssertJ: read like English", "label": "assertions we use",
     "code": """assertThat(task.done()).isTrue();
assertThat(task.title()).isEqualTo("Learn Spring");

assertThat(page.getTotalElements()).isEqualTo(2);
assertThat(page.getContent())
    .extracting(Task::getTitle)
    .containsExactly("Buy groceries", "Pay the bill");

assertThat(counts).containsExactlyInAnyOrder(
    tuple(LOW, 1L), tuple(MEDIUM, 2L), tuple(HIGH, 3L));

assertThatThrownBy(() -> service.findById(5L, OMAR))
    .isInstanceOf(NotFoundException.class)
    .hasMessageContaining("5");

@ParameterizedTest
@CsvSource({"'', title is required", "x, too short"})
void rejects(String title, String message) { ... }""",
     "card": "Tips",
     "bullets": ["Start: assertThat(actual)", "IDE autocompletes the rest", "containsExactly = order", "InAnyOrder when not", "Good failure messages"],
     "notes": "Show a failing assertion and read the message. AssertJ tells you expected and actual, and for lists which element differs."},

    {"t": "section", "title": "Slice tests", "num": "03", "sub": "One layer of Spring at a time",
     "notes": "A unit test cannot check the URL, the JSON, the validation or the security. For that we start the web layer, and only the web layer."},
    {"t": "code", "title": "@WebMvcTest", "label": "controller/TaskControllerTest.java",
     "code": """@WebMvcTest(TaskController.class)
@Import(SecurityConfig.class)
@EnableConfigurationProperties(CorsProperties.class)
class TaskControllerTest {
  @Autowired MockMvc mvc;
  @MockitoBean TaskService service;
  @MockitoBean JwtDecoder jwtDecoder;

  @Test
  void getReturnsTheTaskAsJson() throws Exception {
    when(service.findById(1L, SARA))
        .thenReturn(task(1, "Learn Spring"));

    mvc.perform(get("/api/tasks/1").with(sara()))
       .andExpect(status().isOk())
       .andExpect(jsonPath("$.id").value(1))
       .andExpect(jsonPath("$.title")
           .value("Learn Spring"));
  }""",
     "card": "What starts",
     "bullets": ["Controller + advice", "Jackson, validation", "Our SecurityConfig", "No service, no DB", "@MockitoBean the rest"],
     "notes": "MockMvc sends a fake HTTP request through the real DispatcherServlet and filters, with no server. "
              "The two extra annotations are the slice's price: we import our SecurityConfig and give it the beans it needs."},
    {"t": "code", "title": "Testing security in a slice", "label": "jwt() from spring-security-test",
     "code": """static JwtRequestPostProcessor sara() {
  return jwt()
    .jwt(j -> j.subject("2")
               .claim("email", "sara@flairs.dev")
               .claim("roles", List.of("USER")))
    .authorities(new SimpleGrantedAuthority("ROLE_USER"));
}

@Test
void withoutATokenTheAnswerIs401() throws Exception {
  mvc.perform(get("/api/tasks/1"))
     .andExpect(status().isUnauthorized())
     .andExpect(header().string("WWW-Authenticate",
         containsString("Bearer")));
  verifyNoInteractions(service);
}

// USER on /api/admin/users -> 403, ADMIN -> 200""",
     "card": "Gotcha",
     "bullets": ["jwt() skips our converter", "roles claim is ignored", "Pass authorities yourself", "No secret, no signing", "IT tests the real path"],
     "notes": "Live: remove .authorities and run AdminControllerTest. The ADMIN test gets 403. Explain: jwt() reads 'scope' by default, not our 'roles'. "
              "That is fine for a slice; AuthFlowIT checks the real converter with a real token."},
    {"t": "code", "title": "Validation and Location", "label": "TaskControllerTest, two more",
     "code": """mvc.perform(post("/api/tasks").with(sara())
     .contentType(MediaType.APPLICATION_JSON)
     .content(\"\"\"
        {"title": "", "projectId": -1}
        \"\"\"))
   .andExpect(status().isBadRequest())
   .andExpect(jsonPath("$.errors.title")
       .value("title is required"))
   .andExpect(jsonPath("$.errors.priority").exists())
   .andExpect(jsonPath("$.errors.projectId")
       .value("projectId must be positive"));
verify(service, never()).create(any(), any());

when(service.create(any(), eq(SARA)))
    .thenReturn(task(7, "Write tests"));
// POST a valid body ->
//   201, Location: /api/tasks/7""",
     "card": "Checks",
     "bullets": ["The error contract", "is part of the API", "Bad input never", "reaches the service", "201 + Location"],
     "notes": "The front-end team relies on errors.title. If someone renames the field, this test fails before they do."},
    {"t": "code", "title": "@DataJpaTest", "label": "repository/TaskRepositoryTest.java",
     "code": """@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Import({TestcontainersConfiguration.class,
         JpaConfig.class})
class TaskRepositoryTest {
  @Autowired TaskRepository tasks;

  @Test
  void searchFiltersByOwnerAndPriority() {
    var page = tasks.search(2L, false, HIGH,
        PageRequest.of(0, 1, Sort.by("title")));
    assertThat(page.getTotalElements()).isEqualTo(2);
    assertThat(page.getTotalPages()).isEqualTo(2);
    assertThat(page.getContent().get(0).getTitle())
        .isEqualTo("Move the API to PostgreSQL");
  }
}
// each test runs in a transaction, rolled back after""",
     "card": "What starts",
     "bullets": ["JPA + Flyway", "Repositories only", "Real PostgreSQL", "Seed data from V2", "Rollback after each"],
     "notes": "replace = NONE stops Spring swapping our datasource for H2. JpaConfig gives auditing, so createdAt is set. "
              "The data comes from our Flyway seed migration: the same data as dev."},

    {"t": "section", "title": "Real databases", "num": "04", "sub": "Testcontainers and integration tests",
     "notes": "Why not H2? Our migrations use PostgreSQL SQL, and our queries depend on PostgreSQL behaviour. A green test on H2 proves nothing about production."},
    {"t": "code", "title": "Testcontainers", "label": "TestcontainersConfiguration.java",
     "code": """@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {

  @Bean
  @ServiceConnection          // sets url, user, password
  PostgreSQLContainer postgres() {
    return new PostgreSQLContainer("postgres:17-alpine");
  }
}

// dev on a throw-away DB, no compose needed:
public class TestTaskApiApplication {
  public static void main(String[] args) {
    SpringApplication.from(TaskApiApplication::main)
        .with(TestcontainersConfiguration.class)
        .run(args);
  }
}""",
     "card": "How it works",
     "bullets": ["Starts Docker container", "Random free port", "@ServiceConnection wires", "the datasource", "Stopped after the run"],
     "notes": "Run TaskRepositoryTest and in another terminal run docker ps: the postgres:17-alpine container with a random port. "
              "The same bean is shared by the data slice and every IT, and Spring caches the context so it starts once."},
    {"t": "code", "title": "An integration test", "label": "AuthFlowIT.java",
     "code": """@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class AuthFlowIT {
  @Autowired MockMvc mvc;

  @Test
  void loginThenUseTheToken() throws Exception {
    String body = mvc.perform(post("/api/auth/login")
          .contentType(APPLICATION_JSON)
          .content(LOGIN_SARA))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();
    String token = JsonPath.read(body, "$.accessToken");

    mvc.perform(get("/api/projects")
          .header("Authorization", "Bearer " + token))
       .andExpect(jsonPath("$.length()").value(2));
  }""",
     "card": "Proves",
     "bullets": ["Every bean, real wiring", "BCrypt, real JWT signing", "Real roles converter", "Real Flyway + Postgres", "Name ends in IT"],
     "notes": "Walk through the other ITs: project 3 is 404 for Sara, wrong password is 401 'Invalid email, password or token', "
              "a tampered token is 401, and refresh reuse revokes everything. These are the bugs a slice cannot see."},
    {"t": "list", "title": "Good tests, bad tests",
     "items": ["One rule per test, named after the rule",
               "Never depend on another test's data",
               "No sleep(), no real clock, no network",
               "saveAndFlush when the DB must check",
               "Break the code once: the test must go red"],
     "notes": "Show the saveAndFlush trap: change it to save in the duplicate test, it passes without throwing, because rollback means no INSERT was sent. "
              "A test that never failed is not a test."},

    {"t": "section", "title": "Automate it", "num": "05", "sub": "Coverage and GitHub Actions",
     "notes": "Tests only help if they run. People forget. Robots don't."},
    {"t": "code", "title": "Surefire, Failsafe, JaCoCo", "label": "pom.xml, build section",
     "code": """mvn test     Surefire  -> *Test   (unit + slices)
mvn verify   + package
             Failsafe  -> *IT     (integration)
             JaCoCo    -> merge, report, check

<plugin>
  <artifactId>jacoco-maven-plugin</artifactId>
  <execution><id>check</id>
    <goals><goal>check</goal></goals>
    <configuration><rules><rule>
      <element>BUNDLE</element>
      <limits><limit>
        <counter>LINE</counter>
        <value>COVEREDRATIO</value>
        <minimum>0.70</minimum>
      </limit></limits>
    </rule></rules></configuration>
  </execution>
</plugin>""",
     "card": "Coverage",
     "bullets": ["Which lines ran", "Not: were they checked", "Finds untested code", "Gate: build fails < 70%", "Report: target/site"],
     "notes": "Open target/site/jacoco/index.html. Green, yellow, red lines. Then set the minimum to 0.90 and show the build fail with the exact message. "
              "Warn: 100% coverage with no assertions is possible and worthless."},
    {"t": "code", "title": "GitHub Actions", "label": ".github/workflows/ci.yml",
     "code": """name: CI
on: [push, pull_request]
jobs:
  build:
    runs-on: ubuntu-latest        # Docker included
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: '25'
          cache: maven
      - run: mvn -B verify
      - uses: actions/upload-artifact@v4
        if: always()
        with:
          name: test-reports
          path: target/*-reports""",
     "card": "Words",
     "bullets": ["Workflow: the file", "Event: push, PR", "Job on a runner", "Steps: run or uses", "Artifact: saved files"],
     "notes": "Live: push a branch, open the Actions tab, watch the log. Then break a test, push, open a PR and show the red X. "
              "Testcontainers works on GitHub's Ubuntu runners with no extra setup. Branch protection can block merging a red PR."},
    {"t": "cards3", "title": "Green here, red in CI",
     "cards": [("Not committed", ["A new file you never", "added: git status", "Ignored by .gitignore"]),
               ("Your machine only", ["A running local DB", "An env variable", "Another Java version"]),
               ("Hidden order", ["Tests sharing data", "Time zone, today's date", "HashSet order"])],
     "notes": "When CI is red: read the log from the bottom up, download the test-reports artifact, run the same command locally with a clean checkout."},
    {"t": "cards3", "title": "Afternoon lab: test the library",
     "cards": [("Web slice", ["1 member reads: 200", "2 no token: 401", "3 member adds: 403", "4 librarian: 201, 5 ISBN"]),
               ("Data + IT", ["6 author search", "7 duplicate ISBN", "8 borrow, return, 409", "9 last copy: 409"]),
               ("Done when", ["10 coverage gate + CI", "26 tests + 4 ITs", "0 skipped", "Green check on GitHub"])],
     "notes": "Lab 14:30 to 16:45. The code under test is finished; interns only write tests. Remove each @Disabled when done. "
              "Intern B track: LoanControllerTest, loan-limit IT, a status badge, a parameterized test."},
    {"t": "materials", "title": "Materials for this session",
     "cards": [("Slides", ["Session13_Testing", "_and_CI.pptx"]),
               ("Demo code", ["files/session-13/task-api-v5/", "tests + ci.yml"]),
               ("Lab starters", ["lab/starter/library-api"]),
               ("Lab solutions", ["lab/solutions/library-api", "STUDY-GUIDE.md"])],
     "notes": "See files/session-13/README.md and STUDY-GUIDE.md. Docker and Colima help: environment/SETUP-WEEKS-6-8.md, Testcontainers section."},
    {"t": "cards3", "title": "Before the next session",
     "cards": [("Review", ["Finish the lab", "Green CI run on GitHub", "Send the link"]),
               ("Practice", ["Study guide days 1 to 3", "One test for a class", "under 50% coverage"]),
               ("Next up", ["Cache, messages, gateway", "Docker image, deploy", "Final project kick-off"])],
     "notes": "Ask everyone to read the final project guide tonight and come with a choice. Next session is the last one."},
    {"t": "list", "title": "Key takeaways",
     "items": ["Many unit tests, some slices, few ITs",
               "Mock what you don't test, verify the calls",
               "Test SQL on the real database",
               "A test that never failed proves nothing",
               "CI runs every test on every push"],
     "notes": "Close: from now on, every final project commit runs through CI. The rubric checks for a green pipeline and real tests."},
]

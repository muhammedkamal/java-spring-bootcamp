OUTPUT = "Session12_Spring_Security_and_JWT.pptx"

SLIDES = [
    {"t": "title", "title": "Spring Security and JWT", "week": "WEEK 6  ·  SESSION 12",
     "notes": "Second session of Week 6. Until now anyone could call any endpoint and change anyone's data. "
              "Today the Task API learns who is calling: users with BCrypt passwords, login that returns a signed JWT, "
              "roles, and ownership rules. Check before class: task-api-v4 builds, Docker is running."},
    {"t": "list", "title": "Today's plan",
     "items": ["Authentication vs authorization: 401, 403, 404",
               "The security filter chain and URL rules",
               "Users and BCrypt passwords",
               "Login, JWT access tokens, refresh tokens",
               "Roles, ownership, CORS, then the lab"],
     "notes": "Morning: concepts, filter chain, passwords. Late morning: JWT login. After lunch: roles, ownership, refresh, CORS. Lab from 14:30."},
    {"t": "list", "title": "By the end of today, you can",
     "items": ["Pick 401, 403 or 404 for a refused request",
               "Write URL rules and @PreAuthorize rules",
               "Store and check passwords with BCrypt",
               "Issue and verify a JWT, and read its claims",
               "Enforce 'only the owner' in the service"],
     "notes": "These five are the checkpoint for 16:45. Ask interns to show each with a curl or a test."},

    {"t": "section", "title": "Who are you?", "num": "01", "sub": "Authentication and authorization",
     "notes": "Ask: in the v3 API, what stops me deleting your project? Nothing. Today we fix that."},
    {"t": "cards3", "title": "Three answers to a refused request",
     "cards": [("401 Unauthorized", ["We do not know you", "No token, bad token,", "expired token, bad password",
                                     "Header: WWW-Authenticate"]),
               ("403 Forbidden", ["We know you", "Your role may not do this", "USER on /api/admin/users"]),
               ("404 Not Found", ["Not yours, so it does", "not exist for you", "Hides which ids exist"])],
     "notes": "401 is really 'unauthenticated'. 403 is 'authenticated but not allowed'. "
              "For other people's data we answer 404: a 403 would confirm that project 3 exists. This is the BOLA lesson from OWASP API1."},
    {"t": "code", "title": "The filter chain", "label": "a request, top to bottom",
     "code": """HTTP request  GET /api/tasks
  Authorization: Bearer eyJhbGciOi...
        |
  [ CorsFilter ]             preflight? allowed origin?
  [ BearerTokenAuthFilter ]  read token, JwtDecoder:
                             signature, exp, iss
        |  ok -> SecurityContext = Jwt + ROLE_USER
        |  bad -> 401 (entry point)
  [ AuthorizationFilter ]    URL rules: permitAll,
                             hasRole, authenticated
        |  no -> 403 (access denied handler)
  DispatcherServlet
  TaskController  @AuthenticationPrincipal Jwt
  @PreAuthorize   checked before the method
  TaskService     owner check -> 404""",
     "card": "Remember",
     "bullets": ["Security runs before MVC", "Filters set the SecurityContext", "First matching URL rule wins",
                 "Method rules run later", "Ownership is business logic"],
     "notes": "Draw this on the board. Turn on DEBUG logging for org.springframework.security and show the filter list in the console. "
              "Point out that a 401 never reaches the controller."},
    {"t": "code", "title": "SecurityConfig", "label": "security/SecurityConfig.java",
     "code": """@Configuration
@EnableMethodSecurity
public class SecurityConfig {
  @Bean
  SecurityFilterChain api(HttpSecurity http) throws Exception{
    http
      .csrf(csrf -> csrf.disable())
      .sessionManagement(s -> s.sessionCreationPolicy(
          SessionCreationPolicy.STATELESS))
      .authorizeHttpRequests(auth -> auth
        .requestMatchers(PUBLIC).permitAll()
        .requestMatchers("/api/admin/**").hasRole("ADMIN")
        .anyRequest().authenticated())
      .oauth2ResourceServer(o -> o.jwt(jwt -> jwt
        .jwtAuthenticationConverter(converter())));
    return http.build();
  }
}""",
     "card": "Line by line",
     "bullets": ["No cookies: CSRF off", "STATELESS: no HTTP session", "PUBLIC: login, register, swagger",
                 "Specific rules first", "anyRequest() always last"],
     "notes": "PUBLIC holds /api/auth/login, register, refresh, the swagger paths and /error. "
              "Ask: what happens if anyRequest() comes first? The app refuses to start. The real file also wires the 401/403 handlers and CORS."},

    {"t": "section", "title": "Passwords", "num": "02", "sub": "Never store what you can hash",
     "notes": "Ask: a company leaks its users table. What do the attackers want? Passwords people reuse elsewhere."},
    {"t": "code", "title": "Users and BCrypt", "label": "V6__create_users.sql + AuthService",
     "code": """CREATE TABLE users (
  id            BIGSERIAL PRIMARY KEY,
  email         VARCHAR(255) NOT NULL UNIQUE,
  password_hash VARCHAR(100) NOT NULL,
  display_name  VARCHAR(100) NOT NULL,
  role          VARCHAR(20)  NOT NULL
                CHECK (role IN ('USER', 'ADMIN')));

// register
users.save(new User(email,
    passwordEncoder.encode(request.password()),
    name, Role.USER));                 // never ADMIN
// login
if (!passwordEncoder.matches(raw, user.getPasswordHash()))
    throw new InvalidCredentialsException();

$2a$10$9Hz/YZf0rkqQsQGARvKI4O...  (salt + hash)""",
     "card": "BCrypt",
     "bullets": ["Salted: same password,", "different hash", "Slow on purpose: cost 10",
                 "matches() re-hashes and compares", "Same error for email and password"],
     "notes": "Show the three seed users in psql: same password secret123, three different hashes. "
              "Login says 'Email or password is wrong' for both cases, so attackers cannot discover which emails exist."},

    {"t": "section", "title": "JWT", "num": "03", "sub": "A signed note from the server",
     "notes": "After login, how does the API remember you on the next request without a session? It gives you a signed token."},
    {"t": "code", "title": "What is inside a JWT", "label": "header . payload . signature",
     "code": """eyJhbGciOiJIUzI1NiJ9 . eyJpc3MiOiJ0YXNr... . 3kD9f...

header   {"alg": "HS256"}
payload  {
           "iss": "task-api",
           "sub": "2",
           "email": "sara@flairs.dev",
           "roles": ["USER"],
           "iat": 1763200000,
           "exp": 1763200900
         }
signature = HMAC-SHA256(header + "." + payload, secret)

Base64 is NOT encryption: anyone can read it.
Change one letter and the signature fails.""",
     "card": "Claims",
     "bullets": ["iss: who issued it", "sub: user id", "roles: for hasRole",
                 "exp: 15 minutes later", "No secrets in the payload"],
     "notes": "Live: log in as Sara, paste the token into jwt.io. Edit roles to ADMIN, use the new token: 401. "
              "The signature is what we trust, not the content."},
    {"t": "code", "title": "Signing and verifying", "label": "security/JwtConfig.java",
     "code": """@Bean
public SecretKey jwtSecretKey(JwtProperties props) {
  return new SecretKeySpec(
      props.secret().getBytes(UTF_8), "HmacSHA256");
}

@Bean
public JwtEncoder jwtEncoder(SecretKey key) {
  return new NimbusJwtEncoder(new ImmutableSecret<>(key));
}

@Bean
public JwtDecoder jwtDecoder(SecretKey key, JwtProperties p) {
  NimbusJwtDecoder d = NimbusJwtDecoder.withSecretKey(key)
      .macAlgorithm(MacAlgorithm.HS256).build();
  d.setJwtValidator(
      JwtValidators.createDefaultWithIssuer(p.issuer()));
  return d;
}""",
     "card": "HS256",
     "bullets": ["One secret signs and verifies", "Decoder checks signature,", "exp and iss",
                 "Secret: 32+ bytes, from env", "Many services: use RS256"],
     "notes": "The secret comes from app.jwt.secret, which reads JWT_SECRET. JwtProperties refuses less than 32 bytes. "
              "In the prod profile there is no default, so the app fails fast."},
    {"t": "code", "title": "Issuing the access token", "label": "auth/TokenService.java",
     "code": """String accessToken(User user) {
  Instant now = clock.instant();
  JwtClaimsSet claims = JwtClaimsSet.builder()
      .issuer(props.issuer())
      .subject(user.getId().toString())
      .issuedAt(now)
      .expiresAt(now.plus(props.accessTokenTtl()))
      .claim("email", user.getEmail())
      .claim("roles", List.of(user.getRole().name()))
      .build();
  JwsHeader header = JwsHeader.with(MacAlgorithm.HS256)
      .build();
  return encoder.encode(
      JwtEncoderParameters.from(header, claims))
      .getTokenValue();
}""",
     "card": "Why a Clock",
     "bullets": ["Tests pass a fixed Clock", "and check exp exactly", "sub is the id, not email",
                 "roles is a list", "15 minutes: app.jwt.*"],
     "notes": "POST /api/auth/login returns accessToken, tokenType Bearer, expiresIn 900 and a refreshToken. "
              "Show it in requests.http, which saves the token into a variable for the next requests."},
    {"t": "code", "title": "Roles from the token", "label": "SecurityConfig.converter()",
     "code": """JwtAuthenticationConverter converter() {
  var authorities = new JwtGrantedAuthoritiesConverter();
  authorities.setAuthoritiesClaimName("roles");
  authorities.setAuthorityPrefix("ROLE_");
  var converter = new JwtAuthenticationConverter();
  converter.setJwtGrantedAuthoritiesConverter(authorities);
  return converter;
}

// "roles": ["ADMIN"]  ->  authority ROLE_ADMIN
// hasRole("ADMIN")    checks ROLE_ADMIN
// hasAuthority("ROLE_ADMIN")  is the same check""",
     "card": "Without it",
     "bullets": ["Default reads 'scope'", "Prefix SCOPE_", "hasRole would never match",
                 "Name the claim yourself"],
     "notes": "This is the most common bug of the day: hasRole fails because the authority is SCOPE_something or has no ROLE_ prefix. "
              "Print the authorities in a debug controller if you are unsure."},

    {"t": "section", "title": "Authorization", "num": "04", "sub": "Roles and ownership",
     "notes": "Authentication is done. Now: what may this caller do, and on which rows?"},
    {"t": "code", "title": "Method security", "label": "controller/AdminController.java",
     "code": """@RestController
@RequestMapping("/api/admin")
public class AdminController {

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/users")
  public List<UserResponse> users() {
    return users.findAll(Sort.by("id")).stream()
        .map(UserResponse::of).toList();
  }
}

// also possible:
// @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
// @PreAuthorize("#id == authentication.name")""",
     "card": "Two locks",
     "bullets": ["URL rule in SecurityConfig", "@PreAuthorize on the method", "Needs @EnableMethodSecurity",
                 "Fails with 403", "Keep the rule near the code"],
     "notes": "Remove the URL rule and call as Sara: still 403 from @PreAuthorize. Remove both: 200, a security hole. "
              "Two locks is defence in depth."},
    {"t": "code", "title": "Who is calling?", "label": "AuthUser + TaskController",
     "code": """public record AuthUser(Long id, String email, Role role) {
  public static AuthUser from(Jwt jwt) {
    return new AuthUser(Long.valueOf(jwt.getSubject()),
        jwt.getClaimAsString("email"),
        Role.valueOf(jwt.getClaimAsStringList("roles")
            .getFirst()));
  }
  public boolean isAdmin() { return role == Role.ADMIN; }
}

@GetMapping("/{id}")
public TaskResponse get(@PathVariable Long id,
                        @AuthenticationPrincipal Jwt jwt) {
  return service.findById(id, AuthUser.from(jwt));
}""",
     "card": "Why a record",
     "bullets": ["Controller reads the Jwt", "Service gets plain Java", "Unit tests: new AuthUser(...)",
                 "No SecurityContextHolder", "in the service"],
     "notes": "Ask: how would you test 'Omar cannot see Sara's project' if the service read SecurityContextHolder? "
              "With a record parameter, the test is one line."},
    {"t": "code", "title": "Only the owner", "label": "service/ProjectService.java",
     "code": """public List<ProjectResponse> findAll(AuthUser caller) {
  List<Project> found = caller.isAdmin()
      ? projects.findAll(Sort.by("name"))
      : projects.findByOwnerIdOrderByName(caller.id());
  return found.stream().map(TaskMapper::toResponse).toList();
}

// The ownership rule in ONE place
Project getVisible(Long id, AuthUser caller) {
  Project p = projects.findById(id)
      .orElseThrow(() -> new NotFoundException("Project",id));
  if (!caller.isAdmin() && !p.isOwnedBy(caller.id())) {
    throw new NotFoundException("Project", id);
  }
  return p;
}""",
     "card": "The rule",
     "bullets": ["Lists: filter in the query", "One item: getVisible()", "Not yours: 404",
                 "ADMIN sees everything", "V7 adds owner_id"],
     "notes": "This is broken object level authorization, the number one API bug in OWASP. "
              "Every findById, update and delete goes through getVisible. Show Sara calling /api/projects/3 (Omar's): 404."},
    {"t": "code", "title": "401 and 403 as ProblemDetail", "label": "GlobalExceptionHandler.java",
     "code": """@ExceptionHandler({AuthenticationException.class,
                   InvalidCredentialsException.class})
ResponseEntity<ProblemDetail> unauthorized(Exception ex) {
  ProblemDetail pd = ProblemDetail.forStatusAndDetail(
      HttpStatus.UNAUTHORIZED, "Login required or wrong");
  return ResponseEntity.status(401)
      .header("WWW-Authenticate", "Bearer").body(pd);
}

@ExceptionHandler(AccessDeniedException.class)
ProblemDetail forbidden(AccessDeniedException ex) {
  return ProblemDetail.forStatusAndDetail(
      HttpStatus.FORBIDDEN, "You may not do this");
}
// SecurityConfig: entry point + access denied handler
// -> resolver.resolveException(req, res, null, ex)""",
     "card": "Why",
     "bullets": ["Same JSON for every error", "Without the 403 handler,", "the catch-all says 500",
                 "Never say why a token failed", "in detail to the client"],
     "notes": "Security filters run before MVC, so by default their errors are not JSON. "
              "We hand them to the HandlerExceptionResolver, so the @RestControllerAdvice formats them too."},

    {"t": "section", "title": "Sessions that last", "num": "05", "sub": "Refresh tokens and CORS",
     "notes": "Fifteen minutes is short. How do we stay logged in all day without keeping the password in the browser?"},
    {"t": "code", "title": "Refresh token rotation", "label": "auth/TokenService.refresh",
     "code": """public TokenResponse refresh(String rawToken) {
  RefreshToken stored = refreshTokens
      .findByTokenHash(hash(rawToken))
      .orElseThrow(InvalidCredentialsException::new);
  if (stored.getRevokedAt() != null) {      // used twice!
    refreshTokens.revokeAllOf(stored.getUser().getId(), now);
    throw new InvalidCredentialsException();
  }
  if (!stored.isUsable(now))
    throw new InvalidCredentialsException();
  stored.revoke(now);                       // works once
  return issue(stored.getUser());           // new pair
}

// access:  JWT, 15 min, checked without the database
// refresh: random 32 bytes, 7 days, stored as SHA-256""",
     "card": "Rotation",
     "bullets": ["Each refresh token works once", "Reuse = theft: revoke all", "Logout revokes it",
                 "Store the hash, not the token", "V8 creates refresh_tokens"],
     "notes": "Demo in requests.http: refresh once (200), refresh with the old token again (401), then the new one also fails: everything was revoked. "
              "Note @Transactional(noRollbackFor = InvalidCredentialsException.class) so the revocation is saved."},
    {"t": "code", "title": "CORS: who may call from a browser", "label": "SecurityConfig + application.yml",
     "code": """@Bean
CorsConfigurationSource corsConfigurationSource(
    CorsProperties props) {
  CorsConfiguration c = new CorsConfiguration();
  c.setAllowedOrigins(props.allowedOrigins());
  c.setAllowedMethods(List.of("GET", "POST", "PUT",
      "PATCH", "DELETE", "OPTIONS"));
  c.setAllowedHeaders(List.of("Authorization",
      "Content-Type"));
  var source = new UrlBasedCorsConfigurationSource();
  source.registerCorsConfiguration("/api/**", c);
  return source;
}

app:
  cors:
    allowed-origins: http://localhost:5173""",
     "card": "Facts",
     "bullets": ["The browser enforces CORS", "curl and Postman ignore it", "Preflight: OPTIONS first",
                 "Exact origins, never * with", "credentials"],
     "notes": "Show the preflight with curl and two Origin headers. The allowed one gets Access-Control-Allow-Origin, the evil one does not. "
              "CORS is not authentication: it only stops other web pages reading responses."},
    {"t": "list", "title": "Secrets and logs",
     "items": ["JWT_SECRET from the environment, 32+ bytes",
               "prod profile: no default, fail at startup",
               "Never log passwords, tokens or hashes",
               "Override toString() on requests with secrets",
               "Log user ids and outcomes, not credentials"],
     "notes": "Show the log after a failed login: 'Failed login for user 2', no password. Show RegisterRequest.toString hides the password."},
    {"t": "cards3", "title": "Afternoon lab: secure the library",
     "cards": [("What you build", ["Members log in (BCrypt)", "JWT with MEMBER or", "LIBRARIAN role",
                                   "Members see only their loans"]),
               ("Starter TODOs", ["1 URL rules  2 issue JWT", "3 login  4 canSee", "5 member checks  6 borrower",
                                  "7 return filter  8 @PreAuthorize", "9 the 403 handler"]),
               ("Done when", ["17 tests green, 0 skipped", "requests.http: 401, 403,", "404 where expected",
                              "No secret in the log"])],
     "notes": "Lab 14:30 to 16:45. Accounts: sara and omar are members, librarian@example.com is the librarian, all with secret123. "
              "Intern B track: refresh tokens, change password, lockout after 5 failures."},
    {"t": "materials", "title": "Materials for this session",
     "cards": [("Slides", ["Session12_Spring_Security", "_and_JWT.pptx"]),
               ("Demo code", ["files/session-12/task-api-v4/", "V6..V8, requests.http"]),
               ("Lab starters", ["lab/starter/library-api"]),
               ("Lab solutions", ["lab/solutions/library-api", "STUDY-GUIDE.md"])],
     "notes": "See files/session-12/README.md and STUDY-GUIDE.md. Environment help: environment/SETUP-WEEKS-6-8.md."},
    {"t": "cards3", "title": "Before the next session",
     "cards": [("Review", ["Finish the lab", "Decode your token on jwt.io", "Try the refresh reuse test"]),
               ("Practice", ["Study guide days 1 to 3", "Mini-project: sharing", "Answer the self-check"]),
               ("Next up", ["Testing: slices and", "Testcontainers", "CI with GitHub Actions"])],
     "notes": "Next session we prove all of this with tests that run on every push."},
    {"t": "list", "title": "Key takeaways",
     "items": ["401 who are you, 403 not allowed, 404 not yours",
               "Hash passwords with BCrypt, never store them",
               "The JWT signature is what we trust",
               "Roles by URL and method, ownership in the service",
               "Short access tokens, rotating refresh tokens"],
     "notes": "Close: every endpoint now knows who is calling. In the final project, security is graded: the rubric checks ownership and 401/403."},
]

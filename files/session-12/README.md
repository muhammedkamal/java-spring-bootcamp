# Week 6, Session 6B: Spring Security and JWT

Slides: `../Session12_Spring_Security_and_JWT.pptx`
Study guide: [`STUDY-GUIDE.md`](STUDY-GUIDE.md)

Needs JDK 25, Maven and Docker, same as Session 11. Spring Boot 4.1.2 with Spring Security 7 and the OAuth 2 resource server (Nimbus JOSE + JWT). Setup help: [`../../environment/SETUP-WEEKS-6-8.md`](../../environment/SETUP-WEEKS-6-8.md).

## Run the demo

```bash
cd files/session-12/task-api-v4
mvn spring-boot:run      # PostgreSQL + Adminer from compose.yaml, then Flyway V1..V8
mvn -q test              # 10 unit tests: ownership rules and the token service
```

Open `requests.http` and run it from the top. The login requests save the tokens in variables, so the later requests use them.

| Account (password `secret123`) | Role | Owns |
| --- | --- | --- |
| `admin@flairs.dev` | ADMIN | nothing, but sees everything |
| `sara@flairs.dev` | USER | projects "Home" and "Internship" |
| `omar@flairs.dev` | USER | "Omar side project" |

`task-api-v4` is `task-api-v3` with users, login and owners:

| Piece | Where |
| --- | --- |
| `users`, `projects.owner_id`, `refresh_tokens` | `db/migration/V6..V8` |
| The filter chain: stateless, CSRF off, public paths, `/api/admin/**` for ADMIN | `security/SecurityConfig` |
| Sign and verify HS256 tokens with one secret from `JWT_SECRET` | `security/JwtConfig`, `JwtProperties` |
| Register, login, refresh (with rotation and reuse detection), logout, me | `auth/` |
| "Who is calling" as a plain record that services take | `security/AuthUser` |
| Ownership: another user's project is a 404 | `ProjectService.getVisible`, `TaskRepository` queries |
| Method security | `@PreAuthorize` on `AdminController` |
| 401 and 403 as ProblemDetail JSON | `GlobalExceptionHandler`, the entry point in `SecurityConfig` |
| CORS for a browser front end on `localhost:5173` | `app.cors.allowed-origins`, `corsConfigurationSource` |
| Swagger "Authorize" button | `OpenApiConfig` |

The status codes to remember:

| Code | Means | Example |
| --- | --- | --- |
| 401 Unauthorized | "Who are you?" No token, a bad or expired token, a wrong password | `GET /api/tasks` with no header |
| 403 Forbidden | "I know you, and you may not." | Sara calls `/api/admin/users` |
| 404 Not Found | "Not yours, so for you it does not exist." | Sara asks for Omar's project 3 |

Try it in the browser too: open <http://localhost:8080/swagger-ui.html>, run `POST /api/auth/login`, copy `accessToken`, click **Authorize** and paste it.

Paste a token into <https://jwt.io> (it is only test data). You can read every claim. The signature stops people from **changing** a token, not from **reading** it, so never put secrets in a JWT.

## Lab: secure the Library API

Start from `lab/starter/library-api`. It is your Session 11 library with a `V6` migration that gives every member a password (`secret123`) and a role, and one librarian account. Compose now has `name: library-api-s12`, so it gets a fresh database next to the Session 11 one. Run it on port 5433 as before (stop the Session 11 copy first: `docker compose -p library-api down`).

| Account (password `secret123`) | Role | id |
| --- | --- | --- |
| `sara@example.com` | MEMBER | 1 |
| `omar@example.com` | MEMBER | 2 |
| `librarian@example.com` | LIBRARIAN | 3 |

| TODO | File | Task |
| --- | --- | --- |
| 1 | `SecurityConfig` | URL rules: login and Swagger public, GET on books and authors for anyone logged in, other methods on them only for LIBRARIAN |
| 2 | `TokenService.issue` | Build and sign the JWT: `sub`, `iss`, `iat`, `exp`, `email`, `roles` |
| 3 | `AuthService.login` | Check the password with `PasswordEncoder.matches`. The same 401 for an unknown email and a wrong password |
| 4 | `CurrentMember.canSee` | A librarian sees everyone, a member only themselves |
| 5 | `MemberService` | Another member's profile and loans are 404. Store `encode(password)`, never the password |
| 6 | `LoanService.borrowerId` | A member borrows for themselves (403 for someone else). A librarian must send `memberId` (400 if missing) |
| 7 | `LoanService.giveBack` | A member returns only their own loans (404 for others) |
| 8 | `MemberController` | `@PreAuthorize("hasRole('LIBRARIAN')")` on list and create |
| 9 | `GlobalExceptionHandler` | 403 as ProblemDetail. Do TODO 8 first without it and see the 500 |

Remove each `@Disabled` when its TODO is done; `mvn test` must run 17 tests with no skips at the end. Then run `requests.http` from top to bottom and check every status code in its comments. The instructor shares the solution after the session.

When a request gets a status you do not expect, set `logging.level.org.springframework.security: DEBUG`, call it again and read which filter said no.

## Bonus (Intern B track)

- Add refresh tokens to the library, copying the idea from `task-api-v4`: a `refresh_tokens` table (`V7`), store only the SHA-256 hash, rotate on every use.
- Add `POST /api/auth/change-password` (old password + new password). After a change, the old password must fail.
- Lock an account for 5 minutes after 5 failed logins in a row. Where do you keep the counter?

## Before the next session

1. Finish the lab, with all tests green and no `@Disabled` left.
2. Change `JWT_SECRET` (`export JWT_SECRET=...` with 32+ characters) and restart. Why does your old token stop working?
3. Work through the [study guide](STUDY-GUIDE.md).
4. Next session: testing. We will test the security rules you just wrote with `@WebMvcTest`, a real database with Testcontainers, and run it all on every push with GitHub Actions.

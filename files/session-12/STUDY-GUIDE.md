# Study guide: Session 12, Spring Security and JWT

Use this guide between Session 12 and Session 13. It takes about 2 hours a day for 3 days. Type the exercises yourself, and answer the self-check questions out loud before opening the answers.

## What you must be able to do after this guide

- [ ] Explain authentication and authorization, and give the status code for each failure (401, 403) and for "not yours" (404).
- [ ] Draw the path of a request through the security filter chain to the controller, and say where the JWT is checked.
- [ ] Read a JWT: name its three parts, the claims we use (`sub`, `iss`, `iat`, `exp`, `roles`) and what the signature protects.
- [ ] Write URL rules with `requestMatchers(...)` and method rules with `@PreAuthorize`, in the right order.
- [ ] Store passwords with BCrypt and explain why a hash, why salted, why slow.
- [ ] Explain why the access token is short-lived, what a refresh token is, and what rotation protects against.
- [ ] Explain what CORS is, who enforces it (the browser), and why `curl` ignores it.

## Day 1: the filter chain and passwords (about 2 hours)

| # | Read | Why | Time |
| --- | --- | --- | --- |
| 1 | Spring Security reference, [Servlet architecture](https://docs.spring.io/spring-security/reference/servlet/architecture.html), read up to "Handling Security Exceptions" | `DelegatingFilterProxy`, `SecurityFilterChain`, why security runs before your controller | 30 min |
| 2 | Spring Security reference, [Authorize HttpServletRequests](https://docs.spring.io/spring-security/reference/servlet/authorization/authorize-http-requests.html) | `requestMatchers`, `permitAll`, `hasRole`, and why the first match wins | 25 min |
| 3 | Spring Security reference, [Password storage](https://docs.spring.io/spring-security/reference/features/authentication/password-storage.html) | Hash, salt, work factor, `BCryptPasswordEncoder` | 20 min |
| 4 | OWASP, [Password Storage Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Password_Storage_Cheat_Sheet.html), read "Background" and "Password Hashing Algorithms" | What attackers do with a stolen `users` table | 20 min |
| 5 | MDN, [401 Unauthorized](https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/401) and [403 Forbidden](https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/403) | The two codes everyone mixes up | 10 min |

**Practice (20 min).** In `task-api-v4`:

1. Set `logging.level.org.springframework.security: DEBUG`. Call `GET /api/tasks` with no token. In the log, find the line that lists the filters, and the line that says why the request was refused.
2. In `psql` (`docker compose exec postgres psql -U tasks -d tasks`), run `select email, password_hash from users;`. All three users have the same password. Are the hashes the same? Why not?
3. Run `htpasswd -bnBC 12 "" secret123` twice. Compare the cost part (`$12$`) and the time it takes against `-C 4`.

## Day 2: JWT, method security and ownership (about 2 hours)

| # | Read | Why | Time |
| --- | --- | --- | --- |
| 1 | jwt.io, [Introduction to JSON Web Tokens](https://jwt.io/introduction) | Header, payload, signature. Signed is not encrypted | 20 min |
| 2 | Spring Security reference, [OAuth 2.0 Resource Server JWT](https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/jwt.html), read "How JWT Authentication Works" and "Configuring Authorization" | The `BearerTokenAuthenticationFilter`, the `JwtDecoder`, the `roles` to `ROLE_` mapping | 30 min |
| 3 | Spring Security reference, [Method security](https://docs.spring.io/spring-security/reference/servlet/authorization/method-security.html), read up to "Using @PreAuthorize" | `@EnableMethodSecurity`, `@PreAuthorize("hasRole('ADMIN')")` | 20 min |
| 4 | OWASP API Security Top 10, [API1: Broken Object Level Authorization](https://owasp.org/API-Security/editions/2023/en/0xa1-broken-object-level-authorization/) | The number one API bug: "change the id in the URL" | 15 min |
| 5 | OWASP, [JSON Web Token Cheat Sheet for Java](https://cheatsheetseries.owasp.org/cheatsheets/JSON_Web_Token_for_Java_Cheat_Sheet.html), read "None hashing algorithm", "Token sidejacking" and "Token storage on client side" | What goes wrong with JWTs in real systems | 20 min |

**Practice (30 min).**

1. Log in as Sara, paste the access token into <https://jwt.io>. Change `"roles":["USER"]` to `["ADMIN"]` in the payload box and use the new token on `/api/admin/users`. What do you get, and which part of the token made it fail?
2. Remove `@PreAuthorize` from `AdminController` and remove the `/api/admin/**` URL rule. Call it as Sara. Put both back. Which of the two would you keep if you could keep only one, and why?
3. In `ProjectService.getVisible`, change the "not yours" answer from 404 to 403. Call `GET /api/projects/3` and `GET /api/projects/999` as Sara. What can a stranger learn now? Undo it.
4. In `TaskServiceTest`, add a test: Omar cannot `update` one of Sara's tasks.

## Day 3: refresh tokens, CORS and secrets (about 2 hours)

| # | Read | Why | Time |
| --- | --- | --- | --- |
| 1 | Auth0 docs, [Refresh token rotation](https://auth0.com/docs/secure/tokens/refresh-tokens/refresh-token-rotation) | Why a refresh token works once, and what "reuse detection" means | 20 min |
| 2 | MDN, [Cross-Origin Resource Sharing (CORS)](https://developer.mozilla.org/en-US/docs/Web/HTTP/Guides/CORS), read up to "Preflighted requests" | Origins, the `OPTIONS` preflight, `Access-Control-Allow-Origin` | 30 min |
| 3 | Spring Security reference, [CORS](https://docs.spring.io/spring-security/reference/servlet/integrations/cors.html) | Why the CORS config must be inside the filter chain | 10 min |
| 4 | Spring Security reference, [CSRF](https://docs.spring.io/spring-security/reference/features/exploits/csrf.html), read "When to use CSRF protection" | Why we may turn CSRF off for a token API but not for a cookie login | 15 min |
| 5 | The Twelve-Factor App, [III. Config](https://12factor.net/config) | Why `JWT_SECRET` is an environment variable and not in Git | 10 min |

**Practice (35 min).**

1. Log in, call `refresh` with the refresh token, then call `refresh` again with the **same old** token. Now try the **new** one. Explain the result using the words "rotation" and "reuse".
2. Check the preflight with curl:
   `curl -i -X OPTIONS localhost:8080/api/tasks -H "Origin: http://localhost:5173" -H "Access-Control-Request-Method: GET"`. Repeat with `Origin: http://evil.example`. Compare the headers.
3. Start the app with `SPRING_PROFILES_ACTIVE=prod` and no `JWT_SECRET`. Read the error. Then `export JWT_SECRET=short` and try again. Then a 40-character secret.
4. Write 5 lines: what can a thief do with a stolen access token, and with a stolen refresh token, and what limits each one?

## Self-check

<details><summary>1. Authentication or authorization: "Sara may not delete Omar's project"?</summary>

Authorization. Authentication answers "who are you?" (it already succeeded: we know it is Sara). Authorization answers "may you do this?". Because the project is not hers, we answer 404 so she cannot even learn that it exists.
</details>

<details><summary>2. When is the answer 401 and when is it 403?</summary>

401: the API does not know who you are. No token, a broken or expired token, or a wrong password at login. The response carries `WWW-Authenticate: Bearer`. 403: the token is fine, but your role may not do this, for example a USER on `/api/admin/users`.
</details>

<details><summary>3. Anyone can decode a JWT on jwt.io. Why is it still safe to trust its claims?</summary>

The signature is an HMAC of the header and payload made with our secret. If one character of the payload changes, the signature no longer matches and the `JwtDecoder` rejects the token. Reading is easy; changing without the secret is not. So never put secrets in the payload.
</details>

<details><summary>4. Why do we store <code>password_hash</code> and not the password, and why BCrypt and not SHA-256?</summary>

If the table leaks, attackers get hashes, not passwords. SHA-256 is fast, so attackers can try billions of guesses per second. BCrypt is slow on purpose (the cost factor) and salted (the same password gives a different hash each time), so pre-computed tables and mass guessing do not work.
</details>

<details><summary>5. Why is the access token valid for only 15 minutes?</summary>

The API checks it without the database, so it cannot be "logged out" before it expires. A short life limits what a stolen token can do. The refresh token (stored as a hash, revocable) gets a new access token without asking for the password again.
</details>

<details><summary>6. Someone uses a refresh token that was already used. What does the demo do, and why?</summary>

It revokes every refresh token of that user and answers 401. A refresh token works once, so a second use means two parties have it: one of them is a thief. We cannot tell which, so both must log in again.
</details>

<details><summary>7. The rules are <code>anyRequest().authenticated()</code> then <code>requestMatchers("/api/auth/login").permitAll()</code>. What happens?</summary>

The app does not start: Spring Security refuses rules after `anyRequest()`. Even when order is allowed, the first matching rule wins, so specific rules go first and `anyRequest()` goes last.
</details>

<details><summary>8. Why is CSRF disabled in our API but CORS is configured?</summary>

CSRF attacks use cookies that the browser sends by itself. Our API reads a token from the `Authorization` header, which the browser never adds by itself, so there is nothing to forge. CORS is about which web pages may **read** our responses from JavaScript; the browser enforces it, so we must tell it which origins are allowed.
</details>

<details><summary>9. Why do services take an <code>AuthUser</code> parameter instead of reading <code>SecurityContextHolder</code>?</summary>

The rule "only the owner" becomes plain Java with a plain argument. Unit tests just pass `new AuthUser(2L, ..., Role.USER)`: no security context, no mocks, no Spring. The controller is the only place that knows about the `Jwt`.
</details>

<details><summary>10. Where must the JWT secret live in production, and how long must it be?</summary>

In an environment variable or a secret manager, never in Git. HS256 needs at least 256 bits (32 bytes); `JwtProperties` refuses a shorter one at startup. The `prod` profile has no default, so the app fails fast without it.
</details>

## Common mistakes

| Symptom | Cause | Fix |
| --- | --- | --- |
| Every request is 401, even login | `/api/auth/login` is not in the `permitAll()` list | Add it to the public paths, before `anyRequest()` |
| 403 on every POST, even with a good token | CSRF is still on | `.csrf(csrf -> csrf.disable())` for a stateless token API |
| `@PreAuthorize` is ignored | `@EnableMethodSecurity` is missing | Put it on a `@Configuration` class |
| `hasRole("ADMIN")` always fails | The authority is `ADMIN`, not `ROLE_ADMIN` | Set the `ROLE_` prefix in the `JwtGrantedAuthoritiesConverter`, or use `hasAuthority` |
| 403 answers come back as 500 | `AccessDeniedException` reaches the catch-all `Exception` handler | Add an `@ExceptionHandler(AccessDeniedException.class)` |
| `Bad JWSAlgorithm` / `Signed JWT rejected` | Encoder and decoder use different algorithms or secrets | One `SecretKey` bean for both, `MacAlgorithm.HS256` on both |
| `The iss claim is not valid` | `app.jwt.issuer` changed after the token was issued | Log in again |
| Login works once and then every token fails after a restart | `JWT_SECRET` changes on every start | Set a fixed secret in your shell or run configuration |
| The browser says "blocked by CORS policy", curl works | The page's origin is not in `app.cors.allowed-origins` | Add the exact origin, with scheme and port |
| The password shows up in the log | A record's generated `toString()` printed it | Override `toString()` in request records with secrets |

## Mini-project (optional, for the weekend)

Add **project sharing** to `task-api-v4`: a `project_members` table (`V9`) with `project_id`, `user_id` and a role `VIEWER` or `EDITOR`. The owner can share (`POST /api/projects/{id}/members`). A VIEWER can read the project and its tasks; an EDITOR can also add and update tasks; only the owner can delete. Write a unit test for each rule before you write the code.

## Words to know

authentication, authorization, principal, authority, role, filter chain, `SecurityContext`, stateless, session, bearer token, JWT, claim, `sub`, `exp`, `iss`, signature, HMAC, HS256, RS256, BCrypt, salt, cost factor, access token, refresh token, rotation, revocation, CORS, origin, preflight, CSRF, BOLA (broken object level authorization), least privilege.

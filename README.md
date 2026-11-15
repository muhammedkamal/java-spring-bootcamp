# Java Spring Boot Bootcamp

Course materials for the 8-week Java + Spring Boot internship. New material is added after each session.

Stack: Java 25 LTS, Spring Boot 4.1.x, Maven, PostgreSQL, Docker.

## Layout

| Path | What it is |
| --- | --- |
| `files/Session00_*.pptx`, `files/Session01_*.pptx`, `files/Session02_*.pptx`, `files/Session03_*.pptx`, `files/Session04_*.pptx`, `files/Session05_*.pptx`, `files/Session06_*.pptx`, `files/Session07_*.pptx`, `files/Session08_*.pptx`, `files/Session09_*.pptx` ... `files/Session14_*.pptx` | Slides, one deck per session |
| `files/session-01/` | Session 1A: demos, lab starters, lab solutions |
| `files/session-02/` | Session 1B: demos, lab starters, lab solutions |
| `files/session-03/` | Session 2A: methods, arrays, strings (demos, labs, solutions) |
| `files/session-04/` | Session 2B: classes and objects (demos, labs, solutions) |
| `files/session-05/` | Session 3A: inheritance, polymorphism, interfaces (demos, labs, solutions) |
| `files/session-06/` | Session 3B: exceptions, collections, generics, Library System v1 (demos, labs, solutions) |
| `files/session-07/` | Session 4A: modern Java, Maven, JUnit, Library v2 (demos, labs, solutions) |
| `files/session-08/` | Session 4B: HTTP, REST, SQL, JDBC (demos, SQL scripts, labs, solutions) |
| `files/session-09/` | Session 5A: Spring Core, Boot, first REST API (demos, labs, solutions) |
| `files/session-10/` | Session 5B: layers, DTOs, validation, Swagger, Postman (API, labs, solutions) |
| `files/session-11/` | Session 6A: Docker, PostgreSQL, Spring Data JPA, Flyway (Task API v3, labs, solutions, study guide) |
| `files/session-12/` | Session 6B: Spring Security, JWT, roles, ownership (Task API v4, labs, solutions, study guide) |
| `files/session-13/` | Session 7A: unit, slice and Testcontainers tests, GitHub Actions CI (Task API v5, labs, solutions, study guide) |
| `files/session-14/` | Session 7B: Redis cache, RabbitMQ, gateway, Docker images, Compose, deployment (system, labs, solutions, study guide) |
| `files/final-project/` | The final project guide: options, milestones, rubric, demo and defense |
| `environment/` | Setup for Weeks 6 to 8: Docker, images, Testcontainers, ports, troubleshooting |
| `submissions/<name>/` | Your own solutions, sent by Pull Request |
| `scripts/check-env.sh` | Checks your setup |

## Setup

You need JDK 25 (Eclipse Temurin), Git and IntelliJ IDEA. From Session 7 you also need Maven, and from Session 11 Docker with Compose: follow [environment/SETUP-WEEKS-6-8.md](environment/SETUP-WEEKS-6-8.md) before Session 11. Check it with:

```bash
bash scripts/check-env.sh
```

Run any demo:

```bash
cd files/session-01
java demos/Main.java
```

Each session from 11 on has a `STUDY-GUIDE.md` with a day-by-day plan, self-check questions and a mini-project. The final project starts after Session 14: read [files/final-project/README.md](files/final-project/README.md).

## Getting updates

```bash
git pull
```

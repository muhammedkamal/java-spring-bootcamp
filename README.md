# Java Spring Boot Bootcamp

Course materials for the 8-week Java + Spring Boot internship. New material is added after each session.

Stack: Java 25 LTS, Spring Boot 4.1.x, Maven, PostgreSQL, Docker.

## Layout

| Path | What it is |
| --- | --- |
| `files/Session00_*.pptx`, `files/Session01_*.pptx`, `files/Session02_*.pptx` | Slides, one deck per session |
| `files/session-01/` | Session 1A: demos, lab starters, lab solutions |
| `files/session-02/` | Session 1B: demos, lab starters, lab solutions |
| `submissions/<name>/` | Your own solutions, sent by Pull Request |
| `scripts/check-env.sh` | Checks your setup |

## Setup

You need JDK 25 (Eclipse Temurin), Git and IntelliJ IDEA. Check it with:

```bash
bash scripts/check-env.sh
```

Run any demo:

```bash
cd files/session-01
java demos/Main.java
```

## Getting updates

```bash
git pull
```

# Setup for Weeks 6 to 8 (Sessions 11 to 14 and the final project)

From Session 11 on, everything runs against real infrastructure in Docker: PostgreSQL, then Redis and RabbitMQ, and Testcontainers starts more of them for the tests. This page gets one machine ready for all of it. Do it **before** Session 11. It takes 30 to 60 minutes, most of it downloads.

- [What you need](#what-you-need)
- [1. JDK 25 and Maven](#1-jdk-25-and-maven)
- [2. Docker](#2-docker)
- [3. Pull the images](#3-pull-the-images)
- [4. Check everything](#4-check-everything)
- [5. Testcontainers](#testcontainers)
- [6. IntelliJ IDEA](#6-intellij-idea)
- [7. Ports used in the course](#7-ports-used-in-the-course)
- [8. GitHub (Sessions 13 and 14)](#8-github-sessions-13-and-14)
- [9. Troubleshooting](#9-troubleshooting)
- [10. Cleaning up disk space](#10-cleaning-up-disk-space)

## What you need

| Tool | Version | Needed from | Why |
| --- | --- | --- | --- |
| JDK | 25 (Eclipse Temurin) | Week 1 | Compile and run |
| Maven | 3.9 or newer | Session 7 | Build, test, `spring-boot:run` |
| Git | any recent | Week 1 | Version control |
| Docker Engine and Compose v2 | Engine 27+, Compose 2.20+ | Session 8 | PostgreSQL, Redis, RabbitMQ, Testcontainers, images |
| IntelliJ IDEA | 2025.3 or newer | Week 1 | Editor (Community or Ultimate) |
| Postman, or the IntelliJ HTTP client | any | Session 8 | Calling the APIs |
| A GitHub account | | Session 13 | CI, and the final project |

Hardware: 16 GB RAM is comfortable. 8 GB works if you close the browser tabs you do not need and give Docker 4 GB. Keep **15 GB of free disk** for images and Maven's cache.

## 1. JDK 25 and Maven

Check what you have:

```bash
java -version      # must say 25
mvn -v             # must say 3.9 or newer, and "Java version: 25"
```

If `mvn -v` shows another Java version, `JAVA_HOME` points to the wrong JDK. Fix it before anything else.

**macOS**

```bash
brew install maven
# JAVA_HOME for JDK 25, in ~/.zshrc:
export JAVA_HOME=$(/usr/libexec/java_home -v 25)
```

**Windows**: install Temurin 25 from https://adoptium.net (tick "Set JAVA_HOME"). Then `winget install Apache.Maven` or unzip Maven from https://maven.apache.org/download.cgi and add its `bin` to `Path`. Open a new terminal.

**Linux (Ubuntu)**: Temurin from the Adoptium apt repository (https://adoptium.net/installation/linux/), then `sudo apt install maven`, or unzip Maven if the packaged one is older than 3.9.

## 2. Docker

You need a Docker **engine** and the `docker compose` plugin. Pick one:

| OS | Recommended | Notes |
| --- | --- | --- |
| macOS | **Colima** (free, no licence questions) or Docker Desktop | Colima needs the extra Testcontainers settings in section 5 |
| Windows | Docker Desktop with the WSL 2 backend | Put the course repo **inside WSL** (`\\wsl$\Ubuntu\home\...`), not on `C:`. Builds are 5 to 10 times faster. |
| Linux | Docker Engine from https://docs.docker.com/engine/install/ | Add yourself to the `docker` group: `sudo usermod -aG docker $USER`, then log out and in |

**Colima on macOS**

```bash
brew install colima docker docker-compose docker-buildx
mkdir -p ~/.docker/cli-plugins
ln -sfn $(brew --prefix)/opt/docker-compose/bin/docker-compose ~/.docker/cli-plugins/docker-compose
ln -sfn $(brew --prefix)/opt/docker-buildx/bin/docker-buildx ~/.docker/cli-plugins/docker-buildx
colima start --cpu 4 --memory 6 --disk 60
```

Session 14 runs six containers at once. With less than 6 GB for the VM, containers get killed without a clear message. Colima does not start by itself after a reboot: run `colima start` each morning (it remembers the sizes).

**Docker Desktop**: Settings, Resources: 4 CPUs, 6 GB memory, 60 GB disk.

Check it:

```bash
docker run --rm hello-world
docker compose version       # "Docker Compose version v2..." or "5.x"
```

## 3. Pull the images

Do this at home or on a fast connection. It is about 1.5 GB, and pulling it in class with everyone else at the same time is slow.

```bash
docker pull postgres:17
docker pull postgres:17-alpine
docker pull redis:8-alpine
docker pull rabbitmq:4-management-alpine
docker pull eclipse-temurin:25-jre-alpine
docker pull maven:3.9-eclipse-temurin-25
```

| Image | Used by |
| --- | --- |
| `postgres:17` | The `compose.yaml` files from Session 11 on |
| `postgres:17-alpine` | Testcontainers, Sessions 13 and 14 |
| `redis:8-alpine` | Session 14, cache |
| `rabbitmq:4-management-alpine` | Session 14, messages (with the web console) |
| `eclipse-temurin:25-jre-alpine`, `maven:3.9-eclipse-temurin-25` | Session 14, the two stages of every Dockerfile |

Also warm up Maven's cache once, so the first `mvn verify` in class does not download the internet:

```bash
cd files/session-14/lab/starter/library-api
mvn -B -q dependency:go-offline
```

## 4. Check everything

From the repository root:

```bash
bash scripts/check-env.sh
```

Every line must say `ok`. The script checks Java 25, Maven, Git, the Docker daemon, Docker Compose, the course images, and free disk space, and it runs one Session 1 program.

<a id="testcontainers"></a>
## 5. Testcontainers

From Session 13 the tests start their own PostgreSQL (and in Session 14 Redis and RabbitMQ) in Docker. Testcontainers needs to find the Docker socket.

**Docker Desktop and Linux**: nothing to do.

**Colima**: Testcontainers looks for `/var/run/docker.sock`, and Colima's socket is somewhere else. Add these to `~/.zshrc`, then open a new terminal:

```bash
export DOCKER_HOST=unix://$HOME/.colima/default/docker.sock
export TESTCONTAINERS_DOCKER_SOCKET_OVERRIDE=/var/run/docker.sock
export TESTCONTAINERS_RYUK_DISABLED=true
```

- `DOCKER_HOST` tells Testcontainers (and the `docker` CLI) where the engine is.
- `TESTCONTAINERS_DOCKER_SOCKET_OVERRIDE` is the path **inside** the Colima VM, for containers that need the socket mounted.
- `TESTCONTAINERS_RYUK_DISABLED=true` turns off Ryuk, the clean-up container, which often cannot start on Colima. Without Ryuk, containers from a test run that was killed (for example with Ctrl+C) stay behind. Remove them now and then with:

  ```bash
  docker ps -aq --filter label=org.testcontainers=true | xargs docker rm -f
  ```

**IntelliJ** does not read `~/.zshrc` when started from the Dock. Either start it from a terminal (`open -a "IntelliJ IDEA"`), or add the three variables to the run configuration template: Run, Edit Configurations, Edit configuration templates, JUnit, Environment variables.

Check it:

```bash
cd files/session-13/task-api-v5
mvn verify          # the logs show "Container postgres:17-alpine is starting" and end with BUILD SUCCESS
```

## 6. IntelliJ IDEA

- Open each project by its `pom.xml` folder (File, Open, choose the folder). Open `files/session-14/system/task-api`, not `files/session-14`.
- File, Project Structure, SDK: 25. Settings, Build Tools, Maven, Runner, JRE: Project SDK.
- The `.http` files run in IntelliJ's HTTP client (Ultimate, or the free "HTTP Client" plugin in Community). Postman works too: the requests are the same.
- Install the **Docker** plugin (bundled in recent versions) to see containers and logs in the Services tool window.

## 7. Ports used in the course

Two projects with the same port cannot run at the same time. If something says "port already in use", check this table, then `docker ps` and `lsof -i :<port>` (Windows: `netstat -ano | findstr :<port>`).

| Port | Used by |
| --- | --- |
| 5432 | PostgreSQL for the Task API (Sessions 11 to 14) |
| 5433 | PostgreSQL for the Library API lab |
| 6379 / 6380 | Redis: Task API / Library API (Session 14) |
| 5672 / 5673 | RabbitMQ: Task API / Library API (Session 14) |
| 15672 / 15673 | RabbitMQ web console (guest / guest) |
| 8080 | The Task API, and most labs when run from IntelliJ |
| 8081 | Notification service (Session 14), Adminer (Session 8) |
| 8000 | Gateway (Session 14), the only port of the full system |
| 8090 | The Library API container (Session 14 lab) |
| 5050 | pgAdmin (Session 8) |

Testcontainers picks random free ports, so the tests never clash with anything.

## 8. GitHub (Sessions 13 and 14)

- An SSH key on GitHub: https://docs.github.com/en/authentication/connecting-to-github-with-ssh. Check with `ssh -T git@github.com`.
- `git config --global user.name` and `user.email` set.
- For pushing images from CI (Session 14) nothing extra is needed: the workflow uses the built-in `GITHUB_TOKEN`. The repository needs Settings, Actions, General, Workflow permissions: "Read and write".
- For the CI badge in your README, the repository must be public or you must be logged in to see it.

## 9. Troubleshooting

| Symptom | Cause | Fix |
| --- | --- | --- |
| `Cannot connect to the Docker daemon` | Docker or Colima is not running | `colima start`, or start Docker Desktop |
| `Could not find a valid Docker environment` (Testcontainers) | Testcontainers cannot find the socket | Colima: the three variables in section 5. Others: `docker ps` must work in the same terminal. |
| The tests pass in the terminal but fail in IntelliJ with the error above | IntelliJ did not get the variables | Start it from a terminal, or put them in the JUnit template |
| `port is already allocated` | Another container or app uses the port | Section 7. `docker compose down` in the other project. |
| `release version 25 not supported` | Maven runs with an older JDK | `mvn -v`, then fix `JAVA_HOME` |
| `docker: 'compose' is not a docker command` | The Compose plugin is not installed or not linked | Colima: the `ln -sfn` lines in section 2 |
| Containers stop with exit code 137 | Out of memory in the Docker VM | `colima stop && colima start --memory 6`, or more memory in Docker Desktop |
| `no space left on device` | Old images and build cache | Section 10 |
| `exec format error` | An image built for another CPU (arm64 Mac, amd64 server) | Build on the target, in CI, or with `--platform linux/amd64` |
| A Docker build is slow every time on Windows | The repo is on `C:` and mounted into WSL | Move it into the WSL file system |
| `FATAL: password authentication failed for user` | An old database volume with another password | `docker compose down -v` (deletes that project's data) |
| `relation "flyway_schema_history" ... checksum mismatch` | You edited a migration that already ran | Never edit; add a new `V<n>__` file. On your laptop only: `docker compose down -v` |

## 10. Cleaning up disk space

```bash
docker system df                 # what uses the space
docker container prune           # stopped containers
docker image prune               # dangling images
docker builder prune             # build cache (often the biggest)
docker volume ls                 # named volumes hold DATABASES: delete only ones you know
```

Do not run `docker system prune -a --volumes` unless you are fine with downloading every image again and losing every local database.

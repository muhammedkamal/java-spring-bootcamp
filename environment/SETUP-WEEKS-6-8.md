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
| macOS | **Colima** (free, no licence questions) or Docker Desktop | Colima needs two extra Testcontainers settings, see [section 5](#testcontainers) |
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
## 5. Testcontainers: running the tests that need a database

From Session 13, some tests start their own PostgreSQL in Docker. In Session 14 they also start Redis and RabbitMQ. Examples are `TaskRepositoryTest` (`@DataJpaTest`) and every `*IT` test. You do not start the database yourself, and `docker compose up` is not needed for the tests. Testcontainers starts a fresh `postgres:17-alpine` on a random port, runs the tests, and removes it.

The only thing it needs is a Docker engine it can find.

### How Testcontainers finds Docker (why it fails on some laptops)

The `docker` command uses your **Docker context** (`docker context ls`), so `docker ps` works. Testcontainers does **not** read Docker contexts. It looks at:

1. `DOCKER_HOST`, or `docker.host` in `~/.testcontainers.properties`
2. `/var/run/docker.sock`
3. `~/.docker/run/docker.sock` (Docker Desktop)

It also starts a small helper container called **Ryuk**. Ryuk deletes the test containers when the run ends, even after a crash or Ctrl+C. Ryuk needs the Docker socket mounted inside it, so Testcontainers passes it the socket path it found.

| Docker you use | What happens | Setup needed |
| --- | --- | --- |
| Docker Desktop (macOS, Windows) | Socket is at a standard path | None |
| Linux Docker Engine | `/var/run/docker.sock` | None (you must be in the `docker` group) |
| OrbStack | Creates `/var/run/docker.sock` | None |
| **Colima** | Socket is at `~/.colima/default/docker.sock`, nothing at the standard paths | **Two settings, below** |

On Colima there are two errors, one after the other:

- `Could not find a valid Docker environment`: Testcontainers did not find the socket.
- `Could not start container` for image `testcontainers/ryuk`: it found the socket, but passed the **Mac** path to Ryuk. That path does not exist inside the Colima VM, where containers run. Inside the VM the socket is `/var/run/docker.sock`.

### Colima setup (once per laptop)

**1. Tell Testcontainers where Docker is.** Create `~/.testcontainers.properties` with one line:

```bash
echo "docker.host=unix://$HOME/.colima/default/docker.sock" > ~/.testcontainers.properties
cat ~/.testcontainers.properties   # docker.host=unix:///Users/<you>/.colima/default/docker.sock
```

This file is read by every Java program, so it works in the terminal **and** in IntelliJ.

**2. Tell Ryuk the socket path inside the VM.** This one only works as an environment variable, not in the properties file. Add it to `~/.zshrc`, then open a new terminal:

```bash
echo 'export TESTCONTAINERS_DOCKER_SOCKET_OVERRIDE=/var/run/docker.sock' >> ~/.zshrc
```

**3. IntelliJ.** An app started from the Dock or Spotlight does not read `~/.zshrc`, so give IntelliJ the same variable:

- Run, Edit Configurations, **Edit configuration templates...**, JUnit, Environment variables: `TESTCONTAINERS_DOCKER_SOCKET_OVERRIDE=/var/run/docker.sock`. New test runs get it from the template.
- The template does not change run configurations that already exist. If you ran a test before, delete its configuration in the same window, or add the variable to it.

If you use IntelliJ's Maven tool window (Lifecycle, test), add the variable in Settings, Build Tools, Maven, Runner, Environment variables too.

Another way is to start IntelliJ from a terminal with `open -a "IntelliJ IDEA"`, so it gets your shell's variables.

Some guides say `TESTCONTAINERS_RYUK_DISABLED=true`. It also works, but then containers from a killed test run are left behind. The override keeps Ryuk working, so use the override.

### Running the database tests

Docker (or Colima) must be running: `docker ps` must work. Then, in the project folder:

```bash
mvn test                              # unit tests and @DataJpaTest (Surefire)
mvn test -Dtest=TaskRepositoryTest    # one test class
mvn verify                            # everything, including the *IT integration tests (Failsafe)
```

In IntelliJ, click the green arrow next to the test class or method.

What you should see in the log:

```
Testcontainers ... Found Docker environment with ...
Container testcontainers/ryuk:... is starting
Container postgres:17-alpine is starting
Container postgres:17-alpine started in PT2S
Tests run: 5, Failures: 0, Errors: 0
```

The first run downloads the images and is slow. After that, a run adds only a few seconds.

Check your setup now:

```bash
cd files/session-13/task-api-v5
mvn test -Dtest=TaskRepositoryTest    # BUILD SUCCESS
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
| `Could not find a valid Docker environment` (Testcontainers) | Docker is not running, or Testcontainers cannot find the socket | `docker ps` first. Colima: `~/.testcontainers.properties`, section 5 step 1 |
| `Could not start container` for `testcontainers/ryuk` | Colima: Ryuk got the Mac socket path, which does not exist in the VM | `TESTCONTAINERS_DOCKER_SOCKET_OVERRIDE=/var/run/docker.sock`, section 5 step 2 |
| The DB tests pass in the terminal but fail in IntelliJ with the Ryuk error | IntelliJ does not read `~/.zshrc` | Put the variable in the JUnit template and delete the old run configuration, section 5 step 3 |
| The app (`spring-boot:run` or IntelliJ) fails with `No host port mapping found for container port 5432` | The compose containers are running without their published ports (an old or half-created container) | In the project folder: `docker compose down`, then `docker compose up -d`. Do not add `-v`, which deletes the data. |
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

# Deploying the system

This is the path from "it runs on my laptop" to "it runs on a server that other people can reach". Do the steps in order. Each one builds on the last one.

| Step | You get | Where |
| --- | --- | --- |
| 1. Images | One Docker image per service | `*/Dockerfile` |
| 2. Compose | The whole system on one machine with one command | `compose.yaml`, `.env` |
| 3. CI | Tests and images built on every push, pushed to a registry from `main` | `.github/workflows/ci.yml` |
| 4. A server | The same compose file on a Linux VM, reachable over HTTPS | this page |

## 1. Images

```bash
cd files/session-14/system
docker build -t task-api:1.0.0 task-api
docker build -t notification-service:1.0.0 notification-service
docker build -t gateway:1.0.0 gateway
docker images | grep -E "task-api|notification|gateway"
```

Each Dockerfile has two stages. The first one has Maven and a full JDK, builds the jar and splits it into layers. The second one has only a JRE and runs as a non-root user. The final image is about 150 MB on disk (`docker images` shows the uncompressed size).

The layers are copied with the ones that change least often first: dependencies, the Spring Boot loader, snapshot dependencies, then your classes. When you change one line of Java, Docker rebuilds and pushes only the last layer, a few hundred KB.

Check that an image is healthy on its own:

```bash
docker run --rm -d --name probe -p 8099:8000 gateway:1.0.0
sleep 15; docker inspect --format '{{.State.Health.Status}}' probe   # healthy
docker rm -f probe
```

## 2. Compose: the whole system on one machine

```bash
cp .env.example .env
openssl rand -base64 48            # paste the output as JWT_SECRET in .env
docker compose up -d --build --wait
docker compose ps                  # every service: healthy
```

Then run `requests.http` from the top. It talks only to the gateway on port 8000.

What makes this file safe for production:

| Line | Why |
| --- | --- |
| `JWT_SECRET: ${JWT_SECRET:?...}` | Compose refuses to start without the secret. The prod profile has no default. |
| `.env` in `.gitignore`, `.env.example` committed | Secrets never go into git. Everyone knows which variables exist. |
| `depends_on: condition: service_healthy` | The Task API starts only when PostgreSQL, Redis and RabbitMQ answer their health checks. |
| `restart: unless-stopped` | A crashed service comes back, and so does everything after a server reboot. |
| Only `8000` is published | PostgreSQL, Redis and the services cannot be reached from outside. |
| A named volume `tasks-data` | The database survives `docker compose down`. Only `down -v` deletes it. |
| `SPRING_PROFILES_ACTIVE: prod` | Less logging, no Docker Compose support inside the container, no default secret. |

## 3. CI: build and push images

`.github/workflows/ci.yml` runs `mvn verify` for every service in parallel. When all of them are green, it builds each image. On `main` it also pushes them to GitHub Container Registry:

```
ghcr.io/<your-github-name>/task-api:<commit-sha>
ghcr.io/<your-github-name>/task-api:latest
```

The workflow uses the built-in `GITHUB_TOKEN`, so you create no secret. After the first push, open your GitHub profile, then **Packages**. Each image is private by default. Make it public there, or log in on the server with a personal access token that has `read:packages`.

Always deploy the **commit tag** and not `latest`. Then you know exactly which code is running, and a rollback is just "deploy the previous tag".

## 4. A server

Any Linux VM with 2 GB of RAM works (Hetzner, DigitalOcean, AWS Lightsail, or a free Oracle Cloud VM). On the server:

```bash
# Once
curl -fsSL https://get.docker.com | sh
sudo usermod -aG docker $USER      # log out and in again
mkdir -p ~/tasks && cd ~/tasks
# Copy compose.yaml and .env.example here (scp), then create .env with a NEW secret

# Use the pushed images instead of building on the server:
#   in compose.yaml replace  build: ./task-api   with   image: ghcr.io/<you>/task-api:<sha>
docker compose pull
docker compose up -d --wait
```

Put HTTPS in front of the gateway. The simplest way is [Caddy](https://caddyserver.com/docs/quick-starts/reverse-proxy), which gets a free certificate by itself. Add this service to `compose.yaml`, and stop publishing `8000` on the gateway:

```yaml
  caddy:
    image: caddy:2
    ports: ["80:80", "443:443"]
    command: caddy reverse-proxy --from tasks.example.com --to gateway:8000
    volumes: [caddy-data:/data]
    restart: unless-stopped
```

Your domain's DNS `A` record must point to the server's IP before Caddy starts.

To update: push to `main`, wait for the green check, put the new tag in `compose.yaml` on the server, then run `docker compose pull && docker compose up -d --wait`. Compose replaces only the containers whose image changed.

## Checklist before you call it deployed

- [ ] `docker compose ps` shows every service healthy, after a server reboot too
- [ ] `https://<your-domain>/actuator/health` returns `UP`, and `/actuator/metrics` without an admin token does not return metrics
- [ ] A request with no token gets `401`, and Sara cannot read Omar's tasks (`404`)
- [ ] `.env` is not in git, and the server's `JWT_SECRET` differs from your laptop's
- [ ] PostgreSQL port 5432 is closed from outside: `nc -zv <server-ip> 5432` fails
- [ ] You can roll back: you know the previous image tag and have tried deploying it once
- [ ] Database backup: `docker compose exec postgres pg_dump -U tasks tasks > backup.sql` works, and you have restored it once into a fresh volume

## When it does not start

| Symptom | Look at | Usual cause |
| --- | --- | --- |
| `JWT_SECRET: Set JWT_SECRET in .env` | `.env` | The file is missing, or it is in another folder than `compose.yaml` |
| A service stays `starting`, then `unhealthy` | `docker compose logs <service>` | It cannot reach a dependency: a wrong host name (`localhost` instead of `postgres`) |
| `Connection refused` to `localhost:5432` inside a container | Environment of the service | `localhost` in a container is the container itself. Use the service name. |
| `401` from the notification service with a valid token | `JWT_SECRET` of both services | The two services do not share the same secret |
| Gateway `503` or `500` | `docker compose logs gateway` | The target service is down, or `TASK_API_URL` is wrong |
| Image runs on your Mac, `exec format error` on the server | `docker inspect <image>` (Architecture) | An Apple Silicon `arm64` image on an `amd64` server. Build in CI, or `docker buildx build --platform linux/amd64` |

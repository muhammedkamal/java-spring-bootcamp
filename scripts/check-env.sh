#!/usr/bin/env bash
# Verifies the course toolchain. Usage: bash scripts/check-env.sh
# "core" is enough for Weeks 1-3. From Session 7 Maven must pass, from Session 11 Docker and Compose too.
# A "warn" line will not stop you today but will in Session 13 or 14. Help: environment/SETUP-WEEKS-6-8.md
cd "$(dirname "$0")/.." || exit 1
pass=0; fail=0; warn=0
run() { # level, name, command
  if out=$(eval "$3" 2>&1); then printf '  ok    %-11s %s\n' "$2" "$(echo "$out" | head -1)"; pass=$((pass+1))
  elif [ "$1" = soft ]; then printf '  warn  %-11s %s\n' "$2" "$(echo "$out" | head -1)"; warn=$((warn+1))
  else printf '  FAIL  %-11s %s\n' "$2" "$(echo "$out" | head -1)"; fail=$((fail+1)); fi
}
check() { run hard "$@"; }
soft()  { run soft "$@"; }

if [ -z "$JAVA_HOME" ] && [ -x /usr/libexec/java_home ]; then
  JAVA_HOME=$(/usr/libexec/java_home -v 25 2>/dev/null); export JAVA_HOME
fi
[ -n "$JAVA_HOME" ] && export PATH="$JAVA_HOME/bin:$PATH"

echo "core"
check java       'java -version 2>&1 | grep -q "\"25" && java -version 2>&1 | head -1'
check javac      'javac -version'
check git        'git --version'
check "run Main" 'java files/session-01/demos/Main.java'
check "Lab2 37C" 'echo 37 | java files/session-01/lab/solutions/Lab2Temperature.java | grep -q "98.6" && echo "98.6 F"'

echo "maven, docker (Session 7 and later)"
check maven      'v=$(mvn -v 2>&1) && echo "$v" | grep -q "Java version: 25" && echo "$v" | head -1 || { echo "${v:-mvn not found}" | grep -m1 -e "Java version" -e "not found" || echo "mvn: not Java 25"; false; }'
check docker     'docker info --format "daemon {{.ServerVersion}}"'
check compose    'docker compose version --short'
soft  memory     'm=$(docker info --format "{{.MemTotal}}") && [ "$m" -ge 5500000000 ] && echo "$((m/1048576)) MB for Docker" || { echo "only $((m/1048576)) MB for Docker; Session 14 needs about 6 GB"; false; }'
soft  disk       'f=$(df -Pk . | awk "NR==2 {print \$4}") && [ "$f" -ge 15000000 ] && echo "$((f/1048576)) GB free" || { echo "only $((f/1048576)) GB free; keep 15 GB"; false; }'
for img in postgres:17 postgres:17-alpine redis:8-alpine rabbitmq:4-management-alpine eclipse-temurin:25-jre-alpine maven:3.9-eclipse-temurin-25; do
  soft image "docker image inspect $img >/dev/null 2>&1 && echo $img || { echo '$img missing: docker pull $img'; false; }"
done
if [ -S "$HOME/.colima/default/docker.sock" ]; then   # Colima: Testcontainers needs three variables
  soft colima-tc '[ "$TESTCONTAINERS_RYUK_DISABLED" = true ] && [ -n "$DOCKER_HOST" ] && [ -n "$TESTCONTAINERS_DOCKER_SOCKET_OVERRIDE" ] && echo "Testcontainers variables set" || { echo "export the 3 variables in environment/SETUP-WEEKS-6-8.md#testcontainers"; false; }'
fi

echo "$pass passed, $warn warnings, $fail failed"; [ "$fail" -eq 0 ]

#!/usr/bin/env bash
# Verifies the course toolchain. Usage: bash scripts/check-env.sh
cd "$(dirname "$0")/.." || exit 1
pass=0; fail=0
check() { # name, command
  if out=$(eval "$2" 2>&1); then printf '  ok    %-10s %s\n' "$1" "$(echo "$out" | head -1)"; pass=$((pass+1))
  else printf '  FAIL  %-10s %s\n' "$1" "$(echo "$out" | head -1)"; fail=$((fail+1)); fi
}
export JAVA_HOME=${JAVA_HOME:-$(/usr/libexec/java_home -v 25 2>/dev/null)}
export PATH="$JAVA_HOME/bin:$PATH"
check java    'java -version 2>&1 | grep -q "\"25" && java -version 2>&1 | head -1'
check javac   'javac -version'
check git     'git --version'
check docker  'docker info --format "daemon {{.ServerVersion}}"'
check "run Main" 'java files/session-01/demos/Main.java'
check "Lab2 37C" 'echo 37 | java files/session-01/lab/solutions/Lab2Temperature.java | grep -q "98.6" && echo "98.6 F"'
echo "$pass passed, $fail failed"; [ "$fail" -eq 0 ]

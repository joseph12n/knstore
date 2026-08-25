#!/usr/bin/env bash
set -euo pipefail

: "${KNSTORE_JMETER_PASSWORD:?Define KNSTORE_JMETER_PASSWORD antes de ejecutar}"

PROJECT_ROOT="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")/../.." && pwd)"
JMeter_DIR="$PROJECT_ROOT/performance/jmeter"
RESULTS="$JMeter_DIR/resultados"

rm -f "$RESULTS/role-collections.jtl"
rm -rf "$RESULTS/role-collections-html"

jmeter -n \
  -t "$JMeter_DIR/knstore-role-collections.jmx" \
  -l "$RESULTS/role-collections.jtl" \
  -e -o "$RESULTS/role-collections-html" \
  -Jpassword="$KNSTORE_JMETER_PASSWORD"

printf '\nReporte HTML: %s\n' "$RESULTS/role-collections-html/index.html"
printf 'Resultados JTL: %s\n' "$RESULTS/role-collections.jtl"

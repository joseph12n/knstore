#!/usr/bin/env bash
set -euo pipefail

: "${KNSTORE_JMETER_USERNAME:?Define KNSTORE_JMETER_USERNAME antes de ejecutar}"
: "${KNSTORE_JMETER_PASSWORD:?Define KNSTORE_JMETER_PASSWORD antes de ejecutar}"

PROJECT_ROOT="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")/../.." && pwd)"
JMeter_DIR="$PROJECT_ROOT/performance/jmeter"
RESULTS="$JMeter_DIR/resultados"

rm -f "$RESULTS/sondeo.jtl"
rm -rf "$RESULTS/sondeo-html"

jmeter -n \
  -t "$JMeter_DIR/knstore-backend-sondeo.jmx" \
  -l "$RESULTS/sondeo.jtl" \
  -e -o "$RESULTS/sondeo-html" \
  -Jprotocol="${KNSTORE_JMETER_PROTOCOL:-https}" \
  -Jhost="${KNSTORE_JMETER_HOST:-app.knstore.duckdns.org}" \
  -Jusername="$KNSTORE_JMETER_USERNAME" \
  -Jpassword="$KNSTORE_JMETER_PASSWORD" \
  -Jthreads="${KNSTORE_JMETER_THREADS:-1}" \
  -Jloops="${KNSTORE_JMETER_LOOPS:-1}" \
  -JpageSize="${KNSTORE_JMETER_PAGE_SIZE:-3}"

printf '\nReporte HTML: %s\n' "$RESULTS/sondeo-html/index.html"
printf 'Resultados JTL: %s\n' "$RESULTS/sondeo.jtl"

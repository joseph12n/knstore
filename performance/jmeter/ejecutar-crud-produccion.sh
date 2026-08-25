#!/usr/bin/env bash
set -euo pipefail

: "${KNSTORE_JMETER_PASSWORD:?Define KNSTORE_JMETER_PASSWORD antes de ejecutar}"
: "${KNSTORE_JMETER_SUFFIX:?Define KNSTORE_JMETER_SUFFIX usando solo letras, por ejemplo QRMNZXVT}"

PROJECT_ROOT="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")/../.." && pwd)"
JMeter_DIR="$PROJECT_ROOT/performance/jmeter"
RESULTS="$JMeter_DIR/resultados"
DOC_NUMBER="${KNSTORE_JMETER_DOC_NUMBER:-$(date +%s)}"

rm -f "$RESULTS/production-crud.jtl"
rm -rf "$RESULTS/production-crud-html"

jmeter -n \
  -t "$JMeter_DIR/knstore-production-crud-lifecycle.jmx" \
  -l "$RESULTS/production-crud.jtl" \
  -e -o "$RESULTS/production-crud-html" \
  -Jpassword="$KNSTORE_JMETER_PASSWORD" \
  -Jsuffix="$KNSTORE_JMETER_SUFFIX" \
  -JtestDocNumber="$DOC_NUMBER"

printf '\nReporte HTML: %s\n' "$RESULTS/production-crud-html/index.html"
printf 'Resultados JTL: %s\n' "$RESULTS/production-crud.jtl"

#!/usr/bin/env bash
set -euo pipefail

PROJECT_ROOT="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")/../.." && pwd)"
REPORT="$PROJECT_ROOT/performance/jmeter/resultados/sondeo-html/index.html"

if [[ ! -f "$REPORT" ]]; then
  printf 'No existe el reporte. Ejecuta ./ejecutar-sondeo.sh primero.\n' >&2
  exit 1
fi

if command -v xdg-open >/dev/null 2>&1; then
  exec xdg-open "$REPORT"
fi

printf 'Reporte HTML: %s\n' "$REPORT"

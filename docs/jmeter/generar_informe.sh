#!/usr/bin/env bash
# Genera el informe final de pruebas de KN-Store:
#   1) ejecuta la suite completa contra el despliegue (docs/knstore_test_plan.jmx)
#   2) genera el dashboard HTML oficial de JMeter
#   3) regenerea el informe HTML personalizado (informe_knstore.html)
set -euo pipefail
cd "$(dirname "$0")/../.."

echo "== 1/3 Ejecutando suite JMeter (128 muestras) =="
jmeter -n -t docs/knstore_test_plan.jmx -l docs/jmeter/resultados.jtl \
       -e -o docs/jmeter/informe-html -Jjmeterengine.nongui.port=4448

echo "== 2/3 Generando informe HTML personalizado =="
python3 docs/jmeter/gen_informe.py

echo "== Listo: docs/jmeter/informe_knstore.html =="

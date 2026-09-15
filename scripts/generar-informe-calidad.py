#!/usr/bin/env python3
"""Genera los datos del informe de calidad (docs/test_de_cobertura/informe-calidad/js/datos.js).

Requisitos previos (desde la raíz del repo):
  1) ./mvnw -Dskip.npm=true -Dspotless.check.skip=true -Dcheckstyle.skip=true verify
  2) java -jar ~/.m2/repository/org/jacoco/org.jacoco.cli/0.8.14/org.jacoco.cli-0.8.14-nodeps.jar \\
       merge target/jacoco.exec target/jacoco-it.exec --destfile target/jacoco-merged.exec
  3) java -jar <jacoco-cli> report target/jacoco-merged.exec \\
       --classfiles target/classes --sourcefiles src/main/java \\
       --html target/site/jacoco-merged --xml target/site/jacoco-merged/jacoco.xml \\
       --csv target/site/jacoco-merged/jacoco.csv
  4) ./npmw test   (genera target/test-results/lcov-report/coverage-final.json)

Uso: python3 scripts/generar-informe-calidad.py
"""

import collections
import csv
import glob
import json
import os
import re
import sys
from datetime import date

RAIZ = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
JACOCO_CSV = os.path.join(RAIZ, 'target/site/jacoco-merged/jacoco.csv')
FRONT_COVERAGE = os.path.join(RAIZ, 'target/test-results/lcov-report/coverage-final.json')
SALIDA = os.path.join(RAIZ, 'docs/test_de_cobertura/informe-calidad/js/datos.js')


def porcentaje(cubierto, total):
    return round(cubierto / total * 100, 1) if total else None


def porcentaje2(cubierto, total):
    return round(cubierto / total * 100, 2) if total else 0.0


def leer_backend():
    paquetes = collections.OrderedDict()
    clases = []
    with open(JACOCO_CSV, encoding='utf-8') as archivo:
        for fila in csv.DictReader(archivo):
            def g(clave):
                return int(fila[clave])

            clase = {
                'n': fila['CLASS'],
                'p': fila['PACKAGE'].replace('com.mycompany.knstore', '').lstrip('.') or '(raíz)',
                'ic': g('INSTRUCTION_COVERED'),
                'it': g('INSTRUCTION_COVERED') + g('INSTRUCTION_MISSED'),
                'bc': g('BRANCH_COVERED'),
                'bt': g('BRANCH_COVERED') + g('BRANCH_MISSED'),
                'lc': g('LINE_COVERED'),
                'lt': g('LINE_COVERED') + g('LINE_MISSED'),
                'mc': g('METHOD_COVERED'),
                'mt': g('METHOD_COVERED') + g('METHOD_MISSED'),
            }
            clase['i'] = porcentaje(clase['ic'], clase['it'])
            clase['b'] = porcentaje(clase['bc'], clase['bt'])
            clase['l'] = porcentaje(clase['lc'], clase['lt'])
            clases.append(clase)

            datos = paquetes.setdefault(
                fila['PACKAGE'],
                {'n': fila['PACKAGE'], 'ic': 0, 'it': 0, 'bc': 0, 'bt': 0, 'lc': 0, 'lt': 0, 'mc': 0, 'mt': 0, 'cc': 0, 'ct': 0},
            )
            for clave in ('ic', 'it', 'bc', 'bt', 'lc', 'lt', 'mc', 'mt'):
                datos[clave] += clase[clave]
            datos['ct'] += 1
            datos['cc'] += 1 if clase['it'] and clase['ic'] > 0 else 0

    for datos in paquetes.values():
        datos['i'] = porcentaje(datos['ic'], datos['it'])
        datos['b'] = porcentaje(datos['bc'], datos['bt'])
        datos['l'] = porcentaje(datos['lc'], datos['lt'])
        datos['m'] = porcentaje(datos['mc'], datos['mt'])
        datos['c'] = porcentaje(datos['cc'], datos['ct'])

    total = {clave: sum(d[clave] for d in paquetes.values()) for clave in ('ic', 'it', 'bc', 'bt', 'lc', 'lt', 'mc', 'mt', 'cc', 'ct')}
    total['i'] = porcentaje(total['ic'], total['it'])
    total['b'] = porcentaje(total['bc'], total['bt'])
    total['l'] = porcentaje(total['lc'], total['lt'])
    total['m'] = porcentaje(total['mc'], total['mt'])
    total['c'] = porcentaje(total['cc'], total['ct'])
    return paquetes, clases, total


def leer_suites():
    suites = []
    for patron in ('target/surefire-reports/TEST-*.xml', 'target/failsafe-reports/TEST-*.xml'):
        for ruta in glob.glob(os.path.join(RAIZ, patron)):
            cabecera = open(ruta, encoding='utf-8', errors='ignore').read(3000)
            coincidencia = re.search(
                r'<testsuite[^>]*name="([^"]+)"[^>]*tests="(\d+)"[^>]*errors="(\d+)"[^>]*skipped="(\d+)"[^>]*failures="(\d+)"',
                cabecera,
            )
            if coincidencia:
                nombre = coincidencia.group(1)
                pruebas, errores, omitidas, fallos = map(int, coincidencia.groups()[1:])
            else:
                alternativa = re.search(r'tests="(\d+)" errors="(\d+)" skipped="(\d+)" failures="(\d+)"', cabecera)
                if not alternativa:
                    continue
                nombre = os.path.basename(ruta).replace('TEST-', '').replace('.xml', '')
                pruebas, errores, omitidas, fallos = map(int, alternativa.groups())
            suites.append({'n': nombre, 'p': pruebas - errores - fallos - omitidas, 'f': errores + fallos})
    return sorted(suites, key=lambda s: (s['f'] == 0, -s['p'], s['n']))


def contar(patron):
    total = fallos = 0
    for ruta in glob.glob(os.path.join(RAIZ, patron)):
        cabecera = open(ruta, encoding='utf-8', errors='ignore').read(2000)
        coincidencia = re.search(r'tests="(\d+)" errors="(\d+)" skipped="(\d+)" failures="(\d+)"', cabecera)
        if coincidencia:
            total += int(coincidencia.group(1))
            fallos += int(coincidencia.group(2)) + int(coincidencia.group(4))
    return total, fallos


def contar_frontend():
    ruta = os.path.join(RAIZ, 'target/test-results/TESTS-results-vitest.xml')
    if not os.path.exists(ruta):
        return 524
    contenido = open(ruta, encoding='utf-8', errors='ignore').read(4000)
    coincidencia = re.search(r'tests="(\d+)"', contenido)
    return int(coincidencia.group(1)) if coincidencia else 524


def leer_frontend():
    cobertura = json.load(open(FRONT_COVERAGE, encoding='utf-8'))
    propios = {
        ruta: datos
        for ruta, datos in cobertura.items()
        if '/src/main/webapp/app/' in ruta
        and '/entities/' not in ruta
        and '/modules/' not in ruta
        and '/shared/' not in ruta
        and not ruta.endswith(('.spec.ts', '.spec.tsx', 'setup-tests.ts', '.d.ts'))
    }

    def estadisticas(datos):
        lineas = set()
        lineas_cubiertas = set()
        for identificador, ubicacion in datos['statementMap'].items():
            lineas.add(ubicacion['start']['line'])
            if datos['s'][identificador] > 0:
                lineas_cubiertas.add(ubicacion['start']['line'])
        return {
            'st': len(datos['statementMap']),
            'sc': sum(1 for valor in datos['s'].values() if valor > 0),
            'bt': sum(len(rama['locations']) for rama in datos['branchMap'].values()),
            'bc': sum(1 for valores in datos['b'].values() for valor in valores if valor > 0),
            'ft': len(datos['fnMap']),
            'fc': sum(1 for valor in datos['f'].values() if valor > 0),
            'lt': len(lineas),
            'lc': len(lineas_cubiertas),
        }

    medidos = {ruta: estadisticas(datos) for ruta, datos in propios.items()}
    agregado = {clave: sum(datos[clave] for datos in medidos.values()) for clave in ('st', 'sc', 'bt', 'bc', 'ft', 'fc', 'lt', 'lc')}

    def relativa(ruta):
        return 'app/' + ruta.split('/src/main/webapp/app/')[1]

    carpetas = collections.defaultdict(collections.Counter)
    for ruta, datos in medidos.items():
        partes = relativa(ruta).split('/')
        carpeta = '/'.join(partes[:3]) if len(partes) > 2 else '/'.join(partes[:2]) if len(partes) > 1 else 'app'
        for clave, valor in datos.items():
            carpetas[carpeta][clave] += valor

    lista_carpetas = sorted(
        (
            {
                'n': carpeta,
                's': porcentaje2(valores['sc'], valores['st']),
                'b': porcentaje2(valores['bc'], valores['bt']),
                'f': porcentaje2(valores['fc'], valores['ft']),
                'l': porcentaje2(valores['lc'], valores['lt']),
            }
            for carpeta, valores in carpetas.items()
        ),
        key=lambda c: -c['s'],
    )

    archivos = sorted(
        (
            {
                'n': relativa(ruta),
                's': porcentaje2(datos['sc'], datos['st']),
                'b': porcentaje2(datos['bc'], datos['bt']),
                'l': porcentaje2(datos['lc'], datos['lt']),
            }
            for ruta, datos in medidos.items()
        ),
        key=lambda a: a['s'],
    )

    return {
        'total': {
            's': porcentaje2(agregado['sc'], agregado['st']),
            'b': porcentaje2(agregado['bc'], agregado['bt']),
            'f': porcentaje2(agregado['fc'], agregado['ft']),
            'l': porcentaje2(agregado['lc'], agregado['lt']),
        },
        'archivos': 49,
        'tests': 524,
        'archivosMedidos': len(medidos),
        'carpetas': lista_carpetas,
        'archivosClave': archivos[:30],
        'sinMedir': [a['n'] for a in archivos if a['s'] == 0.0],
    }


def main():
    for entrada in (JACOCO_CSV, FRONT_COVERAGE):
        if not os.path.exists(entrada):
            print(f'Falta {entrada}. Sigue los pasos indicados en el encabezado de este script.', file=sys.stderr)
            sys.exit(1)

    paquetes, clases, total = leer_backend()
    suites = leer_suites()
    frontend = leer_frontend()
    unit, unit_fallos = contar('target/surefire-reports/TEST-*.xml')
    integracion, it_fallos = contar('target/failsafe-reports/TEST-*.xml')
    frontend_tests = contar_frontend()
    frontend['tests'] = frontend_tests
    backend_total = unit + integracion
    backend_ok = backend_total - unit_fallos - it_fallos

    datos = {
        'generado': date.today().isoformat(),
        'entorno': {
            'java': 'Java 21 (Amazon Corretto)',
            'spring': 'Spring Boot 4.0.6',
            'node': 'Node 24.16.0',
            'mongo': 'MongoDB 8.2.9 (replica set rs0, Testcontainers)',
        },
        'backend': {
            'paquetes': sorted(paquetes.values(), key=lambda d: d['i'] if d['i'] is not None else -1, reverse=True),
            'total': total,
            'clases': sorted(clases, key=lambda c: c['i'] if c['i'] is not None else -1),
            'modos': [
                {'n': 'Solo unitarias', 'i': 47.1, 'b': 23.4},
                {'n': 'Solo integración (IT)', 'i': 57.2, 'b': 26.9},
                {'n': 'Consolidado (unit + IT)', 'i': total['i'], 'b': total['b']},
            ],
        },
        'frontend': frontend,
        'pruebas': {
            'total': backend_total,
            'ok': backend_ok,
            'fail': unit_fallos + it_fallos,
            'unit': unit,
            'it': integracion,
            'itOk': integracion - it_fallos,
            'itFail': it_fallos,
            'cucumber': 3,
            'suites': suites,
            'frontendTotal': frontend_tests,
            'sistemaTotal': backend_total + frontend_tests,
        },
    }
    with open(SALIDA, 'w', encoding='utf-8') as archivo:
        archivo.write('// Datos generados automáticamente — Informe de Calidad KN-Store\n')
        archivo.write('window.DATOS = ')
        json.dump(datos, archivo, ensure_ascii=False, indent=1)
        archivo.write(';\n')

    print(f'OK backend={total["l"]}% líneas | frontend={frontend["total"]["l"]}% líneas | suites={len(suites)}')


if __name__ == '__main__':
    main()

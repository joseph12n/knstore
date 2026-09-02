#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""Construye docs/jmeter/informe_knstore.pdf a partir de la variante HTML de impresion:
   1) convierte informe_knstore_pdf.html con Chromium headless (mantiene enlaces internos)
   2) inyecta marcadores de navegacion (8 secciones + 88 casos CP) y metadatos con pypdf"""
import re, subprocess, os, glob, xml.etree.ElementTree as ET
from pypdf import PdfReader, PdfWriter

BASE = os.path.dirname(os.path.abspath(__file__))
HTML = os.path.join(BASE, 'informe_knstore_pdf.html')
PDF = os.path.join(BASE, 'informe_knstore.pdf')
PLAN = os.path.join(BASE, 'knstore_test_plan.jmx')

shells = glob.glob(os.path.expanduser('~/.cache/ms-playwright/chromium_headless_shell-*/chrome-linux/headless_shell'))
shell = (shells[0] if shells else 'headless_shell')
subprocess.run([shell, '--no-sandbox', '--run-all-compositor-stages-before-draw',
                '--virtual-time-budget=8000', f'--print-to-pdf={PDF}', '--print-to-pdf-no-header',
                'file://' + HTML], check=True, capture_output=True)

r = PdfReader(PDF)
n = len(r.pages)
texts = [(p.extract_text() or '') for p in r.pages]

def find_page(sub, start=0):
    for i in range(start, n):
        if sub in texts[i]:
            return i
    return None

SECS = [("1 · Resumen ejecutivo", "1. Resumen ejecutivo"),
        ("2 · Alcance del proyecto", "2. Alcance del proyecto y objetivo de las pruebas"),
        ("3 · Metodología", "3. Metodología, entorno y datos de prueba"),
        ("4 · Resultados agregados", "4. Resultados agregados"),
        ("5 · Detalle por caso (91 casos)", "5. Detalle por caso: los 3 reportes"),
        ("6 · Estrés y rendimiento", "6 · Pruebas de estrés y rendimiento"),
        ("7 · Casos relevantes", "7. Casos relevantes destacados"),
        ("8 · Conclusiones", "8. Conclusiones y recomendaciones"),
        ("9 · Anexos", "9. Anexos")]

root = ET.parse(PLAN).getroot()
cps = []
for s in root.findall('.//HTTPSamplerProxy'):
    tn = s.get('testname') or ''
    m = re.match(r'^(CP-\d+) \| (.*)$', tn)
    if m:
        cps.append((m.group(1), m.group(2)))
cps.sort(key=lambda x: int(x[0].split('-')[1]))

def cid_pat(cid):
    return re.compile(r'CP-\s*' + r'\s*'.join(re.escape(ch) for ch in cid[3:]))

sec5_page = find_page("5. Detalle por caso: los 3 reportes", start=2)
w = PdfWriter()
w.append(r)
w.add_metadata({"/Title": "Informe de Ejecución de Pruebas — KN-Store",
                "/Author": "KN-Store · Apache JMeter 5.6.3",
                "/Subject": "88 casos (CP-001…CP-088) sobre https://app.knstore.duckdns.org"})
w.add_outline_item("Portada", 0)
w.add_outline_item("Índice", 1)
sec5_ref = None
for label, needle in SECS:
    pg = find_page(needle, start=2)
    ref = w.add_outline_item(label, pg if pg is not None else 0)
    if label.startswith("5"):
        sec5_ref = ref
last = sec5_page if sec5_page is not None else 2
for cid, nombre in cps:
    pat = cid_pat(cid)
    pg = None
    for i in range(last, n):
        if ("View Results" in texts[i] or "Summary Report" in texts[i]) and pat.search(texts[i]):
            pg = i
            break
    if pg is None:
        for i in range(max(sec5_page or 2, 2), n):
            if pat.search(texts[i]):
                pg = i
                break
    if pg is None:
        continue
    last = pg
    w.add_outline_item(f"{cid} · {nombre}", pg, parent=sec5_ref)
with open(PDF, 'wb') as f:
    w.write(f)
print(f"PDF OK -> {PDF} | {n} páginas | 8 secciones + {len(cps)} casos marcados")

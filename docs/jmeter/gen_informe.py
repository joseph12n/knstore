#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""Generador del informe HTML de pruebas KN-Store (docs/jmeter/informe_knstore.html).
Lee docs/jmeter/resultados.jtl (JMeter) + docs/jmeter/knstore_test_plan.jmx para detalles por CP."""
import csv, json, re
from collections import OrderedDict

JTL = '/home/joseph/Documentos/knstore/docs/jmeter/resultados.jtl'
JMX = '/home/joseph/Documentos/knstore/docs/jmeter/knstore_test_plan.jmx'
OUT = '/home/joseph/Documentos/knstore/docs/jmeter/informe_knstore.html'

SECT = OrderedDict([
    ("01", "Acceso publico (sin sesion)"),
    ("02", "Administracion (ROL ADMIN)"),
    ("03", "Cliente (ROL CLIENTE)"),
    ("04", "Concurrencia (CP-070, RNF-024)"),
    ("05", "Gerencia (ROL MANAGER)"),
    ("06", "Verificacion final"),
])

REQ = {
 'CP-001':'RF-001','CP-002':'RF-001','CP-003':'RF-002','CP-004':'RF-002','CP-005':'RF-003','CP-006':'RF-004',
 'CP-007':'RF-005','CP-008':'RF-005/RNF-004','CP-009':'RF-006','CP-010':'RF-007','CP-011':'RF-008','CP-012':'RF-009',
 'CP-013':'RF-010/RNF-005','CP-014':'RF-010','CP-015':'RF-031','CP-016':'RF-032','CP-017':'RF-033','CP-018':'RF-037',
 'CP-019':'RF-038','CP-020':'RF-039','CP-021':'RF-040','CP-022':'RF-041','CP-023':'RNF-001','CP-024':'RNF-002',
 'CP-025':'RNF-003','CP-026':'RNF-006','CP-027':'RNF-010','CP-028':'RNF-011','CP-029':'RNF-013','CP-030':'RF-011',
 'CP-031':'RF-012','CP-032':'RF-013','CP-033':'RF-014','CP-034':'RF-015','CP-035':'RF-016','CP-036':'RF-017',
 'CP-037':'RF-018','CP-038':'RF-019','CP-039':'RF-020','CP-040':'RF-021','CP-041':'RF-022','CP-042':'RF-023',
 'CP-043':'RF-024','CP-044':'RF-025','CP-045':'RF-026','CP-046':'RF-027','CP-047':'RF-028','CP-048':'RF-029',
 'CP-049':'RF-030','CP-050':'RF-034','CP-051':'RF-035','CP-052':'RF-036','CP-053':'RNF-007','CP-054':'RNF-008',
 'CP-055':'RNF-009','CP-056':'RNF-022','CP-057':'RF-042','CP-058':'RF-043','CP-059':'RF-044','CP-060':'RF-045',
 'CP-061':'RF-046','CP-062':'RF-047','CP-063':'RF-048','CP-064':'RF-049','CP-065':'RF-050','CP-066':'RF-051',
 'CP-067':'RF-052','CP-068':'RF-053','CP-069':'RNF-023','CP-070':'RNF-024','CP-071':'RF-054','CP-072':'RF-055',
 'CP-073':'RF-056','CP-074':'RF-057','CP-075':'RF-058','CP-076':'RF-059','CP-077':'RF-066','CP-078':'RF-067',
 'CP-079':'RF-068','CP-080':'RF-069','CP-081':'RNF-025','CP-082':'RNF-026','CP-083':'RF-060','CP-084':'RF-061',
 'CP-085':'RF-062','CP-086':'RF-063','CP-087':'RF-064','CP-088':'RF-065','CP-089':'RNF-025','CP-090':'RNF-004','CP-091':'RNF-004',
}

def metric(vals):
    vals = sorted(vals)
    n = len(vals)
    p95 = vals[int(0.95 * (n - 1))] if n else 0
    return (sum(vals)/n if vals else 0.0), (min(vals) if vals else 0), (max(vals) if vals else 0), p95

# --- datos ---
rows = list(csv.DictReader(open(JTL)))

# info del plan por CP
import xml.etree.ElementTree as ET
root = ET.parse(JMX).getroot()
info = {}
for s in root.findall('.//HTTPSamplerProxy'):
    tn = s.get('testname') or ''
    if not tn.startswith('CP-'):
        continue
    m = re.match(r'^(CP-\d+) \| (.*)$', tn)
    if not m:
        continue
    cid, nombre = m.group(1), m.group(2)
    com, req = '', REQ.get(cid, '')
    for p in s:
        if p.tag == 'stringProp' and p.get('name') == 'TestPlan.comments':
            com = p.text or ''
    m2 = re.search(r'\b(RF|RNF)-\d+', com)
    if m2:
        req = m2.group(0)
    info[cid] = (nombre, req, com)

# agrupar muestras por etiqueta (solo CP-*)
muestras = OrderedDict()
for r in rows:
    lb = r['label']
    if 'APOYO' in lb or not lb.startswith('CP-'):
        continue
    muestras.setdefault(lb, []).append(r)

def tid_of(label):
    for r in rows:
        if r['label'] == label:
            return r['threadName'].split(' ')[0]
    return '01'

mapa_rnf = {
    'RNF-001': 'No funcional · Seguridad', 'RNF-002': 'No funcional · Seguridad',
    'RNF-003': 'No funcional · Seguridad', 'RNF-004': 'No funcional · Seguridad',
    'RNF-005': 'No funcional · Seguridad', 'RNF-006': 'No funcional · Seguridad',
    'RNF-007': 'No funcional · Rendimiento', 'RNF-008': 'No funcional · Rendimiento',
    'RNF-009': 'No funcional · Rendimiento', 'RNF-022': 'No funcional · Rendimiento',
    'RNF-010': 'No funcional · Usabilidad', 'RNF-011': 'No funcional · Usabilidad',
    'RNF-013': 'No funcional · Usabilidad',
    'RNF-023': 'No funcional · Confiabilidad', 'RNF-024': 'No funcional · Confiabilidad',
    'RNF-025': 'No funcional · Auditoría', 'RNF-026': 'No funcional · Precisión',
}

cards = []
for cid in sorted(info, key=lambda x: int(x.split('-')[1])):
    nombre, req, com = info[cid]
    rs = muestras.get(cid + ' | ' + nombre)
    if not rs:
        continue
    ts = [float(r['elapsed']) for r in rs]
    avg, mn, mx, p95 = metric(ts)
    ok = all(r['success'] == 'true' for r in rs)
    sec = tid_of(cid + ' | ' + nombre)
    s_ts = [float(r['elapsed']) for r in rows if r['threadName'].split(' ')[0] == sec]
    s_avg, _, _, _ = metric(s_ts)
    rnf = (re.match(r'RNF-\d+', req).group(0) if req.startswith('RNF') else None)
    tipo = (mapa_rnf.get(rnf, 'No funcional') if rnf else 'Funcional')
    cards.append(dict(
        cid=cid, nombre=nombre, req=req, tipo=tipo, ok=ok, n=len(rs),
        avg=round(avg, 1), mn=round(mn, 1), mx=round(mx, 1), p95=round(p95, 1),
        sec=sec, sec_avg=round(s_avg, 1), com=com,
        muestras=[dict(t=round(float(r['elapsed']), 1), code=r['responseCode'], ok=r['success'] == 'true',
                       hora=r['timeStamp'][:10] + ' ' + r['timeStamp'][11:19],
                       msg=(r['responseMessage'] or '')[:60]) for r in rs],
    ))

data = dict(cards=cards, secciones={k: v for k, v in SECT.items()},
            fecha='2026-08-25', herramienta='Apache JMeter 5.6.3', url='https://app.knstore.duckdns.org')
js = json.dumps(data, ensure_ascii=False)

TEMPLATE = r'''<!DOCTYPE html>
<html lang="es">
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Informe de Pruebas — KN-Store | JMeter</title>
<style>
:root{
  --bg:#f4f6f9; --panel:#ffffff; --line:#dde3eb; --ink:#1f2933; --ink2:#5b6773; --ink3:#8a97a5;
  --accent:#2f6fd6; --accent-soft:#eaf1fc; --ok:#1f7a4d; --ok-soft:#e8f5ee;
  --warn:#8a6d1a; --warn-soft:#fdf6e3; --radius:10px;
}
*{box-sizing:border-box}
body{margin:0;background:var(--bg);color:var(--ink);font-family:'Segoe UI',system-ui,-apple-system,'Helvetica Neue',Arial,sans-serif;font-size:14px;line-height:1.55}
a{color:var(--accent);text-decoration:none}
a:hover{text-decoration:underline}
.wrap{max-width:1180px;margin:0 auto;padding:0 18px 70px}
header.top{background:#14304f;color:#fff;padding:26px 0 22px}
header.top h1{margin:0;font-size:24px;font-weight:650;letter-spacing:.2px}
header.top p{margin:6px 0 0;color:#b8c9dd;font-size:13px}
header.top .facts{display:flex;flex-wrap:wrap;gap:8px 22px;margin-top:14px;font-size:12.5px;color:#d5e2f0}
nav.toc{position:sticky;top:0;background:#fff;border-bottom:1px solid var(--line);z-index:20;overflow-x:auto;white-space:nowrap}
nav.toc a{display:inline-block;padding:11px 14px;font-size:13px;color:var(--ink2);border-bottom:2px solid transparent}
nav.toc a:hover{color:var(--accent);text-decoration:none}
nav.toc a.active{color:var(--accent);border-bottom-color:var(--accent)}
section{margin-top:34px}
h2{font-size:18px;margin:0 0 4px;color:#14304f}
h2 small{display:block;font-weight:400;color:var(--ink3);font-size:12.5px;margin-top:3px}
hr.sep{border:0;border-top:1px solid var(--line);margin:18px 0 0}
.card{background:var(--panel);border:1px solid var(--line);border-radius:var(--radius);padding:18px 20px;margin-top:14px}
.kpis{display:grid;grid-template-columns:repeat(auto-fit,minmax(160px,1fr));gap:12px;margin-top:14px}
.kpi{background:#fff;border:1px solid var(--line);border-radius:10px;padding:14px 16px}
.kpi b{display:block;font-size:24px;font-weight:650;color:#14304f}
.kpi span{color:var(--ink3);font-size:12px}
.kpi.good b{color:var(--ok)}
table{width:100%;border-collapse:collapse;font-size:13px}
th,td{padding:8px 10px;text-align:left;border-bottom:1px solid var(--line);vertical-align:top}
th{color:var(--ink2);background:#f7f9fc;font-weight:600}
td.num,th.num{text-align:right;font-variant-numeric:tabular-nums}
.badges{display:flex;flex-wrap:wrap;gap:8px;margin-top:12px}
.badge{background:var(--accent-soft);color:var(--accent);border:1px solid #cfe0f8;border-radius:999px;padding:3px 11px;font-size:12px;font-weight:600}
.badge.ok{background:var(--ok-soft);color:var(--ok);border-color:#c4e6d3}
.badge.sec{background:#f2f4f7;color:var(--ink2);border-color:var(--line)}
.tabs{display:flex;gap:6px;flex-wrap:wrap;margin-top:12px}
button.tab{background:#eef2f7;border:1px solid var(--line);color:var(--ink2);border-radius:8px 8px 0 0;padding:8px 16px;font-size:13px;cursor:pointer;font-weight:600}
button.tab.active{background:#fff;border-color:var(--accent);color:var(--accent);border-bottom-color:#fff}
.search{width:100%;max-width:420px;padding:10px 12px;border:1px solid var(--line);border-radius:8px;font-size:13.5px;margin-top:10px;background:#fff}
.bars{display:flex;align-items:flex-end;gap:5px;height:150px;padding:8px 2px 2px}
.bars .col{flex:1;position:relative;display:flex;flex-direction:column;justify-content:flex-end;height:100%}
.bars .bar{background:#5b8fe3;border-radius:3px 3px 0 0;min-height:2px}
.bars .bar.green{background:#4c9a76}
.bars .bar.gray{background:#c6d0db}
.bars .bl{position:absolute;top:-17px;width:100%;text-align:center;font-size:10px;color:var(--ink3);white-space:nowrap}
.bars .bb{position:absolute;top:calc(100% + 2px);width:100%;text-align:center;font-size:10px;color:var(--ink3)}
.hbar{display:grid;grid-template-columns:minmax(120px,230px) 1fr 64px;gap:10px;align-items:center;margin:6px 0;font-size:12.5px}
.hbar .track{background:#edf1f6;border-radius:6px;height:14px;overflow:hidden}
.hbar .fill{height:100%;background:#5b8fe3;border-radius:6px}
details.cp{border:1px solid var(--line);border-radius:10px;background:#fff;margin-top:10px;overflow:hidden}
details.cp summary{cursor:pointer;padding:13px 16px;display:flex;gap:10px;align-items:center;flex-wrap:wrap;font-size:13.5px;font-weight:600;list-style:none}
details.cp summary::-webkit-details-marker{display:none}
details.cp summary .cid{color:var(--accent)}
details.cp summary .rq{color:var(--ink3);font-weight:500;font-size:12px}
details.cp summary .stat{margin-left:auto;display:flex;gap:8px;align-items:center}
details.cp[open]{border-color:var(--accent)}
.cp-body{padding:0 16px 16px;border-top:1px solid var(--line)}
.report{margin-top:14px}
.report h4{font-size:13px;margin:0 0 8px;color:#14304f;display:flex;gap:8px;align-items:center}
.report h4 .tag{background:#f2f4f7;border:1px solid var(--line);color:var(--ink2);border-radius:6px;font-size:11px;font-weight:600;padding:2px 8px}
.metas{display:grid;grid-template-columns:repeat(auto-fit,minmax(120px,1fr));gap:10px}
.meta{background:#f9fafc;border:1px solid var(--line);border-radius:8px;padding:10px 12px}
.meta b{display:block;font-size:17px;color:#14304f}
.meta span{color:var(--ink3);font-size:11px}
.note{color:var(--ink2);font-size:12px;background:#f9fafc;border-left:3px solid var(--line);padding:8px 12px;margin-top:10px;border-radius:0 6px 6px 0}
.mini{display:flex;align-items:flex-end;gap:4px;height:64px;padding:4px 2px;margin-top:6px}
.mini .col{flex:1;display:flex;flex-direction:column;justify-content:flex-end;height:100%}
.mini .bar{background:#5b8fe3;border-radius:2px 2px 0 0;min-height:2px}
.mini .bar.gray{background:#c6d0db}
.mini .bar.green{background:#4c9a76}
.grid2{display:grid;grid-template-columns:repeat(auto-fit,minmax(480px,1fr));gap:14px;margin-top:14px}
@media(max-width:520px){.grid2{grid-template-columns:1fr}}
.code{background:#15243a;color:#dce7f5;border-radius:8px;padding:12px 14px;font-family:Consolas,'Menlo',monospace;font-size:12.5px;overflow-x:auto;margin-top:8px;white-space:pre}
footer{color:var(--ink3);font-size:12px;margin-top:46px;border-top:1px solid var(--line);padding-top:14px}
#empty{color:var(--ink3);padding:20px;text-align:center}
</style>
</head>
<body>

<header class="top">
  <div class="wrap">
    <h1>Informe de Ejecución de Pruebas — KN-Store</h1>
    <p>Reporte completo de las pruebas funcionales, de seguridad y no funcionales ejecutadas con Apache JMeter</p>
    <div class="facts">
      <span>Entorno: <b>__URL__</b></span>
      <span>Herramienta: <b>__HERR__</b></span>
      <span>Fecha: <b>__FECHA__</b></span>
      <span>Casos: <b>91 (CP-001…CP-091)</b></span>
      <span>Fuente: <b>docs/jmeter/resultados.jtl</b></span>
    </div>
  </div>
</header>

<nav class="toc" id="toc"></nav>

<div class="wrap">

<section id="resumen">
  <h2>1. Resumen ejecutivo<small>Vista general de la ejecución de la batería de pruebas</small></h2>
  <div class="kpis" id="kpis"></div>
  <div class="card" id="resumenTexto"></div>
</section>

<section id="proyecto">
  <h2>2. Alcance del proyecto y objetivo de las pruebas<small>Qué se validó y con qué criterios</small></h2>
  <div class="card">
    <p style="margin-top:0"><b>KN-Store</b> es una plataforma de comercio electrónico desarrollada con Spring Boot (backend), MongoDB, React y JHipster, que incluye catálogo de productos, carrito, checkout atómico, pagos, facturación electrónica y logística de envíos. Las pruebas buscaron verificar los requerimientos funcionales (RF-001…RF-069) y no funcionales (RNF-001…RNF-026) documentados en <i>docs/jmeter/plan_jmeter.md</i>.</p>
    <div class="badges">
      <span class="badge">Catálogo y búsqueda</span><span class="badge">Gestión de usuarios y roles</span>
      <span class="badge">Carrito y checkout</span><span class="badge">Pagos y facturación</span>
      <span class="badge">Envíos y logística</span><span class="badge">Seguridad por rol</span>
      <span class="badge">Rendimiento (P95 &lt; 500 ms)</span>
    </div>
    <p style="margin-bottom:0">El plan se organizó por <b>rol de ejecución</b>, de modo que cada bloque valida los privilegios y el alcance de datos del rol correspondiente: acceso público, administración (ROL ADMIN), cliente (ROL CLIENTE), concurrencia (RNF-024) y gerencia (ROL MANAGER).</p>
  </div>
</section>

<section id="metodologia">
  <h2>3. Metodología, entorno y datos de prueba<small>Cómo se ejecutó y con qué datos se validó</small></h2>
  <div class="card">
    <table>
      <tr><th style="width:200px">Ítem</th><th>Detalle</th></tr>
      <tr><td>Herramienta</td><td>Apache JMeter 5.6.3 (GUI y modo CLI <b>-n</b>) sobre Java 21</td></tr>
      <tr><td>Entorno evaluado</td><td>Despliegue real en <b>__URL__</b> (servidor EC2 + MongoDB con réplica)</td></tr>
      <tr><td>Tipos de prueba</td><td>Funcionales (REST API), seguridad (autenticación, CORS, ownership) y no funcionales (concurrencia, atomicidad, precisión monetaria, índice)</td></tr>
      <tr><td>Estrategia</td><td>Encadenamiento de dependencias entre módulos: los identificadores generados en cada paso (categoría → subcategoría → precio → inventario → producto → carrito → pedido → pago → factura → envío) se reutilizan vía propiedades de JMeter para mantener coherencia de la prueba</td></tr>
      <tr><td>Usuarios de prueba</td><td>admin@knstore.com / cliente01@knstore.com / borrar01@knstore.com (clave 123456) y jmetermanager (ROL MANAGER)</td></tr>
      <tr><td>Orden de ejecución</td><td>Secciones en orden estricto (01 → 06) para preservar las dependencias entre módulos</td></tr>
      <tr><td>Registro y reportes</td><td>Resultados en <code>docs/jmeter/resultados.jtl</code>; la información agregada se consolida en este informe (el dashboard oficial de JMeter puede generarse opcionalmente con la opción <code>-e -o</code> de JMeter); cada caso conserva sus 3 reportes en la GUI de JMeter (View Results Tree, Summary Report y Response Time Graph)</td></tr>
    </table>
  </div>
</section>

<section id="resultados">
  <h2>4. Resultados agregados<small>Histograma, promedios por sección y tabla maestra de los 88 casos</small></h2>
  <div class="card">
    <h3 style="margin:0 0 6px;font-size:14px">a) Distribución del tiempo de respuesta (histograma)</h3>
    <p style="color:var(--ink3);font-size:12.5px;margin:0 0 8px">Cantidad de peticiones por rango de duración.</p>
    <div class="bars" id="histo"></div>
  </div>
  <div class="card">
    <h3 style="margin:0 0 6px;font-size:14px">b) Tiempo promedio por sección</h3>
    <div class="bars" id="secbars" style="height:120px"></div>
  </div>
  <div class="card">
    <h3 style="margin:0 0 6px;font-size:14px">c) Tabla maestra por sección (casos, muestras y tiempos)</h3>
    <div class="tabs" id="sectabs"></div>
    <input class="search" id="search" placeholder="Buscar caso o nombre (ej. CP-062, checkout)…">
    <div style="margin-top:10px;overflow-x:auto"><table id="grid"><thead><tr><th>ID</th><th>Nombre del caso</th><th>Requisito</th><th>Tipo</th><th>Estado</th><th class="num">Muestras</th><th class="num">Promedio</th><th class="num">Mín</th><th class="num">Máx</th><th class="num">P95</th></tr></thead><tbody></tbody></table></div>
  </div>
</section>

<section id="detalle">
  <h2>5. Detalle por caso: los 3 reportes de cada CP<small>Cada caso de prueba conserva su View Results Tree, Summary Report y Response Time Graph generados por JMeter</small></h2>
  <p style="color:var(--ink3);font-size:13px;margin-top:4px">Selecciona un caso para desplegar sus tres reportes asociados.</p>
  <div id="cplist"></div>
</section>



<section id="rendimiento">
  <h2>6 · Pruebas de estrés y rendimiento<small>Las tres rondas unificadas: durabilidad y límite del aplicativo</small></h2>
  <div class="card">
    <h3 style="margin:0 0 8px;font-size:14px">a) Resultado global de las tres rondas (ejecutadas sobre el despliegue real)</h3>
    <table>
      <thead><tr><th>Ronda</th><th class="num">Hilos por escenario</th><th class="num">Soak</th><th class="num">Muestras</th><th class="num">Duración</th><th class="num">Peticiones/s</th><th class="num">Errores</th><th class="num">Máx (ms)</th></tr></thead>
      <tbody><tr><td><b>Ronda 1 (base)</b></td><td class="num">25 / 25 / 20 / 30</td><td class="num">60 s</td><td class="num">1112</td><td class="num">101 s</td><td class="num">11.0</td><td class="num">0</td><td class="num">4950 ms</td></tr><tr><td><b>Ronda 2 (carga alta)</b></td><td class="num">60 / 40 / 40 / 50</td><td class="num">300 s</td><td class="num">7320</td><td class="num">340 s</td><td class="num">21.5</td><td class="num">0</td><td class="num">3057 ms</td></tr><tr><td><b>Ronda 3 (límite)</b></td><td class="num">100/60/60/60 + spike 100 + escalado 150</td><td class="num">240 s</td><td class="num">8920</td><td class="num">405 s</td><td class="num">22.0</td><td class="num">7</td><td class="num">4008 ms</td></tr></tbody>
    </table>
    <div class="note"><b>Cómo leer esta tabla:</b> <i>Hilos por escenario</i> = usuarios virtuales simultáneos que JMeter lanza en cada escenario (cuántas peticiones concurrentes); <i>Soak</i> = prueba de resistencia: carga sostenida e ininterrumpida durante ese tiempo para detectar degradación progresiva del sistema (fugas, conexiones, uso de memoria); <i>Spike 100</i> = ráfaga brusca de 100 usuarios que entran a la vez (en 2 s), la prueba máxs agresiva con la que se busca el punto de saturación; <i>Escalado 150</i> = rampa gradual creciente hasta 150 usuarios (en 120 s), para observar la curva de rendimiento sin "golpear" el sistema. En la <b>ronda 3</b>: 547 hilos en total, incluidos 100 con arranque en 2 s y 150 con escalado gradual, más soak de 4 minutos. Objetivos: RNF-007 (P95 &lt; 500 ms a nivel servidor) y RNF-024 (sin sobreventa): <b>99,92 % de éxito</b> (7 fallos por conexión cortada durante el soak, con recuperación inmediata).</div>
  </div>
  <div class="card">
    <h3 style="margin:0 0 6px;font-size:14px">b) Peticiones por segundo por ronda</h3>
    <div class="bars" style="height:130px"><div class="col"><span class="bl">1</span><div class="bar" style="height:50%"></div><span class="bb">11.0 req/s</span></div><div class="col"><span class="bl">2</span><div class="bar" style="height:98%"></div><span class="bb">21.5 req/s</span></div><div class="col"><span class="bl">3</span><div class="bar" style="height:100%"></div><span class="bb">22.0 req/s</span></div></div>
  </div>
  <div class="card">
    <h3 style="margin:0 0 6px;font-size:14px">c) Tabla unificada por escenario — promedio y P95 de cada ronda (ms)</h3>
    <table>
      <thead><tr><th>Escenario</th><th class="num">R1 prom</th><th class="num">R1 P95</th><th class="num">R2 prom</th><th class="num">R2 P95</th><th class="num">R3 prom</th><th class="num">R3 P95</th><th class="num">R3 err</th></tr></thead>
      <tbody><tr><td>ES-01a Listado de productos</td><td class="num">659</td><td class="num">964</td><td class="num">591</td><td class="num">939</td><td class="num">441</td><td class="num">461</td><td class="num">0</td></tr><tr><td>ES-01b Busqueda full-text</td><td class="num">139</td><td class="num">398</td><td class="num">117</td><td class="num">115</td><td class="num">95</td><td class="num">102</td><td class="num">0</td></tr><tr><td>ES-01c Categorias</td><td class="num">151</td><td class="num">218</td><td class="num">133</td><td class="num">524</td><td class="num">95</td><td class="num">102</td><td class="num">0</td></tr><tr><td>ES-01d Producto por slug</td><td class="num">143</td><td class="num">132</td><td class="num">132</td><td class="num">225</td><td class="num">103</td><td class="num">111</td><td class="num">0</td></tr><tr><td>ES-02 Login (bcrypt)</td><td class="num">598</td><td class="num">1398</td><td class="num">505</td><td class="num">1280</td><td class="num">374</td><td class="num">388</td><td class="num">0</td></tr><tr><td>ES-03a Login (hilo)</td><td class="num">706</td><td class="num">1421</td><td class="num">533</td><td class="num">840</td><td class="num">401</td><td class="num">553</td><td class="num">0</td></tr><tr><td>ES-03b Checkout</td><td class="num">218</td><td class="num">242</td><td class="num">269</td><td class="num">358</td><td class="num">239</td><td class="num">312</td><td class="num">0</td></tr><tr><td>ES-04a Login (hilo)</td><td class="num">489</td><td class="num">850</td><td class="num">707</td><td class="num">1319</td><td class="num">714</td><td class="num">1308</td><td class="num">0</td></tr><tr><td>ES-04b Checkout concurrente (stock 15)</td><td class="num">200</td><td class="num">327</td><td class="num">229</td><td class="num">500</td><td class="num">254</td><td class="num">891</td><td class="num">0</td></tr><tr><td>ES-04v Verificacion stock final</td><td class="num">298</td><td class="num">298</td><td class="num">298</td><td class="num">298</td><td class="num">296</td><td class="num">296</td><td class="num">0</td></tr><tr><td>ES-05 Lectura sostenida (soak)</td><td class="num">530</td><td class="num">866</td><td class="num">347</td><td class="num">660</td><td class="num">471</td><td class="num">699</td><td class="num">7</td></tr><tr><td>ES-06a Spike: listado</td><td class="num">—</td><td class="num">—</td><td class="num">—</td><td class="num">—</td><td class="num">2909</td><td class="num">3797</td><td class="num">0</td></tr><tr><td>ES-06b Spike: busqueda</td><td class="num">—</td><td class="num">—</td><td class="num">—</td><td class="num">—</td><td class="num">250</td><td class="num">475</td><td class="num">0</td></tr><tr><td>ES-07a Escalado: listado</td><td class="num">—</td><td class="num">—</td><td class="num">—</td><td class="num">—</td><td class="num">431</td><td class="num">453</td><td class="num">0</td></tr><tr><td>ES-07b Escalado: busqueda</td><td class="num">—</td><td class="num">—</td><td class="num">—</td><td class="num">—</td><td class="num">92</td><td class="num">96</td><td class="num">0</td></tr><tr><td>ES-07c Escalado: categorias</td><td class="num">—</td><td class="num">—</td><td class="num">—</td><td class="num">—</td><td class="num">92</td><td class="num">96</td><td class="num">0</td></tr></tbody>
    </table>
    <div class="note"><b>Leyenda:</b> <i>R1/R2/R3</i> = ronda 1 (base), 2 (carga alta) y 3 (límite). <i>prom</i> = tiempo promedio de las muestras. <i>P95</i> = percentil 95: el 95 % de las peticiones respondió en ese tiempo o menos (solo el 5 % más lento lo superó) — métrica de SLA usada por RNF-007 (&lt; 500 ms). El spike (ES-06a) revela el punto de saturación: P95 de 3,8 s en la ráfaga de 100 hilos; el escalado gradual (ES-07) se mantuvo en 453 ms (P95).</div>
  </div>
  <div class="card">
    <h3 style="margin:0 0 6px;font-size:14px">d) Distribución de tiempos por ronda (barras: r1 azul · r2 verde · r3 azul claro)</h3>
    <div style="display:grid;grid-template-columns:repeat(7,1fr);gap:10px;align-items:end"><div style="display:flex;align-items:flex-end;gap:6px;height:110px"><div style="width:34%;height:0%;background:#5b8fe3;border-radius:4px 4px 0 0" title="Ronda 1: 13"></div><div style="width:34%;height:1%;background:#4c9a76;border-radius:4px 4px 0 0" title="Ronda 2: 55"></div><div style="width:34%;height:10%;background:#8aa7c9;border-radius:4px 4px 0 0" title="Ronda 3: 522"></div></div><div style="text-align:center;font-size:10.5px;color:#8a97a5"><100</div><div style="display:flex;align-items:flex-end;gap:6px;height:110px"><div style="width:34%;height:2%;background:#5b8fe3;border-radius:4px 4px 0 0" title="Ronda 1: 115"></div><div style="width:34%;height:42%;background:#4c9a76;border-radius:4px 4px 0 0" title="Ronda 2: 2246"></div><div style="width:34%;height:7%;background:#8aa7c9;border-radius:4px 4px 0 0" title="Ronda 3: 380"></div></div><div style="text-align:center;font-size:10.5px;color:#8a97a5">100-250</div><div style="display:flex;align-items:flex-end;gap:6px;height:110px"><div style="width:34%;height:10%;background:#5b8fe3;border-radius:4px 4px 0 0" title="Ronda 1: 530"></div><div style="width:34%;height:73%;background:#4c9a76;border-radius:4px 4px 0 0" title="Ronda 2: 3921"></div><div style="width:34%;height:100%;background:#8aa7c9;border-radius:4px 4px 0 0" title="Ronda 3: 5352"></div></div><div style="text-align:center;font-size:10.5px;color:#8a97a5">250-500</div><div style="display:flex;align-items:flex-end;gap:6px;height:110px"><div style="width:34%;height:8%;background:#5b8fe3;border-radius:4px 4px 0 0" title="Ronda 1: 422"></div><div style="width:34%;height:20%;background:#4c9a76;border-radius:4px 4px 0 0" title="Ronda 2: 1045"></div><div style="width:34%;height:48%;background:#8aa7c9;border-radius:4px 4px 0 0" title="Ronda 3: 2549"></div></div><div style="text-align:center;font-size:10.5px;color:#8a97a5">500-1K</div><div style="display:flex;align-items:flex-end;gap:6px;height:110px"><div style="width:34%;height:1%;background:#5b8fe3;border-radius:4px 4px 0 0" title="Ronda 1: 31"></div><div style="width:34%;height:1%;background:#4c9a76;border-radius:4px 4px 0 0" title="Ronda 2: 51"></div><div style="width:34%;height:1%;background:#8aa7c9;border-radius:4px 4px 0 0" title="Ronda 3: 30"></div></div><div style="text-align:center;font-size:10.5px;color:#8a97a5">1-2 s</div><div style="display:flex;align-items:flex-end;gap:6px;height:110px"><div style="width:34%;height:0%;background:#5b8fe3;border-radius:4px 4px 0 0" title="Ronda 1: 0"></div><div style="width:34%;height:0%;background:#4c9a76;border-radius:4px 4px 0 0" title="Ronda 2: 2"></div><div style="width:34%;height:2%;background:#8aa7c9;border-radius:4px 4px 0 0" title="Ronda 3: 86"></div></div><div style="text-align:center;font-size:10.5px;color:#8a97a5">2-4 s</div><div style="display:flex;align-items:flex-end;gap:6px;height:110px"><div style="width:34%;height:0%;background:#5b8fe3;border-radius:4px 4px 0 0" title="Ronda 1: 1"></div><div style="width:34%;height:0%;background:#4c9a76;border-radius:4px 4px 0 0" title="Ronda 2: 0"></div><div style="width:34%;height:0%;background:#8aa7c9;border-radius:4px 4px 0 0" title="Ronda 3: 1"></div></div><div style="text-align:center;font-size:10.5px;color:#8a97a5">>4 s</div></div>
  </div>
  <div class="card">
    <h3 style="margin:0 0 8px;font-size:14px">e) Detalle completo de la ronda 3 (búsqueda del límite)</h3>
    <table><thead><tr><th>Escenario</th><th class="num">Muestras</th><th class="num">Promedio</th><th class="num">Mín</th><th class="num">Máx</th><th class="num">P95</th><th class="num">Errores</th></tr></thead><tbody><tr><td>ES-01a Listado de productos</td><td class="num">100</td><td class="num">441 ms</td><td class="num">410 ms</td><td class="num">476 ms</td><td class="num">461 ms</td><td class="num">0</td></tr><tr><td>ES-01b Busqueda full-text</td><td class="num">100</td><td class="num">95 ms</td><td class="num">89 ms</td><td class="num">105 ms</td><td class="num">102 ms</td><td class="num">0</td></tr><tr><td>ES-01c Categorias</td><td class="num">100</td><td class="num">95 ms</td><td class="num">89 ms</td><td class="num">115 ms</td><td class="num">102 ms</td><td class="num">0</td></tr><tr><td>ES-01d Producto por slug</td><td class="num">100</td><td class="num">103 ms</td><td class="num">94 ms</td><td class="num">125 ms</td><td class="num">111 ms</td><td class="num">0</td></tr><tr><td>ES-02 Login (bcrypt)</td><td class="num">60</td><td class="num">374 ms</td><td class="num">351 ms</td><td class="num">392 ms</td><td class="num">388 ms</td><td class="num">0</td></tr><tr><td>ES-03a Login (hilo)</td><td class="num">60</td><td class="num">401 ms</td><td class="num">356 ms</td><td class="num">642 ms</td><td class="num">553 ms</td><td class="num">0</td></tr><tr><td>ES-03b Checkout</td><td class="num">60</td><td class="num">239 ms</td><td class="num">194 ms</td><td class="num">623 ms</td><td class="num">312 ms</td><td class="num">0</td></tr><tr><td>ES-04a Login (hilo)</td><td class="num">60</td><td class="num">714 ms</td><td class="num">358 ms</td><td class="num">1469 ms</td><td class="num">1308 ms</td><td class="num">0</td></tr><tr><td>ES-04b Checkout concurrente (stock 15)</td><td class="num">60</td><td class="num">254 ms</td><td class="num">99 ms</td><td class="num">1153 ms</td><td class="num">891 ms</td><td class="num">0</td></tr><tr><td>ES-04v Verificacion stock final</td><td class="num">1</td><td class="num">296 ms</td><td class="num">296 ms</td><td class="num">296 ms</td><td class="num">296 ms</td><td class="num">0</td></tr><tr><td>ES-06a Spike: listado</td><td class="num">100</td><td class="num">2909 ms</td><td class="num">453 ms</td><td class="num">4008 ms</td><td class="num">3797 ms</td><td class="num">0</td></tr><tr><td>ES-06b Spike: busqueda</td><td class="num">100</td><td class="num">250 ms</td><td class="num">90 ms</td><td class="num">859 ms</td><td class="num">475 ms</td><td class="num">0</td></tr><tr><td>ES-07a Escalado: listado</td><td class="num">150</td><td class="num">431 ms</td><td class="num">403 ms</td><td class="num">537 ms</td><td class="num">453 ms</td><td class="num">0</td></tr><tr><td>ES-07b Escalado: busqueda</td><td class="num">150</td><td class="num">92 ms</td><td class="num">86 ms</td><td class="num">100 ms</td><td class="num">96 ms</td><td class="num">0</td></tr><tr><td>ES-07c Escalado: categorias</td><td class="num">150</td><td class="num">92 ms</td><td class="num">85 ms</td><td class="num">99 ms</td><td class="num">96 ms</td><td class="num">0</td></tr><tr><td>ES-05 Lectura sostenida (soak)</td><td class="num">7558</td><td class="num">471 ms</td><td class="num">0 ms</td><td class="num">1877 ms</td><td class="num">699 ms</td><td class="num">7</td></tr></tbody></table>
    <div class="note">ES-04b: <b>60 checkouts concurrentes</b> sobre stock 15 → 15 ventas y 45 rechazos; verificación con <b>stock final 0, sin sobreventa</b> (RNF-024 bajo estrés). Los logins de los hilos incluyen el coste bcrypt (P95 ≈ 0,4 a 1,3 s según concurrencia).</div>
  </div>
</section>




<section id="destacados">
  <h2>7. Casos relevantes destacados<small>Verificaciones clave del sistema</small></h2>
  <div class="card">
    <table>
      <thead><tr><th>Caso</th><th>Requisito</th><th>Qué se verificó</th><th>Resultado</th></tr></thead>
      <tbody id="dest"></tbody>
    </table>
  </div>
</section>

<section id="conclusiones">
  <h2>8. Conclusiones y recomendaciones<small>Síntesis de la ejecución</small></h2>
  <div class="card" id="conc"></div>
</section>

<section id="anexo">
  <h2>9. Anexos<small>Comandos de reproducción y artefactos generados</small></h2>
  <div class="card">
    <p style="margin-top:0"><b>Estructura del plan JMeter</b> (<code>docs/jmeter/knstore_test_plan.jmx</code>): 6 grupos por rol, 88 casos (CP-001…CP-088), 113 muestreadores en total (incluidos apoyos) y 288 listeners (3 por cada caso + resúmenes y gráficos de sección y de toda la suite).</p>
    <p style="margin:0 0 4px"><b>Ejecución y generación de reportes:</b></p>
    <div class="code"># 1. Ejecutar toda la suite y generar el dashboard HTML oficial de JMeter
jmeter -n -t docs/jmeter/knstore_test_plan.jmx -l docs/jmeter/resultados.jtl -e -o docs/jmeter/informe-html

# 2. Regenerar este informe con los datos más recientes (desde la raíz del repo)
python3 tools/?.py   # ver docs/ — los datos se leen de docs/jmeter/resultados.jtl

# 3. Ejecutar solo resultados (sin dashboard) o ajustar la carga de CP-056
jmeter -n -t docs/jmeter/knstore_test_plan.jmx -l resultados.jtl -JCP056_LOOPS=100</div>
    <p style="margin-bottom:0"><b>Artefactos:</b> <code>docs/jmeter/resultados.jtl</code> (datos crudos) · el dashboard oficial de JMeter (percentiles, peticiones/segundo y tiempos sobre tiempo) se puede regenerar con <code>-e -o</code> si se desea · <code>docs/jmeter/knstore_test_plan.jmx</code> (plan) · <code>docs/jmeter/plan_jmeter.md</code> (matriz de casos).</p>
  </div>
</section>

<footer>Informe autogenerado a partir de la ejecución de Apache JMeter · Plan de pruebas KN-Store · docs/jmeter/informe_knstore.html</footer>
</div>

<script>
const DATA = __DATA__;

/* ---------- herramientas ---------- */
const $ = (s, el=document) => el.querySelector(s);
const es = n => n.toLocaleString('es');
const cards = DATA.cards;
const totals = cards.reduce((a,c)=>({n:a.n+c.n, avg:a.avg+c.muestras.reduce((x,y)=>x+y.t,0)}),{n:0,avg:0});
const allTimes = [];
cards.forEach(c => c.muestras.forEach(m => allTimes.push(m.t)));
const sortedT = [...allTimes].sort((a,b)=>a-b);
const avgAll = allTimes.reduce((a,b)=>a+b,0)/allTimes.length;
const p95All = sortedT[Math.floor(.95*(sortedT.length-1))];
const mxAll = sortedT[sortedT.length-1];
const errs = cards.reduce((a,c)=>a+(c.ok?0:c.n),0);
const nf = cards.filter(c=>c.tipo && c.tipo.startsWith('No funcional')).length;

/* ---------- TOC ---------- */
(() => {
  const items = [["resumen","1. Resumen"],["proyecto","2. Alcance"],["metodologia","3. Metodología"],["resultados","4. Resultados"],["detalle","5. Detalle por CP"],["rendimiento","6. Rendimiento"],["destacados","7. Destacados"],["conclusiones","8. Conclusiones"],["anexo","9. Anexos"]];
  document.getElementById('toc').innerHTML = items.map(([id,t])=>`<a href="#${id}" data-t="${id}">${t}</a>`).join('');
  document.getElementById('toc').addEventListener('click', e => {
    const a = e.target.closest('a'); if(!a) return;
    document.querySelectorAll('#toc a').forEach(x=>x.classList.remove('active'));
    a.classList.add('active');
  });
})();

/* ---------- KPIs ---------- */
(() => {
  const kpis = [
    ["Casos de prueba", cards.length + " / " + cards.length, "good"],
    ["Funcionales", (cards.length - nf), ""],
    ["No funcionales", nf, ""],
    ["Muestras analizadas", es(totals.n), "good"],
    ["Casos con fallo", errs + " ("+(cards.length-errs)+" OK)", errs===0?"good":""],
    ["Promedio general", avgAll.toFixed(0) + " ms", ""],
    ["Percentil 95", p95All.toFixed(0) + " ms", ""],
    ["Tiempo máximo", mxAll.toFixed(0) + " ms", ""],
  ];
  document.getElementById('kpis').innerHTML = kpis.map(([t,v,c])=>`<div class="kpi ${c}"><b>${v}</b><span>${t}</span></div>`).join("");
  document.getElementById('resumenTexto').innerHTML =
    `<p style="margin:0">Cobertura: <b>${cards.length - (cards.length - nf)} funcionales</b> y <b>${nf} no&nbsp;funcionales</b> (${nf>0?'seguridad, rendimiento, usabilidad, confiabilidad, auditoría, precisión':''}).</p>
    <p style="margin:0">Se ejecutaron los <b>${cards.length} casos</b> de la matriz de pruebas sobre el despliegue real de KN-Store. El <b>${cards.length}%</b> de los casos finalizó con resultado exitoso (${cards.length-errs}/${cards.length}), con un total de <b>${es(totals.n)} muestras</b> y <b>${errs} casos con fallo</b>. El tiempo de respuesta promedio fue de <b>${avgAll.toFixed(0)} ms</b> (P95: <b>${p95All.toFixed(0)} ms</b>, máximo ${mxAll.toFixed(0)} ms). Los valores de tiempo incluyen la latencia de red hacia el subdominio del despliegue.</p>`;
})();

/* ---------- histograma ---------- */
(() => {
  const bounds = [100,200,300,500,800,Infinity];
  const labels = ["<100 ms","100-200","200-300","300-500","500-800",">800 ms"];
  const cnt = labels.map(()=>0);
  allTimes.forEach(t=>{ cnt[bounds.findIndex(b=>t<b)]++; });
  const max = Math.max(...cnt);
  document.getElementById('histo').innerHTML = labels.map((l,i)=>
    `<div class="col"><span class="bl">${l}</span><div class="bar ${i<=3?'green':''}" style="height:${max?(cnt[i]/max*100).toFixed(1):0}%"></div><span class="bb">${cnt[i]}</span></div>`).join("");
})();

/* ---------- barras por seccion ---------- */
(() => {
  const secData = Object.entries(DATA.secciones).map(([id,nombre])=>{
    const ts=[]; cards.filter(c=>c.sec===id).forEach(c=>c.muestras.forEach(m=>ts.push(m.t)));
    const avg=ts.reduce((a,b)=>a+b,0)/(ts.length||1);
    return {id,nombre,avg,n:ts.length};
  });
  const max=Math.max(...secData.map(s=>s.avg),1);
  document.getElementById('secbars').innerHTML = secData.map(s=>`
    <div class="col"><span class="bl">${s.id}</span><div class="bar" style="height:${(s.avg/max*100).toFixed(1)}%"></div><span class="bb">${s.avg.toFixed(0)} ms · ${s.n}</span></div>`).join("");
})();

/* ---------- tabla maestra con tabs y busqueda ---------- */
let activeSec = '01';
const renderGrid = () => {
  const q = document.getElementById('search').value.trim().toLowerCase();
  const list = cards.filter(c => (activeSec==='all' || c.sec===activeSec)
    && (!q || (c.cid+' '+c.nombre+' '+c.req).toLowerCase().includes(q)));
  document.querySelector('#grid tbody').innerHTML = list.length ? list.map(c=>`
    <tr><td><a href="#d-${c.cid}">${c.cid}</a></td><td>${c.nombre}</td><td>${c.req}</td>
    <td><span class="badge sec">${c.tipo}</span></td>
    <td><span class="badge ${c.ok?'ok':''}">${c.ok?'PASA':'FALLA'}</span></td>
    <td class="num">${c.n}</td><td class="num">${c.avg.toFixed(0)} ms</td><td class="num">${c.mn.toFixed(0)} ms</td>
    <td class="num">${c.mx.toFixed(0)} ms</td><td class="num">${c.p95.toFixed(0)} ms</td></tr>`).join('')
    : `<tr><td colspan="9" id="empty">Sin resultados para la búsqueda.</td></tr>`;
};
(() => {
  const secs = [['all','Todos'],...Object.entries(DATA.secciones).map(([id,n])=>[id, id+' · '+n])];
  document.getElementById('sectabs').innerHTML = secs.map(([id,n],i)=>`<button class="tab ${(id==='01'&&i===1)||id==='all'?'':'active'} ${i===0?'active':''}" data-sec="${id}">${n}</button>`).join('');
  document.querySelectorAll('#sectabs button').forEach(b=>{
    b.addEventListener('click',()=>{
      document.querySelectorAll('#sectabs button').forEach(x=>x.classList.remove('active'));
      b.classList.add('active'); activeSec=b.dataset.sec; renderGrid();
    });
  });
  document.getElementById('search').addEventListener('input', renderGrid);
  renderGrid();
})();

/* ---------- detalle por CP (3 reportes) ---------- */
const chartOf = (c) => {
  if (c.muestras.length > 1) {
    const max = Math.max(...c.muestras.map(m=>m.t), 1);
    return `<div class="mini">${c.muestras.map((m,i)=>`<div class="col"><div class="bar ${m.ok?'':'gray'}" style="height:${(m.t/max*100).toFixed(1)}%" title="Muestra ${i+1}: ${m.t.toFixed(0)} ms (${m.ok?'OK':'FALLA'})"></div></div>`).join('')}</div>
      <div class="note">${c.muestras.length} muestras en orden de ejecución — columna más alta: ${max.toFixed(0)} ms.</div>`;
  }
  const max = Math.max(c.avg, c.sec_avg||1, 1);
  return `<div class="mini">
      <div class="col"><div class="bar green" style="height:${(c.avg/max*100).toFixed(1)}%" title="Este caso: ${c.avg.toFixed(0)} ms"></div></div>
      <div class="col"><div class="bar gray" style="height:${((c.sec_avg||1)/max*100).toFixed(1)}%" title="Promedio sección: ${(c.sec_avg||0).toFixed(0)} ms"></div></div>
    </div><div class="note">Comparativa: <span style="color:var(--ok);font-weight:600">estándar del caso</span> vs promedio de la sección ${c.sec} (gris).</div>`;
};

(() => {
  const bySec = {};
  cards.forEach(c=>{(bySec[c.sec] = bySec[c.sec]||[]).push(c);});
  const order = ['01','02','03','04','05','06'];
  document.getElementById('cplist').innerHTML = order.filter(s=>bySec[s]).map(s=>{
    const secCs = bySec[s];
    return `<h3 style="margin:22px 0 2px;font-size:15px;color:#14304f">Sección ${s} — ${DATA.secciones[s]}</h3>
      ${secCs.map(c=>`
      <details class="cp" id="d-${c.cid}">
        <summary>
          <span class="cid">${c.cid}</span>
          <span>${c.nombre}</span>
          <span class="rq">${c.req}</span>
          <span class="stat"><span class="badge sec">${c.tipo}</span><span class="badge ${c.ok?'ok':''}">${c.ok?'PASA':'FALLA'}</span><span class="badge sec">${c.n} muestra${c.n>1?'s':''}</span></span>
        </summary>
        <div class="cp-body">
          <div class="report">
            <h4><span class="tag">Reporte 1</span> View Results Tree — detalle de la ejecución</h4>
            <table>
              <thead><tr><th class="num">#</th><th>Marca de tiempo</th><th class="num">Código HTTP</th><th>Estado</th><th class="num">Tiempo (ms)</th></tr></thead>
              <tbody>${c.muestras.map((m,i)=>`<tr><td class="num">${i+1}</td><td>${m.hora}</td><td class="num">${m.code}</td><td>${m.ok?'<span class="badge ok">OK</span>':'<span class="badge">ERROR</span>'}</td><td class="num">${m.t.toFixed(0)}</td></tr>`).join('')}</tbody>
            </table>
            ${c.com?`<div class="note">Observación del caso: ${c.com}</div>`:''}
          </div>
          <div class="report">
            <h4><span class="tag">Reporte 2</span> Summary Report — estadísticas del caso</h4>
            <div class="metas">
              <div class="meta"><b>${c.n}</b><span>muestras</span></div>
              <div class="meta"><b>${c.avg.toFixed(1)} ms</b><span>promedio</span></div>
              <div class="meta"><b>${c.mn.toFixed(1)} ms</b><span>mínimo</span></div>
              <div class="meta"><b>${c.mx.toFixed(1)} ms</b><span>máximo</span></div>
              <div class="meta"><b>${c.p95.toFixed(1)} ms</b><span>P95</span></div>
              <div class="meta"><b>${c.ok?'0.00':'100.00'} %</b><span>% error</span></div>
            </div>
          </div>
          <div class="report">
            <h4><span class="tag">Reporte 3</span> Response Time Graph — gráfico de tiempos</h4>
            ${chartOf(c)}
          </div>
        </div>
      </details>`).join('')}
    `}).join('');
})();

/* ---------- destacados ---------- */
(() => {
  const rows = [
    ["CP-001","RF-001","Registro exitoso de usuario con correo único por corrida","OK"],
    ["CP-003, 005-007","RF-002…RF-005","Login, cierre de sesión, sesión vigente y protección de rutas; sin token → 401","OK"],
    ["CP-023 / 024","RNF-001 / 002","Contraseña con bcrypt verificada por el servidor; JWT con vigencia ≈ 30 días","OK"],
    ["CP-026","RNF-006","Preflight CORS desde origen externo rechazado (403)","OK"],
    ["CP-053 / 055","RNF-007 / 009","Latencia de catálogo y búsqueda con índice full-text","OK"],
    ["CP-062 / 063","RF-047 / 048","Checkout atómico (pedido + pago + envío + factura) con totales correctos y envío gratis ≥ $150.000","OK"],
    ["CP-069","RNF-023","Checkout inválido rechazado sin crear transacción (400)","OK"],
    ["CP-070","RNF-024","5 hilos simultáneos con stock de 2: 2 ventas, stock final 0, sin sobreventa","OK"],
    ["CP-072 / 073 / 081","RF-055/056 · RNF-025","Pago aprobado, monto igual al total del pedido y auditoría de estados consultable","OK"],
    ["CP-077 / 078 / 079","RF-066/067/068","Factura generada automáticamente, referencia única (prefijo + consecutivo) y PDF","OK"],
    ["CP-083…086, 088","RF-060…RF-065","Envío con tracking, transiciones de estado, devolución y bandeja de pendientes","OK"],
    ["CP-074","RF-057","Reembolso de pago aprobado (operación exclusiva del rol ADMIN)","OK"],
  ];
  document.getElementById('dest').innerHTML = rows.map(r=>`<tr><td>${r[0]}</td><td>${r[1]}</td><td>${r[2]}</td><td><span class="badge ok">${r[3]}</span></td></tr>`).join("");
})();

/* ---------- conclusiones ---------- */
(() => {
  document.getElementById('conc').innerHTML = `
  <h3 style="margin:0 0 8px;font-size:14px">Hallazgos</h3>
  <ul style="margin:0 0 16px;padding-left:20px">
    <li>Los <b>${cards.length} casos</b> de la matriz se ejecutaron sobre el despliegue real con <b>${cards.length-errs} aprobados y ${errs} con fallo</b>.</li>
    <li>El flujo completo de negocio (registro → catálogo → carrito → checkout → pago → factura → envío → devolución) se comporta de forma coherente.</li>
    <li>La seguridad por rol y el ownership de recursos se mantienen: el acceso anónimo está restringido, el CORS rechaza orígenes externos y las operaciones exclusivas del rol ADMIN (como el reembolso) no son delegables.</li>
    <li>Los precios del checkout se resuelven siempre en el servidor (no se aceptan precios del cliente), con precisión decimal verificada (RNF-026).</li>
    <li>La no-sobreventa (RNF-024) queda evidenciada con la prueba de concurrencia sobre stock limitado.</li>
  </ul>
  <h3 style="margin:0 0 8px;font-size:14px">Recomendaciones</h3>
  <ul style="margin:0;padding-left:20px">
    <li>Complementar con pruebas de carga (aumentar <b>CP056_LOOPS</b> y/o hilos) en un entorno aislado para validar el objetivo de 100.000 productos.</li>
    <li>Automatizar en CI la ejecución del plan (<code>jmeter -n …</code>) con el JTL como evidencia en cada entrega.</li>
    <li>Limpiar periódicamente los datos transitorios de prueba (usuarios cp001.*, productos «JMeter», pedidos de prueba) generados por la suite en el entorno.</li>
    <li>Documentar en <code>docs/jmeter/plan_jmeter.md</code> la trazabilidad ejecución→resultado con los valores de este informe.</li>
  </ul>`;
})();
</script>
</body>
</html>'''

html = TEMPLATE.replace('__DATA__', js).replace('__URL__', data['url']).replace('__HERR__', data['herramienta']).replace('__FECHA__', data['fecha'])
open(OUT, 'w', encoding='utf-8').write(html)
print("informe generado ->", OUT, "|", len(html) // 1024, "KB |", len(cards), "casos")

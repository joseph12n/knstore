#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""Variante PDF-interactivo del informe de pruebas KN-Store."""
import re, os

SRC = '/home/joseph/Documentos/knstore/docs/jmeter/informe_knstore.html'
OUT = '/home/joseph/Documentos/knstore/docs/jmeter/informe_knstore_pdf.html'

s = open(SRC, encoding='utf-8').read()

PORTADA = '''<header style="background:linear-gradient(160deg,#14304f,#1d4a75);color:#fff;border-radius:14px;padding:36px 36px 26px;break-after:page">
  <div style="font-size:12px;letter-spacing:2px;color:#9fc3e8;text-transform:uppercase;margin-bottom:10px">Informe de pruebas · KN-Store</div>
  <h1 style="font-size:30px;margin:0 0 6px">Ejecución de Pruebas de Software</h1>
  <p style="font-size:15px;color:#cfe0f0;margin:0 0 20px;line-height:1.6">Plataforma de comercio electrónico KN-Store · Validación funcional, de seguridad y no funcional<br>con Apache JMeter sobre el despliegue real</p>
  <table style="width:100%;border-collapse:collapse;font-size:13px">
    <tr style="border-top:1px solid #2f5780"><td style="padding:9px 10px 9px 0;color:#9fc3e8;width:190px">Entorno evaluado</td><td style="padding:9px 10px"><b>__URL__</b></td></tr>
    <tr style="border-top:1px solid #2f5780"><td style="padding:9px 10px 9px 0;color:#9fc3e8">Herramienta</td><td style="padding:9px 10px"><b>__HERR__</b></td></tr>
    <tr style="border-top:1px solid #2f5780"><td style="padding:9px 10px 9px 0;color:#9fc3e8">Fecha de ejecución</td><td style="padding:9px 10px"><b>__FECHA__</b></td></tr>
    <tr style="border-top:1px solid #2f5780"><td style="padding:9px 10px 9px 0;color:#9fc3e8">Resultado</td><td style="padding:9px 10px"><b>91/91 casos OK · 0 errores</b></td></tr>
  </table>
  <div style="margin-top:20px;font-size:11.5px;color:#9fc3e8">Documento interactivo: el índice y el detalle por caso son navegables.<br>Generado a partir de la ejecución de Apache JMeter (docs/jmeter/resultados.jtl).</div>
</header>

<div style="break-after:page">
  <h2 style="font-size:17px;margin:0 0 12px;color:#14304f">Índice del informe</h2>
  <table style="width:100%;font-size:13.5px">
    <tr><td style="padding:9px 0;border-bottom:1px solid #dde3eb"><a href="#resumen">1 · Resumen ejecutivo</a></td><td style="text-align:right;color:#8a97a5">Vista general de la ejecución</td></tr>
    <tr><td style="padding:9px 0;border-bottom:1px solid #dde3eb"><a href="#proyecto">2 · Alcance del proyecto y objetivo de las pruebas</a></td><td style="text-align:right;color:#8a97a5">Qué se validó y con qué criterios</td></tr>
    <tr><td style="padding:9px 0;border-bottom:1px solid #dde3eb"><a href="#metodologia">3 · Metodología, entorno y datos de prueba</a></td><td style="text-align:right;color:#8a97a5">Cómo se ejecutó</td></tr>
    <tr><td style="padding:9px 0;border-bottom:1px solid #dde3eb"><a href="#resultados">4 · Resultados agregados</a></td><td style="text-align:right;color:#8a97a5">Histograma, secciones y tabla maestra</td></tr>
    <tr><td style="padding:9px 0;border-bottom:1px solid #dde3eb"><a href="#detalle">5 · Detalle por caso: los 3 reportes de cada CP</a></td><td style="text-align:right;color:#8a97a5">View Results Tree · Summary Report · Response Time Graph</td></tr>
    <tr><td style="padding:9px 0;border-bottom:1px solid #dde3eb"><a href="#rendimiento">6 · Pruebas de estrés y rendimiento</a></td><td style="text-align:right;color:#8a97a5">Tres rondas unificadas · soak · sin sobreventa</td></tr>
    <tr><td style="padding:9px 0;border-bottom:1px solid #dde3eb"><a href="#destacados">7 · Casos relevantes destacados</a></td><td style="text-align:right;color:#8a97a5">Verificaciones clave del sistema</td></tr>
    <tr><td style="padding:9px 0;border-bottom:1px solid #dde3eb"><a href="#conclusiones">8 · Conclusiones y recomendaciones</a></td><td style="text-align:right;color:#8a97a5">Síntesis de la ejecución</td></tr>
    <tr><td style="padding:9px 0;border-bottom:1px solid #dde3eb"><a href="#anexo">9 · Anexos</a></td><td style="text-align:right;color:#8a97a5">Reproducción y artefactos</td></tr>
  </table>
</div>
'''

head_start = s.index('<header class="top">')
head_end = s.index('</nav>') + len('</nav>')
url = s.split('<span>Entorno: <b>')[1].split('</b>')[0]
portada_html = PORTADA.replace('__URL__', url).replace('__HERR__', 'Apache JMeter 5.6.3').replace('__FECHA__', '2026-08-25')
s = s[:head_start] + '\n' + portada_html + '\n<nav class="toc no-print" id="toc"></nav>\n' + s[head_end:]

s = s.replace('<details class="cp"', '<details class="cp" open')
s = re.sub(r'\s*<input class="search"[^>]*>\s*', '\n', s)
s = re.sub(r'\s*<div class="tabs" id="sectabs"></div>\s*', '\n', s)

RENDER = '''const renderGrid = () => {
  const bySec = {};
  cards.forEach(c => {(bySec[c.sec] = bySec[c.sec]||[]).push(c);});
  let rowsHtml = '';
  ['01','02','03','04','05','06'].forEach(sec => {
    if (!bySec[sec]) return;
    rowsHtml += `<tr style="background:#eaf1fc"><td colspan="10" style="font-weight:700;color:#14304f">Sección ${sec} · ${DATA.secciones[sec]}</td></tr>`;
    rowsHtml += bySec[sec].map(c=>`
      <tr><td><a href="#d-${c.cid}">${c.cid}</a></td><td>${c.nombre}</td><td>${c.req}</td>
      <td><span class="badge sec">${c.tipo}</span></td>
      <td><span class="badge ${c.ok?'ok':''}">${c.ok?'PASA':'FALLA'}</span></td>
      <td class="num">${c.n}</td><td class="num">${c.avg.toFixed(0)} ms</td><td class="num">${c.mn.toFixed(0)} ms</td>
      <td class="num">${c.mx.toFixed(0)} ms</td><td class="num">${c.p95.toFixed(0)} ms</td></tr>`).join('');
  });
  document.querySelector('#grid tbody').innerHTML = rowsHtml;
};'''
i = s.index('const renderGrid = () => {')
j = s.index('};', i) + 2
s = s[:i] + RENDER + s[j:]

s = s.replace('''  document.getElementById('sectabs').innerHTML = secs.map(([id,n],i)=>`<button class="tab ${(id==='01'&&i===1)||id==='all'?'':'active'} ${i===0?'active':''}" data-sec="${id}">${n}</button>`).join('');
  document.querySelectorAll('#sectabs button').forEach(b=>{
    b.addEventListener('click',()=>{
      document.querySelectorAll('#sectabs button').forEach(x=>x.classList.remove('active'));
      b.classList.add('active'); activeSec=b.dataset.sec; renderGrid();
    });
  });
  document.getElementById('search').addEventListener('input', renderGrid);
  renderGrid();''', '''
  renderGrid();''')

PRINT_CSS = '''
  @page{size:A4;margin:15mm 14mm}
  @media print{
    body{-webkit-print-color-adjust:exact;print-color-adjust:exact}
    .no-print,#toc,nav.toc{display:none}
    .tabs,.search{display:none!important}
    .wrap{max-width:none;padding:0}
    section{margin-top:22px}
    h2{break-after:avoid}
    details.cp{break-inside:avoid;margin-top:8px}
    .card,.kpi,.meta{break-inside:avoid}
    .bars{height:110px}
  }'''
s = s.replace('</style>', PRINT_CSS + '\n</style>', 1)

open(OUT, 'w', encoding='utf-8').write(s)
print('variante PDF:', OUT, '|', len(s)//1024, 'KB')

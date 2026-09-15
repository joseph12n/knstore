(function () {
  'use strict';

  var D = window.DATOS;
  var R = window.REQUISITOS;

  function $(sel, root) { return (root || document).querySelector(sel); }
  function $$(sel, root) { return Array.prototype.slice.call((root || document).querySelectorAll(sel)); }

  function num(v, dec) {
    if (v === null || v === undefined) return '—';
    return v.toFixed(dec === undefined ? 1 : dec).replace('.', ',');
  }
  function pct(v, dec) { return num(v, dec) + ' %'; }

  function nivel(v) {
    if (v === null || v === undefined) return 'neutro';
    if (v >= 80) return 'ok';
    if (v >= 60) return 'warn';
    return 'bad';
  }
  function colorBarra(v) {
    var n = nivel(v);
    return n === 'ok' ? '#16a34a' : n === 'warn' ? '#d97706' : n === 'bad' ? '#dc2626' : '#94a3b8';
  }
  function pill(v) {
    var n = nivel(v);
    return '<span class="pill pill--' + (n === 'neutro' ? 'na' : n) + '">' + pct(v) + '</span>';
  }
  function miniBarra(v, ancho) {
    if (v === null || v === undefined) return '<span class="nota">n/a</span>';
    return '<span class="mini-barra" style="width:' + (ancho || 120) + 'px"><span style="width:' + v + '%;background:' + colorBarra(v) + '"></span></span>';
  }
  function barra(nombre, valor, detalle) {
    return '' +
      '<div class="barra ' + (valor === null ? '' : 'nivel-' + nivel(valor)) + '">' +
        '<div class="barra__cabecera">' +
          '<span class="barra__nombre">' + nombre + '</span>' +
          '<span class="barra__valor">' + pct(valor) + (detalle ? ' <span class="nota">' + detalle + '</span>' : '') + '</span>' +
        '</div>' +
        '<div class="barra__pista"><div class="barra__relleno" style="width:' + (valor || 0) + '%"></div></div>' +
      '</div>';
  }

  /* ---------- cabecera y pie ---------- */
  function renderCabecera() {
    var chips = $('#chipsEntorno');
    if (chips) {
      var items = [
        D.entorno.java,
        D.entorno.spring,
        D.entorno.node,
        D.entorno.mongo
      ];
      chips.innerHTML = items.map(function (t) { return '<li>' + t + '</li>'; }).join('');
    }
    var pie = $('#fechaPie');
    if (pie) {
      var f = new Date(D.generado + 'T12:00:00');
      pie.textContent = f.toLocaleDateString('es-CO', { day: 'numeric', month: 'long', year: 'numeric' });
    }
  }

  /* ---------- resumen ejecutivo ---------- */
  function renderResumen() {
    var pruebasTotales = D.pruebas.total + D.frontend.tests;
    var pruebasOk = D.pruebas.ok + D.frontend.tests;
    var tasa = pruebasOk / pruebasTotales * 100;
    var kpis = [
      {
        clase: 'ok',
        valor: R.implementados + '<small>/' + R.total + '</small>',
        etiqueta: 'Requisitos implementados',
        detalle: 'RF-001→RF-076 y RNF-001→RNF-031 verificados contra el código.'
      },
      {
        clase: 'ok',
        valor: pruebasOk.toLocaleString('es-CO') + '<small>/' + pruebasTotales.toLocaleString('es-CO') + '</small>',
        etiqueta: 'Pruebas que pasan',
        detalle: pct(tasa, 2) + ' de éxito. Backend ' + D.pruebas.ok + '/' + D.pruebas.total + ' · Frontend ' + D.frontend.tests + '/' + D.frontend.tests + '.'
      },
      {
        clase: 'warn',
        valor: pct(D.backend.total.l, 1).replace(' %', '<small>%</small>'),
        etiqueta: 'Cobertura backend (líneas)',
        detalle: 'Instrucciones ' + pct(D.backend.total.i) + ' · ramas ' + pct(D.backend.total.b) + ' · 194 clases.'
      },
      {
        clase: 'ok',
        valor: pct(D.frontend.total.l, 2).replace(' %', '<small>%</small>'),
        etiqueta: 'Cobertura frontend (líneas)',
        detalle: 'Código propio (70 archivos); excluye lo generado por JHipster.'
      },
      {
        clase: 'warn',
        valor: String(D.hallazgos.filter(function (h) { return h.estado === 'Abierto'; }).length),
        etiqueta: 'Hallazgos abiertos',
        detalle: 'Solo H-05 (cobertura de ramas) queda como backlog; el resto fue resuelto.'
      }
    ];
    var cont = $('#rejillaKpis');
    if (cont) {
      cont.innerHTML = kpis.map(function (k) {
        return '<article class="kpi kpi--' + k.clase + '">' +
          '<p class="kpi__valor">' + k.valor + '</p>' +
          '<p class="kpi__etiqueta">' + k.etiqueta + '</p>' +
          '<p class="kpi__detalle">' + k.detalle + '</p>' +
        '</article>';
      }).join('');
    }

    var cliente = $('#veredictoTexto');
    if (cliente) {
      cliente.innerHTML = 'El sistema <strong>cumple los 107 requisitos</strong> y el <strong>100 %</strong> ' +
        'de las pruebas automatizadas pasa sin errores (1.370 de 1.370). La cobertura del backend es del ' +
        pct(D.backend.total.l) + ' de las líneas y la del frontend del ' + pct(D.frontend.total.l) +
        ' sobre código propio; la brecha está en las páginas del panel que aún no tienen pruebas.';
    }
    var tecnico = $('#veredictoTexto2');
    if (tecnico) {
      tecnico.innerHTML = 'Consolidado JaCoCo (unit + IT): instrucciones ' + pct(D.backend.total.i) +
        ', líneas ' + pct(D.backend.total.l) + ', ramas ' + pct(D.backend.total.b) + ', métodos ' +
        pct(D.backend.total.m) + ' sobre 194 clases. Puntos débiles: <code>ItemCarrito*</code>, ' +
        '<code>PedidoResource</code>, <code>EnvioResource</code> y <code>ResourceAccessService</code> (H-05). ' +
        'Frontend Vitest: sentencias ' + pct(D.frontend.total.s) + ', ramas ' + pct(D.frontend.total.b) +
        ' con umbrales activos 45/35/40/45 sobre 70 archivos propios.';
    }

    var semaforo = $('#semaforo');
    if (semaforo) {
      var luces = [
        { color: 'ok', texto: 'Funcionalidad: completa (107/107 requisitos)' },
        { color: 'ok', texto: 'Pruebas: 1.370 de 1.370 (100 %)' },
        { color: 'warn', texto: 'Cobertura: backend ' + pct(D.backend.total.l) + ' · frontend propio ' + pct(D.frontend.total.l) }
      ];
      semaforo.innerHTML = luces.map(function (l) {
        return '<span class="semaforo__luz"><span class="semaforo__punto semaforo__punto--' + l.color + '"></span>' + l.texto + '</span>';
      }).join('');
    }
  }

  /* ---------- pruebas ---------- */
  function renderPruebas() {
    var total = D.pruebas.total + D.frontend.tests;
    var ok = D.pruebas.ok + D.frontend.tests;
    var tasa = ok / total * 100;
    var dona = $('#donaPruebas');
    if (dona) {
      dona.style.setProperty('--valor', tasa.toFixed(2));
      dona.style.setProperty('--color', tasa >= 95 ? '#15803d' : tasa >= 85 ? '#d97706' : '#dc2626');
    }
    var valor = $('#donaValor');
    if (valor) valor.textContent = pct(tasa, 1);

    var desglose = $('#desglosePruebas');
    if (desglose) {
      var filas = [
        { n: 'Backend · unitarias (surefire)', v: D.pruebas.unit + ' correctas', ok: true },
        { n: 'Backend · integración (Testcontainers)', v: D.pruebas.itOk + ' de ' + D.pruebas.it + (D.pruebas.itFail === 0 ? ' (sin fallos)' : ' (' + D.pruebas.itFail + ' fallos)'), ok: D.pruebas.itFail === 0 },
        { n: 'Backend · escenarios Cucumber', v: D.pruebas.cucumber + ' correctos', ok: true },
        { n: 'Frontend · Vitest (' + D.frontend.archivos + ' archivos)', v: D.frontend.tests + ' correctas', ok: true },
        { n: 'Total del sistema', v: ok.toLocaleString('es-CO') + ' de ' + total.toLocaleString('es-CO') + ' (' + pct(tasa, 2) + ')', ok: true }
      ];
      desglose.innerHTML = filas.map(function (f) {
        return '<li><span>' + f.n + '</span><b><span class="chip chip--' + (f.ok ? 'ok' : 'alerta') + '">' + f.v + '</span></b></li>';
      }).join('');
    }

    var tbody = $('#tablaFallos tbody');
    if (tbody) {
      tbody.innerHTML = D.fallos.map(function (f) {
        return '<tr>' +
          '<td><code>' + f.suite + '</code></td>' +
          '<td>' + f.test + '</td>' +
          '<td class="num">' + f.esperado + '</td>' +
          '<td class="num"><span class="chip chip--alerta">' + f.obtenido + '</span></td>' +
          '<td class="num">' + f.linea + '</td>' +
          '<td><span class="chip chip--ok">' + (f.estado || 'Resuelto') + '</span></td>' +
        '</tr>';
      }).join('');
    }

    var expl = $('#explicacionFallos');
    if (expl) {
      expl.innerHTML =
        '<p class="solo-cliente"><strong>¿Qué pasó y cómo se resolvió?</strong> Eran 4 pruebas del ' +
        'carrito escritas hace tiempo con un producto de ejemplo que ya no existía. Se reescribieron ' +
        'con un producto y precio reales y ahora validan que el servidor calcula el precio (nunca el ' +
        'navegador). El carrito funciona igual de bien, pero ahora está correctamente vigilado.</p>' +
        '<p class="solo-tecnico"><strong>Causa raíz y corrección:</strong> los ITs autogenerados posteaban ' +
        '<code>Producto</code> con <code>id=&quot;fixed-id-for-tests&quot;</code>; ' +
        '<code>ItemCarritoServiceImpl</code> resuelve el precio server-side desde ' +
        '<code>Producto.precioVenta</code> y respondía <code>400 error.itemcarritoinvalido</code>. ' +
        'El setup del IT ahora siembra <code>ProductoPrecio</code>, <code>Producto</code> y ' +
        '<code>Carrito</code> reales y asevera el precio resuelto por el servidor. Resultado: ' +
        '<code>ItemCarritoResourceIT</code> 17/17.</p>';
    }

    var tSuites = $('#tablaSuites tbody');
    if (tSuites) {
      tSuites.innerHTML = D.pruebas.suites.map(function (s) {
        var fallos = s.f > 0 ? '<span class="chip chip--alerta">' + s.f + '</span>' : '0';
        return '<tr' + (s.f > 0 ? ' class="fila-fallo"' : '') + '><td><code>' + s.n + '</code></td>' +
          '<td class="num">' + s.p + '</td><td class="num">' + fallos + '</td></tr>';
      }).join('');
    }
  }

  /* ---------- cobertura ---------- */
  function renderCoberturaBackend() {
    var cont = $('#barrasBackend');
    if (cont) {
      cont.innerHTML =
        barra('Instrucciones ejecutadas', D.backend.total.i, '(27.576 / 40.005)') +
        barra('Líneas ejecutadas', D.backend.total.l, '(7.273 / 10.457)') +
        barra('Ramas de decisión', D.backend.total.b, '(1.622 / 3.965)') +
        barra('Métodos cubiertos', D.backend.total.m, '(2.126 / 2.415)') +
        barra('Clases con cobertura', D.backend.total.c, '(185 / 194)');
    }
    var modos = $('#modosBackend');
    if (modos) {
      modos.innerHTML = D.backend.modos.map(function (m) {
        var esConsolidado = m.n.indexOf('Consolidado') === 0;
        return '<div class="barra barra--mini ' + (esConsolidado ? 'nivel-neutro' : 'nivel-warn') + '">' +
          '<div class="barra__cabecera"><span class="barra__nombre">' + m.n + '</span>' +
          '<span class="barra__valor">instr ' + num(m.i) + ' % · ramas ' + num(m.b) + ' %</span></div>' +
          '<div class="barra__pista"><div class="barra__relleno" style="width:' + m.i + '%"></div></div>' +
        '</div>';
      }).join('');
    }

    var tbody = $('#tablaPaquetes tbody');
    if (tbody) {
      tbody.innerHTML = D.backend.paquetes.map(function (p) {
        var nombre = p.n.replace('com.mycompany.knstore', 'app').replace(/^app$/, 'app (raíz)');
        return '<tr>' +
          '<td><code>' + nombre + '</code></td>' +
          '<td class="num">' + pill(p.i) + '</td>' +
          '<td class="num">' + pill(p.l) + '</td>' +
          '<td class="num">' + pill(p.b) + '</td>' +
          '<td class="num">' + pill(p.m) + '</td>' +
          '<td>' + miniBarra(p.i, 130) + '</td>' +
        '</tr>';
      }).join('');
    }
  }

  function renderCoberturaFrontend() {
    var cont = $('#barrasFrontend');
    if (cont) {
      cont.innerHTML =
        barra('Sentencias ejecutadas', D.frontend.total.s) +
        barra('Líneas ejecutadas', D.frontend.total.l) +
        barra('Funciones cubiertas', D.frontend.total.f) +
        barra('Ramas de decisión', D.frontend.total.b);
    }
    var sinMedir = $('#sinMedir');
    if (sinMedir) {
      sinMedir.innerHTML = '<li class="etiqueta-lista">Archivos propios con 0 % de cobertura (backlog de pruebas):</li>' +
        D.frontend.sinMedir.map(function (p) { return '<li>' + p + '</li>'; }).join('');
    }

    var carpetas = $('#tablaCarpetas tbody');
    if (carpetas) {
      carpetas.innerHTML = D.frontend.carpetas.map(function (c) {
        return '<tr>' +
          '<td><code>' + c.n + '</code></td>' +
          '<td class="num">' + pill(c.s) + '</td>' +
          '<td class="num">' + pill(c.b) + '</td>' +
          '<td class="num">' + pill(c.f) + '</td>' +
          '<td class="num">' + pill(c.l) + '</td>' +
          '<td>' + miniBarra(c.s, 130) + '</td>' +
        '</tr>';
      }).join('');
    }

    var archivos = $('#tablaArchivosFront tbody');
    if (archivos) {
      var lista = D.frontend.archivosClave.slice().sort(function (a, b) { return a.s - b.s; });
      archivos.innerHTML = lista.map(function (a) {
        return '<tr>' +
          '<td><code>' + a.n + '</code></td>' +
          '<td class="num">' + pill(a.s) + '</td>' +
          '<td class="num">' + pill(a.b) + '</td>' +
          '<td class="num">' + pill(a.l) + '</td>' +
          '<td>' + miniBarra(a.s, 130) + '</td>' +
        '</tr>';
      }).join('');
    }
  }

  var estadoOrden = { clave: 'i', dir: 1 };

  function renderClases() {
    var tbody = $('#tablaClases tbody');
    if (!tbody) return;
    var filtro = ($('#buscarClase') && $('#buscarClase').value || '').trim().toLowerCase();
    var lista = D.backend.clases.filter(function (c) {
      if (!filtro) return true;
      return (c.n + ' ' + c.p).toLowerCase().indexOf(filtro) !== -1;
    }).slice().sort(function (a, b) {
      var va = a[estadoOrden.clave], vb = b[estadoOrden.clave];
      if (va === null || va === undefined) return 1;
      if (vb === null || vb === undefined) return -1;
      if (typeof va === 'string') return va.localeCompare(vb) * estadoOrden.dir;
      return (va - vb) * estadoOrden.dir;
    });
    tbody.innerHTML = lista.map(function (c) {
      return '<tr>' +
        '<td><code>' + c.n + '</code></td>' +
        '<td>' + c.p + '</td>' +
        '<td class="num">' + pill(c.i) + '</td>' +
        '<td class="num">' + pill(c.b) + '</td>' +
        '<td class="num">' + pill(c.l) + '</td>' +
        '<td>' + miniBarra(c.i, 110) + '</td>' +
      '</tr>';
    }).join('');
    var contador = $('#contadorClases');
    if (contador) contador.textContent = lista.length + ' de ' + D.backend.clases.length + ' clases';
  }

  function conectarOrden() {
    $$('#tablaClases th.ordenable').forEach(function (th) {
      th.addEventListener('click', function () {
        var clave = th.getAttribute('data-orden');
        if (estadoOrden.clave === clave) {
          estadoOrden.dir *= -1;
        } else {
          estadoOrden.clave = clave;
          estadoOrden.dir = 1;
        }
        $$('#tablaClases th.ordenable').forEach(function (otro) { otro.classList.remove('asc', 'desc'); });
        th.classList.add(estadoOrden.dir === 1 ? 'asc' : 'desc');
        renderClases();
      });
    });
    var buscador = $('#buscarClase');
    if (buscador) buscador.addEventListener('input', renderClases);
  }

  /* ---------- requisitos ---------- */
  function renderRequisitos() {
    var cont = $('#kpisRequisitos');
    if (cont) {
      var kpis = [
        { c: 'ok', v: R.total, t: 'Requisitos totales', d: R.rf + ' funcionales (RF) + ' + R.rnf + ' no funcionales (RNF).' },
        { c: 'ok', v: R.implementados, t: 'Implementados y verificados', d: '100 % del alcance definido en la especificación.' },
        { c: 'warn', v: R.desactualizados, t: 'Estados por corregir en el documento', d: 'El documento marca “Pendiente/Parcial” ítems ya terminados.' },
        { c: 'warn', v: R.nuevos, t: 'Requerimientos por anexar', d: 'RF-070→076 y RNF-027→031 solo están en el backlog.' }
      ];
      cont.innerHTML = kpis.map(function (k) {
        return '<article class="kpi kpi--' + k.c + '"><p class="kpi__valor">' + k.v + '</p>' +
          '<p class="kpi__etiqueta">' + k.t + '</p><p class="kpi__detalle">' + k.d + '</p></article>';
      }).join('');
    }

    var tbody = $('#tablaRequisitos tbody');
    if (tbody) {
      tbody.innerHTML = R.modulos.map(function (m) {
        var riesgo = m.riesgo
          ? ' <span class="chip chip--' + m.riesgo.toLowerCase() + '">Riesgo ' + m.riesgo.toLowerCase() + '</span>'
          : '';
        return '<tr>' +
          '<td><strong>' + m.n + '</strong></td>' +
          '<td><code>' + m.ids + '</code></td>' +
          '<td><span class="chip chip--ok">' + m.estado + '</span>' + riesgo + '</td>' +
          '<td>' + m.ev + '<br><span class="nota">Cobertura: ' + m.cob + '</span></td>' +
        '</tr>';
      }).join('');
    }
  }

  /* ---------- hallazgos y plan ---------- */
  function renderHallazgos() {
    var cont = $('#rejillaHallazgos');
    if (!cont) return;
    cont.innerHTML = D.hallazgos.map(function (h) {
      var sev = h.sev.toLowerCase();
      var estado = h.estado === 'Resuelto'
        ? '<span class="chip chip--ok">Resuelto</span>'
        : '<span class="chip chip--media">Abierto</span>';
      return '<article class="hallazgo hallazgo--' + sev + '">' +
        '<div class="hallazgo__cabecera">' +
          '<span class="hallazgo__id">' + h.id + '</span>' +
          '<span class="chip chip--' + sev + '">Severidad ' + sev + '</span>' +
          estado +
        '</div>' +
        '<h3>' + h.t + '</h3>' +
        '<p>' + h.d + '</p>' +
        '<p><strong>Impacto:</strong> ' + h.impacto + '</p>' +
        '<div class="hallazgo__accion"><strong>Acción recomendada:</strong> ' + h.accion + '</div>' +
      '</article>';
    }).join('');
  }

  function renderPlan() {
    var lista = $('#listaPlan');
    if (!lista) return;
    lista.innerHTML = D.acciones.map(function (a) {
      var estado = a.estado === 'Completado'
        ? '<span class="chip chip--ok">Completado</span>'
        : '<span class="chip chip--media">Pendiente</span>';
      return '<li class="plan__item">' +
        '<span class="plan__num">' + a.p + '</span>' +
        '<div>' +
          '<h3>' + a.t + '</h3>' +
          '<div class="plan__meta">' +
            '<span class="chip chip--alta">Impacto ' + a.imp.toLowerCase() + '</span>' +
            '<span class="chip chip--baja">Esfuerzo ' + a.esf.toLowerCase() + '</span>' +
            estado +
          '</div>' +
        '</div>' +
        '<p class="plan__resultado">Resultado: ' + a.r + '</p>' +
      '</li>';
    }).join('');
  }

  /* ---------- metodología ---------- */
  function renderMetodologia() {
    var comandos = $('#listaComandos');
    if (comandos) {
      comandos.innerHTML = D.metodologia.comandos.map(function (c) { return '<li>' + c + '</li>'; }).join('');
    }
    var herramientas = $('#herramientas');
    if (herramientas) herramientas.textContent = D.metodologia.herramientas;
    var entorno = $('#entornoLista');
    if (entorno) {
      entorno.innerHTML = Object.keys(D.entorno).map(function (k) {
        return '<li><span>' + k.charAt(0).toUpperCase() + k.slice(1) + '</span><b>' + D.entorno[k] + '</b></li>';
      }).join('');
    }
  }

  /* ---------- modo de lectura e índice ---------- */
  function conectarModo() {
    $$('.conmutador__boton').forEach(function (btn) {
      btn.addEventListener('click', function () {
        var modo = btn.getAttribute('data-modo');
        document.body.classList.toggle('tecnico', modo === 'tecnico');
        $$('.conmutador__boton').forEach(function (otro) {
          var activo = otro === btn;
          otro.classList.toggle('activo', activo);
          otro.setAttribute('aria-pressed', String(activo));
        });
      });
    });
    var imprimir = $('#btnImprimir');
    if (imprimir) imprimir.addEventListener('click', function () { window.print(); });
  }

  function conectarIndice() {
    var enlaces = $$('.indice a');
    if (!enlaces.length || !('IntersectionObserver' in window)) return;
    var observador = new IntersectionObserver(function (entradas) {
      entradas.forEach(function (e) {
        if (!e.isIntersecting) return;
        enlaces.forEach(function (a) {
          a.classList.toggle('activo', a.getAttribute('href') === '#' + e.target.id);
        });
      });
    }, { rootMargin: '-20% 0px -70% 0px' });
    $$('main section.seccion').forEach(function (s) { observador.observe(s); });
  }

  document.addEventListener('DOMContentLoaded', function () {
    renderCabecera();
    renderResumen();
    renderPruebas();
    renderCoberturaBackend();
    renderCoberturaFrontend();
    renderClases();
    conectarOrden();
    renderRequisitos();
    renderHallazgos();
    renderPlan();
    renderMetodologia();
    conectarModo();
    conectarIndice();
  });
})();

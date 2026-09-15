/* ==========================================================================
   Contenido cualitativo del informe (fallos, hallazgos, plan y metodología).
   Este archivo es de edición manual; los números viven en datos.js (generado).
   ========================================================================== */
window.FALLOS = [
  { suite: 'ItemCarritoResourceIT', test: 'createItemCarrito', esperado: '201 Created', obtenido: '400 Bad Request', linea: 150, estado: 'Resuelto' },
  { suite: 'ItemCarritoResourceIT', test: 'putExistingItemCarrito', esperado: '200 OK', obtenido: '400 Bad Request', linea: 297, estado: 'Resuelto' },
  { suite: 'ItemCarritoResourceIT', test: 'partialUpdateItemCarritoWithPatch', esperado: '200 OK', obtenido: '400 Bad Request', linea: 382, estado: 'Resuelto' },
  { suite: 'ItemCarritoResourceIT', test: 'fullUpdateItemCarritoWithPatch', esperado: '200 OK', obtenido: '400 Bad Request', linea: 412, estado: 'Resuelto' },
];

window.HALLAZGOS = [
  {
    id: 'H-01',
    sev: 'Alta',
    t: '4 pruebas de integración del carrito fallaban',
    d: 'Los ITs usaban un producto ficticio con precio del cliente. Se reescribieron con Producto/ProductoPrecio/Carrito reales y aserción del precio server-side.',
    impacto: 'Resuelto: ItemCarritoResourceIT 17/17 y suite de integración 485/485.',
    accion: 'Cerrado en la sesión del 2026-09-15.',
    estado: 'Resuelto',
  },
  {
    id: 'H-02',
    sev: 'Alta',
    t: 'verify fallaba en modernizer (61 violaciones)',
    d: 'Se reemplazaron los 61 usos de Optional.get() por orElseThrow() en 11 archivos de prueba.',
    impacto: 'Resuelto: el build completo corre sin -Dmodernizer.skip.',
    accion: 'Cerrado en la sesión del 2026-09-15.',
    estado: 'Resuelto',
  },
  {
    id: 'H-03',
    sev: 'Media',
    t: 'La cobertura del frontend medía solo lo que los tests importaban',
    d: 'Se configuró coverage.include sobre el código propio y se excluyó el generado por JHipster (entities/modules/shared). Ahora se miden 70 archivos propios, incluidas las páginas sin prueba (cuentan como 0%).',
    impacto: 'Resuelto: la cifra refleja la realidad y quedan listados los archivos sin cobertura.',
    accion: 'Cerrado en la sesión del 2026-09-15; agregar specs del panel es backlog.',
    estado: 'Resuelto',
  },
  {
    id: 'H-04',
    sev: 'Media',
    t: 'Umbrales de cobertura del frontend no se aplicaban',
    d: 'statements/branches/functions/lines estaban fuera del bloque thresholds. Se movieron a coverage.thresholds con la línea base real (45/35/40/45).',
    impacto: 'Resuelto: npm test ahora falla si la cobertura baja.',
    accion: 'Cerrado en la sesión del 2026-09-15.',
    estado: 'Resuelto',
  },
  {
    id: 'H-05',
    sev: 'Media',
    t: 'Cobertura de ramas baja (backend 40,8%)',
    d: 'Los caminos felices están cubiertos; faltan casos negativos y de decisión en PedidoResource (55%), EnvioResource (60%), PedidoServiceImpl (66%), ResourceAccessService (66%) e ItemCarrito.',
    impacto: 'Riesgo residual en casos borde; no bloquea el lanzamiento.',
    accion: 'Backlog post-lanzamiento: casos negativos y de decisión en esos 5 puntos.',
    estado: 'Abierto',
  },
  {
    id: 'H-06',
    sev: 'Baja',
    t: 'Documento de requisitos desactualizado',
    d: 'Se sincronizaron los 95 estados a Implementado y se anexaron RF-070→076 y RNF-027→031.',
    impacto: 'Resuelto: trazabilidad alineada con el código.',
    accion: 'Cerrado en la sesión del 2026-09-15.',
    estado: 'Resuelto',
  },
  {
    id: 'H-07',
    sev: 'Alta',
    t: 'El precio denormalizado no se sincronizaba por API (RF-072)',
    d: 'ProductoPrecioDTO no expone la relación inversa producto, así que precio_venta nunca se escribía al crear/editar productos o precios; el orden por precio quedaba sin efecto. Se corrigió en ProductoServiceImpl (al guardar) y ProductoPrecioServiceImpl (resolución inversa por precio.$id), con 2 pruebas unitarias nuevas.',
    impacto: 'Resuelto: los 777 productos del catálogo real quedaron con precio_venta y el orden asc/desc funciona en catálogo y búsqueda.',
    accion: 'Cerrado en la sesión del 2026-09-15.',
    estado: 'Resuelto',
  },
];

window.ACCIONES = [
  { p: 1, t: 'Reescribir ItemCarritoResourceIT (H-01)', imp: 'Alta', esf: 'Baja', r: '17/17 pruebas verdes', estado: 'Completado' },
  { p: 2, t: 'Corregir modernizer (H-02)', imp: 'Alta', esf: 'Baja', r: './mvnw verify completo', estado: 'Completado' },
  { p: 3, t: 'Cobertura real y umbrales Vitest (H-03/H-04)', imp: 'Alta', esf: 'Baja', r: 'Cobertura sobre código propio con control automático', estado: 'Completado' },
  { p: 4, t: 'Sincronizar requerimientos.md (H-06)', imp: 'Media', esf: 'Baja', r: '107/107 requisitos trazados', estado: 'Completado' },
  { p: 5, t: 'Hardening de producción', imp: 'Alta', esf: 'Media', r: 'Secretos por entorno, admin real, Mongo rs0, compose app-prod.yml e imagen 3.0.0', estado: 'Completado' },
  { p: 6, t: 'Corregir denormalización de precio (H-07)', imp: 'Alta', esf: 'Baja', r: 'Orden por precio server-side funcionando', estado: 'Completado' },
  { p: 7, t: 'Seed de catálogo real para producción', imp: 'Alta', esf: 'Media', r: '777 productos, 12 marcas, imágenes y precios reales', estado: 'Completado' },
  { p: 8, t: 'Specs de páginas del panel cliente/admin', imp: 'Media', esf: 'Media', r: 'Cobertura del panel (backlog post-lanzamiento)', estado: 'Pendiente' },
  { p: 9, t: 'Casos negativos en Pedido/Envío/ItemCarrito/ResourceAccess (H-05)', imp: 'Media', esf: 'Media', r: 'Ramas backend de 41% a ~60% (backlog)', estado: 'Pendiente' },
];

window.METODOLOGIA = {
  comandos: [
    './mvnw -Dskip.npm=true -Dspotless.check.skip=true -Dcheckstyle.skip=true verify  →  target/site/jacoco + jacoco-it',
    'java -jar org.jacoco.cli merge target/jacoco.exec target/jacoco-it.exec  →  target/site/jacoco-merged',
    './npmw test  →  target/test-results/lcov-report (código propio)',
    'python3 scripts/generar-informe-calidad.py  →  js/datos.js',
    './mvnw -ntp verify -DskipTests -Pprod -Djib.to.image=eljoseph12/knstore:3.0.0 jib:dockerBuild',
    'docker compose -f src/main/docker/app-prod.yml up -d  (EC2 + .env)',
  ],
  herramientas: 'JaCoCo 0.8.14 · Vitest 4.1.7 (coverage v8) · Testcontainers 2.0.5 (MongoDB 8.2.9) · JUnit 6 · Spring Boot 4.0.6',
};

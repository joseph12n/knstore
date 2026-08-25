# Sondeo JMeter del backend KN-Store

Este directorio contiene el plan de sondeo seguro del backend remoto.

## Plan

- `knstore-backend-sondeo.jmx`: plan generado desde Postman con 52 rutas y 136 operaciones.
- `knstore-role-collections.jmx`: plan maestro organizado por rol y método HTTP.
- `ejecutar-roles-produccion.sh`: ejecuta las colecciones de roles en producción.
- `generate-master-threadgroups.mjs`: regenerador del plan maestro con un `Thread Group` por ruta.
- `generate-role-collections.mjs`: regenerador de las colecciones por rol.
- `apis/`: 52 planes `.jmx`, uno por cada ruta de Postman.
- `generate-api-files.mjs`: regenerador de los planes individuales.
- `abrir-plan.sh`: abre el plan completo en la interfaz gráfica de JMeter.
- `ejecutar-sondeo.sh`: ejecuta el plan desde terminal y regenera resultados y gráficos.
- `ejecutar-crud-produccion.sh`: ejecuta el ciclo temporal `POST/PUT/DELETE` en producción.
- `abrir-graficos.sh`: abre el dashboard HTML del último sondeo.
- `resultados/`: archivos JTL y reporte HTML generados por cada ejecución.

El plan maestro por defecto muestra cinco colecciones de primer nivel:
`ROLE_ADMIN`, `ROLE_MANAGER`, `ROLE_CLIENTE_A`, `ROLE_CLIENTE_B` y `ROLE_USER`.
Dentro de cada rol se organizan carpetas `GET`, `POST`, `PUT`, `PATCH` y
`DELETE`, con cada operación etiquetada por ruta y `operationId`.

Cada API incluye los listeners `Summary Report`, `Response Time Graph` y `View
Results Tree`, para revisar resultados directamente después de presionar
Iniciar. Estos listeners son apropiados para pruebas funcionales y de
diagnóstico en la interfaz gráfica; para cargas grandes conviene usar el modo
terminal y el dashboard HTML.

## Ejecución recomendada

Desde la raíz del proyecto:

```bash
rm -rf performance/jmeter/resultados/sondeo-html
jmeter -n \
  -t performance/jmeter/knstore-backend-sondeo.jmx \
  -l performance/jmeter/resultados/sondeo.jtl \
  -e -o performance/jmeter/resultados/sondeo-html \
  -Jprotocol=https \
  -Jhost=app.knstore.duckdns.org \
  -Jusername=admin \
  -Jpassword=admin \
  -Jthreads=1 \
  -Jloops=1
```

## Accesos rápidos desde terminal

Desde `performance/jmeter/`:

```bash
./abrir-plan.sh
KNSTORE_JMETER_USERNAME=admin KNSTORE_JMETER_PASSWORD=admin ./ejecutar-sondeo.sh
./abrir-graficos.sh
```

La ejecución de roles, solo lectura y permisos, se puede guardar con:

```bash
KNSTORE_JMETER_PASSWORD=123456 ./ejecutar-roles-produccion.sh
```

Además, `jmeter` quedó configurado en `~/.local/bin` para que, sin
argumentos, abra automáticamente `knstore-role-collections.jmx`. Por tanto, en
cualquier terminal nueva basta ejecutar:

```bash
jmeter
```

Si una terminal ya estaba abierta antes de esta configuración, ejecuta
`source ~/.bashrc` una vez o abre una terminal nueva.

El primer comando abre todas las secciones cargadas en JMeter. El segundo
ejecuta el sondeo seguro y genera `resultados/sondeo.jtl` junto con el dashboard
`resultados/sondeo-html/index.html`. El tercero abre los gráficos del último
resultado.

Para repetir el ciclo CRUD controlado en producción:

```bash
KNSTORE_JMETER_PASSWORD=123456 \
KNSTORE_JMETER_SUFFIX=QRMNZXVT \
./ejecutar-crud-produccion.sh
```

El sufijo debe contener solo letras y ser diferente en cada ejecución. El plan
crea registros temporales, los actualiza y los elimina al final.

En la interfaz gráfica deben aparecer directamente cinco `Thread Groups` bajo
`KN-Store - Colecciones por rol`. Cada grupo representa un usuario y contiene
las carpetas de métodos HTTP de sus APIs.

Si necesitas abrir una API como archivo independiente, entra en `apis/` o
ejecuta, por ejemplo:

```bash
jmeter -t performance/jmeter/apis/25-productos.jmx
```

Para cambiar la intensidad sin editar el plan:

```bash
KNSTORE_JMETER_USERNAME=admin \
KNSTORE_JMETER_PASSWORD=admin \
KNSTORE_JMETER_THREADS=2 \
KNSTORE_JMETER_LOOPS=3 \
./ejecutar-sondeo.sh
```

Para una carga controlada se pueden aumentar `threads` y `loops`. El plan usa
una sola iteración por hilo por defecto para evitar generar pedidos, pagos,
facturas o cambios de inventario en producción.

## Seguridad y alcance

- Las credenciales se reciben como propiedades de JMeter y no están escritas en
  el plan.
- Las operaciones de lectura y el `POST /api/pedidos/preview` son las únicas
  acciones ejecutadas por defecto.
- Registro, checkout, reembolsos, callbacks, cambios de estado, CRUD y borrados
  no se ejecutan porque modifican datos persistentes.
- La búsqueda de productos se valida esperando `200`; si el backend devuelve
  `5xx`, el reporte la marca como fallo para facilitar el diagnóstico.
- No se requiere `sudo` para ejecutar JMeter.

## Interpretación

El reporte HTML muestra cada solicitud con el prefijo de su sección. Revisar
especialmente `Error %`, `Throughput`, `Average`, `90th/95th/99th percentile`
y los detalles de las solicitudes fallidas.

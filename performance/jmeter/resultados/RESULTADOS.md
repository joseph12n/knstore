# Resultado del sondeo backend

Fecha de ejecución: 17 de agosto de 2026 14:33 COT  
Servidor: `https://app.knstore.duckdns.org`  
Plan: `../knstore-backend-sondeo.jmx`  
Fuente: `postman/KnstoreApi.json`  
Carga: 1 hilo, 1 iteración

## Cobertura cargada en JMeter

| Elemento | Cantidad |
| --- | ---: |
| Rutas API visibles | 52 |
| Operaciones Postman visibles | 136 |
| Operaciones seguras activas por defecto | 24 |
| Operaciones deshabilitadas por seguridad o precondiciones | 112 |

Cada ruta está agrupada como un `Thread Group` de primer nivel llamado
`API NN - /api/...` y cada método aparece como una solicitud independiente
debajo de ese grupo.

## Ejecución segura

| Métrica | Resultado |
| --- | ---: |
| Solicitudes ejecutadas | 76 |
| Respuestas esperadas | 76 |
| Fallos | 0 |
| Error rate JMeter | 0% |
| Tiempo medio global | 2219 ms |
| Mínimo | 103 ms |
| Máximo | 3376 ms |
| Throughput observado | 18.2 req/s |

La ejecución lanzó 52 grupos en paralelo. Cada grupo autenticó la sesión y las
24 operaciones `GET` seguras activas se ejecutaron cuando correspondía. También
se verificó `GET /api/authenticate` con `204` y el resto de respuestas esperadas
`200`.

## Operaciones deshabilitadas

Las 112 operaciones restantes permanecen cargadas y seleccionables en JMeter,
pero deshabilitadas por defecto porque requieren una de estas condiciones:

- Identificadores reales de recursos.
- Cuerpos JSON específicos.
- Usuario cliente con `Cuenta`, dirección y carrito.
- Creación, actualización, checkout, pagos, reembolsos o borrados.
- Claves de activación o tokens de recuperación.

Para probar una operación individual, selecciona el `Thread Group` `API NN -
/api/...`, habilita el método concreto y ajusta las variables globales antes de
ejecutar.

## Archivos

- `sondeo.jtl`: resultados detallados de la ejecución segura.
- `sondeo-html/index.html`: dashboard HTML con gráficos.
- `sondeo-html/statistics.json`: estadísticas por operación.

El archivo `postman/KnstoreApi.json` contiene la fuente exacta usada para
generar el plan. Si se actualiza en Postman, regenerar el árbol con:

```bash
node performance/jmeter/generate-master-threadgroups.mjs
node performance/jmeter/generate-api-files.mjs
```

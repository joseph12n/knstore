# APIs individuales de KN-Store

Esta carpeta contiene un archivo `.jmx` por cada ruta de
`postman/KnstoreApi.json`.

- 52 archivos JMeter.
- 136 operaciones Postman distribuidas en esos archivos.
- Las lecturas seguras se habilitan por defecto.
- Escrituras, IDs no configurados y flujos con precondiciones quedan deshabilitados.
- Cada archivo incluye `Summary Report`, `Response Time Graph` y `View Results Tree`.

## Abrir una API

Desde la raíz del proyecto:

```bash
jmeter -t performance/jmeter/apis/25-productos.jmx
```

También se puede abrir cualquier archivo desde la interfaz de JMeter en:

```text
performance/jmeter/apis/
```

El plan maestro con todas las carpetas continúa en:

```text
performance/jmeter/knstore-backend-sondeo.jmx
```

Para regenerar estos archivos después de actualizar el JSON de Postman:

```bash
node performance/jmeter/generate-api-files.mjs
```

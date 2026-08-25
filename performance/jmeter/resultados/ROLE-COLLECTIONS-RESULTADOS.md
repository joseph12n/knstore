# Resultado colecciones por rol en producción

Fecha de ejecución: 22 de agosto de 2026 13:52 COT  
Servidor: `https://app.knstore.duckdns.org`  
Plan: `../knstore-role-collections.jmx`  
Credenciales: `jmeter_*` con contraseña recibida por propiedad `-Jpassword`

## Resumen

| Métrica | Resultado |
| --- | ---: |
| Solicitudes ejecutadas | 119 |
| Respuestas esperadas | 119 |
| Fallos | 0 |
| Error rate | 0% |
| Tiempo promedio | 155 ms |
| Mínimo | 77 ms |
| Máximo | 1263 ms |
| Throughput | 24.0 req/s |

## Estructura

El plan abre cinco colecciones de primer nivel, una por rol:

- `ROLE_ADMIN - jmeter_admin`
- `ROLE_MANAGER - jmeter_manager`
- `ROLE_CLIENTE_A - jmeter_cliente_a`
- `ROLE_CLIENTE_B - jmeter_cliente_b`
- `ROLE_USER - jmeter_user`

Cada colección contiene carpetas por método HTTP con las operaciones
permitidas para ese rol:

- `GET` con sus consultas.
- `POST`, `PUT`, `PATCH` y `DELETE` con los métodos expuestos.
- Las operaciones que requieren datos o modificaciones quedan deshabilitadas
  por defecto para evitar contaminación.

## Verificaciones de permisos

- `ROLE_ADMIN` accede a todo.
- `ROLE_MANAGER` accede a operaciones administrativas de negocio y recibe
  `403` en endpoints exclusivos de administrador (`/api/admin/**`,
  `/api/authorities`).
- `ROLE_CLIENTE_A` y `ROLE_CLIENTE_B` acceden a catálogo público y a sus
  propios recursos; reciben `403` en recursos administrativos de catálogo
  interno.
- `ROLE_USER` solo puede acceder a catálogo público, cuenta propia y
  autenticación; recibe `403` en el resto.

## Archivos

- `role-collections.jtl`: resultados detallados.
- `role-collections-html/index.html`: dashboard HTML con gráficos.

## Repetir

```bash
KNSTORE_JMETER_PASSWORD=123456 ./ejecutar-roles-produccion.sh
```

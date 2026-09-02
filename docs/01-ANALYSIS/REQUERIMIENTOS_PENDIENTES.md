# Requerimientos pendientes y deuda técnica (KN-Store)

> **Origen:** auditoría de código del 2026-08-22 (seguridad, buscador, rendimiento y funcionalidad).
> **Convención:** continúa la numeración desde el último requerimiento implementado (RF-069).
> **Última actualización:** 2026-08-24 — barrido completo de pendientes implementado (RF-072→076, RNF-027→031).

---

## 1. Resumen de estados

| ID     | Requerimiento                                                                 | Prioridad | Estado      |
| ------ | ----------------------------------------------------------------------------- | --------- | ----------- |
| RF-070 | Filtros server-side en el buscador (categoría/marca)                          | Alta      | ✅ Resuelto |
| RF-071 | Corregir búsqueda por marca (`@DBRef` no consultable)                         | Crítica   | ✅ Resuelto |
| RF-072 | Ordenamiento por precio en servidor (búsqueda y catálogo)                     | Media     | ✅ Resuelto |
| RF-073 | Completar edición del perfil propio                                           | Media     | ✅ Resuelto |
| RF-074 | Corregir edición de Cuenta desde panel admin (`user` nulo en PUT)             | Crítica   | ✅ Resuelto |
| RF-075 | Reintento de pago desde UI (`OrderDetailPage`)                                | Media     | ✅ Resuelto |
| RF-076 | Incluir pago en resultado del checkout y eliminar llamada redundante          | Baja      | ✅ Resuelto |
| RNF-027| Índice de texto MongoDB para búsquedas                                        | Alta      | ✅ Resuelto |
| RNF-028| Eliminar consultas N+1                                                        | Alta      | ✅ Resuelto |
| RNF-029| Paginación real de productos del carrito                                      | Media     | ✅ Resuelto |
| RNF-030| Consecutivos diarios atómicos para pedidos/facturas                           | Media     | ✅ Resuelto |
| RNF-031| Restringir `/management/prometheus` en producción                             | Alta      | ✅ Resuelto |

---

## 2. Detalle

### RF-070 — Filtros server-side en el buscador ✅ Resuelto (2026-08-22)

**Problema:** `SearchPage.tsx` filtraba categoría, marca y ordenaba solo sobre los resultados de la página actual (≤ tamaño de página). Consecuencias: totales inconsistentes con filtros activos, paginación rota y "0 resultados" fantasma aunque hubiera coincidencias en otras páginas.

**Solución aplicada:** el endpoint `GET /api/productos/search` acepta ahora `categoriaId` y `marcaId` opcionales; el servicio construye criterios dinámicos con `MongoTemplate` y el frontend envía los filtros en la URL. Se eliminó el filtrado client-side.

### RF-071 — Corregir búsqueda por marca ✅ Resuelto (2026-08-22)

**Problema:** la consulta `{'marca.nombre': {$regex}}` nunca coincidía porque `Producto.marca` es `@DBRef` (en MongoDB solo se persiste `{$ref, $id}`); buscar "Nike" devolvía resultados vacíos sin error aparente.

**Solución aplicada:** `MarcaRepository.findByNombreRegex` resuelve primero las marcas cuyo nombre coincide; sus ids se combinan vía `'marca.$id'` dentro del `$or` de texto (nombre, descripción, sku, marca).

### RF-072 — Ordenamiento por precio en servidor ✅ Resuelto (2026-08-24)

**Problema:** el precio vive en `ProductoPrecio` (`@DBRef`), por lo que ordenar por `precio.precioVenta` requería agregación (`$lookup`). El orden por precio se aplicaba client-side solo dentro de la página obtenida.

**Solución aplicada (denormalización):**
- `Producto` ahora persiste `precio_venta` (BigDecimal, 2 decimales vía `MoneyUtils`) denormalizado; `sort=precioVenta,asc|desc` funciona directamente en `/api/productos` y `/api/productos/search`.
- `ProductoPrecioServiceImpl.guardarConTotales` sincroniza el producto asociado en `save/update/partialUpdate`.
- Migración Mongock `ProductoIndexesMigration` (order `007`): backfill idempotente de `precio_venta` sobre productos existentes.
- Frontend: `SearchPage` y `CategoryPage` envían el sort al servidor; eliminado todo ordenamiento client-side.

### RF-073 — Completar edición del perfil propio ✅ Resuelto (2026-08-24)

**Corresponde al parcial RF-033.** Backend: `ResourceAccessService.canAccessCuentaDto` ahora permite a `CLIENTE` editar su propia cuenta enviando el `id` (con `user` nulo, anti mass-assignment) sin exigir `user.login`; el PUT/PATCH propio con `@PreAuthorize` ya existía. Frontend: `ProfilePage` guarda solo campos editables (sin `user`), muestra errores con `getApiErrorMessage` y refresca la cuenta tras guardar; el email sigue no editable, validaciones de fecha/género/teléfono intactas.

### RF-074 — Corregir edición de Cuenta desde panel admin ✅ Resuelto (2026-08-22)

**Causa raíz (cadena completa):**
1. `cuenta-update.tsx:48` carga usuarios con `getUsers({})` → `GET api/users` **paginado** con tamaño por defecto (20).
2. En `saveEntity`, para ADMIN/MANAGER el usuario se resuelve con `users.find(...)`; si la cuenta pertenece a un usuario fuera de esa primera página, `find` devuelve `undefined`.
3. `cleanEntity` elimina claves `undefined` del payload → el PUT llega sin `user`.
4. `CuentaDTO.user` tenía `@NotNull` → Spring rechaza la petición con 400 antes de llegar al servicio. Mismo riesgo latente para `tipoDocumento`.

**Solución aplicada (defensa en 3 capas):**
- Backend (`CuentaServiceImpl.update`): las relaciones `user` y `tipoDocumento` ya no son editables por PUT — si llegan nulas se preservan las existentes (anti mass-assignment).
- Backend (`CuentaDTO`): se retira `@NotNull` de los campos de relación; la obligatoriedad en creación se valida explícitamente en `CuentaResource.createCuenta` (`userrequerido`, `tipodocumentorequerido`).
- Frontend (`cuenta-update.tsx`): si el usuario/tipoDocumento no está en la lista cargada, se hace fallback a `cuentaEntity` en edición en lugar de enviar nulo.

### RF-075 — Reintento de pago desde UI ✅ Resuelto (2026-08-24)

**Solución aplicada:** en `OrderDetailPage` se agregó el botón "Pagar ahora" visible cuando `pedido.estado === 'PENDING'` y el pago no está aprobado (`!pago || REJECTED || PENDING`); llama `POST /api/pagos/iniciar` (idempotente), recarga el pedido y muestra toast de éxito o error.

### RF-076 — Pago en el resultado del checkout ✅ Resuelto (2026-08-24)

**Solución aplicada:** `CheckoutResultDTO` incluye ahora `PagoDTO pago` (ya resuelto dentro de la transacción del checkout); `CheckoutPage` consume `result.pago` y elimina la llamada redundante a `POST /api/pagos/iniciar` (queda solo como fallback defensivo si el pago viniera nulo).

### RNF-027 — Índice de texto MongoDB ✅ Resuelto (2026-08-24)

**Solución aplicada:** `ProductoIndexesMigration` (order `007`) crea índice de texto `producto_search_text` sobre `nombre`, `descripcion` y `sku` con rollback. La búsqueda actual sigue con `$regex` (documentado en `searchActive`); el índice queda disponible para consultas de texto nativas.

### RNF-028 — Eliminar consultas N+1 ✅ Resuelto (2026-08-24)

**Solución aplicada:**

| Zona | Antes | Ahora |
| --- | --- | --- |
| `Pago/Envio/Factura.findAll` (CLIENTE) | 1 query + N por pedido/pago + `InMemoryPageUtils` | `findByPedidoIdIn`/`findByPagoIdIn` paginados reales (3 consultas constantes) |
| `findOne` (CLIENTE) de Pago/Envio/Factura | Recorría todas las listas | 2-3 consultas constantes (recurso → padre → cuenta) |
| `ResourceAccessService` (5 métodos `canAccess*Id`) | Loops sobre listas por elemento (N+1/N²) | Escaneo ascendente a número constante de consultas |
| `ProductoServiceImpl.loadRelationships`/`loadImages` | ~7 consultas por producto | 1 `findByIdIn` por tipo de referencia + 1 `findByProductoIdIn` |

- `InMemoryPageUtils` fue **eliminado** (sin usos restantes).
- Detalle clave MongoDB: los `@DBRef` se persisten con `$id` como `ObjectId`; las consultas por lote usan `@Query("{ 'ref.$id': { $in: ?0 } }")` con `Collection<ObjectId>` (ver `service/util/MongoIdUtils.java`). La query derivada `findByXIdIn` con `String` funciona para igualdad pero **no** para `$in` (no matchea tipos).

### RNF-029 — Paginación real de productos del carrito ✅ Resuelto (2026-08-24)

**Solución aplicada:** nuevo endpoint público `GET /api/productos/por-ids?ids=a,b,c` (máx. 200 ids → 400 si excede) con `ProductoService.findAllByIds`, que resuelve relaciones e imágenes en lote. `CartContext` reemplazó `api/productos?size=1000` por una llamada a `por-ids` con los ids de los ítems del carrito (orden preservado, sin llamada si no hay ítems). El carrito autenticado ya no descarga el catálogo completo.

### RNF-030 — Consecutivos diarios atómicos ✅ Resuelto (2026-08-24)

**Solución aplicada:** colección `secuencias` con documento `{_id: "<tipo>-<yyyyMMdd>", tipo, fecha, seq}`; `SecuenciaService.siguiente(tipo, fecha)` usa `findAndModify` con `$inc` + upsert + `returnNew` (atómico): `Update` incluye `set("tipo")`/`set("fecha")` porque el upsert solo escribe campos del Update (sin esto el índice único `(tipo, fecha)` duplicaba claves nulas con `E11000`). `CheckoutService`, `PedidoServiceImpl` y `FacturaPdfService` delegan en el servicio (se eliminaron las 3 implementaciones duplicadas `pedido_sequence`/`factura_sequence`); los formatos de salida no cambian (`PED-aaaaMMdd-000001`, `FE-000001`). Migración `SecuenciasIndexMigration` (order `008`): índice único `(tipo, fecha)` con rollback.

### RNF-031 — Restringir `/management/prometheus` ✅ Resuelto (2026-08-24)

**Solución aplicada:** se eliminó el `permitAll()` de `/management/prometheus` en `SecurityConfiguration`; ahora lo captura el matcher genérico `/management/**` con `ADMIN`/`MANAGER`. `application-prod.yml` ya deshabilita el export prometheus y se documentó que la exposición HTTP queda protegida (scrape externo no autenticado prohibido). Verificado: sin token → `401`.

---

## 3. Deuda técnica conocida (no resuelta / preexistente)

| Item | Descripción | Estado |
| --- | --- | --- |
| `ItemCarritoResourceIT` (4 tests CRUD) | ITs autogenerados que postean productos ficticios (`fixed-id-for-tests`); devuelven `400 error.itemcarritoinvalido` desde que se blindó el precio server-side en `ItemCarritoServiceImpl` (commit `da066aa`). Preexistentes, ajenos a la sesión 2026-08-24. Cuando se resuelvan los RF-072…RNF-031, estos ITs siguen fallando hasta que se ajusten a dados reales | ⚠️ Preexistente |
| Contadores viejos `pedido_sequence`/`factura_sequence` | Quedan datos huérfanos en BD tras RNF-030 (los consecutivos reinician en `secuencias`; sin impacto en formato) | Cleanup opcional |
| Consecutivos históricos | Pedidos/facturas ya emitidos conservan su número; los nuevos se generan desde la colección `secuencias` | Verificado |

---

## 4. Registro de cambios asociados (2026-08-24)

| Archivo | Cambio |
| --- | --- |
| `domain/Producto.java` | Campo denormalizado `precioVenta` (`@Field("precio_venta")`) — RF-072 |
| `domain/Secuencia.java` | NUEVO: documento de contador diario (`secuencias`) — RNF-030 |
| `repository/{Pago,Envio,Factura}Repository.java` | `@Query("{ 'ref.$id': { $in: ?0 } }")` por lote — RNF-028 |
| `repository/Producto/Derivados*Repository.java` | `findByIdIn` / `findByProductoIdIn` batch — RNF-028 |
| `service/impl/ProductoPrecioServiceImpl.java` | Sincroniza `producto.precio_venta` al guardar — RF-072 |
| `service/impl/ProductoServiceImpl.java` | Resolución por lote (relaciones + imágenes), `findAllByIds` — RNF-028/029 |
| `service/ProductoService.java` / `web/rest/ProductoResource.java` | `findAllByIds` + `GET /api/productos/por-ids` — RNF-029 |
| `service/SecuenciaService(.java/ServiceImpl)` | NUEVO contador atómico — RNF-030 |
| `service/CheckoutService.java` / `PedidoServiceImpl` / `FacturaPdfService` | Delegan consecutivos; `CheckoutResultDTO.pago` — RNF-030/RF-076 |
| `service/dto/CheckoutResultDTO.java` | Campo `PagoDTO pago` — RF-076 |
| `service/util/MongoIdUtils.java` | NUEVO: conversión String→ObjectId para consultas `$id` — RNF-028 |
| `service/util/InMemoryPageUtils.java` | ELIMINADO (sin usos) — RNF-028 |
| `service/ResourceAccessService.java` | Ownership a consultas constantes; `canAccessCuentaDto` permite edición propia — RNF-028/RF-073 |
| `service/impl/{Pago,Envio,Factura}ServiceImpl.java` | Listados CLIENTE por lote paginado — RNF-028 |
| `config/SecurityConfiguration.java` | `/management/prometheus` protegido — RNF-031 |
| `config/dbmigrations/ProductoIndexesMigration.java` | NUEVO order `007`: índice de texto + backfill `precio_venta` — RNF-027/RF-072 |
| `config/dbmigrations/SecuenciasIndexMigration.java` | NUEVO order `008`: índice único `(tipo, fecha)` — RNF-030 |
| `landing/pages/SearchPage.tsx` / `CategoryPage.tsx` | Orden server-side, sin filtrado client-side — RF-072 |
| `landing/pages/CheckoutPage.tsx` / `services/checkout.service.ts` | `result.pago`, sin llamada redundante — RF-076 |
| `landing/pages/OrderDetailPage.tsx` | Botón "Pagar ahora" — RF-075 |
| `landing/pages/ProfilePage.tsx` | PUT propio sin `user`, errores y refresco — RF-073 |
| `landing/context/CartContext.tsx` | `por-ids` en vez de catálogo completo — RNF-029 |
| Tests | `ProductoServiceImplTest`, `SecuenciaServiceImplTest`, `ResourceAccessServiceTest`/`Pago`/`Envio`/`Factura` unit, `ProductoResourceIT`, `CheckoutServiceIT`, `ListadosPropiosResourceIT`, `PagoFlujoResourceIT` y specs frontend (Search/Category/OrderDetail/Checkout/CartContext) |

**Nota (2026-08-22):** cambios de RF-070/071/074 (buscador, marca, cuenta admin) quedaron en el árbol sin commitear; todavía no hay commit de todo el barrido 2026-08-24.

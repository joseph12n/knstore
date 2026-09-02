# AGENTS.md — Contexto para asistentes de código (KN-Store)

> **Propósito:** Este archivo centraliza el contexto del proyecto para agentes de IA. Debe mantenerse actualizado cada vez que cambien arquitectura, flujo de trabajo, convenciones o decisiones de negocio relevantes.

---

## 1. Visión general del proyecto

**KN-Store** es una plataforma de e-commerce completa para la tienda KN_STORE. Busca migrar la operación manual (pedidos por WhatsApp, inventarios en hojas) a un ecosistema digital integrado, seguro y escalable.

- **Generador base:** JHipster 9.1.0
- **Backend:** Spring Boot 4.0.6, Java 21
- **Frontend:** React 19, TypeScript, Redux Toolkit, React Router 7, Bootstrap 5 / React-Bootstrap (tema Bootswatch Cyborg)
- **Base de datos:** MongoDB con Mongock para migraciones
- **Seguridad:** Spring Security + JWT (OAuth2 Resource Server)
- **Build:** Maven 3.2.5+ (backend), Webpack 5 / Vitest (frontend), Node 24.16.0
- **Empaquetado:** JAR ejecutable con Jib (Docker)

---

## 2. Objetivos del sistema

- **Objetivo general:** Gestionar ventas, inventario y usuarios mediante módulos integrados que faciliten la administración interna y mejoren la experiencia de compra.
- **Objetivos específicos:**
  - Diseñar la arquitectura y módulos del sistema.
  - Implementar una tienda online (catálogo, pedidos, compras).
  - Centralizar el control de usuarios e inventario en tiempo real.
  - Garantizar seguridad y roles de acceso diferenciados.

---

## 3. Módulos principales

| Módulo                       | Descripción                                                               |
| ---------------------------- | ------------------------------------------------------------------------- |
| Visualización de productos   | Catálogo público con filtros, búsqueda, paginación y detalle de producto. |
| Control de inventario        | Panel administrativo para calzado por color, talla, marca y referencia.   |
| Distribución de productos    | Gestión de entregas: contraentrega o paquetería convencional.             |
| Servicios                    | Administración de servicios complementarios de la tienda.                 |
| Notificaciones (SMTP)        | Envío automático de correos transaccionales.                              |
| Autenticación y sesión       | Registro, login, JWT, protección de rutas.                                |
| Gestión de usuarios          | CRUD de usuarios con roles.                                               |
| Panel de cliente (`/cuenta`) | Perfil, direcciones, pedidos, pagos, envíos, facturas, seguridad.         |
| Carrito y pedidos            | Carrito local/server, checkout, cancelación.                              |
| Pagos, envíos y facturación  | Flujo de post-venta (parcialmente implementado, ver RF pendientes).       |

---

## 4. Arquitectura

### 4.1 Backend (Spring Boot)

```
src/main/java/com/mycompany/knstore/
├── config/                 # Configuración de Spring
├── domain/                 # Entidades MongoDB (@Document)
├── domain/enumeration/     # Enums de negocio
├── repository/             # Repositorios Spring Data MongoDB
├── service/                # Interfaces de servicio
├── service/impl/           # Implementaciones
├── service/dto/            # DTOs generados por MapStruct
├── service/mapper/         # Mapeadores MapStruct
├── web/rest/               # Controladores REST
├── web/rest/errors/        # Manejo centralizado de errores
├── web/filter/             # Filtros (SPA web filter)
├── security/               # Utilidades JWT y roles
└── aop/logging/            # Aspectos de logging
```

- API REST stateless bajo `/api/**`.
- Patrón DTO + MapStruct para desacoplar la API del modelo de persistencia.
- Repository pattern con Spring Data MongoDB.
- Seguridad declarativa con `@PreAuthorize`.

### 4.2 Frontend (React + TypeScript)

```
src/main/webapp/app/
├── app.tsx                 # Punto de entrada y lógica de layout dual
├── routes.tsx              # Rutas (landing + dashboard + auth)
├── dashboard/              # Wrapper del panel administrativo
├── config/                 # Redux Toolkit, dayjs
├── entities/               # CRUDs generados por JHipster (NO MOVER)
├── modules/                # Módulos JHipster (NO MOVER)
├── shared/                 # Componentes/utilidades JHipster (NO MOVER)
└── landing/                # Tienda pública y panel de cliente
    ├── components/
    ├── pages/
    ├── hooks/              # useCart, useCatalog
    ├── model/
    ├── routes/
    ├── styles/
    └── utils/
```

- **Layout dual:** `app.tsx` decide entre `LandingLayout` (tienda), `AccountLayout` (`/cuenta`) y layout clásico JHipster (admin/CRUD).
- **Enrutamiento con Outlet:** `LandingLayout` carga catálogo y carrito una vez y renderiza `<Outlet />`.
- **Estado global:** Redux Toolkit (auth, perfil, entidades). Carrito anónimo en `localStorage` (`knstore-cart`).
- **Responsividad:** Obligatoria desde 360px hasta 1920px.

### 4.3 Modelo de dominio destacado

- **Catálogo:** `Categoria`, `Subcategoria`, `Marca`, `Producto`, `ProductoPrecio`, `ProductoInventario`, `ProductoImagen`, `EtiquetaProducto`, `CategoriaIVA`.
- **Clientes:** `User` (JHipster) + `Cuenta` (perfil extendido). Sin `Cuenta` no hay direcciones ni pedidos.
- **Pedidos:** `Pedido`, `ItemPedido`, `Pago`, `Envio`, `Factura`.
- **Carrito:** `Carrito`, `ItemCarrito`.
- Relaciones principales: `Cuenta` 1:1 `User`, `Carrito` 1:1 `Cuenta`, `Pedido` 1:1 `Direccion`, `Pedido` 1:1 `Envio`, `Producto` 1:1 `ProductoPrecio`/`ProductoInventario`, `Producto` 1:N `ProductoImagen`.

---

## 5. Roles de seguridad

| Rol            | Descripción                                                                   |
| -------------- | ----------------------------------------------------------------------------- |
| `ROLE_ADMIN`   | Acceso total: administración, usuarios, configuraciones críticas, auditoría.  |
| `ROLE_MANAGER` | Acceso operativo: inventario, servicios, distribución, catálogo.              |
| `ROLE_CLIENTE` | Cliente de la tienda: catálogo, carrito, pedidos propios, perfil (`/cuenta`). |
| `ROLE_USER`    | Usuario genérico; sin acceso a entidades de negocio protegidas.               |

### 5.1 Permisos por ruta (frontend)

| Ruta                                                                   | Acceso                                      |
| ---------------------------------------------------------------------- | ------------------------------------------- |
| `/`, `/categorias`, `/productos`, `/buscar`                            | Público                                     |
| `/carrito`                                                             | Público (local/anónimo)                     |
| `/checkout`                                                            | Autenticado (ADMIN, MANAGER, CLIENTE, USER) |
| `/cuenta/*`                                                            | Autenticado (panel propio del cliente)      |
| `/admin/*`                                                             | ADMIN, MANAGER                              |
| `/account/settings`, `/account/password`                               | ADMIN, MANAGER                              |
| `/login`, `/account/register`, `/account/activate`, `/account/reset/*` | Público                                     |

### 5.2 Ownership de recursos

Para `Cuenta`, `Direccion`, `Carrito`, `Pedido`, `ItemCarrito`, `ItemPedido`, `Pago`, `Envio`, `Factura`:

- `ADMIN`/`MANAGER`: acceso total a recursos de cualquier cliente.
- `CLIENTE`: solo puede leer/escribir/borrar sus propios recursos.
- `USER`: acceso denegado a endpoints protegidos.

### 5.3 Endpoints de negocio (seguridad por diseño)

- `POST /api/pagos/callback` es **server-to-server**: solo `ADMIN`/`MANAGER` (la pasarela notifica). El cliente paga vía `POST /api/pagos/iniciar`, que con la pasarela simulada auto-aprueba en el servidor.
- `POST /api/pedidos/{id}/cancelar`: cancelación con máquina de estados + restauración de stock, disponible para el propietario y administración. Los PATCH/PUT de `Pedido`/`Pago`/`Carrito`/`Factura`/`Envio`/`ItemPedido` son solo `ADMIN`/`MANAGER` (anti mass-assignment).
- **Precios siempre server-side**: checkout e `ItemCarrito` ignoran cualquier `precioUnitario` del cliente; se resuelven desde `Producto.precio.precioVenta` en BD.

---

## 6. Requerimientos funcionales (resumen)

### Implementados

- RF-001 a RF-036: autenticación, gestión de usuarios, categorías, subcategorías, productos, catálogo público, panel admin.
- RF-037 a RF-041: CRUD de direcciones propias con dirección predeterminada atómica (`PATCH /api/direccions/{id}/predeterminada`).
- RF-042 a RF-046: carrito (agregar, consultar, modificar, eliminar, vaciar) con recálculo automático de subtotales.
- RF-047 a RF-053: checkout atómico con preview de totales, reglas de envío (gratis ≥ $150.000), listado/detalle de pedidos y cancelación con máquina de estados.
- RF-054 a RF-069: pasarela de pagos abstracta (simulada configurable), callback idempotente, reembolsos, facturación real con consecutivo, PDF con QR y envío por correo; endpoints de operación de envíos (tracking, estado, devolución, pendientes).
- RF-070 a RF-076: filtros server-side en buscador, búsqueda por marca, orden por precio server-side (`Producto.precioVenta` denormalizado), edición del perfil propio (RF-033 completado), pago en resultado del checkout y botón "Pagar ahora". Detalle en `docs/REQUERIMIENTOS_PENDIENTES.md`.
- RNF-027 a RNF-031: índice de texto MongoDB, eliminación de N+1 (listados CLIENTE, ownership y catálogo por lotes), endpoint `GET /api/productos/por-ids` para el carrito, consecutivos diarios atómicos (colección `secuencias`, `SecuenciaService`) y `/management/prometheus` protegido.
- Seed automático del catálogo en desarrollo (`knstore.seed.catalog=true`).
- MongoDB en replica set para transacciones reales.

### Pendientes / parciales

| ID  | Requerimiento                                               | Estado |
| --- | ----------------------------------------------------------- | ------ |
| —   | Barrido RF-072→RNF-031 implementado 2026-08-24 (sin commit) | ✅     |

\* _Pendientes conocidos de calidad (backlog): `ItemCarritoResourceIT` (4 tests CRUD autogenerados) fallan preexistentes desde que se blindó el precio server-side (productos ficticios, `400 error.itemcarritoinvalido`); contadores viejos `pedido_sequence`/`factura_sequence` huérfanos (cleanup opcional). Detalle completo en `docs/REQUERIMIENTOS_PENDIENTES.md`._

---

## 7. Requerimientos no funcionales (resumen)

### Implementados

- Seguridad: bcrypt costo ≥10, JWT 30 días, validación de entradas, control de roles, eliminación lógica por defecto, CORS restringido.
- Rendimiento: catálogo <500ms P95, paginación obligatoria, índices MongoDB.
- Usabilidad: responsive, animaciones ≥50 FPS, tokens CSS, mensajes de error claros.
- Mantenibilidad: separación de capas, convenciones de nomenclatura, documentación sincronizada, compatibilidad de navegadores, Node LTS.
- Disponibilidad/escalabilidad: 99.5% mensual, manejo robusto de errores, backend stateless, capacidad 100k productos.
- Atomicidad del checkout (RNF-023) y concurrencia de stock sin sobreventa (RNF-024): verificadas con Testcontainers sobre replica set.
- Auditoría de pagos y pedidos (RNF-025): historial de transiciones de estado consultable por propietario y administración.
- Precisión monetaria (RNF-026): todos los valores monetarios se normalizan a 2 decimales con `MoneyUtils`.

---

## 8. Convenciones de desarrollo

### 8.1 Commits (Conventional Commits)

```text
<tipo>(<alcance>): <descripción corta en minúsculas y en español>
```

**Tipos:** `feat`, `fix`, `chore`, `refactor`, `docs`, `style`, `test`.

**Alcances:** `backend`, `frontend`, `config`, `webpack`, `vite`, `docs`.

**Reglas:**

- Descripción en minúsculas y español.
- Tiempo imperativo/infinitivo (ej. `configurar`, `permitir`).
- Sin punto final.

**Ejemplos:**

```text
feat(backend): permitir a CLIENTE crear su propia Cuenta con validación
chore(config): configurar CORS y URL base para producción
fix(webpack): usar URL relativa para API en producción
refactor(frontend): reorganizar storefront como landing y agregar dashboard wrapper
docs: documentar arquitectura, cambios y requerimientos del proyecto
```

### 8.2 Flujo de trabajo en Git

- Trabajar en ramas dedicadas (`feature/modulo-inventario`, `bugfix/error-login`).
- Commits atómicos.
- Validación local antes de push: backend compila, TypeScript sin errores.
- Pull Requests con revisión de al menos un compañero.

### 8.3 Código

- **Backend:** respetar capas Controller → Service → Repository. Usar DTOs y MapStruct. Usar `@PreAuthorize` para seguridad.
- **Frontend:** no mover código autogenerado por JHipster (`entities/`, `modules/`, `shared/`). El código personalizado va en `landing/` y `dashboard/`.
- **Nomenclatura:** camelCase, PascalCase y kebab-case según estándar del proyecto.

---

## 9. Comandos de build y validación

### Backend

```bash
./mvnw compile                         # Compilar
./mvnw -Pprod clean verify             # JAR de producción
./mvnw -Pprod,war clean verify         # WAR
./mvnw -q -DskipITs=false test         # Tests
./mvnw -q -Dtest=ResourceAccessServiceTest test  # Test de ownership
```

### Frontend

```bash
./npmw run start                       # Dev server Webpack
./npmw run webapp:prod                 # Build producción
./npmw run webapp:build                # Build (sin perfil prod)
./npmw test                            # Tests Vitest
./npmw run lint                        # Linter
npx tsc --noEmit                       # Chequeo de tipos
```

### Desarrollo simultáneo

```bash
./npmw run backend:start               # Spring Boot
./npmw run start                       # Webpack dev server
```

### Docker

```bash
docker compose -f src/main/docker/services.yml up   # MongoDB + auxiliares
docker compose -f src/main/docker/app.yml up        # App completa
npm run java:docker                                 # Imagen con Jib
```

---

## 10. Decisiones arquitectónicas clave

1. **Monolito JHipster:** Aprovecha generación automática de CRUDs, seguridad y configuración.
2. **MongoDB:** Modelo documental flexible para catálogo de productos.
3. **JWT stateless:** Separa frontend y backend.
4. **Storefront personalizado:** Tienda pública propia en `landing/` sin alterar endpoints JHipster.
5. **Roles de negocio:** `ADMIN`, `MANAGER`, `CLIENTE` modelan perfiles operativos.
6. **Ownership de recursos:** Clientes solo acceden a sus propios datos.
7. **Carrito híbrido:** `localStorage` para anónimos, backend para autenticados.
8. **Cuenta obligatoria:** Sin `Cuenta` no se pueden gestionar direcciones ni finalizar compras.

---

## 11. Archivos clave de referencia

- `README.md`: presentación general del proyecto.
- `CONTRIBUTING.md`: guía de contribución y convenciones de commits.
- `knstore.jdl`: definición del dominio JHipster.
- `.yo-rc.json`: configuración del generador.
- `pom.xml`: dependencias y plugins Maven.
- `package.json`: scripts y dependencias Node.
- `src/main/resources/config/application.yml`: configuración central Spring Boot.
- `src/main/java/com/mycompany/knstore/config/SecurityConfiguration.java`: reglas de seguridad HTTP.
- `src/main/webapp/app/app.tsx` y `routes.tsx`: enrutamiento y layout dual.
- `src/main/webapp/app/landing/`: tienda pública y panel de cliente.
- `src/main/webapp/app/dashboard/index.tsx`: punto de entrada del panel admin.
- `src/main/java/com/mycompany/knstore/service/util/MongoIdUtils.java`: conversión String→ObjectId para consultas batch sobre `@DBRef` (`ref.$id` guardado como ObjectId).
- `src/main/java/com/mycompany/knstore/service/SecuenciaService.java`: consecutivos diarios atómicos (colección `secuencias`, `findAndModify` + `$inc`).
- `src/main/webapp/app/landing/hooks/useCuentaActual.ts` y `utils/apiError.ts`: patrones compartidos del panel de cliente (carga de cuenta, errores Axios tipados).
- `src/main/webapp/app/landing/services/checkout.service.ts`: payload y llamadas del checkout (precio siempre server-side; el resultado incluye `pago`).
- `docs/knstore_stress_plan.jmx`: plan de estrés/rendimiento (sección 07, escenarios ES-01…ES-07 con cargas vía `-JN_S1…N_S7`).
- `docs/jmeter/gen_informe.py` + `print_variant.py` + `pdf_build.py` + `generar_informe.sh`: pipeline de informes (HTML/PDF) desde `resultados.jtl`.

---

## 12. Notas para el agente

- **PRIMERO:** leer `docs/ESTADO_SESION.md` (handoff con estado git/docker/quirks del trabajo activo).
- Antes de modificar `entities/`, `modules/` o `shared/` consultar si es realmente necesario; es código autogenerado.
- Al trabajar en el landing, preferir hooks `useCart` y `useCatalog` en lugar de repetir lógica de fetching.
- Mantener responsividad; probar desde 360px.
- Respetar ownership: cualquier endpoint nuevo para `CLIENTE` debe validar que el recurso pertenece al usuario autenticado.
- Actualizar este `AGENTS.md` cuando cambien decisiones arquitectónicas, roles, convenciones o requerimientos.

### 12.1 Commits y mirror — obligatorio

Siempre que se pidan commits al agente, ejecutar el flujo completo (no dejar nada a medias):

1. **Commit** en `knstore` (rama `main`) con Conventional Commits.
2. **Push** a `origin` (`joseph12n/knstore`) → `main`.
3. **Mirror**: copiar el estado exacto al repo local en `~/Documentos/2026-3311941-projects-grupo-06-knstore` (fetch del repo `knstore` + `git merge --ff-only`) y **push** a `origin` (`sena-students/2026-3311941-trimestre-5-2026-3311941-trimestre-5-documentat-joseph12n`) → `main`.
4. **Branches unificadas:** en AMBOS repos, mantener las 6 ramas (`main`, `Nicolas`, `carrito`, `joseph`, `lauraG`, `santiago`) apuntadas a `main` — sin adelantos ni conflictos (push `main:<rama>` con `--force-with-lease` si la rama no es fast-forward; el contenido de ramas se incorpora a `main` vía squash).

Reglas:

- `knstore` es la fuente de verdad; sena-students es espejo. Nunca editar directamente en sena-students fuera del mirror.
- Integrar trabajo de ramas (ej. lauraG) en `main` con squash para mantener la historia organizada.
- Antes de reiniciar una rama, respaldar el tip en `refs/backup/<fecha>/<rama>` (local a los dos repos, no se sube).
- Credenciales: PAT del usuario guardado en `~/.git-credentials` (helper `store`, permiso 600). Si un push falla por auth, pedir la credencial al usuario, no reinventar.

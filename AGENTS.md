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

---

## 6. Requerimientos funcionales (resumen)

### Implementados

- RF-001 a RF-036: autenticación, gestión de usuarios, categorías, subcategorías, productos, catálogo público, panel admin.

### Pendientes / parciales

| ID              | Requerimiento                                             | Estado      |
| --------------- | --------------------------------------------------------- | ----------- |
| RF-033          | Editar datos del perfil propio                            | Parcial     |
| RF-037 a RF-041 | CRUD de direcciones propias                               | Pendiente   |
| RF-042 a RF-046 | Carrito (agregar, consultar, modificar, eliminar, vaciar) | Pendiente\* |
| RF-047 a RF-053 | Checkout, total, cancelar, listar/detalle pedidos         | Pendiente\* |
| RF-054 a RF-069 | Pagos, envíos, facturación                                | Pendiente   |

\* _La UI del carrito y sincronización server/localStorage existen; el checkout completo y operaciones de pedido/pago/envío/factura están pendientes._

---

## 7. Requerimientos no funcionales (resumen)

### Implementados

- Seguridad: bcrypt costo ≥10, JWT 30 días, validación de entradas, control de roles, eliminación lógica por defecto, CORS restringido.
- Rendimiento: catálogo <500ms P95, paginación obligatoria, índices MongoDB.
- Usabilidad: responsive, animaciones ≥50 FPS, tokens CSS, mensajes de error claros.
- Mantenibilidad: separación de capas, convenciones de nomenclatura, documentación sincronizada, compatibilidad de navegadores, Node LTS.
- Disponibilidad/escalabilidad: 99.5% mensual, manejo robusto de errores, backend stateless, capacidad 100k productos.

### Pendientes

| ID      | Requerimiento                        |
| ------- | ------------------------------------ |
| RNF-023 | Atomicidad del checkout              |
| RNF-024 | Concurrencia en operaciones de stock |
| RNF-025 | Auditoría de pagos y pedidos         |
| RNF-026 | Precisión monetaria (DECIMAL)        |

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

---

## 12. Notas para el agente

- Antes de modificar `entities/`, `modules/` o `shared/` consultar si es realmente necesario; es código autogenerado.
- Al trabajar en el landing, preferir hooks `useCart` y `useCatalog` en lugar de repetir lógica de fetching.
- Mantener responsividad; probar desde 360px.
- Respetar ownership: cualquier endpoint nuevo para `CLIENTE` debe validar que el recurso pertenece al usuario autenticado.
- Actualizar este `AGENTS.md` cuando cambien decisiones arquitectónicas, roles, convenciones o requerimientos.

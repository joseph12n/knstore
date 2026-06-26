# Arquitectura de Knstore

## 1. Visión general

**Knstore** es una aplicación monolítica de comercio electrónico generada con [JHipster 9.1.0](https://www.jhipster.tech/). Combina un backend en **Spring Boot** con un frontend en **React**, usando **MongoDB** como base de datos y **JWT** para autenticación.

El proyecto tiene dos caras principales:

- **Storefront de cliente**: interfaz pública de tienda (catálogo, carrito, checkout, cuenta del cliente).
- **Panel administrativo JHipster**: gestión CRUD de entidades, usuarios, métricas y logs, dirigido a `ADMIN` y `MANAGER`.

---

## 2. Stack tecnológico

| Capa           | Tecnología                                            |
| -------------- | ----------------------------------------------------- |
| Generador      | JHipster 9.1.0                                        |
| Backend        | Spring Boot 4.0.6, Java 21                            |
| Seguridad      | Spring Security + JWT (OAuth2 Resource Server)        |
| Base de datos  | MongoDB                                               |
| Migraciones    | Mongock                                               |
| Frontend       | React 19, Redux Toolkit, React Router 7, TypeScript   |
| UI             | Bootstrap 5 / React-Bootstrap, tema Bootswatch Cyborg |
| Build backend  | Maven 3.2.5+                                          |
| Build frontend | Webpack 5, Vitest (tests)                             |
| Node           | 24.16.0                                               |
| Empaquetado    | JAR ejecutable con Jib (Docker)                       |

---

## 3. Arquitectura del backend

El backend sigue la estructura por capas típica de JHipster/Spring:

```
src/main/java/com/mycompany/knstore/
├── config/                 # Configuración de Spring (seguridad, MongoDB, Jackson, etc.)
├── domain/                 # Entidades de MongoDB (@Document)
├── domain/enumeration/     # Enums de negocio
├── repository/             # Repositorios Spring Data MongoDB
├── service/                # Interfaces de servicio
├── service/impl/           # Implementaciones de servicio
├── service/dto/            # DTOs generados por MapStruct
├── service/mapper/         # Mapeadores MapStruct
├── web/rest/               # Controladores REST
├── web/rest/errors/        # Manejo centralizado de errores
├── web/filter/             # Filtros (SPA web filter)
├── security/               # Utilidades JWT y constantes de roles
└── aop/logging/            # Aspectos de logging
```

### Patrón arquitectónico

- **API REST** stateless bajo `/api/**`.
- **DTO + MapStruct**: las entidades de dominio se exponen como DTOs para desacoplar la capa web del modelo de persistencia.
- **Repository pattern**: acceso a datos mediante interfaces de Spring Data MongoDB.
- **Servicio con implementación**: cada entidad generada tiene su interfaz `Service` y su `ServiceImpl`.
- **Seguridad declarativa**: uso de `@PreAuthorize` en controladores para restringir por rol.

### Roles de seguridad

Se definen cuatro roles personalizados:

- `ROLE_ADMIN`: acceso total al panel administrativo y a todas las entidades.
- `ROLE_MANAGER`: acceso operativo al catálogo y entidades operativas.
- `ROLE_CLIENTE`: cliente de la tienda; solo puede ver/modificar sus propios recursos.
- `ROLE_USER`: usuario genérico; sin acceso a entidades protegidas de negocio.

### Autenticación

- JWT emitido en `/api/authenticate`.
- Spring Security configura `oauth2ResourceServer().jwt()` para validar tokens.
- Las rutas de lectura pública del catálogo (`GET /api/categorias/**`, `/api/productos/**`, etc.) están abiertas para alimentar el storefront.

---

## 4. Modelo de dominio

La aplicación modela un e-commerce colombiano con las siguientes entidades principales:

### Catálogo

- `Categoria`, `Subcategoria`, `Marca`
- `Producto` (con slug, SKU, referencia, descripción, activo/destacado)
- `ProductoPrecio`, `ProductoInventario`, `ProductoImagen`, `EtiquetaProducto`
- `CategoriaIVA` (impuestos por categoría)

### Clientes y cuentas

- `User` (JHipster) + `Cuenta` (perfil extendido del cliente). Al registrarse o crearse un usuario, se genera automáticamente una `Cuenta` con los datos básicos del usuario; sin ella no se pueden crear direcciones ni pedidos.
- `TipoDocumento`, `Direccion`
- `Carrito`, `ItemCarrito`

### Pedidos y post-venta

- `Pedido`, `ItemPedido`
- `Pago`
- `Envio`
- `Factura`

### Relaciones destacadas

- `Cuenta` ↔ `User` (1:1)
- `Carrito` ↔ `Cuenta` (1:1)
- `Pedido` ↔ `Direccion` (1:1 bidireccional)
- `Pedido` ↔ `Envio` (1:1 bidireccional)
- `Producto` → `ProductoPrecio` / `ProductoInventario` (1:1)
- `Producto` → `ProductoImagen` (1:N)

Las relaciones se modelan con `@DBRef` de MongoDB y validaciones Bean Validation (`jakarta.validation`).

---

## 5. Arquitectura del frontend

El frontend está en `src/main/webapp/app/` y se organiza en dos grandes áreas: **landing** (tienda pública) y **dashboard** (panel administrativo JHipster). El código generado por JHipster (`entities/`, `modules/`, `shared/`) permanece intacto para facilitar futuras regeneraciones.

```
src/main/webapp/app/
├── app.tsx                 # Punto de entrada y lógica de layout dual
├── routes.tsx              # Definición de rutas (landing + dashboard + auth)
├── dashboard/              # Punto de entrada unificado del panel administrativo
│   └── index.tsx           # Re-exporta Admin y EntitiesRoutes
├── config/
│   ├── store.ts            # Configuración de Redux Toolkit
│   └── dayjs.ts            # Configuración regional
├── entities/               # CRUDs generados por JHipster (sin mover)
├── modules/                # Módulos JHipster: account, login, administration (sin mover)
├── shared/                 # Componentes/utilidades compartidas de JHipster (sin mover)
│   ├── auth/               # PrivateRoute, autorización
│   ├── layout/             # Header, footer, menús
│   ├── model/              # Modelos TypeScript generados
│   ├── reducers/           # Reducers globales (auth, perfil, etc.)
│   └── jhipster/constants.ts # Roles y constantes
└── landing/                # Tienda pública personalizada (antes storefront/)
    ├── components/         # UI de la tienda (header, footer, product card, etc.)
    │   └── LandingLayout.tsx # Layout padre con Outlet, useCatalog y useCart
    ├── pages/              # Páginas: home, categoría, producto, carrito, checkout, etc.
    ├── hooks/              # useCart, useCatalog
    ├── model/              # Tipos extendidos del landing
    ├── routes/             # StorefrontRoutes
    ├── styles/             # SCSS y tokens CSS
    └── utils/              # Constantes y utilidades de formateo
```

### Layout dual

`app.tsx` decide qué layout renderizar según la ruta actual:

- **Rutas de landing** (`/`, `/categorias`, `/productos`, `/carrito`, `/checkout`, `/cuenta`): usan `LandingLayout` > `StorefrontLayout` (diseño de tienda).
- **Panel de cliente** (`/cuenta/*`): usa `AccountLayout` con sidebar propio, sin depender del panel administrativo JHipster.
- **Rutas administrativas/CRUD** (`/admin`, `/categoria`, `/producto`, etc.): usan el layout clásico de JHipster (header, footer, jh-card).

### Enrutamiento con Outlet

`routes.tsx` utiliza una ruta padre `<Route element={<LandingLayout />}>` que envuelve todas las rutas de la tienda. `LandingLayout` carga el catálogo y el carrito una sola vez y renderiza el `<Outlet />`, eliminando el prop drilling que existía entre `routes.tsx`, `StorefrontLayout` y las páginas.

### Estado global

- **Redux Toolkit** gestiona autenticación, perfil de la aplicación y entidades JHipster.
- **localStorage** persiste el carrito de compras (`knstore-cart`) para usuarios anónimos.
- Las páginas del landing consumen el carrito directamente mediante el hook `useCart`.
- Todos los componentes y páginas del landing son responsivos: grids de Bootstrap, navegación móvil en `/cuenta`, filtros colapsables en búsqueda/categorías y galerías de producto adaptadas a pantallas pequeñas.

### Hooks del landing

- `useCatalog`: carga categorías, subcategorías, marcas y productos desde los endpoints JHipster generados.
- `useCart`: gestión del carrito. Para usuarios anónimos usa `localStorage`; para usuarios autenticados sincroniza con `Carrito` e `ItemCarrito` en el backend, asociando el carrito a la cuenta del usuario y migrando el carrito local al servidor en el primer inicio de sesión.

---

## 6. Seguridad y permisos por ruta

| Ruta                                                                   | Roles permitidos                                                 |
| ---------------------------------------------------------------------- | ---------------------------------------------------------------- |
| `/`, `/categorias`, `/productos`, `/buscar`                            | Público (lectura)                                                |
| `/carrito`                                                             | Público (carrito local/anónimo)                                  |
| `/checkout`                                                            | `ADMIN`, `MANAGER`, `CLIENTE`, `USER`                            |
| `/cuenta/*`                                                            | `ADMIN`, `MANAGER`, `CLIENTE`, `USER` (panel propio del cliente) |
| `/admin/*`                                                             | `ADMIN`, `MANAGER`                                               |
| `/account/settings`, `/account/password`                               | `ADMIN`, `MANAGER` (clientes usan `/cuenta`)                     |
| `/login`, `/account/register`, `/account/activate`, `/account/reset/*` | Público                                                          |

En el backend, la mayoría de endpoints de entidades de negocio requieren autenticación y están protegidos por rol mediante `@PreAuthorize`.

---

## 7. Base de datos y migraciones

- MongoDB se configura en `application.yml` y perfiles (`application-dev.yml`, `application-prod.yml`).
- **Mongock** ejecuta migraciones al arrancar la aplicación.
- `InitialSetupMigration` crea:
  - Autoridades: `ROLE_ADMIN`, `ROLE_MANAGER`, `ROLE_CLIENTE`, `ROLE_USER`.
  - Usuarios iniciales: `admin`, `manager`, `cliente`, `user`.

---

## 8. Build, empaquetado y despliegue

### Backend

```bash
./mvnw -Pprod clean verify          # JAR de producción
./mvnw -Pprod,war clean verify      # WAR
```

### Frontend

```bash
./npmw run start                    # Dev server con Webpack
./npmw run webapp:prod              # Build de producción
./npmw test                         # Tests con Vitest
```

### Desarrollo simultáneo

```bash
./npmw run backend:start            # Spring Boot
./npmw run start                    # Webpack dev server
```

### Docker

- `src/main/docker/services.yml`: levanta MongoDB y servicios auxiliares.
- `src/main/docker/app.yml`: despliega la aplicación completa.
- `npm run java:docker`: construye imagen Docker con Jib.
- Spring Boot Docker Compose está habilitado por defecto en desarrollo.

---

## 9. Tests

| Tipo                  | Tecnología                          | Ubicación                     |
| --------------------- | ----------------------------------- | ----------------------------- |
| Backend unitarios     | JUnit 5, Spring Boot Test           | `src/test/java/...`           |
| BDD                   | Cucumber                            | `src/test/java/.../cucumber`  |
| Seguridad / ownership | `ResourceAccessServiceTest`         | `src/test/java/.../security/` |
| Frontend unitarios    | Vitest + React Testing Library      | junto a componentes           |
| Calidad de código     | Checkstyle, Sonar, ESLint, Prettier | configuración en raíz         |

---

## 10. Resumen de decisiones arquitectónicas

1. **Monolito JHipster**: simplifica el desarrollo inicial y aprovecha la generación automática de CRUDs, seguridad y configuración.
2. **MongoDB**: base de datos documental acorde al modelo de catálogo flexible de productos.
3. **JWT stateless**: facilita la separación entre frontend y backend y el consumo desde React.
4. **Storefront personalizado**: se construyó una experiencia de tienda diferenciada sin alterar los endpoints JHipster generados, reutilizando los reducers y DTOs existentes.
5. **Roles de negocio**: `ADMIN`, `MANAGER` y `CLIENTE` modelan claramente los perfiles operativos del negocio.
6. **Ownership de recursos**: los clientes solo pueden acceder a sus propios carritos, pedidos, direcciones, pagos, envíos y facturas.
7. **Carrito en cliente**: se mantiene en `localStorage` para permitir navegación anónima antes del checkout.

---

## 11. Archivos clave de referencia

- `knstore.jdl`: definición del dominio y configuración de JHipster.
- `.yo-rc.json`: configuración del generador JHipster.
- `pom.xml`: dependencias y plugins de Maven.
- `package.json`: scripts y dependencias de Node.
- `src/main/resources/config/application.yml`: configuración central de Spring Boot.
- `src/main/java/com/mycompany/knstore/config/SecurityConfiguration.java`: reglas de seguridad HTTP.
- `src/main/webapp/app/app.tsx` y `routes.tsx`: enrutamiento y layout dual.
- `src/main/webapp/app/landing/`: tienda pública personalizada.
- `src/main/webapp/app/dashboard/index.tsx`: punto de entrada del panel administrativo.
- `src/main/webapp/app/landing/components/LandingLayout.tsx`: layout padre del landing con `Outlet`.

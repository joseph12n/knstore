# Arquitectura de Knstore

## Stack Tecnológico

```
┌─────────────────────────────────────────────────────────┐
│                    FRONTEND (React SPA)                  │
│  React 19 + TypeScript 6 + Redux Toolkit + React Router │
│  Bootstrap 5 + Bootswatch Cyborg + React Hook Form      │
└──────────────────────┬──────────────────────────────────┘
                       │  HTTP (JWT)
                       ▼
┌─────────────────────────────────────────────────────────┐
│               BACKEND (Spring Boot 4)                    │
│  Spring Security + Spring Data MongoDB + MapStruct       │
│  REST Controllers → Services → Repositories → MongoDB   │
└──────────────────────┬──────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────┐
│                   MONGODB (ReplicaSet)                   │
│  Colecciones: producto, pedido, categoria, usuario, etc  │
└─────────────────────────────────────────────────────────┘
```

## Estructura de Directorios

```
knstore/
├── src/
│   ├── main/
│   │   ├── java/com/mycompany/knstore/     ← Backend (Java 21)
│   │   │   ├── config/                     ← Spring Config, Seguridad, Mongock
│   │   │   ├── domain/                     ← Modelos MongoDB (Producto, Pedido, etc.)
│   │   │   ├── repository/                 ← Spring Data MongoDB repos
│   │   │   ├── service/                    ← Lógica de negocio + DTOs + Mappers
│   │   │   └── web/rest/                   ← REST controllers
│   │   ├── resources/                      ← application.yml, i18n, etc.
│   │   ├── docker/                         ← Docker Compose (Mongo, app, Sonar)
│   │   └── webapp/                         ← Frontend React
│   │       └── app/
│   │           ├── app.tsx                 ← Componente raíz
│   │           ├── index.tsx               ← Punto de entrada (mount React + store)
│   │           ├── routes.tsx              ← Router principal
│   │           ├── config/                 ← Store Redux, interceptors axios
│   │           ├── entities/               ← CRUD generado por JHipster
│   │           ├── modules/                ← Login, register, admin
│   │           ├── shared/                 ← Layout, auth, modelos, utilidades
│   │           └── storefront/             ← Tienda custom (páginas, hooks, estilos)
│   └── test/                               ← Tests (JUnit, Cucumber, Vitest)
├── webpack/                                ← Config Webpack (dev/prod)
├── .jhipster/                              ← Metadata de entidades JHipster
├── docs/                                   ← Documentación
├── pom.xml                                 ← Build Maven
├── package.json                            ← Scripts y deps frontend
├── tsconfig.json
└── knstore.jdl                             ← Modelo de datos JHipster
```

## Flujo de Datos

```
1. Navegador → index.html → Webpack bundle
2. React monta (index.tsx):
   - Crea store Redux
   - Configura axios (JWT en localStorage/sessionStorage)
   - Dispara getSession() y getProfile()
3. Router decide layout (storefront o admin)
4. Componentes → hooks (useCatalog, useCart) → axios → /api/*
5. Spring Security valida JWT
6. Controller → Service → Repository → MongoDB
7. Respuesta → DTO → JSON → Componente React
```

## Seguridad

| Rol | Acceso |
|-----|--------|
| `ROLE_ADMIN` | Todo |
| `ROLE_MANAGER` | Gestión de catálogo |
| `ROLE_CLIENTE` | Solo sus propios recursos (pedidos, direcciones, etc.) |
| Anónimo | Lectura de catálogo (GET público) |

- JWT: login → `POST /api/authenticate` → token → adjunto en cada request
- Autorización por método con `@PreAuthorize` + `ResourceAccessService` para ownership

## Capas del Backend

```
REST Controller  ──→  Service Interface  ──→  Service Impl  ──→  Repository  ──→  MongoDB
       │                      │
       ▼                      ▼
    DTO (request)          DTO (response)
       │                      │
       ▼                      ▼
   MapStruct Mapper ──────── Domain Entity
```

## Estado del Frontend

- **Redux Store**: auth, application-profile, entidades CRUD
- **Local state (hooks)**: carrito (`useCart` → localStorage), catálogo (`useCatalog`)
- **Axios interceptors**: attach JWT, manejar 401 (logout automático)

## Scripts Principales

| Comando | Acción |
|---------|--------|
| `./mvnw` | Iniciar backend |
| `npm run start` | Frontend dev (hot reload) |
| `npm run test` | Tests frontend (Vitest) |
| `npm run backend:start` | Backend dev |
| `npm run services:up` | Docker MongoDB up |
| `npm run app:up` | Docker app completa |

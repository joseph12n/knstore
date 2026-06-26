# Plan de Ejecución - Reorganización Landing/Dashboard

## Contexto

Documento de ejecución aprobado por el Product Owner para reorganizar el frontend de Knstore separando claramente la tienda pública (`landing`) del panel administrativo (`dashboard`).

**Alcance aprobado:**

- Reorganización de carpetas del frontend (nivel conservador).
- No se mueve código autogenerado por JHipster (`entities/`, `modules/`, `shared/`).
- Se crea `dashboard/` como wrapper de entrada al panel admin.
- Se renombra `storefront/` → `landing/`.
- Se priorizan requerimientos funcionales y no funcionales pendientes.

**Metodología:**

- Atomicidad: cada cambio es una unidad completa y verificable.
- Tests recursivos: build y test del frontend tras cada operación atómica.
- Sin código extra: solo lo necesario para cumplir el objetivo.
- Documentación sincronizada: actualizar `arquitectura.md` al finalizar.

---

## Fase 1 - Reorganización de carpetas

### 1.1 Renombrar `storefront/` → `landing/`

**Estructura objetivo:**

```
src/main/webapp/app/
├── landing/                      # Tienda pública (antes storefront/)
│   ├── components/
│   │   ├── AddressCard.tsx
│   │   ├── AddressForm.tsx
│   │   ├── CartDrawer.tsx
│   │   ├── CategoryNav.tsx
│   │   ├── CheckoutStepper.tsx
│   │   ├── EmptyState.tsx
│   │   ├── ErrorAlert.tsx
│   │   ├── HeroBanner.tsx
│   │   ├── LoadingSpinner.tsx
│   │   ├── OrderCard.tsx
│   │   ├── ProductCard.tsx
│   │   ├── QuantitySelector.tsx
│   │   ├── SearchBox.tsx
│   │   ├── StoreFooter.tsx
│   │   ├── StoreHeader.tsx
│   │   └── StorefrontLayout.tsx
│   ├── hooks/
│   │   ├── useCart.ts
│   │   └── useCatalog.ts
│   ├── model/
│   │   └── storefront.model.ts
│   ├── pages/
│   │   ├── AccountPage.tsx
│   │   ├── AddressesPage.tsx
│   │   ├── CartPage.tsx
│   │   ├── CategoryPage.tsx
│   │   ├── CheckoutPage.tsx
│   │   ├── OrderDetailPage.tsx
│   │   ├── OrdersPage.tsx
│   │   ├── ProductDetailPage.tsx
│   │   ├── SearchPage.tsx
│   │   └── StoreHome.tsx
│   ├── routes/
│   │   └── StorefrontRoutes.tsx
│   ├── styles/
│   │   ├── storefront.scss
│   │   └── tokens.css
│   └── utils/
│       ├── constants.ts
│       └── format.ts
```

**Imports a actualizar:**

- `app/routes.tsx`: todos los imports de `app/storefront/...`
- `app/app.tsx`: import de `app/storefront/styles/storefront.scss`
- Archivos internos de `landing/`: imports relativos cruzados

### 1.2 Crear `dashboard/` wrapper

```
src/main/webapp/app/
├── dashboard/
│   └── index.tsx                # Punto de entrada unificado del panel admin
```

`dashboard/index.tsx` re-exporta `Admin` y `EntitiesRoutes` para que `routes.tsx` tenga una única dependencia del dashboard.

---

## Fase 2 - Limpieza técnica

### 2.1 Eliminar prop drilling en `routes.tsx`

**Problema:** 7 rutas storefront repiten el mismo JSX de `<StorefrontLayout>` + `<StorefrontRoutes>` con 8 props idénticas.

**Solución:** Ruta padre con componente `LandingLayout` que usa `Outlet` y encapsula `useCart()` + Redux.

### 2.2 Eliminar fetching redundante

**Problema:** `routes.tsx` y `useCatalog` hacen dispatch de `getCategorias`/`getSubcategorias`.

**Solución:** Centralizar en `LandingLayout` usando `useCatalog`. Eliminar `useEffect` de `routes.tsx`.

### 2.3 Eliminar código muerto

- Eliminar `src/main/webapp/app/modules/home/`

### 2.4 Resolver conflicto `/cuenta/*`

**Problema:** La ruta storefront `/cuenta/*` tiene prioridad sobre la entidad CRUD `/cuenta/*`.

**Solución:** Documentar el comportamiento. Opcionalmente mover CRUD admin de `cuenta` a `/admin/entidades/cuenta` o similar. Por atomicidad, en esta fase se documenta y se mantiene el comportamiento actual.

### 2.5 Completar menú de entidades

**Problema:** Faltan en `entities/menu.tsx`: `item-carrito`, `item-pedido`, `pago`, `envio`, `factura`.

**Solución:** Agregarlas al dropdown de entidades.

### 2.6 Eliminar duplicidad de autoridades

**Problema:** `routes.tsx` y `StorefrontRoutes.tsx` verifican autoridades por separado.

**Solución:** Usar `PrivateRoute` solo en `routes.tsx`. Las rutas internas de `StorefrontRoutes` no deben revalidar.

### 2.7 Normalizar naming

- `entities/tipo-documento/tipo-documento-reducer.spec.ts` → `tipo-documento.reducer.spec.ts`

---

## Fase 3 - Requerimientos pendientes (priorización)

### Prioridad ALTA

| #       | Requerimiento                   | Área        |
| ------- | ------------------------------- | ----------- |
| RF-042  | Agregar producto al carrito     | Carrito     |
| RF-043  | Consultar carrito               | Carrito     |
| RF-044  | Modificar cantidad de item      | Carrito     |
| RF-045  | Eliminar item del carrito       | Carrito     |
| RF-046  | Vaciar carrito                  | Carrito     |
| RF-047  | Confirmar pedido (checkout)     | Pedidos     |
| RF-048  | Cálculo automático del total    | Pedidos     |
| RF-037  | Crear dirección                 | Direcciones |
| RF-038  | Listar direcciones propias      | Direcciones |
| RF-039  | Editar dirección propia         | Direcciones |
| RF-040  | Eliminar dirección propia       | Direcciones |
| RF-041  | Marcar dirección predeterminada | Direcciones |
| RNF-023 | Atomicidad del checkout         | Backend     |
| RNF-024 | Concurrencia en stock           | Backend     |
| RNF-026 | Precisión monetaria             | Backend     |

### Prioridad MEDIA

| #       | Requerimiento                  | Área    |
| ------- | ------------------------------ | ------- |
| RF-049  | Cancelar pedido                | Pedidos |
| RF-050  | Listar pedidos propios         | Pedidos |
| RF-051  | Listar todos los pedidos       | Pedidos |
| RF-052  | Actualizar estado del pedido   | Pedidos |
| RF-053  | Consultar detalle de un pedido | Pedidos |
| RF-054  | Iniciar proceso de pago        | Pagos   |
| RF-055  | Procesar resultado del pago    | Pagos   |
| RF-056  | Validar coherencia de pago     | Pagos   |
| RF-060  | Crear envío para un pedido     | Envíos  |
| RF-061  | Asignar número de seguimiento  | Envíos  |
| RF-062  | Actualizar estado del envío    | Envíos  |
| RF-064  | Consultar tracking propio      | Envíos  |
| RNF-025 | Auditoría de pagos y pedidos   | Backend |

### Prioridad BAJA

| #      | Requerimiento                       | Área     |
| ------ | ----------------------------------- | -------- |
| RF-033 | Editar datos del perfil (Parcial)   | Cuenta   |
| RF-057 | Solicitar reembolso                 | Pagos    |
| RF-058 | Consultar historial de pagos        | Pagos    |
| RF-059 | Consultar pago de pedido propio     | Pagos    |
| RF-063 | Registrar devolución de envío       | Envíos   |
| RF-065 | Listar envíos pendientes            | Envíos   |
| RF-066 | Generar factura automáticamente     | Facturas |
| RF-067 | Generar referencia única de factura | Facturas |
| RF-068 | Consultar factura propia            | Facturas |
| RF-069 | Listar facturas                     | Facturas |

---

## Registro de ejecución

| Fase | Tarea                                                                   | Estado     |
| ---- | ----------------------------------------------------------------------- | ---------- |
| 1.1  | Renombrar `storefront/` → `landing/`                                    | Completado |
| 1.2  | Crear `dashboard/` wrapper                                              | Completado |
| 2.1  | Eliminar prop drilling en `routes.tsx`                                  | Completado |
| 2.2  | Eliminar fetching redundante                                            | Completado |
| 2.3  | Eliminar código muerto `modules/home/`                                  | Completado |
| 2.4  | Documentar conflicto `/cuenta/*`                                        | Completado |
| 2.5  | Completar menú de entidades                                             | Completado |
| 2.6  | Eliminar duplicidad de autoridades                                      | Completado |
| 2.7  | Normalizar naming `tipo-documento.reducer.spec.ts`                      | Completado |
| 3.1  | Sincronizar carrito con servidor para usuarios autenticados             | Completado |
| 3.2  | Panel de cuenta: perfil, contraseña y layout con sidebar                | Completado |
| 3.3  | Cancelar pedido (RF-049) con modal de confirmación                      | Completado |
| 3.4  | Vista de perfil de solo lectura (`/cuenta/perfil`)                      | Completado |
| 3.5  | Historial de pagos (`/cuenta/pagos`)                                    | Completado |
| 3.6  | Mis envíos/tracking (`/cuenta/envios`)                                  | Completado |
| 3.7  | Mis facturas (`/cuenta/facturas`)                                       | Completado |
| 3.8  | Restringir `/account/*` a ADMIN/MANAGER; clientes usan `/cuenta`        | Completado |
| 3.9  | Separar `AccountRoutes` de `StorefrontRoutes` para corregir `/cuenta/*` | Completado |
| 3.10 | Actualizar `arquitectura.md`                                            | Completado |
| 3.11 | Crear `Cuenta` automáticamente al registrar/crear/activar usuario       | Completado |
| 3.12 | Validar existencia de `Cuenta` en direcciones y checkout                | Completado |
| 3.13 | Permitir a `CLIENTE` crear su propia `Cuenta` (POST `/api/cuentas`)     | Completado |
| 3.14 | Hacer el landing completamente responsive                               | Completado |

### Resultados de verificación final

- **Build frontend:** `webpack 5.107.2 compiled successfully`
- **Tests unitarios:** `430 passed` en `35` archivos de test
- **Lint:** sin errores
- **Imports rotos:** no quedan referencias a `app/storefront/` ni `modules/home/`
- **Carrito:** sincronización server/localStorage implementada en `useCart.ts`
- **Cuenta:** rutas `/cuenta/perfil`, `/cuenta/perfil/editar`, `/cuenta/seguridad`, `/cuenta/direcciones`, `/cuenta/pedidos`, `/cuenta/pagos`, `/cuenta/envios`, `/cuenta/facturas` disponibles
- **Pedidos:** botón de cancelar pedido en listado y detalle, con modal de confirmación
- **Seguridad:** `/account/settings` y `/account/password` solo accesibles para `ADMIN`/`MANAGER`; clientes redirigidos a `/cuenta`
- **Cuenta:** al registrar, crear o activar un usuario se crea automáticamente su `Cuenta` con datos básicos; si no existe, el `CLIENTE` puede crear la propia mediante `POST /api/cuentas` (ownership check); sin `Cuenta` no se pueden gestionar direcciones ni finalizar compras
- **Responsive:** navegación móvil en panel de cuenta, menú móvil del header con accesos a cuenta/carrito, filtros colapsables en búsqueda/categoría, galería de producto scrollable y ajustes tipográficos/spacing en móvil

## Checklist de verificación por cambio atómico

- [x] TypeScript compila sin errores (`npmw run webapp:build`)
- [x] Tests unitarios pasan (`npmw test`)
- [x] No quedan imports rotos de `app/storefront/`
- [x] Rutas storefront funcionan (`/`, `/categorias`, `/productos`, `/carrito`, `/checkout`, `/cuenta`)
- [x] Rutas admin funcionan (`/admin`, `/categoria`, `/producto`, etc.)
- [x] Documentación actualizada

# Resumen de Cambios Realizados - Knstore

## 1. Reorganización del frontend: Landing vs Dashboard

### Objetivo

Separar claramente la tienda pública (`landing`) del panel administrativo (`dashboard`), sin mover código autogenerado por JHipster.

### Cambios

- Se renombró `src/main/webapp/app/storefront/` → `src/main/webapp/app/landing/`.
- Se actualizaron todos los imports internos y externos (`routes.tsx`, `app.tsx`, etc.).
- Se creó `src/main/webapp/app/dashboard/index.tsx` como punto de entrada unificado del panel admin.
- Se eliminó código muerto: `src/main/webapp/app/modules/home/`.
- Se completó el menú de entidades en `src/main/webapp/app/entities/menu.tsx`.
- Se normalizó el nombre del test `tipo-documento.reducer.spec.ts`.

### Resultado

El frontend quedó organizado en dos áreas bien diferenciadas:

- `landing/`: tienda pública y panel de cliente.
- `dashboard/`: panel administrativo JHipster.

---

## 2. Limpieza de enrutamiento

### Cambios

- Se eliminó el prop drilling en `routes.tsx` usando `LandingLayout` con `<Outlet />`.
- Se centralizó la carga del catálogo en `LandingLayout` mediante `useCatalog`.
- Se separaron las rutas de cuenta en `src/main/webapp/app/landing/routes/AccountRoutes.tsx` para corregir el enrutamiento de `/cuenta/*`.
- Se documentó que `/cuenta/*` tiene prioridad sobre el CRUD admin de la entidad `Cuenta`.

### Rutas corregidas

| Ruta                                   | Descripción                          |
| -------------------------------------- | ------------------------------------ |
| `/`                                    | Home de la tienda                    |
| `/categorias/:slug/:subcategoriaSlug?` | Página de categoría                  |
| `/productos/:slug`                     | Detalle de producto                  |
| `/buscar`                              | Búsqueda con filtros                 |
| `/carrito`                             | Carrito de compras                   |
| `/checkout`                            | Finalizar compra                     |
| `/cuenta/*`                            | Panel de cliente (protegido)         |
| `/admin/*`                             | Panel administrativo (ADMIN/MANAGER) |

---

## 3. Panel de cliente completo (`/cuenta`)

### Vistas creadas

| Archivo                  | Ruta                  | Funcionalidad                                            |
| ------------------------ | --------------------- | -------------------------------------------------------- |
| `AccountLayout.tsx`      | `/cuenta/*`           | Layout con sidebar en desktop y nav horizontal en móvil  |
| `AccountSidebar.tsx`     | -                     | Sidebar de navegación del panel de cliente               |
| `AccountMobileNav.tsx`   | -                     | Navegación horizontal deslizable en móvil                |
| `AccountPage.tsx`        | `/cuenta`             | Dashboard resumen del cliente                            |
| `ProfilePage.tsx`        | `/cuenta/perfil`      | Perfil interactivo con foto y formulario editable        |
| `PasswordChangePage.tsx` | `/cuenta/seguridad`   | Cambio de contraseña                                     |
| `AddressesPage.tsx`      | `/cuenta/direcciones` | CRUD completo de direcciones + dirección predeterminada  |
| `OrdersPage.tsx`         | `/cuenta/pedidos`     | Listado de pedidos del cliente                           |
| `OrderDetailPage.tsx`    | `/cuenta/pedidos/:id` | Detalle de pedido con productos, envío, pago y dirección |
| `PaymentsPage.tsx`       | `/cuenta/pagos`       | Historial de pagos del cliente                           |
| `ShipmentsPage.tsx`      | `/cuenta/envios`      | Envíos y tracking                                        |
| `InvoicesPage.tsx`       | `/cuenta/facturas`    | Facturas del cliente                                     |
| `OrderCancelModal.tsx`   | -                     | Modal de confirmación para cancelar pedidos              |

### Características

- El cliente ya no accede al panel administrativo JHipster (`/account/settings`, `/account/password`) para gestionar su cuenta.
- `/account/*` quedó restringido a `ADMIN`/`MANAGER`.
- El header de la tienda y el menú móvil apuntan a `/cuenta`.

---

## 4. Sincronización del carrito con el servidor

### Cambios

- Se refactorizó `src/main/webapp/app/landing/hooks/useCart.ts`.
- Usuarios anónimos: carrito persistente en `localStorage` (`knstore-cart`).
- Usuarios autenticados: carrito sincronizado con las entidades `Carrito` e `ItemCarrito` del backend.
- Al iniciar sesión, el carrito local se migra automáticamente al servidor sumando cantidades a items existentes.
- Operaciones soportadas: agregar, cambiar cantidad, eliminar item y vaciar carrito.

### APIs consumidas

- `GET /api/cuentas`
- `GET /api/carritos`
- `POST /api/carritos`
- `GET /api/item-carritos`
- `POST /api/item-carritos`
- `PUT /api/item-carritos/:id`
- `DELETE /api/item-carritos/:id`
- `GET /api/productos?size=1000&eagerload=true`

---

## 5. Creación automática y validación de `Cuenta`

### Backend

- `AccountResource.java`: al registrar o activar un usuario, se crea automáticamente una `Cuenta` con los datos básicos del usuario.
- `UserResource.java`: al crear un usuario desde el admin, también se crea su `Cuenta`.
- `CuentaResource.java`: se permitió a `ROLE_CLIENTE` crear su propia `Cuenta` mediante `POST /api/cuentas` con verificación de ownership y sin permitir duplicados.

### Frontend

- `ProfilePage.tsx`: si no existe `Cuenta`, el formulario la crea; si existe, la actualiza.
- `AddressesPage.tsx`: redirige a `/cuenta/perfil` si el usuario no tiene `Cuenta`.
- `CheckoutPage.tsx`: redirige a `/cuenta/perfil` si falta la `Cuenta`.

### Regla de negocio

> Un usuario puede existir sin `Cuenta`, pero no puede tener direcciones ni finalizar compras hasta completar su perfil.

---

## 6. Seguridad

### Cambios

- `/account/settings` y `/account/password` solo accesibles para `ADMIN`/`MANAGER`.
- Clientes (`ROLE_CLIENTE`) usan exclusivamente `/cuenta/*`.
- `CuentaResource.createCuenta` ahora permite `CLIENTE` pero solo para crear su propia cuenta.
- Se verifica que un cliente no pueda crear una `Cuenta` duplicada.

---

## 7. Responsive design

### Componentes adaptados

- **`AccountLayout` + `AccountMobileNav`**: navegación horizontal deslizable en móvil para el panel de cliente.
- **`StoreHeader`**: menú móvil con accesos a cuenta, pedidos, direcciones, pagos, envíos, facturas, seguridad, carrito y cierre de sesión.
- **`SearchPage` / `CategoryPage`**: filtros colapsables en móvil.
- **`ProductDetailPage`**: breadcrumb flexible, galería de imágenes scrollable horizontalmente en móvil, botones de compra a ancho completo.
- **`CheckoutPage`**: botones de navegación del stepper apilados en móvil.
- **`storefront.scss`**: clases utilitarias responsive, ajustes tipográficos en móvil, scrollbars ocultas en nav/galería, tablas con scroll horizontal.

---

## 8. Configuración de entorno y despliegue

### Cambios

- `webpack/environment.js`: `SERVER_API_URL: ''` para URLs relativas (funciona en local y producción mismo-dominio).
- `application-dev.yml`: CORS configurado para `localhost`.
- `application-prod.yml`: CORS configurado para el dominio público de producción.

---

## 9. Documentación actualizada

- `arquitectura.md`: estructura landing/dashboard, hooks, seguridad, modelo de dominio y responsividad.
- `PLAN-EJECUCION-LANDING-DASHBOARD.md`: registro completo de fases y tareas ejecutadas.
- `CAMBIOS-REALIZADOS.md`: este documento.

---

## 10. Verificación final

| Verificación                               | Resultado                               |
| ------------------------------------------ | --------------------------------------- |
| Compilación backend (`./mvnw compile`)     | Sin errores                             |
| TypeScript (`npx tsc --noEmit`)            | Sin errores                             |
| Lint (`./npmw run lint`)                   | Sin errores                             |
| Tests unitarios (`./npmw test`)            | 430 passed en 35 archivos               |
| Build frontend (`./npmw run webapp:build`) | `webpack 5.107.2 compiled successfully` |

---

## Archivos más relevantes modificados/creados

### Backend

- `src/main/java/com/mycompany/knstore/web/rest/AccountResource.java`
- `src/main/java/com/mycompany/knstore/web/rest/UserResource.java`
- `src/main/java/com/mycompany/knstore/web/rest/CuentaResource.java`
- `src/main/resources/config/application-dev.yml`
- `src/main/resources/config/application-prod.yml`

### Frontend

- `src/main/webapp/app/routes.tsx`
- `src/main/webapp/app/app.tsx`
- `src/main/webapp/app/landing/` (nueva estructura completa)
- `src/main/webapp/app/dashboard/index.tsx`
- `src/main/webapp/app/shared/layout/menus/account.tsx`
- `webpack/environment.js`
- `webpack/webpack.dev.js`

### Documentación

- `arquitectura.md`
- `PLAN-EJECUCION-LANDING-DASHBOARD.md`
- `CAMBIOS-REALIZADOS.md`

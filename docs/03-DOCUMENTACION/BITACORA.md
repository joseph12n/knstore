# BITÁCORA DE CAMBIOS — KN-Store

> **Regla obligatoria del proyecto:** todo cambio (código, base de datos, infraestructura, configuración de servidores, decisiones y comandos ejecutados sobre la EC2) debe registrarse aquí el mismo día, con evidencia. Entradas nuevas **arriba**. Complementa a `ESTADO_SESION.md`.

---

## 2026-09-18 — Fix responsive del header (dropdown de cuenta)

| # | Cambio | Detalle / evidencia |
|---|--------|---------------------|
| 1 | **El dropdown de cuenta rompía el header en <992px** | Causa: Bootstrap 5 aplica `position: static` a los `.dropdown-menu` dentro del navbar en <lg (asume un collapse que nuestro header no usa); el menú entraba al flujo, estiraba el nav-item (216×254 px) y desalineaba los iconos (el carrito caía a otra fila, el toggle quedaba oculto). Fix en `storefront.scss`: `.storefront .storefront-header .dropdown-menu { position: absolute; max-width: calc(100vw - 1.5rem); }`. Verificado con CDP a 390/540/790/1440: el menú flota alineado a la derecha, dentro del viewport, y el header queda en una sola fila. |
| 2 | **Pruebas** | Frontend **533/533** y `tsc` limpios tras el cambio (solo CSS). |



| # | Cambio | Detalle / evidencia |
|---|--------|---------------------|
| 1 | **Fix de botones (afecta landing y admin)** | Causa raíz: Bootswatch/Cyborg fija `background-color` literal y `background-image` (gradiente) al final del CSS, pisando los tokens (`AÑADIR` azul, `Refrescar lista` lila, `Crear` cian). Se anuló `background-image` y se fijó `background-color`/`:hover`/`:disabled` por token: `ProductCard` ahora usa botón negro de marca, crear del admin rojo y secundarios neutros. Archivos: `storefront.scss`, `admin.scss`. Verificado con capturas. |
| 2 | **Shell propio `AdminLayout`** | Sidebar agrupada (Panel, Operación, Catálogo, Clientes, Ventas, Administración) + topbar con acceso a la tienda, toggle claro/oscuro y menú de sesión. Reemplaza el header/`.jh-card` de JHipster para `/admin/*` y rutas de entidades (`app.tsx`). Responsive (sidebar off-canvas en móvil). |
| 3 | **Dashboard de inicio (`/admin`)** | KPIs (pedidos totales, pendientes, ventas, stock bajo, envíos pendientes), últimos 5 pedidos con badges y accesos rápidos. Spec con axios mockeado. |
| 4 | **Tema del admin (`admin.scss`)** | Tokens del storefront (claro/oscuro) aplicados a las páginas generadas: tablas, formularios, modales, dropdowns, paginación, listas y alertas. `tokens.css` amplía el selector oscuro a `.admin-shell[data-theme='dark']`. |
| 5 | **F2.2 Operación** | Pedidos, Envíos y Reembolsos se renderizan dentro del shell nuevo (captura de `/admin/operacion/pedidos`). |
| 6 | **F2.3/F2.4 Catálogo, clientes, ventas y administración** | Los CRUD generados quedan tematizados y accesibles desde la sidebar (productos, inventario, precios, imágenes, categorías, subcategorías, marcas, etiquetas, IVA, cuentas, direcciones, carritos, ítems, pedidos, pagos, facturas, envíos, usuarios, salud, métricas, configuración, logs, API). Páginas propias de edición rápida quedan como backlog opcional. |
| 7 | **Pruebas** | Frontend **533/533** (55 archivos), `tsc` y webpack sin errores; capturas claro/oscuro de dashboard, CRUD de productos y operación. |
| 8 | **Alcance** | Cambios **solo locales**; sin despliegue a la EC2. |



| # | Cambio | Detalle / evidencia |
|---|--------|---------------------|
| 1 | **Modo oscuro con toggle** | Tokens `[data-theme='dark']` en `landing/styles/tokens.css`; `StorefrontLayout` gestiona el tema (`data-theme` en `.storefront`, persistencia en `localStorage` `kn-theme`, inicial por `prefers-color-scheme`); botón sol/luna en `StoreHeader`. Fondos `#f8f9fa` tokenizados (`ProductCard`, categorías) para que el tema aplique. Evidencia: capturas CDP con `prefers-color-scheme` emulado (claro y oscuro a 1440 y 360). |
| 2 | **Secciones nuevas en el home** | `BenefitsBar` (envío gratis ≥$150.000, pagos, garantía, soporte), `OffersSection` (productos con descuento y completado con más baratos), `BrandStrip` (`/api/marcas`), `Testimonials` (3 opiniones) y `NewsletterCta` (validación de correo + toast). Integradas en `StoreHome` en ese orden. |
| 3 | **Fix de contraste del newsletter** | `.storefront h3/p` pisaban el color del banner y el texto quedaba invisible en oscuro; se agregó la clase `.kn-newsletter` en `storefront.scss` con colores por token (claro: banner oscuro/texto blanco; oscuro: banner claro/texto oscuro). Verificado con capturas. |
| 4 | **Pruebas** | 5 specs nuevas (Benefits, Testimonials, Newsletter con interacción, BrandStrip con axios mockeado, OffersSection): **532/532** frontend, 54 archivos, cobertura 50,19% sentencias / 50,48% líneas; `tsc` y webpack sin errores. |
| 5 | **Alcance** | Cambios **solo locales** (webpack dev + backend dev activos para revisión). Sin despliegue a la EC2. |



Plan aprobado: responsive del catálogo, envío gratis visible, landing con toggle oscuro + secciones nuevas y admin por fases (local primero; despliegue a EC2 se decide al final).

| # | Cambio | Detalle / evidencia |
|---|--------|---------------------|
| 1 | **`ProductCard` responsive y botón** | Causa: fila precio/CTA sin `flex-wrap` y `.storefront .btn-primary` pisaba a `btn-sm` (padding grande + uppercase); además el `stretched-link` obligaba a un `z-3` frágil. Se quitó el `stretched-link`, se creó `.kn-product-card__footer/__price/__add` con `container-type: inline-size` y `@container (max-width: 240px)` que apila precio y botón a ancho completo. Evidencia: capturas a 360/768/1024/1440 sin superposición (`/tmp/opencode/after-*`, `full-*`). Archivos: `landing/components/ProductCard.tsx`, `landing/styles/storefront.scss`. |
| 2 | **Envío gratis visible en el checkout** | El backend ya aplicaba la regla (`subtotal >= 150000 ⇒ envío 0`), pero el preview solo se cargaba en el paso 3 y el paso 2 mostraba siempre el costo de lista. Se movió la carga del preview al seleccionar dirección (paso 1) y se agregó `envioGratis` (preview o subtotal local) que muestra "Gratis" con el costo tachado y un aviso cuando aplica. Archivos: `landing/pages/CheckoutPage.tsx`, `CheckoutPage.spec.tsx`. |
| 3 | **Pruebas** | IT nuevos en `CheckoutServiceIT`: envío gratis con subtotal ≥ umbral (preview, pedido, pago y envío en 0) y cobro estándar bajo el umbral → 7/7 verdes. Spec de UI nueva en `CheckoutPage.spec.tsx` (525/525 frontend). `tsc` limpio y umbrales de cobertura vigentes. |
| 4 | **Alcance** | Cambios **solo locales**; no se desplegó a la EC2. Si se aprueba, se construye imagen nueva y se actualiza solo el tag en `app-prod.yml`. |


## 2026-09-18 — Operación EC2 (encendido tras apagón, enrutamiento y accesos)

| # | Cambio | Detalle / evidencia |
|---|--------|---------------------|
| 1 | **Mongo no revivía al reiniciar la EC2** | Tras encender la EC2, `knstore-mongodb-1` estaba `Exited` (sin política de reinicio) y la app no levantaba. Se agregó `restart: unless-stopped` al servicio `mongodb` en `src/main/docker/mongodb-replicaset.yml` y en `/home/ubuntu/knstore/mongodb-replicaset.yml`; se recreó el contenedor (volumen intacto). Verificado: `mongo restart=unless-stopped`, `app restart=unless-stopped`, health 200, dominio 200. |
| 2 | **NPM ahora enruta por red interna** | Los 3 proxy hosts apuntaban a la IP pública (`44.197.126.33`), lo que impedía cerrar puertos sin romper los dominios. Se cambiaron a interno: `app.knstore.duckdns.org → knstore-app-1:8080`, `npm.knstore.duckdns.org → 127.0.0.1:81`, `portainer.knstore.duckdns.org → portainer:9000` (http). Se actualizó la BD (`proxy_host`) y los `*.conf` de `/data/nginx/proxy_host/` + `nginx -s reload`. Evidencia: los 3 dominios responden 200 desde la EC2 y desde Internet. |
| 3 | **NPM conectado a redes externas** | `/home/ubuntu/nginx/docker-compose.yml` ahora declara las redes externas `knstore` y `portainer_portainer_network` (persiste ante recreación de NPM). Respaldo del compose: `docker-compose.yml.bak-20260918-2336`. Respaldo de datos NPM: `/home/ubuntu/backups/npm-backup-20260918-2335.tgz`. |
| 4 | **Portainer: contraseña reseteada** | Se recuperó acceso con `docker run --rm -v portainer_portainer_data:/data portainer/helper-reset-password -password '...'` (usuario `admin`); login verificado por API (HTTP 200). La credencial la custodia el responsable; **no se registra en el repo**. Datos/stacks intactos. |
| 5 | **Secretos / accesos** | La app password de Gmail vigente se cargó en el `.env` de la EC2 y se verificó por SMTP. Se crearon usuarios de rol `manager@knstore.com` (MANAGER) y `cliente@knstore.com` (CLIENTE); los usuarios demo (`user`, `manager`, `cliente`, `jmetermanager`) quedaron desactivados. |
| 6 | **Documentación obligatoria** | Se creó esta bitácora y se añadió la regla a `AGENTS.md`: registrar todo cambio (incluida la infraestructura de la EC2) en `BITACORA.md` y actualizar `ESTADO_SESION.md`. |
| 7 | **Security Group restringido** | Inbound final: `22` solo desde `152.201.209.84/32` (IP del responsable), `80` y `443` abiertos a Internet; retiradas las reglas de `81`, `8080`, `9000` y `9443`. Verificado desde Internet el 2026-09-18: los 3 dominios responden 200, los puertos directos no responden (000) y el SSH sigue operativo. Si cambia la IP del responsable, actualizar la regla SSH (o entrar por EC2 Instance Connect). |

**Security Group:** aplicado el 2026-09-18 (ver #7): solo `22` (IP del responsable), `80` y `443`.

---

## 2026-09-15 — Cierre de calidad y lanzamiento a producción

**Calidad (todo verde al cierre):** unit 363/363 · IT 486/486 · Vitest 524/524 · `./mvnw verify` completo con modernizer · `tsc` limpio.

| # | Cambio | Detalle / evidencia |
|---|--------|---------------------|
| 1 | **H-01 ITs del carrito** | `ItemCarritoResourceIT` reescrito con `Producto`/`ProductoPrecio`/`Carrito` reales y aserción del precio server-side (17/17). |
| 2 | **H-02 modernizer** | 61 `Optional.get()` → `orElseThrow()` en 11 archivos de prueba; `verify` sin `-Dmodernizer.skip`. |
| 3 | **H-03/H-04 cobertura frontend** | `vitest.config.ts`: `coverage.include` sobre código propio (excluye `entities/modules/shared`), umbrales reales 45/35/40/45 en `coverage.thresholds`. |
| 4 | **H-06 requerimientos** | `requerimientos.md`: 95 estados → Implementado + anexados RF-070→076 y RNF-027→031. |
| 5 | **H-07 precio denormalizado (RF-072)** | `ProductoServiceImpl` escribe `precio_venta` al guardar; `ProductoPrecioServiceImpl` resuelve la referencia inversa (`precio.$id`). 2 pruebas unitarias nuevas; orden asc/desc verificado. |
| 6 | **H-08 imágenes y CSP** | `ProductoImagenRepository.findByProductoIdIn` con `@Query` y `ObjectId` (quirk `@DBRef`) + IT de regresión; `img-src` de la CSP ahora permite `images.unsplash.com` y `plus.unsplash.com`. Fotos visibles en producción. |
| 7 | **Hardening de producción** | `secret-samples` fuera del perfil prod; JWT por `JHIPSTER_SECURITY_AUTHENTICATION_JWT_BASE64_SECRET`; SMTP por env; `knstore.seed.demo-users=false`; admin inicial por `KNSTORE_SECURITY_ADMIN_PASSWORD`; `src/main/docker/app-prod.yml` + `.env.example`. |
| 8 | **Imágenes publicadas** | `eljoseph12/knstore:3.0.0` → `3.0.1` (precio_venta) → `3.0.3` (imágenes+CSP), todas con `latest`. |
| 9 | **Despliegue EC2** | `/home/ubuntu/knstore/app-prod.yml` + `.env` (600); volumen `knstore-mongodb-data` intacto (159 pedidos/facturas); 136 productos demo desactivados y **777 productos reales** activos con fotos; admin rotado; backups diarios 03:00 (`backup-mongo.sh`, retención 7 días). |
| 10 | **Scripts nuevos** | `scripts/seed-catalogo-real.js` (seed idempotente del catálogo real), `scripts/rotate-prod-users.js` (rotación de admin y baja de demo), `scripts/generar-informe-calidad.py` (regenera informe y resultados). |
| 11 | **Informes** | `docs/test_de_cobertura/informe-calidad/` (HTML/CSS/JS, números dinámicos, H-01→H-08) y `docs/test_de_cobertura/resultados-pruebas.html` (1.373 pruebas, 0 fallos, 129 suites). |
| 12 | **Git** | Commits en `main`, push a `origin` y mirror `sena-students`, 6 ramas unificadas y tags `v3.0.0`, `v3.0.1`, `v3.0.3`. |

# BITÁCORA DE CAMBIOS — KN-Store

> **Regla obligatoria del proyecto:** todo cambio (código, base de datos, infraestructura, configuración de servidores, decisiones y comandos ejecutados sobre la EC2) debe registrarse aquí el mismo día, con evidencia. Entradas nuevas **arriba**. Complementa a `ESTADO_SESION.md`.

---

## 2026-09-22 — Limpieza: eliminación de la rama `revert-3-lauraG`

| # | Cambio | Detalle / evidencia |
|---|--------|---------------------|
| 1 | **Respaldo previo (§12.1)** | El tip de la rama tenía 1 commit único no contenido en `main`: `c6cbf07 Revert "Docs: Manuales y planes del sistema knstore"` (revert del PR #3 creado por GitHub). Antes de borrarlo se respaldó en `refs/backup/2026-09-22/revert-3-lauraG` en **ambos** repos locales (referencia local, no se sube), preservando el commit. |
| 2 | **Rama eliminada de `origin`** | `git push origin --delete revert-3-lauraG` en `joseph12n/knstore` (`- [deleted] revert-3-lauraG`). El espejo **sena-students** nunca tuvo esa rama (verificado con `git ls-remote`). |
| 3 | **Resultado** | Los dos `origin` quedan exactamente con las 6 ramas oficiales (`main`, `Nicolas`, `carrito`, `joseph`, `lauraG`, `santiago`), sin ramas ajenas al flujo. |



## 2026-09-22 — Contenido de la tienda: manifiesto de catálogo y seed idempotente

| # | Cambio | Detalle / evidencia |
|---|--------|---------------------|
| 1 | **Manifiesto de contenido** | `contenido/catalogo.json` (v1) describe marcas, categorías/subcategorías y productos con precio, inventario e imágenes; el `slug` es la llave de idempotencia. `contenido/imagenes/README.md` fija la estructura `imagenes/<slug>/<orden>-<variante>.jpg` (≤500 KB, ~800 px, `01-principal` como única principal y `alt` descriptivo en español). Validado: JSON parseable (2 marcas, 1 categoría, 1 producto de ejemplo). |
| 2 | **Seed idempotente** | `scripts/seed-contenido.js` (289 líneas) lee el manifiesto y sube marcas, categorías, subcategorías, productos con precios e inventario e imágenes locales vía API REST, siguiendo el patrón de `scripts/seed-catalogo-real.js`: credenciales por entorno (`KNSTORE_USERNAME`/`KNSTORE_PASSWORD`), sin secretos reales en el repo. Idempotente por `slug` (crear si no existe, actualizar si existe; nunca duplica) y con flag `--force-images`. Evidencia: `node --check` OK. |
| 3 | **Higiene del repo** | `.gitignore` ahora excluye las carpetas de agentes IA (`.agent/`, `.claude/`, `.gemini/`, `.opencode/`): ~72 MB de skills duplicados (`impeccable`) que no deben versionarse ni subirse al espejo. |
| 4 | **Git** | Commit `ada5041` (`feat(config): agregar manifiesto de contenido y seed idempotente de la tienda`) → push a `origin` (`343a7a6..ada5041`) → mirror a **sena-students** (fast-forward al mismo SHA; `git log` verificado idéntico en ambos). Este registro documental cierra el flujo con las 6 ramas (`main`, `Nicolas`, `carrito`, `joseph`, `lauraG`, `santiago`) unificadas a `main` en ambos repos (solo fast-forwards; sin reinicios ni `refs/backup`). |



## 2026-09-18 — Release 3.1.1: fix visibilidad del sidebar del admin

| # | Cambio | Detalle / evidencia |
|---|--------|---------------------|
| 1 | **Ítems del sidebar admin en rojo y activo invisible** | La regla de enlaces del admin (`a:not(.btn):not(.nav-link):not(.dropdown-item)`) aplicaba también a `.admin-nav__item` y al botón "Tienda" del topbar: pintaba los ítems en acento rojo y dejaba el activo **rojo sobre rojo**. Se limitó al contenido: `.admin-content a:not(...)`. Verificado con capturas en claro y oscuro: ítems en color de texto secundario, activo rojo con texto blanco, "Tienda" con el color del tema. |
| 2 | **Pruebas** | Frontend **534/534**, `tsc` limpio. |
| 3 | **Release 3.1.1** | Imagen `eljoseph12/knstore:3.1.1` (+ `latest`, digest `sha256:d99ac4d8e3175414c11495f3da0f72b0eca65276b43ae1d39247faacb5d2f764`) publicada y desplegada en la EC2: `knstore-app-1` con 3.1.1 `Up (healthy)`, home 200, health 200, Mongo intacto. |
| 4 | **Git** | Commits, push a `origin`, mirror `sena-students`, 6 ramas unificadas a `main` y tag `v3.1.1`. |



## 2026-09-18 — Release 3.1.0: limpieza, regresión y despliegue

| # | Cambio | Detalle / evidencia |
|---|--------|---------------------|
| 1 | **Limpieza de código** | Eliminados los archivos muertos `modules/login/login.tsx` y `modules/login/login-modal.tsx` (reemplazados por `landing/pages/LoginPage.tsx`) y `.vscode/launch.json` (apuntaba al export viejo de tests). `.vscode/settings.json` queda versionado. Sin referencias rotas (`grep` + `tsc`). |
| 2 | **Regresión completa** | Unit **363/363** · IT **488/488** · Frontend **534/534** (56 archivos) · `tsc` limpio · `verify` con modernizer y JaCoCo. Cobertura: backend **70,1% líneas**, frontend **50,97%**. |
| 3 | **Informe de calidad** | `informe-calidad/js/datos.js` y `resultados-pruebas.html` regenerados con las cifras finales (129 suites). |
| 4 | **Imagen 3.1.0** | `eljoseph12/knstore:3.1.0` (+ `latest`, digest `sha256:e5b6afff4fedb60c1df279fb30173420b6ac06d5815be4b27f6ff0d2786a5ca4`) publicada en Docker Hub. |
| 5 | **Despliegue EC2** | `src/main/docker/app-prod.yml` actualizado a 3.1.0; en el servidor `docker compose pull && docker compose up -d`. Mongo conservó el mismo contenedor (datos intactos). Verificado: `knstore-app-1` con la imagen 3.1.0 `Up (healthy)`, `/management/health` 200, home 200, nueva página de login servida y capturada, 777 productos activos / 160 pedidos / 17 usuarios. |
| 6 | **Git** | Commits, push a `origin`, mirror `sena-students`, 6 ramas unificadas a `main` y tag `v3.1.0`. |



## 2026-09-18 — Fix visibilidad de la navegación del header

| # | Cambio | Detalle / evidencia |
|---|--------|---------------------|
| 1 | **"Ingresar" y las categorías invisibles en modo oscuro** | Al excluir `.nav-link` de la regla genérica de enlaces, estos quedaron con `--bs-nav-link-color` (negro del tema base de Cyborg) sobre el header oscuro. Se fijó con tokens: `.storefront .nav-link { color: var(--kn-color-text) }` y `.storefront .dropdown-item { color: var(--kn-color-text) }`, además de `.storefront .btn-link { color: var(--kn-color-text) }` (toggle de tema). Verificado con capturas del header en claro y oscuro: marca, toggle, "Ingresar", carrito y menú de categorías visibles y consistentes. |
| 2 | **Pruebas** | Frontend **534/534** (56 archivos) y `tsc` limpios. |



## 2026-09-18 — Fix bucle de recarga en el inicio (Service Worker)

| # | Cambio | Detalle / evidencia |
|---|--------|---------------------|
| 1 | **El inicio se quedaba en bucle de recargas** | Causa raíz: `registerServiceWorker()` se ejecutaba **también en desarrollo** (`app/index.tsx`), registrando el SW de Workbox; el SW servía bundle/index cacheados y, combinado con HMR, provocaba el bucle de recargas y el error de montaje en `LandingLayout`. Fix: el SW solo se registra con `process.env.NODE_ENV === 'production'`; en dev se **desregistran** los SW existentes y se limpian las `caches` (libera al navegador sin pasos manuales). Verificado con CDP: carga limpia con 0 SW; un SW registrado manualmente queda en 0 tras recargar, con 1 sola recarga (sin bucle). |
| 2 | **Pruebas** | Frontend **534/534** (56 archivos), `tsc` y webpack limpios. |
| 3 | **Si persiste en un navegador ya afectado** | Un reload normal basta (el documento se pide a red); si no, DevTools → Application → Service Workers → *Unregister* y *Clear site data*. |



## 2026-09-18 — Fix carrito oscuro, login, registro y botones del admin

| # | Cambio | Detalle / evidencia |
|---|--------|---------------------|
| 1 | **Carrito (drawer) en modo oscuro** | React-Bootstrap renderiza el `Offcanvas` en un portal fuera de `.storefront`, por lo que no heredaba los tokens y quedaba blanco. Se pasó `container={() => document.querySelector('.storefront') ?? document.body}` en `CartDrawer` y se invierte el `btn-close` en oscuro. Captura verificada con ítems reales: fondo oscuro, tarjetas y textos legibles. |
| 2 | **Sección de cuentas demo en el registro** | Se eliminó el bloque `Alert` de "cuentas predeterminadas" en `modules/account/register/register.tsx` (y el import de `Link` que quedaba sin uso). Ya no expone admin/admin en la UI. |
| 3 | **Login rediseñado** | Nuevo `landing/pages/LoginPage.tsx` (reemplaza al modal de JHipster en la ruta `/login`): tarjeta de dos columnas con panel de marca, formulario con tokens del storefront, toggle claro/oscuro, redirección por rol (ADMIN/MANAGER/cliente) y spec de render (`LoginPage.spec.tsx`). Capturas claro/oscuro verificadas. |
| 4 | **Botones de acciones del admin** | Las acciones de tabla son `<a class="btn ...">`; la regla `.admin-shell a { color: accent }` pisaba el color del botón (texto rojo sobre rojo en "Eliminar"). Se excluyeron los enlaces-botón: `.admin-shell a:not(.btn):not(.nav-link):not(.dropdown-item)` (igual en `storefront.scss`). Verificado en claro y oscuro: Vista neutro, Editar/Eliminar rojos con texto blanco. |
| 5 | **Pruebas** | Frontend **534/534** (56 archivos), `tsc` limpio, webpack sin errores. |



## 2026-09-18 — Fix modo oscuro: hero y hovers

| # | Cambio | Detalle / evidencia |
|---|--------|---------------------|
| 1 | **Hero invisible en modo oscuro** | `HeroBanner` usaba `backgroundColor: var(--kn-color-primary)` (que en oscuro se invierte a claro) con título por token inverso y subtítulo blanco fijo → subtítulo invisible. Se fijó el hero como bloque de marca **siempre oscuro** (`#111111`) con título blanco y subtítulo `rgba(255,255,255,.85)`, igual en ambos temas. Verificado con capturas 1440 y 390 en oscuro. |
| 2 | **Hovers inconsistentes en oscuro** | Red de seguridad con tokens en `storefront.scss` y `admin.scss`: `nav-link`, `dropdown-item`, `list-group-item-action`, `page-link`, `btn-link` y enlaces del footer usan `--kn-color-accent` en `:hover`/`:focus`, para que ningún componente caiga en colores oscuros de Bootstrap sobre fondos oscuros. |
| 3 | **Loop de recarga con sesión** | Verificado con CDP en idle (12 s en `/admin` y en `/`): **0 navegaciones/recargas**. El comportamiento observado es el *live reload* de webpack al guardar archivos durante el desarrollo (se detiene al terminar de editar), no un bucle de la aplicación; si se reproduce sin ediciones, escalar. |
| 4 | **Pruebas** | Frontend **533/533**, `tsc` limpio. |



## 2026-09-18 — Fix responsive del header (dropdown de cuenta)

| # | Cambio | Detalle / evidencia |
|---|--------|---------------------|
| 1 | **El dropdown de cuenta rompía el header en <992px** | Causa: Bootstrap 5 aplica `position: static` a los `.dropdown-menu` dentro del navbar en <lg (asume un collapse que nuestro header no usa); el menú entraba al flujo, estiraba el nav-item (216×254 px) y desalineaba los iconos (el carrito caía a otra fila, el toggle quedaba oculto). Fix en `storefront.scss`: `.storefront .storefront-header .dropdown-menu { position: absolute; max-width: calc(100vw - 1.5rem); }`. Verificado con CDP a 390/540/790/1440: el menú flota alineado a la derecha, dentro del viewport, y el header queda en una sola fila. |
| 2 | **Pruebas** | Frontend **533/533** y `tsc` limpios tras el cambio (solo CSS). |



## 2026-09-18 — Fase 2 panel administrativo propio (local)

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



## 2026-09-18 — Fase 1 landing: modo oscuro y secciones nuevas (local)

| # | Cambio | Detalle / evidencia |
|---|--------|---------------------|
| 1 | **Modo oscuro con toggle** | Tokens `[data-theme='dark']` en `landing/styles/tokens.css`; `StorefrontLayout` gestiona el tema (`data-theme` en `.storefront`, persistencia en `localStorage` `kn-theme`, inicial por `prefers-color-scheme`); botón sol/luna en `StoreHeader`. Fondos `#f8f9fa` tokenizados (`ProductCard`, categorías) para que el tema aplique. Evidencia: capturas CDP con `prefers-color-scheme` emulado (claro y oscuro a 1440 y 360). |
| 2 | **Secciones nuevas en el home** | `BenefitsBar` (envío gratis ≥$150.000, pagos, garantía, soporte), `OffersSection` (productos con descuento y completado con más baratos), `BrandStrip` (`/api/marcas`), `Testimonials` (3 opiniones) y `NewsletterCta` (validación de correo + toast). Integradas en `StoreHome` en ese orden. |
| 3 | **Fix de contraste del newsletter** | `.storefront h3/p` pisaban el color del banner y el texto quedaba invisible en oscuro; se agregó la clase `.kn-newsletter` en `storefront.scss` con colores por token (claro: banner oscuro/texto blanco; oscuro: banner claro/texto oscuro). Verificado con capturas. |
| 4 | **Pruebas** | 5 specs nuevas (Benefits, Testimonials, Newsletter con interacción, BrandStrip con axios mockeado, OffersSection): **532/532** frontend, 54 archivos, cobertura 50,19% sentencias / 50,48% líneas; `tsc` y webpack sin errores. |
| 5 | **Alcance** | Cambios **solo locales** (webpack dev + backend dev activos para revisión). Sin despliegue a la EC2. |



## 2026-09-18 — Fase 0 mejoras de front (local)

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

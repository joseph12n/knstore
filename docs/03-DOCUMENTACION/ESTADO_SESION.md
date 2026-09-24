# ESTADO_SESION.md — Handoff (actualizado 2026-09-24)

> ⚠️ OBLIGATORIO: leer este archivo completo antes de tocar nada.
> **Regla obligatoria:** todo cambio debe quedar registrado en `docs/03-DOCUMENTACION/BITACORA.md` (entradas nuevas arriba) y reflejado aquí cuando cambie el estado.
> Resume el cierre de calidad (H-01→H-08), el despliegue en producción y la operación de la EC2 del 2026-09-18, el flujo de contenido de la tienda del 2026-09-22, el release 3.3.0 del 2026-09-23 (Quality Gate de SonarCloud en verde) y el **wipe + re-siembra completa de la BD de producción del 2026-09-24** (60 productos con fotos locales + operaciones demo).

---

## 1. Git — estado

- Rama **main** con los commits de calidad/hardening y las correcciones E2E del lanzamiento (ver `git log --oneline -15`).
- **2026-09-23 — release 3.3.0 publicado:** 5 commits del hito (`73e089f`…`48642a9`) + `49647e1` (bump de `app-prod.yml` a 3.3.0) + registro del release → push a `origin` y mirror a **sena-students** (mismo SHA), **6 ramas unificadas** a `main` en ambos repos (tips previos en `refs/backup/2026-09-23/`) y tag anotado **`v3.3.0`** sobre el estado final. Imagen `eljoseph12/knstore:3.3.0` desplegada en la EC2 y **Quality Gate de SonarCloud OK** (duplicación nueva 1,12 %).
- **2026-09-23 (publicado en el release 3.3.0, Fase 4b):** panel admin oculto/bloqueado en viewports ≤ 992px — hook `useIsMobileView`, aviso `DesktopOnlyNotice`, gate en `app.tsx`, redirect en `LoginPage`, toast en `StorefrontLayout` y enlaces ocultos en `StoreHeader` + 4 specs nuevos y 1 ampliado (20 pruebas nuevas; regresión `landing/` 97/97). `tsc`/`prettier`/`eslint` limpios. Detalle en `BITACORA.md`.
- **2026-09-23 (publicado en el release 3.3.0, Fase 3b):** `app/landing/model/divipola.ts` + rediseño de `AddressForm.tsx` (cascada Departamento → Municipio) y `<Modal>` en `AddressesPage.tsx`. Detalle en `BITACORA.md`.
- **2026-09-23 (publicado en el release 3.3.0, Fase 2c):** `scripts/seed-operaciones.js` (seed idempotente de operaciones vía API: clientes, cuentas, direcciones, checkout, estados de pedido/envío y resumen) + CSP con `https://images.pexels.com` en `application.yml`. **Ejecutado en dev (3 corridas) y en PRODUCCIÓN (2026-09-23, doble corrida EXIT=0):** 5 clientes + 5 cuentas + 7 direcciones + 7 pedidos multi-estado (`PED-20260924-000001…000007`), 7 pagos APPROVED, 7 facturas, diff de conteos exacto (+5/+7/+7), 160 pedidos reales intactos y ownership de `ana.gomez` OK (1 pedido). Se agregó un **fallback de solo-lectura** en `cargarProductos()` → `/productos/search` porque `GET /api/productos` devuelve **500 preexistente en prod** por el producto inactivo corrupto `6a8f96f544303c4d9331990f` (bug reportado en BITACORA; el storefront no lo ve). Detalle en `BITACORA.md`.
- **2026-09-22:** commit `ada5041` (`feat(config): agregar manifiesto de contenido y seed idempotente de la tienda`) con `contenido/catalogo.json`, `contenido/imagenes/README.md` y `scripts/seed-contenido.js`; push a `origin` y mirror a **sena-students** (mismo SHA). Las 6 ramas quedan unificadas a `main` en ambos repos.
- **2026-09-22:** rama `revert-3-lauraG` (revert del PR #3, commit único `c6cbf07`) eliminada de `origin` tras respaldar su tip en `refs/backup/2026-09-22/revert-3-lauraG` (local, en los dos repos). Los dos `origin` quedan solo con las 6 ramas oficiales.
- **2026-09-22 (tarde):** commits `82e326c` (fix del lint: `eslint.config.ts` ignora las carpetas de skills de agentes IA; `scripts/seed-contenido.js` formateado con prettier), `b83dda2` y `3f603b5` (informe de calidad regenerado con la corrida de cobertura del mismo día) → push a `origin` y mirror a **sena-students** (SHA `3f603b5`), 6 ramas unificadas a `main` en ambos repos.
- Tags de release: `v3.0.0` (hardening), `v3.0.1` (precio_venta RF-072), `v3.0.3` (imágenes + CSP), `v3.1.0` (front: fixes responsive, modo oscuro, secciones, login y admin), `v3.1.1` (sidebar admin), `v3.2.0` (manifiesto v2, cascada divipola, panel solo escritorio y seed operacional), **`v3.3.0`** (contenido v3 con imágenes reales + fix de duplicación del Quality Gate).
- Push a `origin` (joseph12n/knstore), mirror a **sena-students**, 6 ramas unificadas a `main` y backups de ramas en `refs/backup/2026-09-18/` y `refs/backup/2026-09-23/`.
- Imagen publicada en Docker Hub: **`eljoseph12/knstore:3.3.0`** (= `latest`, digest `sha256:2d3b62f1…`).

## 2. Calidad — todo verde

- Unit 363/363 · IT 487/487 · Vitest **563/563** (61 archivos) · `./npmw test` (lint + Vitest + umbrales) en verde · `./mvnw verify` completo (modernizer incluido) · `tsc` limpio. (Unit/IT del 2026-09-22; Vitest revalidado el 2026-09-23 en el pre-vuelo del release 3.3.0; la corrida del IDE reportó 853/853.)
- Cobertura backend (consolidada unit+IT): **70,1% líneas** / 41,3% ramas / 88,6% métodos / 194 clases.
- Cobertura frontend: **59,73% líneas** (1.108/1.855) sobre **81 archivos propios** (corrida del 2026-09-23); excluye generado y aplica umbrales 45/35/40/45.
- Informe visual: `docs/test_de_cobertura/informe-calidad/index.html` y `docs/test_de_cobertura/resultados-pruebas.html`; se regeneran con `python3 scripts/generar-informe-calidad.py`.

## 3. Producción — desplegado y verificado

- **EC2 `44.197.126.33`** (`app.knstore.duckdns.org`) con Nginx Proxy Manager + SSL.
- Imagen **`eljoseph12/knstore:3.3.0`** (digest `sha256:2d3b62f1…`) con **perfil prod** desplegada desde `/home/ubuntu/knstore/app-prod.yml` + `.env` (chmod 600; JWT, SMTP y URI rs0 por entorno); release 3.3.0 desplegado el 2026-09-23 con `knstore-app-1` healthy y smoke interno/externo 200/200/200 + 401/401.
- **2026-09-24 — wipe + re-siembra completa (aprobada por el responsable):** BD `knstore` vaciada (incl. `mongockChangeLog`) y re-sembrada desde cero: **60 productos / 179 imágenes locales (blob)** del catálogo v2, **7 pedidos** multi-estado (CONFIRMED 2 · SHIPPED 2 · DELIVERED 2 · CANCELLED 1) con 7 pagos/envíos/facturas, **6 usuarios** (admin + 5 clientes demo `ana.gomez`…`lucia.martin`), 5 cuentas y 7 direcciones. Mongock re-corrió las **9 migraciones** y el admin inicial se recreó desde el `.env`. Los 982 productos/1099 imágenes y 167 pedidos/22 usuarios anteriores quedaron **solo en el backup** `backups/pre-wipe-20260924.archive.gz` (verificado: 6055 docs restaurables; los 777 reales y los 69 viejos **no** se recargaron). El 500 preexistente de `GET /api/productos` (producto inactivo corrupto) desapareció con el wipe.
- **`.env` de la EC2 (2026-09-24):** ahora incluye `KNSTORE_SECURITY_ADMIN_LOGIN/PASSWORD/EMAIL` (imprescindibles para recrear el admin cuando la BD se vacía; copia previa en `.env.bak-pre-wipe-20260924`). Tras un wipe: **no** basta `docker restart` (el env se lee al crear el contenedor) → `docker compose up -d --no-deps app`. La contraseña del admin es **de prueba y debe rotarse** (pendiente del responsable, junto con la de los clientes demo `Demo1234!`).
- SMTP verificado con la app password vigente (no está en el repo).
- Backup diario a las 03:00 (`/home/ubuntu/knstore/backup-mongo.sh`, retención 7 días) + backups manuales en `backups/`.
- Smoke: health UP, home/detalle/categoría 200, búsqueda y orden por precio OK, `/management/prometheus` y `/v3/api-docs` 401.
- **2026-09-18 — reinicios:** Mongo ahora tiene `restart: unless-stopped` (no revivía al encender la EC2; ya aplicado en repo y servidor).
- **2026-09-18 — NPM interno:** los proxy hosts apuntan a `knstore-app-1:8080`, `127.0.0.1:81` y `portainer:9000`; NPM está en las redes `knstore` y `portainer_portainer_network` (compose en `/home/ubuntu/nginx/docker-compose.yml`). Dominios verificados 200 dentro y fuera.
- **2026-09-18 — Portainer:** contraseña reseteada con el helper oficial (la credencial la custodia el responsable; no está en el repo).
- **2026-09-18 — Security Group:** solo `22` (IP del responsable), `80` y `443`; `81/8080/9000/9443` bloqueados desde Internet (verificado). Si cambia la IP del responsable, actualizar la regla SSH o entrar por EC2 Instance Connect.

## 4. Pendientes inmediatos

1. Backlog de calidad: specs de páginas del panel `/cuenta` y admin, casos negativos (H-05: ramas de 41% a ~60%) e imágenes de categorías (hoy son placeholders por diseño).
2. Rotar periódicamente la app password de Gmail, el secreto JWT del `.env` y la contraseña del administrador; **tras el wipe del 2026-09-24 quedó la credencial de prueba** (rotarla con `scripts/rotate-prod-users.js`).
3. Mantener actualizada la regla SSH del Security Group si cambia la IP del responsable.
4. **(Acción del responsable)** Recuperar el export del IDE de las 14:04 del 2026-09-22 (`Test Results - java_in_knstore.html`, 853/853 en 41,77 s) desde la Historia local de IntelliJ y volverlo a commitear; por el incidente documentado en `BITACORA.md` (fila 9) hoy queda en su lugar la exportación anterior (848 total / 4 failed).

## 5. Entorno y quirks

- **Documentación:** cada cambio (código, infra, EC2) se registra en `docs/03-DOCUMENTACION/BITACORA.md`; este archivo refleja el estado vigente.
- **Contenido de la tienda (2026-09-23):** el manifiesto `contenido/catalogo.json` (v2, **60 productos / 179 imágenes**) lo mantiene el agente `contenido-tienda` y lo carga `scripts/seed-contenido.js` (idempotente por `slug`; imágenes locales en `contenido/imagenes/<slug>/`). Las fotos ya **no** son URLs de Unsplash: se descargan de **Zappos** con `scripts/scraper-zapatos.js` (curaduría en `contenido/scraping/listing.json`, QA visual en `contenido/scraping/qa.json`) y se cargan por base64 (idempotencia local por `alt`; cambiar de URL a local exige `--force-images`). Cargado en dev con doble corrida sin duplicados (243 productos / 484 imágenes: 305 externas ajenas + 179 locales) y **en PROD el 2026-09-24 tras el wipe** (60/179, doble corrida: la 2.ª = 0 creados/0 subidas). Credenciales por entorno `KNSTORE_USERNAME`/`KNSTORE_PASSWORD`. Las carpetas de agentes IA (`.agent/`, `.claude/`, `.gemini/`, `.opencode/`) están en `.gitignore` (skills duplicados, ~72 MB): no versionarlas.
- **Lint (2026-09-22):** `eslint.config.ts` ignora las carpetas de skills de agentes IA (`.opencode/`, `.agent/`, `.claude/`, `.gemini/`) — antes rompían `npm test` con 5831 errores. Si se agregan scripts a `scripts/`, formatearlos con prettier: `ci:frontend:test` ejecuta `npm test` (con lint) también en clones limpios.
- **Reportes QA en GitHub Pages (repo `joseph12n/pruebas`, 2026-09-22):** el sitio KN·QA Observatory tiene ahora el informe de **Cobertura** (JaCoCo unit+IT + Vitest, datos del 2026-09-22) además de maestro/unitarias/e2e/jmeter/lighthouse, y quedó **reestructurado con Jekyll** (shell único en `_layouts/` + `_includes/`; las páginas solo llevan front matter + contenido). El Quality Gate de **SonarCloud quedó OK (5/5)**: fiabilidad A (21 bugs cerrados, incluido 1 crítico de CSS) y duplicación 0,0 % (Auto-Scan ignora `sonar-project.properties`; la duplicación real del shell se eliminó con Jekyll). Verificación: diff de píxel RMSE = 0 vs el sitio previo. Se regenera con `tools/build_data.py` desde `docs/cobertura/` (jacoco.csv + vitest-resumen.json); build local con `docker run --rm -v "$PWD":/srv/jekyll jekyll/jekyll:4 jekyll build`. El detalle vive en `~/Descargas/pruebas/CONTEXT.md`.
- **NPM (EC2):** nunca apuntar un proxy host a la IP pública; usar nombres internos (`knstore-app-1`, `portainer`) o `127.0.0.1` para NPM admin. Si NPM se recrea, su compose ya incluye las redes externas.
- **Actualizar la EC2:** solo se cambia `image:` en `/home/ubuntu/knstore/app-prod.yml` (el `docker-compose.yml` es un symlink a ese archivo) y se ejecuta `docker compose pull && docker compose up -d`. No tocar `.env`, `mongodb-replicaset.yml` ni el `name: knstore`.
- `docker-compose.legacy-dev.yml.bak` es el compose viejo (perfil dev con seed/prometheus): no usar.
- Mongo dev rs0: `docker compose -f src/main/docker/services.yml up -d --wait` → `127.0.0.1:27018`; `my-mongo` (27017) es standalone, no usar para checkout.
- Boot 4: la URI de Mongo es `spring.mongodb.uri` → env **`SPRING_MONGODB_URI`**.
- `@DBRef`: las consultas por lote contra `ref.$id` requieren `ObjectId` (`MongoIdUtils`); ver `ProductoImagenRepository.findByProductoIdIn`.
- **CSP:** si se agregan hosts de imágenes externas hay que incluirlos en `jhipster.security.content-security-policy` (`img-src`).
- La imagen prod se construye con `-Pprod` (compila el frontend); verificar RAM antes (webpack es el pico).

## 6. Trabajo en curso — plan de front (local)

Plan aprobado 2026-09-18 (local primero; el despliegue a la EC2 se decide al cierre). Estado:

- **Fase 0 — completada:** botón "Añadir" de `ProductCard` (responsive 360→1920 + estilo compacto) y envío gratis visible en el checkout (preview desde el paso de dirección, "Gratis" con costo tachado). Pruebas: `CheckoutServiceIT` 7/7 (2 nuevas), `CheckoutPage.spec` 7/7, frontend 525/525, `tsc` limpio. Detalle y capturas en `BITACORA.md`.
- **Fase 1 — completada:** modo oscuro con toggle persistente (`data-theme` + `kn-theme` + `prefers-color-scheme`) y 5 secciones nuevas en el home (Beneficios, Ofertas, Marcas, Testimonios, Newsletter). Frontend 532/532, cobertura 50,19% sentencias. Detalle y capturas en `BITACORA.md`.
- **Fase 2 — completada:** shell propio `AdminLayout` (sidebar + topbar con tema claro/oscuro), dashboard `/admin` con KPIs, CRUDs generados tematizados (tokens del storefront) y fix de botones del tema (se eliminó el gradiente de Bootswatch; AÑADIR negro, crear rojo). Frontend 533/533. Detalle y capturas en `BITACORA.md`.
- **Backlog opcional del admin:** páginas propias de edición rápida para productos/inventario (hoy CRUDs generados tematizados); specs de las páginas del panel `/cuenta`; casos negativos (H-05).

# ESTADO_SESION.md — Handoff (actualizado 2026-09-24)

> ⚠️ OBLIGATORIO: leer este archivo completo antes de tocar nada.
> **Regla obligatoria:** todo cambio debe quedar registrado en `docs/03-DOCUMENTACION/BITACORA.md` (entradas nuevas arriba) y reflejado aquí cuando cambie el estado.
> Resume el hito **2026-09-23/24**: release **3.3.0** (contenido v3 con 60 productos e imágenes locales de Zappos, fix de duplicación del Quality Gate de SonarCloud, imagen en Docker Hub y despliegue en la EC2) y el **wipe + re-siembra completa de la BD de producción del 2026-09-24** (60 productos/179 imágenes + operaciones demo). Incluye el handoff previo del 2026-09-18 (calidad H-01→H-08, EC2) y el flujo de contenido del 2026-09-22.

---

## 1. Git — estado

- Rama **main** en **`b26be02`** (`docs: registrar la re-siembra completa de produccion`), working tree limpio. `origin` (`joseph12n/knstore`) y el espejo **sena-students** están en el **mismo SHA** (`b26be02`) con las **6 ramas** (`main`, `Nicolas`, `carrito`, `joseph`, `lauraG`, `santiago`) apuntando a `main`.
- **Release 3.3.0 (2026-09-23):** **7 commits** `73e089f`…`77e5403` — fix del gate (`73e089f`), scraper (`dc65ed9`), catálogo v3 (`688adc6`), idempotencia/fallback (`949fe32`), bitácora (`48642a9`), bump de `app-prod.yml` (`49647e1`) y registro del release (`77e5403`) → push a `origin` + mirror, **6 ramas unificadas** y tag anotado **`v3.3.0`** sobre `77e5403` (`2bfae103`, presente en ambos repos). Tips previos de las ramas respaldados en `refs/backup/2026-09-23/` y `refs/backup/2026-09-24/` (local a los dos repos, no se suben).
- **Fases 2c/3b/4b (2026-09-23)** — seed operacional, cascada DIVIPOLA y panel admin solo escritorio — se publicaron en el **release 3.2.0** (tag anotado sobre `15f4588`, 2026-09-23 21:05), junto con el manifiesto v2 y el host Pexels en la CSP (`f7472ca`, `888819f`, `e0fc34f`, `15ddb12`, `a591e75`).
- Tags de release: `v3.0.0` (hardening), `v3.0.1` (precio_venta RF-072), `v3.0.3` (imágenes + CSP), `v3.1.0` (front: fixes responsive, modo oscuro, secciones, login y admin), `v3.1.1` (sidebar admin), `v3.2.0` (manifiesto v2, cascada divipola, panel solo escritorio y seed operacional), **`v3.3.0`** (contenido v3 con imágenes reales + fix de duplicación del Quality Gate).
- Imagen publicada en Docker Hub: **`eljoseph12/knstore:3.3.0`** (= `latest`, digest `sha256:2d3b62f1cc94c4e90e0388cd257230aebe5c7240ad5394fed5fda44df0d03a75`; verificado por API el 2026-09-24 04:44 UTC).
- **SonarCloud** (`joseph12n_knstore`): Quality Gate **OK (5/5)** con **1,1 % de duplicación en código nuevo** (umbral 3 %); análisis `b24e740b…` (rev `77e5403`, 2026-09-24T04:53Z) y `ac037261…` (rev `b26be02`, 2026-09-24T05:29Z) verificados por API pública. Auto-Scan ignora `sonar-project.properties` (ver §5).

## 2. Calidad — todo verde

- Backend: Unit **363/363** · IT **488/488** = **851/851** (informe consolidado, 0 fallos; corrida fresca del 2026-09-22: IT 487/487 — el 488 del informe incluye el `.txt` residual de `ListadosQueryDebugIT` del 24-ago). `./mvnw verify` completo con modernizer y JaCoCo.
- Frontend: Vitest **563/563** (61 archivos) y `./npmw test` (lint + Vitest + umbrales) en verde; `tsc`/prettier/eslint limpios (pre-vuelo del release 3.3.0, 2026-09-23).
- Cobertura backend (consolidada unit+IT): **70,1 % líneas** / 41,3 % ramas / 88,6 % métodos / 194 clases.
- Cobertura frontend: **59,73 % líneas** (1.108/1.855) sobre **81 archivos propios** (corrida del 2026-09-23); excluye generado y aplica umbrales 45/35/40/45.
- Informe visual: `docs/test_de_cobertura/informe-calidad/index.html` y `docs/test_de_cobertura/resultados-pruebas.html`; se regeneran con `python3 scripts/generar-informe-calidad.py`.

## 3. Producción — desplegado y verificado

- **EC2 `44.197.126.33`** (`app.knstore.duckdns.org`) con Nginx Proxy Manager + SSL.
- Imagen **`eljoseph12/knstore:3.3.0`** (digest `sha256:2d3b62f1…`) con **perfil prod** desplegada desde `/home/ubuntu/knstore/app-prod.yml` + `.env` (chmod 600; JWT, SMTP y URI rs0 por entorno). `knstore-app-1` healthy; health externo verificado el 2026-09-24: **200/UP**.
- **2026-09-24 — wipe + re-siembra completa (aprobada por el responsable):** backup verificado `/home/ubuntu/knstore/backups/pre-wipe-20260924.archive.gz` (165.481 B, sha256 `b9cd3f1c…`, **6.055 docs** restaurados en `knstore_verify` con 0 fallos); BD `knstore` vaciada (incl. `mongockChangeLog`); re-arranque con Mongock **9/9** y admin recreado desde el `.env` (`POST /api/authenticate` 200).
- **Conteos finales:** **60 productos** (con `precio_venta` e inventario), **179 imágenes locales** (blob JPEG 1000×1000; 0 con `imagenUrl`), 10 marcas, 4 categorías, 9 subcategorías, 2 IVA, 146 etiquetas; **6 usuarios** (admin + 5 clientes demo), 5 cuentas, 7 direcciones; **7 pedidos** (CONFIRMED 2 · SHIPPED 2 · DELIVERED 2 · CANCELLED 1) con 7 pagos APPROVED, 7 envíos, 7 facturas y 58 filas de historial; `carrito`/`item_carrito` en 0. Los 982 productos/1099 imágenes y 167 pedidos/22 usuarios anteriores quedaron **solo en el backup** (los 777 reales y los 69 viejos **no** se recargaron). El 500 preexistente de `GET /api/productos` (producto inactivo corrupto) desapareció con el wipe.
- **Seeds (doble corrida idempotente, 2026-09-24):** `seed-contenido.js` (corrida 1: 60 productos/179 imágenes; corrida 2: 0 creados/0 subidas) y `seed-operaciones.js` (corrida 1: +5 usuarios/cuentas y +7 direcciones/pedidos/pagos/envíos/facturas; corrida 2: 0 creados).
- **`.env` de la EC2 (2026-09-24):** incluye `KNSTORE_SECURITY_ADMIN_LOGIN/PASSWORD/EMAIL` (necesarias para recrear el admin tras un wipe; copia previa en `.env.bak-pre-wipe-20260924`). Tras un wipe **no** basta `docker restart`: `docker compose up -d --no-deps app`. La contraseña del admin y la de los clientes demo (`Demo1234!`) son **de prueba y deben rotarse** (pendiente del responsable).
- Smoke prod: home/productos/buscar/detalle 200 (detalle con imágenes `data:image/jpeg;base64,…`), `/management/prometheus` y `/v3/api-docs` 401, ownership de `ana.gomez` OK (1 pedido). SMTP verificado con la app password vigente (no está en el repo).
- Backups: `backup-mongo.sh` diario a las 03:00 (retención 7 días) + backups manuales en `backups/`.
- **2026-09-18 — reinicios:** Mongo con `restart: unless-stopped` (repo y servidor).
- **2026-09-18 — NPM interno:** proxy hosts → `knstore-app-1:8080`, `127.0.0.1:81` y `portainer:9000`; NPM en las redes `knstore` y `portainer_portainer_network`. Dominios verificados 200 dentro y fuera.
- **2026-09-18 — Portainer:** contraseña reseteada con el helper oficial (la credencial la custodia el responsable; no está en el repo).
- **2026-09-18 — Security Group:** solo `22` (IP del responsable), `80` y `443`; `81/8080/9000/9443` bloqueados desde Internet. Si cambia la IP del responsable, actualizar la regla SSH o entrar por EC2 Instance Connect.

## 4. Pendientes inmediatos

1. **(Responsable) Rotar credenciales de producción:** admin de prueba creado desde el `.env` tras el wipe y clientes demo con `Demo1234!` → `node scripts/rotate-prod-users.js https://app.knstore.duckdns.org`; rotar también periódicamente la app password de Gmail y el secreto JWT del `.env`.
2. Backlog de cobertura frontend: specs de páginas del panel `/cuenta` y admin, páginas sin cobertura (`StoreHome`, `CartPage`, `CartDrawer`, `navItems.ts`) y casos negativos (H-05: ramas de 41 % a ~60 %).
3. **Peso de los listados con imágenes locales:** `GET /api/productos` serializa las imágenes blob en base64 (~1,6 MB por página de 12 productos); evaluar un endpoint de bytes/miniaturas o servir las imágenes como estáticos.
4. **Facturas con `cufe`/`codigoQr` nulos** (preexistente; en dev 16/16): definir la generación real o documentar el stub.
5. Mantener actualizada la regla SSH del Security Group si cambia la IP del responsable.
6. **(Acción del responsable)** Recuperar el export del IDE de las 14:04 del 2026-09-22 (`Test Results - java_in_knstore.html`, 853/853 en 41,77 s) desde la Historia local de IntelliJ y volverlo a commitear; por el incidente documentado en `BITACORA.md`, hoy queda la exportación anterior (848 total / 4 failed).

## 5. Entorno y quirks

- **Documentación:** cada cambio (código, infra, EC2) se registra en `docs/03-DOCUMENTACION/BITACORA.md`; este archivo refleja el estado vigente.
- **AGENTS.md sincronizados (2026-09-24):** el `AGENTS.md` raíz y `docs/03-DOCUMENTACION/AGENTS.md` tienen el mismo contenido (solo difiere la línea de autoreferencia de cada archivo); actualizar **ambos** a la vez y verificar con `npx prettier --check`.
- **Contenido de la tienda (2026-09-24):** el manifiesto `contenido/catalogo.json` **v2 (60 productos / 179 imágenes locales)** lo mantiene el agente `contenido-tienda` y lo carga `scripts/seed-contenido.js` (idempotente por `slug`; imágenes locales identificadas por `alt`; `--force-images` para reemplazarlas). Las fotos se descargan de **Zappos** con `scripts/scraper-zapatos.js` (curaduría en `contenido/scraping/listing.json`, QA visual en `contenido/scraping/qa.json` — **179/179 aprobadas**) y se cargan por base64. Credenciales por entorno `KNSTORE_USERNAME`/`KNSTORE_PASSWORD`. Las carpetas de agentes IA (`.agent/`, `.claude/`, `.gemini/`, `.opencode/`) están en `.gitignore` (skills duplicados, ~72 MB): no versionarlas.
- **Lint:** `eslint.config.ts` ignora las carpetas de skills de agentes IA (`.opencode/`, `.agent/`, `.claude/`, `.gemini/`) — antes rompían `npm test` con 5.831 errores. Si se agregan scripts a `scripts/`, formatearlos con prettier (`ci:frontend:test` ejecuta `npm test` con lint también en clones limpios).
- **SonarCloud:** el **Auto-Scan ignora `sonar-project.properties`** (no aplican exclusiones ni `sonar.cpd.exclusions`). Evitar datasets con arrays de literales de estructura repetida (el CPD ignora las diferencias de literales y los marca duplicados): usar formato serializado (ver `divipola.ts`) o helpers en specs. Gate vigente: **OK, 1,1 % de duplicación nueva**.
- **Panel admin solo escritorio (por diseño):** `useIsMobileView` (≤991,98 px) bloquea `/admin` con `DesktopOnlyNotice` y oculta los accesos en `StoreHeader`/`LoginPage`. No habilitarlo en móvil sin decisión de producto.
- **Seeds:** catálogo `scripts/seed-contenido.js` (manifiesto v2; imágenes locales por `alt`) y operaciones `scripts/seed-operaciones.js` (se loguea como cada cliente para el checkout; marca `seed-operaciones v1` en `notasCliente`). Ambos con **doble corrida idempotente** verificada (dev y prod).
- **Reportes QA en GitHub Pages (repo `joseph12n/pruebas`):** sitio KN·QA Observatory con maestro/unitarias/e2e/jmeter/lighthouse + **Cobertura**, reestructurado con Jekyll (shell único en `_layouts/`/`_includes/`) y Quality Gate de SonarCloud OK. Se regenera con `tools/build_data.py` desde `docs/cobertura/`; build local con `docker run --rm -v "$PWD":/srv/jekyll jekyll/jekyll:4 jekyll build`. Detalle en `~/Descargas/pruebas/CONTEXT.md`.
- **NPM (EC2):** nunca apuntar un proxy host a la IP pública; usar nombres internos (`knstore-app-1`, `portainer`) o `127.0.0.1` para NPM admin. Si NPM se recrea, su compose ya incluye las redes externas.
- **Actualizar la EC2:** solo se cambia `image:` en `/home/ubuntu/knstore/app-prod.yml` (el `docker-compose.yml` es un symlink a ese archivo) y se ejecuta `docker compose pull && docker compose up -d`. No tocar `.env`, `mongodb-replicaset.yml` ni el `name: knstore`.
- `docker-compose.legacy-dev.yml.bak` es el compose viejo (perfil dev con seed/prometheus): no usar.
- Mongo dev rs0: `docker compose -f src/main/docker/services.yml up -d --wait` → `127.0.0.1:27018`; `my-mongo` (27017) es standalone, no usar para checkout.
- Boot 4: la URI de Mongo es `spring.mongodb.uri` → env **`SPRING_MONGODB_URI`**.
- `@DBRef`: las consultas por lote contra `ref.$id` requieren `ObjectId` (`MongoIdUtils`); ver `ProductoImagenRepository.findByProductoIdIn`.
- **CSP:** hosts de imágenes externas permitidos: `images.unsplash.com`, `plus.unsplash.com`, `images.pexels.com` (`img-src`); **el catálogo actual usa blobs locales**, así que no depende de hosts externos.
- La imagen prod se construye con `-Pprod` (compila el frontend); verificar RAM antes (webpack es el pico).

## 6. Historial del plan de front (cerrado)

Plan aprobado 2026-09-18; todas las fases quedaron completadas y publicadas:

| Fase | Contenido                                                                | Release                          | Evidencia                                                          |
| ---- | ------------------------------------------------------------------------ | -------------------------------- | ------------------------------------------------------------------ |
| 0    | Botón "Añadir" de `ProductCard` responsive + envío gratis en el checkout | 3.1.0                            | `CheckoutServiceIT` 7/7, `CheckoutPage.spec` 7/7, frontend 525/525 |
| 1    | Modo oscuro persistente (`data-theme`/`kn-theme`) + 5 secciones del home | 3.1.0                            | Frontend 532/532, cobertura 50,19 % sentencias                     |
| 2    | Shell propio `AdminLayout` + dashboard `/admin` + tematización de CRUDs  | 3.1.0 (fix del sidebar en 3.1.1) | Frontend 533/533 (534/534 tras 3.1.1)                              |
| 2c   | `seed-operaciones.js` + host Pexels en la CSP                            | 3.2.0                            | Doble corrida dev/prod (BITACORA)                                  |
| 3b   | Cascada Departamento → Municipio (DIVIPOLA) + modal en direcciones       | 3.2.0                            | `AddressForm` 12/12, `divipola.spec.ts`, frontend 539/539          |
| 4b   | Panel admin bloqueado en ≤992 px + `DesktopOnlyNotice`                   | 3.2.0                            | 20 pruebas nuevas, `landing/` 97/97                                |

**Backlog opcional del admin:** páginas propias de edición rápida para productos/inventario (hoy CRUDs generados tematizados).

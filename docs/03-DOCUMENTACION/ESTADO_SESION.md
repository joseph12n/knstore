# ESTADO_SESION.md — Handoff (actualizado 2026-09-22)

> ⚠️ OBLIGATORIO: leer este archivo completo antes de tocar nada.
> **Regla obligatoria:** todo cambio debe quedar registrado en `docs/03-DOCUMENTACION/BITACORA.md` (entradas nuevas arriba) y reflejado aquí cuando cambie el estado.
> Resume el cierre de calidad (H-01→H-08), el despliegue en producción y la operación de la EC2 del 2026-09-18, más el flujo de contenido de la tienda del 2026-09-22.

---

## 1. Git — estado

- Rama **main** con los commits de calidad/hardening y las correcciones E2E del lanzamiento (ver `git log --oneline -15`).
- **2026-09-22:** commit `ada5041` (`feat(config): agregar manifiesto de contenido y seed idempotente de la tienda`) con `contenido/catalogo.json`, `contenido/imagenes/README.md` y `scripts/seed-contenido.js`; push a `origin` y mirror a **sena-students** (mismo SHA). Las 6 ramas quedan unificadas a `main` en ambos repos.
- **2026-09-22:** rama `revert-3-lauraG` (revert del PR #3, commit único `c6cbf07`) eliminada de `origin` tras respaldar su tip en `refs/backup/2026-09-22/revert-3-lauraG` (local, en los dos repos). Los dos `origin` quedan solo con las 6 ramas oficiales.
- **2026-09-22 (tarde):** fix del lint (`eslint.config.ts` ignora las carpetas de skills de agentes IA; `scripts/seed-contenido.js` formateado con prettier) e informe de calidad regenerado con la corrida de cobertura del mismo día → push a `origin` y mirror a **sena-students**, 6 ramas unificadas a `main`.
- Tags de release: `v3.0.0` (hardening), `v3.0.1` (precio_venta RF-072), `v3.0.3` (imágenes + CSP), `v3.1.0` (front: fixes responsive, modo oscuro, secciones, login y admin), **`v3.1.1`** (sidebar admin).
- Push a `origin` (joseph12n/knstore), mirror a **sena-students**, 6 ramas unificadas a `main` y backups de ramas en `refs/backup/2026-09-18/`.
- Imagen publicada en Docker Hub: **`eljoseph12/knstore:3.1.1`** (= `latest`, digest `d99ac4d8…`).

## 2. Calidad — todo verde

- Unit 363/363 · IT 487/487 · Vitest 534/534 · `./npmw test` (lint + Vitest + umbrales) en verde · `./mvnw verify` completo (modernizer incluido) · `tsc` limpio. (Revalidado el 2026-09-22 tarde con JaCoCo/Vitest; la corrida del IDE reportó 853/853.)
- Cobertura backend (consolidada unit+IT): **70,1% líneas** / 41,3% ramas / 88,6% métodos / 194 clases.
- Cobertura frontend: **50,97% líneas** sobre **78 archivos propios**; excluye generado y aplica umbrales 45/35/40/45.
- Informe visual: `docs/test_de_cobertura/informe-calidad/index.html` y `docs/test_de_cobertura/resultados-pruebas.html`; se regeneran con `python3 scripts/generar-informe-calidad.py`.

## 3. Producción — desplegado y verificado

- **EC2 `44.197.126.33`** (`app.knstore.duckdns.org`) con Nginx Proxy Manager + SSL.
- Imagen **`eljoseph12/knstore:3.1.1`** con **perfil prod** desplegada desde `/home/ubuntu/knstore/app-prod.yml` + `.env` (chmod 600; JWT, SMTP y URI rs0 por entorno).
- Datos intactos: **159 pedidos/facturas** y 16 usuarios; los 136 productos demo quedaron **desactivados** (preservan el historial) y **777 productos reales activos** con fotos, precios e inventario.
- Administrador rotado y usuarios demo desactivados (`scripts/rotate-prod-users.js`); la contraseña la tiene el responsable.
- SMTP verificado con la app password vigente (no está en el repo).
- Backup diario a las 03:00 (`/home/ubuntu/knstore/backup-mongo.sh`, retención 7 días) + backups manuales en `backups/`.
- Smoke: health UP, home/detalle/categoría 200, búsqueda y orden por precio OK, `/management/prometheus` y `/v3/api-docs` 401.
- **2026-09-18 — reinicios:** Mongo ahora tiene `restart: unless-stopped` (no revivía al encender la EC2; ya aplicado en repo y servidor).
- **2026-09-18 — NPM interno:** los proxy hosts apuntan a `knstore-app-1:8080`, `127.0.0.1:81` y `portainer:9000`; NPM está en las redes `knstore` y `portainer_portainer_network` (compose en `/home/ubuntu/nginx/docker-compose.yml`). Dominios verificados 200 dentro y fuera.
- **2026-09-18 — Portainer:** contraseña reseteada con el helper oficial (la credencial la custodia el responsable; no está en el repo).
- **2026-09-18 — Security Group:** solo `22` (IP del responsable), `80` y `443`; `81/8080/9000/9443` bloqueados desde Internet (verificado). Si cambia la IP del responsable, actualizar la regla SSH o entrar por EC2 Instance Connect.

## 4. Pendientes inmediatos

1. Backlog de calidad: specs de páginas del panel `/cuenta` y admin, casos negativos (H-05: ramas de 41% a ~60%) e imágenes de categorías (hoy son placeholders por diseño).
2. Rotar periódicamente la app password de Gmail, el secreto JWT del `.env` y la contraseña del administrador.
3. Mantener actualizada la regla SSH del Security Group si cambia la IP del responsable.
4. **(Acción del responsable)** Recuperar el export del IDE de las 14:04 del 2026-09-22 (`Test Results - java_in_knstore.html`, 853/853 en 41,77 s) desde la Historia local de IntelliJ y volverlo a commitear; por el incidente documentado en `BITACORA.md` (fila 9) hoy queda en su lugar la exportación anterior (848 total / 4 failed).

## 5. Entorno y quirks

- **Documentación:** cada cambio (código, infra, EC2) se registra en `docs/03-DOCUMENTACION/BITACORA.md`; este archivo refleja el estado vigente.
- **Contenido de la tienda (2026-09-22):** el manifiesto `contenido/catalogo.json` lo mantiene el agente `contenido-tienda` y lo carga `scripts/seed-contenido.js` (idempotente por `slug`, imágenes desde `contenido/imagenes/<slug>/`). Credenciales por entorno `KNSTORE_USERNAME`/`KNSTORE_PASSWORD`. Las carpetas de agentes IA (`.agent/`, `.claude/`, `.gemini/`, `.opencode/`) están en `.gitignore` (skills duplicados, ~72 MB): no versionarlas.
- **Lint (2026-09-22):** `eslint.config.ts` ignora las carpetas de skills de agentes IA (`.opencode/`, `.agent/`, `.claude/`, `.gemini/`) — antes rompían `npm test` con 5831 errores. Si se agregan scripts a `scripts/`, formatearlos con prettier: `ci:frontend:test` ejecuta `npm test` (con lint) también en clones limpios.
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

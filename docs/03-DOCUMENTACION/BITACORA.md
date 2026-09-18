# BITÁCORA DE CAMBIOS — KN-Store

> **Regla obligatoria del proyecto:** todo cambio (código, base de datos, infraestructura, configuración de servidores, decisiones y comandos ejecutados sobre la EC2) debe registrarse aquí el mismo día, con evidencia. Entradas nuevas **arriba**. Complementa a `ESTADO_SESION.md`.

---

## 2026-09-18 — Operación EC2 (encendido tras apagón, enrutamiento y accesos)

| # | Cambio | Detalle / evidencia |
|---|--------|---------------------|
| 1 | **Mongo no revivía al reiniciar la EC2** | Tras encender la EC2, `knstore-mongodb-1` estaba `Exited` (sin política de reinicio) y la app no levantaba. Se agregó `restart: unless-stopped` al servicio `mongodb` en `src/main/docker/mongodb-replicaset.yml` y en `/home/ubuntu/knstore/mongodb-replicaset.yml`; se recreó el contenedor (volumen intacto). Verificado: `mongo restart=unless-stopped`, `app restart=unless-stopped`, health 200, dominio 200. |
| 2 | **NPM ahora enruta por red interna** | Los 3 proxy hosts apuntaban a la IP pública (`44.197.126.33`), lo que impedía cerrar puertos sin romper los dominios. Se cambiaron a interno: `app.knstore.duckdns.org → knstore-app-1:8080`, `npm.knstore.duckdns.org → 127.0.0.1:81`, `portainer.knstore.duckdns.org → portainer:9000` (http). Se actualizó la BD (`proxy_host`) y los `*.conf` de `/data/nginx/proxy_host/` + `nginx -s reload`. Evidencia: los 3 dominios responden 200 desde la EC2 y desde Internet. |
| 3 | **NPM conectado a redes externas** | `/home/ubuntu/nginx/docker-compose.yml` ahora declara las redes externas `knstore` y `portainer_portainer_network` (persiste ante recreación de NPM). Respaldo del compose: `docker-compose.yml.bak-20260918-2336`. Respaldo de datos NPM: `/home/ubuntu/backups/npm-backup-20260918-2335.tgz`. |
| 4 | **Portainer: contraseña reseteada** | Se recuperó acceso con `docker run --rm -v portainer_portainer_data:/data portainer/helper-reset-password -password '...'` (usuario `admin`); login verificado por API (HTTP 200). La credencial la custodia el responsable; **no se registra en el repo**. Datos/stacks intactos. |
| 5 | **Secretos / accesos** | La app password de Gmail vigente se cargó en el `.env` de la EC2 y se verificó por SMTP. Se crearon usuarios de rol `manager@knstore.com` (MANAGER) y `cliente@knstore.com` (CLIENTE); los usuarios demo (`user`, `manager`, `cliente`, `jmetermanager`) quedaron desactivados. |
| 6 | **Documentación obligatoria** | Se creó esta bitácora y se añadió la regla a `AGENTS.md`: registrar todo cambio (incluida la infraestructura de la EC2) en `BITACORA.md` y actualizar `ESTADO_SESION.md`. |

**Pendiente del responsable (Security Group, sin cambios aún en AWS):** dejar solo `22` (IP propia), `80` y `443`; retirar `81`, `8080`, `9000`, `9443`. Es seguro porque NPM ya no depende de esos puertos públicos.

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

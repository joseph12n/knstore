# ESTADO_SESION.md — Handoff (actualizado 2026-09-18)

> ⚠️ OBLIGATORIO: leer este archivo completo antes de tocar nada.
> **Regla obligatoria:** todo cambio debe quedar registrado en `docs/03-DOCUMENTACION/BITACORA.md` (entradas nuevas arriba) y reflejado aquí cuando cambie el estado.
> Resume el cierre de calidad (H-01→H-08), el despliegue en producción y la operación de la EC2 del 2026-09-18.

---

## 1. Git — estado

- Rama **main** con los commits de calidad/hardening y las correcciones E2E del lanzamiento (ver `git log --oneline -15`).
- Tags de release: `v3.0.0` (hardening), `v3.0.1` (precio_venta RF-072), `v3.0.3` (imágenes + CSP).
- Push a `origin` (joseph12n/knstore), mirror a **sena-students**, 6 ramas unificadas a `main` y backups de ramas en `refs/backup/2026-09-15/`.
- Imagen publicada en Docker Hub: **`eljoseph12/knstore:3.0.3`** (= `latest`).

## 2. Calidad — todo verde

- Unit 363/363 · IT 486/486 · Vitest 524/524 · `./mvnw verify` completo (modernizer incluido) · `tsc` limpio.
- Cobertura backend (consolidada unit+IT): **69,8% líneas** / 41% ramas / 194 clases.
- Cobertura frontend: **49,46% líneas** sobre **70 archivos propios** (37 en 0%); excluye generado y aplica umbrales 45/35/40/45.
- Informe visual: `docs/test_de_cobertura/informe-calidad/index.html`; se regenera con `python3 scripts/generar-informe-calidad.py`.

## 3. Producción — desplegado y verificado

- **EC2 `44.197.126.33`** (`app.knstore.duckdns.org`) con Nginx Proxy Manager + SSL.
- Imagen `eljoseph12/knstore:3.0.3` con **perfil prod** desplegada desde `/home/ubuntu/knstore/app-prod.yml` + `.env` (chmod 600; JWT, SMTP y URI rs0 por entorno).
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

## 5. Entorno y quirks

- **Documentación:** cada cambio (código, infra, EC2) se registra en `docs/03-DOCUMENTACION/BITACORA.md`; este archivo refleja el estado vigente.
- **NPM (EC2):** nunca apuntar un proxy host a la IP pública; usar nombres internos (`knstore-app-1`, `portainer`) o `127.0.0.1` para NPM admin. Si NPM se recrea, su compose ya incluye las redes externas.
- **Actualizar la EC2:** solo se cambia `image:` en `/home/ubuntu/knstore/app-prod.yml` (el `docker-compose.yml` es un symlink a ese archivo) y se ejecuta `docker compose pull && docker compose up -d`. No tocar `.env`, `mongodb-replicaset.yml` ni el `name: knstore`.
- `docker-compose.legacy-dev.yml.bak` es el compose viejo (perfil dev con seed/prometheus): no usar.
- Mongo dev rs0: `docker compose -f src/main/docker/services.yml up -d --wait` → `127.0.0.1:27018`; `my-mongo` (27017) es standalone, no usar para checkout.
- Boot 4: la URI de Mongo es `spring.mongodb.uri` → env **`SPRING_MONGODB_URI`**.
- `@DBRef`: las consultas por lote contra `ref.$id` requieren `ObjectId` (`MongoIdUtils`); ver `ProductoImagenRepository.findByProductoIdIn`.
- **CSP:** si se agregan hosts de imágenes externas hay que incluirlos en `jhipster.security.content-security-policy` (`img-src`).
- La imagen prod se construye con `-Pprod` (compila el frontend); verificar RAM antes (webpack es el pico).

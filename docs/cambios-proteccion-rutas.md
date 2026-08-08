# Cambios: Protección de Rutas Frontend (Admin/Manager)

## Archivo modificado

**`src/main/webapp/app/modules/administration/index.tsx`**

## Descripción

Se agregó protección granular dentro del dashboard de administración. Antes, el acceso a `/admin/*` permitía que cualquier usuario con rol ADMIN o MANAGER viera todas las subrutas. Ahora ciertas rutas internas están restringidas solo para ADMIN.

## Detalle de protección por ruta

| Ruta | Acceso | Componente |
|------|--------|------------|
| `/admin/user-management/*` | Solo `ROLE_ADMIN` | UserManagement |
| `/admin/configuration` | Solo `ROLE_ADMIN` | Configuration |
| `/admin/logs` | Solo `ROLE_ADMIN` | Logs |
| `/admin/health` | `ROLE_ADMIN` y `ROLE_MANAGER` | Health |
| `/admin/metrics` | `ROLE_ADMIN` y `ROLE_MANAGER` | Metrics |
| `/admin/docs` | `ROLE_ADMIN` y `ROLE_MANAGER` | Docs |

## Mecanismo usado

Se reutilizó el componente `PrivateRoute` existente en `app/shared/auth/private-route.tsx`, que:

1. Espera a que se haya obtenido la sesión (`sessionHasBeenFetched`)
2. Si no está autenticado → redirige a `/login`
3. Si está autenticado pero no tiene el rol requerido → muestra "No tiene permisos"
4. Si está autenticado y autorizado → renderiza el componente hijo

## Comportamiento esperado

- Un **MANAGER** verá "No tiene permisos" al intentar acceder a user-management, configuration o logs
- Un **ADMIN** tendrá acceso completo a todas las rutas del dashboard
- Las rutas health, metrics y docs siguen siendo accesibles para ambos roles

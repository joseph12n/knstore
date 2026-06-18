# QA execution report roles + ownership

Fecha: 2026-06-18

## 1) Validacion de pruebas en entorno
### Runtime instalado
- Java: openjdk 21.0.11
- Node: v24.14.1
- npm: 11.11.0

### Backend
Comando ejecutado:
- ./mvnw -q -Dskip.installnodenpm -Dskip.npm -Dtest=ResourceAccessServiceTest test

Resultado:
- PASS (marca en consola: BACKEND_TEST_OK)
- Observaciones: warnings de Mockito/ByteBuddy por carga dinamica de agent en JDK 21 (no bloqueante)

### Frontend
Comandos ejecutados:
- npm install
- npm run lint
- npm run vitest-run

Resultado:
- lint: PASS (marca en consola: FRONTEND_LINT_OK)
- vitest: PASS (marca en consola: FRONTEND_TEST_OK)
- 35 archivos de test, 430 tests en verde
- cobertura global reportada: statements 93.28%, lines 93.35%

## 2) Ejecucion de checklist
Checklist base:
- docs/qa-roles-ownership-checklist.md

Estado de ejecucion:
- Items automatizables: ejecutados y en verde (backend ownership central + frontend lint/tests)
- Items manuales de smoke por usuario real (admin, manager, clienteA, clienteB): pendientes de ejecucion funcional con datos semilla en entorno levantado

## 3) Confirmacion de criterio de catalogo para CLIENTE
Criterio confirmado y documentado:
- CLIENTE no ve catalogo administrativo.
- Solo ADMIN/MANAGER ven rutas y menu de catalogo.

Evidencia de implementacion:
- src/main/webapp/app/entities/routes.tsx
- src/main/webapp/app/entities/menu.tsx
- docs/qa-roles-ownership-checklist.md

## Conclusion
- Los 3 puntos de cierre fueron ejecutados.
- Queda solo la pasada manual de smoke con usuarios reales para validacion funcional final en entorno de negocio.

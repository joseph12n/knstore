# PLAN SANTIAGO - EJECUCION OPERATIVA

## Objetivo

Arrancar y ejecutar las tareas de Santiago con control de dependencias, minimizando riesgo de retrabajo y manteniendo compatibilidad con lo ya implementado en backend/frontend.

## Estado de entrada

- El plan fuente existe en [plan-santiago.pdf](plan-santiago.pdf).
- No hay version textual extraible de forma confiable dentro del workspace.
- Ya existen implementaciones relevantes que cubren parte de dependencias cruzadas (checkout, ownership, auditoria, pagos/facturacion).

## Paso 0 (bloqueante)

Convertir [plan-santiago.pdf](plan-santiago.pdf) a texto versionado en repo:

- Nombre sugerido: `plan-santiago.txt` o `plan-santiago.md`
- Requisito: conservar IDs de tareas (S-01, S-02, etc.), RF/RNF y criterios de aceptacion.

Sin este paso, no se puede garantizar cierre formal por tarea S-xx.

## Matriz de trabajo inicial (inferida + verificable)

> Esta matriz es operativa para avanzar ya, pero debe normalizarse con el texto oficial del plan.

### S-03 (infra transaccional / base checkout)

- Objetivo tecnico:
  - Validar transaccionalidad real de checkout con replica set.
- Evidencia actual:
  - [src/main/java/com/mycompany/knstore/service/CheckoutService.java](src/main/java/com/mycompany/knstore/service/CheckoutService.java)
  - [src/main/java/com/mycompany/knstore/web/rest/PedidoResource.java](src/main/java/com/mycompany/knstore/web/rest/PedidoResource.java)
- Riesgo pendiente:
  - Cierre IT depende de Docker/Testcontainers.

### S-04 (direcciones)

- Objetivo tecnico:
  - Ownership de direcciones y flujo cliente consistente.
- Evidencia actual:
  - [src/main/java/com/mycompany/knstore/service/impl/DireccionServiceImpl.java](src/main/java/com/mycompany/knstore/service/impl/DireccionServiceImpl.java)
- Validacion pendiente:
  - Confirmar que coincide 1:1 con criterios exactos del plan Santiago.

### S-06 (busqueda/catalogo)

- Objetivo tecnico:
  - Endpoint backend de busqueda y filtros con soporte de rendimiento.
- Gap probable:
  - Frontend aun usa patrones de consumo generico y puede no estar amarrado a endpoint full-text dedicado.
- Referencias:
  - [src/main/webapp/app/landing/pages/CheckoutPage.tsx](src/main/webapp/app/landing/pages/CheckoutPage.tsx)
  - [src/main/webapp/app/entities/pedido/pedido.reducer.ts](src/main/webapp/app/entities/pedido/pedido.reducer.ts)

### S-07 (checkout consistente)

- Objetivo tecnico:
  - Consolidar contrato final checkout y reglas de calculo.
- Evidencia actual:
  - [src/main/java/com/mycompany/knstore/service/CheckoutService.java](src/main/java/com/mycompany/knstore/service/CheckoutService.java)
- Nota:
  - L-03/L-04/L-05 ya avanzaron sobre este contrato.

### S-08 (endpoints admin / trazabilidad)

- Objetivo tecnico:
  - Operaciones administrativas de estados/devoluciones con auditoria.
- Evidencia actual parcial:
  - [src/main/java/com/mycompany/knstore/service/impl/PedidoServiceImpl.java](src/main/java/com/mycompany/knstore/service/impl/PedidoServiceImpl.java)
  - [src/main/java/com/mycompany/knstore/web/rest/PedidoResource.java](src/main/java/com/mycompany/knstore/web/rest/PedidoResource.java)
- Pendiente:
  - Confirmar endpoints exactos requeridos por S-08.

## Orden de ejecucion recomendado

1. Normalizar texto oficial del plan (Paso 0).
2. Congelar matriz S-xx -> archivo/endpoint/test.
3. Implementar o ajustar en este orden:
   1. S-03
   2. S-07
   3. S-04 y S-06 (paralelo)
   4. S-08
4. Validacion:
   1. Unit tests por bloque
   2. IT cuando haya Docker
   3. reporte de cobertura/calidad

## Checklist de ejecucion inmediata

- [ ] Agregar `plan-santiago.txt` o `plan-santiago.md` al repo.
- [ ] Crear tabla de trazabilidad S-xx con RF/RNF y estado (Pendiente/En curso/Hecho).
- [ ] Confirmar contratos compartidos entre backend y frontend para S-07/S-08.
- [ ] Ejecutar sprint tecnico S-03/S-07 y publicar changelog por endpoint.

## Trazabilidad S-xx (estado actual)

| Tarea                                           | Estado                                  | Evidencia principal                                                                                                                                                                                                                                                                                   | Pruebas                                                                                                                                                                                                                                                                                                                 | Siguiente accion                                                |
| ----------------------------------------------- | --------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | --------------------------------------------------------------- |
| S-03 (transaccionalidad checkout / replica set) | En curso (bloqueado parcial por Docker) | [src/main/java/com/mycompany/knstore/service/CheckoutService.java](src/main/java/com/mycompany/knstore/service/CheckoutService.java) · [src/main/java/com/mycompany/knstore/web/rest/PedidoResource.java](src/main/java/com/mycompany/knstore/web/rest/PedidoResource.java)                           | [src/test/java/com/mycompany/knstore/service/CheckoutServiceS03UnitTest.java](src/test/java/com/mycompany/knstore/service/CheckoutServiceS03UnitTest.java) · [src/test/java/com/mycompany/knstore/service/CheckoutServiceS03IT.java](src/test/java/com/mycompany/knstore/service/CheckoutServiceS03IT.java)             | Ejecutar IT con Testcontainers cuando Docker este habilitado    |
| S-04 (direcciones ownership + predeterminada)   | Hecho                                   | [src/main/java/com/mycompany/knstore/service/impl/DireccionServiceImpl.java](src/main/java/com/mycompany/knstore/service/impl/DireccionServiceImpl.java) · [src/main/java/com/mycompany/knstore/web/rest/DireccionResource.java](src/main/java/com/mycompany/knstore/web/rest/DireccionResource.java) | [src/test/java/com/mycompany/knstore/service/impl/DireccionServiceImplDefaultTest.java](src/test/java/com/mycompany/knstore/service/impl/DireccionServiceImplDefaultTest.java)                                                                                                                                          | Verificar RF exactos contra texto final del plan                |
| S-06 (busqueda/catalogo)                        | Hecho                                   | [src/main/java/com/mycompany/knstore/service/impl/ProductoServiceImpl.java](src/main/java/com/mycompany/knstore/service/impl/ProductoServiceImpl.java) · [src/main/java/com/mycompany/knstore/web/rest/ProductoResource.java](src/main/java/com/mycompany/knstore/web/rest/ProductoResource.java)     | [src/test/java/com/mycompany/knstore/service/impl/ProductoServiceImplSearchTest.java](src/test/java/com/mycompany/knstore/service/impl/ProductoServiceImplSearchTest.java)                                                                                                                                              | Revisar amarre final con consumo frontend dedicado              |
| S-07 (checkout consistente)                     | Hecho (funcional)                       | [src/main/java/com/mycompany/knstore/service/CheckoutService.java](src/main/java/com/mycompany/knstore/service/CheckoutService.java) · [src/main/webapp/app/landing/pages/CheckoutPage.tsx](src/main/webapp/app/landing/pages/CheckoutPage.tsx)                                                       | [src/test/java/com/mycompany/knstore/service/CheckoutServiceS03UnitTest.java](src/test/java/com/mycompany/knstore/service/CheckoutServiceS03UnitTest.java)                                                                                                                                                              | Completar IT end-to-end con pasarela simulada en entorno Docker |
| S-08 (endpoints admin / trazabilidad)           | Hecho                                   | [src/main/java/com/mycompany/knstore/web/rest/PedidoResource.java](src/main/java/com/mycompany/knstore/web/rest/PedidoResource.java) · [src/main/java/com/mycompany/knstore/web/rest/EnvioResource.java](src/main/java/com/mycompany/knstore/web/rest/EnvioResource.java)                             | [src/test/java/com/mycompany/knstore/web/rest/PedidoResourceS08Test.java](src/test/java/com/mycompany/knstore/web/rest/PedidoResourceS08Test.java) · [src/test/java/com/mycompany/knstore/service/impl/EnvioServiceImplAdminTest.java](src/test/java/com/mycompany/knstore/service/impl/EnvioServiceImplAdminTest.java) | Validar endpoint matrix final contra PDF textual                |

### Bloqueantes vigentes de cierre formal

- No existe aun una extraccion textual confiable y versionada de [plan-santiago.pdf](plan-santiago.pdf).
- Los IT de transaccionalidad real (replica set/Testcontainers) siguen pendientes por entorno Docker.

### Comando de validacion S-03 cuando Docker este disponible

- `./mvnw -Dtest=CheckoutServiceS03IT test`
- Resultado esperado:
  - `rollbackCuandoFallaDecrementoIntermedio` en verde (sin residuos de pedido/item/envio y sin decremento parcial de stock).
  - `concurrenciaSobreMismoStockPermiteSoloUnCheckout` en verde (1 checkout exitoso, 1 fallido, stock final coherente).

## Criterio de cierre de esta fase

Se considera listo para implementar al 100% cuando exista el plan textual y cada tarea S-xx tenga:

- alcance exacto,
- archivo(s) objetivo,
- pruebas asociadas,
- evidencia de cumplimiento.

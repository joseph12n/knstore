# QA checklist roles + ownership

## Objetivo
Validar que los endpoints y paneles se comportan segun rol:
- ADMIN: acceso total
- MANAGER: acceso operativo y catalogo
- CLIENTE: acceso solo a sus propios recursos
- USER: sin acceso a endpoints de entidades protegidas

## Decision de negocio confirmada
- CLIENTE no tiene acceso al catalogo de administracion (categoria, subcategoria, marca, producto y derivados).
- El catalogo administrativo queda visible solo para ADMIN y MANAGER en rutas y menu.

## Prerrequisitos
1. Levantar app con perfil de test o dev con datos iniciales.
2. Tener 4 usuarios activos:
   - admin (ROLE_ADMIN)
   - manager (ROLE_MANAGER)
   - clienteA (ROLE_CLIENTE)
   - clienteB (ROLE_CLIENTE)
3. Tener datos para ownership:
   - una cuenta para clienteA y otra para clienteB
   - un carrito/pedido/direccion por cada cliente
   - al menos un itemCarrito, itemPedido, pago, envio y factura encadenados por cada cliente

## Endpoints de ownership (backend)
Validar para cada recurso:
- /api/cuentas
- /api/direccions
- /api/carritos
- /api/pedidos
- /api/item-carritos
- /api/item-pedidos
- /api/pagos
- /api/envios
- /api/facturas

### Matriz esperada
1. ADMIN y MANAGER:
   - GET/POST/PUT/PATCH/DELETE permitidos segun endpoint
   - acceso a recursos de cualquier cliente
2. CLIENTE (recurso propio):
   - GET by id permitido
   - PUT/PATCH permitido solo si id y DTO pertenecen al mismo login
   - DELETE permitido solo si el recurso es suyo
3. CLIENTE (recurso de otro cliente):
   - GET by id denegado
   - PUT/PATCH denegado
   - DELETE denegado
4. USER:
   - acceso denegado en todos los endpoints protegidos por ADMIN/MANAGER/CLIENTE

## Casos minimos por recurso
Para cada endpoint de ownership ejecutar al menos:
1. clienteA hace GET por id propio -> 200
2. clienteA hace GET por id de clienteB -> 403
3. clienteA hace PUT/PATCH con id propio pero DTO apuntando a entidad de clienteB -> 403
4. clienteA hace DELETE sobre id de clienteB -> 403
5. manager hace GET/PUT/DELETE sobre id de clienteA -> permitido

## UI y rutas (frontend)
### Login y redireccion
1. login admin -> /admin/user-management
2. login manager -> /pedido
3. login cliente -> /carrito

### Menu y paneles
1. ADMIN: ve menu de administracion y entidades
2. MANAGER: ve entidades operativas/catalogo, no panel admin
3. CLIENTE: ve solo panel/entidades permitidas para cliente
4. USER: no debe navegar a entidades protegidas

## Smoke test manual recomendado
1. Iniciar sesion con clienteA.
2. Abrir listado de carrito/pedido/factura.
3. Confirmar que solo aparecen registros propios.
4. Intentar abrir URL directa con id de clienteB.
5. Confirmar respuesta 403 o no encontrado filtrado (segun flujo).

## Pruebas automatizadas agregadas
- Test unitario de ownership central:
  - src/test/java/com/mycompany/knstore/security/ResourceAccessServiceTest.java

## Comandos de validacion (cuando Java/Node esten disponibles)
1. Backend tests:
   - ./mvnw -q -DskipITs=false test
2. Solo security:
   - ./mvnw -q -Dtest=ResourceAccessServiceTest test
3. Frontend checks:
   - ./npmw test -- --runInBand
   - ./npmw run lint

## Criterio de cierre
1. Todos los casos minimos pasan para los 9 recursos de ownership.
2. Redireccion y visibilidad de menu por rol pasan sin regresiones.
3. Sin errores de compilacion ni fallos criticos en tests.

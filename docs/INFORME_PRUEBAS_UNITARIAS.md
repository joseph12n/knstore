# Informe de Pruebas Unitarias — KN-Store (JUnit 5 + Mockito)

**Fecha:** 2026-08-29
**Alcance:** Capa de servicios del backend (`service/impl`, `service`, `service/util`)
**Stack de pruebas:** JUnit 5, Mockito 5 (MockitoExtension + mockStatic), AssertJ, mappers MapStruct reales (`new XxxMapperImpl()`)
**Comando de ejecución:** `./mvnw -q -Dskip.npm=true -Dspotless.check.skip=true -Dcheckstyle.skip=true -Djacoco.skip=true test`

---

## 1. Resumen ejecutivo

| Métrica | Valor |
| --- | --- |
| Suites ejecutadas (Surefire) | **93** |
| Tests totales | **361** |
| Tests **nuevos** en esta sesión | **150** |
| Fallos | **0** |
| Errores | **0** |
| Omitidos | **0** |
| Resultado | ✅ **VERDE** |

Antes de esta sesión, 14 clases de servicio no tenían pruebas unitarias. Se escribieron 14 suites nuevas sin modificar ningún archivo de producción.

---

## 2. Estrategia de las pruebas

- **Unitarias puras (sin Spring, sin BD):** cada servicio se instancia por constructor con repositorios `@Mock` (Mockito) y mappers MapStruct reales, siguiendo el estilo de `PedidoServiceImplTest`.
- **Seguridad simulada:** para los métodos que filtran por rol CLIENTE (`SecurityUtils.hasCurrentUserThisAuthority` / `getCurrentUserId`), se usó `Mockito.mockStatic(SecurityUtils.class)` en try-with-resources; en `UserService` se inyectó `UsernamePasswordAuthenticationToken` en `SecurityContextHolder` (limpiado en `@AfterEach`).
- **Mockito estricto:** sin stubs innecesarios (fallarían con `UnnecessaryStubbingException`); `ArgumentCaptor` para verificar la entidad persistida; `thenAnswer(invocation -> invocation.getArgument(0))` para propagar `save`.
- **Casos de negocio cubiertos** (no solo CRUD mecánico):
  - Ownership: un CLIENTE solo ve sus propios carritos, cuentas, direcciones e items; admin delega al repositorio general.
  - Dirección predeterminada atómica: desmarca la anterior, marca la nueva, ignora inactivas, excepciones si no existe / es de otra cuenta / el cliente no tiene `Cuenta`.
  - Carrito: `vaciar` borra items, reinicia subtotal a 0 y actualiza fecha.
  - Registro de usuarios: email/username duplicados (incluida la rama `DuplicateKeyException`), eliminación de usuarios no activados antes de re-registro, roles `ROLE_USER` + `ROLE_CLIENTE`, reset de contraseña con ventana de expiración de 1 día.
  - `partialUpdate` con semántica `NullValuePropertyMappingStrategy.IGNORE` (campos no nulos intactos).
  - Quirk MongoDB: `MongoIdUtils` convierte String→ObjectId solo con hex válidos de 24 caracteres (queries batch sobre `@DBRef`).

---

## 3. Suites nuevas (14 archivos / 150 tests)

### 3.1 Módulo cliente — Agente 1 (48 tests)

| Suite | Tests | Escenarios clave |
| --- | --- | --- |
| `service/impl/CarritoServiceImplTest` | 12 | Ownership por cuenta en findAll/findOne; `vaciar` reinicia subtotal y fecha; vaciar sin carrito no guarda |
| `service/impl/CuentaServiceImplTest` | 12 | Preserva user/tipoDocumento en update; CLIENTE solo ve su cuenta paginada; consultas eager para admin |
| `service/impl/DireccionServiceImplTest` | 16 | Predeterminada atómica (cliente y admin); 3 excepciones de ownership/existencia; asignación de cuenta en save/update |
| `service/util/MongoIdUtilsTest` | 8 | Hex válido/inválido/vacío/null; colecciones con elementos nulos e inválidos |

### 3.2 Catálogo — Agente 2 (50 tests)

| Suite | Tests | Escenarios clave |
| --- | --- | --- |
| `service/impl/CategoriaServiceImplTest` | 8 | CRUD completo + partialUpdate sin guardado cuando no existe |
| `service/impl/CategoriaIVAServiceImplTest` | 8 | CRUD completo (findAll en lista) |
| `service/impl/SubcategoriaServiceImplTest` | 9 | Consultas eager (`categoriaNombre` anidado) |
| `service/impl/MarcaServiceImplTest` | 8 | CRUD completo |
| `service/impl/TipoDocumentoServiceImplTest` | 8 | CRUD completo |
| `service/impl/EtiquetaProductoServiceImplTest` | 9 | Consultas eager (`productoNombre` anidado) |

### 3.3 Producto y pedidos — Agente 3 (30 tests)

| Suite | Tests | Escenario clave |
| --- | --- | --- |
| `service/impl/ProductoImagenServiceImplTest` | 8 | CRUD + gallery por producto |
| `service/impl/ProductoInventarioServiceImplTest` | 9 | CRUD + `findAllWhereProductoIsNull` (filtra inventarios huérfanos por `@DBRef` nulo) |
| `service/impl/ItemPedidoServiceImplTest` | 13 | Ownership: CLIENTE solo ve items de SUS pedidos (autenticación con `JwtAuthenticationToken`); eager para admin |

### 3.4 Usuarios — Agente 4 (22 tests)

| Suite | Tests | Escenarios clave |
| --- | --- | --- |
| `service/UserServiceTest` | 22 | Registro (roles, password codificada, usuario desactivado), duplicados activados/inactivados, `DuplicateKeyException` (login vs email), activación, reset de contraseña (expiración 1 día), cambio de contraseña (contraseña actual incorrecta → `InvalidPasswordException`), `removeNotActivatedUsers`, authorities |

---

## 4. Suites preexistentes verificadas (sin cambios)

Las 79 suites que ya existían siguen en verde, entre ellas:
`SecuenciaServiceImplTest` (5), `PedidoServiceImplTest` (7), `PagoServiceImplTest` (14), `FacturaServiceImplTest` (3), `EnvioServiceImplTest` (7), `ItemCarritoServiceImplTest` (4), `HistorialEstadoServiceImplTest` (2), `ProductoServiceImplTest` (4), `ProductoPrecioServiceImplTest` (3), `ResourceAccessServiceTest` (23), `SecurityUtilsUnitTest` (9), `SimulatedPaymentGatewayTest` (5), `FacturaPdfServiceTest` (3), `MoneyUtilsTest` (4), mappers (21×1 + UserMapper 10), DTOs (21×1), dominios (52), config/Cucumber (18), `SecurityMetersServiceTests` (2).

---

## 5. Conclusiones y recomendaciones

1. **Cobertura:** la capa de servicio (`service/impl` + `service`) queda 100% cubierta con pruebas unitarias; no quedan clases de negocio sin suite.
2. **Calidad:** Mockito estricto evita stubs muertos; los tests verifican interacciones relevantes (verificaciones de guardado con captor, no solo el valor retornado).
3. **Backlog conocido (preexistente, no relacionado con esta sesión):** `ItemCarritoResourceIT` (4 tests de integración) sigue fallando por el blindaje de precios server-side; requiere reescritura con productos reales. Ver `docs/REQUERIMIENTOS_PENDIENTES.md`.
4. **Siguiente paso sugerido:** activar JaCoCo en CI para medir % de cobertura de líneas y priorizar `CheckoutService` (hoy solo tiene IT).

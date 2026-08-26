## Usuarios y cuentas
| Unnamed: 0 | Unnamed: 1 | Unnamed: 2 | Unnamed: 3 | Unnamed: 4 | Unnamed: 5 | Unnamed: 6 | Unnamed: 7 | Unnamed: 8 | Unnamed: 9 | Unnamed: 10 | Unnamed: 11 | Unnamed: 12 | Unnamed: 13 | Unnamed: 14 | Unnamed: 15 | Unnamed: 16 | Estados |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN | Seleccione |
| Empresa: | NaN | NaN | NaN | NaN | Sprint | NaN | Módulo: | Usuario | Url de archivos de la prueba: | NaN | NaN | NaN | NaN | NaN | NaN | NaN | Aprobado |
| Elaboró: | NaN | NaN | NaN | NaN | NaN | NaN | Revisó: | NaN | Aprobó: | NaN | NaN | NaN | NaN | NaN | NaN | NaN | Por corregir |
| NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN | Información para el seguimiento | NaN | NaN | NaN | NaN | NaN | Seleccione |
| ID CASO DE PRUEBA | ID Caso de prueba anterior | Nombre del caso de prueba | Id de requerimiento | Descripción del requerimiento | Fecha creación | Funcionalidad/Característica | Datos/Accion | Resultado esperado | Procedimientos especiales requeridos | Dependencias con otros casos de prueba | Resultado obtenido | Estado | Fecha prueba | Estado reportado por el Developer | Observaciones | Imagen | Realizado |
| CP-001 | NaN | Registro exitoso de usuario | Funcional | Registrar usuario nuevo | 2026-06-06 00:00:00 | RF-001 Registro usuario | Nombre, correo válido, contraseña válida | Usuario creado correctamente | NaN | NaN | NaN | Seleccione | NaN | Seleccione | NaN | NaN | NaN |
| CP-002 | NaN | Registro con correo duplicado | Funcional | Registrar usando correo existente | 2026-06-06 00:00:00 | RF-001 | Correo existente | Mensaje de error | NaN | NaN | NaN | Seleccione | NaN | Seleccione | NaN | NaN | NaN |
| CP-003 | CP-001 | Inicio de sesión exitoso | Funcional | Autenticarse | 2026-06-06 00:00:00 | RF-002 | Correo y contraseña válidos | JWT generado | NaN | NaN | NaN | Seleccione | NaN | Seleccione | NaN | NaN | NaN |
| CP-004 | NaN | Inicio sesión inválido | Funcional | Intentar acceso incorrecto | 2026-06-06 00:00:00 | RF-002 | Credenciales erróneas | Acceso rechazado | NaN | NaN | NaN | Seleccione | NaN | Seleccione | NaN | NaN | NaN |
| CP-005 | CP-003 | Cierre de sesión | Funcional | Ejecutar logout | 2026-06-06 00:00:00 | RF-003 | JWT válido | Sesión cerrada | NaN | NaN | NaN | Seleccione | NaN | Seleccione | NaN | NaN | NaN |
| CP-006 | CP-003 | Verificación automática de sesión | Funcional | Recargar aplicación | 2026-06-06 00:00:00 | RF-004 | JWT vigente | Usuario permanece autenticado | NaN | NaN | NaN | Seleccione | NaN | Seleccione | NaN | NaN | NaN |
| CP-007 | CP-003 | Protección de rutas privadas | Funcional | Acceder a ruta restringida | 2026-06-06 00:00:00 | RF-005 | JWT válido | Acceso permitido | NaN | NaN | NaN | Seleccione | NaN | Seleccione | NaN | NaN | NaN |
| CP-008 | NaN | Acceso sin token | Seguridad | Abrir ruta protegida | 2026-06-06 00:00:00 | RF-005 / RNF-004 | Sin JWT | Acceso denegado | NaN | NaN | NaN | Seleccione | NaN | Seleccione | NaN | NaN | NaN |
| CP-009 | CP-003 | Listar usuarios | Funcional | Consultar listado admin | 2026-06-06 00:00:00 | RF-006 | Usuario Admin | Lista retornada | NaN | NaN | NaN | Seleccione | NaN | Seleccione | NaN | NaN | NaN |
| CP-010 | CP-009 | Consultar usuario por ID | Funcional | Buscar usuario específico | 2026-06-06 00:00:00 | RF-007 | ID usuario | Datos completos | NaN | NaN | NaN | Seleccione | NaN | Seleccione | NaN | NaN | NaN |
| CP-011 | CP-009 | Crear usuario por Admin | Funcional | Crear usuario con rol | 2026-06-06 00:00:00 | RF-008 | Datos usuario + rol | Usuario creado | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN |
| CP-012 | CP-010 | Actualizar usuario | Funcional | Modificar atributos | 2026-06-06 00:00:00 | RF-009 | Datos actualizados | Información modificada | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN |
| CP-013 | CP-010 | Eliminación lógica usuario | Funcional | Primer DELETE | 2026-06-06 00:00:00 | RF-010 / RNF-005 | ID usuario | active=false | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN |
| CP-014 | CP-013 | Eliminación física usuario | Funcional | Segundo DELETE | 2026-06-06 00:00:00 | RF-010 | ID usuario | Registro eliminado | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN |
| CP-015 | CP-003 | Consultar perfil propio | Funcional | Ver perfil | 2026-06-06 00:00:00 | RF-031 | JWT válido | Perfil mostrado | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN |
| CP-016 | CP-003 | Eliminar perfil propio | Funcional | Solicitar eliminación | 2026-06-06 00:00:00 | RF-032 | Usuario autenticado | Cuenta eliminada | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN |
| CP-017 | CP-015 | Editar perfil propio | Funcional | Actualizar datos | 2026-06-06 00:00:00 | RF-033 | Nombre, correo | Cambios guardados | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN |
| CP-018 | CP-003 | Crear dirección | Funcional | Registrar dirección | 2026-06-06 00:00:00 | RF-037 | Dirección válida | Dirección creada | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN |
| CP-019 | CP-018 | Listar direcciones | Funcional | Consultar direcciones | 2026-06-06 00:00:00 | RF-038 | Usuario autenticado | Direcciones listadas | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN |
| CP-020 | CP-018 | Editar dirección | Funcional | Modificar dirección | 2026-06-06 00:00:00 | RF-039 | Datos actualizados | Dirección actualizada | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN |
| CP-021 | CP-018 | Eliminar dirección | Funcional | Borrar dirección | 2026-06-06 00:00:00 | RF-040 | ID dirección | Dirección eliminada | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN |
| CP-022 | CP-018 | Dirección predeterminada | Funcional | Marcar dirección | 2026-06-06 00:00:00 | RF-041 | ID dirección | Dirección principal asignada | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN |
| CP-023 | CP-001 | Hashing bcrypt | Seguridad | Verificar almacenamiento | 2026-06-06 00:00:00 | RNF-001 | Contraseña registrada | Hash bcrypt costo ≥10 | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN |
| CP-024 | CP-003 | Vigencia JWT | Seguridad | Validar token | 2026-06-06 00:00:00 | RNF-002 | JWT emitido | Expira a 30 días | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN |
| CP-025 | NaN | Validación entradas | Seguridad | Enviar datos inválidos | 2026-06-06 00:00:00 | RNF-003 | Campos vacíos | Validación ejecutada | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN |
| CP-026 | NaN | Restricción CORS | Seguridad | Solicitud origen no permitido | 2026-06-06 00:00:00 | RNF-006 | Origin externo | Solicitud bloqueada | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN |
| CP-027 | NaN | Diseño responsivo login | Usabilidad | Probar 360px-1920px | 2026-06-06 00:00:00 | RNF-010 | Diferentes resoluciones | UI adaptable | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN |
| CP-028 | NaN | Animaciones UI | Usabilidad | Medir FPS | 2026-06-06 00:00:00 | RNF-011 | Navegación | FPS ≥50 | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN |
| CP-029 | NaN | Mensajes de error claros | Usabilidad | Generar errores | 2026-06-06 00:00:00 | RNF-013 | Errores controlados | Mensajes entendibles | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN |

| CP-089 | CP-068 | Consultar historial de pedido | Funcional | Ver historial de estados del pedido | 2026-08-26 00:00:00 | RNF-025 | Propietario autenticado | Transiciones visibles (PENDING, CONFIRMED, CANCELLED) | NaN | NaN | NaN | Seleccione | NaN | Seleccione | NaN | NaN | NaN |
| CP-090 | CP-021 | Ownership cruzado: dirección ajena | Seguridad | CLIENTE intenta eliminar dirección de otra cuenta | 2026-08-26 00:00:00 | RNF-004 | Dirección de borrar01 | Acceso denegado (403) | NaN | NaN | NaN | Seleccione | NaN | Seleccione | NaN | NaN | NaN |
| CP-091 | CP-064 | Ownership cruzado: pedido ajeno | Seguridad | CLIENTE intenta consultar pedido de otra cuenta | 2026-08-26 00:00:00 | RNF-004 | Pedido de borrar01 | Acceso denegado (403) | NaN | NaN | NaN | Seleccione | NaN | Seleccione | NaN | NaN | NaN |
## Catalogo
| Unnamed: 0 | Unnamed: 1 | Unnamed: 2 | Unnamed: 3 | Unnamed: 4 | Unnamed: 5 | Unnamed: 6 | Unnamed: 7 | Unnamed: 8 | Unnamed: 9 | Unnamed: 10 | Unnamed: 11 | Unnamed: 12 | Unnamed: 13 | Unnamed: 14 | Unnamed: 15 | Unnamed: 16 |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN |
| Empresa: | NaN | NaN | NaN | NaN | Sprint | NaN | Módulo: | Usuario | Url de archivos de la prueba: | NaN | NaN | NaN | NaN | NaN | NaN | NaN |
| Elaboró: | NaN | NaN | NaN | NaN | NaN | NaN | Revisó: | NaN | Aprobó: | NaN | NaN | NaN | NaN | NaN | NaN | NaN |
| NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN | Información para el seguimiento | NaN | NaN | NaN | NaN | NaN |
| ID CASO DE PRUEBA | ID Caso de prueba anterior | Nombre del caso de prueba | Id de requerimiento | Descripción del requerimiento | Fecha creación | Funcionalidad/Característica | Datos/Accion | Resultado esperado | Procedimientos especiales requeridos | Dependencias con otros casos de prueba | Resultado obtenido | Estado | Fecha prueba | Estado reportado por el Developer | Observaciones | Imagen |
| CP-030 | NaN | Listar categorías | Funcional | Consultar categorías | 2026-06-06 00:00:00 | RF-011 | Sin autenticación | Categorías activas | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN |
| CP-031 | CP-030 | Consultar categoría | Funcional | Buscar categoría | 2026-06-06 00:00:00 | RF-012 | ID categoría | Datos mostrados | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN |
| CP-032 | CP-003 | Crear categoría | Funcional | Crear categoría | 2026-06-06 00:00:00 | RF-013 | Nombre categoría | Registro creado | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN |
| CP-033 | CP-032 | Actualizar categoría | Funcional | Editar categoría | 2026-06-06 00:00:00 | RF-014 | Datos nuevos | Cambios guardados | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN |
| CP-034 | CP-032 | Eliminar categoría | Funcional | Eliminar categoría | 2026-06-06 00:00:00 | RF-015 | ID categoría | Eliminación correcta | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN |
| CP-035 | NaN | Listar subcategorías | Funcional | Consultar subcategorías | 2026-06-06 00:00:00 | RF-016 | Sin autenticación | Datos retornados | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN |
| CP-036 | CP-035 | Crear subcategoría | Funcional | Registrar subcategoría | 2026-06-06 00:00:00 | RF-017 | Datos válidos | Subcategoría creada | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN |
| CP-037 | CP-036 | Actualizar subcategoría | Funcional | Modificar subcategoría | 2026-06-06 00:00:00 | RF-018 | Datos nuevos | Actualización exitosa | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN |
| CP-038 | CP-036 | Eliminar subcategoría | Funcional | Eliminar registro | 2026-06-06 00:00:00 | RF-019 | ID subcategoría | Eliminación exitosa | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN |
| CP-039 | CP-003 | Crear producto | Funcional | Registrar producto | 2026-06-06 00:00:00 | RF-020 | Producto + variantes | Producto creado | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN |
| CP-040 | CP-039 | Actualizar producto | Funcional | Editar producto | 2026-06-06 00:00:00 | RF-021 | Datos modificados | Producto actualizado | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN |
| CP-041 | CP-039 | Calcular precio automático | Funcional | Registrar costos | 2026-06-06 00:00:00 | RF-022 | Costo y margen | Precio calculado | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN |
| CP-042 | CP-039 | Eliminar producto | Funcional | Eliminar producto | 2026-06-06 00:00:00 | RF-023 | ID producto | Producto eliminado | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN |
| CP-043 | CP-039 | Búsqueda full-text | Funcional | Buscar texto | 2026-06-06 00:00:00 | RF-024 | Palabra clave | Resultados relevantes | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN |
| CP-044 | NaN | Listar productos paginados | Funcional | Consultar catálogo | 2026-06-06 00:00:00 | RF-025 | page, limit | Datos paginados | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN |
| CP-045 | CP-044 | Consultar producto por slug | Funcional | Buscar detalle | 2026-06-06 00:00:00 | RF-026 | slug | Producto encontrado | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN |
| CP-046 | CP-044 | Filtrar por categoría | Funcional | Aplicar filtro | 2026-06-06 00:00:00 | RF-027 | Categoría | Productos filtrados | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN |
| CP-047 | CP-044 | Filtrar por subcategoría | Funcional | Aplicar filtro | 2026-06-06 00:00:00 | RF-028 | Subcategoría | Resultados correctos | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN |
| CP-048 | CP-044 | Filtrar por marca | Funcional | Aplicar filtro | 2026-06-06 00:00:00 | RF-029 | Marca | Resultados correctos | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN |
| CP-049 | CP-044 | Filtrar por etiqueta | Funcional | Aplicar filtro | 2026-06-06 00:00:00 | RF-030 | Tag | Resultados correctos | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN |
| CP-050 | CP-003 | Acceso panel admin | Funcional | Abrir panel | 2026-06-06 00:00:00 | RF-034 | Rol autorizado | Acceso permitido | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN |
| CP-051 | CP-050 | CRUD desde interfaz | Regresión | Ejecutar operaciones CRUD | 2026-06-06 00:00:00 | RF-035 | Entidades varias | Operaciones exitosas | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN |
| CP-052 | CP-050 | Visualizar estado activo | Funcional | Consultar entidades | 2026-06-06 00:00:00 | RF-036 | active/inactive | Estado visible | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN |
| CP-053 | NaN | Tiempo respuesta catálogo | Rendimiento | Medir consultas | 2026-06-06 00:00:00 | RNF-007 | Catálogo | P95 < 500ms | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN |
| CP-054 | CP-044 | Paginación obligatoria | Rendimiento | Consulta masiva | 2026-06-06 00:00:00 | RNF-008 | Sin page/limit | Paginación aplicada | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN |
| CP-055 | CP-043 | Índices MongoDB | Rendimiento | Ejecutar búsquedas | 2026-06-06 00:00:00 | RNF-009 | Campos indexados | Índices utilizados | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN |
| CP-056 | NaN | Escalabilidad catálogo | No funcional | Cargar 100.000 productos | 2026-06-06 00:00:00 | RNF-022 | Dataset masivo | Operación estable | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN |

## Carrito
| Unnamed: 0 | Unnamed: 1 | Unnamed: 2 | Unnamed: 3 | Unnamed: 4 | Unnamed: 5 | Unnamed: 6 | Unnamed: 7 | Unnamed: 8 | Unnamed: 9 | Unnamed: 10 | Unnamed: 11 | Unnamed: 12 | Unnamed: 13 | Unnamed: 14 | Unnamed: 15 | Unnamed: 16 |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN |
| Empresa: | NaN | NaN | NaN | NaN | Sprint | NaN | Módulo: | Usuario | Url de archivos de la prueba: | NaN | NaN | NaN | NaN | NaN | NaN | NaN |
| Elaboró: | NaN | NaN | NaN | NaN | NaN | NaN | Revisó: | NaN | Aprobó: | NaN | NaN | NaN | NaN | NaN | NaN | NaN |
| NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN | Información para el seguimiento | NaN | NaN | NaN | NaN | NaN |
| ID CASO DE PRUEBA | ID Caso de prueba anterior | Nombre del caso de prueba | Id de requerimiento | Descripción del requerimiento | Fecha creación | Funcionalidad/Característica | Datos/Accion | Resultado esperado | Procedimientos especiales requeridos | Dependencias con otros casos de prueba | Resultado obtenido | Estado | Fecha prueba | Estado reportado por el Developer | Observaciones | Imagen |
| CP-057 | CP-003 | Agregar producto al carrito | Funcional | Añadir producto | 2026-06-06 00:00:00 | RF-042 | Producto disponible | Ítem agregado | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN |
| CP-058 | CP-057 | Consultar carrito | Funcional | Ver carrito | 2026-06-06 00:00:00 | RF-043 | Usuario autenticado | Productos visibles | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN |
| CP-059 | CP-057 | Modificar cantidad | Funcional | Cambiar cantidad | 2026-06-06 00:00:00 | RF-044 | Cantidad válida | Total actualizado | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN |
| CP-060 | CP-057 | Eliminar ítem | Funcional | Remover producto | 2026-06-06 00:00:00 | RF-045 | Producto carrito | Producto eliminado | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN |
| CP-061 | CP-057 | Vaciar carrito | Funcional | Eliminar todos | 2026-06-06 00:00:00 | RF-046 | Carrito con productos | Carrito vacío | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN |

## Pedidos
| Unnamed: 0 | Unnamed: 1 | Unnamed: 2 | Unnamed: 3 | Unnamed: 4 | Unnamed: 5 | Unnamed: 6 | Unnamed: 7 | Unnamed: 8 | Unnamed: 9 | Unnamed: 10 | Unnamed: 11 | Unnamed: 12 | Unnamed: 13 | Unnamed: 14 | Unnamed: 15 | Unnamed: 16 |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN |
| Empresa: | NaN | NaN | NaN | NaN | Sprint | NaN | Módulo: | Usuario | Url de archivos de la prueba: | NaN | NaN | NaN | NaN | NaN | NaN | NaN |
| Elaboró: | NaN | NaN | NaN | NaN | NaN | NaN | Revisó: | NaN | Aprobó: | NaN | NaN | NaN | NaN | NaN | NaN | NaN |
| NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN | Información para el seguimiento | NaN | NaN | NaN | NaN | NaN |
| ID CASO DE PRUEBA | ID Caso de prueba anterior | Nombre del caso de prueba | Id de requerimiento | Descripción del requerimiento | Fecha creación | Funcionalidad/Característica | Datos/Accion | Resultado esperado | Procedimientos especiales requeridos | Dependencias con otros casos de prueba | Resultado obtenido | Estado | Fecha prueba | Estado reportado por el Developer | Observaciones | Imagen |
| CP-062 | CP-057 | Confirmar checkout | Funcional | Crear pedido | 2026-06-06 00:00:00 | RF-047 | Carrito válido | Pedido generado | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN |
| CP-063 | CP-062 | Calcular total pedido | Funcional | Verificar sumatoria | 2026-06-06 00:00:00 | RF-048 | Subtotales | Total correcto | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN |
| CP-064 | CP-062 | Cancelar pedido válido | Funcional | Cancelar estado permitido | 2026-06-06 00:00:00 | RF-049 | Estado PENDING | Pedido cancelado | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN |
| CP-065 | CP-062 | Listar pedidos propios | Funcional | Consultar historial | 2026-06-06 00:00:00 | RF-050 | Usuario autenticado | Pedidos mostrados | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN |
| CP-066 | CP-003 | Listar todos pedidos | Funcional | Consulta administrativa | 2026-06-06 00:00:00 | RF-051 | Rol Admin/Manager | Pedidos listados | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN |
| CP-067 | CP-066 | Actualizar estado pedido | Funcional | Cambiar estado | 2026-06-06 00:00:00 | RF-052 | Estado destino | Estado actualizado | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN |
| CP-068 | CP-062 | Consultar detalle pedido | Funcional | Ver pedido | 2026-06-06 00:00:00 | RF-053 | ID pedido | Detalle completo | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN |
| CP-069 | CP-062 | Atomicidad checkout | No Funcional | Simular fallo intermedio | 2026-06-06 00:00:00 | RNF-023 | Error transacción | Rollback completo | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN |
| CP-070 | CP-062 | Concurrencia stock | No Funcional | Compras simultáneas | 2026-06-06 00:00:00 | RNF-024 | Stock limitado | Sin sobreventa | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN |

## Pagos y facturacion
| Unnamed: 0 | Unnamed: 1 | Unnamed: 2 | Unnamed: 3 | Unnamed: 4 | Unnamed: 5 | Unnamed: 6 | Unnamed: 7 | Unnamed: 8 | Unnamed: 9 | Unnamed: 10 | Unnamed: 11 | Unnamed: 12 | Unnamed: 13 | Unnamed: 14 | Unnamed: 15 | Unnamed: 16 |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN |
| Empresa: | NaN | NaN | NaN | NaN | Sprint | NaN | Módulo: | Usuario | Url de archivos de la prueba: | NaN | NaN | NaN | NaN | NaN | NaN | NaN |
| Elaboró: | NaN | NaN | NaN | NaN | NaN | NaN | Revisó: | NaN | Aprobó: | NaN | NaN | NaN | NaN | NaN | NaN | NaN |
| NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN | Información para el seguimiento | NaN | NaN | NaN | NaN | NaN |
| ID CASO DE PRUEBA | ID Caso de prueba anterior | Nombre del caso de prueba | Id de requerimiento | Descripción del requerimiento | Fecha creación | Funcionalidad/Característica | Datos/Accion | Resultado esperado | Procedimientos especiales requeridos | Dependencias con otros casos de prueba | Resultado obtenido | Estado | Fecha prueba | Estado reportado por el Developer | Observaciones | Imagen |
| CP-071 | CP-062 | Iniciar pago | Funcional | Seleccionar método | 2026-06-06 00:00:00 | RF-054 | Método pago | Proceso iniciado | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN |
| CP-072 | CP-071 | Procesar pago exitoso | Funcional | Respuesta pasarela | 2026-06-06 00:00:00 | RF-055 | Pago aprobado | Estado PAGADO | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN |
| CP-073 | CP-071 | Validar coherencia monto | Funcional | Comparar montos | 2026-06-06 00:00:00 | RF-056 | Pedido vs pago | Coincidencia validada | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN |
| CP-074 | CP-072 | Solicitar reembolso | Funcional | Reembolso admin | 2026-06-06 00:00:00 | RF-057 | Pago válido | Reembolso generado | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN |
| CP-075 | CP-072 | Historial pagos | Funcional | Consultar pagos | 2026-06-06 00:00:00 | RF-058 | Admin | Historial completo | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN |
| CP-076 | CP-072 | Consultar pago propio | Funcional | Ver estado pago | 2026-06-06 00:00:00 | RF-059 | Propietario | Información mostrada | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN |
| CP-077 | CP-072 | Generar factura automática | Funcional | Completar pago | 2026-06-06 00:00:00 | RF-066 | Pago exitoso | Factura creada | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN |
| CP-078 | CP-077 | Referencia única factura | Funcional | Crear factura | 2026-06-06 00:00:00 | RF-067 | Factura nueva | Referencia única | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN |
| CP-079 | CP-077 | Consultar factura propia | Funcional | Ver factura | 2026-06-06 00:00:00 | RF-068 | Propietario | Factura visible | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN |
| CP-080 | CP-077 | Listar facturas | Funcional | Consulta administrativa | 2026-06-06 00:00:00 | RF-069 | Admin/Manager | Facturas listadas | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN |
| CP-081 | CP-072 | Auditoría pagos | No Funcional | Revisar logs | 2026-06-06 00:00:00 | RNF-025 | Cambio estado | Registro temporal creado | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN |
| CP-082 | CP-063 | Precisión monetaria | No Funcional | Operaciones decimales | 2026-06-06 00:00:00 | RNF-026 | Valores monetarios | Exactitud DECIMAL | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN |

## Envios y logistica
| Unnamed: 0 | Unnamed: 1 | Unnamed: 2 | Unnamed: 3 | Unnamed: 4 | Unnamed: 5 | Unnamed: 6 | Unnamed: 7 | Unnamed: 8 | Unnamed: 9 | Unnamed: 10 | Unnamed: 11 | Unnamed: 12 | Unnamed: 13 | Unnamed: 14 | Unnamed: 15 | Unnamed: 16 |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN |
| Empresa: | NaN | NaN | NaN | NaN | Sprint | NaN | Módulo: | Usuario | Url de archivos de la prueba: | NaN | NaN | NaN | NaN | NaN | NaN | NaN |
| Elaboró: | NaN | NaN | NaN | NaN | NaN | NaN | Revisó: | NaN | Aprobó: | NaN | NaN | NaN | NaN | NaN | NaN | NaN |
| NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN | Información para el seguimiento | NaN | NaN | NaN | NaN | NaN |
| ID CASO DE PRUEBA | ID Caso de prueba anterior | Nombre del caso de prueba | Id de requerimiento | Descripción del requerimiento | Fecha creación | Funcionalidad/Característica | Datos/Accion | Resultado esperado | Procedimientos especiales requeridos | Dependencias con otros casos de prueba | Resultado obtenido | Estado | Fecha prueba | Estado reportado por el Developer | Observaciones | Imagen |
| CP-083 | CP-067 | Crear envío | Funcional | Crear registro envío | 2026-06-06 00:00:00 | RF-060 | Pedido válido | Envío creado | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN |
| CP-084 | CP-083 | Asignar tracking | Funcional | Generar guía | 2026-06-06 00:00:00 | RF-061 | Número seguimiento | Tracking asignado | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN |
| CP-085 | CP-083 | Actualizar estado envío | Funcional | Cambiar estado logístico | 2026-06-06 00:00:00 | RF-062 | Estado nuevo | Estado actualizado | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN |
| CP-086 | CP-085 | Registrar devolución | Funcional | Marcar devolución | 2026-06-06 00:00:00 | RF-063 | Envío entregado | Estado DEVUELTO | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN |
| CP-087 | CP-084 | Consultar tracking propio | Funcional | Revisar seguimiento | 2026-06-06 00:00:00 | RF-064 | Tracking válido | Estado visible | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN |
| CP-088 | CP-083 | Listar envíos pendientes | Funcional | Consulta logística | 2026-06-06 00:00:00 | RF-065 | Estado pendiente | Lista generada | NaN | NaN | NaN | NaN | NaN | NaN | NaN | NaN |


---

## Pruebas de estrés y rendimiento (Punto 07 del plan JMeter)

> Matriz de los escenarios de estrés y rendimiento incluidos en el plan como sección **07** de `docs/knstore_stress_plan.jmx`
> (también integrada como punto 07 de `docs/knstore_test_plan.jmx`). Cargas parametrizables con `-JN_S1…N_S7`, `-JS5_DUR`, `-JES_PAGE`.

| ID | Módulo / objetivo | Escenario | Carga (default → ronda 3) | Resultado esperado |
|---|---|---|---|---|
| ES-01a…d | Catálogo (RNF-007/022) | Lectura pública: listado paginado, búsqueda full-text, categorías y detalle por slug | 50 → 100 hilos | P95 < 500 ms a nivel servidor; 0 errores |
| ES-02 | Autenticación | Login (coste bcrypt) bajo concurrencia | 40 → 60 hilos | 0 errores; P95 ≈ 0,4–1,4 s |
| ES-03 | Checkout | Escritura atómica de pedidos + pago + envío + factura | 40 → 60 hilos | 0 errores; stock sin negativos |
| ES-04 | Concurrencia (RNF-024) | 30–60 hilos simultáneos sobre stock 15 | 50 → 60 hilos | 15 ventas / resto rechazos; stock final 0, **sin sobreventa** |
| ES-04v | Verificación RNF-024 | Comprobación de stock final tras concurrencia | 1 consulta | stock == 0 |
| ES-05 | Resistencia (soak) | Lectura sostenida durante S5_DUR | 12 hilos · 60–240 s | Sin degradación progresiva ni errores |
| ES-06 | Spike | Ráfaga brusca (ramp 2 s) sobre catálogo | 100 hilos | Punto de saturación documentado (P95 ≈ 3,8 s) |
| ES-07 | Escalado | Rampa gradual (ramp 120 s) a 150 hilos | 150 hilos | Estabilidad: listado P95 ≈ 453 ms |

### Ejecuciones de referencia (EC2 · 2026-08-25)

| Ronda | Carga | Muestras | Errores | Observación |
|---|---|---|---|---|
| 1 · Base | 25/25/20/30 hilos · soak 60 s | 1 112 | 0 | Todo verde |
| 2 · Carga alta | 60/40/40/50 hilos · soak 300 s | 7 320 | 0 | Estable, 21,5 req/s |
| 3 · Límite | 100/60/60/60 · spike 100 (2 s) · escalado 150 · soak 240 s | 8 920 | 7 (0,08 %) | 99,92 % de éxito; 7 conexiones cortadas en el soak con recuperación inmediata; spike alcanzó P95 3,8 s |

**Conclusión de durabilidad:** el aplicativo se mantiene funcional hasta 150 hilos concurrentes con degradación suave; el punto de saturación se observa en ráfagas súbitas de 100+ hilos (P95 ≈ 3,8 s) y el servicio se recupera sin intervención. Comandos:

```bash
# Ronda de referencia por defecto
jmeter -n -t docs/knstore_stress_plan.jmx -l docs/jmeter/stress/resultados_stress.jtl -e -o docs/jmeter/stress/informe-html
# Escalado manual (limite)
jmeter -n -t docs/knstore_stress_plan.jmx -l resultados.jtl -JN_S1=150 -JN_S2=80 -JN_S3=80 -JN_S4=80 -JN_S6=150 -JN_S7=200 -JS5_DUR=600
```

---

## Resultados de ejecución (2026-08-26 · corrida completa, 6800 muestras, 0 errores)

| Caso | Nombre | Estado | Muestras | Promedio (ms) | P95 (ms) | Máx (ms) |
|---|---|---|---|---|---|---|
| CP-001 | Registro exitoso de usuario | PASA | 1 | 693 | 693 | 693 |
| CP-002 | Registro con correo duplicado | PASA | 1 | 101 | 101 | 101 |
| CP-004 | Inicio de sesion invalido | PASA | 1 | 183 | 183 | 183 |
| CP-008 | Acceso sin token | PASA | 1 | 91 | 91 | 91 |
| CP-023 | Hashing bcrypt | PASA | 1 | 180 | 180 | 180 |
| CP-024 | Vigencia JWT (30 dias) | PASA | 1 | 93 | 93 | 93 |
| CP-025 | Validacion de entradas | PASA | 1 | 94 | 94 | 94 |
| CP-026 | Restriccion CORS | PASA | 1 | 90 | 90 | 90 |
| CP-027 | Diseno responsivo login | PASA | 1 | 94 | 94 | 94 |
| CP-028 | Animaciones UI | PASA | 1 | 91 | 91 | 91 |
| CP-029 | Mensajes de error claros | PASA | 1 | 92 | 92 | 92 |
| CP-030 | Listar categorias | PASA | 1 | 93 | 93 | 93 |
| CP-031 | Consultar categoria | PASA | 1 | 92 | 92 | 92 |
| CP-035 | Listar subcategorias | PASA | 1 | 98 | 98 | 98 |
| CP-043 | Busqueda full-text | PASA | 1 | 91 | 91 | 91 |
| CP-044 | Listar productos paginados | PASA | 1 | 126 | 126 | 126 |
| CP-045 | Consultar producto por slug | PASA | 1 | 92 | 92 | 92 |
| CP-046 | Filtrar por categoria | PASA | 1 | 90 | 90 | 90 |
| CP-047 | Filtrar por subcategoria | PASA | 1 | 90 | 90 | 90 |
| CP-048 | Filtrar por marca | PASA | 1 | 90 | 90 | 90 |
| CP-049 | Filtrar por etiqueta | PASA | 1 | 155 | 155 | 155 |
| CP-053 | Tiempo respuesta catalogo | PASA | 1 | 151 | 151 | 151 |
| CP-054 | Paginacion obligatoria | PASA | 1 | 156 | 156 | 156 |
| CP-055 | Indices MongoDB | PASA | 1 | 92 | 92 | 92 |
| CP-056 | Escalabilidad catalogo (100k productos) | PASA | 10 | 424 | 445 | 500 |
| CP-009 | Listar usuarios | PASA | 1 | 95 | 95 | 95 |
| CP-010 | Consultar usuario por ID | PASA | 1 | 95 | 95 | 95 |
| CP-011 | Crear usuario por Admin | PASA | 1 | 190 | 190 | 190 |
| CP-012 | Actualizar usuario | PASA | 1 | 104 | 104 | 104 |
| CP-013 | Eliminacion logica usuario | PASA | 1 | 98 | 98 | 98 |
| CP-014 | Eliminacion fisica usuario | PASA | 1 | 94 | 94 | 94 |
| CP-050 | Acceso panel admin | PASA | 1 | 95 | 95 | 95 |
| CP-051 | CRUD desde interfaz | PASA | 1 | 109 | 109 | 109 |
| CP-052 | Visualizar estado activo | PASA | 1 | 92 | 92 | 92 |
| CP-032 | Crear categoria | PASA | 1 | 95 | 95 | 95 |
| CP-033 | Actualizar categoria | PASA | 1 | 97 | 97 | 97 |
| CP-036 | Crear subcategoria | PASA | 1 | 99 | 99 | 99 |
| CP-037 | Actualizar subcategoria | PASA | 1 | 96 | 96 | 96 |
| CP-039 | Crear producto | PASA | 1 | 96 | 96 | 96 |
| CP-040 | Actualizar producto | PASA | 1 | 101 | 101 | 101 |
| CP-041 | Calcular precio automatico | PASA | 1 | 98 | 98 | 98 |
| CP-042 | Eliminar producto | PASA | 1 | 94 | 94 | 94 |
| CP-038 | Eliminar subcategoria | PASA | 1 | 96 | 96 | 96 |
| CP-034 | Eliminar categoria | PASA | 1 | 94 | 94 | 94 |
| CP-003 | Inicio de sesion exitoso | PASA | 1 | 391 | 391 | 391 |
| CP-005 | Cierre de sesion | PASA | 1 | 93 | 93 | 93 |
| CP-006 | Verificacion automatica de sesion | PASA | 1 | 96 | 96 | 96 |
| CP-007 | Proteccion de rutas privadas | PASA | 1 | 121 | 121 | 121 |
| CP-015 | Consultar perfil propio | PASA | 1 | 98 | 98 | 98 |
| CP-016 | Eliminar perfil propio | PASA | 1 | 100 | 100 | 100 |
| CP-017 | Editar perfil propio | PASA | 1 | 102 | 102 | 102 |
| CP-018 | Crear direccion | PASA | 1 | 103 | 103 | 103 |
| CP-019 | Listar direcciones | PASA | 1 | 112 | 112 | 112 |
| CP-020 | Editar direccion | PASA | 1 | 107 | 107 | 107 |
| CP-021 | Eliminar direccion | PASA | 1 | 104 | 104 | 104 |
| CP-022 | Direccion predeterminada | PASA | 1 | 152 | 152 | 152 |
| CP-057 | Agregar producto al carrito | PASA | 1 | 118 | 118 | 118 |
| CP-058 | Consultar carrito | PASA | 1 | 105 | 105 | 105 |
| CP-059 | Modificar cantidad | PASA | 1 | 128 | 128 | 128 |
| CP-060 | Eliminar item | PASA | 1 | 120 | 120 | 120 |
| CP-061 | Vaciar carrito | PASA | 1 | 106 | 106 | 106 |
| CP-062 | Confirmar checkout | PASA | 1 | 195 | 195 | 195 |
| CP-063 | Calcular total pedido | PASA | 1 | 102 | 102 | 102 |
| CP-068 | Consultar detalle pedido | PASA | 1 | 110 | 110 | 110 |
| CP-065 | Listar pedidos propios | PASA | 1 | 137 | 137 | 137 |
| CP-071 | Iniciar pago | PASA | 1 | 107 | 107 | 107 |
| CP-072 | Procesar pago exitoso | PASA | 1 | 121 | 121 | 121 |
| CP-073 | Validar coherencia monto | PASA | 1 | 120 | 120 | 120 |
| CP-076 | Consultar pago propio | PASA | 1 | 2085 | 2085 | 2085 |
| CP-081 | Auditoria pagos | PASA | 1 | 112 | 112 | 112 |
| CP-082 | Precisión monetaria | PASA | 1 | 102 | 102 | 102 |
| CP-069 | Atomicidad checkout | PASA | 1 | 102 | 102 | 102 |
| CP-064 | Cancelar pedido valido | PASA | 1 | 152 | 152 | 152 |
| CP-089 | Consultar historial de pedido | PASA | 1 | 106 | 106 | 106 |
| CP-090 | Ownership cruzado: direccion ajena | PASA | 1 | 98 | 98 | 98 |
| CP-091 | Ownership cruzado: pedido ajeno | PASA | 1 | 100 | 100 | 100 |
| CP-087 | Consultar tracking propio | PASA | 1 | 1990 | 1990 | 1990 |
| CP-070 | Concurrencia stock (sin sobreventa) | PASA | 5 | 140 | 199 | 200 |
| CP-066 | Listar todos pedidos | PASA | 1 | 273 | 273 | 273 |
| CP-067 | Actualizar estado pedido | PASA | 1 | 109 | 109 | 109 |
| CP-077 | Generar factura automatica | PASA | 1 | 739 | 739 | 739 |
| CP-078 | Referencia unica factura | PASA | 1 | 102 | 102 | 102 |
| CP-079 | Consultar factura propia | PASA | 1 | 109 | 109 | 109 |
| CP-083 | Crear envio | PASA | 1 | 99 | 99 | 99 |
| CP-084 | Asignar tracking | PASA | 1 | 112 | 112 | 112 |
| CP-085 | Actualizar estado envio | PASA | 1 | 107 | 107 | 107 |
| CP-086 | Registrar devolucion | PASA | 1 | 117 | 117 | 117 |
| CP-088 | Listar envios pendientes | PASA | 1 | 134 | 134 | 134 |
| CP-074 | Solicitar reembolso | PASA | 1 | 109 | 109 | 109 |
| CP-075 | Historial pagos | PASA | 1 | 181 | 181 | 181 |
| CP-080 | Listar facturas | PASA | 1 | 193 | 193 | 193 |

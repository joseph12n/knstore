# [cite_start]Especificación de Requerimientos - KN-Store [cite: 1, 4]

## [cite_start]1. Requerimientos Funcionales (RF) [cite: 117]

### [cite_start]Módulo de Autenticación y Sesión [cite: 120]

- [cite_start]**RF-001 Registro de usuario nuevo:** Permite a un visitante anónimo registrarse proporcionando nombre, correo electrónico y contraseña. [cite: 122] [cite_start]Estado: Implementado. [cite: 122]
- [cite_start]**RF-002 Inicio de sesión:** Permite a un usuario registrado iniciar sesión mediante correo y contraseña para emitir un token JWT. [cite: 125] [cite_start]Estado: Implementado. [cite: 128]
- [cite_start]**RF-003 Cierre de sesión:** Permite al usuario autenticado cerrar sesión invalidando localmente el token. [cite: 131] [cite_start]Estado: Implementado. [cite: 131]
- [cite_start]**RF-004 Verificación automática de sesión:** Verifica la existencia y vigencia del token JWT al cargar el frontend. [cite: 134] [cite_start]Estado: Implementado. [cite: 134]
- [cite_start]**RF-005 Protección de rutas:** Un middleware intercepta rutas restringidas para validar el token JWT y el rol del usuario. [cite: 138] [cite_start]Estado: Implementado. [cite: 142]

### [cite_start]Módulo de Gestión de Usuarios (Administración) [cite: 142]

- [cite_start]**RF-006 Listar todos los usuarios:** El rol Admin puede obtener la lista de todos los usuarios registrados. [cite: 142] [cite_start]Estado: Implementado. [cite: 146]
- [cite_start]**RF-007 Consultar usuario por identificador:** El rol Admin puede consultar el detalle de cualquier usuario. [cite: 146] [cite_start]Estado: Implementado. [cite: 149]
- [cite_start]**RF-008 Crear usuario con rol arbitrario:** Permite al Admin crear nuevos usuarios especificando su rol. [cite: 149] [cite_start]Estado: Implementado. [cite: 153]
- [cite_start]**RF-009 Actualizar datos de un usuario:** Permite al Admin modificar los atributos de cualquier usuario. [cite: 153] [cite_start]Estado: Implementado. [cite: 157]
- [cite_start]**RF-010 Eliminar usuario (lógica y física):** Permite al Admin realizar eliminación lógica, y tras una segunda invocación, eliminación física. [cite: 158, 160] [cite_start]Estado: Implementado. [cite: 164]

### [cite_start]Módulo de Gestión de Categorías y Subcategorías [cite: 164, 177]

- [cite_start]**RF-011 Listar categorías públicamente:** Expone la lista de categorías activas sin requerir autenticación. [cite: 164] [cite_start]Estado: Implementado. [cite: 166]
- [cite_start]**RF-012 Consultar categoría por identificador:** Permite consultar el detalle de una categoría específica. [cite: 166] [cite_start]Estado: Implementado. [cite: 168]
- [cite_start]**RF-013 Crear categoría:** Permite a Manager y Admin crear nuevas categorías. [cite: 168] [cite_start]Estado: Implementado. [cite: 171]
- [cite_start]**RF-014 Actualizar categoría:** Permite a Manager y Admin actualizar atributos de una categoría. [cite: 171] [cite_start]Estado: Implementado. [cite: 174]
- [cite_start]**RF-015 Eliminar categoría:** Permite al Admin eliminar categorías. [cite: 174] [cite_start]Estado: Implementado. [cite: 177]
- [cite_start]**RF-016 Listar y consultar subcategorías públicamente:** Expone la lista de subcategorías activas. [cite: 177] [cite_start]Estado: Implementado. [cite: 179]
- [cite_start]**RF-017 Crear subcategoría:** Permite a Manager y Admin crear subcategorías. [cite: 179] [cite_start]Estado: Implementado. [cite: 181]
- [cite_start]**RF-018 Actualizar subcategoría:** Permite modificar atributos de una subcategoría existente. [cite: 182] [cite_start]Estado: Implementado. [cite: 184]
- [cite_start]**RF-019 Eliminar subcategoría:** Permite al Admin eliminar subcategorías. [cite: 184] [cite_start]Estado: Implementado. [cite: 187]

### [cite_start]Módulo de Gestión de Productos y Catálogo Público [cite: 187, 206]

- [cite_start]**RF-020 Crear producto con variantes:** Permite a Manager y Admin crear productos. [cite: 187] [cite_start]Estado: Implementado. [cite: 194]
- [cite_start]**RF-021 Actualizar producto:** Permite actualizar cualquier atributo de un producto existente. [cite: 194] [cite_start]Estado: Implementado. [cite: 197]
- [cite_start]**RF-022 Cálculo automático de precios:** Calcula dinámicamente precio final, utilidad y margen. [cite: 197] [cite_start]Estado: Implementado. [cite: 201]
- [cite_start]**RF-023 Eliminar producto:** Permite al rol Admin eliminar productos. [cite: 201] [cite_start]Estado: Implementado. [cite: 204]
- [cite_start]**RF-024 Indexación full-text:** Declara índices de texto para soportar búsquedas eficientes. [cite: 204] [cite_start]Estado: Implementado. [cite: 206]
- [cite_start]**RF-025 Listar productos con filtros y paginación:** Permite consultar productos con parámetros de búsqueda. [cite: 206, 207] [cite_start]Estado: Implementado. [cite: 210]
- [cite_start]**RF-026 Consultar producto por slug:** Permite consultar el detalle completo de un producto mediante su slug. [cite: 210] [cite_start]Estado: Implementado. [cite: 212]
- [cite_start]**RF-027 Filtrar productos por categoría:** Filtra productos por una categoría específica. [cite: 212] [cite_start]Estado: Implementado. [cite: 214]
- [cite_start]**RF-028 Filtrar productos por subcategoría:** Filtra productos por una subcategoría específica. [cite: 214] [cite_start]Estado: Implementado. [cite: 216]
- [cite_start]**RF-029 Filtrar productos por marca:** Consulta productos por su marca. [cite: 216] [cite_start]Estado: Implementado. [cite: 218]
- [cite_start]**RF-030 Filtrar productos por etiqueta:** Consulta productos que incluyen una etiqueta solicitada. [cite: 218, 221] [cite_start]Estado: Implementado. [cite: 222]

### [cite_start]Módulos de Perfil, Panel Admin y Direcciones [cite: 222, 232, 240]

- [cite_start]**RF-031 Consultar perfil propio:** Permite a un usuario autenticado consultar su propio perfil. [cite: 222] [cite_start]Estado: Implementado. [cite: 225]
- [cite_start]**RF-032 Eliminar perfil propio:** Permite a un usuario autenticado solicitar la eliminación de su cuenta. [cite: 225] [cite_start]Estado: Implementado. [cite: 228]
- [cite_start]**RF-033 Editar datos del perfil propio:** Ofrece interfaz para que el usuario edite sus datos. [cite: 228, 229] [cite_start]Estado: Parcial. [cite: 232]
- [cite_start]**RF-034 Acceso al panel administrativo:** Expone rutas bajo `/admin` segmentadas por rol. [cite: 232] [cite_start]Estado: Implementado. [cite: 235]
- [cite_start]**RF-035 Operaciones CRUD desde la interfaz:** Ofrece interfaz visual para operaciones de creación, edición y eliminación. [cite: 235] [cite_start]Estado: Implementado. [cite: 238]
- [cite_start]**RF-036 Visualización del estado:** Muestra el estado activo o inactivo de cada entidad. [cite: 238] [cite_start]Estado: Implementado. [cite: 240]
- [cite_start]**RF-037 Crear dirección:** Permite registrar una nueva dirección de envío. [cite: 243] [cite_start]Estado: Pendiente. [cite: 248]
- [cite_start]**RF-038 Listar direcciones propias:** Permite consultar direcciones registradas. [cite: 248] [cite_start]Estado: Pendiente. [cite: 251]
- [cite_start]**RF-039 Editar dirección propia:** Permite al usuario editar sus direcciones. [cite: 251] [cite_start]Estado: Pendiente. [cite: 256]
- [cite_start]**RF-040 Eliminar dirección propia:** Permite al usuario eliminar sus direcciones. [cite: 256, 257] [cite_start]Estado: Pendiente. [cite: 261]
- [cite_start]**RF-041 Marcar dirección como predeterminada:** Permite designar una dirección como predeterminada. [cite: 261] [cite_start]Estado: Pendiente. [cite: 265]

### [cite_start]Módulo de Carrito y Pedidos [cite: 265, 287]

- [cite_start]**RF-042 Agregar producto al carrito:** Permite añadir un producto al carrito o crear uno si no existe. [cite: 268, 269] [cite_start]Estado: Pendiente. [cite: 274]
- [cite_start]**RF-043 Consultar carrito:** Permite consultar el contenido actual del carrito. [cite: 274] [cite_start]Estado: Pendiente. [cite: 277]
- [cite_start]**RF-044 Modificar cantidad de item:** Permite actualizar la cantidad de un producto en el carrito. [cite: 277] [cite_start]Estado: Pendiente. [cite: 281]
- [cite_start]**RF-045 Eliminar item del carrito:** Permite remover un producto específico. [cite: 281] [cite_start]Estado: Pendiente. [cite: 284]
- [cite_start]**RF-046 Vaciar carrito:** Elimina todos los items del carrito. [cite: 284] [cite_start]Estado: Pendiente. [cite: 287]
- [cite_start]**RF-047 Confirmar pedido (checkout):** Permite confirmar el carrito convirtiéndolo en pedido. [cite: 290] [cite_start]Estado: Pendiente. [cite: 296]
- [cite_start]**RF-048 Cálculo automático del total:** Calcula el total a partir de la sumatoria de subtotales. [cite: 296] [cite_start]Estado: Pendiente. [cite: 300]
- [cite_start]**RF-049 Cancelar pedido:** Permite cancelar un pedido en estado PENDING o CONFIRMED. [cite: 300, 301] [cite_start]Estado: Pendiente. [cite: 306]
- [cite_start]**RF-050 Listar pedidos propios:** Permite consultar historial de pedidos. [cite: 306] [cite_start]Estado: Pendiente. [cite: 309]
- [cite_start]**RF-051 Listar todos los pedidos:** Permite a Admin y Manager consultar todos los pedidos. [cite: 309] [cite_start]Estado: Pendiente. [cite: 312]
- [cite_start]**RF-052 Actualizar estado del pedido:** Permite promover el pedido por sus diferentes estados. [cite: 312] [cite_start]Estado: Pendiente. [cite: 316]
- [cite_start]**RF-053 Consultar detalle de un pedido:** Consulta detalle completo incluyendo relaciones. [cite: 316] [cite_start]Estado: Pendiente. [cite: 319]

### [cite_start]Módulos de Pagos, Envíos y Facturación [cite: 319, 346, 373]

- [cite_start]**RF-054 Iniciar proceso de pago:** Inicia pago indicando el método. [cite: 323] [cite_start]Estado: Pendiente. [cite: 328]
- [cite_start]**RF-055 Procesar resultado del pago:** Interactúa con la pasarela externa para procesar el pago. [cite: 328] [cite_start]Estado: Pendiente. [cite: 332]
- [cite_start]**RF-056 Validar coherencia de pago:** Verifica que el monto del pago coincida con el pedido. [cite: 332, 333] [cite_start]Estado: Pendiente. [cite: 336]
- [cite_start]**RF-057 Solicitar reembolso:** Permite al Admin iniciar un reembolso. [cite: 336] [cite_start]Estado: Pendiente. [cite: 340]
- [cite_start]**RF-058 Consultar historial de pagos:** Permite al Admin consultar historial completo. [cite: 340] [cite_start]Estado: Pendiente. [cite: 343]
- [cite_start]**RF-059 Consultar pago de pedido propio:** Permite al propietario consultar el estado de pago. [cite: 343] [cite_start]Estado: Pendiente. [cite: 346]
- [cite_start]**RF-060 Crear envío para un pedido:** Permite a Admin y Manager crear registro de envío. [cite: 350] [cite_start]Estado: Pendiente. [cite: 354]
- [cite_start]**RF-061 Asignar número de seguimiento:** Permite asignar número de tracking al envío. [cite: 354] [cite_start]Estado: Pendiente. [cite: 357]
- [cite_start]**RF-062 Actualizar estado del envío:** Permite promover el envío entre estados logísticos. [cite: 357] [cite_start]Estado: Pendiente. [cite: 362]
- [cite_start]**RF-063 Registrar devolución de envío:** Permite marcar envío como devuelto. [cite: 362, 363] [cite_start]Estado: Pendiente. [cite: 366]
- [cite_start]**RF-064 Consultar tracking propio:** Permite consultar seguimiento del envío propio. [cite: 366] [cite_start]Estado: Pendiente. [cite: 369]
- [cite_start]**RF-065 Listar envíos pendientes:** Permite listar envíos para apoyar logística. [cite: 369] [cite_start]Estado: Pendiente. [cite: 372]
- [cite_start]**RF-066 Generar factura automáticamente:** Genera registro al completarse el pago. [cite: 375] [cite_start]Estado: Pendiente. [cite: 379]
- [cite_start]**RF-067 Generar referencia única:** Produce identificador legible y único de factura. [cite: 379] [cite_start]Estado: Pendiente. [cite: 382]
- [cite_start]**RF-068 Consultar factura propia:** Permite al propietario consultar su factura. [cite: 383] [cite_start]Estado: Pendiente. [cite: 386]
- [cite_start]**RF-069 Listar facturas:** Permite a Admin y Manager consultar todas las facturas. [cite: 386] [cite_start]Estado: Pendiente. [cite: 389]

---

## [cite_start]2. Requerimientos No Funcionales (RNF) [cite: 390]

### [cite_start]Seguridad [cite: 390]

- [cite_start]**RNF-001 Hashing de contraseñas:** Almacenamiento con bcrypt y costo no inferior a 10. [cite: 390] [cite_start]Estado: Implementado. [cite: 393]
- [cite_start]**RNF-002 Autenticación JWT:** Tokens con vigencia de 30 días, esquema Bearer. [cite: 394] [cite_start]Estado: Implementado. [cite: 395]
- [cite_start]**RNF-003 Validación de entradas:** Rutas de escritura con validaciones explícitas (express-validator). [cite: 395] [cite_start]Estado: Implementado. [cite: 398]
- [cite_start]**RNF-004 Control de acceso por rol:** Rutas privadas verifican roles. [cite: 398] [cite_start]Estado: Implementado. [cite: 400]
- [cite_start]**RNF-005 Eliminación lógica como mecanismo por defecto:** DELETE ejecuta eliminación lógica (active=false) inicialmente. [cite: 400] [cite_start]Estado: Implementado. [cite: 404]
- [cite_start]**RNF-006 Configuración de CORS:** Backend restringe orígenes permitidos explícitamente. [cite: 404] [cite_start]Estado: Implementado. [cite: 406]

### [cite_start]Rendimiento [cite: 406]

- [cite_start]**RNF-007 Tiempo de respuesta:** Consultas al catálogo menores a 500ms (P95). [cite: 406] [cite_start]Estado: Implementado. [cite: 408]
- [cite_start]**RNF-008 Paginación obligatoria:** Consultas extensas soportan y aplican paginación por defecto. [cite: 408] [cite_start]Estado: Implementado. [cite: 410]
- [cite_start]**RNF-009 Índices en MongoDB:** Campos de búsqueda respaldados por índices declarados. [cite: 410, 411] [cite_start]Estado: Implementado. [cite: 414]

### [cite_start]Usabilidad [cite: 414]

- [cite_start]**RNF-010 Diseño responsivo:** Frontend adaptable desde 360px hasta 1920px. [cite: 414] [cite_start]Estado: Implementado. [cite: 417]
- [cite_start]**RNF-011 Animaciones e interacciones:** Animaciones sin reducir tasa de frames bajo 50 FPS. [cite: 417, 418] [cite_start]Estado: Implementado. [cite: 420]
- [cite_start]**RNF-012 Sistema de tokens de diseño:** Colores y espacios manejados con tokens CSS. [cite: 420, 421] [cite_start]Estado: Implementado. [cite: 423]
- [cite_start]**RNF-013 Mensajes de error claros:** Respuestas de error incluyen mensajes legibles sin exponer sistemas. [cite: 423] [cite_start]Estado: Implementado. [cite: 426]

### [cite_start]Mantenibilidad y Compatibilidad [cite: 426, 435]

- [cite_start]**RNF-014 Separación estricta de capas:** Respetar estructura Middleware → Controlador → Modelo. [cite: 426] [cite_start]Estado: Implementado. [cite: 429]
- [cite_start]**RNF-015 Convenciones de nomenclatura:** Uso estandarizado de camelCase, PascalCase y kebab-case. [cite: 430] [cite_start]Estado: Implementado. [cite: 433]
- [cite_start]**RNF-016 Documentación interna actualizada:** Cambios documentados sincronizados con cada release. [cite: 433, 434] [cite_start]Estado: Implementado. [cite: 435]
- [cite_start]**RNF-017 Compatibilidad de navegadores:** Funcional en las dos últimas versiones mayores de Chrome, Firefox, Edge y Safari. [cite: 435] [cite_start]Estado: Implementado. [cite: 437]
- [cite_start]**RNF-018 Versión de Node.js:** Ejecución correcta sobre versiones LTS activas de Node.js (20.x+). [cite: 438] [cite_start]Estado: Implementado. [cite: 440]

### [cite_start]Disponibilidad, Escalabilidad e Integridad [cite: 440, 446, 452]

- [cite_start]**RNF-019 Disponibilidad operativa:** Disponibilidad mensual de producción no inferior al 99,5%. [cite: 440] [cite_start]Estado: Implementado. [cite: 443]
- [cite_start]**RNF-020 Manejo robusto de errores:** Backend intercepta errores para evitar caídas de proceso. [cite: 443] [cite_start]Estado: Implementado. [cite: 445]
- [cite_start]**RNF-021 Arquitectura sin estado:** Backend no almacena sesión en memoria (habilita escalado). [cite: 446, 448] [cite_start]Estado: Implementado. [cite: 450]
- [cite_start]**RNF-022 Capacidad de crecimiento:** Operación correcta con hasta 100.000 productos. [cite: 450] [cite_start]Estado: Implementado. [cite: 452]
- [cite_start]**RNF-023 Atomicidad del checkout:** Confirmación de pedido es una operación atómica. [cite: 452] [cite_start]Estado: Pendiente. [cite: 455]
- [cite_start]**RNF-024 Concurrencia en operaciones de stock:** Manejo de solicitudes simultáneas contra el inventario. [cite: 455] [cite_start]Estado: Pendiente. [cite: 458]
- [cite_start]**RNF-025 Auditoría de pagos y pedidos:** Modificaciones de estado generan registros con marca temporal. [cite: 458] [cite_start]Estado: Pendiente. [cite: 460]
- [cite_start]**RNF-026 Precisión monetaria:** Cálculos utilizan tipos numéricos DECIMAL(10,2) o DECIMAL(12,2). [cite: 460, 461] [cite_start]Estado: Pendiente. [cite: 465]

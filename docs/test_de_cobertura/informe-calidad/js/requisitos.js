/* ==========================================================================
   Cumplimiento de requisitos — verificación contra código y pruebas
   Fuente: docs/01-ANALYSIS/03-Requerimentos/requerimientos.md
         + docs/01-ANALYSIS/REQUERIMIENTOS_PENDIENTES.md (RF-070→076, RNF-027→031)
   ========================================================================== */
window.REQUISITOS = {
  total: 107,
  rf: 76,
  rnf: 31,
  implementados: 107,
  desactualizados: 46,
  nuevos: 11,
  modulos: [
    {
      n: 'Autenticación y sesión',
      ids: 'RF-001 → RF-005',
      estado: 'Implementado',
      cob: 'Seguridad 92,8 %',
      ev: 'AccountResourceIT (32), AuthenticateControllerIT (3), TokenAuthenticationIT (4), DomainUserDetailsServiceIT (6), SecurityUtilsUnitTest (9), private-route.spec'
    },
    {
      n: 'Gestión de usuarios (admin)',
      ids: 'RF-006 → RF-010',
      estado: 'Implementado',
      cob: 'UserService 22 pruebas unitarias',
      ev: 'UserResourceIT (14), UserServiceIT (7), UserServiceTest (22), UserMapperTest (10), user-management.reducer.spec'
    },
    {
      n: 'Categorías y subcategorías',
      ids: 'RF-011 → RF-019',
      estado: 'Implementado',
      cob: 'Servicios 8 + 9 unitarias',
      ev: 'CategoriaResourceIT (18), SubcategoriaResourceIT (18), CategoriaServiceImplTest (8), SubcategoriaServiceImplTest (9)'
    },
    {
      n: 'Productos y catálogo público',
      ids: 'RF-020 → RF-030',
      estado: 'Implementado',
      cob: 'Dominio 96,6 %',
      ev: 'ProductoResourceIT (23), Marca (17), Etiqueta (16), Imagen (16), Precio (17), Inventario (16); SearchPage.spec, CategoryPage.spec, useCatalog.spec'
    },
    {
      n: 'Perfil, panel admin y direcciones',
      ids: 'RF-031 → RF-041',
      estado: 'Implementado',
      cob: 'service/impl 88,9 %',
      ev: 'CuentaResourceIT (28), DireccionResourceIT (28), AddressForm.spec (7); páginas /cuenta sin spec (AddressCard 33 %)'
    },
    {
      n: 'Carrito de compras',
      ids: 'RF-042 → RF-046',
      estado: 'Implementado',
      cob: 'Ítems 67,7 % · servicio 68,3 %',
      ev: 'CarritoResourceIT (16), ItemCarritoResourceIT (17, 4 fallos), CarritoServiceImplTest (12), CartContext.spec (14)',
      riesgo: 'Alto'
    },
    {
      n: 'Pedidos y checkout',
      ids: 'RF-047 → RF-053',
      estado: 'Implementado',
      cob: 'Recurso 55,3 % · servicio 65,8 %',
      ev: 'PedidoResourceIT (19), CheckoutServiceIT (5), PedidoServiceImplTest (7); CheckoutPage.spec, OrdersPage.spec, OrderDetailPage.spec',
      riesgo: 'Medio'
    },
    {
      n: 'Pagos',
      ids: 'RF-054 → RF-059',
      estado: 'Implementado',
      cob: 'service/payment 100 %',
      ev: 'PagoResourceIT (18), PagoFlujoResourceIT (8), PagoServiceImplTest (14), SimulatedPaymentGatewayTest (5)'
    },
    {
      n: 'Envíos y distribución',
      ids: 'RF-060 → RF-065',
      estado: 'Implementado',
      cob: 'EnvioResource 59,5 %',
      ev: 'EnvioResourceIT (16), EnvioServiceImplTest (7); AdminShipmentsPage sin spec',
      riesgo: 'Medio'
    },
    {
      n: 'Facturación',
      ids: 'RF-066 → RF-069',
      estado: 'Implementado',
      cob: 'service/invoice 92,6 %',
      ev: 'FacturaResourceIT (18), FacturaPdfServiceTest (3), FacturaServiceImplTest (3), MailServiceIT (10); invoice.ts 9 %'
    },
    {
      n: 'Atomicidad, concurrencia y auditoría',
      ids: 'RNF-023 → RNF-026',
      estado: 'Implementado',
      cob: 'CheckoutServiceIT verificado con Testcontainers',
      ev: 'CheckoutServiceIT (5), MoneyUtilsTest (4), HistorialEstadoServiceImplTest (2), ResourceAccessServiceTest (23)'
    },
    {
      n: 'Rendimiento, búsqueda y seguridad de endpoints',
      ids: 'RNF-027 → RNF-031',
      estado: 'Implementado',
      cob: 'MongoIdUtils 8 · Secuencias 5',
      ev: 'MongoIdUtilsTest (8), SecuenciaServiceImplTest (5), ProductoResourceIT (23), InvalidBearerTokenFilterIT (5)'
    }
  ]
};

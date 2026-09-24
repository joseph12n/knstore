/**
 * Seed de OPERACIONES para KN-Store (usuarios, cuentas, direcciones, pedidos, pagos, envíos y facturas).
 *
 * Todo se hace vía API real (nada de Mongo directo) y es idempotente:
 *   - Usuarios: se reutilizan si el login ya existe (`GET /api/admin/users/{login}`).
 *   - Cuentas: se reutilizan si ya existe una para ese user (`GET /api/cuentas` paginado).
 *   - Direcciones: se reutilizan por texto de dirección dentro de la cuenta; la primera
 *     se marca predeterminada con `PATCH /api/direccions/{id}/predeterminada`.
 *   - Pedidos: cada uno lleva una marca en `notasCliente` (`seed-operaciones v1 | <login> | s<slot>`);
 *     si la marca ya existe no se vuelve a hacer checkout.
 *   - Estados: se aplican transiciones hacia un objetivo determinista (CONFIRMED, SHIPPED + envío
 *     IN_TRANSIT con tracking, DELIVERED + envío DELIVERED, CANCELLED vía `POST /cancelar`) sin
 *     retroceder; lo ya alcanzado se omite. Pedido y envío se mueven juntos para no dejar incoherencias.
 *
 * Los clientes se loguean UNO A UNO para el checkout porque la Cuenta sale SIEMPRE del JWT.
 *
 * Uso:
 *   node scripts/seed-operaciones.js [BASE_URL] [--force]
 *
 * Ejemplos:
 *   node scripts/seed-operaciones.js
 *   node scripts/seed-operaciones.js http://localhost:8080
 *   node scripts/seed-operaciones.js https://app.knstore.duckdns.org --force   # crea pedidos extra aunque exista la marca
 *
 * Flags:
 *   --force  ignora la marca de idempotencia de PEDIDOS y hace checkout nuevo por slot (más volumen).
 *            Usuarios, cuentas y direcciones nunca se duplican, con o sin --force.
 *
 * Variables de entorno:
 *   - KNSTORE_BASE_URL        (default: http://localhost:8080)
 *   - KNSTORE_USERNAME        (admin, default: admin)
 *   - KNSTORE_PASSWORD        (admin, default: admin)
 *   - KNSTORE_CLIENT_PASSWORD (contraseña de los clientes demo, default: Demo1234!)
 */

import axios from 'axios';

const args = process.argv.slice(2);
const flags = args.filter(a => a.startsWith('--'));
const posicionales = args.filter(a => !a.startsWith('--'));

const FORCE = flags.includes('--force');
const BASE_URL = posicionales[0] || process.env.KNSTORE_BASE_URL || 'http://localhost:8080';
const USERNAME = process.env.KNSTORE_USERNAME || 'admin';
const PASSWORD = process.env.KNSTORE_PASSWORD || 'admin';
const PASSWORD_CLIENTE = process.env.KNSTORE_CLIENT_PASSWORD || 'Demo1234!';

/** Marca en notasCliente que identifica los pedidos de este seed (idempotencia). */
const MARCA = 'seed-operaciones v1';
const MOTIVO_CANCELACION = 'Pedido de ejemplo cancelado por seed';

const api = axios.create({
  baseURL: `${BASE_URL}/api`,
  headers: { 'Content-Type': 'application/json' },
});

let token = null;
api.interceptors.request.use(config => {
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

/** Instancias por cliente (cada uno con su propio JWT para el checkout). */
const apiClientes = new Map();

const resumen = {
  usuariosCreados: 0,
  usuariosReutilizados: 0,
  cuentasCreadas: 0,
  cuentasReutilizadas: 0,
  direccionesCreadas: 0,
  direccionesReutilizadas: 0,
  predeterminadas: 0,
  pedidosCreados: 0,
  pedidosReutilizados: 0,
  pedidosSinCrear: 0,
  estadosNoAplicados: 0,
  trackingAsignados: 0,
  sinProductos: false,
  clientesSinSesion: [],
};

/**
 * Patrón determinista de estados: los clientes con 1 pedido rotan por índice y la
 * cliente con 3 pedidos cubre los estados restantes, dejando representantes de todos.
 */
const PATRON_ESTADOS = ['CONFIRMED', 'SHIPPED', 'DELIVERED', 'CANCELLED'];
const METODOS_PAGO = ['NEQUI', 'PSE', 'CASH', 'CREDIT_CARD', 'DEBIT_CARD', 'EFECTY', 'DAVIPLATA', 'CONTRA_ENTREGA'];
const TIPOS_ENVIO = ['ESTANDAR', 'EXPRESS', 'MISMO_DIA', 'PROGRAMADO', 'PUNTO_PICKUP'];
const CAMINOS = {
  CONFIRMED: ['CONFIRMED'],
  SHIPPED: ['CONFIRMED', 'SHIPPED'],
  DELIVERED: ['CONFIRMED', 'SHIPPED', 'DELIVERED'],
};
const ORDEN_ENVIO = { PENDING: 0, DISPATCHED: 1, IN_TRANSIT: 2, IN_CITY: 3, DELIVERED: 4 };
const PASOS_ENVIO = ['DISPATCHED', 'IN_TRANSIT', 'IN_CITY', 'DELIVERED'];

/** Clientes demo (nombres y lugares SOLO letras, teléfonos/documentos SOLO dígitos). */
const CLIENTES = [
  {
    login: 'ana.gomez',
    email: 'ana.gomez@example.com',
    firstName: 'Ana',
    lastName: 'Gomez',
    cuenta: {
      numDocumento: '70000001',
      primerNombre: 'Ana',
      segundoNombre: 'Maria',
      primerApellido: 'Gomez',
      segundoApellido: 'Rios',
      genero: 'FEMENINO',
      fechaNacimiento: '1995-03-14',
      celular: '3001110001',
      telefono: '6011110001',
    },
    direcciones: [
      { direccion: 'Carrera 15 numero 20 - 30', barrio: 'Rosales', localidad: 'Chapinero' },
      { direccion: 'Calle 93 numero 12 - 45', barrio: 'Cedritos', localidad: 'Usaquen' },
    ],
    pedidos: [{ slot: 0, objetivo: PATRON_ESTADOS[0] }],
  },
  {
    login: 'carlos.ruiz',
    email: 'carlos.ruiz@example.com',
    firstName: 'Carlos',
    lastName: 'Ruiz',
    cuenta: {
      numDocumento: '70000002',
      primerNombre: 'Carlos',
      segundoNombre: 'Andres',
      primerApellido: 'Ruiz',
      segundoApellido: 'Moreno',
      genero: 'MASCULINO',
      fechaNacimiento: '1990-07-22',
      celular: '3001110002',
      telefono: '6011110002',
    },
    direcciones: [{ direccion: 'Avenida Suba numero 10 - 20', barrio: 'Nativitas', localidad: 'Suba' }],
    pedidos: [{ slot: 0, objetivo: PATRON_ESTADOS[1] }],
  },
  {
    login: 'maria.lopez',
    email: 'maria.lopez@example.com',
    firstName: 'Maria',
    lastName: 'Lopez',
    cuenta: {
      numDocumento: '70000003',
      primerNombre: 'Maria',
      segundoNombre: 'Fernanda',
      primerApellido: 'Lopez',
      segundoApellido: 'Castro',
      genero: 'FEMENINO',
      fechaNacimiento: '1998-11-05',
      celular: '3001110003',
      telefono: '6011110003',
    },
    direcciones: [{ direccion: 'Transversal 45 numero 7 - 11', barrio: 'Belencito', localidad: 'Barrios Unidos' }],
    pedidos: [{ slot: 0, objetivo: PATRON_ESTADOS[2] }],
  },
  {
    login: 'andres.ruiz',
    email: 'andres.ruiz@example.com',
    firstName: 'Andres',
    lastName: 'Ruiz',
    cuenta: {
      numDocumento: '70000004',
      primerNombre: 'Andres',
      segundoNombre: 'Felipe',
      primerApellido: 'Ruiz',
      segundoApellido: 'Salazar',
      genero: 'MASCULINO',
      fechaNacimiento: '1992-01-30',
      celular: '3001110004',
      telefono: '6011110004',
    },
    direcciones: [{ direccion: 'Calle 26 numero 4 - 80', barrio: 'San Victorino', localidad: 'Santa Fe' }],
    pedidos: [{ slot: 0, objetivo: PATRON_ESTADOS[3] }],
  },
  {
    login: 'lucia.martin',
    email: 'lucia.martin@example.com',
    firstName: 'Lucia',
    lastName: 'Martin',
    cuenta: {
      numDocumento: '70000005',
      primerNombre: 'Lucia',
      segundoNombre: 'Paula',
      primerApellido: 'Martin',
      segundoApellido: 'Herrera',
      genero: 'FEMENINO',
      fechaNacimiento: '1997-05-18',
      celular: '3001110005',
      telefono: '6011110005',
    },
    direcciones: [
      { direccion: 'Carrera 11 numero 93 - 40', barrio: 'El Retiro', localidad: 'Chapinero' },
      { direccion: 'Diagonal 75 numero 15 - 20', barrio: 'Concepcion', localidad: 'Teusaquillo' },
    ],
    // 3 pedidos: rota sobre el patrón desde su índice (CONFIRMED, SHIPPED, DELIVERED)
    pedidos: [
      { slot: 0, objetivo: PATRON_ESTADOS[4 % PATRON_ESTADOS.length] },
      { slot: 1, objetivo: PATRON_ESTADOS[(4 + 1) % PATRON_ESTADOS.length] },
      { slot: 2, objetivo: PATRON_ESTADOS[(4 + 2) % PATRON_ESTADOS.length] },
    ],
  },
];

/** Pedidos del seed (existentes + creados), con búsqueda por la marca de notasCliente. */
const pedidosSeed = [];
const buscarPorMarca = marca => pedidosSeed.find(p => p.notasCliente === marca);
const marcaDe = (cliente, slot) => `${MARCA} | ${cliente.login} | s${slot}`;

async function autenticarAdmin() {
  const { data } = await api.post('/authenticate', { username: USERNAME, password: PASSWORD, rememberMe: true });
  token = data.id_token;
  console.log(`Autenticado como ${USERNAME} en ${BASE_URL}`);
}

async function apiDeCliente(cliente) {
  if (apiClientes.has(cliente.login)) return apiClientes.get(cliente.login);
  try {
    const { data } = await axios.post(`${BASE_URL}/api/authenticate`, {
      username: cliente.login,
      password: PASSWORD_CLIENTE,
      rememberMe: true,
    });
    const instancia = axios.create({
      baseURL: `${BASE_URL}/api`,
      headers: { 'Content-Type': 'application/json', Authorization: `Bearer ${data.id_token}` },
    });
    apiClientes.set(cliente.login, instancia);
    return instancia;
  } catch (error) {
    const fallo = new Error(`no se pudo iniciar sesión como ${cliente.login} (${mensajeError(error)})`);
    fallo.esSesionCliente = true;
    throw fallo;
  }
}

/** Listado paginado genérico (respeta x-total-count, patrón seed-contenido.js). */
async function listar(recurso, params = {}) {
  const todos = [];
  const size = 200;
  for (let page = 0; ; page += 1) {
    const { data, headers } = await api.get(`/${recurso}`, { params: { page, size, sort: 'id,asc', ...params } });
    todos.push(...data);
    const total = Number(headers['x-total-count'] ?? data.length);
    if (data.length === 0 || todos.length >= total) break;
  }
  return todos;
}

function mensajeError(error) {
  const data = error.response?.data;
  if (data?.errorKey) return data.errorKey;
  if (typeof data === 'string') return data;
  if (data?.message) return data.message;
  return error.message;
}

async function asegurarTipoDocumento() {
  const { data } = await api.get('/tipo-documentos');
  const tipos = Array.isArray(data) ? data : [];
  const existente = tipos.find(t => (t.sigla || '').toUpperCase() === 'CC');
  if (existente) return existente;
  const creado = await api.post('/tipo-documentos', { sigla: 'CC', nombreTipo: 'Cedula', estado: 'ACTIVO' });
  console.log(`  Tipo de documento CC creado (${creado.data.id})`);
  return creado.data;
}

async function asegurarUsuario(cliente) {
  try {
    const { data } = await api.get(`/admin/users/${cliente.login}`);
    resumen.usuariosReutilizados += 1;
    return data.id;
  } catch (error) {
    if (error.response?.status !== 404) throw error;
  }

  await api.post('/admin/users', {
    login: cliente.login,
    password: PASSWORD_CLIENTE,
    email: cliente.email,
    firstName: cliente.firstName,
    lastName: cliente.lastName,
    activated: true,
    authorities: ['ROLE_USER', 'ROLE_CLIENTE'],
  });
  const { data } = await api.get(`/admin/users/${cliente.login}`);
  resumen.usuariosCreados += 1;
  console.log(`  Usuario creado: ${cliente.login} (${data.id})`);
  return data.id;
}

async function asegurarCuenta(cliente, userId, tipoDocumento) {
  const cuentas = await listar('cuentas');
  const existente = cuentas.find(c => c.user?.login === cliente.login || c.user?.id === userId);
  if (existente) {
    resumen.cuentasReutilizadas += 1;
    return existente;
  }

  const { data } = await api.post('/cuentas', {
    ...cliente.cuenta,
    activo: true,
    user: { id: userId },
    tipoDocumento: { id: tipoDocumento.id },
  });
  resumen.cuentasCreadas += 1;
  console.log(`  Cuenta creada para ${cliente.login} (${data.id})`);
  return data;
}

async function asegurarDirecciones(cliente, cuenta) {
  const existentes = (await listar('direccions')).filter(d => d.cuenta?.id === cuenta.id);
  const direcciones = [];

  for (const spec of cliente.direcciones) {
    const encontrada = existentes.find(d => d.direccion === spec.direccion);
    if (encontrada) {
      direcciones.push(encontrada);
      resumen.direccionesReutilizadas += 1;
      continue;
    }
    const { data } = await api.post('/direccions', {
      direccion: spec.direccion,
      barrio: spec.barrio,
      localidad: spec.localidad,
      municipio: 'Bogota',
      departamento: 'Cundinamarca',
      activo: true,
      telefonoContacto: cliente.cuenta.celular,
      destinatario: `${cliente.cuenta.primerNombre} ${cliente.cuenta.primerApellido}`,
      codigoPostal: '110231',
      cuenta: { id: cuenta.id },
    });
    direcciones.push(data);
    resumen.direccionesCreadas += 1;
    console.log(`  Dirección creada para ${cliente.login}: ${spec.direccion}`);
  }

  if (direcciones.length > 0) {
    await api.patch(`/direccions/${direcciones[0].id}/predeterminada`);
    resumen.predeterminadas += 1;
  }
  return direcciones;
}

/** Productos activos con precio y stock > 0 (el checkout valida stock server-side). */
async function cargarProductos() {
  let productos;
  try {
    productos = await listar('productos');
  } catch (error) {
    // Workaround (prod): GET /api/productos devuelve 500 si el catálogo contiene un
    // producto inactivo con datos corruptos; /productos/search devuelve solo los
    // activos, que es exactamente lo que este filtro necesita.
    console.warn(`  ⚠ Listado de productos falló (${mensajeError(error)}): uso /productos/search (solo activos)`);
    productos = await listar('productos/search', { q: '' });
  }
  return productos.filter(p => p.activo !== false && p.precio && (p.inventario?.stock ?? 0) > 0);
}

/** Items rotativos con stock controlado en memoria (nunca pide más de lo disponible). */
function escogerItems(productos, stockDisponible, cantidadItems, offset) {
  const items = [];
  for (let i = 0; items.length < cantidadItems && i < productos.length; i += 1) {
    const producto = productos[(offset + i) % productos.length];
    const disponible = stockDisponible.get(producto.id) ?? 0;
    if (disponible < 1) continue;
    const cantidad = Math.min(disponible, 1 + ((offset + i) % 2));
    items.push({ productoId: producto.id, cantidad });
    stockDisponible.set(producto.id, disponible - cantidad);
  }
  return items;
}

async function asegurarPedido(cliente, spec, direccion, productos, stockDisponible, indiceGlobal) {
  const marca = marcaDe(cliente, spec.slot);
  const existente = buscarPorMarca(marca);

  if (existente && !FORCE) {
    resumen.pedidosReutilizados += 1;
    return existente;
  }

  const apiCliente = await apiDeCliente(cliente);
  const items = escogerItems(productos, stockDisponible, 1 + (indiceGlobal % 2), indiceGlobal * 3);
  if (items.length === 0) throw new Error('sin stock disponible para armar el pedido');

  const { data } = await apiCliente.post('/pedidos/checkout', {
    direccionId: direccion.id,
    metodoPago: METODOS_PAGO[indiceGlobal % METODOS_PAGO.length],
    tipoServicioEnvio: TIPOS_ENVIO[indiceGlobal % TIPOS_ENVIO.length],
    notasCliente: marca,
    items,
  });
  resumen.pedidosCreados += 1;
  console.log(`  Pedido creado: ${data.pedido.numeroPedido} (${data.pedido.estado})`);

  const pedido = { ...data.pedido, notasCliente: marca };
  pedidosSeed.push(pedido);
  return pedido;
}

/** Pasos a ejecutar para llegar de `actual` a `objetivo`; null si la máquina de estados lo impide. */
function pasosHacia(actual, objetivo) {
  if (actual === objetivo) return [];
  if (objetivo === 'CANCELLED') {
    return ['PENDING', 'CONFIRMED', 'PROCESSING'].includes(actual) ? ['CANCELLED'] : null;
  }
  const camino = CAMINOS[objetivo];
  if (!camino) return null;
  const indice = camino.indexOf(actual);
  return indice === -1 ? null : camino.slice(indice + 1);
}

async function aplicarEstadoPedido(pedido, objetivo) {
  const pasos = pasosHacia(pedido.estado, objetivo);
  if (pasos === null) {
    console.warn(`  ⚠ ${pedido.numeroPedido}: ya en ${pedido.estado}, no se mueve a ${objetivo} (se omite)`);
    resumen.estadosNoAplicados += 1;
    return;
  }
  for (const estado of pasos) {
    try {
      if (estado === 'CANCELLED') {
        // Cancelación de administración: sin ventana de tiempo y restaura stock.
        const { data } = await api.post(`/pedidos/${pedido.id}/cancelar`, { motivo: MOTIVO_CANCELACION });
        pedido.estado = data.estado;
        console.log(`  Pedido ${pedido.numeroPedido} cancelado: ${MOTIVO_CANCELACION}`);
      } else {
        const { data } = await api.patch(`/pedidos/${pedido.id}/estado`, { estado });
        pedido.estado = data.estado;
      }
    } catch (error) {
      console.warn(`  ⚠ ${pedido.numeroPedido}: no se pudo pasar a ${estado}: ${mensajeError(error)}`);
      resumen.estadosNoAplicados += 1;
      return;
    }
  }
}

/**
 * El envío del pedido avanza en conjunto con el estado del pedido (el PATCH de envío
 * NO mueve el pedido, así que aquí se coordinan ambos para dejarlos coherentes).
 */
async function asegurarEnvio(pedido, objetivo, indiceGlobal) {
  if (objetivo !== 'SHIPPED' && objetivo !== 'DELIVERED') return;

  const envios = await listar('envios');
  const envio = envios.find(e => e.pedido?.id === pedido.id);
  if (!envio) {
    console.warn(`  ⚠ Sin envío para el pedido ${pedido.numeroPedido}`);
    return;
  }

  if (!envio.numeroRastreo) {
    await api.patch(`/envios/${envio.id}/tracking`, {
      transportadora: 'Servientrega',
      numeroRastreo: `SEED-${String(indiceGlobal + 1).padStart(4, '0')}`,
    });
    resumen.trackingAsignados += 1;
    console.log(`  Tracking asignado al envío del pedido ${pedido.numeroPedido}: SEED-${String(indiceGlobal + 1).padStart(4, '0')}`);
  }

  const meta = objetivo === 'SHIPPED' ? ORDEN_ENVIO.IN_TRANSIT : ORDEN_ENVIO.DELIVERED;
  const actual = ORDEN_ENVIO[envio.estado] ?? 0;
  for (const paso of PASOS_ENVIO) {
    const ordenPaso = ORDEN_ENVIO[paso];
    if (ordenPaso <= actual || ordenPaso > meta) continue;
    try {
      const { data } = await api.patch(`/envios/${envio.id}/estado`, { estado: paso });
      envio.estado = data.estado;
    } catch (error) {
      console.warn(`  ⚠ Envío del pedido ${pedido.numeroPedido}: no se pudo pasar a ${paso}: ${mensajeError(error)}`);
      return;
    }
  }
}

function agruparPorEstado(lista) {
  return lista.reduce((acc, item) => {
    const estado = item.estado ?? 'SIN_ESTADO';
    acc[estado] = (acc[estado] ?? 0) + 1;
    return acc;
  }, {});
}

function imprimirResumenLocal() {
  const porEstado = agruparPorEstado(pedidosSeed);
  const orden = ['PENDING', ...PATRON_ESTADOS, 'RETURNED'];
  const detalle = orden
    .filter(estado => porEstado[estado])
    .map(estado => `${estado}: ${porEstado[estado]}`)
    .join(' · ');

  console.log('\n=== Resumen seed-operaciones ===');
  console.log(`Usuarios:      ${resumen.usuariosCreados} creados · ${resumen.usuariosReutilizados} reutilizados`);
  console.log(`Cuentas:       ${resumen.cuentasCreadas} creadas · ${resumen.cuentasReutilizadas} reutilizadas`);
  console.log(
    `Direcciones:   ${resumen.direccionesCreadas} creadas · ${resumen.direccionesReutilizadas} reutilizadas · ${resumen.predeterminadas} predeterminadas aplicadas`,
  );
  console.log(
    `Pedidos:       ${resumen.pedidosCreados} creados · ${resumen.pedidosReutilizados} reutilizados · ${resumen.pedidosSinCrear} sin crear · ${resumen.estadosNoAplicados} estados sin mover`,
  );
  console.log(`  por estado:  ${detalle || 'sin pedidos del seed'}`);
  console.log(`Tracking:      ${resumen.trackingAsignados} asignados`);

  if (resumen.sinProductos) {
    console.log('\n⚠ No hay productos activos con stock: no se crearon pedidos, pagos, envíos ni facturas.');
    console.log('  Corre primero el seed de catálogo (scripts/seed-contenido.js o seed-catalogo-real.js).');
    return null;
  }
  if (resumen.clientesSinSesion.length > 0) {
    console.log(`\n⚠ Sin sesión para hacer checkout: ${[...new Set(resumen.clientesSinSesion)].join(', ')}`);
  }
  return Promise.all([listar('pagos'), listar('envios'), listar('facturas')]);
}

async function imprimirResumenGlobal() {
  const detalleLocal = imprimirResumenLocal();
  if (!detalleLocal) return;
  const [pagos, envios, facturas] = await detalleLocal;

  const idsPedidos = new Set(pedidosSeed.map(p => p.id));
  const pagosSeed = pagos.filter(p => idsPedidos.has(p.pedido?.id));
  const idsPagos = new Set(pagosSeed.map(p => p.id));
  const enviosSeed = envios.filter(e => idsPedidos.has(e.pedido?.id));
  const facturasSeed = facturas.filter(f => idsPagos.has(f.pago?.id));

  console.log(
    `Pagos:         ${pagosSeed.length} del seed (total en instancia: ${pagos.length}) · ${JSON.stringify(agruparPorEstado(pagosSeed))}`,
  );
  console.log(
    `Envíos:        ${enviosSeed.length} del seed (total en instancia: ${envios.length}) · ${JSON.stringify(agruparPorEstado(enviosSeed))}`,
  );
  console.log(`Facturas:      ${facturasSeed.length} del seed (total en instancia: ${facturas.length})`);
}

async function main() {
  await autenticarAdmin();
  const tipoDocumento = await asegurarTipoDocumento();

  // 1) Clientes: usuario + cuenta + direcciones
  const direccionesPorCliente = new Map();
  for (const cliente of CLIENTES) {
    console.log(`\nCliente ${cliente.login}`);
    const userId = await asegurarUsuario(cliente);
    const cuenta = await asegurarCuenta(cliente, userId, tipoDocumento);
    direccionesPorCliente.set(cliente.login, await asegurarDirecciones(cliente, cuenta));
  }

  // 2) Catálogo disponible para los items del checkout
  const productos = await cargarProductos();
  const stockDisponible = new Map(productos.map(p => [p.id, p.inventario.stock]));
  if (productos.length === 0) {
    resumen.sinProductos = true;
    await imprimirResumenGlobal();
    return;
  }

  // 3) Pedidos del seed existentes (idempotencia por la marca en notasCliente)
  for (const pedido of await listar('pedidos')) {
    if ((pedido.notasCliente || '').startsWith(MARCA) && !buscarPorMarca(pedido.notasCliente)) pedidosSeed.push(pedido);
  }

  // 4) Checkout + estados + envíos, con patrón determinista por índice global
  let indiceGlobal = 0;
  for (const cliente of CLIENTES) {
    const direcciones = direccionesPorCliente.get(cliente.login);
    console.log(`\nPedidos de ${cliente.login}`);
    if (direcciones.length === 0) {
      console.warn(`  ⚠ ${cliente.login} sin dirección: se omiten sus pedidos`);
      resumen.pedidosSinCrear += cliente.pedidos.length;
      indiceGlobal += cliente.pedidos.length;
      continue;
    }
    for (const spec of cliente.pedidos) {
      const i = indiceGlobal;
      indiceGlobal += 1;
      let pedido;
      try {
        pedido = await asegurarPedido(cliente, spec, direcciones[0], productos, stockDisponible, i);
      } catch (error) {
        if (error.esSesionCliente) resumen.clientesSinSesion.push(cliente.login);
        console.warn(`  ⚠ Pedido s${spec.slot} no creado: ${mensajeError(error)}`);
        resumen.pedidosSinCrear += 1;
        continue;
      }
      await aplicarEstadoPedido(pedido, spec.objetivo);
      await asegurarEnvio(pedido, spec.objetivo, i);
    }
  }

  await imprimirResumenGlobal();
}

main().catch(error => {
  if (error.code === 'ECONNREFUSED' || error.code === 'ENOTFOUND' || error.code === 'ECONNABORTED') {
    console.error(`❌ No se pudo conectar a ${BASE_URL}: el backend no está respondiendo.`);
    console.error('   Levanta el backend (./npmw run backend:start) y vuelve a ejecutar el script: es idempotente.');
  } else {
    console.error('❌ El seed de operaciones falló:', mensajeError(error));
    console.error('   Puedes re-ejecutarlo: es idempotente y no duplica usuarios, cuentas, direcciones ni pedidos marcados.');
  }
  process.exitCode = 1;
});

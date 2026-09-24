/**
 * Seed de catálogo real para KN-Store (tienda de calzado).
 *
 * Carga marcas, categorías, subcategorías, productos con variantes de color y talla,
 * precios, inventario e imágenes reales. Es idempotente: los productos se identifican
 * por slug y se actualizan si ya existen.
 *
 * Uso:
 *   node scripts/seed-catalogo-real.js [BASE_URL]
 *
 * Ejemplos:
 *   node scripts/seed-catalogo-real.js
 *   node scripts/seed-catalogo-real.js http://localhost:8080
 *   node scripts/seed-catalogo-real.js https://app.knstore.duckdns.org
 *
 * Variables de entorno:
 *   - KNSTORE_BASE_URL  (default: http://localhost:8080)
 *   - KNSTORE_USERNAME  (default: admin)
 *   - KNSTORE_PASSWORD  (default: admin)
 */

import axios from 'axios';

const BASE_URL = process.argv[2] || process.env.KNSTORE_BASE_URL || 'http://localhost:8080';
const USERNAME = process.env.KNSTORE_USERNAME || 'admin';
const PASSWORD = process.env.KNSTORE_PASSWORD || 'admin';

const api = axios.create({
  baseURL: `${BASE_URL}/api`,
  headers: { 'Content-Type': 'application/json' },
});

const CATEGORIAS = [
  { nombre: 'Hombre', slug: 'hombre', tallas: ['38', '39', '40', '41', '42', '43', '44'] },
  { nombre: 'Mujer', slug: 'mujer', tallas: ['35', '36', '37', '38', '39', '40'] },
  { nombre: 'Niño', slug: 'nino', tallas: ['28', '29', '30', '31', '32', '33', '34'] },
  { nombre: 'Unisex', slug: 'unisex', tallas: ['36', '37', '38', '39', '40', '41', '42', '43', '44'] },
];

const MARCAS = [
  'Nike',
  'Adidas',
  'Puma',
  'Reebok',
  'New Balance',
  'Vans',
  'Converse',
  'Asics',
  'Fila',
  'Under Armour',
  'Jordan',
  'Skechers',
];

const DESCRIPCION_POR_SUBCATEGORIA = {
  Running: 'Amortiguación y ligereza para tus carreras diarias.',
  Casual: 'Estilo urbano y comodidad para el día a día.',
  Fútbol: 'Tracción y control para dominar la cancha.',
  Baloncesto: 'Soporte, agarre y estabilidad para la duela.',
  Skate: 'Durabilidad y adherencia para el skate.',
  Botas: 'Resistencia y confort para cualquier terreno.',
  Sandalias: 'Frescura y comodidad para el clima cálido.',
};

const MODELOS = [
  {
    marca: 'Nike',
    categoria: 'Hombre',
    sub: 'Running',
    nombre: 'Air Zoom Pegasus 41',
    ref: 'NK-AZP41',
    venta: 479900,
    colores: ['Negro', 'Azul', 'Gris'],
    destacado: true,
  },
  {
    marca: 'Nike',
    categoria: 'Unisex',
    sub: 'Casual',
    nombre: 'Air Force 1 07',
    ref: 'NK-AF107',
    venta: 449900,
    colores: ['Blanco', 'Negro'],
    destacado: true,
  },
  {
    marca: 'Nike',
    categoria: 'Hombre',
    sub: 'Casual',
    nombre: 'Air Max 270',
    ref: 'NK-AM270',
    venta: 549900,
    colores: ['Negro', 'Rojo'],
    destacado: true,
  },
  { marca: 'Nike', categoria: 'Unisex', sub: 'Casual', nombre: 'Dunk Low', ref: 'NK-DUNKL', venta: 499900, colores: ['Blanco', 'Verde'] },
  { marca: 'Nike', categoria: 'Niño', sub: 'Running', nombre: 'Revolution 7', ref: 'NK-REV7', venta: 249900, colores: ['Negro', 'Azul'] },
  {
    marca: 'Nike',
    categoria: 'Hombre',
    sub: 'Fútbol',
    nombre: 'Mercurial Superfly 10',
    ref: 'NK-MSF10',
    venta: 899900,
    colores: ['Azul', 'Rojo'],
    destacado: true,
  },
  {
    marca: 'Nike',
    categoria: 'Mujer',
    sub: 'Running',
    nombre: 'Air Zoom Pegasus 41 W',
    ref: 'NK-AZP41W',
    venta: 469900,
    colores: ['Rosa', 'Blanco'],
  },
  {
    marca: 'Nike',
    categoria: 'Mujer',
    sub: 'Casual',
    nombre: 'Air Max SC W',
    ref: 'NK-AMSCW',
    venta: 379900,
    colores: ['Blanco', 'Beige'],
  },

  {
    marca: 'Adidas',
    categoria: 'Hombre',
    sub: 'Running',
    nombre: 'Ultraboost 22',
    ref: 'AD-UB22',
    venta: 649900,
    colores: ['Negro', 'Gris'],
    destacado: true,
  },
  {
    marca: 'Adidas',
    categoria: 'Unisex',
    sub: 'Casual',
    nombre: 'Samba OG',
    ref: 'AD-SAMBA',
    venta: 399900,
    colores: ['Negro', 'Blanco'],
    destacado: true,
  },
  {
    marca: 'Adidas',
    categoria: 'Unisex',
    sub: 'Casual',
    nombre: 'Superstar',
    ref: 'AD-SUPER',
    venta: 349900,
    colores: ['Blanco', 'Negro'],
  },
  {
    marca: 'Adidas',
    categoria: 'Mujer',
    sub: 'Casual',
    nombre: 'Grand Court W',
    ref: 'AD-GCW',
    venta: 249900,
    colores: ['Blanco', 'Rosa'],
  },
  { marca: 'Adidas', categoria: 'Niño', sub: 'Running', nombre: 'Runfalcon 5', ref: 'AD-RF5', venta: 219900, colores: ['Azul', 'Negro'] },
  {
    marca: 'Adidas',
    categoria: 'Hombre',
    sub: 'Fútbol',
    nombre: 'Predator League FG',
    ref: 'AD-PRL',
    venta: 349900,
    colores: ['Negro', 'Verde'],
  },
  {
    marca: 'Adidas',
    categoria: 'Hombre',
    sub: 'Running',
    nombre: 'Adizero SL2',
    ref: 'AD-AZSL2',
    venta: 549900,
    colores: ['Blanco', 'Azul'],
  },

  { marca: 'Puma', categoria: 'Hombre', sub: 'Casual', nombre: 'RS-X Efekt', ref: 'PM-RSX', venta: 379900, colores: ['Negro', 'Gris'] },
  {
    marca: 'Puma',
    categoria: 'Unisex',
    sub: 'Casual',
    nombre: 'Suede Classic XXI',
    ref: 'PM-SUEDE',
    venta: 299900,
    colores: ['Negro', 'Rojo'],
    destacado: true,
  },
  { marca: 'Puma', categoria: 'Niño', sub: 'Casual', nombre: 'Smash V2', ref: 'PM-SMASH', venta: 219900, colores: ['Blanco', 'Azul'] },
  {
    marca: 'Puma',
    categoria: 'Mujer',
    sub: 'Running',
    nombre: 'Velocity Nitro 3 W',
    ref: 'PM-VN3W',
    venta: 449900,
    colores: ['Rosa', 'Negro'],
  },
  {
    marca: 'Puma',
    categoria: 'Hombre',
    sub: 'Fútbol',
    nombre: 'Future 7 Play',
    ref: 'PM-FUT7',
    venta: 329900,
    colores: ['Azul', 'Blanco'],
  },
  { marca: 'Puma', categoria: 'Mujer', sub: 'Casual', nombre: 'Carina 2.0', ref: 'PM-CAR2', venta: 249900, colores: ['Blanco', 'Beige'] },

  {
    marca: 'Reebok',
    categoria: 'Unisex',
    sub: 'Casual',
    nombre: 'Classic Leather',
    ref: 'RB-CLASS',
    venta: 329900,
    colores: ['Blanco', 'Negro'],
  },
  { marca: 'Reebok', categoria: 'Unisex', sub: 'Casual', nombre: 'Club C 85', ref: 'RB-CC85', venta: 309900, colores: ['Blanco', 'Verde'] },
  { marca: 'Reebok', categoria: 'Hombre', sub: 'Running', nombre: 'Nano X4', ref: 'RB-NANOX4', venta: 499900, colores: ['Negro', 'Gris'] },
  { marca: 'Reebok', categoria: 'Niño', sub: 'Running', nombre: 'Energen Lite', ref: 'RB-ENL', venta: 229900, colores: ['Azul', 'Negro'] },

  {
    marca: 'New Balance',
    categoria: 'Unisex',
    sub: 'Casual',
    nombre: '574 Core',
    ref: 'NB-574',
    venta: 379900,
    colores: ['Gris', 'Azul Marino'],
    destacado: true,
  },
  { marca: 'New Balance', categoria: 'Hombre', sub: 'Casual', nombre: '9060', ref: 'NB-9060', venta: 649900, colores: ['Gris', 'Beige'] },
  {
    marca: 'New Balance',
    categoria: 'Hombre',
    sub: 'Running',
    nombre: 'Fresh Foam 1080 v13',
    ref: 'NB-FF1080',
    venta: 799900,
    colores: ['Azul', 'Negro'],
  },
  { marca: 'New Balance', categoria: 'Mujer', sub: 'Casual', nombre: '327 W', ref: 'NB-327W', venta: 399900, colores: ['Blanco', 'Rosa'] },

  {
    marca: 'Vans',
    categoria: 'Unisex',
    sub: 'Skate',
    nombre: 'Old Skool',
    ref: 'VN-OLDSK',
    venta: 329900,
    colores: ['Negro', 'Azul Marino'],
    destacado: true,
  },
  { marca: 'Vans', categoria: 'Unisex', sub: 'Skate', nombre: 'Knu Skool', ref: 'VN-KNUSK', venta: 379900, colores: ['Negro', 'Marrón'] },
  { marca: 'Vans', categoria: 'Unisex', sub: 'Skate', nombre: 'Sk8-Hi', ref: 'VN-SK8HI', venta: 359900, colores: ['Negro', 'Rojo'] },
  { marca: 'Vans', categoria: 'Niño', sub: 'Casual', nombre: 'Slip-On', ref: 'VN-SLIP', venta: 259900, colores: ['Negro', 'Verde'] },

  {
    marca: 'Converse',
    categoria: 'Unisex',
    sub: 'Casual',
    nombre: 'Chuck Taylor All Star High',
    ref: 'CV-CTA',
    venta: 289900,
    colores: ['Negro', 'Blanco'],
    destacado: true,
  },
  {
    marca: 'Converse',
    categoria: 'Mujer',
    sub: 'Casual',
    nombre: 'Run Star Hike',
    ref: 'CV-RSH',
    venta: 449900,
    colores: ['Blanco', 'Negro'],
  },
  { marca: 'Converse', categoria: 'Unisex', sub: 'Casual', nombre: 'Chuck 70', ref: 'CV-C70', venta: 359900, colores: ['Beige', 'Verde'] },

  {
    marca: 'Asics',
    categoria: 'Hombre',
    sub: 'Running',
    nombre: 'Gel-Kayano 31',
    ref: 'AS-GK31',
    venta: 849900,
    colores: ['Azul', 'Negro'],
    destacado: true,
  },
  {
    marca: 'Asics',
    categoria: 'Hombre',
    sub: 'Running',
    nombre: 'Gel-Nimbus 26',
    ref: 'AS-GN26',
    venta: 899900,
    colores: ['Blanco', 'Azul'],
  },
  {
    marca: 'Asics',
    categoria: 'Unisex',
    sub: 'Running',
    nombre: 'Novablast 5',
    ref: 'AS-NOVA5',
    venta: 649900,
    colores: ['Negro', 'Verde'],
  },
  { marca: 'Asics', categoria: 'Mujer', sub: 'Casual', nombre: 'Japan S W', ref: 'AS-JPSW', venta: 299900, colores: ['Blanco', 'Rosa'] },

  { marca: 'Fila', categoria: 'Mujer', sub: 'Casual', nombre: 'Disruptor 2', ref: 'FL-DIS2', venta: 329900, colores: ['Blanco', 'Rosa'] },
  { marca: 'Fila', categoria: 'Unisex', sub: 'Casual', nombre: 'Ray Tracer', ref: 'FL-RAYT', venta: 289900, colores: ['Negro', 'Gris'] },
  {
    marca: 'Fila',
    categoria: 'Hombre',
    sub: 'Running',
    nombre: 'Double Bounce',
    ref: 'FL-DBLB',
    venta: 259900,
    colores: ['Azul', 'Negro'],
  },

  {
    marca: 'Under Armour',
    categoria: 'Hombre',
    sub: 'Running',
    nombre: 'Charged Assert 10',
    ref: 'UA-CA10',
    venta: 279900,
    colores: ['Negro', 'Gris'],
  },
  {
    marca: 'Under Armour',
    categoria: 'Hombre',
    sub: 'Running',
    nombre: 'Hovr Infinite 5',
    ref: 'UA-HI5',
    venta: 549900,
    colores: ['Azul', 'Blanco'],
  },
  {
    marca: 'Under Armour',
    categoria: 'Niño',
    sub: 'Running',
    nombre: 'Surge 4',
    ref: 'UA-SURG4',
    venta: 229900,
    colores: ['Azul', 'Rojo'],
  },

  {
    marca: 'Jordan',
    categoria: 'Unisex',
    sub: 'Baloncesto',
    nombre: 'Air Jordan 1 Low',
    ref: 'JD-AJ1L',
    venta: 599900,
    colores: ['Negro', 'Rojo'],
    destacado: true,
  },
  {
    marca: 'Jordan',
    categoria: 'Hombre',
    sub: 'Baloncesto',
    nombre: 'Air Jordan 1 Mid',
    ref: 'JD-AJ1M',
    venta: 649900,
    colores: ['Blanco', 'Azul'],
  },
  {
    marca: 'Jordan',
    categoria: 'Hombre',
    sub: 'Baloncesto',
    nombre: 'Air Jordan 4 Retro',
    ref: 'JD-AJ4R',
    venta: 1199900,
    colores: ['Gris', 'Negro'],
    destacado: true,
  },

  {
    marca: 'Skechers',
    categoria: 'Mujer',
    sub: 'Casual',
    nombre: "D'Lites Fresh Start",
    ref: 'SK-DLFS',
    venta: 279900,
    colores: ['Blanco', 'Rosa'],
  },
  {
    marca: 'Skechers',
    categoria: 'Hombre',
    sub: 'Running',
    nombre: 'Go Run Ride 11',
    ref: 'SK-GRR11',
    venta: 379900,
    colores: ['Negro', 'Azul'],
  },
  {
    marca: 'Skechers',
    categoria: 'Mujer',
    sub: 'Casual',
    nombre: 'Arch Fit Comfy Wave',
    ref: 'SK-AFCW',
    venta: 349900,
    colores: ['Beige', 'Negro'],
  },
];

const UNSPLASH_PHOTO_IDS = [
  '1542291026-7eec264c27ff',
  '1549298916-b41d501d3772',
  '1595950653106-6c9ebd614d3a',
  '1600185365483-26d7a4cc7519',
  '1560769629-975ec94e6a86',
  '1543508282-6319a3e2621f',
  '1525966222134-fcfa99b8ae77',
  '1606107557195-0e29a4b5b4aa',
  '1514989940723-e8e51635b782',
  '1595341888016-a392ef81b7de',
  '1608256246200-53e635b5b65f',
  '1600185365926-3a2ce3cdb9eb',
  '1547949003-9792a18a2601',
  '1560343090-f0409e92791a',
  '1544005313-94ddf0286df2',
  '1607522370275-f14206abe5d3',
  '1571731956672-f2b94d7dd0cb',
  '1596703263926-eb0762ee17e4',
  '1583394838336-acd977736f90',
  '1600269452121-4f2416e55c28',
  '1608231387042-66d1773070a5',
];

function slugify(text) {
  return text
    .toString()
    .normalize('NFD')
    .replace(/[\u0300-\u036f]/g, '')
    .toLowerCase()
    .replace(/[^a-z0-9]+/g, '-')
    .replace(/(^-|-$)/g, '');
}

function randomInt(min, max) {
  return Math.floor(Math.random() * (max - min + 1)) + min;
}

function hash(text) {
  let value = 0;
  for (const char of text) {
    value = (value * 31 + char.charCodeAt(0)) % 1000000000;
  }
  return value;
}

function normalizarPrecio(valor) {
  return Math.round(valor / 100) * 100;
}

function getItems(response) {
  const data = response.data;
  return Array.isArray(data) ? data : data?.content || [];
}

async function findOne(path, predicate) {
  try {
    const items = getItems(await api.get(path, { params: { size: 2000 } }));
    return items.find(predicate) || null;
  } catch (error) {
    console.warn(`AVISO: no se pudo consultar ${path}: ${error.response?.data?.message || error.message}`);
    return null;
  }
}

async function findBySlug(path, slug) {
  try {
    const response = await api.get(`${path}/slug/${slug}`);
    return response.data || null;
  } catch (error) {
    if (error.response?.status === 404) return null;
    throw error;
  }
}

async function create(path, body) {
  const payload = { ...body };
  delete payload.id;
  const response = await api.post(path, payload);
  return response.data;
}

async function update(path, body) {
  if (!body.id) throw new Error(`No se puede actualizar ${path} sin id`);
  const response = await api.put(`${path}/${body.id}`, body);
  return response.data;
}

async function authenticate() {
  const response = await api.post('/authenticate', {
    username: USERNAME,
    password: PASSWORD,
    rememberMe: false,
  });
  api.defaults.headers.common.Authorization = `Bearer ${response.data.id_token}`;
  console.log(`Autenticado como ${USERNAME}`);
}

async function asegurarCategoriaIva() {
  const existente = await findOne('/categoria-ivas', c => c.nombre === 'IVA 19%');
  if (existente?.id) return existente;
  return create('/categoria-ivas', { nombre: 'IVA 19%', porcentaje: '19', estado: 'ACTIVO' });
}

async function asegurarMarcas() {
  const resultado = {};
  for (const nombre of MARCAS) {
    const slug = slugify(nombre);
    const existente = await findOne('/marcas', m => m.slug === slug);
    resultado[nombre] = existente?.id ? existente : await create('/marcas', { nombre, slug });
  }
  console.log(`Marcas listas: ${MARCAS.length}`);
  return resultado;
}

async function asegurarCategorias() {
  const resultado = {};
  for (const categoria of CATEGORIAS) {
    const existente = await findOne('/categorias', c => c.slug === categoria.slug);
    const body = {
      nombre: categoria.nombre,
      slug: categoria.slug,
      descripcion: `Calzado para ${categoria.nombre.toLowerCase()}`,
      activo: true,
    };
    resultado[categoria.nombre] = {
      ...categoria,
      ...(existente?.id ? await update('/categorias', { ...body, id: existente.id }) : await create('/categorias', body)),
    };
  }
  console.log(`Categorías listas: ${CATEGORIAS.length}`);
  return resultado;
}

async function asegurarSubcategorias(categorias) {
  const resultado = {};
  for (const modelo of MODELOS) {
    const categoria = categorias[modelo.categoria];
    const slugSub = `${slugify(modelo.sub)}-${categoria.slug}`;
    const clave = `${modelo.sub}|${modelo.categoria}`;
    if (resultado[clave]) continue;
    const existente = await findOne('/subcategorias', s => s.slug === slugSub);
    const body = {
      nombre: `${modelo.sub} ${modelo.categoria}`,
      slug: slugSub,
      descripcion: DESCRIPCION_POR_SUBCATEGORIA[modelo.sub] || `Calzado ${modelo.sub.toLowerCase()}`,
      activo: true,
      categoria: { id: categoria.id },
    };
    resultado[clave] = existente?.id ? await update('/subcategorias', { ...body, id: existente.id }) : await create('/subcategorias', body);
  }
  console.log(`Subcategorías listas: ${Object.keys(resultado).length}`);
  return resultado;
}

function construirProducto(modelo, categoria, subcategoria, marca, categoriaIva, color, talla) {
  const slug = `${slugify(modelo.marca)}-${slugify(modelo.nombre)}-${categoria.slug}-${slugify(color)}-${talla}`;
  const precioVenta = normalizarPrecio(modelo.venta);
  const precioCompra = normalizarPrecio(modelo.venta * 0.62);
  const garantiaMeses = modelo.sub === 'Running' || modelo.sub === 'Baloncesto' ? 12 : 6;
  const descripcion = `${modelo.nombre} de ${modelo.marca}. ${DESCRIPCION_POR_SUBCATEGORIA[modelo.sub] || ''} Color ${color.toLowerCase()}, talla ${talla}. Garantía de ${garantiaMeses} meses.`;
  const tallaCentral = categoria.tallas[Math.floor(categoria.tallas.length / 2)];
  return {
    slug,
    sku: `${modelo.ref}-${color.substring(0, 3).toUpperCase()}-${talla}`,
    referencia: modelo.ref,
    nombre: `${modelo.marca} ${modelo.nombre} ${categoria.nombre} ${color} Talla ${talla}`,
    color,
    talla,
    codigoBarras: `77${String(hash(slug)).padStart(10, '0')}`,
    unidadMedida: 'Par',
    descripcion,
    destacado: Boolean(modelo.destacado) && color === modelo.colores[0] && talla === tallaCentral,
    activo: true,
    precio: { precioCompra: precioCompra.toString(), precioVenta: precioVenta.toString(), precioAdicional: '0' },
    inventario: {
      stock: randomInt(8, 45),
      stockMinimo: 5,
      ubicacionBodega: 'BODEGA_PRINCIPAL',
      garantiaMeses,
    },
    relaciones: {
      categoria: { id: categoria.id },
      subcategoria: { id: subcategoria.id },
      marca: { id: marca.id },
      categoriaIva: { id: categoriaIva.id },
    },
  };
}

async function seed() {
  await authenticate();

  const categoriaIva = await asegurarCategoriaIva();
  const marcas = await asegurarMarcas();
  const categorias = await asegurarCategorias();
  const subcategorias = await asegurarSubcategorias(categorias);

  let creados = 0;
  let actualizados = 0;
  let imagenes = 0;
  let indiceModelo = 0;

  for (const modelo of MODELOS) {
    const categoria = categorias[modelo.categoria];
    const subcategoria = subcategorias[`${modelo.sub}|${modelo.categoria}`];
    const marca = marcas[modelo.marca];

    if (!categoria || !subcategoria || !marca) {
      console.warn(`AVISO: relaciones incompletas para ${modelo.nombre}, se omite`);
      indiceModelo++;
      continue;
    }

    for (const color of modelo.colores) {
      const fotoId = UNSPLASH_PHOTO_IDS[(indiceModelo * 3 + modelo.colores.indexOf(color)) % UNSPLASH_PHOTO_IDS.length];
      const imagenUrl = `https://images.unsplash.com/photo-${fotoId}?w=800&q=80&fm=jpg&fit=crop`;

      for (const talla of categoria.tallas) {
        const data = construirProducto(modelo, categoria, subcategoria, marca, categoriaIva, color, talla);
        const existente = await findBySlug('/productos', data.slug);

        if (existente?.id) {
          if (existente.precio?.id) {
            await update('/producto-precios', { ...data.precio, id: existente.precio.id, producto: { id: existente.id } });
          }
          if (existente.inventario?.id) {
            await update('/producto-inventarios', { ...data.inventario, id: existente.inventario.id, producto: { id: existente.id } });
          }
          await update('/productos', {
            ...existente,
            ...data.relaciones,
            id: existente.id,
            nombre: data.nombre,
            slug: data.slug,
            referencia: data.referencia,
            sku: data.sku,
            color: data.color,
            talla: data.talla,
            codigoBarras: data.codigoBarras,
            unidadMedida: data.unidadMedida,
            descripcion: data.descripcion,
            destacado: data.destacado,
            activo: data.activo,
            precio: { id: existente.precio?.id },
            inventario: { id: existente.inventario?.id },
          });
          actualizados++;
          continue;
        }

        const precio = await create('/producto-precios', data.precio);
        const inventario = await create('/producto-inventarios', data.inventario);
        const producto = await create('/productos', {
          ...data.relaciones,
          nombre: data.nombre,
          slug: data.slug,
          referencia: data.referencia,
          sku: data.sku,
          color: data.color,
          talla: data.talla,
          codigoBarras: data.codigoBarras,
          unidadMedida: data.unidadMedida,
          descripcion: data.descripcion,
          destacado: data.destacado,
          activo: data.activo,
          precio: { id: precio.id },
          inventario: { id: inventario.id },
        });

        await update('/producto-precios', { ...data.precio, id: precio.id, producto: { id: producto.id } });

        await create('/producto-imagens', {
          imagenContentType: 'image/jpeg',
          imagenUrl,
          imagenAlt: `${modelo.marca} ${modelo.nombre} color ${color.toLowerCase()}`,
          esPrincipal: true,
          producto: { id: producto.id },
        });
        imagenes++;
        creados++;

        if ((creados + actualizados) % 50 === 0) {
          console.log(`Progreso: ${creados} creados, ${actualizados} actualizados...`);
        }
      }
    }
    indiceModelo++;
  }

  console.log('');
  console.log(`Seed completado: ${creados} productos creados, ${actualizados} actualizados, ${imagenes} imágenes nuevas.`);
  console.log(`Catálogo: ${BASE_URL}/productos`);
}

seed().catch(error => {
  console.error('Error insertando el catálogo real:', error.response?.data || error.message);
  process.exit(1);
});

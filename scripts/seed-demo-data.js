/**
 * Script de inserción de datos demo para knstore - tienda de zapatos.
 *
 * Uso:
 *   node scripts/seed-demo-data.js [BASE_URL]
 *
 * Ejemplos:
 *   node scripts/seed-demo-data.js
 *   node scripts/seed-demo-data.js http://localhost:8080
 *   node scripts/seed-demo-data.js https://app.knstore.freeddns.org
 *
 * Variables de entorno:
 *   - KNSTORE_USERNAME  (default: admin)
 *   - KNSTORE_PASSWORD  (default: admin)
 *   - KNSTORE_BASE_URL  (default: http://localhost:8080)
 */

import axios from 'axios';

const BASE_URL = process.argv[2] || process.env.KNSTORE_BASE_URL || 'http://localhost:8080';
const USERNAME = process.env.KNSTORE_USERNAME || 'admin';
const PASSWORD = process.env.KNSTORE_PASSWORD || 'admin';

const api = axios.create({
  baseURL: `${BASE_URL}/api`,
  headers: { 'Content-Type': 'application/json' },
});

async function authenticate() {
  const response = await api.post('/authenticate', {
    username: USERNAME,
    password: PASSWORD,
    rememberMe: true,
  });
  api.defaults.headers.common.Authorization = `Bearer ${response.data.id_token}`;
  console.log(`✅ Autenticado como ${USERNAME}`);
}

function getItems(response) {
  const data = response.data;
  return Array.isArray(data) ? data : data?.content || [];
}

async function findOne(path, predicate) {
  try {
    const items = getItems(await api.get(path));
    return items.find(predicate) || null;
  } catch (error) {
    console.warn(`⚠️ No se pudo consultar ${path}:`, error.response?.data?.message || error.message);
    return null;
  }
}

async function findBySlug(path, slug) {
  try {
    const response = await api.get(`${path}/slug/${slug}`);
    return response.data || null;
  } catch (error) {
    if (error.response?.status === 404) {
      return null;
    }
    throw error;
  }
}

async function create(path, body) {
  const payload = { ...body };
  delete payload.id;
  const response = await api.post(path, payload);
  console.log(`✅ Creado: ${path} -> ${response.data.id}`);
  return response.data;
}

async function update(path, body) {
  if (!body.id) {
    throw new Error(`No se puede actualizar ${path} sin id`);
  }
  const response = await api.put(`${path}/${body.id}`, body);
  console.log(`✅ Actualizado: ${path}/${body.id}`);
  return response.data;
}

async function createOrUpdate(path, body, finder) {
  const existing = await finder();
  if (existing?.id) {
    return update(path, { ...body, id: existing.id });
  }
  return create(path, body);
}

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

async function seed() {
  await authenticate();

  // Categoría IVA única para todos los zapatos (19 %)
  const categoriaIva = await createOrUpdate('/categoria-ivas', { nombre: 'IVA 19%', porcentaje: '19', estado: 'ACTIVO' }, () =>
    findOne('/categoria-ivas', c => c.nombre === 'IVA 19%'),
  );

  // Marcas
  const marcasData = [
    { nombre: 'Nike', slug: 'nike', precioVenta: 320000, precioCompra: 180000 },
    { nombre: 'Adidas', slug: 'adidas', precioVenta: 280000, precioCompra: 150000 },
    { nombre: 'Puma', slug: 'puma', precioVenta: 240000, precioCompra: 130000 },
  ];

  const marcas = [];
  for (const m of marcasData) {
    const marca = await createOrUpdate('/marcas', { nombre: m.nombre, slug: m.slug }, () => findOne('/marcas', x => x.slug === m.slug));
    marcas.push({ ...marca, ...m });
  }

  // Categorías
  const categoriasData = [
    { nombre: 'Hombre', slug: 'hombre', tallas: ['38', '39', '40', '41', '42'] },
    { nombre: 'Mujer', slug: 'mujer', tallas: ['35', '36', '37', '38', '39'] },
    { nombre: 'Niño', slug: 'nino', tallas: ['28', '29', '30', '31', '32'] },
    { nombre: 'Unisex', slug: 'unisex', tallas: ['37', '38', '39', '40', '41'] },
  ];

  const categorias = [];
  for (const c of categoriasData) {
    const categoria = await createOrUpdate(
      '/categorias',
      { nombre: c.nombre, slug: c.slug, descripcion: `Calzado ${c.nombre.toLowerCase()}`, activo: true },
      () => findOne('/categorias', x => x.slug === c.slug),
    );
    categorias.push({ ...categoria, tallas: c.tallas });
  }

  // Subcategorías
  const nombresSubcategorias = ['Deportivo', 'Casual', 'Formal'];
  const subcategorias = [];
  for (const categoria of categorias) {
    for (const nombreSub of nombresSubcategorias) {
      const slugSub = `${slugify(nombreSub)}-${categoria.slug}`;
      const subcategoria = await createOrUpdate(
        '/subcategorias',
        {
          nombre: `${nombreSub} ${categoria.nombre}`,
          slug: slugSub,
          descripcion: `Zapatos ${nombreSub.toLowerCase()} para ${categoria.nombre.toLowerCase()}`,
          activo: true,
          categoria: { id: categoria.id },
        },
        () => findOne('/subcategorias', x => x.slug === slugSub),
      );
      subcategorias.push({ ...subcategoria, categoria });
    }
  }

  // Modelos por marca / categoría / subcategoría
  const modelos = [
    // Nike
    { marca: 'Nike', categoria: 'Hombre', subcategoria: 'Deportivo', nombre: 'Air Max', ref: 'NIKE-AM' },
    { marca: 'Nike', categoria: 'Hombre', subcategoria: 'Casual', nombre: 'Court Vision', ref: 'NIKE-CV' },
    { marca: 'Nike', categoria: 'Hombre', subcategoria: 'Formal', nombre: 'Oxford Classic', ref: 'NIKE-OC' },
    { marca: 'Nike', categoria: 'Mujer', subcategoria: 'Deportivo', nombre: 'Air Zoom', ref: 'NIKE-AZ' },
    { marca: 'Nike', categoria: 'Mujer', subcategoria: 'Casual', nombre: 'Tanjun', ref: 'NIKE-TJ' },
    { marca: 'Nike', categoria: 'Mujer', subcategoria: 'Formal', nombre: 'Ballet Flat', ref: 'NIKE-BF' },
    { marca: 'Nike', categoria: 'Niño', subcategoria: 'Deportivo', nombre: 'Star Runner', ref: 'NIKE-SR' },
    { marca: 'Nike', categoria: 'Niño', subcategoria: 'Casual', nombre: 'Pico 5', ref: 'NIKE-P5' },
    { marca: 'Nike', categoria: 'Unisex', subcategoria: 'Deportivo', nombre: 'Revolution', ref: 'NIKE-RV' },
    { marca: 'Nike', categoria: 'Unisex', subcategoria: 'Casual', nombre: 'SB Check', ref: 'NIKE-SB' },

    // Adidas
    { marca: 'Adidas', categoria: 'Hombre', subcategoria: 'Deportivo', nombre: 'Ultraboost', ref: 'ADI-UB' },
    { marca: 'Adidas', categoria: 'Hombre', subcategoria: 'Casual', nombre: 'Grand Court', ref: 'ADI-GC' },
    { marca: 'Adidas', categoria: 'Hombre', subcategoria: 'Formal', nombre: 'Duramo Dress', ref: 'ADI-DD' },
    { marca: 'Adidas', categoria: 'Mujer', subcategoria: 'Deportivo', nombre: 'Galaxy', ref: 'ADI-GL' },
    { marca: 'Adidas', categoria: 'Mujer', subcategoria: 'Casual', nombre: 'Advantage', ref: 'ADI-AV' },
    { marca: 'Adidas', categoria: 'Mujer', subcategoria: 'Formal', nombre: 'Court Silk', ref: 'ADI-CS' },
    { marca: 'Adidas', categoria: 'Niño', subcategoria: 'Deportivo', nombre: 'Runfalcon', ref: 'ADI-RF' },
    { marca: 'Adidas', categoria: 'Niño', subcategoria: 'Casual', nombre: 'Tensaur', ref: 'ADI-TS' },
    { marca: 'Adidas', categoria: 'Unisex', subcategoria: 'Deportivo', nombre: 'Lite Racer', ref: 'ADI-LR' },
    { marca: 'Adidas', categoria: 'Unisex', subcategoria: 'Casual', nombre: 'VL Court', ref: 'ADI-VC' },

    // Puma
    { marca: 'Puma', categoria: 'Hombre', subcategoria: 'Deportivo', nombre: 'RS-X', ref: 'PMA-RS' },
    { marca: 'Puma', categoria: 'Hombre', subcategoria: 'Casual', nombre: 'Smash V2', ref: 'PMA-SV' },
    { marca: 'Puma', categoria: 'Hombre', subcategoria: 'Formal', nombre: 'Dress Soft', ref: 'PMA-DS' },
    { marca: 'Puma', categoria: 'Mujer', subcategoria: 'Deportivo', nombre: 'Deviate', ref: 'PMA-DV' },
    { marca: 'Puma', categoria: 'Mujer', subcategoria: 'Casual', nombre: 'Carina', ref: 'PMA-CA' },
    { marca: 'Puma', categoria: 'Mujer', subcategoria: 'Formal', nombre: 'Ella Loafer', ref: 'PMA-EL' },
    { marca: 'Puma', categoria: 'Niño', subcategoria: 'Deportivo', nombre: 'Future Rider', ref: 'PMA-FR' },
    { marca: 'Puma', categoria: 'Niño', subcategoria: 'Casual', nombre: 'Puma Fun', ref: 'PMA-PF' },
    { marca: 'Puma', categoria: 'Unisex', subcategoria: 'Deportivo', nombre: 'Speedcat', ref: 'PMA-SC' },
    { marca: 'Puma', categoria: 'Unisex', subcategoria: 'Casual', nombre: 'Suede Classic', ref: 'PMA-SC2' },
  ];

  const colores = ['Negro', 'Blanco', 'Azul', 'Gris'];
  let creados = 0;
  let actualizados = 0;

  for (const modelo of modelos) {
    const marca = marcas.find(m => m.nombre === modelo.marca);
    const categoria = categorias.find(c => c.nombre === modelo.categoria);
    const subcategoria = subcategorias.find(s => s.categoria.id === categoria.id && s.nombre.startsWith(modelo.subcategoria));

    if (!marca || !categoria || !subcategoria) {
      console.warn(`⚠️ No se encontró relación para modelo ${modelo.nombre}`);
      continue;
    }

    for (const color of colores) {
      for (const talla of categoria.tallas) {
        const slug = `${marca.slug}-${slugify(modelo.nombre)}-${categoria.slug}-${slugify(color)}-${talla}`;
        const sku = `${modelo.ref}-${categoria.nombre.charAt(0)}-${slugify(color).substring(0, 3).toUpperCase()}-${talla}`;
        const referencia = `${modelo.ref}-${categoria.nombre.charAt(0)}`;
        const nombre = `${modelo.nombre} ${modelo.marca} ${categoria.nombre} - ${color} Talla ${talla}`;
        const precioVenta = marca.precioVenta + (modelo.subcategoria === 'Formal' ? 50000 : 0);
        const precioCompra = marca.precioCompra + (modelo.subcategoria === 'Formal' ? 30000 : 0);
        const stock = randomInt(5, 25);

        const existing = await findBySlug('/productos', slug);

        if (existing?.id) {
          await update('/productos', {
            ...existing,
            nombre,
            slug,
            referencia,
            sku,
            color,
            talla,
            descripcion: `${nombre}. Calzado ${modelo.subcategoria.toLowerCase()} de la marca ${modelo.marca}.`,
            destacado: modelo.subcategoria === 'Deportivo' && color === 'Negro',
            activo: true,
            categoria: { id: categoria.id },
            subcategoria: { id: subcategoria.id },
            marca: { id: marca.id },
            categoriaIva: { id: categoriaIva.id },
          });
          actualizados++;
          continue;
        }

        // Precio
        const precio = await create('/producto-precios', {
          precioCompra: precioCompra.toString(),
          precioVenta: precioVenta.toString(),
          precioAdicional: '0',
          ganancia: (precioVenta - precioCompra).toString(),
        });

        // Inventario
        const inventario = await create('/producto-inventarios', {
          stock,
          stockMinimo: 3,
          ubicacionBodega: 'BODEGA_PRINCIPAL',
          garantiaMeses: 6,
        });

        // Producto
        const producto = await create('/productos', {
          nombre,
          slug,
          referencia,
          sku,
          color,
          talla,
          codigoBarras: `77${randomInt(100000000, 999999999)}`,
          unidadMedida: 'Par',
          descripcion: `${nombre}. Calzado ${modelo.subcategoria.toLowerCase()} de la marca ${modelo.marca}.`,
          destacado: modelo.subcategoria === 'Deportivo' && color === 'Negro',
          activo: true,
          precio: { id: precio.id },
          inventario: { id: inventario.id },
          categoria: { id: categoria.id },
          subcategoria: { id: subcategoria.id },
          marca: { id: marca.id },
          categoriaIva: { id: categoriaIva.id },
        });

        // Imagen placeholder
        await create('/producto-imagens', {
          imagenContentType: 'image/png',
          imagenAlt: nombre,
          esPrincipal: true,
          producto: { id: producto.id },
        });

        creados++;
      }
    }
  }

  console.log(`\n🎉 Seed de zapatos completado: ${creados} productos creados, ${actualizados} actualizados.`);
  console.log('URLs de prueba:');
  console.log(`  - Categoría Hombre:  ${BASE_URL}/categorias/hombre`);
  console.log(`  - Categoría Mujer:   ${BASE_URL}/categorias/mujer`);
  console.log(`  - Producto ejemplo:  ${BASE_URL}/productos/nike-air-max-hombre-negro-40`);
  console.log(`  - Carrito:           ${BASE_URL}/carrito`);
}

seed().catch(error => {
  console.error('\n💥 Error insertando datos demo:', error.response?.data || error.message);
  process.exit(1);
});

/**
 * Script de carga de contenido para KN-Store (tienda de calzado).
 *
 * Lee el manifiesto `contenido/catalogo.json` y sube marcas, categorías,
 * subcategorías, productos con precios e inventario, e imágenes locales desde
 * `contenido/imagenes/<slug-producto>/` a la API REST. Es idempotente por `slug`:
 * crear lo que no existe, actualizar lo que existe. Re-ejecutarlo nunca duplica.
 *
 * Lo genera y mantiene el agente `contenido-tienda` (patrón: scripts/seed-catalogo-real.js).
 *
 * Uso:
 *   node scripts/seed-contenido.js [BASE_URL] [--force-images]
 *
 * Ejemplos:
 *   node scripts/seed-contenido.js
 *   node scripts/seed-contenido.js http://localhost:8080
 *   node scripts/seed-contenido.js https://app.knstore.duckdns.org --force-images
 *
 * Variables de entorno:
 *   - KNSTORE_BASE_URL  (default: http://localhost:8080)
 *   - KNSTORE_USERNAME  (default: admin)
 *   - KNSTORE_PASSWORD  (default: admin)
 */

import axios from 'axios';
import fs from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const __dirname = path.dirname(fileURLToPath(import.meta.url));
const RAIZ = path.resolve(__dirname, '..');
const MANIFIESTO = path.join(RAIZ, 'contenido', 'catalogo.json');
const IMAGENES_DIR = path.join(RAIZ, 'contenido', 'imagenes');

const args = process.argv.slice(2).filter(a => !a.startsWith('--'));
const FORCE_IMAGES = process.argv.includes('--force-images');
const BASE_URL = args[0] || process.env.KNSTORE_BASE_URL || 'http://localhost:8080';
const USERNAME = process.env.KNSTORE_USERNAME || 'admin';
const PASSWORD = process.env.KNSTORE_PASSWORD || 'admin';

const api = axios.create({
  baseURL: `${BASE_URL}/api`,
  headers: { 'Content-Type': 'application/json' },
});

let token = null;
api.interceptors.request.use(config => {
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

const resumen = { marcas: 0, categorias: 0, subcategorias: 0, productosCreados: 0, productosActualizados: 0, imagenes: 0, omitidos: 0 };

const MIME = { '.jpg': 'image/jpeg', '.jpeg': 'image/jpeg', '.png': 'image/png', '.webp': 'image/webp' };

function cargarManifiesto() {
  if (!fs.existsSync(MANIFIESTO)) {
    console.error(`No se encontró el manifiesto: ${MANIFIESTO}`);
    process.exit(1);
  }
  const manifiesto = JSON.parse(fs.readFileSync(MANIFIESTO, 'utf8'));
  if (!Array.isArray(manifiesto.productos)) {
    console.error('El manifiesto debe tener una lista "productos".');
    process.exit(1);
  }
  return manifiesto;
}

async function autenticar() {
  const { data } = await api.post('/authenticate', { username: USERNAME, password: PASSWORD });
  token = data.id_token;
  console.log(`Autenticado como ${USERNAME} en ${BASE_URL}`);
}

async function listar(recurso) {
  const todos = [];
  const size = 200;
  for (let page = 0; ; page += 1) {
    const { data, headers } = await api.get(`/${recurso}?page=${page}&size=${size}&sort=id,asc`);
    todos.push(...data);
    const total = Number(headers['x-total-count'] ?? data.length);
    if (data.length === 0 || todos.length >= total) break;
  }
  return todos;
}

const crear = async (recurso, body) => (await api.post(`/${recurso}`, body)).data;
const actualizar = async (recurso, body) => (await api.put(`/${recurso}`, body)).data;

async function asegurarPorSlug(lista, recurso, datos) {
  const existente = lista.find(x => x.slug === datos.slug);
  if (existente) return { obj: existente, creado: false };
  const obj = await crear(recurso, datos);
  lista.push(obj);
  return { obj, creado: true };
}

async function procesarCategorias(manifiesto) {
  const categorias = await listar('categorias');
  const subcategorias = await listar('subcategorias');

  for (const marca of manifiesto.marcas ?? []) {
    const marcas = await listar('marcas');
    await asegurarPorSlug(marcas, 'marcas', { nombre: marca.nombre, slug: marca.slug });
  }

  for (const categoria of manifiesto.categorias ?? []) {
    const { obj, creado } = await asegurarPorSlug(categorias, 'categorias', {
      nombre: categoria.nombre,
      slug: categoria.slug,
      descripcion: categoria.descripcion ?? null,
      activo: true,
    });
    if (creado) resumen.categorias += 1;

    for (const sub of categoria.subcategorias ?? []) {
      const { creado: subCreada } = await asegurarPorSlug(subcategorias, 'subcategorias', {
        nombre: sub.nombre,
        slug: sub.slug,
        descripcion: sub.descripcion ?? null,
        activo: true,
        categoria: { id: obj.id },
      });
      if (subCreada) resumen.subcategorias += 1;
    }
  }

  return {
    marcas: await listar('marcas'),
    categorias,
    subcategorias,
  };
}

function resolverImagenes(producto) {
  const dir = path.join(IMAGENES_DIR, producto.slug);
  const definidas = producto.imagenes ?? [];
  const errores = [];
  for (const img of definidas) {
    const ruta = path.join(dir, img.archivo);
    if (!fs.existsSync(ruta)) errores.push(`Falta el archivo ${ruta}`);
    else if (!MIME[path.extname(img.archivo).toLowerCase()]) errores.push(`Formato no soportado: ${img.archivo}`);
    else if (!img.alt) errores.push(`Falta "alt" para ${producto.slug}/${img.archivo}`);
  }
  const principales = definidas.filter(i => i.principal).length;
  if (definidas.length > 0 && principales !== 1)
    errores.push(`${producto.slug}: debe tener exactamente 1 imagen principal (tiene ${principales})`);
  return errores;
}

async function subirImagenes(producto, productoId, imagenesExistentes) {
  if (FORCE_IMAGES) {
    for (const img of imagenesExistentes) {
      if (img.id) await api.delete(`/producto-imagens/${img.id}`);
    }
  } else if (imagenesExistentes.length > 0) {
    resumen.omitidos += 1;
    return;
  }

  const dir = path.join(IMAGENES_DIR, producto.slug);
  for (const img of producto.imagenes ?? []) {
    const ruta = path.join(dir, img.archivo);
    const base64 = fs.readFileSync(ruta).toString('base64');
    await crear('/producto-imagens', {
      imagen: base64,
      imagenContentType: MIME[path.extname(img.archivo).toLowerCase()],
      imagenAlt: img.alt,
      esPrincipal: Boolean(img.principal),
      producto: { id: productoId },
    });
    resumen.imagenes += 1;
  }
}

async function procesarProductos(manifiesto, taxonomia) {
  const productosExistentes = await listar('productos');
  const porSlug = new Map(productosExistentes.map(p => [p.slug, p]));

  for (const producto of manifiesto.productos) {
    const errores = resolverImagenes(producto);
    if (errores.length > 0) {
      console.warn(`⚠ ${producto.slug} omitido:\n  - ${errores.join('\n  - ')}`);
      resumen.omitidos += 1;
      continue;
    }

    const marca = taxonomia.marcas.find(m => m.slug === producto.marca);
    const categoria = taxonomia.categorias.find(c => c.slug === producto.categoria);
    const subcategoria = taxonomia.subcategorias.find(s => s.slug === producto.subcategoria);
    if (!marca || !categoria || !subcategoria) {
      console.warn(`⚠ ${producto.slug} omitido: marca/categoría/subcategoría no resuelta.`);
      resumen.omitidos += 1;
      continue;
    }

    const relaciones = {
      marca: { id: marca.id },
      categoria: { id: categoria.id },
      subcategoria: { id: subcategoria.id },
    };

    const precioPayload = {
      precioCompra: producto.precio?.precioCompra ?? null,
      precioVenta: producto.precio?.precioVenta ?? null,
      precioAdicional: producto.precio?.precioAdicional ?? 0,
      ganancia: producto.precio?.ganancia ?? (producto.precio?.precioVenta ?? 0) - (producto.precio?.precioCompra ?? 0),
    };
    const inventarioPayload = {
      stock: producto.inventario?.stock ?? 0,
      stockMinimo: producto.inventario?.stockMinimo ?? 0,
      ubicacionBodega: producto.inventario?.ubicacionBodega ?? 'BODEGA_PRINCIPAL',
      garantiaMeses: producto.inventario?.garantiaMeses ?? 0,
    };

    const campos = {
      ...relaciones,
      nombre: producto.nombre,
      slug: producto.slug,
      referencia: producto.referencia ?? null,
      sku: producto.sku ?? null,
      color: producto.color ?? null,
      talla: producto.talla ?? null,
      codigoBarras: producto.codigoBarras ?? null,
      unidadMedida: producto.unidadMedida ?? 'PAR',
      descripcion: producto.descripcion ?? null,
      destacado: Boolean(producto.destacado),
      activo: producto.activo !== false,
    };

    const existente = porSlug.get(producto.slug);
    let productoId;

    if (existente) {
      let precio = existente.precio;
      let inventario = existente.inventario;
      if (precio?.id) await actualizar('/producto-precios', { ...precioPayload, id: precio.id, producto: { id: existente.id } });
      else precio = await crear('/producto-precios', precioPayload);
      if (inventario?.id) await actualizar('/producto-inventarios', { ...inventarioPayload, id: inventario.id });
      else inventario = await crear('/producto-inventarios', inventarioPayload);

      await actualizar('/productos', {
        ...existente,
        ...campos,
        id: existente.id,
        precio: { id: precio.id },
        inventario: { id: inventario.id },
      });
      productoId = existente.id;
      resumen.productosActualizados += 1;
    } else {
      const precio = await crear('/producto-precios', precioPayload);
      const inventario = await crear('/producto-inventarios', inventarioPayload);
      const productoNuevo = await crear('/productos', {
        ...campos,
        precio: { id: precio.id },
        inventario: { id: inventario.id },
      });
      // Referencia inversa del precio al producto (patrón seed-catalogo-real.js).
      await actualizar('/producto-precios', { ...precioPayload, id: precio.id, producto: { id: productoNuevo.id } });
      productoId = productoNuevo.id;
      resumen.productosCreados += 1;
      porSlug.set(producto.slug, productoNuevo);
    }

    await subirImagenes(producto, productoId, existente?.imagenes ?? []);
  }
}

async function main() {
  const manifiesto = cargarManifiesto();
  console.log(
    `Manifiesto: ${manifiesto.productos.length} productos, imágenes en ${IMAGENES_DIR}${FORCE_IMAGES ? ' (modo --force-images)' : ''}`,
  );
  await autenticar();
  const taxonomia = await procesarCategorias(manifiesto);
  await procesarProductos(manifiesto, taxonomia);

  console.log('\n=== Resumen de carga ===');
  console.log(`Categorías creadas:       ${resumen.categorias}`);
  console.log(`Subcategorías creadas:    ${resumen.subcategorias}`);
  console.log(`Productos creados:        ${resumen.productosCreados}`);
  console.log(`Productos actualizados:   ${resumen.productosActualizados}`);
  console.log(`Imágenes subidas:         ${resumen.imagenes}`);
  console.log(`Elementos omitidos:       ${resumen.omitidos}`);
}

main().catch(error => {
  console.error('❌ La carga falló:', error.response?.data ?? error.message);
  console.error('Puedes re-ejecutar el script: es idempotente por slug y no duplica datos.');
  process.exitCode = 1;
});

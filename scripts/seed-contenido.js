/**
 * Script de carga de contenido para KN-Store (tienda de calzado).
 *
 * Lee el manifiesto `contenido/catalogo.json` (versiones 1 y 2) y sube categorías de IVA,
 * marcas, categorías, subcategorías, productos con precios, inventario, etiquetas,
 * e imágenes (por URL externa o por archivo local) a la API REST.
 * Es idempotente por `slug`: crear lo que no existe, actualizar lo que existe. Re-ejecutarlo nunca duplica.
 *
 * Manifiesto v2 (recomendado):
 *   - Raíz `categoriasIVA` ({nombre, porcentaje, estado}) asegurada por nombre en /api/categoria-ivas.
 *   - Producto.categoriaIVA (nombre) -> se envía `categoriaIva: {id}` al crear/actualizar.
 *   - Producto.etiquetas (2-4 strings): el manifiesto es la fuente de verdad; se crean/borran en /api/etiqueta-productos.
 *   - Imágenes con `url` (https + host permitido) -> POST /api/producto-imagens con `imagenUrl` (sin base64),
 *     idempotente por URL dentro del producto. Imágenes con `archivo` -> flujo local (base64) como en v1.
 *   - Pre-vuelo (preflight) ANTES de tocar la API: slugs duplicados = error fatal;
 *     referencias rotas o imágenes fuera de regla = warn + omitir producto.
 *
 * Lo genera y mantiene el agente `contenido-tienda` (patrón: scripts/seed-catalogo-real.js).
 *
 * Uso:
 *   node scripts/seed-contenido.js [BASE_URL] [--force-images] [--preflight]
 *
 * Ejemplos:
 *   node scripts/seed-contenido.js                       # local (http://localhost:8080)
 *   node scripts/seed-contenido.js http://localhost:8080
 *   node scripts/seed-contenido.js https://app.knstore.duckdns.org
 *   node scripts/seed-contenido.js --preflight           # solo valida el manifiesto, no toca la API
 *   node scripts/seed-contenido.js --force-images        # reemplaza las imágenes ya cargadas
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
const PREFLIGHT = process.argv.includes('--preflight');
const BASE_URL = args[0] || process.env.KNSTORE_BASE_URL || 'http://localhost:8080';
const USERNAME = process.env.KNSTORE_USERNAME || 'admin';
const PASSWORD = process.env.KNSTORE_PASSWORD || 'admin';

const VERSIONES_SOPORTADAS = new Set([1, 2]);
// Hosts permitidos para imágenes por URL (deben coincidir con la CSP img-src de application.yml).
const HOSTS_IMAGEN_PERMITIDOS = new Set(['images.unsplash.com', 'plus.unsplash.com', 'images.pexels.com']);
const MIME = { '.jpg': 'image/jpeg', '.jpeg': 'image/jpeg', '.png': 'image/png', '.webp': 'image/webp' };
const ALT_MIN = 10;
const ALT_MAX = 120;
const IMAGENES_MIN = { 1: 1, 2: 2 };
const IMAGENES_MAX = 4;

const api = axios.create({
  baseURL: `${BASE_URL}/api`,
  headers: { 'Content-Type': 'application/json' },
});

let token = null;
api.interceptors.request.use(config => {
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

const resumen = {
  marcas: 0,
  categorias: 0,
  subcategorias: 0,
  categoriaIVACreadas: 0,
  categoriaIVAActualizadas: 0,
  productosCreados: 0,
  productosActualizados: 0,
  etiquetasCreadas: 0,
  etiquetasBorradas: 0,
  imagenes: 0,
  imagenesActualizadas: 0,
  imagenesOmitidas: 0,
  imagenesBorradas: 0,
  omitidos: 0,
};

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
  const version = manifiesto.version;
  if (version === undefined || version === null) {
    console.error('El manifiesto debe declarar "version": 1 o 2 en la raíz.');
    process.exit(1);
  }
  if (!VERSIONES_SOPORTADAS.has(version)) {
    console.error(`Versión de manifiesto no soportada: ${version}. Versiones soportadas: 1, 2.`);
    process.exit(1);
  }
  if (version === 1) {
    console.warn('⚠ Manifiesto en versión 1 (legacy): sin categorías IVA, etiquetas ni imágenes por URL. Considera migrar a la versión 2.');
  }
  return manifiesto;
}

/** Compatibilidad: v2 usa `esPrincipal`, v1 usaba `principal`. */
const esPrincipalDe = img => Boolean(img.esPrincipal ?? img.principal);

function slugsDuplicados(valores) {
  const vistos = new Set();
  const dup = new Set();
  for (const v of valores) (vistos.has(v) ? dup : vistos).add(v);
  return [...dup];
}

/**
 * Pre-vuelo: corre ANTES de tocar la API.
 * - Slugs duplicados (marcas/categorías/subcategorías globales/productos) => error fatal.
 * - Referencias rotas o imágenes fuera de regla => warn + omitir el producto.
 * Devuelve la lista de productos listos para cargar.
 */
function preflight(manifiesto) {
  const fatales = [];
  const marcasDef = manifiesto.marcas ?? [];
  const categoriasDef = manifiesto.categorias ?? [];
  const subcategoriasDef = categoriasDef.flatMap(c => c.subcategorias ?? []);

  for (const [etiqueta, valores] of [
    ['marcas', marcasDef.map(x => x.slug)],
    ['categorías', categoriasDef.map(x => x.slug)],
    ['subcategorías (slugs son globales)', subcategoriasDef.map(x => x.slug)],
    ['productos', manifiesto.productos.map(x => x.slug)],
  ]) {
    const dups = slugsDuplicados(valores.filter(Boolean));
    if (dups.length > 0) fatales.push(`${etiqueta} con slug duplicado: ${dups.join(', ')}`);
  }
  if (fatales.length > 0) {
    console.error('❌ Preflight: errores fatales en el manifiesto:\n  - ' + fatales.join('\n  - '));
    process.exit(1);
  }

  const marcas = new Set(marcasDef.map(x => x.slug));
  const categorias = new Set(categoriasDef.map(x => x.slug));
  const subcategorias = new Set(subcategoriasDef.map(x => x.slug));
  const ivas = new Set((manifiesto.categoriasIVA ?? []).map(x => x.nombre));
  const version = manifiesto.version;
  const minImagenes = IMAGENES_MIN[version] ?? 2;

  const listos = [];
  for (const producto of manifiesto.productos) {
    const errs = [];
    if (!producto.marca || !marcas.has(producto.marca)) errs.push(`marca "${producto.marca}" no existe en el manifiesto`);
    if (!producto.categoria || !categorias.has(producto.categoria))
      errs.push(`categoría "${producto.categoria}" no existe en el manifiesto`);
    if (!producto.subcategoria || !subcategorias.has(producto.subcategoria))
      errs.push(`subcategoría "${producto.subcategoria}" no existe en el manifiesto`);
    if (version >= 2 && producto.categoriaIVA && !ivas.has(producto.categoriaIVA)) {
      errs.push(`categoría IVA "${producto.categoriaIVA}" no existe en categoriasIVA`);
    }
    if (version >= 2 && !producto.categoriaIVA) {
      console.warn(`⚠ ${producto.slug}: sin "categoriaIVA"; se guardará sin clasificación de IVA.`);
    }

    const imagenes = producto.imagenes ?? [];
    if (imagenes.length < minImagenes || imagenes.length > IMAGENES_MAX) {
      errs.push(`${imagenes.length} imágenes (la versión ${version} exige ${minImagenes}-${IMAGENES_MAX})`);
    }
    const principales = imagenes.filter(esPrincipalDe).length;
    if (imagenes.length > 0 && principales !== 1) errs.push(`debe haber exactamente 1 imagen principal (hay ${principales})`);

    imagenes.forEach((img, i) => {
      const n = `imagen ${i + 1}`;
      if (!img.alt) errs.push(`${n}: falta "alt"`);
      else if (version >= 2 && (img.alt.length < ALT_MIN || img.alt.length > ALT_MAX)) {
        errs.push(`${n}: "alt" debe tener ${ALT_MIN}-${ALT_MAX} caracteres (tiene ${img.alt.length})`);
      }
      if (img.url) {
        try {
          const url = new URL(img.url);
          if (url.protocol !== 'https:') errs.push(`${n}: la URL debe ser https`);
          else if (!HOSTS_IMAGEN_PERMITIDOS.has(url.hostname)) errs.push(`${n}: host no permitido "${url.hostname}"`);
        } catch {
          errs.push(`${n}: URL inválida "${img.url}"`);
        }
      } else if (img.archivo) {
        const ruta = path.join(IMAGENES_DIR, producto.slug, img.archivo);
        if (!fs.existsSync(ruta)) errs.push(`${n}: falta el archivo ${ruta}`);
        else if (!MIME[path.extname(img.archivo).toLowerCase()]) errs.push(`${n}: formato no soportado (${img.archivo})`);
      } else {
        errs.push(`${n}: necesita "url" (remota) o "archivo" (local)`);
      }
    });

    if (errs.length > 0) {
      console.warn(`⚠ ${producto.slug} omitido:\n  - ${errs.join('\n  - ')}`);
      resumen.omitidos += 1;
    } else {
      listos.push(producto);
    }
  }
  return listos;
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

// Normaliza a UNA sola barra inicial: axios 1.x interpreta "//ruta" como URL absoluta (protocol-relative) y falla con "Invalid URL".
const rutaDe = recurso => `/${String(recurso).replace(/^\/+/, '')}`;
const crear = async (recurso, body) => (await api.post(rutaDe(recurso), body)).data;
// JHipster actualiza con PUT /recurso/{id} (el id va en la URL y en el body; sin id en la URL responde 405).
const actualizar = async (recurso, body) => {
  if (!body?.id) throw new Error(`actualizar(${recurso}) requiere "id" en el body`);
  return (await api.put(`${rutaDe(recurso)}/${body.id}`, body)).data;
};

async function asegurarPorSlug(lista, recurso, datos) {
  const existente = lista.find(x => x.slug === datos.slug);
  if (existente) return { obj: existente, creado: false };
  const obj = await crear(recurso, datos);
  lista.push(obj);
  return { obj, creado: true };
}

/** Asegura las categorías de IVA de la raíz del manifiesto por `nombre` (GET/POST/PUT /api/categoria-ivas). */
async function procesarCategoriasIVA(manifiesto) {
  const porNombre = new Map();
  const defs = manifiesto.categoriasIVA ?? [];
  if (defs.length === 0) return porNombre;

  for (const existente of await listar('categoria-ivas')) porNombre.set(existente.nombre, existente);

  for (const def of defs) {
    const body = { nombre: def.nombre, porcentaje: def.porcentaje, estado: def.estado ?? 'ACTIVO' };
    const existente = porNombre.get(def.nombre);
    if (!existente) {
      porNombre.set(def.nombre, await crear('/categoria-ivas', body));
      resumen.categoriaIVACreadas += 1;
    } else if (Number(existente.porcentaje) !== Number(body.porcentaje) || existente.estado !== body.estado) {
      porNombre.set(def.nombre, await actualizar('/categoria-ivas', { ...body, id: existente.id }));
      resumen.categoriaIVAActualizadas += 1;
    }
  }
  return porNombre;
}

async function procesarTaxonomia(manifiesto) {
  const categorias = await listar('categorias');
  const subcategorias = await listar('subcategorias');
  const marcas = await listar('marcas');

  for (const marca of manifiesto.marcas ?? []) {
    const { creado } = await asegurarPorSlug(marcas, 'marcas', { nombre: marca.nombre, slug: marca.slug });
    if (creado) resumen.marcas += 1;
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

function agruparPorProducto(lista) {
  const mapa = new Map();
  for (const item of lista) {
    const key = item.producto?.id;
    if (!key) continue;
    if (!mapa.has(key)) mapa.set(key, []);
    mapa.get(key).push(item);
  }
  return mapa;
}

/**
 * Sincroniza las etiquetas de un producto contra el manifiesto (fuente de verdad):
 * crea las que faltan y borra las sobrantes. Sin `etiquetas` en el manifiesto no hace nada (compat v1).
 */
async function sincronizarEtiquetas(productoId, etiquetasDeseadas, porProducto) {
  if (!Array.isArray(etiquetasDeseadas)) return;
  const existentes = porProducto.get(productoId) ?? [];
  porProducto.set(productoId, existentes);
  const deseadas = [...new Set(etiquetasDeseadas)];

  for (const nombre of deseadas) {
    if (!existentes.some(e => e.etiqueta === nombre)) {
      const creada = await crear('/etiqueta-productos', { etiqueta: nombre, producto: { id: productoId } });
      existentes.push(creada);
      resumen.etiquetasCreadas += 1;
    }
  }
  for (const e of [...existentes]) {
    if (!deseadas.includes(e.etiqueta)) {
      await api.delete(`/etiqueta-productos/${e.id}`);
      existentes.splice(existentes.indexOf(e), 1);
      resumen.etiquetasBorradas += 1;
    }
  }
}

/**
 * Sube las imágenes de un producto:
 * - con `url`  => POST imagenUrl (idempotente por URL dentro del producto; actualiza alt/principal si cambiaron).
 * - con `archivo` => base64 desde contenido/imagenes/<slug>/ (se omite si el producto ya tiene imágenes, como en v1).
 */
async function subirImagenes(producto, productoId, imagenesExistentes) {
  if (FORCE_IMAGES && imagenesExistentes.length > 0) {
    for (const img of imagenesExistentes) {
      if (img.id) await api.delete(`/producto-imagens/${img.id}`);
    }
    resumen.imagenesBorradas += imagenesExistentes.length;
    imagenesExistentes = [];
  }

  const dir = path.join(IMAGENES_DIR, producto.slug);
  for (const img of producto.imagenes ?? []) {
    const principal = esPrincipalDe(img);

    if (img.url) {
      const existente = imagenesExistentes.find(e => e.imagenUrl === img.url);
      if (existente) {
        const cambio = existente.imagenAlt !== img.alt || Boolean(existente.esPrincipal) !== principal;
        if (cambio) {
          await actualizar('/producto-imagens', { ...existente, imagenAlt: img.alt, esPrincipal: principal });
          resumen.imagenesActualizadas += 1;
        } else {
          resumen.imagenesOmitidas += 1;
        }
        continue;
      }
      const creada = await crear('/producto-imagens', {
        imagenUrl: img.url,
        imagenContentType: 'image/jpeg',
        imagenAlt: img.alt,
        esPrincipal: principal,
        producto: { id: productoId },
      });
      imagenesExistentes.push(creada);
      resumen.imagenes += 1;
      continue;
    }

    // Archivo local (v1): sin forma de identificarlo contra lo remoto, se omite si ya hay imágenes.
    if (imagenesExistentes.length > 0) {
      resumen.imagenesOmitidas += 1;
      continue;
    }
    const ruta = path.join(dir, img.archivo);
    const base64 = fs.readFileSync(ruta).toString('base64');
    const creada = await crear('/producto-imagens', {
      imagen: base64,
      imagenContentType: MIME[path.extname(img.archivo).toLowerCase()],
      imagenAlt: img.alt,
      esPrincipal: principal,
      producto: { id: productoId },
    });
    imagenesExistentes.push(creada);
    resumen.imagenes += 1;
  }
}

async function procesarProductos(productosListos, taxonomia, ivasPorNombre) {
  const productosExistentes = await listar('productos');
  const porSlug = new Map(productosExistentes.map(p => [p.slug, p]));
  const imagenesPorProducto = agruparPorProducto(await listar('producto-imagens'));
  const etiquetasPorProducto = agruparPorProducto(await listar('etiqueta-productos'));

  for (const producto of productosListos) {
    const marca = taxonomia.marcas.find(m => m.slug === producto.marca);
    const categoria = taxonomia.categorias.find(c => c.slug === producto.categoria);
    const subcategoria = taxonomia.subcategorias.find(s => s.slug === producto.subcategoria);
    if (!marca || !categoria || !subcategoria) {
      console.warn(`⚠ ${producto.slug} omitido: marca/categoría/subcategoría no resuelta en la API.`);
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
    if (producto.categoriaIVA) {
      const iva = ivasPorNombre.get(producto.categoriaIVA);
      if (iva) campos.categoriaIva = { id: iva.id };
    }

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
      imagenesPorProducto.set(productoId, []);
      etiquetasPorProducto.set(productoId, []);
    }

    await subirImagenes(producto, productoId, imagenesPorProducto.get(productoId) ?? []);
    await sincronizarEtiquetas(productoId, producto.etiquetas, etiquetasPorProducto);
  }
}

function imprimirResumen() {
  console.log('\n=== Resumen de carga ===');
  console.log(`Categorías IVA creadas:      ${resumen.categoriaIVACreadas}`);
  console.log(`Categorías IVA actualizadas: ${resumen.categoriaIVAActualizadas}`);
  console.log(`Marcas creadas:              ${resumen.marcas}`);
  console.log(`Categorías creadas:          ${resumen.categorias}`);
  console.log(`Subcategorías creadas:       ${resumen.subcategorias}`);
  console.log(`Productos creados:           ${resumen.productosCreados}`);
  console.log(`Productos actualizados:      ${resumen.productosActualizados}`);
  console.log(`Etiquetas creadas:           ${resumen.etiquetasCreadas}`);
  console.log(`Etiquetas borradas:          ${resumen.etiquetasBorradas}`);
  console.log(`Imágenes subidas:            ${resumen.imagenes}`);
  console.log(`Imágenes actualizadas:       ${resumen.imagenesActualizadas}`);
  console.log(`Imágenes sin cambios:        ${resumen.imagenesOmitidas}`);
  if (FORCE_IMAGES) console.log(`Imágenes borradas (--force): ${resumen.imagenesBorradas}`);
  console.log(`Elementos omitidos:          ${resumen.omitidos}`);
}

async function main() {
  const manifiesto = cargarManifiesto();
  const imagenesTotales = manifiesto.productos.reduce((acc, p) => acc + (p.imagenes?.length ?? 0), 0);
  console.log(
    `Manifiesto v${manifiesto.version}: ${manifiesto.productos.length} productos, ${imagenesTotales} imágenes` +
      `${FORCE_IMAGES ? ' (modo --force-images)' : ''}`,
  );

  // Pre-vuelo local ANTES de tocar la API: slugs duplicados = fatal; refs/imágenes rotas = warn + omitir.
  const productosListos = preflight(manifiesto);
  console.log(`Preflight: ${productosListos.length} productos listos, ${resumen.omitidos} omitidos.`);

  if (PREFLIGHT) {
    console.log('Modo --preflight: el manifiesto se validó sin consultar la API.');
    return;
  }

  await autenticar();
  const ivasPorNombre = await procesarCategoriasIVA(manifiesto);
  const taxonomia = await procesarTaxonomia(manifiesto);
  await procesarProductos(productosListos, taxonomia, ivasPorNombre);
  imprimirResumen();
}

main().catch(error => {
  console.error('❌ La carga falló:', error.response?.data ?? error.message);
  console.error('Puedes re-ejecutar el script: es idempotente por slug y no duplica datos.');
  process.exitCode = 1;
});

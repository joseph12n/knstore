/**
 * Scraper de imágenes de zapatos para KN-Store (tienda de calzado).
 *
 * Fuente: Zappos (https://www.zappos.com). Las páginas de búsqueda embeben
 * `window.__INITIAL_STATE__` con la lista de productos: nombre, marca, color,
 * género, precio, URL de la página del producto y `imageMap` (MAIN/FRNT/BACK/
 * LEFT/RGHT/TOPP/BOTT). Las fotos son de estudio sobre fondo blanco y el
 * título acompaña a la foto por construcción (mismo objeto de producto).
 *
 * Dos modos:
 *   1. --collect : scrapea las búsquedas de Zappos y regenera
 *      `contenido/scraping/candidatos.json` (candidatos crudos con trazabilidad).
 *   2. (default) : lee `contenido/scraping/listing.json` (curado) y descarga las
 *      imágenes a `contenido/imagenes/<slug>/` de forma idempotente
 *      (no re-descarga si el archivo ya existe).
 *
 * El QA visual (aprobada/rechazada por imagen) se registra aparte en
 * `contenido/scraping/qa.json`; este script no decide calidad visual.
 *
 * Uso:
 *   node scripts/scraper-zapatos.js --collect                 # solo Zappos -> candidatos.json
 *   node scripts/scraper-zapatos.js                           # descarga todo el listing.json
 *   node scripts/scraper-zapatos.js --rol principal           # descarga solo ese rol
 *   node scripts/scraper-zapatos.js --solo slug1,slug2        # limita a ciertos slugs
 *   node scripts/scraper-zapatos.js --base contenido/imagenes # carpeta destino (default)
 *
 * Variables de entorno:
 *   - KNSTORE_SCRAPER_DELAY_MS  (default: 400) pausa entre descargas
 *   - KNSTORE_SCRAPER_COLLECT_DELAY_MS (default: 1500) pausa entre búsquedas
 */

import axios from 'axios';
import fs from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const __dirname = path.dirname(fileURLToPath(import.meta.url));
const RAIZ = path.resolve(__dirname, '..');
const SCRAPING_DIR = path.join(RAIZ, 'contenido', 'scraping');
const CANDIDATOS = path.join(SCRAPING_DIR, 'candidatos.json');
const LISTING = path.join(SCRAPING_DIR, 'listing.json');

const args = process.argv.slice(2);
const flag = nombre => args.includes(nombre);
const valorFlag = nombre => {
  const i = args.indexOf(nombre);
  return i >= 0 ? args[i + 1] : undefined;
};

const COLLECT = flag('--collect');
const BASE_DIR = valorFlag('--base') ?? path.join(RAIZ, 'contenido', 'imagenes');
const ROL = valorFlag('--rol');
const SOLO = (valorFlag('--solo') ?? '')
  .split(',')
  .map(s => s.trim())
  .filter(Boolean);
const DELAY_MS = Number(process.env.KNSTORE_SCRAPER_DELAY_MS ?? 400);
const COLLECT_DELAY_MS = Number(process.env.KNSTORE_SCRAPER_COLLECT_DELAY_MS ?? 1500);

const UA = 'Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36';
const CABECERAS = {
  'User-Agent': UA,
  Accept: 'text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8',
  'Accept-Language': 'en-US,en;q=0.9,es;q=0.8',
};
const CABECERAS_IMAGEN = { ...CABECERAS, Referer: 'https://www.zappos.com/', Accept: 'image/avif,image/webp,image/*,*/*;q=0.8' };

// Marcas del catálogo KN-Store (slug -> nombres que puede reportar Zappos).
const MARCAS = {
  nike: ['nike'],
  adidas: ['adidas'],
  puma: ['puma'],
  converse: ['converse'],
  vans: ['vans'],
  'new-balance': ['new balance'],
  reebok: ['reebok'],
  jordan: ['jordan', 'air jordan'],
  fila: ['fila'],
  skechers: ['skechers'],
  crocs: ['crocs'],
  'under-armour': ['under armour', 'under armour®'],
};
const NOMBRE_MARCA = {
  nike: 'Nike',
  adidas: 'Adidas',
  puma: 'Puma',
  converse: 'Converse',
  vans: 'Vans',
  'new-balance': 'New Balance',
  reebok: 'Reebok',
  jordan: 'Jordan',
  fila: 'Fila',
  skechers: 'Skechers',
  crocs: 'Crocs',
  'under-armour': 'Under Armour',
};

// Búsquedas de Zappos a scrapear (una por marca; cada una devuelve hasta ~100 productos).
const BUSQUEDAS = [
  { term: 'nike', marca: 'nike' },
  { term: 'adidas', marca: 'adidas' },
  { term: 'puma', marca: 'puma' },
  { term: 'converse', marca: 'converse' },
  { term: 'vans', marca: 'vans' },
  { term: 'new balance', marca: 'new-balance' },
  { term: 'reebok', marca: 'reebok' },
  { term: 'jordan', marca: 'jordan' },
  { term: 'fila', marca: 'fila' },
  { term: 'skechers', marca: 'skechers' },
  { term: 'crocs', marca: 'crocs' },
  { term: 'under armour', marca: 'under-armour' },
];

// No queremos botas/sandalias/ropa ni accesorios: solo calzado urbano/deportivo.
const EXCLUIR_NOMBRE =
  /(cleat|golf|slide|sandal|thong|slipper|boot|bootie|heel|pump|wedge|loafer|moccasin|oxford|dress shoe|water shoe|clog|sock|jacket|shirt|short|pant|hoodie|bag|backpack|glove|hat|cap\b|beanie|tumbler|charm|jibbitz|soccer|football|baseball|softball|wrestling|track)/i;
const EXCLUIR_SALVO_CROCS = /clog/i;

const dormir = ms => new Promise(r => setTimeout(r, ms));

function limpiarTitulo(nombre) {
  return String(nombre ?? '')
    .replace(/&#\d+;/g, ' ')
    .replace(/&amp;/g, '&')
    .replace(/&[a-z]+;/gi, ' ')
    .replace(/[™®©]/g, '')
    .replace(/\s+/g, ' ')
    .trim();
}

function marcaDe(brandName) {
  const b = String(brandName ?? '')
    .toLowerCase()
    .replace(/[®™]/g, '')
    .trim();
  for (const [slug, alias] of Object.entries(MARCAS)) {
    if (alias.includes(b)) return slug;
  }
  return null;
}

function generoDe(genders) {
  const g = (genders ?? []).map(x => String(x).toLowerCase());
  const ninos = g.some(x => /boy|girl|kid|infant|toddler/.test(x));
  const hombres = g.includes('men');
  const mujeres = g.includes('women');
  if (ninos && !hombres && !mujeres) return 'nino';
  if (hombres && mujeres) return 'unisex';
  if (mujeres) return 'mujer';
  if (hombres) return 'hombre';
  if (ninos) return 'nino';
  return null;
}

/** Extrae el JSON de `window.__INITIAL_STATE__ = {...};` con conteo de llaves. */
function extraerInitialState(html) {
  const i = html.indexOf('__INITIAL_STATE__');
  if (i < 0) return null;
  const j = html.indexOf('{', i);
  if (j < 0) return null;
  let depth = 0;
  let enCadena = false;
  let escape = false;
  for (let k = j; k < html.length; k += 1) {
    const c = html[k];
    if (enCadena) {
      if (escape) escape = false;
      else if (c === '\\') escape = true;
      else if (c === '"') enCadena = false;
      continue;
    }
    if (c === '"') enCadena = true;
    else if (c === '{') depth += 1;
    else if (c === '}') {
      depth -= 1;
      if (depth === 0) {
        try {
          return JSON.parse(html.slice(j, k + 1));
        } catch (error) {
          console.error(`  ⚠ No se pudo parsear __INITIAL_STATE__: ${error.message}`);
          return null;
        }
      }
    }
  }
  return null;
}

function normalizarProducto(p, term) {
  const marca = marcaDe(p.brandName);
  const titulo = limpiarTitulo(p.productName);
  if (!marca || !titulo) return null;
  if (p.productType && p.productType !== 'Shoes') return null;
  if (EXCLUIR_NOMBRE.test(titulo) && !(marca === 'crocs' && EXCLUIR_SALVO_CROCS.test(titulo))) return null;
  const imageMap = p.imageMap ?? {};
  if (!imageMap.MAIN) return null;
  const genero = generoDe(p.txAttrFacet_Gender);
  if (!genero) return null;
  return {
    productId: String(p.productId),
    styleId: String(p.styleId ?? ''),
    marca,
    marcaNombre: NOMBRE_MARCA[marca],
    titulo,
    color: p.color ?? '',
    styleColor: p.styleColor ?? '',
    genero,
    precioUSD: Number(String(p.price ?? '').replace(/[^0-9.]/g, '')) || null,
    pagina: `https://www.zappos.com${p.productUrl ?? p.productSeoUrl ?? ''}`,
    imageMap,
    busqueda: term,
  };
}

async function recolectar() {
  fs.mkdirSync(SCRAPING_DIR, { recursive: true });
  const porId = new Map();
  const fallos = [];

  for (const busqueda of BUSQUEDAS) {
    const url = `https://www.zappos.com/search?term=${encodeURIComponent(busqueda.term)}`;
    process.stdout.write(`Buscando "${busqueda.term}" ... `);
    try {
      const { data: html, status } = await axios.get(url, { headers: CABECERAS, timeout: 30000 });
      const state = extraerInitialState(String(html));
      const lista = state?.products?.list ?? [];
      let utiles = 0;
      for (const p of lista) {
        const cand = normalizarProducto(p, busqueda.term);
        if (!cand) continue;
        if (!porId.has(cand.productId)) {
          porId.set(cand.productId, cand);
          utiles += 1;
        }
      }
      console.log(`HTTP ${status}: ${lista.length} en la lista, ${utiles} candidatos nuevos`);
    } catch (error) {
      const detalle = error.response ? `HTTP ${error.response.status}` : error.message;
      console.log(`FALLÓ (${detalle})`);
      fallos.push({ term: busqueda.term, error: detalle });
    }
    await dormir(COLLECT_DELAY_MS);
  }

  const candidatos = [...porId.values()].sort((a, b) => `${a.marca}${a.titulo}`.localeCompare(`${b.marca}${b.titulo}`));
  const salida = {
    generado: new Date().toISOString(),
    fuente: 'zappos.com',
    nota: 'Candidatos crudos del scraper (sin QA visual). El curado va en listing.json.',
    busquedas: BUSQUEDAS.map(b => b.term),
    fallos,
    total: candidatos.length,
    candidatos,
  };
  fs.writeFileSync(CANDIDATOS, `${JSON.stringify(salida, null, 2)}\n`);
  console.log(`\nCandidatos: ${candidatos.length} -> ${path.relative(RAIZ, CANDIDATOS)}`);
  if (fallos.length > 0) console.log(`Fallos de búsqueda: ${fallos.map(f => f.term).join(', ')}`);
}

function leerListing() {
  if (!fs.existsSync(LISTING)) {
    console.error(`No existe ${LISTING}. Genera primero el listing curado (ver README de contenido/imagenes).`);
    process.exit(1);
  }
  const listing = JSON.parse(fs.readFileSync(LISTING, 'utf8'));
  if (!Array.isArray(listing.productos)) {
    console.error('listing.json debe tener una lista "productos".');
    process.exit(1);
  }
  return listing;
}

async function descargarImagen(url, destino) {
  const { data, headers, status } = await axios.get(url, {
    headers: CABECERAS_IMAGEN,
    responseType: 'arraybuffer',
    timeout: 30000,
  });
  const tipo = String(headers['content-type'] ?? '');
  if (status !== 200 || !tipo.startsWith('image/')) throw new Error(`content-type inesperado: ${tipo || 'ninguno'}`);
  const buffer = Buffer.from(data);
  if (buffer.length < 5000) throw new Error(`archivo muy pequeño (${buffer.length} bytes)`);
  if (!(buffer[0] === 0xff && buffer[1] === 0xd8)) throw new Error('no parece un JPEG válido');
  fs.mkdirSync(path.dirname(destino), { recursive: true });
  fs.writeFileSync(destino, buffer);
  return buffer.length;
}

async function descargar() {
  const listing = leerListing();
  const seleccion = listing.productos.filter(p => SOLO.length === 0 || SOLO.includes(p.slug));
  const resumen = { descargadas: 0, omitidas: 0, errores: 0, bytes: 0 };
  const errores = [];

  for (const producto of seleccion) {
    const imagenes = (producto.imagenes ?? []).filter(img => !ROL || img.rol === ROL);
    for (const img of imagenes) {
      const destino = path.join(BASE_DIR, producto.slug, img.archivo);
      if (fs.existsSync(destino)) {
        resumen.omitidas += 1;
        continue;
      }
      try {
        const bytes = await descargarImagen(img.url, destino);
        resumen.descargadas += 1;
        resumen.bytes += bytes;
        console.log(`  ✓ ${producto.slug}/${img.archivo} (${Math.round(bytes / 1024)} KB)`);
      } catch (error) {
        resumen.errores += 1;
        errores.push(`${producto.slug}/${img.archivo}: ${error.message}`);
        console.warn(`  ✗ ${producto.slug}/${img.archivo}: ${error.message}`);
      }
      await dormir(DELAY_MS);
    }
  }

  console.log('\n=== Resumen de descarga ===');
  console.log(`Descargadas: ${resumen.descargadas}  Omitidas (ya existían): ${resumen.omitidas}  Errores: ${resumen.errores}`);
  console.log(`Peso total: ${(resumen.bytes / 1024 / 1024).toFixed(1)} MB`);
  if (errores.length > 0) console.log(`Errores:\n  - ${errores.join('\n  - ')}`);
}

async function main() {
  if (COLLECT) await recolectar();
  else await descargar();
}

main().catch(error => {
  console.error('❌ El scraper falló:', error.response?.status ? `HTTP ${error.response.status}` : error.message);
  process.exitCode = 1;
});

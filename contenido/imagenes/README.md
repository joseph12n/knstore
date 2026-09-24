# Imágenes de producto — KN-Store

Carpeta de trabajo del agente `contenido-tienda`. Aquí se depositan las imágenes de
producto que se subirán a la tienda; el manifiesto `contenido/catalogo.json` las
describe y `scripts/seed-contenido.js` las carga contra la API.

**Flujo vigente (2026-09-23): imágenes locales descargadas de un retailer real con QA
visual.** Cada producto tiene 2-3 fotos de estudio (fondo blanco) cuyo título acompaña
a la foto por construcción, porque ambas salen del mismo objeto de producto.

## Flujo local (el que usa el catálogo actual)

1. `node scripts/scraper-zapatos.js --collect` — scrapea Zappos y regenera
   `contenido/scraping/candidatos.json` (candidatos crudos, con `productId`, marca,
   título, color, género, precio USD y `imageMap`).
2. Curaduría manual → `contenido/scraping/listing.json`: 50-70 candidatos con `slug`,
   título real, marca/color/género, `pagina` (URL del producto para trazabilidad) y
   `imagenes: [{url, archivo, rol}]` (variante 1000×1000 del CDN).
3. `node scripts/scraper-zapatos.js` — descarga a `contenido/imagenes/<slug>/` de forma
   idempotente (no re-descarga si el archivo existe). Filtros: `--rol principal`,
   `--solo slug1,slug2`, `--base <carpeta>`.
4. **QA visual obligatorio** (ver abajo) → veredicto por imagen en
   `contenido/scraping/qa.json` (`{archivo, veredicto: 'aprobada'|'rechazada', motivo}`).
   Solo pasan a `contenido/catalogo.json` los productos con ≥ 2 imágenes aprobadas.
5. `node scripts/seed-contenido.js --preflight` y luego la carga normal.

### Estructura

```
contenido/imagenes/<slug-producto>/<orden>-<rol>.jpg
```

Ejemplo real:

```
contenido/imagenes/
├── nike-court-vision-low-next-nature-blanco/
│   ├── 01-principal.jpg     # esPrincipal: true (imageMap.MAIN)
│   ├── 02-lateral.jpg       # imageMap.LEFT/RGHT
│   └── 03-frente.jpg        # imageMap.FRNT (frente o talón según el modelo)
└── vans-old-skool-negro-blanco/
    ├── 01-principal.jpg
    ├── 02-lateral.jpg
    └── 03-frente.jpg
```

### QA visual (obligatorio antes de aceptar una imagen)

Criterios de aceptación: **solo el zapato** — sin personas, pies calzados, manos, ropa,
fondo urbano, marcas de agua ni texto promocional; toma profesional (producto centrado,
fondo blanco/claro uniforme, buena luz, encuadre completo).

Método usado:

- Hojas de contacto etiquetadas con ImageMagick:
  `montage -label '%t' <archivos> -tile 4x4 -geometry 380x380+6+6 hoja.jpg`
  (permite descartar en bloque).
- Lectura individual a resolución completa de las imágenes dudosas o atípicas.
- Verificación de dimensiones/peso (`identify`): actualmente 179/179 a 1000×1000,
  promedio ~60 KB, máximo 127 KB (≤ 500 KB ideal ✅).

### Trazabilidad (`fuente`, `origen`, `rol`)

Cada imagen del manifiesto declara:

- `fuente`: `"retailer"` (imagen descargada de Zappos en el flujo vigente; los flujos
  antiguos usaban `"unsplash"`/`"pexels"`).
- `origen`: URL de la **página del producto** en el retailer (no de la imagen), para
  poder auditar que el título y la foto corresponden al mismo producto.
- `rol`: `principal` | `lateral` | `frente` (el `01-principal.jpg` es el único con
  `esPrincipal: true`).

## Flujo por URL (soportado, ya no preferido)

Cada imagen puede ser remota: `url`, `alt`, `esPrincipal`, `fuente`, `fotoId`, `origen`.
`url` debe ser `https://` y el host debe estar permitido en el preflight
(`images.unsplash.com`, `plus.unsplash.com`, `images.pexels.com`, que coinciden con el
`img-src` de la CSP en `application.yml`). **No usar hosts fuera de esa lista sin
coordinar el cambio de CSP con `backend-senior`.** Las URLs externas fallaron como
fuente de contenido (fotos que no correspondían al producto), por eso el flujo vigente
es local.

## Convenciones

- **Slug**: exactamente el mismo `slug` del producto en `contenido/catalogo.json`
  (minúsculas, sin acentos, guiones: `nike-court-vision-low-next-nature-blanco`). Es la
  llave de idempotencia de la carga.
- **Cantidad**: 2-4 imágenes por producto (v2); exactamente **1** con `esPrincipal: true`.
- **Formatos locales**: `.jpg`, `.png` o `.webp`; ideal ≤ 500 KB y ~1000 px de ancho.
- **Alt (obligatorio)**: texto alternativo en español de **10 a 120 caracteres** que
  describe **solo lo visible** (tipo + color + ángulo). Ej.:
  `"Zapatilla Vans Old Skool en negro, vista lateral sobre fondo blanco"`.
  El preflight omite el producto si el `alt` falta o está fuera de rango.
- **Falla visual**: si un producto queda sin imagen, la tienda muestra
  `/content/images/product-placeholder.png`.

## Carga y validación

```bash
node scripts/seed-contenido.js --preflight           # solo valida el manifiesto (no toca la API)
node scripts/seed-contenido.js                       # local (http://localhost:8080)
node scripts/seed-contenido.js https://app.knstore.duckdns.org
node scripts/seed-contenido.js --force-images        # borra y recrea las imágenes del producto
```

Idempotencia de imágenes locales: el seed las identifica por `alt` dentro del producto
(el recurso API no expone bytes ni URL), así que re-ejecutarlo **no duplica**. Cambiar un
producto de imágenes por URL a imágenes locales exige `--force-images` (borra las
viejas y sube las nuevas). Variables: `KNSTORE_BASE_URL`, `KNSTORE_USERNAME`,
`KNSTORE_PASSWORD`.

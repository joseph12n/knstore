# Imágenes de producto — KN-Store

Carpeta de trabajo del agente `contenido-tienda`. Aquí se depositan las imágenes de
producto que se subirán a la tienda; el manifiesto `contenido/catalogo.json` las
describe y `scripts/seed-contenido.js` las carga contra la API.

## Dos formas de cargar imágenes (conviven en el mismo manifiesto)

Cada imagen del manifiesto puede ser **remota (URL)** o **local (archivo)**:

| Tipo    | Campos en el manifiesto                                   | ¿Requiere archivo en esta carpeta? | Cómo la sube el script                                   |
| ------- | --------------------------------------------------------- | ---------------------------------- | -------------------------------------------------------- |
| URL     | `url`, `alt`, `esPrincipal`, `fuente`, `fotoId`, `origen` | **No**                             | `POST /api/producto-imagens` con `imagenUrl` (sin bytes) |
| Archivo | `archivo`, `alt`, `esPrincipal` (o `principal` en v1)     | Sí: `contenido/imagenes/<slug>/…`  | Base64 del archivo en `imagen`                           |

Reglas para imagen **por URL**:

- `url` debe ser `https://` y el host debe estar permitido:
  `images.unsplash.com`, `plus.unsplash.com`, `images.pexels.com`
  (coinciden con el `img-src` de la CSP en `application.yml`; usar otro host
  exige coordinar el cambio de CSP con `backend-senior`).
- No hace falta depositar ningún archivo en esta carpeta: el producto puede
  cargarse completo solo con URLs.
- **Idempotencia**: la URL es la llave dentro del producto. Si la URL ya existe
  en el producto, el script la omite (o actualiza `alt`/`esPrincipal` si cambiaron);
  nunca duplica. `--force-images` borra y recrea.

### Trazabilidad de licencia (`fuente`, `fotoId`, `origen`)

Para imágenes por URL de fuentes con licencia libre:

- `fuente`: `"unsplash"` o `"pexels"`.
- `fotoId`: identificador de la foto en su CDN (Unsplash: parte después de
  `photo-` en la URL; permite reconstruir `url` con la plantilla
  `?w=800&q=80&fm=jpg&fit=crop`).
- `origen`: URL de la **página de la foto** (donde se verifica la licencia).
  Ej.: `https://unsplash.com/photos/a-pair-of-white-nike-air-force-sneakers-ORM7dQjvupI`.

## Estructura (solo si usas archivos locales)

```
contenido/imagenes/<slug-producto>/<orden>-<variante>.jpg
```

Ejemplo:

```
contenido/imagenes/
├── nike-air-force-1-07-blanco/
│   ├── 01-principal.jpg      # imagen principal (esPrincipal: true)
│   ├── 02-trasera.jpg
│   └── 03-suela.jpg
└── adidas-forum-low-blanco/
    └── 01-principal.jpg
```

## Convenciones

- **Slug**: exactamente el mismo `slug` del producto en `contenido/catalogo.json`
  (minúsculas, sin acentos, guiones: `nike-air-force-1-07-blanco`). Es la llave de
  idempotencia de la carga.
- **Cantidad**: 2-4 imágenes por producto (v2); exactamente **1** con `esPrincipal: true`.
- **Formatos locales**: `.jpg`, `.png` o `.webp`; ideal ≤ 500 KB y ~800 px de ancho.
- **Alt (obligatorio)**: texto alternativo descriptivo en español de **10 a 120
  caracteres** con producto + color + ángulo. Ej.:
  `"Nike Air Force 1 '07 en color blanco, par completo, vista lateral"`.
  El preflight omite el producto si el `alt` falta o está fuera de rango.
- **Falla visual**: si un producto queda sin imagen, la tienda muestra
  `/content/images/product-placeholder.png`.

## Carga y validación

```bash
node scripts/seed-contenido.js --preflight           # solo valida el manifiesto (no toca la API)
node scripts/seed-contenido.js                       # local (http://localhost:8080)
node scripts/seed-contenido.js https://app.knstore.duckdns.org
node scripts/seed-contenido.js --force-images        # reemplaza imágenes ya cargadas
```

El script es idempotente por `slug`: re-ejecutarlo nunca duplica productos ni imágenes.
Variables: `KNSTORE_BASE_URL`, `KNSTORE_USERNAME`, `KNSTORE_PASSWORD`.

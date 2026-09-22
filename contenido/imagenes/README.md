# Imágenes de producto — KN-Store

Carpeta de trabajo del agente `contenido-tienda`. Aquí se depositan las imágenes de
producto que se subirán a la tienda; el manifiesto `contenido/catalogo.json` las
describe y `scripts/seed-contenido.js` las carga contra la API.

## Estructura

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
- **Formatos**: `.jpg`, `.png` o `.webp`.
- **Peso y tamaño**: ideal ≤ 500 KB por archivo y ~800 px de ancho (así se cargan
  las imágenes de referencia del seed real). Optimiza antes de depositar.
- **Principal**: el archivo `01-principal.*` es el único marcado como principal.
  Toda imagen se referencia con su orden en el manifiesto.
- **Alt**: cada imagen necesita texto alternativo descriptivo en español (se toma
  del campo `alt` del manifiesto; accesibilidad obligatoria).
- **Falla visual**: si un producto queda sin imagen, la tienda muestra
  `/content/images/product-placeholder.png`.

## Carga

```bash
node scripts/seed-contenido.js                          # local (http://localhost:8080)
node scripts/seed-contenido.js https://app.knstore.duckdns.org
node scripts/seed-contenido.js --force-images           # reemplaza imágenes ya cargadas
```

El script es idempotente por `slug`: re-ejecutarlo nunca duplica productos.

/**
 * Script de inserción de datos demo para knstore.
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

const axios = require('axios');

const BASE_URL = process.argv[2] || process.env.KNSTORE_BASE_URL || 'http://localhost:8080';
const USERNAME = process.env.KNSTORE_USERNAME || 'admin';
const PASSWORD = process.env.KNSTORE_PASSWORD || 'admin';

const api = axios.create({
  baseURL: `${BASE_URL}/api`,
  headers: { 'Content-Type': 'application/json' },
});

let authToken = null;

async function authenticate() {
  const response = await api.post('/authenticate', {
    username: USERNAME,
    password: PASSWORD,
    rememberMe: true,
  });
  authToken = response.data.id_token;
  api.defaults.headers.common.Authorization = `Bearer ${authToken}`;
  console.log(`✅ Autenticado como ${USERNAME}`);
}

async function createOrUpdate(path, body) {
  try {
    const response = await api.post(path, body);
    console.log(`✅ Creado: ${path} -> ${response.data.id}`);
    return response.data;
  } catch (error) {
    if (error.response?.status === 400 && error.response?.data?.title?.includes('already')) {
      console.log(`⚠️  Ya existe: ${path}`);
      return body;
    }
    console.error(`❌ Error creando ${path}:`, error.response?.data || error.message);
    throw error;
  }
}

async function seed() {
  await authenticate();

  // Marca
  const marca = await createOrUpdate('/marcas', {
    id: 'marca-demo-knstore',
    nombre: 'Knstore',
    slug: 'knstore',
  });

  // Categoría IVA
  const categoriaIva = await createOrUpdate('/categoria-ivas', {
    id: 'iva-demo-19',
    nombre: 'IVA 19%',
    porcentaje: 19,
    estado: 'ACTIVO',
  });

  // Categorías
  const categoriaTest = await createOrUpdate('/categorias', {
    id: 'cat-demo-test',
    nombre: 'Test',
    slug: 'test',
    descripcion: 'Categoría de prueba para testeo del storefront',
    activo: true,
  });

  const categoriaElectronica = await createOrUpdate('/categorias', {
    id: 'cat-demo-electronica',
    nombre: 'Electrónica',
    slug: 'electronica',
    descripcion: 'Productos electrónicos',
    activo: true,
  });

  // Subcategorías
  const subTest = await createOrUpdate('/subcategorias', {
    id: 'sub-demo-test',
    nombre: 'Subcategoría Test',
    slug: 'sub-test',
    descripcion: 'Subcategoría de prueba',
    activo: true,
    categoria: { id: categoriaTest.id },
  });

  const subCelulares = await createOrUpdate('/subcategorias', {
    id: 'sub-demo-celulares',
    nombre: 'Celulares',
    slug: 'celulares',
    descripcion: 'Teléfonos móviles',
    activo: true,
    categoria: { id: categoriaElectronica.id },
  });

  // Productos
  const productos = [
    {
      id: 'prod-demo-test-test',
      nombre: 'Producto Test',
      slug: 'test-test',
      referencia: 'TEST-001',
      sku: 'SKU-TEST-001',
      color: 'Negro',
      talla: 'Única',
      codigoBarras: '1234567890123',
      unidadMedida: 'Unidad',
      descripcion: 'Producto de prueba para validar el carrito y checkout.',
      destacado: true,
      activo: true,
      precioVenta: 99000,
      precioCompra: 50000,
      stock: 100,
      categoria: categoriaTest,
      subcategoria: subTest,
    },
    {
      id: 'prod-demo-celular',
      nombre: 'Celular Demo',
      slug: 'celular-demo',
      referencia: 'CEL-001',
      sku: 'SKU-CEL-001',
      color: 'Azul',
      talla: '6.5 pulgadas',
      codigoBarras: '1234567890124',
      unidadMedida: 'Unidad',
      descripcion: 'Celular de prueba para testeo.',
      destacado: false,
      activo: true,
      precioVenta: 899000,
      precioCompra: 600000,
      stock: 50,
      categoria: categoriaElectronica,
      subcategoria: subCelulares,
    },
    {
      id: 'prod-demo-sin-stock',
      nombre: 'Producto Sin Stock',
      slug: 'sin-stock',
      referencia: 'NOSTK-001',
      sku: 'SKU-NOSTK-001',
      color: 'Rojo',
      talla: 'Única',
      codigoBarras: '1234567890125',
      unidadMedida: 'Unidad',
      descripcion: 'Producto agotado para probar validación de stock.',
      destacado: false,
      activo: true,
      precioVenta: 45000,
      precioCompra: 20000,
      stock: 0,
      categoria: categoriaTest,
      subcategoria: subTest,
    },
  ];

  for (const p of productos) {
    // Precio
    const precio = await createOrUpdate('/producto-precios', {
      precioCompra: p.precioCompra,
      precioVenta: p.precioVenta,
      precioAdicional: 0,
      ganancia: p.precioVenta - p.precioCompra,
    });

    // Inventario
    const inventario = await createOrUpdate('/producto-inventarios', {
      stock: p.stock,
      stockMinimo: 5,
      ubicacionBodega: 'BODEGA_PRINCIPAL',
      garantiaMeses: 12,
    });

    // Producto
    const producto = await createOrUpdate('/productos', {
      id: p.id,
      nombre: p.nombre,
      slug: p.slug,
      referencia: p.referencia,
      sku: p.sku,
      color: p.color,
      talla: p.talla,
      codigoBarras: p.codigoBarras,
      unidadMedida: p.unidadMedida,
      descripcion: p.descripcion,
      destacado: p.destacado,
      activo: p.activo,
      precio: { id: precio.id },
      inventario: { id: inventario.id },
      categoria: { id: p.categoria.id },
      subcategoria: { id: p.subcategoria.id },
      marca: { id: marca.id },
      categoriaIva: { id: categoriaIva.id },
    });

    // Imagen placeholder
    await createOrUpdate('/producto-imagens', {
      imagenContentType: 'image/png',
      imagenAlt: p.nombre,
      esPrincipal: true,
      producto: { id: producto.id },
    });

    console.log(`✅ Producto completo: ${p.nombre} (${p.slug})`);
  }

  console.log('\n🎉 Datos demo insertados correctamente.');
  console.log('URLs de prueba:');
  console.log(`  - Categoría:  ${BASE_URL}/categorias/test`);
  console.log(`  - Producto 1: ${BASE_URL}/productos/test-test`);
  console.log(`  - Producto 2: ${BASE_URL}/productos/celular-demo`);
  console.log(`  - Carrito:    ${BASE_URL}/carrito`);
}

seed().catch(error => {
  console.error('\n💥 Error insertando datos demo:', error.message);
  process.exit(1);
});

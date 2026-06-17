import etiquetaProducto from 'app/entities/etiqueta-producto/etiqueta-producto.reducer';
import direccion from 'app/entities/direccion/direccion.reducer';
import carrito from 'app/entities/carrito/carrito.reducer';
import categoria from 'app/entities/categoria/categoria.reducer';
import categoriaIVA from 'app/entities/categoria-iva/categoria-iva.reducer';
import cuenta from 'app/entities/cuenta/cuenta.reducer';
import pago from 'app/entities/pago/pago.reducer';
import envio from 'app/entities/envio/envio.reducer';
import factura from 'app/entities/factura/factura.reducer';
import itemCarrito from 'app/entities/item-carrito/item-carrito.reducer';
import itemPedido from 'app/entities/item-pedido/item-pedido.reducer';
import marca from 'app/entities/marca/marca.reducer';
import pedido from 'app/entities/pedido/pedido.reducer';
import producto from 'app/entities/producto/producto.reducer';
import productoImagen from 'app/entities/producto-imagen/producto-imagen.reducer';
import productoInventario from 'app/entities/producto-inventario/producto-inventario.reducer';
import productoPrecio from 'app/entities/producto-precio/producto-precio.reducer';
import subcategoria from 'app/entities/subcategoria/subcategoria.reducer';
import tipoDocumento from 'app/entities/tipo-documento/tipo-documento.reducer';
/* jhipster-needle-add-reducer-import - JHipster will add reducer here */

const entitiesReducers = {
  tipoDocumento,
  cuenta,
  categoria,
  subcategoria,
  marca,
  categoriaIVA,
  producto,
  productoPrecio,
  productoInventario,
  productoImagen,
  etiquetaProducto,
  direccion,
  carrito,
  itemCarrito,
  pedido,
  itemPedido,
  pago,
  envio,
  factura,
  /* jhipster-needle-add-reducer-combine - JHipster will add reducer here */
};

export default entitiesReducers;

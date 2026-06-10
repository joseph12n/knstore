import carrito from 'app/entities/carrito/carrito.reducer';
import categoria from 'app/entities/categoria/categoria.reducer';
import cuenta from 'app/entities/cuenta/cuenta.reducer';
import direccion from 'app/entities/direccion/direccion.reducer';
import envio from 'app/entities/envio/envio.reducer';
import etiquetaProducto from 'app/entities/etiqueta-producto/etiqueta-producto.reducer';
import factura from 'app/entities/factura/factura.reducer';
import itemCarrito from 'app/entities/item-carrito/item-carrito.reducer';
import itemPedido from 'app/entities/item-pedido/item-pedido.reducer';
import pago from 'app/entities/pago/pago.reducer';
import pedido from 'app/entities/pedido/pedido.reducer';
import producto from 'app/entities/producto/producto.reducer';
import subcategoria from 'app/entities/subcategoria/subcategoria.reducer';
import tipoDocumento from 'app/entities/tipo-documento/tipo-documento.reducer';
import varianteProducto from 'app/entities/variante-producto/variante-producto.reducer';
/* jhipster-needle-add-reducer-import - JHipster will add reducer here */

const entitiesReducers = {
  tipoDocumento,
  cuenta,
  categoria,
  subcategoria,
  producto,
  varianteProducto,
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

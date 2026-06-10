import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Carrito from './carrito';
import Categoria from './categoria';
import Cuenta from './cuenta';
import Direccion from './direccion';
import Envio from './envio';
import EtiquetaProducto from './etiqueta-producto';
import Factura from './factura';
import ItemCarrito from './item-carrito';
import ItemPedido from './item-pedido';
import Pago from './pago';
import Pedido from './pedido';
import Producto from './producto';
import Subcategoria from './subcategoria';
import TipoDocumento from './tipo-documento';
import VarianteProducto from './variante-producto';
/* jhipster-needle-add-route-import - JHipster will add routes here */

export default () => {
  return (
    <div>
      <ErrorBoundaryRoutes>
        {/* prettier-ignore */}
        <Route path="/tipo-documento/*" element={<TipoDocumento />} />
        <Route path="/cuenta/*" element={<Cuenta />} />
        <Route path="/categoria/*" element={<Categoria />} />
        <Route path="/subcategoria/*" element={<Subcategoria />} />
        <Route path="/producto/*" element={<Producto />} />
        <Route path="/variante-producto/*" element={<VarianteProducto />} />
        <Route path="/etiqueta-producto/*" element={<EtiquetaProducto />} />
        <Route path="/direccion/*" element={<Direccion />} />
        <Route path="/carrito/*" element={<Carrito />} />
        <Route path="/item-carrito/*" element={<ItemCarrito />} />
        <Route path="/pedido/*" element={<Pedido />} />
        <Route path="/item-pedido/*" element={<ItemPedido />} />
        <Route path="/pago/*" element={<Pago />} />
        <Route path="/envio/*" element={<Envio />} />
        <Route path="/factura/*" element={<Factura />} />
        {/* jhipster-needle-add-route-path - JHipster will add routes here */}
      </ErrorBoundaryRoutes>
    </div>
  );
};

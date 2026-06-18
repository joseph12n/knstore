import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';
import PrivateRoute from 'app/shared/auth/private-route';
import { Authority } from 'app/shared/jhipster/constants';

import EtiquetaProducto from './etiqueta-producto';
import Direccion from './direccion';
import Carrito from './carrito';
import Categoria from './categoria';
import CategoriaIVA from './categoria-iva';
import Cuenta from './cuenta';
import Pago from './pago';
import Envio from './envio';
import Factura from './factura';
import ItemCarrito from './item-carrito';
import ItemPedido from './item-pedido';
import Marca from './marca';
import Pedido from './pedido';
import Producto from './producto';
import ProductoImagen from './producto-imagen';
import ProductoInventario from './producto-inventario';
import ProductoPrecio from './producto-precio';
import Subcategoria from './subcategoria';
import TipoDocumento from './tipo-documento';
/* jhipster-needle-add-route-import - JHipster will add routes here */

export default () => {
  return (
    <div>
      <ErrorBoundaryRoutes>
        {/* prettier-ignore */}
        <Route path="/tipo-documento/*" element={<PrivateRoute hasAnyAuthorities={[Authority.ADMIN, Authority.MANAGER]}><TipoDocumento /></PrivateRoute>} />
        <Route
          path="/cuenta/*"
          element={
            <PrivateRoute hasAnyAuthorities={[Authority.ADMIN, Authority.MANAGER, Authority.CLIENTE]}>
              <Cuenta />
            </PrivateRoute>
          }
        />
        <Route
          path="/categoria/*"
          element={
            <PrivateRoute hasAnyAuthorities={[Authority.ADMIN, Authority.MANAGER]}>
              <Categoria />
            </PrivateRoute>
          }
        />
        <Route
          path="/subcategoria/*"
          element={
            <PrivateRoute hasAnyAuthorities={[Authority.ADMIN, Authority.MANAGER]}>
              <Subcategoria />
            </PrivateRoute>
          }
        />
        <Route
          path="/marca/*"
          element={
            <PrivateRoute hasAnyAuthorities={[Authority.ADMIN, Authority.MANAGER]}>
              <Marca />
            </PrivateRoute>
          }
        />
        <Route
          path="/categoria-iva/*"
          element={
            <PrivateRoute hasAnyAuthorities={[Authority.ADMIN, Authority.MANAGER]}>
              <CategoriaIVA />
            </PrivateRoute>
          }
        />
        <Route
          path="/producto/*"
          element={
            <PrivateRoute hasAnyAuthorities={[Authority.ADMIN, Authority.MANAGER]}>
              <Producto />
            </PrivateRoute>
          }
        />
        <Route
          path="/producto-precio/*"
          element={
            <PrivateRoute hasAnyAuthorities={[Authority.ADMIN, Authority.MANAGER]}>
              <ProductoPrecio />
            </PrivateRoute>
          }
        />
        <Route
          path="/producto-inventario/*"
          element={
            <PrivateRoute hasAnyAuthorities={[Authority.ADMIN, Authority.MANAGER]}>
              <ProductoInventario />
            </PrivateRoute>
          }
        />
        <Route
          path="/producto-imagen/*"
          element={
            <PrivateRoute hasAnyAuthorities={[Authority.ADMIN, Authority.MANAGER]}>
              <ProductoImagen />
            </PrivateRoute>
          }
        />
        <Route
          path="/etiqueta-producto/*"
          element={
            <PrivateRoute hasAnyAuthorities={[Authority.ADMIN, Authority.MANAGER]}>
              <EtiquetaProducto />
            </PrivateRoute>
          }
        />
        <Route
          path="/direccion/*"
          element={
            <PrivateRoute hasAnyAuthorities={[Authority.ADMIN, Authority.MANAGER, Authority.CLIENTE]}>
              <Direccion />
            </PrivateRoute>
          }
        />
        <Route
          path="/carrito/*"
          element={
            <PrivateRoute hasAnyAuthorities={[Authority.ADMIN, Authority.MANAGER, Authority.CLIENTE]}>
              <Carrito />
            </PrivateRoute>
          }
        />
        <Route
          path="/item-carrito/*"
          element={
            <PrivateRoute hasAnyAuthorities={[Authority.ADMIN, Authority.MANAGER, Authority.CLIENTE]}>
              <ItemCarrito />
            </PrivateRoute>
          }
        />
        <Route
          path="/pedido/*"
          element={
            <PrivateRoute hasAnyAuthorities={[Authority.ADMIN, Authority.MANAGER, Authority.CLIENTE]}>
              <Pedido />
            </PrivateRoute>
          }
        />
        <Route
          path="/item-pedido/*"
          element={
            <PrivateRoute hasAnyAuthorities={[Authority.ADMIN, Authority.MANAGER, Authority.CLIENTE]}>
              <ItemPedido />
            </PrivateRoute>
          }
        />
        <Route
          path="/pago/*"
          element={
            <PrivateRoute hasAnyAuthorities={[Authority.ADMIN, Authority.MANAGER, Authority.CLIENTE]}>
              <Pago />
            </PrivateRoute>
          }
        />
        <Route
          path="/envio/*"
          element={
            <PrivateRoute hasAnyAuthorities={[Authority.ADMIN, Authority.MANAGER, Authority.CLIENTE]}>
              <Envio />
            </PrivateRoute>
          }
        />
        <Route
          path="/factura/*"
          element={
            <PrivateRoute hasAnyAuthorities={[Authority.ADMIN, Authority.MANAGER, Authority.CLIENTE]}>
              <Factura />
            </PrivateRoute>
          }
        />
        {/* jhipster-needle-add-route-path - JHipster will add routes here */}
      </ErrorBoundaryRoutes>
    </div>
  );
};

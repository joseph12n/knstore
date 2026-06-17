import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import ProductoInventario from './producto-inventario';
import ProductoInventarioDeleteDialog from './producto-inventario-delete-dialog';
import ProductoInventarioDetail from './producto-inventario-detail';
import ProductoInventarioUpdate from './producto-inventario-update';

const ProductoInventarioRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<ProductoInventario />} />
    <Route path="new" element={<ProductoInventarioUpdate />} />
    <Route path=":id">
      <Route index element={<ProductoInventarioDetail />} />
      <Route path="edit" element={<ProductoInventarioUpdate />} />
      <Route path="delete" element={<ProductoInventarioDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default ProductoInventarioRoutes;

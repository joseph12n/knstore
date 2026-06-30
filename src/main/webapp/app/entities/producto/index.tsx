import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Producto from './producto';
import ProductoDeleteDialog from './producto-delete-dialog';
import ProductoDetail from './producto-detail';
import ProductoUpdate from './producto-update';

const ProductoRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Producto />} />
    <Route path="new" element={<ProductoUpdate />} />
    <Route path=":id">
      <Route index element={<ProductoDetail />} />
      <Route path="edit" element={<ProductoUpdate />} />
      <Route path="delete" element={<ProductoDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default ProductoRoutes;

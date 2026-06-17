import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import ProductoPrecio from './producto-precio';
import ProductoPrecioDeleteDialog from './producto-precio-delete-dialog';
import ProductoPrecioDetail from './producto-precio-detail';
import ProductoPrecioUpdate from './producto-precio-update';

const ProductoPrecioRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<ProductoPrecio />} />
    <Route path="new" element={<ProductoPrecioUpdate />} />
    <Route path=":id">
      <Route index element={<ProductoPrecioDetail />} />
      <Route path="edit" element={<ProductoPrecioUpdate />} />
      <Route path="delete" element={<ProductoPrecioDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default ProductoPrecioRoutes;

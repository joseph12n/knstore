import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import ProductoImagen from './producto-imagen';
import ProductoImagenDeleteDialog from './producto-imagen-delete-dialog';
import ProductoImagenDetail from './producto-imagen-detail';
import ProductoImagenUpdate from './producto-imagen-update';

const ProductoImagenRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<ProductoImagen />} />
    <Route path="new" element={<ProductoImagenUpdate />} />
    <Route path=":id">
      <Route index element={<ProductoImagenDetail />} />
      <Route path="edit" element={<ProductoImagenUpdate />} />
      <Route path="delete" element={<ProductoImagenDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default ProductoImagenRoutes;

import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import VarianteProducto from './variante-producto';
import VarianteProductoDeleteDialog from './variante-producto-delete-dialog';
import VarianteProductoDetail from './variante-producto-detail';
import VarianteProductoUpdate from './variante-producto-update';

const VarianteProductoRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<VarianteProducto />} />
    <Route path="new" element={<VarianteProductoUpdate />} />
    <Route path=":id">
      <Route index element={<VarianteProductoDetail />} />
      <Route path="edit" element={<VarianteProductoUpdate />} />
      <Route path="delete" element={<VarianteProductoDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default VarianteProductoRoutes;

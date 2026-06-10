import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import EtiquetaProducto from './etiqueta-producto';
import EtiquetaProductoDeleteDialog from './etiqueta-producto-delete-dialog';
import EtiquetaProductoDetail from './etiqueta-producto-detail';
import EtiquetaProductoUpdate from './etiqueta-producto-update';

const EtiquetaProductoRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<EtiquetaProducto />} />
    <Route path="new" element={<EtiquetaProductoUpdate />} />
    <Route path=":id">
      <Route index element={<EtiquetaProductoDetail />} />
      <Route path="edit" element={<EtiquetaProductoUpdate />} />
      <Route path="delete" element={<EtiquetaProductoDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default EtiquetaProductoRoutes;

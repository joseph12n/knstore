import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import TipoDocumento from './tipo-documento';
import TipoDocumentoDeleteDialog from './tipo-documento-delete-dialog';
import TipoDocumentoDetail from './tipo-documento-detail';
import TipoDocumentoUpdate from './tipo-documento-update';

const TipoDocumentoRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<TipoDocumento />} />
    <Route path="new" element={<TipoDocumentoUpdate />} />
    <Route path=":id">
      <Route index element={<TipoDocumentoDetail />} />
      <Route path="edit" element={<TipoDocumentoUpdate />} />
      <Route path="delete" element={<TipoDocumentoDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default TipoDocumentoRoutes;

import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Cuenta from './cuenta';
import CuentaDeleteDialog from './cuenta-delete-dialog';
import CuentaDetail from './cuenta-detail';
import CuentaUpdate from './cuenta-update';

const CuentaRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Cuenta />} />
    <Route path="new" element={<CuentaUpdate />} />
    <Route path=":id">
      <Route index element={<CuentaDetail />} />
      <Route path="edit" element={<CuentaUpdate />} />
      <Route path="delete" element={<CuentaDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default CuentaRoutes;

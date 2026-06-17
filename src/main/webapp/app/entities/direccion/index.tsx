import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Direccion from './direccion';
import DireccionDeleteDialog from './direccion-delete-dialog';
import DireccionDetail from './direccion-detail';
import DireccionUpdate from './direccion-update';

const DireccionRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Direccion />} />
    <Route path="new" element={<DireccionUpdate />} />
    <Route path=":id">
      <Route index element={<DireccionDetail />} />
      <Route path="edit" element={<DireccionUpdate />} />
      <Route path="delete" element={<DireccionDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default DireccionRoutes;

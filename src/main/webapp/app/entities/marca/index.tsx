import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Marca from './marca';
import MarcaDeleteDialog from './marca-delete-dialog';
import MarcaDetail from './marca-detail';
import MarcaUpdate from './marca-update';

const MarcaRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Marca />} />
    <Route path="new" element={<MarcaUpdate />} />
    <Route path=":id">
      <Route index element={<MarcaDetail />} />
      <Route path="edit" element={<MarcaUpdate />} />
      <Route path="delete" element={<MarcaDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default MarcaRoutes;

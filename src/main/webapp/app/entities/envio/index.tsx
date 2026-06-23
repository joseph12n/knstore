import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Envio from './envio';
import EnvioDeleteDialog from './envio-delete-dialog';
import EnvioDetail from './envio-detail';
import EnvioUpdate from './envio-update';

const EnvioRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Envio />} />
    <Route path="new" element={<EnvioUpdate />} />
    <Route path=":id">
      <Route index element={<EnvioDetail />} />
      <Route path="edit" element={<EnvioUpdate />} />
      <Route path="delete" element={<EnvioDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default EnvioRoutes;

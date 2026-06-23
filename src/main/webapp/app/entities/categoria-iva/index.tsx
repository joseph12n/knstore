import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import CategoriaIVA from './categoria-iva';
import CategoriaIVADeleteDialog from './categoria-iva-delete-dialog';
import CategoriaIVADetail from './categoria-iva-detail';
import CategoriaIVAUpdate from './categoria-iva-update';

const CategoriaIVARoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<CategoriaIVA />} />
    <Route path="new" element={<CategoriaIVAUpdate />} />
    <Route path=":id">
      <Route index element={<CategoriaIVADetail />} />
      <Route path="edit" element={<CategoriaIVAUpdate />} />
      <Route path="delete" element={<CategoriaIVADeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default CategoriaIVARoutes;

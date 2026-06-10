import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Categoria from './categoria';
import CategoriaDeleteDialog from './categoria-delete-dialog';
import CategoriaDetail from './categoria-detail';
import CategoriaUpdate from './categoria-update';

const CategoriaRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Categoria />} />
    <Route path="new" element={<CategoriaUpdate />} />
    <Route path=":id">
      <Route index element={<CategoriaDetail />} />
      <Route path="edit" element={<CategoriaUpdate />} />
      <Route path="delete" element={<CategoriaDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default CategoriaRoutes;

import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Subcategoria from './subcategoria';
import SubcategoriaDeleteDialog from './subcategoria-delete-dialog';
import SubcategoriaDetail from './subcategoria-detail';
import SubcategoriaUpdate from './subcategoria-update';

const SubcategoriaRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Subcategoria />} />
    <Route path="new" element={<SubcategoriaUpdate />} />
    <Route path=":id">
      <Route index element={<SubcategoriaDetail />} />
      <Route path="edit" element={<SubcategoriaUpdate />} />
      <Route path="delete" element={<SubcategoriaDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default SubcategoriaRoutes;

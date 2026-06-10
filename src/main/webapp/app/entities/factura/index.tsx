import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Factura from './factura';
import FacturaDeleteDialog from './factura-delete-dialog';
import FacturaDetail from './factura-detail';
import FacturaUpdate from './factura-update';

const FacturaRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Factura />} />
    <Route path="new" element={<FacturaUpdate />} />
    <Route path=":id">
      <Route index element={<FacturaDetail />} />
      <Route path="edit" element={<FacturaUpdate />} />
      <Route path="delete" element={<FacturaDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default FacturaRoutes;

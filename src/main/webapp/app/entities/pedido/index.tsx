import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Pedido from './pedido';
import PedidoDeleteDialog from './pedido-delete-dialog';
import PedidoDetail from './pedido-detail';
import PedidoUpdate from './pedido-update';

const PedidoRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Pedido />} />
    <Route path="new" element={<PedidoUpdate />} />
    <Route path=":id">
      <Route index element={<PedidoDetail />} />
      <Route path="edit" element={<PedidoUpdate />} />
      <Route path="delete" element={<PedidoDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default PedidoRoutes;

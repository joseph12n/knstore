import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import ItemPedido from './item-pedido';
import ItemPedidoDeleteDialog from './item-pedido-delete-dialog';
import ItemPedidoDetail from './item-pedido-detail';
import ItemPedidoUpdate from './item-pedido-update';

const ItemPedidoRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<ItemPedido />} />
    <Route path="new" element={<ItemPedidoUpdate />} />
    <Route path=":id">
      <Route index element={<ItemPedidoDetail />} />
      <Route path="edit" element={<ItemPedidoUpdate />} />
      <Route path="delete" element={<ItemPedidoDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default ItemPedidoRoutes;

import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import ItemCarrito from './item-carrito';
import ItemCarritoDeleteDialog from './item-carrito-delete-dialog';
import ItemCarritoDetail from './item-carrito-detail';
import ItemCarritoUpdate from './item-carrito-update';

const ItemCarritoRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<ItemCarrito />} />
    <Route path="new" element={<ItemCarritoUpdate />} />
    <Route path=":id">
      <Route index element={<ItemCarritoDetail />} />
      <Route path="edit" element={<ItemCarritoUpdate />} />
      <Route path="delete" element={<ItemCarritoDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default ItemCarritoRoutes;

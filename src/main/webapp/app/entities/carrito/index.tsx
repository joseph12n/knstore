import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Carrito from './carrito';
import CarritoDeleteDialog from './carrito-delete-dialog';
import CarritoDetail from './carrito-detail';
import CarritoUpdate from './carrito-update';

const CarritoRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Carrito />} />
    <Route path="new" element={<CarritoUpdate />} />
    <Route path=":id">
      <Route index element={<CarritoDetail />} />
      <Route path="edit" element={<CarritoUpdate />} />
      <Route path="delete" element={<CarritoDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default CarritoRoutes;

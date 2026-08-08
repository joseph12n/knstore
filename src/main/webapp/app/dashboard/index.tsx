import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';
import PrivateRoute from 'app/shared/auth/private-route';
import { Authority } from 'app/shared/jhipster/constants';
import AdministrationRoutes from 'app/modules/administration';
import EntitiesRoutes from 'app/entities/routes';
import AdminOrdersPage from './pages/AdminOrdersPage';
import AdminShipmentsPage from './pages/AdminShipmentsPage';
import AdminRefundsPage from './pages/AdminRefundsPage';

const Admin = () => (
  <div>
    <ErrorBoundaryRoutes>
      {/* Operación diaria: pedidos, envíos y reembolsos */}
      <Route
        path="operacion/pedidos"
        element={
          <PrivateRoute hasAnyAuthorities={[Authority.ADMIN, Authority.MANAGER]}>
            <AdminOrdersPage />
          </PrivateRoute>
        }
      />
      <Route
        path="operacion/envios"
        element={
          <PrivateRoute hasAnyAuthorities={[Authority.ADMIN, Authority.MANAGER]}>
            <AdminShipmentsPage />
          </PrivateRoute>
        }
      />
      <Route
        path="operacion/reembolsos"
        element={
          <PrivateRoute hasAnyAuthorities={[Authority.ADMIN]}>
            <AdminRefundsPage />
          </PrivateRoute>
        }
      />
      {/* Rutas clásicas de JHipster */}
      <Route path="*" element={<AdministrationRoutes />} />
    </ErrorBoundaryRoutes>
  </div>
);

export { Admin, EntitiesRoutes };
export default Admin;

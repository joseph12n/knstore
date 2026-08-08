import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';
import PrivateRoute from 'app/shared/auth/private-route';
import { Authority } from 'app/shared/jhipster/constants';

import Configuration from './configuration/configuration';
import Docs from './docs/docs';
import Health from './health/health';
import Logs from './logs/logs';
import Metrics from './metrics/metrics';
import UserManagement from './user-management';

const AdministrationRoutes = () => (
  <div>
    <ErrorBoundaryRoutes>
      <Route
        path="user-management/*"
        element={
          <PrivateRoute hasAnyAuthorities={[Authority.ADMIN]}>
            <UserManagement />
          </PrivateRoute>
        }
      />
      <Route path="health" element={<Health />} />
      <Route path="metrics" element={<Metrics />} />
      <Route
        path="configuration"
        element={
          <PrivateRoute hasAnyAuthorities={[Authority.ADMIN]}>
            <Configuration />
          </PrivateRoute>
        }
      />
      <Route
        path="logs"
        element={
          <PrivateRoute hasAnyAuthorities={[Authority.ADMIN]}>
            <Logs />
          </PrivateRoute>
        }
      />
      <Route path="docs" element={<Docs />} />
    </ErrorBoundaryRoutes>
  </div>
);

export default AdministrationRoutes;

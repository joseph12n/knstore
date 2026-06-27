import React, { Suspense } from 'react';
import { Route } from 'react-router';

import Activate from 'app/modules/account/activate/activate';
import PasswordResetFinish from 'app/modules/account/password-reset/finish/password-reset-finish';
import PasswordResetInit from 'app/modules/account/password-reset/init/password-reset-init';
import Register from 'app/modules/account/register/register';
import Login from 'app/modules/login/login';
import Logout from 'app/modules/login/logout';
import PrivateRoute from 'app/shared/auth/private-route';
import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';
import PageNotFound from 'app/shared/error/page-not-found';
import { Authority } from 'app/shared/jhipster/constants';
import LandingLayout from 'app/landing/components/LandingLayout';
import StorefrontRoutes from 'app/landing/routes/StorefrontRoutes';
import AccountRoutes from 'app/landing/routes/AccountRoutes';
import { Admin, EntitiesRoutes } from 'app/dashboard';

const loading = <div>loading ...</div>;

const Account = React.lazy(() => import(/* webpackChunkName: "account" */ 'app/modules/account'));

const ADMIN_AUTHORITIES = [Authority.ADMIN];
const STORE_AUTHORITIES = [Authority.ADMIN, Authority.MANAGER, Authority.CLIENTE, Authority.USER];
const ACCOUNT_AUTHORITIES = [Authority.ADMIN, Authority.MANAGER];

const AppRoutes = () => (
  <div className="view-routes">
    <Suspense fallback={loading}>
      <ErrorBoundaryRoutes>
        {/* Landing / tienda pública */}
        <Route element={<LandingLayout />}>
          <Route index element={<StorefrontRoutes />} />
          <Route path="categorias/:categoriaSlug/:subcategoriaSlug?" element={<StorefrontRoutes />} />
          <Route path="productos/:slug" element={<StorefrontRoutes />} />
          <Route path="buscar" element={<StorefrontRoutes />} />
          <Route path="carrito" element={<StorefrontRoutes />} />
          <Route
            path="checkout"
            element={
              <PrivateRoute hasAnyAuthorities={STORE_AUTHORITIES}>
                <StorefrontRoutes />
              </PrivateRoute>
            }
          />
          {/*
            NOTA: Esta ruta tiene prioridad sobre la entidad CRUD /cuenta/* generada por JHipster.
            El CRUD admin de la entidad Cuenta queda oculto para usuarios que accedan por URL /cuenta.
            Los administradores pueden gestionar cuentas desde el menú Entities > Cuenta, que apunta a la misma ruta.
          */}
          <Route
            path="cuenta/*"
            element={
              <PrivateRoute hasAnyAuthorities={STORE_AUTHORITIES}>
                <AccountRoutes />
              </PrivateRoute>
            }
          />
        </Route>

        {/* Autenticación */}
        <Route path="login" element={<Login />} />
        <Route path="logout" element={<Logout />} />
        <Route path="account">
          {/* Panel de cuenta clásico de JHipster: solo ADMIN/MANAGER.
              Los clientes usan su propio panel en /cuenta. */}
          <Route
            path="*"
            element={
              <PrivateRoute hasAnyAuthorities={ACCOUNT_AUTHORITIES}>
                <Account />
              </PrivateRoute>
            }
          />
          <Route path="register" element={<Register />} />
          <Route path="activate" element={<Activate />} />
          <Route path="reset">
            <Route path="request" element={<PasswordResetInit />} />
            <Route path="finish" element={<PasswordResetFinish />} />
          </Route>
        </Route>

        {/* Dashboard administrativo JHipster - solo ADMIN/MANAGER */}
        <Route
          path="admin/*"
          element={
            <PrivateRoute hasAnyAuthorities={ADMIN_AUTHORITIES}>
              <Admin />
            </PrivateRoute>
          }
        />

        {/* CRUD de entidades JHipster - cada ruta interna define sus propios permisos */}
        <Route path="*" element={<EntitiesRoutes />} />
        <Route path="*" element={<PageNotFound />} />
      </ErrorBoundaryRoutes>
    </Suspense>
  </div>
);

export default AppRoutes;

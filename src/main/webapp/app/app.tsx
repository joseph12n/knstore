import 'react-toastify/dist/ReactToastify.css';
import './app.scss';
import 'app/landing/styles/storefront.scss';
import 'app/config/dayjs';

import React, { useEffect } from 'react';
import { Card } from 'react-bootstrap';
import { BrowserRouter, useLocation } from 'react-router';

import { ToastContainer } from 'react-toastify';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import AppRoutes from 'app/routes';
import { hasAnyAuthority } from 'app/shared/auth/private-route';
import ErrorBoundary from 'app/shared/error/error-boundary';
import { Authority } from 'app/shared/jhipster/constants';
import Footer from 'app/shared/layout/footer/footer';
import Header from 'app/shared/layout/header/header';
import { getProfile } from 'app/shared/reducers/application-profile';
import { getSession } from 'app/shared/reducers/authentication';

const baseHref = document.querySelector('base')!.getAttribute('href')!.replace(/\/$/, '');

const AppContent = () => {
  const location = useLocation();
  // Rutas que pertenecen al dashboard administrativo generado por JHipster.
  const isAdminRoute = location.pathname.startsWith('/admin');
  // Las rutas de entidades CRUD autogeneradas también usan el layout admin.
  const isEntityCrudRoute =
    !isAdminRoute &&
    !location.pathname.startsWith('/cuenta') &&
    !location.pathname.startsWith('/carrito') &&
    !location.pathname.startsWith('/checkout') &&
    !location.pathname.startsWith('/categorias') &&
    !location.pathname.startsWith('/productos') &&
    !location.pathname.startsWith('/buscar') &&
    location.pathname !== '/' &&
    location.pathname !== '/login' &&
    location.pathname !== '/logout' &&
    !location.pathname.startsWith('/account');

  const isStorefrontRoute = !isAdminRoute && !isEntityCrudRoute;

  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getSession());
    dispatch(getProfile());
  }, []);

  const isAuthenticated = useAppSelector(state => state.authentication.isAuthenticated);
  const isAdmin = useAppSelector(state => hasAnyAuthority(state.authentication.account.authorities, [Authority.ADMIN]));
  const isManager = useAppSelector(state => hasAnyAuthority(state.authentication.account.authorities, [Authority.MANAGER]));
  const isCliente = useAppSelector(state => hasAnyAuthority(state.authentication.account.authorities, [Authority.CLIENTE]));
  const ribbonEnv = useAppSelector(state => state.applicationProfile.ribbonEnv);
  const isInProduction = useAppSelector(state => state.applicationProfile.inProduction);
  const isOpenAPIEnabled = useAppSelector(state => state.applicationProfile.isOpenAPIEnabled);

  if (isStorefrontRoute) {
    return (
      <div className="app-container storefront-app">
        <ToastContainer position="top-right" className="toastify-container" toastClassName="toastify-toast" />
        <ErrorBoundary>
          <AppRoutes />
        </ErrorBoundary>
      </div>
    );
  }

  const paddingTop = '60px';
  return (
    <div className="app-container" style={{ paddingTop }}>
      <ToastContainer position="top-left" className="toastify-container" toastClassName="toastify-toast" />
      <ErrorBoundary>
        <Header
          isAuthenticated={isAuthenticated}
          isAdmin={isAdmin}
          isManager={isManager}
          isCliente={isCliente}
          ribbonEnv={ribbonEnv}
          isInProduction={isInProduction}
          isOpenAPIEnabled={isOpenAPIEnabled}
        />
      </ErrorBoundary>
      <div className="container-fluid view-container" id="app-view-container">
        <Card className="jh-card">
          <ErrorBoundary>
            <AppRoutes />
          </ErrorBoundary>
        </Card>
        <Footer />
      </div>
    </div>
  );
};

export const App = () => (
  <BrowserRouter basename={baseHref}>
    <AppContent />
  </BrowserRouter>
);

export default App;

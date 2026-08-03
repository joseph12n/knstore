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

// Layouts:
// - Storefront: la tienda pública y el panel del cliente usan el header/footer de storefront.
// - Admin: el dashboard /admin y los CRUD de entidades (incluyendo /cuenta) usan el layout JHipster clásico.
type AppLayout = 'storefront' | 'admin';

// Rutas públicas de la tienda y del panel del cliente. Deben mantenerse sincronizadas con routes.tsx.
const STOREFRONT_PATHS = [
  '/',
  '/categorias',
  '/productos',
  '/buscar',
  '/carrito',
  '/checkout',
  '/mi-cuenta',
  '/login',
  '/logout',
  '/account',
];

// Determina el layout que debe usar la ruta actual. El fallback es el layout admin.
const resolveLayout = (pathname: string): AppLayout => {
  if (pathname.startsWith('/admin')) {
    return 'admin';
  }

  const isStorefront = STOREFRONT_PATHS.some(path => (path === '/' ? pathname === '/' || pathname === '' : pathname.startsWith(path)));
  return isStorefront ? 'storefront' : 'admin';
};

const AppContent = () => {
  const location = useLocation();
  const layout = resolveLayout(location.pathname);

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

  if (layout === 'storefront') {
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

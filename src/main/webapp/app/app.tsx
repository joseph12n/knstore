import 'react-toastify/dist/ReactToastify.css';
import './app.scss';
import 'app/landing/styles/storefront.scss';
import 'app/dashboard/admin.scss';
import 'app/config/dayjs';

import React, { useEffect } from 'react';
import { BrowserRouter, useLocation } from 'react-router';

import { ToastContainer } from 'react-toastify';

import { useAppDispatch } from 'app/config/store';
import AppRoutes from 'app/routes';
import AdminLayout from 'app/dashboard/layout/AdminLayout';
import DesktopOnlyNotice from 'app/landing/components/DesktopOnlyNotice';
import { useIsMobileView } from 'app/landing/hooks/useIsMobileView';
import ErrorBoundary from 'app/shared/error/error-boundary';
import { getProfile } from 'app/shared/reducers/application-profile';
import { getSession } from 'app/shared/reducers/authentication';

const baseHref = document.querySelector('base')!.getAttribute('href')!.replace(/\/$/, '');

// Layouts:
// - Storefront: la tienda pública y el panel del cliente usan el header/footer de storefront.
// - Admin: el panel administrativo usa el shell propio (AdminLayout) con los tokens del storefront.
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
  const isMobile = useIsMobileView();

  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getSession());
    dispatch(getProfile());
  }, []);

  // En viewports móviles el panel admin se reemplaza por un aviso a pantalla
  // completa: sin AdminLayout (sidebar) y sin montar AppRoutes.
  if (layout === 'admin' && isMobile) {
    return (
      <div className="app-container storefront-app">
        <ToastContainer position="top-right" className="toastify-container" toastClassName="toastify-toast" />
        <ErrorBoundary>
          <DesktopOnlyNotice />
        </ErrorBoundary>
      </div>
    );
  }

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

  return (
    <div className="app-container">
      <ToastContainer position="top-left" className="toastify-container" toastClassName="toastify-toast" />
      <ErrorBoundary>
        <AdminLayout>
          <AppRoutes />
        </AdminLayout>
      </ErrorBoundary>
    </div>
  );
};

export const App = () => (
  <BrowserRouter basename={baseHref}>
    <AppContent />
  </BrowserRouter>
);

export default App;

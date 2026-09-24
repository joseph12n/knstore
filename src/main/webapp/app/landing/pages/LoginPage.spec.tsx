import React from 'react';
import { render, screen } from '@testing-library/react';
import { MemoryRouter, Route, Routes, useLocation } from 'react-router';
import { Provider } from 'react-redux';
import { beforeEach, describe, expect, it, vi } from 'vitest';

import getStore from 'app/config/store';
import { Authority } from 'app/shared/jhipster/constants';
import { getAccount, logoutSession } from 'app/shared/reducers/authentication';

import { LoginPage } from './LoginPage';

const mocks = vi.hoisted(() => ({ isMobile: false }));

vi.mock('app/landing/hooks/useIsMobileView', () => ({
  MOBILE_VIEW_QUERY: '(max-width: 991.98px)',
  useIsMobileView: () => mocks.isMobile,
}));

/** Sonda que revela la ruta alcanzada y si el redirect lleva el aviso de panel. */
const Sonda = ({ label }: { label: string }) => {
  const location = useLocation();
  const aviso = (location.state as { avisoPanelEscritorio?: boolean } | null)?.avisoPanelEscritorio;
  return <div>{aviso ? `${label} [aviso]` : label}</div>;
};

interface OpcionesLogin {
  from?: { pathname: string };
  authorities?: string[];
}

const renderLogin = ({ from, authorities }: OpcionesLogin = {}) => {
  const store = getStore();
  if (authorities) {
    store.dispatch(getAccount.fulfilled({ data: { activated: true, login: 'admin', authorities } }, 'req-rol'));
  }

  return render(
    <Provider store={store}>
      <MemoryRouter initialEntries={[{ pathname: '/login', state: from ? { from } : undefined }]}>
        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route path="/" element={<Sonda label="HOME" />} />
          <Route path="/admin/user-management" element={<Sonda label="ADMIN_USER_MANAGEMENT" />} />
          <Route path="/admin/*" element={<Sonda label="ADMIN_PANEL" />} />
          <Route path="/mi-cuenta" element={<Sonda label="MI_CUENTA" />} />
        </Routes>
      </MemoryRouter>
    </Provider>,
  );
};

describe('LoginPage', () => {
  beforeEach(() => {
    window.localStorage.clear();
    getStore().dispatch(logoutSession());
    mocks.isMobile = false;
  });

  it('renderiza el formulario de inicio de sesión con usuario, contraseña y submit', () => {
    render(
      <Provider store={getStore()}>
        <MemoryRouter>
          <LoginPage />
        </MemoryRouter>
      </Provider>,
    );

    expect(screen.getByText('Inicia sesión')).toBeTruthy();
    expect(document.querySelector('input[data-cy="username"]')).toBeTruthy();
    expect(document.querySelector('input[data-cy="password"]')).toBeTruthy();
    expect(document.querySelector('button[data-cy="submit"]')).toBeTruthy();
  });

  it('en móvil redirige al home con el aviso cuando el rol es ADMIN', () => {
    mocks.isMobile = true;

    renderLogin({ authorities: [Authority.ADMIN] });

    expect(screen.getByText('HOME [aviso]')).toBeTruthy();
    expect(screen.queryByText('ADMIN_USER_MANAGEMENT')).toBeNull();
  });

  it('en móvil redirige al home con el aviso cuando from apunta a /admin/* (cualquier rol)', () => {
    mocks.isMobile = true;

    renderLogin({ from: { pathname: '/admin/operacion/pedidos' }, authorities: [Authority.CLIENTE] });

    expect(screen.getByText('HOME [aviso]')).toBeTruthy();
    expect(screen.queryByText('ADMIN_PANEL')).toBeNull();
  });

  it('en escritorio un ADMIN autenticado sigue yendo a /admin/user-management', () => {
    mocks.isMobile = false;

    renderLogin({ authorities: [Authority.ADMIN] });

    expect(screen.getByText('ADMIN_USER_MANAGEMENT')).toBeTruthy();
    expect(screen.queryByText(/HOME/)).toBeNull();
  });

  it('en escritorio conserva el redirect a from cuando apunta al panel admin', () => {
    mocks.isMobile = false;

    renderLogin({ from: { pathname: '/admin/operacion/pedidos' }, authorities: [Authority.CLIENTE] });

    expect(screen.getByText('ADMIN_PANEL')).toBeTruthy();
    expect(screen.queryByText(/HOME/)).toBeNull();
  });
});

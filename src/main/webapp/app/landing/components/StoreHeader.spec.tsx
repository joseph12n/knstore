import React from 'react';
import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import { MemoryRouter } from 'react-router';
import { Provider } from 'react-redux';
import { beforeEach, describe, expect, it, vi } from 'vitest';

import getStore from 'app/config/store';
import { CartProvider } from 'app/landing/context/CartContext';
import { Authority } from 'app/shared/jhipster/constants';
import { getAccount, logoutSession } from 'app/shared/reducers/authentication';

import StoreHeader from './StoreHeader';

const mocks = vi.hoisted(() => ({ isMobile: false }));

vi.mock('app/landing/hooks/useIsMobileView', () => ({
  MOBILE_VIEW_QUERY: '(max-width: 991.98px)',
  useIsMobileView: () => mocks.isMobile,
}));

const renderHeader = () => {
  const store = getStore();
  store.dispatch(getAccount.fulfilled({ data: { activated: true, login: 'admin', authorities: [Authority.ADMIN] } }, 'req-admin'));

  return render(
    <Provider store={store}>
      <MemoryRouter>
        {/* Carrito anónimo local: sin fetch al servidor, el header solo necesita el count. */}
        <CartProvider isAuthenticated={false} login="">
          <StoreHeader categorias={[]} subcategorias={[]} tema="light" onToggleTema={() => {}} />
        </CartProvider>
      </MemoryRouter>
    </Provider>,
  );
};

describe('StoreHeader — acceso al panel administrativo', () => {
  beforeEach(() => {
    window.localStorage.clear();
    getStore().dispatch(logoutSession());
    mocks.isMobile = false;
  });

  it('en escritorio muestra "Panel administrativo" dentro del dropdown "Mi cuenta"', async () => {
    mocks.isMobile = false;
    renderHeader();

    fireEvent.click(screen.getByText('Mi cuenta'));

    await waitFor(() => expect(screen.getByText('Panel administrativo')).toBeTruthy());
  });

  it('en móvil oculta "Panel administrativo" del dropdown "Mi cuenta"', async () => {
    mocks.isMobile = true;
    renderHeader();

    fireEvent.click(screen.getByText('Mi cuenta'));

    // El menú sí se abre, pero sin la entrada del panel admin.
    await waitFor(() => expect(screen.getByText('Cerrar sesión')).toBeTruthy());
    expect(screen.queryByText('Panel administrativo')).toBeNull();
  });

  it('en escritorio muestra "Panel administrativo" en el menú móvil (offcanvas)', async () => {
    mocks.isMobile = false;
    renderHeader();

    fireEvent.click(screen.getByRole('button', { name: 'Menú' }));

    await waitFor(() => expect(screen.getByText('Mis pedidos')).toBeTruthy());
    expect(screen.getByText('Panel administrativo')).toBeTruthy();
  });

  it('en móvil oculta "Panel administrativo" del menú móvil (offcanvas)', async () => {
    mocks.isMobile = true;
    renderHeader();

    fireEvent.click(screen.getByRole('button', { name: 'Menú' }));

    // El offcanvas abre y muestra el resto de enlaces, pero sin el panel admin.
    await waitFor(() => expect(screen.getByText('Mis pedidos')).toBeTruthy());
    expect(screen.queryByText('Panel administrativo')).toBeNull();
  });
});

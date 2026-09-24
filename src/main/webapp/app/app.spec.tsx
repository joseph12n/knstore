import React from 'react';
import { render, screen } from '@testing-library/react';
import { Provider } from 'react-redux';
import { configureStore } from '@reduxjs/toolkit';
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';
import axios from 'axios';

import sharedReducers from 'app/shared/reducers';

// app.tsx lee <base href> al importar el módulo; happy-dom nace sin <base>.
vi.hoisted(() => {
  if (typeof document !== 'undefined' && !document.querySelector('base')) {
    const base = document.createElement('base');
    base.setAttribute('href', '/');
    document.head.appendChild(base);
  }
});

import App from 'app/app';

const TITULO_AVISO = 'El panel está disponible desde escritorio';

/** matchMedia controlable: el query móvil responde según el test, el resto false. */
const stubMatchMedia = (isMobile: boolean) => {
  vi.stubGlobal(
    'matchMedia',
    vi.fn((query: string) => ({
      matches: query.includes('max-width') ? isMobile : false,
      media: query,
      onchange: null,
      addEventListener: vi.fn(),
      removeEventListener: vi.fn(),
      addListener: vi.fn(),
      removeListener: vi.fn(),
      dispatchEvent: vi.fn(() => true),
    })),
  );
};

// Store fresco por test: aísla el estado de sesión entre casos.
const renderApp = () =>
  render(
    <Provider store={configureStore({ reducer: sharedReducers })}>
      <App />
    </Provider>,
  );

describe('App — gate del panel admin en viewports móviles', () => {
  beforeEach(() => {
    // Sesión que nunca resuelve: el estado queda determinista (sin redirects async).
    vi.spyOn(axios, 'get').mockReturnValue(new Promise(() => {}));
    vi.spyOn(axios, 'post').mockReturnValue(new Promise(() => {}));
    window.history.pushState({}, '', '/admin');
  });

  afterEach(() => {
    window.history.replaceState({}, '', '/');
    vi.unstubAllGlobals();
    vi.restoreAllMocks();
  });

  it('en móvil muestra el aviso a pantalla completa y NO monta el sidebar admin', () => {
    stubMatchMedia(true);

    renderApp();

    expect(screen.getByRole('heading', { level: 1, name: TITULO_AVISO })).toBeTruthy();
    expect(document.querySelector('.admin-sidebar')).toBeNull();
    expect(screen.queryByText('Administración')).toBeNull();
  });

  it('en desktop monta el AdminLayout con su sidebar y no muestra el aviso', () => {
    stubMatchMedia(false);

    renderApp();

    expect(document.querySelector('.admin-sidebar')).toBeTruthy();
    expect(screen.queryByRole('heading', { level: 1, name: TITULO_AVISO })).toBeNull();
  });
});

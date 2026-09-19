import React from 'react';
import { render, screen } from '@testing-library/react';
import { MemoryRouter } from 'react-router';
import { Provider } from 'react-redux';
import { beforeEach, describe, expect, it } from 'vitest';

import getStore from 'app/config/store';

import { LoginPage } from './LoginPage';

describe('LoginPage', () => {
  beforeEach(() => {
    window.localStorage.clear();
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
});

import React from 'react';
import { render, screen } from '@testing-library/react';
import { MemoryRouter } from 'react-router';
import { describe, expect, it } from 'vitest';

import DesktopOnlyNotice from './DesktopOnlyNotice';

const renderNotice = () =>
  render(
    <MemoryRouter>
      <DesktopOnlyNotice />
    </MemoryRouter>,
  );

describe('DesktopOnlyNotice', () => {
  it('muestra el heading exacto del aviso', () => {
    renderNotice();

    expect(screen.getByRole('heading', { level: 1, name: 'El panel está disponible desde escritorio' })).toBeTruthy();
  });

  it('muestra la descripción y el hint', () => {
    renderNotice();

    expect(
      screen.getByText(
        'El panel administrativo está diseñado para pantallas grandes. Ábrelo desde tu computador para gestionar pedidos, catálogo e inventario.',
      ),
    ).toBeTruthy();
    expect(screen.getByText('Si tienes una pantalla pequeña, gírala o amplía la ventana para ver el panel.')).toBeTruthy();
  });

  it('ofrece el CTA "Volver a la tienda" que navega a la raíz', () => {
    const { container } = renderNotice();

    // Button as={Link} se resuelve como <a href="/"> navegable con Tab.
    const cta = screen.getByText('Volver a la tienda').closest('a');
    expect(cta).toBeTruthy();
    expect(cta!.getAttribute('href')).toBe('/');
    expect(container.querySelector('a[href="/"]')).toBeTruthy();
  });

  it('tiene un solo h1 (heading de nivel 1 por rol)', () => {
    const { container } = renderNotice();

    expect(container.querySelectorAll('h1')).toHaveLength(1);
  });
});

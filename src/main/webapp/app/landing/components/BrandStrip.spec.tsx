import React from 'react';
import { render, screen } from '@testing-library/react';
import { MemoryRouter } from 'react-router';
import { afterEach, describe, expect, it, vi } from 'vitest';
import axios from 'axios';

import BrandStrip from './BrandStrip';

describe('BrandStrip', () => {
  afterEach(() => {
    vi.restoreAllMocks();
  });

  it('muestra las marcas obtenidas del backend', async () => {
    vi.spyOn(axios, 'get').mockResolvedValue({
      data: [
        { id: '1', nombre: 'Nike' },
        { id: '2', nombre: 'Adidas' },
      ],
    });

    render(
      <MemoryRouter>
        <BrandStrip />
      </MemoryRouter>,
    );

    expect(await screen.findByText('Nike')).toBeDefined();
    expect(screen.getByText('Adidas')).toBeDefined();
  });

  it('no renderiza nada cuando no hay marcas', async () => {
    vi.spyOn(axios, 'get').mockResolvedValue({ data: [] });

    const { container } = render(
      <MemoryRouter>
        <BrandStrip />
      </MemoryRouter>,
    );

    await Promise.resolve();
    expect(container.querySelector('section')).toBeNull();
  });
});

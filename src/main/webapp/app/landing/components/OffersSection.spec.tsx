import React from 'react';
import { render, screen } from '@testing-library/react';
import { MemoryRouter } from 'react-router';
import { describe, expect, it } from 'vitest';

import { IProductoStorefront } from 'app/landing/model/storefront.model';
import OffersSection from './OffersSection';

const productos: IProductoStorefront[] = [
  {
    id: '1',
    nombre: 'Zapatos en oferta',
    slug: 'zapatos-en-oferta',
    precio: { precioCompra: 200000, precioVenta: 100000 },
    imagenes: [],
  },
  {
    id: '2',
    nombre: 'Camiseta blanca',
    slug: 'camiseta-blanca',
    precio: { precioCompra: 50000, precioVenta: 50000 },
    imagenes: [],
  },
  {
    id: '3',
    nombre: 'Gorra negra',
    slug: 'gorra-negra',
    precio: { precioCompra: 30000, precioVenta: 30000 },
    imagenes: [],
  },
  {
    id: '4',
    nombre: 'Medias grises',
    slug: 'medias-grises',
    precio: { precioCompra: 10000, precioVenta: 10000 },
    imagenes: [],
  },
];

describe('OffersSection', () => {
  it('muestra el título de ofertas y las tarjetas de producto', () => {
    render(
      <MemoryRouter>
        <OffersSection productos={productos} />
      </MemoryRouter>,
    );

    expect(screen.getByText('Ofertas')).toBeDefined();
    expect(screen.getByText('Zapatos en oferta')).toBeDefined();
  });
});

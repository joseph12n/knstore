import React from 'react';
import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import { MemoryRouter, Route, Routes } from 'react-router';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import axios from 'axios';

import { CategoryPage } from './CategoryPage';

const mocks = vi.hoisted(() => ({
  addItem: vi.fn(),
  categorias: [{ id: 'c-1', nombre: 'Calzado', slug: 'calzado', activo: true }],
  subcategorias: [{ id: 's-1', nombre: 'Tenis', slug: 'tenis', activo: true, categoria: { id: 'c-1', slug: 'calzado' } }],
}));

vi.mock('app/landing/hooks/useCatalog', () => ({
  default: () => ({
    categorias: mocks.categorias,
    subcategorias: mocks.subcategorias,
    marcas: [],
    loading: false,
    errorMessage: null,
    retry: vi.fn(),
  }),
  useCatalog: () => ({
    categorias: mocks.categorias,
    subcategorias: mocks.subcategorias,
    marcas: [],
    loading: false,
    errorMessage: null,
    retry: vi.fn(),
  }),
}));

vi.mock('app/landing/hooks/useCart', () => ({
  default: () => ({ addItem: mocks.addItem }),
}));

const PRODUCTOS_RESPUESTA = [
  {
    id: 'p-1',
    nombre: 'Tenis Blanca',
    slug: 'tenis-blanca',
    activo: true,
    marca: { nombre: 'Marca A' },
    precio: { precioVenta: 10000 },
    imagenes: [],
  },
];

const flush = () => new Promise(resolve => setTimeout(resolve, 50));

const mockSearchByUrl = () => {
  const getMock = vi.fn((url: string) => {
    if (String(url).startsWith('api/productos/search')) {
      return Promise.resolve({ data: PRODUCTOS_RESPUESTA, headers: { 'x-total-count': '1' } });
    }
    return Promise.resolve({ data: [] });
  });
  vi.spyOn(axios, 'get').mockImplementation(getMock);
  return getMock;
};

const renderCategory = (path = '/categorias/calzado') =>
  render(
    <MemoryRouter initialEntries={[path]}>
      <Routes>
        <Route path="/categorias/:categoriaSlug" element={<CategoryPage />} />
        <Route path="/categorias/:categoriaSlug/:subcategoriaSlug" element={<CategoryPage />} />
      </Routes>
    </MemoryRouter>,
  );

describe('CategoryPage', () => {
  beforeEach(() => {
    vi.restoreAllMocks();
    mocks.addItem.mockReset();
  });

  it('pide los productos de la categoria con sort por nombre y paginacion server-side (RF-072)', async () => {
    const getMock = mockSearchByUrl();
    renderCategory();

    await waitFor(() => {
      expect(screen.getByText('Tenis Blanca')).toBeTruthy();
    });

    const call = getMock.mock.calls.find(c => String(c[0]).startsWith('api/productos/search'));
    expect(call).toBeTruthy();
    const url = decodeURIComponent(String(call![0]));
    expect(url).toContain('categoriaId=c-1');
    expect(url).toContain('page=0');
    expect(url).toContain('size=24');
    expect(url).toContain('sort=nombre,asc');
  });

  it('filtra por subcategoria con subcategoriaId cuando la ruta es /categorias/{cat}/{sub}', async () => {
    const getMock = mockSearchByUrl();
    renderCategory('/categorias/calzado/tenis');

    await waitFor(() => {
      expect(screen.getByText('Tenis Blanca')).toBeTruthy();
    });

    const call = getMock.mock.calls.find(c => String(c[0]).startsWith('api/productos/search'));
    const url = decodeURIComponent(String(call![0]));
    expect(url).toContain('subcategoriaId=s-1');
    expect(url).not.toMatch(/&categoriaId=/);
  });

  it('el dropdown de orden envia sort=precioVenta,desc y reinicia la paginacion', async () => {
    const getMock = mockSearchByUrl();
    renderCategory();

    await waitFor(() => {
      expect(screen.getByText('Tenis Blanca')).toBeTruthy();
    });

    fireEvent.change(screen.getByRole('combobox'), { target: { value: 'precioVenta,desc' } });

    await waitFor(() => {
      const call = getMock.mock.calls.find(c => decodeURIComponent(String(c[0])).includes('sort=precioVenta,desc'));
      expect(call).toBeTruthy();
    });

    const priceCall = getMock.mock.calls.find(c => decodeURIComponent(String(c[0])).includes('sort=precioVenta,desc'));
    const priceUrl = decodeURIComponent(String(priceCall![0]));
    expect(priceUrl).toContain('page=0');
    expect(priceUrl).toContain('categoriaId=c-1');
    await flush();
  });
});

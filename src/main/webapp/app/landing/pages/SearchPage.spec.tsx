import React from 'react';
import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import { MemoryRouter } from 'react-router';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import axios from 'axios';

import { SearchPage } from './SearchPage';

const mocks = vi.hoisted(() => ({
  addItem: vi.fn(),
  categorias: [{ id: 'c-1', nombre: 'Calzado', slug: 'calzado', activo: true }],
  marcas: [],
}));

vi.mock('app/landing/hooks/useCatalog', () => ({
  default: () => ({ categorias: mocks.categorias, marcas: mocks.marcas, loading: false, errorMessage: null, retry: vi.fn() }),
  useCatalog: () => ({ categorias: mocks.categorias, marcas: mocks.marcas, loading: false, errorMessage: null, retry: vi.fn() }),
}));

vi.mock('app/landing/hooks/useCart', () => ({
  default: () => ({ addItem: mocks.addItem }),
}));

// El servidor devuelve la lista ya ordenada; el cliente NO debe re-ordenarla.
const PRODUCTOS_RESPUESTA = [
  {
    id: 'p-2',
    nombre: 'Bota Marrón',
    slug: 'bota-marron',
    activo: true,
    marca: { nombre: 'Marca A' },
    precio: { precioVenta: 50000 },
    imagenes: [],
  },
  {
    id: 'p-1',
    nombre: 'Tenis Blanco',
    slug: 'tenis-blanco',
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
      return Promise.resolve({ data: PRODUCTOS_RESPUESTA, headers: { 'x-total-count': '2' } });
    }
    return Promise.resolve({ data: [] });
  });
  vi.spyOn(axios, 'get').mockImplementation(getMock);
  return getMock;
};

const renderSearch = (query = 'tenis') =>
  render(
    <MemoryRouter initialEntries={[`/?q=${query}`]}>
      <SearchPage />
    </MemoryRouter>,
  );

const selectOrden = (value: string) => {
  // 3 combos: Categoria, Marca y Ordenar (el ultimo).
  const combos = screen.getAllByRole('combobox');
  const orden = combos[combos.length - 1];
  fireEvent.change(orden, { target: { value } });
};

describe('SearchPage', () => {
  beforeEach(() => {
    vi.restoreAllMocks();
    mocks.addItem.mockReset();
  });

  it('busca con q, page 0 y ordenamiento por relevancia (nombre,asc) al cargar', async () => {
    const getMock = mockSearchByUrl();
    renderSearch('tenis');

    await waitFor(() => {
      expect(screen.getByText('Tenis Blanco')).toBeTruthy();
    });

    const searchCall = getMock.mock.calls.find(c => String(c[0]).startsWith('api/productos/search'));
    expect(searchCall).toBeTruthy();
    expect(String(searchCall![0])).toContain('q=tenis');
    expect(String(searchCall![0])).toContain('page=0');
    expect(String(searchCall![0])).toContain('size=24');
    expect(String(searchCall![0])).toContain('sort=nombre,asc');
  });

  it('cambiar el orden a "Precio: menor a mayor" genera una nueva petición con sort=precioVenta,asc', async () => {
    const getMock = mockSearchByUrl();
    renderSearch('tenis');

    await waitFor(() => {
      expect(screen.getByText('Tenis Blanco')).toBeTruthy();
    });

    selectOrden('priceAsc');

    await waitFor(() => {
      const call = getMock.mock.calls.find(
        c => String(c[0]).startsWith('api/productos/search') && String(c[0]).includes('sort=precioVenta,asc'),
      );
      expect(call).toBeTruthy();
    });

    // RF-072: el cambio de orden reinicia la paginacion (page=0).
    const priceCall = getMock.mock.calls.find(c => String(c[0]).includes('sort=precioVenta,asc'));
    expect(String(priceCall![0])).toContain('page=0');
  });

  it('no re-ordenar la lista en el cliente: respeta el orden que devuelve el servidor', async () => {
    mockSearchByUrl();
    renderSearch('tenis');

    await waitFor(() => {
      expect(screen.getByText('Tenis Blanco')).toBeTruthy();
    });

    selectOrden('priceAsc');
    await waitFor(() => {
      expect(screen.getAllByText(/Bota Marrón|Tenis Blanco/).length).toBeGreaterThan(0);
    });
    await flush();

    // getByText con regex devuelve los nodos en orden de documento: se verifica
    // que el DOM conserva el orden del servidor (sin re-ordenamiento client-side).
    const nombresEnDom = screen.getAllByText(/Bota Marrón|Tenis Blanco/).map(el => el.textContent);
    expect(nombresEnDom).toEqual(['Bota Marrón', 'Tenis Blanco']);
  });
});

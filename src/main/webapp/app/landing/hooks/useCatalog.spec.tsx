import React from 'react';
import { Provider } from 'react-redux';
import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import axios from 'axios';

import getStore from 'app/config/store';
import { reset as resetCategorias } from 'app/entities/categoria/categoria.reducer';
import { reset as resetMarcas } from 'app/entities/marca/marca.reducer';
import { reset as resetProductos } from 'app/entities/producto/producto.reducer';
import { reset as resetSubcategorias } from 'app/entities/subcategoria/subcategoria.reducer';

import { useCatalog } from './useCatalog';

const entity = { id: '1', nombre: 'Test', activo: true };

const mockResponse = () => ({ data: [entity], headers: { 'x-total-count': '1' } });

const CatalogConsumer = ({ size = 100, loadOnMount = true }) => {
  const { productos, loading, errorMessage } = useCatalog({ page: 0, size, sort: 'nombre,asc', loadOnMount });
  return (
    <div>
      <span data-testid="loading">{String(loading)}</span>
      <span data-testid="productos">{productos.length}</span>
      <span data-testid="error">{errorMessage ?? ''}</span>
    </div>
  );
};

const LayoutConsumer = ({ children }: { children: React.ReactNode }) => {
  useCatalog({ page: 0, size: 100, sort: 'nombre,asc' });
  return <>{children}</>;
};

const RetryConsumer = () => {
  const { errorMessage, retry } = useCatalog({ page: 0, size: 100, sort: 'nombre,asc' });
  return (
    <div>
      <span data-testid="error">{errorMessage ?? ''}</span>
      <button onClick={retry}>Reintentar</button>
    </div>
  );
};

const flush = () => new Promise(resolve => setTimeout(resolve, 50));

describe('useCatalog', () => {
  beforeEach(() => {
    vi.restoreAllMocks();
    const store = getStore();
    store.dispatch(resetCategorias());
    store.dispatch(resetSubcategorias());
    store.dispatch(resetMarcas());
    store.dispatch(resetProductos());
  });

  it('fetches each catalog entity once when layout and page mount together', async () => {
    const getMock = vi.fn().mockResolvedValue(mockResponse());
    vi.spyOn(axios, 'get').mockImplementation(getMock);

    render(
      <Provider store={getStore()}>
        <LayoutConsumer>
          <CatalogConsumer loadOnMount={false} />
        </LayoutConsumer>
      </Provider>,
    );

    await waitFor(() => {
      expect(getMock).toHaveBeenCalledTimes(4);
    });

    await flush();
    expect(getMock).toHaveBeenCalledTimes(4);
  });

  it('does not fetch when loadOnMount is false', async () => {
    const getMock = vi.fn().mockResolvedValue(mockResponse());
    vi.spyOn(axios, 'get').mockImplementation(getMock);

    render(
      <Provider store={getStore()}>
        <CatalogConsumer loadOnMount={false} />
      </Provider>,
    );

    await flush();
    expect(getMock).not.toHaveBeenCalled();
  });

  it('does not refetch when entities are already loaded', async () => {
    const getMock = vi.fn().mockResolvedValue(mockResponse());
    vi.spyOn(axios, 'get').mockImplementation(getMock);

    const { unmount } = render(
      <Provider store={getStore()}>
        <CatalogConsumer />
      </Provider>,
    );

    await waitFor(() => {
      expect(getMock).toHaveBeenCalledTimes(4);
    });

    unmount();

    render(
      <Provider store={getStore()}>
        <CatalogConsumer />
      </Provider>,
    );

    await flush();
    expect(getMock).toHaveBeenCalledTimes(4);
  });

  it('retry refetches the catalog after a failed load and clears the error', async () => {
    const getMock = vi.fn().mockRejectedValue({ message: 'Network Error', isAxiosError: true, config: {}, request: {} });
    vi.spyOn(axios, 'get').mockImplementation(getMock);

    render(
      <Provider store={getStore()}>
        <RetryConsumer />
      </Provider>,
    );

    await waitFor(() => {
      expect(screen.getByTestId('error').textContent).not.toBe('');
    });
    expect(getMock).toHaveBeenCalledTimes(4);

    getMock.mockResolvedValue(mockResponse());
    fireEvent.click(screen.getByRole('button', { name: 'Reintentar' }));

    await waitFor(() => {
      expect(screen.getByTestId('error').textContent).toBe('');
    });
    expect(getMock).toHaveBeenCalledTimes(8);
  });
});

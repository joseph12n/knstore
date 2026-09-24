import React from 'react';
import { render, screen, waitFor } from '@testing-library/react';
import { Provider } from 'react-redux';
import { MemoryRouter } from 'react-router';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import axios from 'axios';

import getStore from 'app/config/store';
import AdminDashboardPage from './AdminDashboardPage';

const PEDIDOS = [
  {
    id: 'pedido-1',
    numeroPedido: 'PED-1001',
    total: 100000,
    estado: 'PENDING',
    createdDate: '2026-09-17T10:00:00.000Z',
    cuenta: { primerNombre: 'Ana', primerApellido: 'Lopez', user: { login: 'ana' } },
  },
  {
    id: 'pedido-2',
    numeroPedido: 'PED-1002',
    total: 200000,
    estado: 'DELIVERED',
    createdDate: '2026-09-16T10:00:00.000Z',
    cuenta: { primerNombre: 'Luis', primerApellido: 'Perez', user: { login: 'luis' } },
  },
];

const INVENTARIOS = [
  { id: 'inventario-1', stock: 1, stockMinimo: 2, producto: { id: 'producto-1', nombre: 'Producto A' } },
  { id: 'inventario-2', stock: 10, stockMinimo: 2, producto: { id: 'producto-2', nombre: 'Producto B' } },
];

const ENVIOS = [{ id: 'envio-1', estado: 'PENDING' }];

const PAGOS = [{ id: 'pago-1', estado: 'APPROVED' }];

const mockGet = () =>
  vi.spyOn(axios, 'get').mockImplementation((url: string) => {
    const endpoint = String(url);
    if (endpoint.startsWith('api/pedidos')) {
      return Promise.resolve({ data: PEDIDOS });
    }
    if (endpoint.startsWith('api/producto-inventarios')) {
      return Promise.resolve({ data: INVENTARIOS });
    }
    if (endpoint.startsWith('api/envios')) {
      return Promise.resolve({ data: ENVIOS });
    }
    if (endpoint.startsWith('api/pagos')) {
      return Promise.resolve({ data: PAGOS });
    }
    return Promise.resolve({ data: [] });
  });

const renderPage = () =>
  render(
    <Provider store={getStore()}>
      <MemoryRouter>
        <AdminDashboardPage />
      </MemoryRouter>
    </Provider>,
  );

describe('AdminDashboardPage', () => {
  beforeEach(() => {
    vi.restoreAllMocks();
  });

  it('muestra el resumen con las ventas y el pedido más reciente', async () => {
    mockGet();
    renderPage();

    await waitFor(() => {
      expect(screen.getByText('Panel administrativo')).toBeTruthy();
      expect(screen.getByText('$ 300.000')).toBeTruthy();
      expect(screen.getByText(/PED-1001/)).toBeTruthy();
    });
  });
});

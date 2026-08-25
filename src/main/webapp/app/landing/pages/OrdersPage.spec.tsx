import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { MemoryRouter } from 'react-router';
import { Provider } from 'react-redux';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import axios from 'axios';

import getStore from 'app/config/store';
import { OrdersPage } from 'app/landing/pages/OrdersPage';

vi.mock('app/landing/hooks/useCuentaActual', () => ({
  default: () => ({
    account: { login: 'cliente-test' },
    cuenta: { id: 'cuenta-1' },
    loading: false,
  }),
}));

vi.mock('axios', () => ({
  default: {
    get: vi.fn(),
    post: vi.fn(),
    put: vi.fn(),
    delete: vi.fn(),
  },
}));

const ahora = new Date().toISOString();

const mockListados = (pedidos: unknown[], pagos: unknown[], envios: unknown[], facturas: unknown[]) => {
  vi.mocked(axios.get).mockImplementation((url: string) => {
    if (url.startsWith('api/pedidos')) {
      return Promise.resolve({ data: pedidos, headers: { 'x-total-count': String(pedidos.length) } });
    }
    if (url.startsWith('api/pagos')) {
      return Promise.resolve({ data: pagos, headers: { 'x-total-count': String(pagos.length) } });
    }
    if (url.startsWith('api/envios')) {
      return Promise.resolve({ data: envios, headers: { 'x-total-count': String(envios.length) } });
    }
    if (url.startsWith('api/facturas')) {
      return Promise.resolve({ data: facturas, headers: { 'x-total-count': String(facturas.length) } });
    }
    return Promise.resolve({ data: [], headers: {} });
  });
};

describe('OrdersPage', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('cancela el pedido enviando el motivo y solicita el reembolso si el pago fue aprobado', async () => {
    const pedido = { id: 'p1', numeroPedido: 'PED-T-000001', estado: 'CONFIRMED', total: 100000, costoEnvio: 0, createdDate: ahora };
    const pago = { id: 'pg1', estado: 'APPROVED', monto: 100000, pedido: { id: 'p1' } };
    mockListados([pedido], [pago], [], []);
    vi.mocked(axios.post).mockResolvedValue({ data: {} });

    render(
      <Provider store={getStore()}>
        <MemoryRouter>
          <OrdersPage />
        </MemoryRouter>
      </Provider>,
    );

    expect(await screen.findByText('#PED-T-000001')).toBeDefined();
    await waitFor(() => expect(screen.getByRole('button', { name: 'Cancelar' })).toBeDefined());

    fireEvent.click(screen.getByRole('button', { name: 'Cancelar' }));
    await waitFor(() => expect(screen.getByText('Sí, cancelar pedido')).toBeDefined());
    fireEvent.change(screen.getByPlaceholderText(/cuéntanos por qué cancelas/i), { target: { value: 'Ya no lo necesito' } });
    fireEvent.click(screen.getByText('Sí, cancelar pedido'));

    await waitFor(() => expect(axios.post).toHaveBeenCalledWith('api/pedidos/p1/cancelar', { motivo: 'Ya no lo necesito' }));
  });

  it('oculta el boton cancelar cuando vencio la ventana de 1 hora', async () => {
    const pedido = {
      id: 'p2',
      numeroPedido: 'PED-T-000002',
      estado: 'CONFIRMED',
      total: 100000,
      costoEnvio: 0,
      createdDate: new Date(Date.now() - 2 * 60 * 60 * 1000).toISOString(),
    };
    mockListados([pedido], [], [], []);

    render(
      <Provider store={getStore()}>
        <MemoryRouter>
          <OrdersPage />
        </MemoryRouter>
      </Provider>,
    );

    expect(await screen.findByText('#PED-T-000002')).toBeDefined();
    expect(screen.queryByRole('button', { name: 'Cancelar' })).toBeNull();
  });

  it('muestra las pestanas de pedidos, envios, facturas y pagos con sus estados', async () => {
    const pedido = { id: 'p3', numeroPedido: 'PED-T-000003', estado: 'CANCELLED', total: 100000, costoEnvio: 0, createdDate: ahora };
    const pago = { id: 'pg3', estado: 'REFUNDED', monto: 100000, metodoPago: 'NEQUI', fechaPago: ahora, pedido: { id: 'p3' } };
    const envio = { id: 'e3', estado: 'IN_TRANSIT', transportadora: 'Interrapidísimo', numeroRastreo: 'TRK-3' };
    const factura = { id: 'f3', prefijo: 'FE', total: 100000, enviada: true, fechaEmision: ahora };
    mockListados([pedido], [pago], [envio], [factura]);

    render(
      <Provider store={getStore()}>
        <MemoryRouter>
          <OrdersPage />
        </MemoryRouter>
      </Provider>,
    );

    expect(await screen.findByText(`Pedidos (1)`)).toBeDefined();
    expect(screen.getByText('Cancelado')).toBeDefined();

    fireEvent.click(screen.getByText('Pagos (1)'));
    await waitFor(() => expect(screen.getByText('Reembolsado')).toBeDefined());
    expect(screen.getByText('NEQUI')).toBeDefined();

    fireEvent.click(screen.getByText('Envíos (1)'));
    expect((await screen.findAllByText(/TRK-3/)).length).toBeGreaterThan(0);
    expect(screen.getByText('En tránsito')).toBeDefined();

    fireEvent.click(screen.getByText('Facturas (1)'));
    expect(await screen.findByText(/Descargar factura/i)).toBeDefined();
    expect(screen.getByText(/Emisión por correo: enviada/i)).toBeDefined();
  });
});

import React from 'react';
import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import { MemoryRouter, Route, Routes } from 'react-router';
import { Provider } from 'react-redux';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import axios from 'axios';

import getStore from 'app/config/store';
import { OrderDetailPage } from './OrderDetailPage';

const PEDIDO = {
  id: 'pedido-1',
  numeroPedido: 'PED-1',
  estado: 'PENDING',
  subtotal: 100000,
  costoEnvio: 9900,
  ivaTotal: 19000,
  total: 128900,
  direccion: { direccion: 'Calle 1 #2-3', municipio: 'Bogota', departamento: 'Cundinamarca' },
};

const ITEM = {
  id: 'item-1',
  pedido: { id: 'pedido-1' },
  nombreProducto: 'Tenis Test',
  marcaProducto: 'Marca A',
  cantidad: 1,
  precioUnitario: 100000,
};

const flush = () => new Promise(resolve => setTimeout(resolve, 50));

const mockGet = (pagos: unknown[]) => {
  const getMock = vi.fn((url: string) => {
    const u = String(url);
    if (u.startsWith('api/pedidos/pedido-1')) {
      return Promise.resolve({ data: PEDIDO });
    }
    if (u.startsWith('api/pedidos?')) {
      return Promise.resolve({ data: [PEDIDO], headers: { 'x-total-count': '1' } });
    }
    if (u.startsWith('api/item-pedidos')) {
      return Promise.resolve({ data: [ITEM], headers: { 'x-total-count': '1' } });
    }
    if (u.startsWith('api/pagos')) {
      return Promise.resolve({ data: pagos, headers: { 'x-total-count': String(pagos.length) } });
    }
    if (u.startsWith('api/envios')) {
      return Promise.resolve({ data: [], headers: { 'x-total-count': '0' } });
    }
    if (u.startsWith('api/facturas')) {
      return Promise.resolve({ data: [], headers: { 'x-total-count': '0' } });
    }
    return Promise.resolve({ data: [] });
  });
  vi.spyOn(axios, 'get').mockImplementation(getMock);
  return getMock;
};

const renderOrder = () =>
  render(
    <Provider store={getStore()}>
      <MemoryRouter initialEntries={['/mi-cuenta/pedidos/pedido-1']}>
        <Routes>
          <Route path="/mi-cuenta/pedidos/:id" element={<OrderDetailPage />} />
        </Routes>
      </MemoryRouter>
    </Provider>,
  );

describe('OrderDetailPage', () => {
  beforeEach(() => {
    vi.restoreAllMocks();
  });

  it('muestra "Pagar ahora" cuando el pedido está PENDING y el pago fue REJECTED (RF-075)', async () => {
    mockGet([{ id: 'pago-1', estado: 'REJECTED', metodoPago: 'NEQUI', monto: 128900, pedido: { id: 'pedido-1' } }]);
    renderOrder();

    await waitFor(() => {
      expect(screen.getByText('Pedido #PED-1')).toBeTruthy();
    });
    expect(screen.getByRole('button', { name: 'Pagar ahora' })).toBeTruthy();
  });

  it('no muestra "Pagar ahora" cuando el pago esta APPROVED', async () => {
    mockGet([{ id: 'pago-1', estado: 'APPROVED', metodoPago: 'NEQUI', monto: 128900, pedido: { id: 'pedido-1' } }]);
    renderOrder();

    await waitFor(() => {
      expect(screen.getByText('Pedido #PED-1')).toBeTruthy();
    });
    expect(screen.queryByRole('button', { name: 'Pagar ahora' })).toBeNull();
  });

  it('al hacer click en "Pagar ahora" llama a api/pagos/iniciar y recarga el pedido', async () => {
    const getMock = mockGet([{ id: 'pago-1', estado: 'PENDING', metodoPago: 'NEQUI', monto: 128900, pedido: { id: 'pedido-1' } }]);
    const postMock = vi.spyOn(axios, 'post').mockResolvedValue({ data: { id: 'pago-2', estado: 'APPROVED' } });
    renderOrder();

    await waitFor(() => {
      expect(screen.getByText('Pedido #PED-1')).toBeTruthy();
    });
    const getPedidoCallsIniciales = getMock.mock.calls.filter(c => String(c[0]).startsWith('api/pedidos/pedido-1')).length;

    fireEvent.click(screen.getByRole('button', { name: 'Pagar ahora' }));
    await flush();

    const iniciarPagoCall = postMock.mock.calls.find(c => String(c[0]).endsWith('api/pagos/iniciar'));
    expect(iniciarPagoCall).toBeTruthy();
    expect(iniciarPagoCall![1]).toEqual({ pedidoId: 'pedido-1' });

    // RF-075: tras el pago se recarga el pedido con su nuevo estado.
    await waitFor(() => {
      const getPedidoCallsFinales = getMock.mock.calls.filter(c => String(c[0]).startsWith('api/pedidos/pedido-1')).length;
      expect(getPedidoCallsFinales).toBeGreaterThan(getPedidoCallsIniciales);
    });
  });

  it('si iniciarPago falla muestra el error y sigue permitiendo reintentar', async () => {
    mockGet([]);
    const postMock = vi.spyOn(axios, 'post').mockRejectedValue({ response: { data: { detail: 'Pasarela no disponible' } } });
    renderOrder();

    await waitFor(() => {
      expect(screen.getByText('Pedido #PED-1')).toBeTruthy();
    });

    fireEvent.click(screen.getByRole('button', { name: 'Pagar ahora' }));
    await flush();

    expect(postMock).toHaveBeenCalled();
    const boton = screen.getByRole('button', { name: 'Pagar ahora' }) as HTMLButtonElement;
    expect(boton.disabled).toBe(false);
  });
});

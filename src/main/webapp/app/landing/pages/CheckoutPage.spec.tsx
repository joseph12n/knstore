import React from 'react';
import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import { MemoryRouter } from 'react-router';
import { Provider } from 'react-redux';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import axios from 'axios';

import getStore from 'app/config/store';
import { CheckoutPage } from './CheckoutPage';

const mocks = vi.hoisted(() => ({
  items: [{ id: 'item-1', producto: { id: 'prod-1', nombre: 'Tenis Test' }, cantidad: 1, precioUnitario: 100000 }] as {
    id?: string;
    producto: { id: string; nombre: string };
    cantidad: number;
    precioUnitario: number;
  }[],
  refresh: vi.fn(),
  navigate: vi.fn(),
  account: { login: 'cliente' },
  cuenta: { id: 'cuenta-1' },
}));

vi.mock('app/landing/hooks/useCart', () => ({
  default: () => ({ items: mocks.items, refresh: mocks.refresh }),
}));

vi.mock('app/landing/hooks/useCuentaActual', () => ({
  default: () => ({ account: mocks.account, cuenta: mocks.cuenta }),
}));

vi.mock('react-router', async importOriginal => {
  const actual = await importOriginal<typeof import('react-router')>();
  return { ...actual, useNavigate: () => mocks.navigate };
});

const CUENTA = { id: 'cuenta-1', user: { login: 'cliente' } };
const DIRECCION = {
  id: 'dir-1',
  direccion: 'Calle 1 #2-3',
  municipio: 'Bogota',
  departamento: 'Cundinamarca',
  activo: true,
  cuenta: { id: 'cuenta-1' },
};

const PREVIEW = { subtotal: 100000, iva: 19000, envio: 9900, total: 128900 };

const mockGet = () => {
  vi.spyOn(axios, 'get').mockImplementation((url: string) => {
    if (String(url).startsWith('api/account')) {
      return Promise.resolve({ data: { login: 'cliente', authorities: ['ROLE_CLIENTE'] } });
    }
    if (String(url).startsWith('api/cuentas')) {
      return Promise.resolve({ data: [CUENTA] });
    }
    if (String(url).startsWith('api/direccions')) {
      return Promise.resolve({ data: [DIRECCION], headers: { 'x-total-count': '1' } });
    }
    return Promise.resolve({ data: [] });
  });
};

const mockPost = (pagoEstado = 'APPROVED') => {
  const postMock = vi.spyOn(axios, 'post').mockImplementation((url: string) => {
    if (String(url).endsWith('api/pedidos/preview')) {
      return Promise.resolve({ data: PREVIEW });
    }
    if (String(url).endsWith('api/pedidos/checkout')) {
      // RF-076: el checkout ya devuelve el pago resultante (pasarela simulada).
      return Promise.resolve({
        data: {
          pedido: { id: 'pedido-1', numeroPedido: 'PED-1' },
          pago: { id: 'pago-1', estado: pagoEstado, descripcionRespuesta: 'Pago procesado' },
        },
      });
    }
    if (String(url).endsWith('api/pagos/iniciar')) {
      return Promise.resolve({ data: { id: 'pago-1', estado: pagoEstado, descripcionRespuesta: 'Pago aprobado' } });
    }
    return Promise.resolve({ data: {} });
  });
  return postMock;
};

const flush = () => new Promise(resolve => setTimeout(resolve, 50));

const renderCheckout = () =>
  render(
    <Provider store={getStore()}>
      <MemoryRouter>
        <CheckoutPage />
      </MemoryRouter>
    </Provider>,
  );

const goToStep = async (step: number) => {
  for (let i = 0; i < step; i++) {
    fireEvent.click(screen.getByRole('button', { name: 'Continuar' }));
    await flush();
  }
};

describe('CheckoutPage', () => {
  beforeEach(() => {
    vi.restoreAllMocks();
    mocks.refresh.mockReset().mockResolvedValue(undefined);
    mocks.navigate.mockReset();
    mocks.items[0] = { id: 'item-1', producto: { id: 'prod-1', nombre: 'Tenis Test' }, cantidad: 1, precioUnitario: 100000 };
    mockGet();
  });

  it('muestra carrito vacio sin llamar al checkout', async () => {
    mocks.items.length = 0;
    renderCheckout();
    await flush();

    expect(screen.getByText('Tu carrito está vacío')).toBeTruthy();
  });

  it('carga el preview al confirmar y no lo repite al escribir notas', async () => {
    const postMock = mockPost();
    renderCheckout();
    await flush();

    await goToStep(2);
    const notasInput = screen.getByPlaceholderText('Instrucciones de entrega, referencias, etc.');
    fireEvent.change(notasInput, { target: { value: 'Entregar en porteria' } });

    fireEvent.click(screen.getByRole('button', { name: 'Continuar' }));
    await flush();

    await waitFor(() => {
      expect(postMock.mock.calls.some(c => String(c[0]).endsWith('api/pedidos/preview'))).toBe(true);
    });
    const llamadasAntes = postMock.mock.calls.filter(c => String(c[0]).endsWith('api/pedidos/preview')).length;

    // Escribir mas notas ya en el paso 3 no debe disparar otro preview.
    fireEvent.change(notasInput, { target: { value: 'Entregar en porteria, timbrar dos veces' } });
    await flush();

    const llamadasDespues = postMock.mock.calls.filter(c => String(c[0]).endsWith('api/pedidos/preview')).length;
    expect(llamadasDespues).toBe(llamadasAntes);
    await waitFor(() => {
      expect(screen.getByText('$ 128.900')).toBeTruthy();
    });
  });

  it('el payload de checkout no incluye el precio unitario del cliente', async () => {
    const postMock = mockPost();
    renderCheckout();
    await flush();

    await goToStep(3);
    await waitFor(() => {
      const confirmBtn = screen.getByRole('button', { name: 'Confirmar pedido' }) as HTMLButtonElement;
      expect(confirmBtn.disabled).toBe(false);
    });

    fireEvent.click(screen.getByRole('button', { name: 'Confirmar pedido' }));
    await flush();

    const checkoutCall = postMock.mock.calls.find(c => String(c[0]).endsWith('api/pedidos/checkout'));
    expect(checkoutCall).toBeTruthy();
    const payload = checkoutCall![1] as { items: Record<string, unknown>[] };
    expect(payload.items[0]).toEqual({ productoId: 'prod-1', cantidad: 1 });
    expect(Object.keys(payload.items[0])).not.toContain('precioUnitario');
  });

  it('pago aprobado usa el pago del checkout sin llamar a iniciarPago y navega al detalle', async () => {
    const postMock = mockPost('APPROVED');
    renderCheckout();
    await flush();

    await goToStep(3);
    await waitFor(() => {
      const confirmBtn = screen.getByRole('button', { name: 'Confirmar pedido' }) as HTMLButtonElement;
      expect(confirmBtn.disabled).toBe(false);
    });

    fireEvent.click(screen.getByRole('button', { name: 'Confirmar pedido' }));
    await flush();

    expect(mocks.refresh).toHaveBeenCalled();
    // RF-076: el pago viene dentro de la respuesta del checkout, no se repite la llamada.
    expect(postMock.mock.calls.filter(c => String(c[0]).endsWith('api/pagos/iniciar'))).toHaveLength(0);
    await waitFor(() => {
      expect(mocks.navigate).toHaveBeenCalledWith('/mi-cuenta/pedidos/pedido-1');
    });
  });

  it('si el checkout no devuelve pago, intenta iniciarPago por compatibilidad', async () => {
    const postMock = vi.spyOn(axios, 'post').mockImplementation((url: string) => {
      if (String(url).endsWith('api/pedidos/preview')) {
        return Promise.resolve({ data: PREVIEW });
      }
      if (String(url).endsWith('api/pedidos/checkout')) {
        return Promise.resolve({ data: { pedido: { id: 'pedido-1', numeroPedido: 'PED-1' } } });
      }
      if (String(url).endsWith('api/pagos/iniciar')) {
        return Promise.resolve({ data: { id: 'pago-1', estado: 'APPROVED', descripcionRespuesta: 'Pago aprobado' } });
      }
      return Promise.resolve({ data: {} });
    });
    renderCheckout();
    await flush();

    await goToStep(3);
    await waitFor(() => {
      const confirmBtn = screen.getByRole('button', { name: 'Confirmar pedido' }) as HTMLButtonElement;
      expect(confirmBtn.disabled).toBe(false);
    });

    fireEvent.click(screen.getByRole('button', { name: 'Confirmar pedido' }));
    await flush();

    const iniciarPagoCall = postMock.mock.calls.find(c => String(c[0]).endsWith('api/pagos/iniciar'));
    expect(iniciarPagoCall).toBeTruthy();
    expect(iniciarPagoCall![1]).toEqual({ pedidoId: 'pedido-1' });
    await waitFor(() => {
      expect(mocks.navigate).toHaveBeenCalledWith('/mi-cuenta/pedidos/pedido-1');
    });
  });

  it('pago rechazado tambien sincroniza el carrito y navega al detalle', async () => {
    mockPost('REJECTED');
    renderCheckout();
    await flush();

    await goToStep(3);
    await waitFor(() => {
      const confirmBtn = screen.getByRole('button', { name: 'Confirmar pedido' }) as HTMLButtonElement;
      expect(confirmBtn.disabled).toBe(false);
    });

    fireEvent.click(screen.getByRole('button', { name: 'Confirmar pedido' }));
    await flush();

    expect(mocks.refresh).toHaveBeenCalled();
    await waitFor(() => {
      expect(mocks.navigate).toHaveBeenCalledWith('/mi-cuenta/pedidos/pedido-1');
    });
  });
});

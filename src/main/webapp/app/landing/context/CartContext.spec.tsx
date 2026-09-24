import React, { useState } from 'react';
import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import { describe, expect, it, vi, beforeEach, afterEach } from 'vitest';
import axios from 'axios';
import { toast } from 'react-toastify';

import { CartProvider, useCartContext } from './CartContext';
import type { AddItemResult } from './CartContext';
import { IProductoStorefront } from 'app/landing/model/storefront.model';

const PRODUCTO = {
  id: 'prod-1',
  nombre: 'Producto A',
  slug: 'producto-a',
  precio: { precioVenta: 100000 },
  imagenes: [],
} as IProductoStorefront;

const CUENTA = { id: 'cuenta-1' };

const TestConsumer = () => {
  const { items, count, total } = useCartContext();
  return (
    <div>
      <div data-testid="count">{count}</div>
      <div data-testid="total">{total}</div>
      <div data-testid="items">{items.length}</div>
    </div>
  );
};

const ActionsConsumer = ({ producto = PRODUCTO, cantidad = 1 }: { producto?: IProductoStorefront; cantidad?: number }) => {
  const { items, count, total, addItem, updateQuantity, removeItem, clearCart } = useCartContext();
  const [addResult, setAddResult] = useState<AddItemResult | null>(null);
  return (
    <div>
      <div data-testid="count">{count}</div>
      <div data-testid="total">{total}</div>
      <div data-testid="items">{items.length}</div>
      <div data-testid="item-quantities">{items.map(item => item.cantidad).join(',')}</div>
      <div data-testid="add-result">{addResult ? (addResult.ok ? 'ok' : addResult.reason) : ''}</div>
      <button data-testid="add-item" onClick={() => void addItem(producto, cantidad).then(setAddResult)}>
        Add
      </button>
      <button
        data-testid="add-item-twice"
        onClick={() => {
          void addItem(producto, cantidad).then(setAddResult);
          void addItem(producto, cantidad).then(setAddResult);
        }}
      >
        Add twice
      </button>
      <button data-testid="update-item" onClick={() => void updateQuantity('item-1', 3)}>
        Update
      </button>
      <button data-testid="remove-item" onClick={() => void removeItem('item-1')}>
        Remove
      </button>
      <button data-testid="clear-cart" onClick={() => void clearCart()}>
        Clear
      </button>
    </div>
  );
};

const flush = () => new Promise(resolve => setTimeout(resolve, 50));

const mockServerWithItem = () => {
  const productUrls: string[] = [];
  const getMock = vi.fn((url: string) => {
    const u = String(url);
    if (u.startsWith('api/cuentas')) return Promise.resolve({ data: [CUENTA] });
    if (u.startsWith('api/carritos')) return Promise.resolve({ data: [{ id: 'carrito-1' }] });
    if (u.startsWith('api/item-carritos'))
      return Promise.resolve({
        data: [{ id: 'item-1', cantidad: 1, precioUnitario: 100000, producto: { id: 'prod-1' }, carrito: { id: 'carrito-1' } }],
      });
    if (u.startsWith('api/productos')) {
      productUrls.push(u);
      return Promise.resolve({ data: [PRODUCTO] });
    }
    return Promise.resolve({ data: [] });
  });
  vi.spyOn(axios, 'get').mockImplementation(getMock);
  return { getMock, productUrls };
};

// RNF-029: helper con producto solo en /por-ids y sin fallback para el catalogo completo.
const mockServerConPorIds = (itemCarritos: unknown[] = []) => {
  const productUrls: string[] = [];
  vi.spyOn(axios, 'get').mockImplementation((url: string) => {
    const u = String(url);
    if (u.startsWith('api/cuentas')) return Promise.resolve({ data: [CUENTA] });
    if (u.startsWith('api/carritos')) return Promise.resolve({ data: [{ id: 'carrito-1' }] });
    if (u.startsWith('api/item-carritos')) return Promise.resolve({ data: itemCarritos });
    if (u.startsWith('api/productos')) {
      productUrls.push(u);
      return Promise.resolve({ data: [PRODUCTO] });
    }
    return Promise.resolve({ data: [] });
  });
  return { productUrls };
};

const ITEM_CARRITO = { id: 'item-1', cantidad: 1, precioUnitario: 100000, producto: { id: 'prod-1' }, carrito: { id: 'carrito-1' } };

describe('CartContext anonymous user', () => {
  beforeEach(() => {
    localStorage.clear();
  });

  afterEach(() => {
    localStorage.clear();
    vi.restoreAllMocks();
  });

  it('loads empty cart for anonymous user', async () => {
    render(
      <CartProvider isAuthenticated={false} login="">
        <TestConsumer />
      </CartProvider>,
    );

    await waitFor(() => {
      expect(screen.getByTestId('count').textContent).toBe('0');
      expect(screen.getByTestId('total').textContent).toBe('0');
      expect(screen.getByTestId('items').textContent).toBe('0');
    });
  });

  it('loads cart from localStorage', async () => {
    localStorage.setItem(
      'knstore-cart',
      JSON.stringify([
        {
          id: 'item-1',
          producto: PRODUCTO,
          cantidad: 2,
          precioUnitario: 100000,
        },
      ]),
    );

    render(
      <CartProvider isAuthenticated={false} login="">
        <TestConsumer />
      </CartProvider>,
    );

    await waitFor(() => {
      expect(screen.getByTestId('count').textContent).toBe('2');
      expect(screen.getByTestId('total').textContent).toBe('200000');
      expect(screen.getByTestId('items').textContent).toBe('1');
    });
  });
});

describe('CartContext authenticated user', () => {
  beforeEach(() => {
    localStorage.clear();
  });

  afterEach(() => {
    localStorage.clear();
    vi.restoreAllMocks();
  });

  it('returns no-cuenta and does not mutate state when the user has no Cuenta', async () => {
    vi.spyOn(axios, 'get').mockImplementation((url: string) => {
      if (String(url).startsWith('api/cuentas')) return Promise.resolve({ data: [] });
      return Promise.resolve({ data: [] });
    });
    const postMock = vi.spyOn(axios, 'post').mockResolvedValue({ data: { id: 'item-1' } });

    render(
      <CartProvider isAuthenticated={true} login="usuario">
        <ActionsConsumer />
      </CartProvider>,
    );

    await flush();
    fireEvent.click(screen.getByTestId('add-item'));

    await waitFor(() => {
      expect(screen.getByTestId('add-result').textContent).toBe('no-cuenta');
    });
    expect(screen.getByTestId('items').textContent).toBe('0');
    expect(postMock).not.toHaveBeenCalled();
  });

  it('loads server cart products only by ids and never fetches the full catalog (RNF-029)', async () => {
    const { productUrls } = mockServerConPorIds([ITEM_CARRITO]);

    render(
      <CartProvider isAuthenticated={true} login="usuario">
        <TestConsumer />
      </CartProvider>,
    );

    await waitFor(() => {
      expect(screen.getByTestId('items').textContent).toBe('1');
    });

    expect(productUrls).toHaveLength(1);
    expect(productUrls[0]).toContain('api/productos/por-ids');
    expect(productUrls[0]).toContain('ids=prod-1');
    expect(productUrls.some(u => u.includes('size=1000'))).toBe(false);
  });

  it('does not call productos/por-ids when the server cart has no items', async () => {
    const { productUrls } = mockServerConPorIds([]);

    render(
      <CartProvider isAuthenticated={true} login="usuario">
        <TestConsumer />
      </CartProvider>,
    );

    await waitFor(() => {
      expect(screen.getByTestId('items').textContent).toBe('0');
    });
    await flush();

    expect(productUrls).toHaveLength(0);
  });

  it('adds a product and reflects it for an authenticated user with Cuenta', async () => {
    vi.spyOn(axios, 'get').mockImplementation((url: string) => {
      if (String(url).startsWith('api/cuentas')) return Promise.resolve({ data: [CUENTA] });
      if (String(url).startsWith('api/carritos')) return Promise.resolve({ data: [] });
      if (String(url).startsWith('api/item-carritos')) return Promise.resolve({ data: [] });
      return Promise.resolve({ data: [] });
    });
    vi.spyOn(axios, 'post').mockImplementation((url: string) => {
      if (String(url) === 'api/carritos') return Promise.resolve({ data: { id: 'carrito-1' } });
      return Promise.resolve({ data: { id: 'item-1', cantidad: 1, precioUnitario: 100000 } });
    });

    render(
      <CartProvider isAuthenticated={true} login="usuario">
        <ActionsConsumer />
      </CartProvider>,
    );

    await flush();
    fireEvent.click(screen.getByTestId('add-item'));

    await waitFor(() => {
      expect(screen.getByTestId('add-result').textContent).toBe('ok');
    });
    expect(screen.getByTestId('items').textContent).toBe('1');
    expect(screen.getByTestId('count').textContent).toBe('1');
    expect(screen.getByTestId('total').textContent).toBe('100000');
  });

  it('rolls back the optimistic quantity on addItem when the PUT fails', async () => {
    mockServerWithItem();
    vi.spyOn(axios, 'put').mockRejectedValue({ response: { data: { detail: 'boom' } } });

    render(
      <CartProvider isAuthenticated={true} login="usuario">
        <ActionsConsumer />
      </CartProvider>,
    );

    await waitFor(() => {
      expect(screen.getByTestId('item-quantities').textContent).toBe('1');
    });

    fireEvent.click(screen.getByTestId('add-item'));
    await waitFor(() => {
      expect(screen.getByTestId('item-quantities').textContent).toBe('2');
    });
    await waitFor(() => {
      expect(screen.getByTestId('item-quantities').textContent).toBe('1');
    });
    expect(screen.getByTestId('add-result').textContent).toBe('error');
  });

  it('rolls back the optimistic quantity on updateQuantity when the PUT fails', async () => {
    mockServerWithItem();
    vi.spyOn(axios, 'put').mockRejectedValue({ response: { data: { detail: 'boom' } } });

    render(
      <CartProvider isAuthenticated={true} login="usuario">
        <ActionsConsumer />
      </CartProvider>,
    );

    await waitFor(() => {
      expect(screen.getByTestId('item-quantities').textContent).toBe('1');
    });

    fireEvent.click(screen.getByTestId('update-item'));
    await waitFor(() => {
      expect(screen.getByTestId('item-quantities').textContent).toBe('3');
    });
    await waitFor(() => {
      expect(screen.getByTestId('item-quantities').textContent).toBe('1');
    });
  });

  it('rolls back the optimistic removal when the DELETE fails', async () => {
    mockServerWithItem();
    vi.spyOn(axios, 'delete').mockRejectedValue({ response: { data: { detail: 'boom' } } });

    render(
      <CartProvider isAuthenticated={true} login="usuario">
        <ActionsConsumer />
      </CartProvider>,
    );

    await waitFor(() => {
      expect(screen.getByTestId('items').textContent).toBe('1');
    });

    fireEvent.click(screen.getByTestId('remove-item'));
    await waitFor(() => {
      expect(screen.getByTestId('items').textContent).toBe('0');
    });
    await waitFor(() => {
      expect(screen.getByTestId('items').textContent).toBe('1');
    });
  });

  it('clears the authenticated cart with a single DELETE api/carritos/{id}/items', async () => {
    mockServerWithItem();
    const deleteMock = vi.spyOn(axios, 'delete').mockResolvedValue({ data: {} });

    render(
      <CartProvider isAuthenticated={true} login="usuario">
        <ActionsConsumer />
      </CartProvider>,
    );

    await waitFor(() => {
      expect(screen.getByTestId('items').textContent).toBe('1');
    });

    fireEvent.click(screen.getByTestId('clear-cart'));
    await waitFor(() => {
      expect(screen.getByTestId('items').textContent).toBe('0');
    });

    expect(deleteMock).toHaveBeenCalledTimes(1);
    expect(deleteMock).toHaveBeenCalledWith('api/carritos/carrito-1/items');
  });

  it('treats a 403/404 on clearCart as success: does not restore items and does not show a toast', async () => {
    mockServerWithItem();
    const toastErrorMock = vi.spyOn(toast, 'error').mockImplementation(() => '');
    vi.spyOn(axios, 'delete').mockRejectedValue({ response: { status: 403, data: { detail: 'Access Denied' } } });

    render(
      <CartProvider isAuthenticated={true} login="usuario">
        <ActionsConsumer />
      </CartProvider>,
    );

    await waitFor(() => {
      expect(screen.getByTestId('items').textContent).toBe('1');
    });

    fireEvent.click(screen.getByTestId('clear-cart'));
    await waitFor(() => {
      expect(screen.getByTestId('items').textContent).toBe('0');
    });
    await flush();

    expect(screen.getByTestId('items').textContent).toBe('0');
    expect(toastErrorMock).not.toHaveBeenCalled();
  });

  it('restores items and shows a friendly toast when clearCart fails with a 500', async () => {
    mockServerWithItem();
    const toastErrorMock = vi.spyOn(toast, 'error').mockImplementation(() => '');
    vi.spyOn(axios, 'delete').mockRejectedValue({ response: { status: 500, data: { detail: 'boom' } } });

    render(
      <CartProvider isAuthenticated={true} login="usuario">
        <ActionsConsumer />
      </CartProvider>,
    );

    await waitFor(() => {
      expect(screen.getByTestId('items').textContent).toBe('1');
    });

    fireEvent.click(screen.getByTestId('clear-cart'));
    await waitFor(() => {
      expect(screen.getByTestId('items').textContent).toBe('0');
    });
    await waitFor(() => {
      expect(screen.getByTestId('items').textContent).toBe('1');
    });

    expect(toastErrorMock).toHaveBeenCalledWith('No se pudo vaciar el carrito. Inténtalo de nuevo.');
  });

  it('deduplicates concurrent findOrCreateCarrito calls to a single POST api/carritos', async () => {
    let carritosGetCalls = 0;
    let carritosPostCalls = 0;
    vi.spyOn(axios, 'get').mockImplementation((url: string) => {
      if (String(url).startsWith('api/carritos')) {
        carritosGetCalls += 1;
        return Promise.resolve({ data: [] });
      }
      if (String(url).startsWith('api/cuentas')) return Promise.resolve({ data: [CUENTA] });
      if (String(url).startsWith('api/item-carritos')) return Promise.resolve({ data: [] });
      return Promise.resolve({ data: [] });
    });
    vi.spyOn(axios, 'post').mockImplementation((url: string) => {
      if (String(url) === 'api/carritos') {
        carritosPostCalls += 1;
        return Promise.resolve({ data: { id: 'carrito-1' } });
      }
      return Promise.resolve({ data: { id: `item-${carritosPostCalls}`, cantidad: 1, precioUnitario: 100000 } });
    });

    render(
      <CartProvider isAuthenticated={true} login="usuario">
        <ActionsConsumer />
      </CartProvider>,
    );

    await flush();
    fireEvent.click(screen.getByTestId('add-item-twice'));

    await waitFor(() => {
      expect(screen.getByTestId('items').textContent).toBe('2');
    });

    expect(carritosPostCalls).toBe(2);
    expect(carritosGetCalls).toBe(2);
  });

  it('clears localStorage and local items on logout after a merge', async () => {
    localStorage.setItem('knstore-cart', JSON.stringify([{ id: 'local-1', producto: PRODUCTO, cantidad: 1, precioUnitario: 100000 }]));
    vi.spyOn(axios, 'get').mockImplementation((url: string) => {
      if (String(url).startsWith('api/cuentas')) return Promise.resolve({ data: [CUENTA] });
      if (String(url).startsWith('api/carritos')) return Promise.resolve({ data: [{ id: 'carrito-1' }] });
      if (String(url).startsWith('api/item-carritos')) return Promise.resolve({ data: [] });
      if (String(url).startsWith('api/productos')) return Promise.resolve({ data: [PRODUCTO] });
      return Promise.resolve({ data: [] });
    });
    vi.spyOn(axios, 'post').mockResolvedValue({ data: { id: 'item-1', cantidad: 1, precioUnitario: 100000 } });

    const { rerender } = render(
      <CartProvider isAuthenticated={true} login="usuario">
        <ActionsConsumer />
      </CartProvider>,
    );

    await waitFor(() => {
      expect(localStorage.getItem('knstore-cart')).toBeNull();
    });
    await waitFor(() => {
      expect(screen.getByTestId('items').textContent).toBe('1');
    });

    rerender(
      <CartProvider isAuthenticated={false} login="">
        <ActionsConsumer />
      </CartProvider>,
    );

    await waitFor(() => {
      expect(screen.getByTestId('items').textContent).toBe('0');
    });
    const stored = localStorage.getItem('knstore-cart');
    expect(stored === null || JSON.parse(stored).length === 0).toBe(true);
  });
});

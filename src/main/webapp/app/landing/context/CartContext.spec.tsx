import React from 'react';
import { render, screen, waitFor } from '@testing-library/react';
import { describe, expect, it, vi, beforeEach, afterEach } from 'vitest';
import { CartProvider, useCartContext } from './CartContext';
import { IProductoStorefront } from 'app/landing/model/storefront.model';

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
    const producto: IProductoStorefront = {
      id: 'prod-1',
      nombre: 'Producto A',
      slug: 'producto-a',
      precio: { precioVenta: 100000 },
    } as IProductoStorefront;

    localStorage.setItem(
      'knstore-cart',
      JSON.stringify([
        {
          id: 'item-1',
          producto,
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

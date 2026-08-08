import { describe, expect, it } from 'vitest';
import {
  calculateSubtotal,
  calculateShipping,
  calculateIva,
  calculateTotal,
  DEFAULT_SHIPPING_COST,
  FREE_SHIPPING_THRESHOLD,
} from './checkout';

describe('checkout utils', () => {
  const items = [
    { precioUnitario: 100000, cantidad: 1 },
    { precioUnitario: 50000, cantidad: 2 },
  ];

  it('calculates subtotal from items', () => {
    expect(calculateSubtotal(items)).toBe(200000);
  });

  it('returns free shipping when subtotal reaches threshold', () => {
    expect(calculateShipping(FREE_SHIPPING_THRESHOLD)).toBe(0);
    expect(calculateShipping(FREE_SHIPPING_THRESHOLD + 1)).toBe(0);
  });

  it('returns default shipping cost below threshold', () => {
    expect(calculateShipping(FREE_SHIPPING_THRESHOLD - 1)).toBe(DEFAULT_SHIPPING_COST);
  });

  it('returns correct shipping cost by method', () => {
    expect(calculateShipping(0, 'EXPRESS')).toBe(19900);
    expect(calculateShipping(0, 'PUNTO_PICKUP')).toBe(0);
  });

  it('calculates IVA with default rate', () => {
    expect(calculateIva(100000)).toBe(19000);
  });

  it('calculates total', () => {
    expect(calculateTotal(100000, 0, 19000)).toBe(119000);
  });
});

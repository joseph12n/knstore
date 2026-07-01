/**
 * Utilidades de cálculo para el checkout del storefront.
 * RNF-026: los montos siempre se muestran formateados a 2 decimales en COP.
 */

export const FREE_SHIPPING_THRESHOLD = 150000;
export const DEFAULT_SHIPPING_COST = 9900;

export interface CartItemLike {
  precioUnitario: number;
  cantidad: number;
}

export const calculateSubtotal = (items: CartItemLike[]): number =>
  items.reduce((sum, item) => sum + item.precioUnitario * item.cantidad, 0);

export const calculateShipping = (subtotal: number, method?: string): number => {
  if (subtotal >= FREE_SHIPPING_THRESHOLD) {
    return 0;
  }
  switch (method) {
    case 'EXPRESS':
      return 19900;
    case 'MISMO_DIA':
      return 29900;
    case 'PROGRAMADO':
      return 14900;
    case 'PUNTO_PICKUP':
      return 0;
    default:
      return DEFAULT_SHIPPING_COST;
  }
};

export const calculateIva = (subtotal: number, rate = 0.19): number => Math.round(subtotal * rate);

export const calculateTotal = (subtotal: number, shipping: number, iva: number): number => subtotal + shipping + iva;

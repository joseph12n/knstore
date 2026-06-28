import axios from 'axios';

import { CartItem } from 'app/storefront/model/storefront.model';

export interface CheckoutPayload {
  direccionId: string;
  notasCliente?: string;
  costoEnvio: number;
  metodoPago: string;
  items: {
    productoId: string;
    cantidad: number;
    precioUnitario: number;
    nombreProducto?: string;
    slugProducto?: string;
    marcaProducto?: string;
    skuProducto?: string;
    colorProducto?: string;
    tallaProducto?: string;
  }[];
}

export interface CheckoutResponse {
  pedidoId: string;
  numeroPedido: string;
  total: number;
  estado: string;
}

export const checkout = async (payload: CheckoutPayload): Promise<CheckoutResponse> => {
  const response = await axios.post<CheckoutResponse>('api/pedidos/checkout', payload);
  return response.data;
};

export const buildCheckoutPayload = (
  items: CartItem[],
  direccionId: string,
  costoEnvio: number,
  metodoPago: string,
  notasCliente?: string,
): CheckoutPayload => ({
  direccionId,
  notasCliente,
  costoEnvio,
  metodoPago,
  items: items
    .filter(item => item.producto.id)
    .map(item => ({
      productoId: item.producto.id!,
      cantidad: item.cantidad,
      precioUnitario: item.precioUnitario,
      nombreProducto: item.producto.nombre ?? undefined,
      slugProducto: item.producto.slug ?? undefined,
      marcaProducto: item.producto.marca?.nombre ?? undefined,
      skuProducto: item.producto.sku ?? undefined,
      colorProducto: item.producto.color ?? undefined,
      tallaProducto: item.producto.talla ?? undefined,
    })),
});

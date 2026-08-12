import axios from 'axios';

import { CartItem } from 'app/landing/model/storefront.model';

export interface CheckoutItemPayload {
  productoId: string;
  cantidad: number;
}

export interface CheckoutPayload {
  direccionId: string;
  metodoPago: string;
  tipoServicioEnvio: string;
  notasCliente?: string;
  items: CheckoutItemPayload[];
}

export interface CheckoutPreview {
  subtotal: number;
  iva: number;
  envio: number;
  total: number;
}

export interface CheckoutResponse {
  pedido: { id: string; numeroPedido?: string };
}

export interface IniciarPagoResponse {
  id?: string;
  estado: string;
  descripcionRespuesta?: string;
}

/**
 * Construye el payload del checkout. El precio unitario no se envia: el
 * servidor lo resuelve siempre desde el producto en base de datos.
 */
export const buildCheckoutPayload = (
  items: CartItem[],
  direccionId: string,
  metodoPago: string,
  tipoServicioEnvio: string,
  notasCliente?: string,
): CheckoutPayload => ({
  direccionId,
  metodoPago,
  tipoServicioEnvio,
  notasCliente,
  items: items
    .filter(item => item.producto.id)
    .map(item => ({
      productoId: item.producto.id!,
      cantidad: item.cantidad,
    })),
});

export const checkout = async (payload: CheckoutPayload, signal?: AbortSignal): Promise<CheckoutResponse> => {
  const response = await axios.post<CheckoutResponse>('api/pedidos/checkout', payload, { signal });
  return response.data;
};

export const getPreview = async (payload: CheckoutPayload, signal?: AbortSignal): Promise<CheckoutPreview> => {
  const response = await axios.post<CheckoutPreview>('api/pedidos/preview', payload, { signal });
  return response.data;
};

export const iniciarPago = async (pedidoId: string, signal?: AbortSignal): Promise<IniciarPagoResponse> => {
  const response = await axios.post<IniciarPagoResponse>('api/pagos/iniciar', { pedidoId }, { signal });
  return response.data;
};

/**
 * Constantes del storefront de cliente.
 */

export const STORE_NAME = 'Knstore';

export const CATALOG_PAGE_SIZE = 24;

export const BREAKPOINTS = {
  xs: 0,
  sm: 576,
  md: 768,
  lg: 992,
  xl: 1200,
  xxl: 1400,
};

export const CHECKOUT_STEPS = [
  { key: 'direccion', label: 'Dirección' },
  { key: 'envio', label: 'Envío' },
  { key: 'pago', label: 'Pago' },
  { key: 'confirmacion', label: 'Confirmación' },
];

export const PAYMENT_METHODS = [
  { key: 'CREDIT_CARD', label: 'Tarjeta de crédito', icon: 'faCreditCard' },
  { key: 'DEBIT_CARD', label: 'Tarjeta de débito', icon: 'faCreditCard' },
  { key: 'PSE', label: 'PSE', icon: 'faUniversity' },
  { key: 'CASH', label: 'Efectivo', icon: 'faMoneyBill' },
  { key: 'NEQUI', label: 'Nequi', icon: 'faMobileAlt' },
  { key: 'DAVIPLATA', label: 'Daviplata', icon: 'faMobileAlt' },
  { key: 'EFECTY', label: 'Efecty', icon: 'faStore' },
  { key: 'CONTRA_ENTREGA', label: 'Contra entrega', icon: 'faTruck' },
];

export const SHIPPING_METHODS = [
  { key: 'ESTANDAR', label: 'Estándar', description: '3-5 días hábiles', cost: 9900 },
  { key: 'EXPRESS', label: 'Express', description: '1-2 días hábiles', cost: 19900 },
  { key: 'MISMO_DIA', label: 'Mismo día', description: 'Entrega el mismo día', cost: 29900 },
  { key: 'PROGRAMADO', label: 'Programado', description: 'Elige tu fecha y franja', cost: 14900 },
  { key: 'PICKUP', label: 'Punto Pickup', description: 'Recoge en tienda', cost: 0 },
];

export const ORDER_STATUS_LABELS: Record<string, string> = {
  Pendiente: 'Pendiente',
  Confirmado: 'Confirmado',
  EnProceso: 'En proceso',
  Enviado: 'Enviado',
  Entregado: 'Entregado',
  Cancelado: 'Cancelado',
  Devuelto: 'Devuelto',
};

export const ORDER_STATUS_COLORS: Record<string, string> = {
  Pendiente: 'warning',
  Confirmado: 'info',
  EnProceso: 'primary',
  Enviado: 'info',
  Entregado: 'success',
  Cancelado: 'danger',
  Devuelto: 'secondary',
};

export const PAYMENT_STATUS_LABELS: Record<string, string> = {
  Pendiente: 'Pendiente',
  Aprobado: 'Aprobado',
  Rechazado: 'Rechazado',
  Reembolsado: 'Reembolsado',
  Expirado: 'Expirado',
  Cancelado: 'Cancelado',
};

export const SHIPPING_STATUS_LABELS: Record<string, string> = {
  Pendiente: 'Pendiente',
  Despachado: 'Despachado',
  EnTransito: 'En tránsito',
  EnCiudad: 'En ciudad',
  Entregado: 'Entregado',
  Devuelto: 'Devuelto',
  Perdido: 'Perdido',
};

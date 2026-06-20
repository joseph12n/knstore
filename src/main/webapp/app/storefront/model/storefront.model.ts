/**
 * Tipos extendidos del storefront de cliente. No modifican el modelo de datos
 * generado por JHipster; solo enriquecen la vista con datos que el backend
 * puede exponer ya sea en el DTO de Producto o vía relaciones.
 */

import { IProducto } from 'app/shared/model/producto.model';
import { IProductoImagen } from 'app/shared/model/producto-imagen.model';

export interface IProductoStorefront extends IProducto {
  imagenes?: IProductoImagen[];
}

export interface CartItem {
  id?: string;
  producto: IProductoStorefront;
  cantidad: number;
  precioUnitario: number;
}

export interface CheckoutState {
  direccion?: string;
  metodoEnvio?: string;
  metodoPago?: string;
  notas?: string;
}

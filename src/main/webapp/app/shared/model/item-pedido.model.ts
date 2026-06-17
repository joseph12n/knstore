import { IPedido } from 'app/shared/model/pedido.model';
import { IProducto } from 'app/shared/model/producto.model';

export interface IItemPedido {
  id?: string;
  nombreProducto?: string;
  slugProducto?: string | null;
  marcaProducto?: string | null;
  skuProducto?: string | null;
  colorProducto?: string | null;
  tallaProducto?: string | null;
  cantidad?: number;
  precioUnitario?: number;
  porcentajeIva?: number | null;
  valorIva?: number | null;
  descuento?: number | null;
  subtotal?: number | null;
  pedido?: IPedido;
  producto?: IProducto;
}

export const defaultValue: Readonly<IItemPedido> = {};

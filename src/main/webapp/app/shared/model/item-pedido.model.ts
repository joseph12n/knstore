import { IPedido } from 'app/shared/model/pedido.model';
import { IProducto } from 'app/shared/model/producto.model';
import { IVarianteProducto } from 'app/shared/model/variante-producto.model';

export interface IItemPedido {
  id?: string;
  cantidad?: number;
  precioUnitario?: number;
  porcentajeIva?: number | null;
  valorIva?: number | null;
  descuento?: number | null;
  subtotal?: number | null;
  pedido?: IPedido;
  producto?: IProducto;
  variante?: IVarianteProducto | null;
}

export const defaultValue: Readonly<IItemPedido> = {};

import { ICarrito } from 'app/shared/model/carrito.model';
import { IProducto } from 'app/shared/model/producto.model';

export interface IItemCarrito {
  id?: string;
  cantidad?: number;
  precioUnitario?: number;
  subtotal?: number | null;
  carrito?: ICarrito;
  producto?: IProducto;
}

export const defaultValue: Readonly<IItemCarrito> = {};

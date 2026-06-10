import { ICarrito } from 'app/shared/model/carrito.model';
import { IProducto } from 'app/shared/model/producto.model';
import { IVarianteProducto } from 'app/shared/model/variante-producto.model';

export interface IItemCarrito {
  id?: string;
  cantidad?: number;
  precioUnitario?: number;
  subtotal?: number | null;
  carrito?: ICarrito;
  producto?: IProducto;
  variante?: IVarianteProducto | null;
}

export const defaultValue: Readonly<IItemCarrito> = {};

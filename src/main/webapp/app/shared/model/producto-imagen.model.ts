import { IProducto } from 'app/shared/model/producto.model';

export interface IProductoImagen {
  id?: string;
  imagenContentType?: string;
  imagen?: string;
  imagenUrl?: string | null;
  imagenAlt?: string | null;
  esPrincipal?: boolean;
  producto?: IProducto | null;
}

export const defaultValue: Readonly<IProductoImagen> = {
  esPrincipal: false,
};

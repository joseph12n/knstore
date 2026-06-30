import { IProducto } from 'app/shared/model/producto.model';

export interface IEtiquetaProducto {
  id?: string;
  etiqueta?: string;
  producto?: IProducto;
}

export const defaultValue: Readonly<IEtiquetaProducto> = {};

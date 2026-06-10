import { IProducto } from 'app/shared/model/producto.model';

export interface IVarianteProducto {
  id?: string;
  sku?: string | null;
  color?: string | null;
  talla?: string | null;
  codigoBarras?: string | null;
  stock?: number;
  stockMinimo?: number | null;
  ubicacionBodega?: string | null;
  precioAdicional?: number | null;
  activo?: boolean;
  producto?: IProducto;
}

export const defaultValue: Readonly<IVarianteProducto> = {
  activo: false,
};

import { UbicacionBodega } from 'app/shared/model/enumerations/ubicacion-bodega.model';

export interface IProductoInventario {
  id?: string;
  stock?: number;
  stockMinimo?: number | null;
  ubicacionBodega?: keyof typeof UbicacionBodega | null;
  garantiaMeses?: number | null;
}

export const defaultValue: Readonly<IProductoInventario> = {};

export interface IProductoPrecio {
  id?: string;
  precioCompra?: number;
  precioVenta?: number;
  precioAdicional?: number | null;
  ganancia?: number | null;
}

export const defaultValue: Readonly<IProductoPrecio> = {};

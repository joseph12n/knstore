import { ICategoriaIVA } from 'app/shared/model/categoria-iva.model';
import { ICategoria } from 'app/shared/model/categoria.model';
import { IMarca } from 'app/shared/model/marca.model';
import { IProductoImagen } from 'app/shared/model/producto-imagen.model';
import { IProductoInventario } from 'app/shared/model/producto-inventario.model';
import { IProductoPrecio } from 'app/shared/model/producto-precio.model';
import { ISubcategoria } from 'app/shared/model/subcategoria.model';

export interface IProducto {
  id?: string;
  nombre?: string;
  slug?: string;
  referencia?: string | null;
  sku?: string;
  color?: string | null;
  talla?: string | null;
  codigoBarras?: string | null;
  unidadMedida?: string | null;
  descripcion?: string | null;
  destacado?: boolean;
  activo?: boolean;
  precio?: IProductoPrecio | null;
  inventario?: IProductoInventario | null;
  categoria?: ICategoria;
  subcategoria?: ISubcategoria;
  marca?: IMarca | null;
  categoriaIva?: ICategoriaIVA | null;
  imagenes?: IProductoImagen[] | null;
}

export const defaultValue: Readonly<IProducto> = {
  destacado: false,
  activo: false,
};

import { CategoriaIVA } from 'app/shared/model/enumerations/categoria-iva.model';
import { ISubcategoria } from 'app/shared/model/subcategoria.model';

export interface IProducto {
  id?: string;
  nombre?: string;
  slug?: string;
  descripcion?: string | null;
  imagenContentType?: string | null;
  imagen?: string | null;
  imagenAlt?: string | null;
  marca?: string | null;
  referencia?: string | null;
  codigoBarras?: string | null;
  unidadMedida?: string | null;
  pesoKg?: number | null;
  largoCm?: number | null;
  anchoCm?: number | null;
  altoCm?: number | null;
  categoriaIva?: keyof typeof CategoriaIVA;
  precioCompra?: number;
  precioVenta?: number;
  ganancia?: number | null;
  margen?: number | null;
  garantiaMeses?: number | null;
  destacado?: boolean;
  activo?: boolean;
  subcategoria?: ISubcategoria;
}

export const defaultValue: Readonly<IProducto> = {
  destacado: false,
  activo: false,
};

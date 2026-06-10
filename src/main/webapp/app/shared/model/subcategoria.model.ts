import { ICategoria } from 'app/shared/model/categoria.model';

export interface ISubcategoria {
  id?: string;
  nombre?: string;
  slug?: string;
  descripcion?: string | null;
  imagenContentType?: string | null;
  imagen?: string | null;
  activo?: boolean;
  categoria?: ICategoria;
}

export const defaultValue: Readonly<ISubcategoria> = {
  activo: false,
};

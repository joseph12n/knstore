export interface ICategoria {
  id?: string;
  nombre?: string;
  slug?: string;
  descripcion?: string | null;
  imagenContentType?: string | null;
  imagen?: string | null;
  activo?: boolean;
}

export const defaultValue: Readonly<ICategoria> = {
  activo: false,
};

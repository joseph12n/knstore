import { EstadoIVA } from 'app/shared/model/enumerations/estado-iva.model';

export interface ICategoriaIVA {
  id?: string;
  nombre?: string;
  porcentaje?: number;
  estado?: keyof typeof EstadoIVA;
}

export const defaultValue: Readonly<ICategoriaIVA> = {};

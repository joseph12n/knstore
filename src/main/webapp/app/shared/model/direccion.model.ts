import { ICuenta } from 'app/shared/model/cuenta.model';

export interface IDireccion {
  id?: string;
  direccion?: string;
  barrio?: string | null;
  localidad?: string | null;
  departamento?: string;
  municipio?: string;
  activo?: boolean;
  cuenta?: ICuenta;
}

export const defaultValue: Readonly<IDireccion> = {
  activo: false,
};

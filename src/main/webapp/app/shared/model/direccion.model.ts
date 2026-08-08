import { ICuenta } from 'app/shared/model/cuenta.model';

export interface IDireccion {
  id?: string;
  direccion?: string;
  barrio?: string | null;
  localidad?: string | null;
  municipio?: string;
  departamento?: string;
  activo?: boolean;
  telefonoContacto?: string | null;
  destinatario?: string | null;
  codigoPostal?: string | null;
  cuenta?: ICuenta;
}

export const defaultValue: Readonly<IDireccion> = {
  activo: false,
};

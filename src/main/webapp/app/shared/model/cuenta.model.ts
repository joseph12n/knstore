import dayjs from 'dayjs';

import { Genero } from 'app/shared/model/enumerations/genero.model';
import { ITipoDocumento } from 'app/shared/model/tipo-documento.model';
import { IUser } from 'app/shared/model/user.model';

export interface ICuenta {
  id?: string;
  numDocumento?: string | null;
  primerNombre?: string;
  segundoNombre?: string | null;
  primerApellido?: string;
  segundoApellido?: string | null;
  genero?: keyof typeof Genero | null;
  fechaNacimiento?: dayjs.Dayjs | null;
  celular?: string | null;
  telefono?: string | null;
  fotoPerfilContentType?: string | null;
  fotoPerfil?: string | null;
  activo?: boolean;
  user?: IUser;
  tipoDocumento?: ITipoDocumento | null;
}

export const defaultValue: Readonly<ICuenta> = {
  activo: false,
};

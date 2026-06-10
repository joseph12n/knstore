import dayjs from 'dayjs';

import { Genero } from 'app/shared/model/enumerations/genero.model';
import { TipoPersona } from 'app/shared/model/enumerations/tipo-persona.model';
import { ITipoDocumento } from 'app/shared/model/tipo-documento.model';
import { IUser } from 'app/shared/model/user.model';

export interface ICuenta {
  id?: string;
  numDocumento?: string | null;
  tipoPersona?: keyof typeof TipoPersona;
  primerNombre?: string;
  segundoNombre?: string | null;
  primerApellido?: string;
  segundoApellido?: string | null;
  celular?: string | null;
  telefono?: string | null;
  fechaNacimiento?: dayjs.Dayjs | null;
  genero?: keyof typeof Genero | null;
  fotoPerfilContentType?: string | null;
  fotoPerfil?: string | null;
  activo?: boolean;
  user?: IUser;
  tipoDocumento?: ITipoDocumento | null;
}

export const defaultValue: Readonly<ICuenta> = {
  activo: false,
};

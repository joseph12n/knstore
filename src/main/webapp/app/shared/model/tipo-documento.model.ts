import { EstadoTipoDocumento } from 'app/shared/model/enumerations/estado-tipo-documento.model';

export interface ITipoDocumento {
  id?: string;
  sigla?: string;
  nombreTipo?: string;
  estado?: keyof typeof EstadoTipoDocumento;
}

export const defaultValue: Readonly<ITipoDocumento> = {};

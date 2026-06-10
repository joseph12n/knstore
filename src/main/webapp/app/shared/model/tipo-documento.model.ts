import { EstadoTipoDocumento } from 'app/shared/model/enumerations/estado-tipo-documento.model';

export interface ITipoDocumento {
  id?: string;
  estado?: keyof typeof EstadoTipoDocumento;
  sigla?: string;
  nombreTipo?: string;
}

export const defaultValue: Readonly<ITipoDocumento> = {};

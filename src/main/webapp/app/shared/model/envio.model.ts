import dayjs from 'dayjs';

import { EstadoEnvio } from 'app/shared/model/enumerations/estado-envio.model';
import { TipoServicioEnvio } from 'app/shared/model/enumerations/tipo-servicio-envio.model';

export interface IEnvio {
  id?: string;
  numeroRastreo?: string | null;
  transportadora?: string | null;
  tipoServicio?: keyof typeof TipoServicioEnvio | null;
  estado?: keyof typeof EstadoEnvio;
  costoEnvio?: number | null;
  pesoKg?: number | null;
  valorDeclarado?: number | null;
  urlRastreo?: string | null;
  observaciones?: string | null;
  fechaDespacho?: dayjs.Dayjs | null;
  fechaEntregaEstimada?: dayjs.Dayjs | null;
  fechaEntrega?: dayjs.Dayjs | null;
}

export const defaultValue: Readonly<IEnvio> = {};

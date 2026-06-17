import dayjs from 'dayjs';

import { EstadoEnvio } from 'app/shared/model/enumerations/estado-envio.model';
import { TipoServicioEnvio } from 'app/shared/model/enumerations/tipo-servicio-envio.model';
import { IPedido } from 'app/shared/model/pedido.model';

export interface IEnvio {
  id?: string;
  transportadora?: string | null;
  numeroRastreo?: string | null;
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
  pedido?: IPedido;
}

export const defaultValue: Readonly<IEnvio> = {};

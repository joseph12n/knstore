import dayjs from 'dayjs';

import { EstadoPago } from 'app/shared/model/enumerations/estado-pago.model';
import { MetodoPago } from 'app/shared/model/enumerations/metodo-pago.model';
import { IPedido } from 'app/shared/model/pedido.model';

export interface IPago {
  id?: string;
  metodoPago?: keyof typeof MetodoPago;
  estado?: keyof typeof EstadoPago;
  monto?: number;
  referenciaPasarela?: string | null;
  codigoAutorizacion?: string | null;
  descripcionRespuesta?: string | null;
  intentos?: number | null;
  fechaPago?: dayjs.Dayjs | null;
  pedido?: IPedido;
}

export const defaultValue: Readonly<IPago> = {};

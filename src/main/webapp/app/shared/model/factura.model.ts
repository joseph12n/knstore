import dayjs from 'dayjs';

import { IPago } from 'app/shared/model/pago.model';

export interface IFactura {
  id?: string;
  prefijo?: string | null;
  cufe?: string | null;
  subtotal?: number;
  descuentos?: number | null;
  baseGravableIva?: number | null;
  valorIva?: number | null;
  total?: number;
  notasAdicionales?: string | null;
  codigoQr?: string | null;
  enviada?: boolean;
  fechaEmision?: dayjs.Dayjs | null;
  fechaVencimiento?: dayjs.Dayjs | null;
  fechaEnvioEmail?: dayjs.Dayjs | null;
  pago?: IPago;
}

export const defaultValue: Readonly<IFactura> = {
  enviada: false,
};

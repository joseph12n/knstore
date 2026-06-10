import dayjs from 'dayjs';

import { IPedido } from 'app/shared/model/pedido.model';

export interface IFactura {
  id?: string;
  referencia?: string;
  cufe?: string | null;
  resolucionDian?: string | null;
  fechaVigenciaResolucion?: dayjs.Dayjs | null;
  prefijo?: string | null;
  consecutivo?: number | null;
  subtotal?: number;
  descuentos?: number | null;
  baseGravableIva?: number | null;
  valorIva?: number | null;
  retefuente?: number | null;
  reteIva?: number | null;
  reteIca?: number | null;
  total?: number;
  fechaEmision?: dayjs.Dayjs | null;
  fechaVencimiento?: dayjs.Dayjs | null;
  notasAdicionales?: string | null;
  codigoQr?: string | null;
  enviada?: boolean;
  fechaEnvioEmail?: dayjs.Dayjs | null;
  pedido?: IPedido;
}

export const defaultValue: Readonly<IFactura> = {
  enviada: false,
};

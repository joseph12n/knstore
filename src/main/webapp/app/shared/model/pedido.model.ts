import { ICuenta } from 'app/shared/model/cuenta.model';
import { IDireccion } from 'app/shared/model/direccion.model';
import { EstadoPedido } from 'app/shared/model/enumerations/estado-pedido.model';
import { IEnvio } from 'app/shared/model/envio.model';

export interface IPedido {
  id?: string;
  numeroPedido?: string;
  estado?: keyof typeof EstadoPedido;
  subtotal?: number;
  descuento?: number | null;
  ivaTotal?: number | null;
  costoEnvio?: number | null;
  total?: number;
  notasCliente?: string | null;
  notasInternas?: string | null;
  ipOrigen?: string | null;
  userAgent?: string | null;
  direccion?: IDireccion;
  envio?: IEnvio;
  cuenta?: ICuenta;
}

export const defaultValue: Readonly<IPedido> = {};

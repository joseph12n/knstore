import dayjs from 'dayjs';

import { ICuenta } from 'app/shared/model/cuenta.model';

export interface ICarrito {
  id?: string;
  subtotal?: number | null;
  fechaActualizacion?: dayjs.Dayjs | null;
  cuenta?: ICuenta;
}

export const defaultValue: Readonly<ICarrito> = {};

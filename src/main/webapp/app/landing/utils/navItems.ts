import { IconProp } from '@fortawesome/fontawesome-svg-core';
import {
  faCreditCard,
  faFileInvoice,
  faHome,
  faLock,
  faMapMarkerAlt,
  faShoppingBag,
  faShoppingCart,
  faTruck,
  faUser,
} from '@fortawesome/free-solid-svg-icons';

export interface AccountNavItem {
  to: string;
  label: string;
  icon: IconProp;
  end?: boolean;
}

export const accountNavItems: AccountNavItem[] = [
  { to: '/mi-cuenta', label: 'Mi cuenta', icon: faHome, end: true },
  { to: '/mi-cuenta/perfil', label: 'Perfil', icon: faUser },
  { to: '/mi-cuenta/direcciones', label: 'Direcciones', icon: faMapMarkerAlt },
  { to: '/mi-cuenta/pedidos', label: 'Pedidos', icon: faShoppingBag },
  { to: '/mi-cuenta/pagos', label: 'Pagos', icon: faCreditCard },
  { to: '/mi-cuenta/envios', label: 'Envíos', icon: faTruck },
  { to: '/mi-cuenta/facturas', label: 'Facturas', icon: faFileInvoice },
  { to: '/mi-cuenta/seguridad', label: 'Seguridad', icon: faLock },
];

export const accountExternalNavItems: AccountNavItem[] = [{ to: '/carrito', label: 'Mi carrito', icon: faShoppingCart }];

export const isAccountNavActive = (pathname: string, item: AccountNavItem): boolean => {
  if (item.end) {
    return pathname === item.to || pathname === `${item.to}/`;
  }
  return pathname.startsWith(item.to);
};

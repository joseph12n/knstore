import React from 'react';
import { Nav } from 'react-bootstrap';
import { Link, useLocation } from 'react-router';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import {
  faHome,
  faUser,
  faMapMarkerAlt,
  faShoppingBag,
  faCreditCard,
  faTruck,
  faFileInvoice,
  faLock,
} from '@fortawesome/free-solid-svg-icons';

interface NavItem {
  to: string;
  label: string;
  icon: any;
  end?: boolean;
}

const navItems: NavItem[] = [
  { to: '/mi-cuenta', label: 'Inicio', icon: faHome, end: true },
  { to: '/mi-cuenta/perfil', label: 'Perfil', icon: faUser },
  { to: '/mi-cuenta/direcciones', label: 'Direcciones', icon: faMapMarkerAlt },
  { to: '/mi-cuenta/pedidos', label: 'Pedidos', icon: faShoppingBag },
  { to: '/mi-cuenta/pagos', label: 'Pagos', icon: faCreditCard },
  { to: '/mi-cuenta/envios', label: 'Envíos', icon: faTruck },
  { to: '/mi-cuenta/facturas', label: 'Facturas', icon: faFileInvoice },
  { to: '/mi-cuenta/seguridad', label: 'Seguridad', icon: faLock },
];

export const AccountMobileNav = () => {
  const location = useLocation();

  const isActive = (item: NavItem) => {
    if (item.end) {
      return location.pathname === item.to || location.pathname === `${item.to}/`;
    }
    return location.pathname.startsWith(item.to);
  };

  return (
    <Nav className="account-mobile-nav flex-nowrap overflow-auto pb-2 d-lg-none" style={{ gap: '0.5rem' }}>
      {navItems.map(item => {
        const active = isActive(item);
        return (
          <Nav.Link
            key={item.to}
            as={Link as any}
            to={item.to}
            className={`d-flex align-items-center gap-2 rounded-3 flex-shrink-0 ${active ? 'active' : ''}`}
            style={{
              color: active ? 'var(--kn-color-text-inverse)' : 'var(--kn-color-text)',
              backgroundColor: active ? 'var(--kn-color-primary)' : 'var(--kn-color-surface)',
              border: active ? 'none' : '1px solid var(--kn-color-border)',
              padding: '0.5rem 0.75rem',
              whiteSpace: 'nowrap',
            }}
          >
            <FontAwesomeIcon icon={item.icon} size="sm" />
            <span className="small">{item.label}</span>
          </Nav.Link>
        );
      })}
    </Nav>
  );
};

export default AccountMobileNav;

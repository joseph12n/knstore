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
  faSignOutAlt,
  faShoppingCart,
} from '@fortawesome/free-solid-svg-icons';

import { logout } from 'app/shared/reducers/authentication';
import { useAppDispatch } from 'app/config/store';

interface NavItem {
  to: string;
  label: string;
  icon: any;
  end?: boolean;
}

const navItems: NavItem[] = [
  { to: '/cuenta', label: 'Mi cuenta', icon: faHome, end: true },
  { to: '/cuenta/perfil', label: 'Perfil', icon: faUser },
  { to: '/cuenta/direcciones', label: 'Direcciones', icon: faMapMarkerAlt },
  { to: '/cuenta/pedidos', label: 'Pedidos', icon: faShoppingBag },
  { to: '/cuenta/pagos', label: 'Pagos', icon: faCreditCard },
  { to: '/cuenta/envios', label: 'Envíos', icon: faTruck },
  { to: '/cuenta/facturas', label: 'Facturas', icon: faFileInvoice },
  { to: '/cuenta/seguridad', label: 'Seguridad', icon: faLock },
];

const externalNavItems: NavItem[] = [{ to: '/carrito', label: 'Mi carrito', icon: faShoppingCart }];

export const AccountSidebar = () => {
  const location = useLocation();
  const dispatch = useAppDispatch();

  const isActive = (item: NavItem) => {
    if (item.end) {
      return location.pathname === item.to || location.pathname === `${item.to}/`;
    }
    return location.pathname.startsWith(item.to);
  };

  return (
    <aside className="account-sidebar p-3 rounded" style={{ backgroundColor: 'var(--kn-color-surface)', minWidth: '260px' }}>
      <h5 className="fw-bold px-3 mb-3">Mi cuenta</h5>
      <Nav className="flex-column gap-1">
        {navItems.map(item => {
          const active = isActive(item);
          return (
            <Nav.Link
              key={item.to}
              as={Link as any}
              to={item.to}
              className={`d-flex align-items-center gap-2 rounded ${active ? 'active' : ''}`}
              style={{
                color: active ? 'var(--kn-color-text-inverse)' : 'var(--kn-color-text)',
                backgroundColor: active ? 'var(--kn-color-primary)' : 'transparent',
                padding: '0.75rem 1rem',
              }}
            >
              <FontAwesomeIcon icon={item.icon} fixedWidth />
              <span>{item.label}</span>
            </Nav.Link>
          );
        })}
        <hr className="my-2" />
        {externalNavItems.map(item => {
          const active = isActive(item);
          return (
            <Nav.Link
              key={item.to}
              as={Link as any}
              to={item.to}
              className={`d-flex align-items-center gap-2 rounded ${active ? 'active' : ''}`}
              style={{
                color: active ? 'var(--kn-color-text-inverse)' : 'var(--kn-color-text)',
                backgroundColor: active ? 'var(--kn-color-primary)' : 'transparent',
                padding: '0.75rem 1rem',
              }}
            >
              <FontAwesomeIcon icon={item.icon} fixedWidth />
              <span>{item.label}</span>
            </Nav.Link>
          );
        })}
        <hr className="my-2" />
        <Nav.Link
          as="button"
          onClick={() => dispatch(logout())}
          className="d-flex align-items-center gap-2 rounded border-0 bg-transparent w-100 text-start"
          style={{ padding: '0.75rem 1rem', color: 'var(--kn-color-danger)' }}
        >
          <FontAwesomeIcon icon={faSignOutAlt} fixedWidth />
          <span>Cerrar sesión</span>
        </Nav.Link>
      </Nav>
    </aside>
  );
};

export default AccountSidebar;

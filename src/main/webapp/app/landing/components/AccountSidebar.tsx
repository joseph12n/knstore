import React from 'react';
import { Nav } from 'react-bootstrap';
import { Link, useLocation } from 'react-router';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSignOutAlt } from '@fortawesome/free-solid-svg-icons';

import { logout } from 'app/shared/reducers/authentication';
import { useAppDispatch } from 'app/config/store';
import { AccountNavItem, accountExternalNavItems, accountNavItems, isAccountNavActive } from 'app/landing/utils/navItems';

export const AccountSidebar = () => {
  const location = useLocation();
  const dispatch = useAppDispatch();

  const renderNavItem = (item: AccountNavItem) => {
    const active = isAccountNavActive(location.pathname, item);
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
  };

  return (
    <aside className="account-sidebar p-3 rounded" style={{ backgroundColor: 'var(--kn-color-surface)', minWidth: '260px' }}>
      <h5 className="fw-bold px-3 mb-3">Mi cuenta</h5>
      <Nav className="flex-column gap-1">
        {accountNavItems.map(renderNavItem)}
        <hr className="my-2" />
        {accountExternalNavItems.map(renderNavItem)}
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

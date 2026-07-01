import React from 'react';
import { Outlet } from 'react-router';

import StorefrontLayout from './StorefrontLayout';
import { useCatalog } from 'app/landing/hooks/useCatalog';
import { CartProvider } from 'app/landing/context/CartContext';
import { useAppSelector } from 'app/config/store';

const LandingLayout = () => {
  const { categorias, subcategorias } = useCatalog({ page: 0, size: 100, sort: 'nombre,asc' });
  const isAuthenticated = useAppSelector(state => state.authentication.isAuthenticated);
  const account = useAppSelector(state => state.authentication.account);

  return (
    <CartProvider isAuthenticated={isAuthenticated} login={account?.login}>
      <StorefrontLayout categorias={categorias} subcategorias={subcategorias}>
        <Outlet />
      </StorefrontLayout>
    </CartProvider>
  );
};

export default LandingLayout;

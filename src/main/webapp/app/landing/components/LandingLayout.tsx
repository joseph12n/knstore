import React from 'react';
import { Outlet } from 'react-router';

import StorefrontLayout from './StorefrontLayout';
import { useCatalog } from 'app/landing/hooks/useCatalog';

const LandingLayout = () => {
  const { categorias, subcategorias } = useCatalog({ page: 0, size: 12, sort: 'nombre,asc' });

  return (
    <StorefrontLayout categorias={categorias} subcategorias={subcategorias}>
      <Outlet />
    </StorefrontLayout>
  );
};

export default LandingLayout;

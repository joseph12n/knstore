import React from 'react';

import StoreHeader from './StoreHeader';
import StoreFooter from './StoreFooter';
import { ICategoria } from 'app/shared/model/categoria.model';
import { ISubcategoria } from 'app/shared/model/subcategoria.model';

interface StorefrontLayoutProps {
  children: React.ReactNode;
  categorias: ICategoria[];
  subcategorias: ISubcategoria[];
}

export const StorefrontLayout = ({ children, categorias, subcategorias }: StorefrontLayoutProps) => (
  <div className="storefront d-flex flex-column min-vh-100">
    <StoreHeader categorias={categorias} subcategorias={subcategorias} />
    <main className="flex-grow-1">{children}</main>
    <StoreFooter />
  </div>
);

export default StorefrontLayout;

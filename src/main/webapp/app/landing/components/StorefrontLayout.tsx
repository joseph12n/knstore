import React, { useEffect, useState } from 'react';

import StoreHeader from './StoreHeader';
import StoreFooter from './StoreFooter';
import { ICategoria } from 'app/shared/model/categoria.model';
import { ISubcategoria } from 'app/shared/model/subcategoria.model';

interface StorefrontLayoutProps {
  children: React.ReactNode;
  categorias: ICategoria[];
  subcategorias: ISubcategoria[];
}

type Tema = 'light' | 'dark';

const TEMA_KEY = 'kn-theme';

const getTemaInicial = (): Tema => {
  const guardado = window.localStorage.getItem(TEMA_KEY);
  if (guardado === 'light' || guardado === 'dark') {
    return guardado;
  }
  return window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light';
};

export const StorefrontLayout = ({ children, categorias, subcategorias }: StorefrontLayoutProps) => {
  const [tema, setTema] = useState<Tema>(getTemaInicial);

  useEffect(() => {
    window.localStorage.setItem(TEMA_KEY, tema);
  }, [tema]);

  return (
    <div className="storefront d-flex flex-column min-vh-100" data-theme={tema}>
      <StoreHeader
        categorias={categorias}
        subcategorias={subcategorias}
        tema={tema}
        onToggleTema={() => setTema(actual => (actual === 'dark' ? 'light' : 'dark'))}
      />
      <main className="flex-grow-1">{children}</main>
      <StoreFooter />
    </div>
  );
};

export default StorefrontLayout;

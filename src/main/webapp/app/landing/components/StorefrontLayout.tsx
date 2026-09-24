import React, { useEffect, useState } from 'react';
import { useLocation } from 'react-router';
import { toast } from 'react-toastify';

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
  const location = useLocation();

  useEffect(() => {
    window.localStorage.setItem(TEMA_KEY, tema);
  }, [tema]);

  // Feedback tras el redirect del login cuando el panel admin no está disponible en móvil.
  // El state del router vive solo en esa entrada de navegación: el efecto corre una sola vez.
  useEffect(() => {
    const state = location.state as { avisoPanelEscritorio?: boolean } | null;
    if (state?.avisoPanelEscritorio) {
      toast.info('El panel administrativo solo está disponible desde escritorio.');
    }
  }, [location.state]);

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

import React, { useState } from 'react';
import { Link } from 'react-router';
import { Button } from 'react-bootstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faDesktop } from '@fortawesome/free-solid-svg-icons';

type Tema = 'light' | 'dark';

const TEMA_KEY = 'kn-theme';

// Mismo patrón de tema que LoginPage: kn-theme en localStorage con fallback a prefers-color-scheme.
const getTemaInicial = (): Tema => {
  const guardado = window.localStorage.getItem(TEMA_KEY);
  if (guardado === 'light' || guardado === 'dark') {
    return guardado;
  }
  return window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light';
};

/**
 * Aviso a pantalla completa cuando el usuario abre el panel administrativo
 * desde un viewport móvil (<= 991.98px). Reemplaza al AdminLayout en ese caso.
 */
export const DesktopOnlyNotice = () => {
  const [tema] = useState<Tema>(getTemaInicial);

  return (
    <div className="storefront min-vh-100 d-flex align-items-center justify-content-center p-4" data-theme={tema}>
      <main className="text-center" style={{ maxWidth: '32rem' }}>
        <div
          className="rounded-circle d-inline-flex align-items-center justify-content-center mb-4"
          style={{ width: '4.5rem', height: '4.5rem', backgroundColor: 'var(--kn-color-surface)', color: 'var(--kn-color-accent)' }}
        >
          <FontAwesomeIcon icon={faDesktop} size="2x" />
        </div>
        <h1 className="fw-bold mb-3">El panel está disponible desde escritorio</h1>
        <p className="mb-3" style={{ color: 'var(--kn-color-text-secondary)' }}>
          El panel administrativo está diseñado para pantallas grandes. Ábrelo desde tu computador para gestionar pedidos, catálogo e
          inventario.
        </p>
        <p className="small text-muted mb-4">Si tienes una pantalla pequeña, gírala o amplía la ventana para ver el panel.</p>
        <Button as={Link as any} to="/" variant="primary" className="w-100 d-md-inline">
          Volver a la tienda
        </Button>
      </main>
    </div>
  );
};

export default DesktopOnlyNotice;

import React from 'react';
import { render, screen } from '@testing-library/react';
import { MemoryRouter } from 'react-router';
import { describe, expect, it } from 'vitest';

import { OrderCard } from 'app/landing/components/OrderCard';

const pedidoBase = { id: 'p1', numeroPedido: 'PED-T-000001', total: 100000, costoEnvio: 0, createdDate: new Date().toISOString() } as never;

const renderCard = (pedido: Record<string, unknown>, pago?: Record<string, unknown>) =>
  render(
    <MemoryRouter>
      <OrderCard pedido={pedido as never} pago={pago as never} onCancel={() => {}} />
    </MemoryRouter>,
  );

describe('OrderCard', () => {
  it('muestra estado Cancelado y oculta el boton para pedidos cancelados', () => {
    renderCard({ ...pedidoBase, estado: 'CANCELLED' });
    expect(screen.getByText('Cancelado')).toBeDefined();
    expect(screen.queryByRole('button', { name: /cancelar/i })).toBeNull();
  });

  it('muestra Pago no aprobado cuando la pasarela rechazo el pago', () => {
    renderCard({ ...pedidoBase, estado: 'PENDING' }, { id: 'pg1', estado: 'REJECTED', pedido: { id: 'p1' } });
    expect(screen.getByText('Pago no aprobado')).toBeDefined();
    expect(screen.getByText('Pendiente')).toBeDefined();
  });

  it('muestra Reembolsado cuando el pago fue reembolsado', () => {
    renderCard({ ...pedidoBase, estado: 'CONFIRMED' }, { id: 'pg1', estado: 'REFUNDED', pedido: { id: 'p1' } });
    expect(screen.getByText('Reembolsado')).toBeDefined();
  });

  it('muestra Pago pendiente y permite cancelar en pedidos PENDING sin pago', () => {
    renderCard({ ...pedidoBase, estado: 'PENDING' });
    expect(screen.getByText('Pago pendiente')).toBeDefined();
    expect(screen.getByRole('button', { name: 'Cancelar' })).toBeDefined();
  });

  it('permite cancelar dentro de la ventana aunque el pago este aprobado', () => {
    renderCard({ ...pedidoBase, estado: 'CONFIRMED' }, { id: 'pg1', estado: 'APPROVED', pedido: { id: 'p1' } });
    expect(screen.queryByText(/Pago pendiente|Pago no aprobado|Reembolsado/)).toBeNull();
    expect(screen.getByRole('button', { name: 'Cancelar' })).toBeDefined();
  });

  it('oculta el boton cancelar cuando vencio la ventana de 1 hora', () => {
    renderCard({ ...pedidoBase, estado: 'CONFIRMED', createdDate: new Date(Date.now() - 2 * 60 * 60 * 1000).toISOString() });
    expect(screen.queryByRole('button', { name: 'Cancelar' })).toBeNull();
  });
});

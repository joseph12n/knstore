import { describe, expect, it } from 'vitest';

import { getNombreCliente, VALID_TRANSITIONS } from './AdminOrdersPage';

describe('VALID_TRANSITIONS (espejo de la maquina de estados del backend)', () => {
  it('PENDING solo puede confirmar o cancelar', () => {
    expect(VALID_TRANSITIONS.PENDING).toEqual(['CONFIRMED', 'CANCELLED']);
  });

  it('CONFIRMED puede enviarse o cancelarse, nunca volver a PROCESSING', () => {
    expect(VALID_TRANSITIONS.CONFIRMED).toEqual(['SHIPPED', 'CANCELLED']);
    expect(VALID_TRANSITIONS.CONFIRMED).not.toContain('PROCESSING');
  });

  it('PROCESSING puede enviarse o cancelarse', () => {
    expect(VALID_TRANSITIONS.PROCESSING).toEqual(['SHIPPED', 'CANCELLED']);
  });

  it('SHIPPED solo puede entregarse', () => {
    expect(VALID_TRANSITIONS.SHIPPED).toEqual(['DELIVERED']);
  });

  it('DELIVERED solo puede devolverse', () => {
    expect(VALID_TRANSITIONS.DELIVERED).toEqual(['RETURNED']);
  });

  it('CANCELLED y RETURNED son terminales', () => {
    expect(VALID_TRANSITIONS.CANCELLED).toEqual([]);
    expect(VALID_TRANSITIONS.RETURNED).toEqual([]);
  });

  it('ninguna transicion permite quedarse en el mismo estado', () => {
    for (const [estado, destinos] of Object.entries(VALID_TRANSITIONS)) {
      expect(destinos).not.toContain(estado);
    }
  });
});

describe('getNombreCliente', () => {
  it('compone el nombre completo desde la Cuenta', () => {
    const pedido = {
      cuenta: { primerNombre: 'Ana', segundoNombre: 'Maria', primerApellido: 'Lopez', user: { login: 'ana' } },
    };
    expect(getNombreCliente(pedido as never)).toBe('Ana Maria Lopez');
  });

  it('cae al login cuando la Cuenta no tiene nombres', () => {
    const pedido = { cuenta: { user: { login: 'solo-login' } } };
    expect(getNombreCliente(pedido as never)).toBe('solo-login');
  });

  it('cae a guion cuando no hay Cuenta ni usuario', () => {
    const pedido = {};
    expect(getNombreCliente(pedido as never)).toBe('-');
  });
});

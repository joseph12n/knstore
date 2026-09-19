import React from 'react';
import { render, screen } from '@testing-library/react';
import { describe, expect, it } from 'vitest';

import BenefitsBar from './BenefitsBar';

describe('BenefitsBar', () => {
  it('renderiza los cuatro beneficios de la tienda', () => {
    render(<BenefitsBar />);

    expect(screen.getByText('Envío gratis')).toBeDefined();
    expect(screen.getByText('Pagos seguros')).toBeDefined();
    expect(screen.getByText('Garantía')).toBeDefined();
    expect(screen.getByText('Soporte')).toBeDefined();
  });
});

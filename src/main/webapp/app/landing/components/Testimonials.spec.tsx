import React from 'react';
import { render, screen } from '@testing-library/react';
import { describe, expect, it } from 'vitest';

import Testimonials from './Testimonials';

describe('Testimonials', () => {
  it('muestra el título de la sección y tres testimonios verificados', () => {
    render(<Testimonials />);

    expect(screen.getByText('Lo que dicen nuestros clientes')).toBeDefined();
    expect(screen.getAllByText(/Compra verificada/i)).toHaveLength(3);
  });
});

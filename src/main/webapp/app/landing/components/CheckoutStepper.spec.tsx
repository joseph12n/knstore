import React from 'react';
import { render, screen } from '@testing-library/react';
import { describe, expect, it } from 'vitest';
import CheckoutStepper from './CheckoutStepper';

describe('CheckoutStepper', () => {
  it('renders all steps', () => {
    render(<CheckoutStepper currentStep={0} />);
    expect(screen.getByText('1')).toBeTruthy();
    expect(screen.getByText('2')).toBeTruthy();
    expect(screen.getByText('3')).toBeTruthy();
    expect(screen.getByText('4')).toBeTruthy();
  });

  it('marks completed steps with checkmark', () => {
    render(<CheckoutStepper currentStep={2} />);
    expect(screen.getAllByText('✓').length).toBe(2);
  });

  it('renders step labels', () => {
    render(<CheckoutStepper currentStep={0} />);
    expect(screen.getByText('Dirección')).toBeTruthy();
    expect(screen.getByText('Envío')).toBeTruthy();
    expect(screen.getByText('Pago')).toBeTruthy();
    expect(screen.getByText('Confirmación')).toBeTruthy();
  });
});

import React from 'react';
import { fireEvent, render, screen } from '@testing-library/react';
import { describe, expect, it, vi } from 'vitest';
import AddressForm from './AddressForm';

const fillRequiredFields = (container: HTMLElement) => {
  fireEvent.change(container.querySelector('input[name="destinatario"]')!, { target: { value: 'Ana Gomez' } });
  fireEvent.change(container.querySelector('input[name="direccion"]')!, { target: { value: 'Calle 1 #2-3' } });
  fireEvent.change(container.querySelector('input[name="municipio"]')!, { target: { value: 'Bogota' } });
  fireEvent.change(container.querySelector('input[name="departamento"]')!, { target: { value: 'Cundinamarca' } });
  fireEvent.change(container.querySelector('input[name="telefonoContacto"]')!, { target: { value: '3001234567' } });
  fireEvent.change(container.querySelector('input[name="codigoPostal"]')!, { target: { value: '110111' } });
};

describe('AddressForm', () => {
  it('valida que el teléfono de contacto solo acepte dígitos', async () => {
    const onSubmit = vi.fn();
    const { container } = render(<AddressForm onSubmit={onSubmit} onCancel={() => {}} />);

    fillRequiredFields(container);
    fireEvent.change(container.querySelector('input[name="telefonoContacto"]')!, { target: { value: '12345ABC' } });
    fireEvent.submit(container.querySelector('form')!);

    expect(await screen.findByText('Debe tener entre 7 y 15 dígitos.')).toBeTruthy();
    expect(onSubmit).not.toHaveBeenCalled();
  });

  it('valida que el código postal solo acepte dígitos', async () => {
    const onSubmit = vi.fn();
    const { container } = render(<AddressForm onSubmit={onSubmit} onCancel={() => {}} />);

    fillRequiredFields(container);
    fireEvent.change(container.querySelector('input[name="codigoPostal"]')!, { target: { value: 'ABC-123' } });
    fireEvent.submit(container.querySelector('form')!);

    expect(await screen.findByText('Solo se permiten números.')).toBeTruthy();
    expect(onSubmit).not.toHaveBeenCalled();
  });

  it('valida que el destinatario solo acepte letras', async () => {
    const onSubmit = vi.fn();
    const { container } = render(<AddressForm onSubmit={onSubmit} onCancel={() => {}} />);

    fillRequiredFields(container);
    fireEvent.change(container.querySelector('input[name="destinatario"]')!, { target: { value: 'Ana123' } });
    fireEvent.submit(container.querySelector('form')!);

    expect(await screen.findByText('Solo se permiten letras.')).toBeTruthy();
    expect(onSubmit).not.toHaveBeenCalled();
  });

  it('valida que la dirección contenga al menos una letra', async () => {
    const onSubmit = vi.fn();
    const { container } = render(<AddressForm onSubmit={onSubmit} onCancel={() => {}} />);

    fillRequiredFields(container);
    fireEvent.change(container.querySelector('input[name="direccion"]')!, { target: { value: '12345' } });
    fireEvent.submit(container.querySelector('form')!);

    expect(await screen.findByText('La dirección debe contener al menos una letra.')).toBeTruthy();
    expect(onSubmit).not.toHaveBeenCalled();
  });

  it('valida que el barrio solo acepte letras', async () => {
    const onSubmit = vi.fn();
    const { container } = render(<AddressForm onSubmit={onSubmit} onCancel={() => {}} />);

    fillRequiredFields(container);
    fireEvent.change(container.querySelector('input[name="barrio"]')!, { target: { value: '123' } });
    fireEvent.submit(container.querySelector('form')!);

    expect(await screen.findByText('Solo se permiten letras.')).toBeTruthy();
    expect(onSubmit).not.toHaveBeenCalled();
  });

  it('valida que el municipio solo acepte letras', async () => {
    const onSubmit = vi.fn();
    const { container } = render(<AddressForm onSubmit={onSubmit} onCancel={() => {}} />);

    fillRequiredFields(container);
    fireEvent.change(container.querySelector('input[name="municipio"]')!, { target: { value: 'Bogota2026' } });
    fireEvent.submit(container.querySelector('form')!);

    expect(await screen.findByText('Solo se permiten letras.')).toBeTruthy();
    expect(onSubmit).not.toHaveBeenCalled();
  });

  it('envía el formulario con valores válidos', async () => {
    const onSubmit = vi.fn();
    const { container } = render(<AddressForm onSubmit={onSubmit} onCancel={() => {}} />);

    fillRequiredFields(container);
    fireEvent.submit(container.querySelector('form')!);

    expect(await screen.findByRole('button', { name: 'Guardar dirección' })).toBeTruthy();
    expect(onSubmit).toHaveBeenCalledTimes(1);
  });
});

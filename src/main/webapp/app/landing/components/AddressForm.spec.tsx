import React from 'react';
import { fireEvent, render, screen, within } from '@testing-library/react';
import { describe, expect, it, vi } from 'vitest';
import AddressForm from './AddressForm';
import { IDireccion } from 'app/shared/model/direccion.model';

const fillRequiredFields = (container: HTMLElement) => {
  fireEvent.change(container.querySelector('input[name="destinatario"]')!, { target: { value: 'Ana Gomez' } });
  fireEvent.change(container.querySelector('input[name="direccion"]')!, { target: { value: 'Calle 1 #2-3' } });
  fireEvent.change(container.querySelector('select[name="departamento"]')!, { target: { value: 'Cundinamarca' } });
  fireEvent.change(container.querySelector('select[name="municipio"]')!, { target: { value: 'Soacha' } });
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

  it('valida que el barrio solo acepte letras, números y signos básicos', async () => {
    const onSubmit = vi.fn();
    const { container } = render(<AddressForm onSubmit={onSubmit} onCancel={() => {}} />);

    fillRequiredFields(container);
    fireEvent.change(container.querySelector('input[name="barrio"]')!, { target: { value: 'Barrio@#$' } });
    fireEvent.submit(container.querySelector('form')!);

    expect(await screen.findByText('Solo letras, números y signos básicos.')).toBeTruthy();
    expect(onSubmit).not.toHaveBeenCalled();
  });

  it('valida el municipio manual cuando se elige "Otro (escribir manualmente)"', async () => {
    const onSubmit = vi.fn();
    const { container } = render(<AddressForm onSubmit={onSubmit} onCancel={() => {}} />);

    fillRequiredFields(container);
    fireEvent.change(container.querySelector('select[name="municipio"]')!, { target: { value: '__otro__' } });
    fireEvent.change(container.querySelector('input[name="municipio"]')!, { target: { value: 'Bogot@2026' } });
    fireEvent.submit(container.querySelector('form')!);

    expect(await screen.findByText('Solo letras, números y signos básicos.')).toBeTruthy();
    expect(onSubmit).not.toHaveBeenCalled();
  });

  it('envía el formulario con valores válidos y preserva localidad vacía', async () => {
    const onSubmit = vi.fn();
    const { container } = render(<AddressForm onSubmit={onSubmit} onCancel={() => {}} />);

    fillRequiredFields(container);
    fireEvent.submit(container.querySelector('form')!);

    expect(await screen.findByRole('button', { name: 'Guardar dirección' })).toBeTruthy();
    expect(onSubmit).toHaveBeenCalledTimes(1);
    const payload = onSubmit.mock.calls[0][0];
    expect(payload.departamento).toBe('Cundinamarca');
    expect(payload.municipio).toBe('Soacha');
    expect(payload.localidad).toBe('');
  });

  it('mantiene el municipio deshabilitado hasta elegir un departamento', () => {
    const onSubmit = vi.fn();
    const { container } = render(<AddressForm onSubmit={onSubmit} onCancel={() => {}} />);

    const municipioSelect = container.querySelector('select[name="municipio"]') as HTMLSelectElement;
    expect(municipioSelect.disabled).toBe(true);
    expect(within(municipioSelect).getAllByRole('option')).toHaveLength(1);
    expect(within(municipioSelect).getByRole('option', { name: 'Selecciona un departamento' })).toBeTruthy();
  });

  it('filtra los municipios al cambiar de departamento', () => {
    const onSubmit = vi.fn();
    const { container } = render(<AddressForm onSubmit={onSubmit} onCancel={() => {}} />);

    const departamentoSelect = container.querySelector('select[name="departamento"]')!;
    const municipioSelect = container.querySelector('select[name="municipio"]') as HTMLSelectElement;

    fireEvent.change(departamentoSelect, { target: { value: 'Antioquia' } });
    expect(municipioSelect.disabled).toBe(false);
    expect(within(municipioSelect).getByRole('option', { name: 'Selecciona un municipio' })).toBeTruthy();
    expect(within(municipioSelect).getByRole('option', { name: 'Medellín' })).toBeTruthy();
    expect(within(municipioSelect).queryByRole('option', { name: 'Soacha' })).toBeNull();
    expect(within(municipioSelect).getByRole('option', { name: 'Otro (escribir manualmente)' })).toBeTruthy();

    fireEvent.change(departamentoSelect, { target: { value: 'Cundinamarca' } });
    expect(within(municipioSelect).queryByRole('option', { name: 'Medellín' })).toBeNull();
    expect(within(municipioSelect).getByRole('option', { name: 'Soacha' })).toBeTruthy();
  });

  it('resetea el municipio a vacío al cambiar de departamento', () => {
    const onSubmit = vi.fn();
    const { container } = render(<AddressForm onSubmit={onSubmit} onCancel={() => {}} />);

    const departamentoSelect = container.querySelector('select[name="departamento"]')!;
    const municipioSelect = container.querySelector('select[name="municipio"]') as HTMLSelectElement;

    fireEvent.change(departamentoSelect, { target: { value: 'Antioquia' } });
    fireEvent.change(municipioSelect, { target: { value: 'Medellín' } });
    expect(municipioSelect.value).toBe('Medellín');

    fireEvent.change(departamentoSelect, { target: { value: 'Cundinamarca' } });
    expect(municipioSelect.value).toBe('');
  });

  it('revela los campos manuales con "Otro (escribir manualmente)" y permite enviar', async () => {
    const onSubmit = vi.fn();
    const { container } = render(<AddressForm onSubmit={onSubmit} onCancel={() => {}} />);

    fireEvent.change(container.querySelector('select[name="departamento"]')!, { target: { value: '__otro__' } });

    // La sentinela __otro__ nunca llega al formulario: aparecen inputs de texto.
    const departamentoInput = container.querySelector('input[name="departamento"]')!;
    const municipioInput = container.querySelector('input[name="municipio"]')!;
    expect(departamentoInput).toBeTruthy();
    expect(municipioInput).toBeTruthy();
    expect(container.querySelector('select[name="departamento"]')).toBeNull();

    fireEvent.submit(container.querySelector('form')!);
    expect(await screen.findByText('Escribe el departamento.')).toBeTruthy();
    expect(await screen.findByText('Escribe el municipio.')).toBeTruthy();
    expect(onSubmit).not.toHaveBeenCalled();

    fireEvent.change(departamentoInput, { target: { value: 'Cundinamarca' } });
    fireEvent.change(municipioInput, { target: { value: 'Bogotá' } });
    fireEvent.change(container.querySelector('input[name="destinatario"]')!, { target: { value: 'Ana Gomez' } });
    fireEvent.change(container.querySelector('input[name="direccion"]')!, { target: { value: 'Calle 1 #2-3' } });
    fireEvent.change(container.querySelector('input[name="telefonoContacto"]')!, { target: { value: '3001234567' } });
    fireEvent.change(container.querySelector('input[name="codigoPostal"]')!, { target: { value: '110111' } });
    fireEvent.submit(container.querySelector('form')!);

    expect(await screen.findByRole('button', { name: 'Guardar dirección' })).toBeTruthy();
    expect(onSubmit).toHaveBeenCalledTimes(1);
    const payload = onSubmit.mock.calls[0][0];
    expect(payload.departamento).toBe('Cundinamarca');
    expect(payload.municipio).toBe('Bogotá');
  });

  it('en edición muestra un valor fuera del dataset como opción "(valor guardado)" seleccionada', () => {
    const onSubmit = vi.fn();
    const initialData = {
      id: 'dir-1',
      departamento: 'Villamérica',
      municipio: 'Los Almendros',
      destinatario: 'Ana Gomez',
      direccion: 'Calle 1 #2-3',
      telefonoContacto: '3001234567',
      codigoPostal: '110111',
    } as IDireccion;
    const { container } = render(<AddressForm initialData={initialData} onSubmit={onSubmit} onCancel={() => {}} />);

    const departamentoSelect = container.querySelector('select[name="departamento"]') as HTMLSelectElement;
    expect(departamentoSelect.value).toBe('Villamérica');
    expect(within(departamentoSelect).getByRole('option', { name: 'Villamérica (valor guardado)' })).toBeTruthy();
    expect(within(departamentoSelect).getByRole('option', { selected: true, name: 'Villamérica (valor guardado)' })).toBeTruthy();

    const municipioSelect = container.querySelector('select[name="municipio"]') as HTMLSelectElement;
    expect(municipioSelect.value).toBe('Los Almendros');
    expect(within(municipioSelect).getByRole('option', { name: 'Los Almendros (valor guardado)' })).toBeTruthy();
    expect(within(municipioSelect).getByRole('option', { selected: true, name: 'Los Almendros (valor guardado)' })).toBeTruthy();
  });
});

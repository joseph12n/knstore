import React from 'react';
import { fireEvent, render, screen, within } from '@testing-library/react';
import { describe, expect, it, vi } from 'vitest';
import AddressForm from './AddressForm';
import { IDireccion } from 'app/shared/model/direccion.model';

/**
 * Helpers locales de los 12 casos: evitan repetir la misma secuencia de setup,
 * cambio y envío en cada test (duplicación que Sonar CPD marcaba en este archivo).
 * No cambian la semántica de las pruebas: cada `it` conserva sus aserciones.
 */

const cambiar = (container: HTMLElement, selector: string, value: string) =>
  fireEvent.change(container.querySelector(selector)!, { target: { value } });

const escribir = (container: HTMLElement, name: string, value: string) => cambiar(container, `input[name="${name}"]`, value);

const enviar = (container: HTMLElement) => fireEvent.submit(container.querySelector('form')!);

const seleccionar = (container: HTMLElement, name: string) => container.querySelector(`select[name="${name}"]`) as HTMLSelectElement;

const renderForm = (initialData?: IDireccion) => {
  const onSubmit = vi.fn();
  const { container } = render(<AddressForm initialData={initialData} onSubmit={onSubmit} onCancel={() => {}} />);

  /** Comprueba el envío exitoso y devuelve el payload que recibió `onSubmit`. */
  const expectEnvioExitoso = async () => {
    expect(await screen.findByRole('button', { name: 'Guardar dirección' })).toBeTruthy();
    expect(onSubmit).toHaveBeenCalledTimes(1);
    return onSubmit.mock.calls[0][0];
  };

  return { onSubmit, container, expectEnvioExitoso };
};

const fillDatosPersonales = (container: HTMLElement) => {
  escribir(container, 'destinatario', 'Ana Gomez');
  escribir(container, 'direccion', 'Calle 1 #2-3');
  escribir(container, 'telefonoContacto', '3001234567');
  escribir(container, 'codigoPostal', '110111');
};

const fillRequiredFields = (container: HTMLElement) => {
  fillDatosPersonales(container);
  cambiar(container, 'select[name="departamento"]', 'Cundinamarca');
  cambiar(container, 'select[name="municipio"]', 'Soacha');
};

/** Envía el formulario con un campo inválido y comprueba que el error impide el envío. */
const expectCampoInvalido = async (selector: string, valorInvalido: string, mensaje: string) => {
  const { onSubmit, container } = renderForm();
  fillRequiredFields(container);
  cambiar(container, selector, valorInvalido);
  enviar(container);
  expect(await screen.findByText(mensaje)).toBeTruthy();
  expect(onSubmit).not.toHaveBeenCalled();
};

describe('AddressForm', () => {
  it('valida que el teléfono de contacto solo acepte dígitos', async () => {
    await expectCampoInvalido('input[name="telefonoContacto"]', '12345ABC', 'Debe tener entre 7 y 15 dígitos.');
  });

  it('valida que el código postal solo acepte dígitos', async () => {
    await expectCampoInvalido('input[name="codigoPostal"]', 'ABC-123', 'Solo se permiten números.');
  });

  it('valida que el destinatario solo acepte letras', async () => {
    await expectCampoInvalido('input[name="destinatario"]', 'Ana123', 'Solo se permiten letras.');
  });

  it('valida que la dirección contenga al menos una letra', async () => {
    await expectCampoInvalido('input[name="direccion"]', '12345', 'La dirección debe contener al menos una letra.');
  });

  it('valida que el barrio solo acepte letras, números y signos básicos', async () => {
    await expectCampoInvalido('input[name="barrio"]', 'Barrio@#$', 'Solo letras, números y signos básicos.');
  });

  it('valida el municipio manual cuando se elige "Otro (escribir manualmente)"', async () => {
    const { onSubmit, container } = renderForm();
    fillRequiredFields(container);
    cambiar(container, 'select[name="municipio"]', '__otro__');
    escribir(container, 'municipio', 'Bogot@2026');
    enviar(container);

    expect(await screen.findByText('Solo letras, números y signos básicos.')).toBeTruthy();
    expect(onSubmit).not.toHaveBeenCalled();
  });

  it('envía el formulario con valores válidos y preserva localidad vacía', async () => {
    const { container, expectEnvioExitoso } = renderForm();
    fillRequiredFields(container);
    enviar(container);

    const payload = await expectEnvioExitoso();
    expect(payload.departamento).toBe('Cundinamarca');
    expect(payload.municipio).toBe('Soacha');
    expect(payload.localidad).toBe('');
  });

  it('mantiene el municipio deshabilitado hasta elegir un departamento', () => {
    const { container } = renderForm();
    const municipioSelect = seleccionar(container, 'municipio');

    expect(municipioSelect.disabled).toBe(true);
    expect(within(municipioSelect).getAllByRole('option')).toHaveLength(1);
    expect(within(municipioSelect).getByRole('option', { name: 'Selecciona un departamento' })).toBeTruthy();
  });

  it('filtra los municipios al cambiar de departamento', () => {
    const { container } = renderForm();
    const municipioSelect = seleccionar(container, 'municipio');

    cambiar(container, 'select[name="departamento"]', 'Antioquia');
    expect(municipioSelect.disabled).toBe(false);
    expect(within(municipioSelect).getByRole('option', { name: 'Selecciona un municipio' })).toBeTruthy();
    expect(within(municipioSelect).getByRole('option', { name: 'Medellín' })).toBeTruthy();
    expect(within(municipioSelect).queryByRole('option', { name: 'Soacha' })).toBeNull();
    expect(within(municipioSelect).getByRole('option', { name: 'Otro (escribir manualmente)' })).toBeTruthy();

    cambiar(container, 'select[name="departamento"]', 'Cundinamarca');
    expect(within(municipioSelect).queryByRole('option', { name: 'Medellín' })).toBeNull();
    expect(within(municipioSelect).getByRole('option', { name: 'Soacha' })).toBeTruthy();
  });

  it('resetea el municipio a vacío al cambiar de departamento', () => {
    const { container } = renderForm();
    const municipioSelect = seleccionar(container, 'municipio');

    cambiar(container, 'select[name="departamento"]', 'Antioquia');
    cambiar(container, 'select[name="municipio"]', 'Medellín');
    expect(municipioSelect.value).toBe('Medellín');

    cambiar(container, 'select[name="departamento"]', 'Cundinamarca');
    expect(municipioSelect.value).toBe('');
  });

  it('revela los campos manuales con "Otro (escribir manualmente)" y permite enviar', async () => {
    const { onSubmit, container, expectEnvioExitoso } = renderForm();

    cambiar(container, 'select[name="departamento"]', '__otro__');

    // La sentinela __otro__ nunca llega al formulario: aparecen inputs de texto.
    expect(container.querySelector('input[name="departamento"]')).toBeTruthy();
    expect(container.querySelector('input[name="municipio"]')).toBeTruthy();
    expect(container.querySelector('select[name="departamento"]')).toBeNull();

    enviar(container);
    expect(await screen.findByText('Escribe el departamento.')).toBeTruthy();
    expect(await screen.findByText('Escribe el municipio.')).toBeTruthy();
    expect(onSubmit).not.toHaveBeenCalled();

    escribir(container, 'departamento', 'Cundinamarca');
    escribir(container, 'municipio', 'Bogotá');
    fillDatosPersonales(container);
    enviar(container);

    const payload = await expectEnvioExitoso();
    expect(payload.departamento).toBe('Cundinamarca');
    expect(payload.municipio).toBe('Bogotá');
  });

  it('en edición muestra un valor fuera del dataset como opción "(valor guardado)" seleccionada', () => {
    const initialData = {
      id: 'dir-1',
      departamento: 'Villamérica',
      municipio: 'Los Almendros',
      destinatario: 'Ana Gomez',
      direccion: 'Calle 1 #2-3',
      telefonoContacto: '3001234567',
      codigoPostal: '110111',
    } as IDireccion;
    const { container } = renderForm(initialData);

    const departamentoSelect = seleccionar(container, 'departamento');
    expect(departamentoSelect.value).toBe('Villamérica');
    expect(within(departamentoSelect).getByRole('option', { name: 'Villamérica (valor guardado)' })).toBeTruthy();
    expect(within(departamentoSelect).getByRole('option', { selected: true, name: 'Villamérica (valor guardado)' })).toBeTruthy();

    const municipioSelect = seleccionar(container, 'municipio');
    expect(municipioSelect.value).toBe('Los Almendros');
    expect(within(municipioSelect).getByRole('option', { name: 'Los Almendros (valor guardado)' })).toBeTruthy();
    expect(within(municipioSelect).getByRole('option', { selected: true, name: 'Los Almendros (valor guardado)' })).toBeTruthy();
  });
});

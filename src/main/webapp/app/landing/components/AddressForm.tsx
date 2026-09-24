import React, { useState } from 'react';
import { Button, Col, Form, Row } from 'react-bootstrap';
import { useForm } from 'react-hook-form';

import { IDireccion } from 'app/shared/model/direccion.model';
import { DEPARTAMENTOS, municipiosDe, normalizar } from 'app/landing/model/divipola';

export interface AddressFormData {
  direccion: string;
  barrio: string;
  localidad: string;
  municipio: string;
  departamento: string;
  activo: boolean;
  telefonoContacto: string;
  destinatario: string;
  codigoPostal: string;
}

interface AddressFormProps {
  initialData?: IDireccion;
  onSubmit: (data: AddressFormData) => void;
  onCancel: () => void;
  isSubmitting?: boolean;
}

/** Sentinel de "Otro (escribir manualmente)"; nunca debe llegar a react-hook-form. */
const OTRO_MANUAL = '__otro__';
/** Cubre ñ, tildes, números y signos básicos ('Bogotá D.C.', 'Nariño', 'Km 5'). */
const SOLO_LETRAS_NUMEROS = /^[\p{L}\p{M}\d\s.'’,()-]+$/u;
const MSG_SIGNOS = 'Solo letras, números y signos básicos.';

const REGLAS_DEP_SELECT = { required: 'Selecciona un departamento.' };
const REGLAS_DEP_MANUAL = { required: 'Escribe el departamento.', pattern: { value: SOLO_LETRAS_NUMEROS, message: MSG_SIGNOS } };
const REGLAS_MUN_SELECT = { required: 'Selecciona un municipio.' };
const REGLAS_MUN_MANUAL = { required: 'Escribe el municipio.', pattern: { value: SOLO_LETRAS_NUMEROS, message: MSG_SIGNOS } };

const ordenar = (a: string, b: string) => a.localeCompare(b, 'es');
const DEPARTAMENTOS_ORDENADOS = [...DEPARTAMENTOS].sort((a, b) => ordenar(a.nombre, b.nombre));

const crearValoresIniciales = (initialData?: IDireccion): AddressFormData => ({
  direccion: initialData?.direccion || '',
  barrio: initialData?.barrio || '',
  localidad: initialData?.localidad || '',
  municipio: initialData?.municipio || '',
  departamento: initialData?.departamento || '',
  activo: initialData?.activo ?? true,
  telefonoContacto: initialData?.telefonoContacto || '',
  destinatario: initialData?.destinatario || '',
  codigoPostal: initialData?.codigoPostal || '',
});

export const AddressForm = ({ initialData, onSubmit, onCancel, isSubmitting = false }: AddressFormProps) => {
  // Modo manual ("Otro (escribir manualmente)"): la sentinela __otro__ nunca vive en el formulario.
  const [depManual, setDepManual] = useState(false);
  const [munManual, setMunManual] = useState(false);

  const {
    register,
    handleSubmit,
    setValue,
    watch,
    formState: { errors },
  } = useForm<AddressFormData>({
    defaultValues: crearValoresIniciales(initialData),
  });

  const departamentoValor = watch('departamento');
  const municipioValor = watch('municipio');

  // Coincidencia con el dataset usando normalizar() para tolerar tildes/mayúsculas.
  const departamentoActual = DEPARTAMENTOS_ORDENADOS.find(dep => normalizar(dep.nombre) === normalizar(departamentoValor));
  const municipiosDisponibles = departamentoActual
    ? [...municipiosDe(departamentoActual.codigo)].sort((a, b) => ordenar(a.nombre, b.nombre))
    : [];
  const municipioEnLista = municipiosDisponibles.some(municipio => normalizar(municipio.nombre) === normalizar(municipioValor));

  // Si el departamento es manual no hay lista de municipios: también se escribe a mano.
  const mostrarDepManual = depManual;
  const mostrarMunManual = depManual || munManual;

  const registroDepartamento = register('departamento', mostrarDepManual ? REGLAS_DEP_MANUAL : REGLAS_DEP_SELECT);
  const registroMunicipio = register('municipio', mostrarMunManual ? REGLAS_MUN_MANUAL : REGLAS_MUN_SELECT);

  const manejarCambioDepartamento = (evento: React.ChangeEvent<HTMLSelectElement>) => {
    const valor = evento.target.value;
    if (valor === OTRO_MANUAL) {
      // La sentinela nunca llega a RHF: se limpia el valor y se entra en modo manual.
      setValue('departamento', '', { shouldDirty: true, shouldValidate: !!errors.departamento });
      setDepManual(true);
    } else {
      setValue('departamento', valor, { shouldDirty: true, shouldValidate: !!errors.departamento });
    }
    // El municipio SIEMPRE se reinicia al cambiar de departamento (sin useEffect sobre watch,
    // que en edición borraría el municipio preseleccionado).
    setValue('municipio', '', { shouldDirty: true, shouldValidate: !!errors.municipio });
  };

  const manejarCambioMunicipio = (evento: React.ChangeEvent<HTMLSelectElement>) => {
    const valor = evento.target.value;
    if (valor === OTRO_MANUAL) {
      setValue('municipio', '', { shouldDirty: true });
      setMunManual(true);
    } else {
      setValue('municipio', valor, { shouldDirty: true, shouldValidate: !!errors.municipio });
    }
  };

  return (
    <Form onSubmit={handleSubmit(onSubmit)} noValidate>
      <Row>
        <Col md={12} className="mb-3">
          <Form.Group>
            <Form.Label>Destinatario *</Form.Label>
            <Form.Control
              type="text"
              placeholder="Nombre de quien recibe"
              isInvalid={!!errors.destinatario}
              {...register('destinatario', {
                required: 'El destinatario es obligatorio.',
                pattern: {
                  value: /^[A-Za-zÁÉÍÓÚÜÑáéíóúüñ' .-]+$/,
                  message: 'Solo se permiten letras.',
                },
              })}
            />
            <Form.Control.Feedback type="invalid">{errors.destinatario?.message}</Form.Control.Feedback>
          </Form.Group>
        </Col>
        <Col md={6} className="mb-3">
          <Form.Group controlId="departamento">
            <Form.Label>Departamento *</Form.Label>
            {mostrarDepManual ? (
              <Form.Control
                type="text"
                placeholder="Departamento"
                isInvalid={!!errors.departamento}
                aria-invalid={!!errors.departamento}
                aria-describedby="departamento-error"
                {...registroDepartamento}
              />
            ) : (
              <Form.Select
                {...registroDepartamento}
                value={departamentoValor}
                onChange={evento => {
                  registroDepartamento.onChange(evento);
                  manejarCambioDepartamento(evento);
                }}
                isInvalid={!!errors.departamento}
                aria-invalid={!!errors.departamento}
                aria-describedby="departamento-error"
              >
                <option value="">Selecciona un departamento</option>
                {DEPARTAMENTOS_ORDENADOS.map(departamento => (
                  <option key={departamento.codigo} value={departamento.nombre}>
                    {departamento.nombre}
                  </option>
                ))}
                {!departamentoActual && departamentoValor && (
                  <option value={departamentoValor}>{departamentoValor} (valor guardado)</option>
                )}
                <option value={OTRO_MANUAL}>Otro (escribir manualmente)</option>
              </Form.Select>
            )}
            <Form.Control.Feedback type="invalid" id="departamento-error">
              {errors.departamento?.message}
            </Form.Control.Feedback>
          </Form.Group>
        </Col>
        <Col md={6} className="mb-3">
          <Form.Group controlId="municipio">
            <Form.Label>Municipio *</Form.Label>
            {mostrarMunManual ? (
              <Form.Control
                type="text"
                placeholder="Municipio"
                isInvalid={!!errors.municipio}
                aria-invalid={!!errors.municipio}
                aria-describedby="municipio-error"
                {...registroMunicipio}
              />
            ) : (
              <Form.Select
                {...registroMunicipio}
                value={municipioValor}
                onChange={evento => {
                  registroMunicipio.onChange(evento);
                  manejarCambioMunicipio(evento);
                }}
                disabled={!departamentoValor}
                isInvalid={!!errors.municipio}
                aria-invalid={!!errors.municipio}
                aria-describedby="municipio-error"
              >
                <option value="">{departamentoValor ? 'Selecciona un municipio' : 'Selecciona un departamento'}</option>
                {departamentoValor && (
                  <>
                    {municipiosDisponibles.map(municipio => (
                      <option key={municipio.codigo} value={municipio.nombre}>
                        {municipio.nombre}
                      </option>
                    ))}
                    {!municipioEnLista && municipioValor && <option value={municipioValor}>{municipioValor} (valor guardado)</option>}
                    <option value={OTRO_MANUAL}>Otro (escribir manualmente)</option>
                  </>
                )}
              </Form.Select>
            )}
            <Form.Control.Feedback type="invalid" id="municipio-error">
              {errors.municipio?.message}
            </Form.Control.Feedback>
          </Form.Group>
        </Col>
        <Col md={12} className="mb-3">
          <Form.Group>
            <Form.Label>Dirección *</Form.Label>
            <Form.Control
              type="text"
              placeholder="Calle, número, apartamento, torre"
              isInvalid={!!errors.direccion}
              {...register('direccion', {
                required: 'La dirección es obligatoria.',
                pattern: {
                  value: /.*[A-Za-zÁÉÍÓÚÜÑáéíóúüñ].*/,
                  message: 'La dirección debe contener al menos una letra.',
                },
              })}
            />
            <Form.Control.Feedback type="invalid">{errors.direccion?.message}</Form.Control.Feedback>
          </Form.Group>
        </Col>
        <Col md={6} className="mb-3">
          <Form.Group>
            <Form.Label>Barrio</Form.Label>
            <Form.Control
              type="text"
              placeholder="Barrio"
              isInvalid={!!errors.barrio}
              {...register('barrio', {
                pattern: { value: SOLO_LETRAS_NUMEROS, message: MSG_SIGNOS },
              })}
            />
            <Form.Control.Feedback type="invalid">{errors.barrio?.message}</Form.Control.Feedback>
          </Form.Group>
        </Col>
        <Col md={6} className="mb-3">
          <Form.Group>
            <Form.Label>Teléfono de contacto *</Form.Label>
            <Form.Control
              type="tel"
              placeholder="Teléfono de contacto"
              isInvalid={!!errors.telefonoContacto}
              {...register('telefonoContacto', {
                required: 'El teléfono de contacto es obligatorio.',
                pattern: {
                  value: /^\d{7,15}$/,
                  message: 'Debe tener entre 7 y 15 dígitos.',
                },
              })}
            />
            <Form.Control.Feedback type="invalid">{errors.telefonoContacto?.message}</Form.Control.Feedback>
          </Form.Group>
        </Col>
        <Col md={6} className="mb-3">
          <Form.Group>
            <Form.Label>Código postal *</Form.Label>
            <Form.Control
              type="text"
              placeholder="Código postal"
              isInvalid={!!errors.codigoPostal}
              {...register('codigoPostal', {
                required: 'El código postal es obligatorio.',
                pattern: {
                  value: /^[0-9]+$/,
                  message: 'Solo se permiten números.',
                },
              })}
            />
            <Form.Control.Feedback type="invalid">{errors.codigoPostal?.message}</Form.Control.Feedback>
          </Form.Group>
        </Col>
      </Row>
      <div className="d-flex gap-2 justify-content-end">
        <Button variant="outline-secondary" onClick={onCancel} disabled={isSubmitting}>
          Cancelar
        </Button>
        <Button variant="primary" type="submit" disabled={isSubmitting}>
          {isSubmitting ? 'Guardando...' : initialData?.id ? 'Actualizar dirección' : 'Guardar dirección'}
        </Button>
      </div>
    </Form>
  );
};

export default AddressForm;

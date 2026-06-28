import React from 'react';
import { Button, Col, Form, Row } from 'react-bootstrap';
import { useForm } from 'react-hook-form';
import dayjs from 'dayjs';

import { ICuenta } from 'app/shared/model/cuenta.model';
import { ITipoDocumento } from 'app/shared/model/tipo-documento.model';
import { Genero } from 'app/shared/model/enumerations/genero.model';

export interface ProfileFormData {
  primerNombre: string;
  segundoNombre: string;
  primerApellido: string;
  segundoApellido: string;
  tipoDocumentoId: string;
  numDocumento: string;
  genero: keyof typeof Genero | '';
  fechaNacimiento: string;
  celular: string;
  telefono: string;
}

interface ProfileFormProps {
  initialData?: ICuenta;
  tiposDocumento: ITipoDocumento[];
  onSubmit: (data: ProfileFormData) => void;
  onCancel: () => void;
  isSubmitting?: boolean;
}

export const ProfileForm = ({ initialData, tiposDocumento, onSubmit, onCancel, isSubmitting = false }: ProfileFormProps) => {
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<ProfileFormData>({
    defaultValues: {
      primerNombre: initialData?.primerNombre || '',
      segundoNombre: initialData?.segundoNombre || '',
      primerApellido: initialData?.primerApellido || '',
      segundoApellido: initialData?.segundoApellido || '',
      tipoDocumentoId: initialData?.tipoDocumento?.id || '',
      numDocumento: initialData?.numDocumento || '',
      genero: initialData?.genero || '',
      fechaNacimiento: initialData?.fechaNacimiento ? dayjs(initialData.fechaNacimiento).format('YYYY-MM-DD') : '',
      celular: initialData?.celular || '',
      telefono: initialData?.telefono || '',
    },
  });

  return (
    <Form onSubmit={handleSubmit(onSubmit)} noValidate>
      <Row>
        <Col md={6} className="mb-3">
          <Form.Group>
            <Form.Label>Primer nombre *</Form.Label>
            <Form.Control
              type="text"
              isInvalid={!!errors.primerNombre}
              {...register('primerNombre', { required: 'El primer nombre es obligatorio.' })}
            />
            <Form.Control.Feedback type="invalid">{errors.primerNombre?.message}</Form.Control.Feedback>
          </Form.Group>
        </Col>
        <Col md={6} className="mb-3">
          <Form.Group>
            <Form.Label>Segundo nombre</Form.Label>
            <Form.Control type="text" {...register('segundoNombre')} />
          </Form.Group>
        </Col>
        <Col md={6} className="mb-3">
          <Form.Group>
            <Form.Label>Primer apellido *</Form.Label>
            <Form.Control
              type="text"
              isInvalid={!!errors.primerApellido}
              {...register('primerApellido', { required: 'El primer apellido es obligatorio.' })}
            />
            <Form.Control.Feedback type="invalid">{errors.primerApellido?.message}</Form.Control.Feedback>
          </Form.Group>
        </Col>
        <Col md={6} className="mb-3">
          <Form.Group>
            <Form.Label>Segundo apellido</Form.Label>
            <Form.Control type="text" {...register('segundoApellido')} />
          </Form.Group>
        </Col>
        <Col md={6} className="mb-3">
          <Form.Group>
            <Form.Label>Tipo de documento</Form.Label>
            <Form.Select {...register('tipoDocumentoId')}>
              <option value="">Selecciona...</option>
              {tiposDocumento.map(tipo => (
                <option key={tipo.id} value={tipo.id}>
                  {tipo.nombreTipo}
                </option>
              ))}
            </Form.Select>
          </Form.Group>
        </Col>
        <Col md={6} className="mb-3">
          <Form.Group>
            <Form.Label>Número de documento</Form.Label>
            <Form.Control type="text" {...register('numDocumento')} />
          </Form.Group>
        </Col>
        <Col md={6} className="mb-3">
          <Form.Group>
            <Form.Label>Género</Form.Label>
            <Form.Select {...register('genero')}>
              <option value="">Selecciona...</option>
              {Object.entries(Genero).map(([key, label]) => (
                <option key={key} value={key}>
                  {label}
                </option>
              ))}
            </Form.Select>
          </Form.Group>
        </Col>
        <Col md={6} className="mb-3">
          <Form.Group>
            <Form.Label>Fecha de nacimiento</Form.Label>
            <Form.Control type="date" {...register('fechaNacimiento')} />
          </Form.Group>
        </Col>
        <Col md={6} className="mb-3">
          <Form.Group>
            <Form.Label>Celular</Form.Label>
            <Form.Control type="tel" {...register('celular')} />
          </Form.Group>
        </Col>
        <Col md={6} className="mb-3">
          <Form.Group>
            <Form.Label>Teléfono</Form.Label>
            <Form.Control type="tel" {...register('telefono')} />
          </Form.Group>
        </Col>
      </Row>
      <div className="d-flex gap-2 justify-content-end">
        <Button variant="outline-secondary" onClick={onCancel} disabled={isSubmitting}>
          Cancelar
        </Button>
        <Button variant="primary" type="submit" disabled={isSubmitting}>
          {isSubmitting ? 'Guardando...' : 'Guardar cambios'}
        </Button>
      </div>
    </Form>
  );
};

export default ProfileForm;

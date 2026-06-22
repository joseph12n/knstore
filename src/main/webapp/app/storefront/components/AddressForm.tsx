import React from 'react';
import { Button, Col, Form, Row } from 'react-bootstrap';
import { useForm } from 'react-hook-form';

import { IDireccion } from 'app/shared/model/direccion.model';

interface AddressFormData {
  direccion: string;
  barrio: string;
  localidad: string;
  municipio: string;
  departamento: string;
  activo: boolean;
}

interface AddressFormProps {
  initialData?: IDireccion;
  onSubmit: (data: AddressFormData) => void;
  onCancel: () => void;
  isSubmitting?: boolean;
}

export const AddressForm = ({ initialData, onSubmit, onCancel, isSubmitting = false }: AddressFormProps) => {
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<AddressFormData>({
    defaultValues: {
      direccion: initialData?.direccion || '',
      barrio: initialData?.barrio || '',
      localidad: initialData?.localidad || '',
      municipio: initialData?.municipio || '',
      departamento: initialData?.departamento || '',
      activo: initialData?.activo ?? true,
    },
  });

  return (
    <Form onSubmit={handleSubmit(onSubmit)} noValidate>
      <Row>
        <Col md={12} className="mb-3">
          <Form.Group>
            <Form.Label>Dirección *</Form.Label>
            <Form.Control
              type="text"
              placeholder="Calle, número, apartamento, torre"
              isInvalid={!!errors.direccion}
              {...register('direccion', { required: 'La dirección es obligatoria.' })}
            />
            <Form.Control.Feedback type="invalid">{errors.direccion?.message}</Form.Control.Feedback>
          </Form.Group>
        </Col>
        <Col md={6} className="mb-3">
          <Form.Group>
            <Form.Label>Barrio</Form.Label>
            <Form.Control type="text" placeholder="Barrio" {...register('barrio')} />
          </Form.Group>
        </Col>
        <Col md={6} className="mb-3">
          <Form.Group>
            <Form.Label>Localidad</Form.Label>
            <Form.Control type="text" placeholder="Localidad" {...register('localidad')} />
          </Form.Group>
        </Col>
        <Col md={6} className="mb-3">
          <Form.Group>
            <Form.Label>Municipio *</Form.Label>
            <Form.Control
              type="text"
              placeholder="Municipio"
              isInvalid={!!errors.municipio}
              {...register('municipio', { required: 'El municipio es obligatorio.' })}
            />
            <Form.Control.Feedback type="invalid">{errors.municipio?.message}</Form.Control.Feedback>
          </Form.Group>
        </Col>
        <Col md={6} className="mb-3">
          <Form.Group>
            <Form.Label>Departamento *</Form.Label>
            <Form.Control
              type="text"
              placeholder="Departamento"
              isInvalid={!!errors.departamento}
              {...register('departamento', { required: 'El departamento es obligatorio.' })}
            />
            <Form.Control.Feedback type="invalid">{errors.departamento?.message}</Form.Control.Feedback>
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

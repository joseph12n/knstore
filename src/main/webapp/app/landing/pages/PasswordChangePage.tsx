import React, { useEffect, useState } from 'react';
import { Card, Form, Button, InputGroup } from 'react-bootstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faEye, faEyeSlash } from '@fortawesome/free-solid-svg-icons';
import { useForm } from 'react-hook-form';
import { toast } from 'react-toastify';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { savePassword, reset as resetPassword } from 'app/modules/account/password/password.reducer';

interface PasswordFormData {
  currentPassword: string;
  newPassword: string;
  confirmPassword: string;
}

export const PasswordChangePage = () => {
  const dispatch = useAppDispatch();
  const [showCurrent, setShowCurrent] = useState(false);
  const [showNew, setShowNew] = useState(false);

  const passwordState = useAppSelector(state => state.password);

  const {
    register,
    handleSubmit,
    watch,
    reset: resetForm,
    formState: { errors },
  } = useForm<PasswordFormData>();

  useEffect(() => {
    return () => {
      dispatch(resetPassword());
    };
  }, [dispatch]);

  useEffect(() => {
    if (passwordState.updateSuccess) {
      toast.success('Contraseña actualizada correctamente.');
      resetForm();
    } else if (passwordState.updateFailure) {
      toast.error(passwordState.errorMessage || 'No pudimos cambiar la contraseña.');
    }
  }, [passwordState, resetForm]);

  const onSubmit = (data: PasswordFormData) => {
    dispatch(
      savePassword({
        currentPassword: data.currentPassword,
        newPassword: data.newPassword,
      }),
    );
  };

  const newPassword = watch('newPassword');

  return (
    <div className="kn-fade-in">
      <h1 className="h2 fw-bold mb-4">Seguridad</h1>
      <Card>
        <Card.Body className="p-4">
          <h5 className="fw-bold mb-3">Cambiar contraseña</h5>
          <Form onSubmit={handleSubmit(onSubmit)} noValidate>
            <Form.Group className="mb-3">
              <Form.Label>Contraseña actual</Form.Label>
              <InputGroup>
                <Form.Control
                  type={showCurrent ? 'text' : 'password'}
                  isInvalid={!!errors.currentPassword}
                  {...register('currentPassword', { required: 'Ingresa tu contraseña actual.' })}
                />
                <Button variant="outline-secondary" onClick={() => setShowCurrent(!showCurrent)}>
                  <FontAwesomeIcon icon={showCurrent ? faEyeSlash : faEye} />
                </Button>
                <Form.Control.Feedback type="invalid">{errors.currentPassword?.message}</Form.Control.Feedback>
              </InputGroup>
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Label>Nueva contraseña</Form.Label>
              <InputGroup>
                <Form.Control
                  type={showNew ? 'text' : 'password'}
                  isInvalid={!!errors.newPassword}
                  {...register('newPassword', {
                    required: 'Ingresa una nueva contraseña.',
                    minLength: { value: 8, message: 'La contraseña debe tener al menos 8 caracteres.' },
                  })}
                />
                <Button variant="outline-secondary" onClick={() => setShowNew(!showNew)}>
                  <FontAwesomeIcon icon={showNew ? faEyeSlash : faEye} />
                </Button>
                <Form.Control.Feedback type="invalid">{errors.newPassword?.message}</Form.Control.Feedback>
              </InputGroup>
            </Form.Group>

            <Form.Group className="mb-4">
              <Form.Label>Confirmar nueva contraseña</Form.Label>
              <Form.Control
                type="password"
                isInvalid={!!errors.confirmPassword}
                {...register('confirmPassword', {
                  required: 'Confirma la nueva contraseña.',
                  validate: value => value === newPassword || 'Las contraseñas no coinciden.',
                })}
              />
              <Form.Control.Feedback type="invalid">{errors.confirmPassword?.message}</Form.Control.Feedback>
            </Form.Group>

            <div className="d-flex justify-content-end">
              <Button variant="primary" type="submit" disabled={passwordState.loading}>
                {passwordState.loading ? 'Actualizando...' : 'Cambiar contraseña'}
              </Button>
            </div>
          </Form>
        </Card.Body>
      </Card>
    </div>
  );
};

export default PasswordChangePage;

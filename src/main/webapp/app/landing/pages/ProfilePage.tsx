import React, { useEffect, useMemo, useRef, useState } from 'react';
import { Button, Card, Col, Form, Row } from 'react-bootstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faCamera, faEnvelope, faPhone, faUser } from '@fortawesome/free-solid-svg-icons';
import { useForm } from 'react-hook-form';
import dayjs from 'dayjs';
import { toast } from 'react-toastify';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getSession } from 'app/shared/reducers/authentication';
import { createEntity as createCuenta, getEntities as getCuentas, updateEntity as updateCuenta } from 'app/entities/cuenta/cuenta.reducer';
import { getEntities as getTiposDocumento } from 'app/entities/tipo-documento/tipo-documento.reducer';
import { saveAccountSettings, reset as resetSettings } from 'app/modules/account/settings/settings.reducer';
import { ICuenta } from 'app/shared/model/cuenta.model';
import { Genero } from 'app/shared/model/enumerations/genero.model';
import LoadingSpinner from 'app/landing/components/LoadingSpinner';

interface ProfileFormData {
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

const buildImageSrc = (contentType?: string | null, base64?: string | null) => {
  if (!base64) return undefined;
  return `data:${contentType || 'image/jpeg'};base64,${base64}`;
};

export const ProfilePage = () => {
  const dispatch = useAppDispatch();
  const fileInputRef = useRef<HTMLInputElement>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [previewImage, setPreviewImage] = useState<string | undefined>();
  const [imageFile, setImageFile] = useState<{ contentType: string; base64: string } | undefined>();

  const account = useAppSelector(state => state.authentication.account);
  const cuentas = useAppSelector(state => state.cuenta.entities) ?? [];
  const tiposDocumento = useAppSelector(state => state.tipoDocumento.entities) ?? [];
  const loading = useAppSelector(state => state.cuenta.loading || state.tipoDocumento.loading);

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<ProfileFormData>();

  useEffect(() => {
    dispatch(getSession());
    dispatch(getCuentas({ page: 0, size: 100, sort: 'primerNombre,asc' }));
    dispatch(getTiposDocumento({ page: 0, size: 100, sort: 'nombreTipo,asc' }));

    return () => {
      dispatch(resetSettings());
    };
  }, [dispatch]);

  const cuenta = useMemo(() => cuentas.find(c => c.user?.login === account.login), [cuentas, account.login]);

  useEffect(() => {
    if (cuenta) {
      reset({
        primerNombre: cuenta.primerNombre || '',
        segundoNombre: cuenta.segundoNombre || '',
        primerApellido: cuenta.primerApellido || '',
        segundoApellido: cuenta.segundoApellido || '',
        tipoDocumentoId: cuenta.tipoDocumento?.id || '',
        numDocumento: cuenta.numDocumento || '',
        genero: cuenta.genero || '',
        fechaNacimiento: cuenta.fechaNacimiento ? dayjs(cuenta.fechaNacimiento).format('YYYY-MM-DD') : '',
        celular: cuenta.celular || '',
        telefono: cuenta.telefono || '',
      });
      setPreviewImage(buildImageSrc(cuenta.fotoPerfilContentType, cuenta.fotoPerfil));
      setImageFile(undefined);
    }
  }, [cuenta, reset]);

  const handleImageChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0];
    if (!file) return;

    if (file.size > 2 * 1024 * 1024) {
      toast.error('La imagen no debe superar los 2 MB.');
      return;
    }

    const reader = new FileReader();
    reader.onloadend = () => {
      const result = reader.result as string;
      const base64 = result.split(',')[1];
      setImageFile({ contentType: file.type, base64 });
      setPreviewImage(result);
    };
    reader.readAsDataURL(file);
  };

  const onSubmit = async (data: ProfileFormData) => {
    setIsSubmitting(true);

    try {
      const tipoDocumento = data.tipoDocumentoId ? { id: data.tipoDocumentoId } : null;

      const payload: ICuenta = {
        ...cuenta,
        primerNombre: data.primerNombre,
        segundoNombre: data.segundoNombre || undefined,
        primerApellido: data.primerApellido,
        segundoApellido: data.segundoApellido || undefined,
        numDocumento: data.numDocumento || undefined,
        genero: data.genero || undefined,
        fechaNacimiento: data.fechaNacimiento ? dayjs(data.fechaNacimiento) : undefined,
        celular: data.celular || undefined,
        telefono: data.telefono || undefined,
        activo: true,
        tipoDocumento,
        user: { id: account.id, login: account.login },
      };

      if (imageFile) {
        payload.fotoPerfilContentType = imageFile.contentType;
        payload.fotoPerfil = imageFile.base64;
      }

      if (cuenta?.id) {
        await dispatch(updateCuenta({ ...payload, id: cuenta.id }));
      } else {
        await dispatch(createCuenta(payload));
      }

      dispatch(
        saveAccountSettings({
          ...account,
          firstName: data.primerNombre,
          lastName: data.primerApellido,
        }),
      );

      dispatch(getCuentas({ page: 0, size: 100, sort: 'primerNombre,asc' }));
      toast.success('Perfil actualizado correctamente.');
      setImageFile(undefined);
    } catch {
      toast.error('No pudimos actualizar tu perfil. Inténtalo de nuevo.');
    } finally {
      setIsSubmitting(false);
    }
  };

  if (loading && !cuenta) {
    return <LoadingSpinner fullScreen />;
  }

  return (
    <div className="kn-fade-in">
      <h1 className="h2 fw-bold mb-4">Mi perfil</h1>

      <Row className="g-4">
        <Col lg={4}>
          <Card className="text-center p-4">
            <Card.Body>
              <div
                className="rounded-circle mx-auto mb-3 d-flex align-items-center justify-content-center position-relative"
                style={{
                  width: '140px',
                  height: '140px',
                  backgroundColor: 'var(--kn-color-surface)',
                  color: 'var(--kn-color-text)',
                  backgroundImage: previewImage ? `url(${previewImage})` : undefined,
                  backgroundSize: 'cover',
                  backgroundPosition: 'center',
                  cursor: 'pointer',
                }}
                onClick={() => fileInputRef.current?.click()}
              >
                {!previewImage && <FontAwesomeIcon icon={faUser} size="4x" />}
                <div
                  className="position-absolute bottom-0 end-0 rounded-circle d-flex align-items-center justify-content-center"
                  style={{
                    width: '36px',
                    height: '36px',
                    backgroundColor: 'var(--kn-color-primary)',
                    color: 'var(--kn-color-text-inverse)',
                  }}
                >
                  <FontAwesomeIcon icon={faCamera} size="sm" />
                </div>
              </div>
              <input ref={fileInputRef} type="file" accept="image/*" className="d-none" onChange={handleImageChange} />
              <h5 className="fw-bold mb-1">
                {cuenta?.primerNombre || account.firstName} {cuenta?.primerApellido || account.lastName}
              </h5>
              <p className="text-muted small mb-0">{account.email}</p>
            </Card.Body>
          </Card>
        </Col>

        <Col lg={8}>
          <Card>
            <Card.Body className="p-4">
              <h5 className="fw-bold mb-4">Información personal</h5>
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
                      <Form.Label>
                        <FontAwesomeIcon icon={faPhone} className="me-1" /> Celular
                      </Form.Label>
                      <Form.Control type="tel" {...register('celular')} />
                    </Form.Group>
                  </Col>
                  <Col md={6} className="mb-3">
                    <Form.Group>
                      <Form.Label>
                        <FontAwesomeIcon icon={faPhone} className="me-1" /> Teléfono
                      </Form.Label>
                      <Form.Control type="tel" {...register('telefono')} />
                    </Form.Group>
                  </Col>
                  <Col md={6} className="mb-3">
                    <Form.Group>
                      <Form.Label>
                        <FontAwesomeIcon icon={faEnvelope} className="me-1" /> Correo electrónico
                      </Form.Label>
                      <Form.Control type="email" value={account.email || ''} disabled />
                      <Form.Text className="text-muted">El correo no se puede editar desde aquí.</Form.Text>
                    </Form.Group>
                  </Col>
                </Row>
                <div className="d-flex gap-2 justify-content-end mt-3">
                  <Button variant="primary" type="submit" disabled={isSubmitting || loading}>
                    {isSubmitting || loading ? 'Guardando...' : 'Guardar cambios'}
                  </Button>
                </div>
              </Form>
            </Card.Body>
          </Card>
        </Col>
      </Row>
    </div>
  );
};

export default ProfilePage;

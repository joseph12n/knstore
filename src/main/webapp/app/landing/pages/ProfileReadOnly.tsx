import React, { useEffect } from 'react';
import { Button, Card, Col, Row } from 'react-bootstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faEnvelope, faPhone, faUser } from '@fortawesome/free-solid-svg-icons';
import { useNavigate } from 'react-router';
import dayjs from 'dayjs';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getSession } from 'app/shared/reducers/authentication';
import { getCuentaByLogin, reset as resetCuenta } from 'app/entities/cuenta/cuenta.reducer';
import LoadingSpinner from 'app/landing/components/LoadingSpinner';

const buildImageSrc = (contentType?: string | null, base64?: string | null) => {
  if (!base64) return undefined;
  return `data:${contentType || 'image/jpeg'};base64,${base64}`;
};

export const ProfileReadOnly = () => {
  const navigate = useNavigate();
  const dispatch = useAppDispatch();

  const account = useAppSelector(state => state.authentication.account);
  const cuenta = useAppSelector(state => state.cuenta.entity);
  const loading = useAppSelector(state => state.cuenta.loading);

  useEffect(() => {
    dispatch(getSession());
    if (account.login) {
      dispatch(getCuentaByLogin(account.login));
    }
    return () => {
      dispatch(resetCuenta());
    };
  }, [dispatch, account.login]);

  if (loading) {
    return <LoadingSpinner fullScreen />;
  }

  const previewImage = buildImageSrc(cuenta?.fotoPerfilContentType, cuenta?.fotoPerfil);

  return (
    <div className="kn-fade-in">
      <div className="d-flex justify-content-between align-items-start mb-4">
        <h1 className="h2 fw-bold mb-0">Mi perfil</h1>
        <Button variant="primary" onClick={() => navigate('/mi-cuenta/perfil/editar')}>
          Editar perfil
        </Button>
      </div>

      <Row className="g-4">
        <Col lg={4}>
          <Card className="text-center p-4">
            <Card.Body>
              <div
                className="rounded-circle mx-auto mb-3 d-flex align-items-center justify-content-center"
                style={{
                  width: '140px',
                  height: '140px',
                  backgroundColor: 'var(--kn-color-surface)',
                  color: 'var(--kn-color-text)',
                  backgroundImage: previewImage ? `url(${previewImage})` : undefined,
                  backgroundSize: 'cover',
                  backgroundPosition: 'center',
                }}
              >
                {!previewImage && <FontAwesomeIcon icon={faUser} size="4x" />}
              </div>
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
              <Row>
                <Col md={6} className="mb-3">
                  <div className="text-muted small">Documento</div>
                  <div>
                    {cuenta?.tipoDocumento?.nombreTipo || ''} {cuenta?.numDocumento || 'No registrado'}
                  </div>
                </Col>
                <Col md={6} className="mb-3">
                  <div className="text-muted small">Género</div>
                  <div>{cuenta?.genero || 'No registrado'}</div>
                </Col>
                <Col md={6} className="mb-3">
                  <div className="text-muted small">Fecha de nacimiento</div>
                  <div>{cuenta?.fechaNacimiento ? dayjs(cuenta.fechaNacimiento).format('DD/MM/YYYY') : 'No registrada'}</div>
                </Col>
                <Col md={6} className="mb-3">
                  <div className="text-muted small">
                    <FontAwesomeIcon icon={faPhone} className="me-1" /> Celular
                  </div>
                  <div>{cuenta?.celular || 'No registrado'}</div>
                </Col>
                <Col md={6} className="mb-3">
                  <div className="text-muted small">
                    <FontAwesomeIcon icon={faPhone} className="me-1" /> Teléfono
                  </div>
                  <div>{cuenta?.telefono || 'No registrado'}</div>
                </Col>
                <Col md={6} className="mb-3">
                  <div className="text-muted small">
                    <FontAwesomeIcon icon={faEnvelope} className="me-1" /> Correo electrónico
                  </div>
                  <div>{account.email || 'No registrado'}</div>
                </Col>
              </Row>
              <div className="d-flex gap-2 justify-content-end mt-3">
                <Button variant="primary" onClick={() => navigate('/mi-cuenta/perfil/editar')}>
                  Editar perfil
                </Button>
              </div>
            </Card.Body>
          </Card>
        </Col>
      </Row>
    </div>
  );
};

export default ProfileReadOnly;

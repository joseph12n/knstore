import React from 'react';
import { Button, Card, Col, Row } from 'react-bootstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faEnvelope, faPhone } from '@fortawesome/free-solid-svg-icons';
import { useNavigate } from 'react-router';
import dayjs from 'dayjs';

import useCuentaActual from 'app/landing/hooks/useCuentaActual';
import LoadingSpinner from 'app/landing/components/LoadingSpinner';
import ProfileHeaderCard from 'app/landing/components/ProfileHeaderCard';

const buildImageSrc = (contentType?: string | null, base64?: string | null) => {
  if (!base64) return undefined;
  return `data:${contentType || 'image/jpeg'};base64,${base64}`;
};

export const ProfileReadOnly = () => {
  const navigate = useNavigate();
  const { account, cuenta, loading } = useCuentaActual();

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
          <ProfileHeaderCard cuenta={cuenta} account={account} avatarSize={140} fotoPerfil={previewImage} />
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

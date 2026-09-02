import React from 'react';
import { Button, Card, Col, Row } from 'react-bootstrap';
import { Link } from 'react-router';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faKey, faMapMarkerAlt, faShoppingBag } from '@fortawesome/free-solid-svg-icons';

import useCuentaActual from 'app/landing/hooks/useCuentaActual';
import LoadingSpinner from 'app/landing/components/LoadingSpinner';
import ProfileHeaderCard from 'app/landing/components/ProfileHeaderCard';

export const AccountPage = () => {
  const { account, cuenta, loading } = useCuentaActual();

  if (loading) {
    return <LoadingSpinner fullScreen />;
  }

  return (
    <div className="kn-fade-in">
      <h1 className="h2 fw-bold mb-4">Mi cuenta</h1>
      <Row className="g-4">
        <Col md={4}>
          <ProfileHeaderCard cuenta={cuenta} account={account}>
            <p className="text-muted small mb-0 mt-1">{account.login}</p>
            <Link to="/mi-cuenta/perfil/editar" className="btn btn-outline-primary btn-sm mt-2">
              Editar perfil
            </Link>
          </ProfileHeaderCard>
        </Col>
        <Col md={8}>
          <Row className="g-4">
            <Col md={6}>
              <Card className="h-100">
                <Card.Body>
                  <FontAwesomeIcon icon={faShoppingBag} size="2x" className="mb-3 text-muted" />
                  <h5 className="fw-bold">Mis pedidos</h5>
                  <p className="text-muted small">Consulta el estado y el historial de tus compras.</p>
                  <Link to="/mi-cuenta/pedidos" className="btn btn-primary btn-sm">
                    Ver pedidos
                  </Link>
                </Card.Body>
              </Card>
            </Col>
            <Col md={6}>
              <Card className="h-100">
                <Card.Body>
                  <FontAwesomeIcon icon={faMapMarkerAlt} size="2x" className="mb-3 text-muted" />
                  <h5 className="fw-bold">Direcciones</h5>
                  <p className="text-muted small">Administra tus direcciones de envío.</p>
                  <Link to="/mi-cuenta/direcciones" className="btn btn-primary btn-sm">
                    Ver direcciones
                  </Link>
                </Card.Body>
              </Card>
            </Col>
          </Row>

          <Card className="mt-4">
            <Card.Body>
              <h5 className="fw-bold mb-3">Información de contacto</h5>
              <Row>
                <Col sm={6} className="mb-2">
                  <span className="text-muted small">Celular</span>
                  <div>{cuenta?.celular || 'No registrado'}</div>
                </Col>
                <Col sm={6} className="mb-2">
                  <span className="text-muted small">Teléfono</span>
                  <div>{cuenta?.telefono || 'No registrado'}</div>
                </Col>
                <Col sm={6} className="mb-2">
                  <span className="text-muted small">Género</span>
                  <div>{cuenta?.genero || 'No registrado'}</div>
                </Col>
                <Col sm={6} className="mb-2">
                  <span className="text-muted small">Documento</span>
                  <div>
                    {cuenta?.tipoDocumento?.nombre} {cuenta?.numDocumento || 'No registrado'}
                  </div>
                </Col>
              </Row>
              <Button variant="outline-primary" size="sm" className="mt-3" as={Link as any} to="/mi-cuenta/perfil/editar">
                Editar información
              </Button>
            </Card.Body>
          </Card>

          <Card className="mt-4">
            <Card.Body>
              <h5 className="fw-bold mb-3">
                <FontAwesomeIcon icon={faKey} className="me-2 text-muted" />
                Seguridad
              </h5>
              <p className="text-muted small mb-3">Actualiza tu contraseña para mantener tu cuenta protegida.</p>
              <Link to="/mi-cuenta/seguridad" className="btn btn-outline-primary btn-sm">
                Cambiar contraseña
              </Link>
            </Card.Body>
          </Card>
        </Col>
      </Row>
    </div>
  );
};

export default AccountPage;

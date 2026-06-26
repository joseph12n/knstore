import React, { useEffect, useMemo } from 'react';
import { Button, Card, Col, Row } from 'react-bootstrap';
import { Link } from 'react-router';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faKey, faMapMarkerAlt, faShoppingBag, faUser } from '@fortawesome/free-solid-svg-icons';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getSession } from 'app/shared/reducers/authentication';
import { getEntities as getCuentas } from 'app/entities/cuenta/cuenta.reducer';
import LoadingSpinner from 'app/landing/components/LoadingSpinner';

export const AccountPage = () => {
  const dispatch = useAppDispatch();
  const account = useAppSelector(state => state.authentication.account);
  const cuentas = useAppSelector(state => state.cuenta.entities) ?? [];
  const loading = useAppSelector(state => state.cuenta.loading);

  useEffect(() => {
    dispatch(getSession());
    dispatch(getCuentas({ page: 0, size: 100, sort: 'primerNombre,asc' }));
  }, [dispatch]);

  const cuenta = useMemo(() => cuentas.find(c => c.user?.login === account.login), [cuentas, account.login]);

  if (loading) {
    return <LoadingSpinner fullScreen />;
  }

  return (
    <div className="kn-fade-in">
      <h1 className="h2 fw-bold mb-4">Mi cuenta</h1>
      <Row className="g-4">
        <Col md={4}>
          <Card className="h-100 text-center p-3">
            <Card.Body>
              <div
                className="rounded-circle mx-auto mb-3 d-flex align-items-center justify-content-center"
                style={{
                  width: '80px',
                  height: '80px',
                  backgroundColor: 'var(--kn-color-surface)',
                  color: 'var(--kn-color-text)',
                }}
              >
                <FontAwesomeIcon icon={faUser} size="2x" />
              </div>
              <h5 className="fw-bold">
                {cuenta?.primerNombre || account.firstName} {cuenta?.primerApellido || account.lastName}
              </h5>
              <p className="text-muted mb-1">{account.email}</p>
              <p className="text-muted small">{account.login}</p>
              <Link to="/cuenta/perfil/editar" className="btn btn-outline-primary btn-sm mt-2">
                Editar perfil
              </Link>
            </Card.Body>
          </Card>
        </Col>
        <Col md={8}>
          <Row className="g-4">
            <Col md={6}>
              <Card className="h-100">
                <Card.Body>
                  <FontAwesomeIcon icon={faShoppingBag} size="2x" className="mb-3 text-muted" />
                  <h5 className="fw-bold">Mis pedidos</h5>
                  <p className="text-muted small">Consulta el estado y el historial de tus compras.</p>
                  <Link to="/cuenta/pedidos" className="btn btn-primary btn-sm">
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
                  <Link to="/cuenta/direcciones" className="btn btn-primary btn-sm">
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
              <Button variant="outline-primary" size="sm" className="mt-3" as={Link as any} to="/cuenta/perfil/editar">
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
              <Link to="/cuenta/seguridad" className="btn btn-outline-primary btn-sm">
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

import React, { useEffect, useState } from 'react';
import { Badge, Button, Card, Col, Row } from 'react-bootstrap';
import { Link } from 'react-router';
import dayjs from 'dayjs';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities as getEnvios } from 'app/entities/envio/envio.reducer';
import useCuentaActual from 'app/landing/hooks/useCuentaActual';
import LoadingSpinner from 'app/landing/components/LoadingSpinner';
import EmptyState from 'app/landing/components/EmptyState';
import Pagination from 'app/landing/components/Pagination';
import { SHIPPING_STATUS_LABELS } from 'app/landing/utils/constants';

const ITEMS_PER_PAGE = 10;

export const ShipmentsPage = () => {
  const dispatch = useAppDispatch();
  const { account } = useCuentaActual();
  const envios = useAppSelector(state => state.envio.entities) ?? [];
  const totalItems = useAppSelector(state => state.envio.totalItems ?? 0);
  const loading = useAppSelector(state => state.envio.loading);

  const [activePage, setActivePage] = useState(1);

  useEffect(() => {
    // El backend pagina y filtra por la cuenta del cliente autenticado.
    dispatch(getEnvios({ page: activePage - 1, size: ITEMS_PER_PAGE, sort: 'id,desc' }));
  }, [dispatch, activePage, account.login]);

  if (loading) {
    return <LoadingSpinner fullScreen />;
  }

  if (envios.length === 0) {
    return (
      <div className="kn-fade-in">
        <h1 className="h2 fw-bold mb-4">Mis envíos</h1>
        <EmptyState
          title="Aún no tienes envíos registrados"
          description="Cuando tu pedido sea despachado, podrás rastrearlo aquí."
          action={
            <Link to="/mi-cuenta/pedidos" className="btn btn-primary">
              Ver mis pedidos
            </Link>
          }
        />
      </div>
    );
  }

  return (
    <div className="kn-fade-in">
      <h1 className="h2 fw-bold mb-4">Mis envíos</h1>
      <Row className="g-4">
        {envios.map(envio => (
          <Col md={6} key={envio.id}>
            <Card className="h-100">
              <Card.Body>
                <div className="d-flex justify-content-between align-items-start mb-3">
                  <div>
                    <div className="text-muted small">Pedido</div>
                    <div className="fw-bold">#{envio.pedido?.numeroPedido || envio.pedido?.id}</div>
                  </div>
                  <Badge bg={envio.estado === 'DELIVERED' ? 'success' : envio.estado === 'IN_TRANSIT' ? 'info' : 'warning'}>
                    {SHIPPING_STATUS_LABELS[envio.estado || 'Pendiente'] || envio.estado}
                  </Badge>
                </div>

                <Row className="g-2 mb-3">
                  <Col xs={6}>
                    <div className="text-muted small">Transportadora</div>
                    <div className="fw-semibold">{envio.transportadora || 'Por asignar'}</div>
                  </Col>
                  <Col xs={6}>
                    <div className="text-muted small">Número de rastreo</div>
                    <div className="fw-semibold">{envio.numeroRastreo || 'Pendiente'}</div>
                  </Col>
                  <Col xs={6}>
                    <div className="text-muted small">Tipo de servicio</div>
                    <div>{envio.tipoServicio || 'Estándar'}</div>
                  </Col>
                  <Col xs={6}>
                    <div className="text-muted small">Entrega estimada</div>
                    <div>{envio.fechaEntregaEstimada ? dayjs(envio.fechaEntregaEstimada).format('DD/MM/YYYY') : 'Por definir'}</div>
                  </Col>
                </Row>

                <div className="d-flex gap-2">
                  <Link to={`/mi-cuenta/pedidos/${envio.pedido?.id}`} className="btn btn-outline-primary btn-sm flex-grow-1">
                    Ver pedido
                  </Link>
                  {envio.urlRastreo && (
                    <Button variant="primary" size="sm" href={envio.urlRastreo} target="_blank" rel="noopener noreferrer">
                      Rastrear
                    </Button>
                  )}
                </div>
              </Card.Body>
            </Card>
          </Col>
        ))}
      </Row>
      <Pagination activePage={activePage} itemsPerPage={ITEMS_PER_PAGE} totalItems={totalItems} onPageChange={setActivePage} />
    </div>
  );
};

export default ShipmentsPage;

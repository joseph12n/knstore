import React, { useEffect, useMemo, useState } from 'react';
import { Badge, Button, Card, Col, Row } from 'react-bootstrap';
import { Link } from 'react-router';
import dayjs from 'dayjs';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getSession } from 'app/shared/reducers/authentication';
import { getEntities as getEnvios } from 'app/entities/envio/envio.reducer';
import { getEntities as getPedidos } from 'app/entities/pedido/pedido.reducer';
import { getCuentaByLogin, reset as resetCuenta } from 'app/entities/cuenta/cuenta.reducer';
import LoadingSpinner from 'app/landing/components/LoadingSpinner';
import EmptyState from 'app/landing/components/EmptyState';
import Pagination from 'app/landing/components/Pagination';
import { SHIPPING_STATUS_LABELS } from 'app/landing/utils/constants';

const ITEMS_PER_PAGE = 10;

export const ShipmentsPage = () => {
  const dispatch = useAppDispatch();
  const account = useAppSelector(state => state.authentication.account);
  const envios = useAppSelector(state => state.envio.entities) ?? [];
  const totalItems = useAppSelector(state => state.envio.totalItems ?? 0);
  const pedidos = useAppSelector(state => state.pedido.entities) ?? [];
  const cuenta = useAppSelector(state => state.cuenta.entity);
  const loading = useAppSelector(state => state.envio.loading || state.pedido.loading || state.cuenta.loading);

  const [activePage, setActivePage] = useState(1);

  useEffect(() => {
    dispatch(getSession());
    if (account.login) {
      dispatch(getCuentaByLogin(account.login));
    }
    return () => {
      dispatch(resetCuenta());
    };
  }, [dispatch, account.login]);

  useEffect(() => {
    // TODO backend: agregar filtro por cuentaId para paginar envíos del usuario directamente.
    dispatch(getPedidos({ page: 0, size: 1000, sort: 'numeroPedido,desc' }));
  }, [dispatch]);

  useEffect(() => {
    dispatch(getEnvios({ page: activePage - 1, size: ITEMS_PER_PAGE, sort: 'id,desc' }));
  }, [dispatch, activePage]);

  const pedidosUsuarioIds = useMemo(() => new Set(pedidos.filter(p => p.cuenta?.id === cuenta?.id).map(p => p.id)), [pedidos, cuenta]);

  const enviosUsuario = useMemo(
    () => envios.filter(e => e.pedido?.id && pedidosUsuarioIds.has(e.pedido.id)).sort((a, b) => (b.id || '').localeCompare(a.id || '')),
    [envios, pedidosUsuarioIds],
  );

  if (loading) {
    return <LoadingSpinner fullScreen />;
  }

  if (enviosUsuario.length === 0) {
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
        {enviosUsuario.map(envio => (
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

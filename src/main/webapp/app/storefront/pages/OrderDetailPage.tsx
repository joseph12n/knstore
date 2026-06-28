import React, { useEffect, useMemo } from 'react';
import { useParams } from 'react-router';
import { Badge, Button, Card, Col, Container, Row, Table } from 'react-bootstrap';
import { Link } from 'react-router';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntity as getPedido } from 'app/entities/pedido/pedido.reducer';
import { getEntities as getItemsPedido } from 'app/entities/item-pedido/item-pedido.reducer';
import { getEntities as getPagos } from 'app/entities/pago/pago.reducer';
import { getEntities as getEnvios } from 'app/entities/envio/envio.reducer';
import { getEntities as getFacturas } from 'app/entities/factura/factura.reducer';
import LoadingSpinner from 'app/storefront/components/LoadingSpinner';
import EmptyState from 'app/storefront/components/EmptyState';
import ErrorAlert from 'app/storefront/components/ErrorAlert';
import { ORDER_STATUS_COLORS, ORDER_STATUS_LABELS, PAYMENT_STATUS_LABELS, SHIPPING_STATUS_LABELS } from 'app/storefront/utils/constants';
import { formatCOP } from 'app/storefront/utils/format';

export const OrderDetailPage = () => {
  const { id } = useParams<{ id: string }>();
  const dispatch = useAppDispatch();

  const pedido = useAppSelector(state => state.pedido.entity);
  const loading = useAppSelector(state => state.pedido.loading);
  const errorMessage = useAppSelector(state => state.pedido.errorMessage);
  const itemsPedido = useAppSelector(state => state.itemPedido.entities) ?? [];
  const pagos = useAppSelector(state => state.pago.entities) ?? [];
  const envios = useAppSelector(state => state.envio.entities) ?? [];
  const facturas = useAppSelector(state => state.factura.entities) ?? [];

  useEffect(() => {
    if (id) {
      dispatch(getPedido(id));
      dispatch(getItemsPedido({ page: 0, size: 100, sort: 'id,asc' }));
      dispatch(getPagos({ page: 0, size: 100, sort: 'id,desc' }));
      dispatch(getEnvios({ page: 0, size: 100, sort: 'id,desc' }));
      dispatch(getFacturas({ page: 0, size: 100, sort: 'id,desc' }));
    }
  }, [dispatch, id]);

  const items = useMemo(() => itemsPedido.filter(i => i.pedido?.id === id), [itemsPedido, id]);
  const pago = useMemo(() => pagos.find(p => p.pedido?.id === id), [pagos, id]);
  const envio = useMemo(() => envios.find(e => e.pedido?.id === id), [envios, id]);
  const factura = useMemo(() => facturas.find(f => f.pago?.id === pago?.id), [facturas, pago]);

  if (loading) {
    return <LoadingSpinner fullScreen />;
  }

  if (errorMessage) {
    return (
      <Container className="py-5">
        <ErrorAlert message="No pudimos cargar el pedido. Inténtalo de nuevo." />
      </Container>
    );
  }

  if (!pedido.id) {
    return (
      <Container className="py-5">
        <EmptyState
          title="Pedido no encontrado"
          description="El pedido que buscas no existe o no tienes acceso."
          action={
            <Link to="/cuenta/pedidos" className="btn btn-primary">
              Volver a mis pedidos
            </Link>
          }
        />
      </Container>
    );
  }

  const estado = pedido.estado || 'Pendiente';

  return (
    <Container className="py-4 kn-fade-in">
      <Link to="/cuenta/pedidos" className="text-muted small d-block mb-3">
        ← Volver a mis pedidos
      </Link>
      <div className="d-flex flex-wrap justify-content-between align-items-start gap-3 mb-4">
        <div>
          <h1 className="h2 fw-bold mb-1">Pedido #{pedido.numeroPedido || pedido.id}</h1>
          <Badge bg={ORDER_STATUS_COLORS[estado] || 'secondary'}>{ORDER_STATUS_LABELS[estado] || estado}</Badge>
        </div>
        {factura?.codigoQr && (
          <Button variant="outline-primary" size="sm" href={`api/facturas/${factura.id}/download`}>
            Descargar factura
          </Button>
        )}
      </div>

      <Row className="g-4">
        <Col lg={8}>
          <Card className="mb-4">
            <Card.Body>
              <h5 className="fw-bold mb-3">Productos</h5>
              <Table responsive className="align-middle">
                <thead>
                  <tr>
                    <th>Producto</th>
                    <th className="text-center">Cantidad</th>
                    <th className="text-end">Precio</th>
                    <th className="text-end">Subtotal</th>
                  </tr>
                </thead>
                <tbody>
                  {items.length === 0 ? (
                    <tr>
                      <td colSpan={4} className="text-center text-muted py-4">
                        No se encontraron items del pedido.
                      </td>
                    </tr>
                  ) : (
                    items.map(item => (
                      <tr key={item.id}>
                        <td>
                          <div className="fw-semibold">{item.nombreProducto}</div>
                          <small className="text-muted">
                            {item.marcaProducto}
                            {item.colorProducto && ` · ${item.colorProducto}`}
                            {item.tallaProducto && ` · Talla ${item.tallaProducto}`}
                          </small>
                        </td>
                        <td className="text-center">{item.cantidad}</td>
                        <td className="text-end">{formatCOP(item.precioUnitario)}</td>
                        <td className="text-end fw-bold">{formatCOP((item.precioUnitario || 0) * (item.cantidad || 0))}</td>
                      </tr>
                    ))
                  )}
                </tbody>
              </Table>
            </Card.Body>
          </Card>

          {envio && (
            <Card className="mb-4">
              <Card.Body>
                <h5 className="fw-bold mb-3">Envío</h5>
                <Row>
                  <Col md={6} className="mb-2">
                    <span className="text-muted small">Transportadora</span>
                    <div className="fw-semibold">{envio.transportadora || 'Por asignar'}</div>
                  </Col>
                  <Col md={6} className="mb-2">
                    <span className="text-muted small">Número de rastreo</span>
                    <div className="fw-semibold">{envio.numeroRastreo || 'Pendiente'}</div>
                  </Col>
                  <Col md={6} className="mb-2">
                    <span className="text-muted small">Estado</span>
                    <div>{SHIPPING_STATUS_LABELS[envio.estado || 'Pendiente'] || envio.estado}</div>
                  </Col>
                  <Col md={6} className="mb-2">
                    <span className="text-muted small">Tipo de servicio</span>
                    <div>{envio.tipoServicio || 'Estándar'}</div>
                  </Col>
                </Row>
                {envio.urlRastreo && (
                  <a href={envio.urlRastreo} target="_blank" rel="noopener noreferrer" className="btn btn-outline-primary btn-sm mt-3">
                    Rastrear envío
                  </a>
                )}
              </Card.Body>
            </Card>
          )}
        </Col>

        <Col lg={4}>
          <Card className="mb-4">
            <Card.Body>
              <h5 className="fw-bold mb-3">Resumen</h5>
              <div className="d-flex justify-content-between mb-2">
                <span>Subtotal</span>
                <span>{formatCOP(pedido.subtotal)}</span>
              </div>
              {pedido.descuento ? (
                <div className="d-flex justify-content-between mb-2 text-success">
                  <span>Descuento</span>
                  <span>-{formatCOP(pedido.descuento)}</span>
                </div>
              ) : null}
              <div className="d-flex justify-content-between mb-2">
                <span>Envío</span>
                <span>{pedido.costoEnvio ? formatCOP(pedido.costoEnvio) : 'Gratis'}</span>
              </div>
              {pedido.ivaTotal ? (
                <div className="d-flex justify-content-between mb-2">
                  <span>IVA</span>
                  <span>{formatCOP(pedido.ivaTotal)}</span>
                </div>
              ) : null}
              <hr />
              <div className="d-flex justify-content-between">
                <span className="fw-bold">Total</span>
                <span className="h4 fw-bold">{formatCOP(pedido.total)}</span>
              </div>
            </Card.Body>
          </Card>

          <Card className="mb-4">
            <Card.Body>
              <h5 className="fw-bold mb-3">Pago</h5>
              {pago ? (
                <>
                  <div className="mb-2">
                    <span className="text-muted small">Método</span>
                    <div className="fw-semibold">{pago.metodoPago}</div>
                  </div>
                  <div className="mb-2">
                    <span className="text-muted small">Estado</span>
                    <div>{PAYMENT_STATUS_LABELS[pago.estado || 'Pendiente'] || pago.estado}</div>
                  </div>
                  <div>
                    <span className="text-muted small">Monto</span>
                    <div className="fw-semibold">{formatCOP(pago.monto)}</div>
                  </div>
                </>
              ) : (
                <p className="text-muted mb-0">Información de pago no disponible.</p>
              )}
            </Card.Body>
          </Card>

          <Card>
            <Card.Body>
              <h5 className="fw-bold mb-3">Dirección de envío</h5>
              <p className="mb-1 fw-semibold">{pedido.direccion?.direccion}</p>
              <p className="text-muted small mb-0">
                {pedido.direccion?.barrio && `${pedido.direccion.barrio}, `}
                {pedido.direccion?.localidad && `${pedido.direccion.localidad}, `}
                {pedido.direccion?.municipio}, {pedido.direccion?.departamento}
              </p>
            </Card.Body>
          </Card>
        </Col>
      </Row>
    </Container>
  );
};

export default OrderDetailPage;

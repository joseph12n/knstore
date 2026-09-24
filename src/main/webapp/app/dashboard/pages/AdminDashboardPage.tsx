import React, { useCallback, useEffect, useState } from 'react';
import { Badge, Card, Col, Container, Row, Spinner, Table } from 'react-bootstrap';
import { Link } from 'react-router';
import axios from 'axios';

import { formatCOP } from 'app/landing/utils/format';
import { IEnvio } from 'app/shared/model/envio.model';
import { IPago } from 'app/shared/model/pago.model';
import { IPedido } from 'app/shared/model/pedido.model';
import { IProductoInventario } from 'app/shared/model/producto-inventario.model';

const ESTADO_PEDIDO_BADGE: Record<string, string> = {
  PENDING: 'warning',
  CONFIRMED: 'info',
  PROCESSING: 'info',
  SHIPPED: 'primary',
  DELIVERED: 'success',
  CANCELLED: 'danger',
  RETURNED: 'danger',
};

const ACCESOS_RAPIDOS = [
  { to: '/admin/operacion/pedidos', label: 'Pedidos' },
  { to: '/admin/operacion/envios', label: 'Envíos' },
  { to: '/admin/operacion/reembolsos', label: 'Reembolsos' },
  { to: '/producto', label: 'Catálogo' },
];

const getNombreCliente = (pedido: IPedido): string => {
  const cuenta = pedido.cuenta;
  if (!cuenta) {
    return '-';
  }
  const nombres = [cuenta.primerNombre, cuenta.segundoNombre, cuenta.primerApellido, cuenta.segundoApellido].filter(Boolean).join(' ');
  return nombres || cuenta.user?.login || '-';
};

const formatFecha = (fecha?: string): string => (fecha ? new Date(fecha).toLocaleDateString('es-CO') : '-');

export const AdminDashboardPage = () => {
  const [pedidos, setPedidos] = useState<IPedido[]>([]);
  const [inventarios, setInventarios] = useState<IProductoInventario[]>([]);
  const [envios, setEnvios] = useState<IEnvio[]>([]);
  const [pagos, setPagos] = useState<IPago[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const cargarResumen = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const [pedidosResponse, inventariosResponse, enviosResponse, pagosResponse] = await Promise.all([
        axios.get<IPedido[]>('api/pedidos?size=200&sort=createdDate,desc'),
        axios.get<IProductoInventario[]>('api/producto-inventarios?size=1000'),
        axios.get<IEnvio[]>('api/envios?size=200'),
        axios.get<IPago[]>('api/pagos?size=200'),
      ]);
      setPedidos(pedidosResponse.data ?? []);
      setInventarios(inventariosResponse.data ?? []);
      setEnvios(enviosResponse.data ?? []);
      setPagos(pagosResponse.data ?? []);
    } catch {
      setError('No se pudo cargar el resumen operativo. Intenta nuevamente.');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    cargarResumen();
  }, [cargarResumen]);

  const pedidosPendientes = pedidos.filter(pedido => pedido.estado === 'PENDING').length;
  const ventas = pedidos
    .filter(pedido => pedido.estado !== 'CANCELLED' && pedido.estado !== 'RETURNED')
    .reduce((total, pedido) => total + (pedido.total ?? 0), 0);
  const stockBajo = inventarios.filter(
    inventario => inventario.stock != null && inventario.stockMinimo != null && inventario.stock <= inventario.stockMinimo,
  ).length;
  const enviosPendientes = envios.filter(envio => envio.estado === 'PENDING').length;
  const pagosAprobados = pagos.filter(pago => pago.estado === 'APPROVED').length;
  const ultimosPedidos = pedidos.slice(0, 5);

  const kpis = [
    { label: 'Pedidos totales', value: String(pedidos.length) },
    { label: 'Pendientes', value: String(pedidosPendientes) },
    { label: 'Ventas', value: formatCOP(ventas) },
    { label: 'Stock bajo', value: String(stockBajo) },
    { label: 'Envíos pendientes', value: String(enviosPendientes) },
  ];

  return (
    <Container fluid className="p-4 kn-fade-in">
      <h2 className="fw-bold mb-1">Panel administrativo</h2>
      <p className="text-muted mb-4">Resumen operativo de la tienda</p>

      {loading && (
        <div className="d-flex justify-content-center align-items-center py-5">
          <Spinner animation="border" role="status" />
        </div>
      )}

      {!loading && error && (
        <div className="alert alert-danger d-flex flex-column flex-sm-row align-items-sm-center justify-content-between gap-3" role="alert">
          <span>{error}</span>
          <button type="button" className="btn btn-outline-danger btn-sm" onClick={cargarResumen}>
            Reintentar
          </button>
        </div>
      )}

      {!loading && !error && (
        <>
          <Row className="g-3">
            {kpis.map(kpi => (
              <Col key={kpi.label} xs={6} md={4} xl={2}>
                <Card
                  className="h-100"
                  style={{
                    backgroundColor: 'var(--kn-color-surface-elevated)',
                    border: '1px solid var(--kn-color-border)',
                    color: 'var(--kn-color-text)',
                  }}
                >
                  <Card.Body>
                    <p className="admin-kpi__valor fw-bold mb-1">{kpi.value}</p>
                    <small className="text-muted">{kpi.label}</small>
                  </Card.Body>
                </Card>
              </Col>
            ))}
          </Row>

          <Card className="mt-4" style={{ border: '1px solid var(--kn-color-border)' }}>
            <Card.Header className="fw-semibold">Últimos pedidos</Card.Header>
            <Card.Body className="p-0">
              <Table responsive className="align-middle mb-0">
                <thead>
                  <tr>
                    <th>Número</th>
                    <th>Cliente</th>
                    <th>Total</th>
                    <th>Estado</th>
                    <th>Fecha</th>
                  </tr>
                </thead>
                <tbody>
                  {ultimosPedidos.map(pedido => (
                    <tr key={pedido.id}>
                      <td className="fw-semibold">#{pedido.numeroPedido || pedido.id || '-'}</td>
                      <td>{getNombreCliente(pedido)}</td>
                      <td>{formatCOP(pedido.total)}</td>
                      <td>
                        <Badge bg={ESTADO_PEDIDO_BADGE[pedido.estado || 'PENDING'] || 'secondary'}>{pedido.estado}</Badge>
                      </td>
                      <td>{formatFecha(pedido.createdDate)}</td>
                    </tr>
                  ))}
                  {ultimosPedidos.length === 0 && (
                    <tr>
                      <td colSpan={5} className="text-center text-muted py-4">
                        No hay pedidos registrados.
                      </td>
                    </tr>
                  )}
                </tbody>
              </Table>
            </Card.Body>
          </Card>

          <Card className="mt-4" style={{ border: '1px solid var(--kn-color-border)' }}>
            <Card.Body>
              <h5 className="fw-semibold mb-3">Accesos rápidos</h5>
              <div className="d-flex flex-wrap gap-2">
                {ACCESOS_RAPIDOS.map(acceso => (
                  <Link
                    key={acceso.to}
                    to={acceso.to}
                    className="btn btn-sm"
                    style={{
                      backgroundColor: 'var(--kn-color-surface-elevated)',
                      color: 'var(--kn-color-text)',
                      border: '1px solid var(--kn-color-border)',
                    }}
                  >
                    {acceso.label}
                  </Link>
                ))}
              </div>
              <p className="small text-muted mb-0 mt-3">Pagos aprobados disponibles para reembolso: {pagosAprobados}</p>
            </Card.Body>
          </Card>
        </>
      )}
    </Container>
  );
};

export default AdminDashboardPage;

import React, { useEffect, useMemo } from 'react';
import { Badge, Card, Table } from 'react-bootstrap';
import { Link } from 'react-router';
import dayjs from 'dayjs';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getSession } from 'app/shared/reducers/authentication';
import { getEntities as getFacturas } from 'app/entities/factura/factura.reducer';
import { getEntities as getPagos } from 'app/entities/pago/pago.reducer';
import { getEntities as getPedidos } from 'app/entities/pedido/pedido.reducer';
import { getEntities as getCuentas } from 'app/entities/cuenta/cuenta.reducer';
import LoadingSpinner from 'app/landing/components/LoadingSpinner';
import EmptyState from 'app/landing/components/EmptyState';
import { formatCOP } from 'app/landing/utils/format';

export const InvoicesPage = () => {
  const dispatch = useAppDispatch();
  const account = useAppSelector(state => state.authentication.account);
  const facturas = useAppSelector(state => state.factura.entities) ?? [];
  const pagos = useAppSelector(state => state.pago.entities) ?? [];
  const pedidos = useAppSelector(state => state.pedido.entities) ?? [];
  const cuentas = useAppSelector(state => state.cuenta.entities) ?? [];
  const loading = useAppSelector(state => state.factura.loading || state.pago.loading || state.pedido.loading || state.cuenta.loading);

  useEffect(() => {
    dispatch(getSession());
    dispatch(getCuentas({ page: 0, size: 100, sort: 'primerNombre,asc' }));
    dispatch(getPedidos({ page: 0, size: 100, sort: 'numeroPedido,desc' }));
    dispatch(getPagos({ page: 0, size: 100, sort: 'id,desc' }));
    dispatch(getFacturas({ page: 0, size: 100, sort: 'id,desc' }));
  }, [dispatch]);

  const cuentaUsuario = useMemo(() => cuentas.find(c => c.user?.login === account.login), [cuentas, account.login]);

  const pedidosUsuarioIds = useMemo(
    () => new Set(pedidos.filter(p => p.cuenta?.id === cuentaUsuario?.id).map(p => p.id)),
    [pedidos, cuentaUsuario],
  );

  const pagosUsuarioIds = useMemo(
    () => new Set(pagos.filter(p => p.pedido?.id && pedidosUsuarioIds.has(p.pedido.id)).map(p => p.id)),
    [pagos, pedidosUsuarioIds],
  );

  const facturasUsuario = useMemo(
    () => facturas.filter(f => f.pago?.id && pagosUsuarioIds.has(f.pago.id)).sort((a, b) => (b.id || '').localeCompare(a.id || '')),
    [facturas, pagosUsuarioIds],
  );

  if (loading) {
    return <LoadingSpinner fullScreen />;
  }

  if (facturasUsuario.length === 0) {
    return (
      <div className="kn-fade-in">
        <h1 className="h2 fw-bold mb-4">Mis facturas</h1>
        <EmptyState
          title="Aún no tienes facturas registradas"
          description="Cuando se emita una factura para tus pagos, podrás consultarla aquí."
          action={
            <Link to="/cuenta/pagos" className="btn btn-primary">
              Ver mis pagos
            </Link>
          }
        />
      </div>
    );
  }

  return (
    <div className="kn-fade-in">
      <h1 className="h2 fw-bold mb-4">Mis facturas</h1>
      <Card>
        <Card.Body className="p-0">
          <Table responsive className="align-middle mb-0">
            <thead>
              <tr>
                <th>Factura</th>
                <th>Pedido</th>
                <th>Estado</th>
                <th>Total</th>
                <th>Fecha de emisión</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              {facturasUsuario.map(factura => (
                <tr key={factura.id}>
                  <td className="fw-semibold">{factura.prefijo || factura.id}</td>
                  <td>
                    <Link to={`/cuenta/pedidos/${factura.pago?.pedido?.id}`}>
                      #{factura.pago?.pedido?.numeroPedido || factura.pago?.pedido?.id}
                    </Link>
                  </td>
                  <td>
                    <Badge bg={factura.enviada ? 'success' : 'warning'}>{factura.enviada ? 'Enviada' : 'Pendiente'}</Badge>
                  </td>
                  <td className="fw-semibold">{formatCOP(factura.total)}</td>
                  <td>{factura.fechaEmision ? dayjs(factura.fechaEmision).format('DD/MM/YYYY') : '-'}</td>
                  <td>
                    {factura.codigoQr && (
                      // TODO backend: exponer endpoint de descarga de factura con QR (RF-068).
                      <span className="text-muted small">Descarga no disponible</span>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        </Card.Body>
      </Card>
    </div>
  );
};

export default InvoicesPage;

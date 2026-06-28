import React, { useEffect, useMemo } from 'react';
import { Badge, Card, Table } from 'react-bootstrap';
import { Link } from 'react-router';
import dayjs from 'dayjs';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getSession } from 'app/shared/reducers/authentication';
import { getEntities as getPagos } from 'app/entities/pago/pago.reducer';
import { getEntities as getPedidos } from 'app/entities/pedido/pedido.reducer';
import { getEntities as getCuentas } from 'app/entities/cuenta/cuenta.reducer';
import LoadingSpinner from 'app/landing/components/LoadingSpinner';
import EmptyState from 'app/landing/components/EmptyState';
import { PAYMENT_STATUS_LABELS } from 'app/landing/utils/constants';
import { formatCOP } from 'app/landing/utils/format';

export const PaymentsPage = () => {
  const dispatch = useAppDispatch();
  const account = useAppSelector(state => state.authentication.account);
  const pagos = useAppSelector(state => state.pago.entities) ?? [];
  const pedidos = useAppSelector(state => state.pedido.entities) ?? [];
  const cuentas = useAppSelector(state => state.cuenta.entities) ?? [];
  const loading = useAppSelector(state => state.pago.loading || state.pedido.loading || state.cuenta.loading);

  useEffect(() => {
    dispatch(getSession());
    dispatch(getCuentas({ page: 0, size: 100, sort: 'primerNombre,asc' }));
    dispatch(getPedidos({ page: 0, size: 100, sort: 'numeroPedido,desc' }));
    dispatch(getPagos({ page: 0, size: 100, sort: 'id,desc' }));
  }, [dispatch]);

  const cuentaUsuario = useMemo(() => cuentas.find(c => c.user?.login === account.login), [cuentas, account.login]);

  const pedidosUsuarioIds = useMemo(
    () => new Set(pedidos.filter(p => p.cuenta?.id === cuentaUsuario?.id).map(p => p.id)),
    [pedidos, cuentaUsuario],
  );

  const pagosUsuario = useMemo(
    () => pagos.filter(p => p.pedido?.id && pedidosUsuarioIds.has(p.pedido.id)).sort((a, b) => (b.id || '').localeCompare(a.id || '')),
    [pagos, pedidosUsuarioIds],
  );

  if (loading) {
    return <LoadingSpinner fullScreen />;
  }

  if (pagosUsuario.length === 0) {
    return (
      <div className="kn-fade-in">
        <h1 className="h2 fw-bold mb-4">Mis pagos</h1>
        <EmptyState
          title="Aún no tienes pagos registrados"
          description="Cuando realices un pedido, podrás ver el historial de pagos aquí."
          action={
            <Link to="/cuenta/pedidos" className="btn btn-primary">
              Ver mis pedidos
            </Link>
          }
        />
      </div>
    );
  }

  return (
    <div className="kn-fade-in">
      <h1 className="h2 fw-bold mb-4">Mis pagos</h1>
      <Card>
        <Card.Body className="p-0">
          <Table responsive className="align-middle mb-0">
            <thead>
              <tr>
                <th>Pedido</th>
                <th>Método</th>
                <th>Estado</th>
                <th>Monto</th>
                <th>Fecha</th>
              </tr>
            </thead>
            <tbody>
              {pagosUsuario.map(pago => (
                <tr key={pago.id}>
                  <td>
                    <Link to={`/cuenta/pedidos/${pago.pedido?.id}`}>#{pago.pedido?.numeroPedido || pago.pedido?.id}</Link>
                  </td>
                  <td>{pago.metodoPago}</td>
                  <td>
                    <Badge bg={pago.estado === 'APPROVED' ? 'success' : pago.estado === 'REJECTED' ? 'danger' : 'warning'}>
                      {PAYMENT_STATUS_LABELS[pago.estado || 'Pendiente'] || pago.estado}
                    </Badge>
                  </td>
                  <td className="fw-semibold">{formatCOP(pago.monto)}</td>
                  <td>{pago.fechaPago ? dayjs(pago.fechaPago).format('DD/MM/YYYY HH:mm') : '-'}</td>
                </tr>
              ))}
            </tbody>
          </Table>
        </Card.Body>
      </Card>
    </div>
  );
};

export default PaymentsPage;

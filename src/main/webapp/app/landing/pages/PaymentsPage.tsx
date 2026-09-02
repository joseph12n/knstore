import React, { useEffect, useState } from 'react';
import { Badge, Card, Table } from 'react-bootstrap';
import { Link } from 'react-router';
import dayjs from 'dayjs';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities as getPagos } from 'app/entities/pago/pago.reducer';
import useCuentaActual from 'app/landing/hooks/useCuentaActual';
import LoadingSpinner from 'app/landing/components/LoadingSpinner';
import EmptyState from 'app/landing/components/EmptyState';
import Pagination from 'app/landing/components/Pagination';
import { PAYMENT_STATUS_LABELS } from 'app/landing/utils/constants';
import { formatCOP } from 'app/landing/utils/format';

const ITEMS_PER_PAGE = 10;

export const PaymentsPage = () => {
  const dispatch = useAppDispatch();
  const { account } = useCuentaActual();
  const pagos = useAppSelector(state => state.pago.entities) ?? [];
  const totalItems = useAppSelector(state => state.pago.totalItems ?? 0);
  const loading = useAppSelector(state => state.pago.loading);

  const [activePage, setActivePage] = useState(1);

  useEffect(() => {
    // El backend pagina y filtra por la cuenta del cliente autenticado.
    dispatch(getPagos({ page: activePage - 1, size: ITEMS_PER_PAGE, sort: 'id,desc' }));
  }, [dispatch, activePage, account.login]);

  if (loading) {
    return <LoadingSpinner fullScreen />;
  }

  if (pagos.length === 0) {
    return (
      <div className="kn-fade-in">
        <h1 className="h2 fw-bold mb-4">Mis pagos</h1>
        <EmptyState
          title="Aún no tienes pagos registrados"
          description="Cuando realices un pedido, podrás ver el historial de pagos aquí."
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
              {pagos.map(pago => (
                <tr key={pago.id}>
                  <td>
                    <Link to={`/mi-cuenta/pedidos/${pago.pedido?.id}`}>#{pago.pedido?.numeroPedido || pago.pedido?.id}</Link>
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
      <Pagination activePage={activePage} itemsPerPage={ITEMS_PER_PAGE} totalItems={totalItems} onPageChange={setActivePage} />
    </div>
  );
};

export default PaymentsPage;

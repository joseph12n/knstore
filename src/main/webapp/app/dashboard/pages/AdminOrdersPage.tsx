import React, { useEffect, useState } from 'react';
import { Badge, Card, Form, Table } from 'react-bootstrap';
import { toast } from 'react-toastify';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities, partialUpdateEntity as partialUpdatePedido } from 'app/entities/pedido/pedido.reducer';
import Pagination from 'app/landing/components/Pagination';
import LoadingSpinner from 'app/landing/components/LoadingSpinner';
import { ORDER_STATUS_COLORS, ORDER_STATUS_LABELS } from 'app/landing/utils/constants';
import { formatCOP } from 'app/landing/utils/format';
import { IPedido } from 'app/shared/model/pedido.model';
import { EstadoPedido } from 'app/shared/model/enumerations/estado-pedido.model';

type EstadoPedidoKey = keyof typeof EstadoPedido;

const VALID_TRANSITIONS: Record<EstadoPedidoKey, EstadoPedidoKey[]> = {
  PENDING: ['CONFIRMED', 'CANCELLED'],
  CONFIRMED: ['PROCESSING', 'CANCELLED'],
  PROCESSING: ['SHIPPED', 'CANCELLED'],
  SHIPPED: ['DELIVERED', 'RETURNED'],
  DELIVERED: ['RETURNED'],
  CANCELLED: [],
  RETURNED: [],
};

const AdminOrdersPage = () => {
  const dispatch = useAppDispatch();
  const pedidos = useAppSelector(state => state.pedido.entities) ?? [];
  const totalItems = useAppSelector(state => state.pedido.totalItems ?? 0);
  const loading = useAppSelector(state => state.pedido.loading);
  const [activePage, setActivePage] = useState(1);
  const [updatingId, setUpdatingId] = useState<string | null>(null);

  useEffect(() => {
    dispatch(getEntities({ page: activePage - 1, size: 20, sort: 'numeroPedido,desc' }));
  }, [dispatch, activePage]);

  const handleChangeStatus = async (pedido: IPedido, nuevoEstado: EstadoPedidoKey) => {
    if (!pedido.id || !pedido.estado) return;
    const permitidos = VALID_TRANSITIONS[pedido.estado] ?? [];
    if (!permitidos.includes(nuevoEstado)) {
      toast.error(`Transición inválida: ${pedido.estado} → ${nuevoEstado}`);
      return;
    }
    setUpdatingId(pedido.id);
    try {
      await dispatch(partialUpdatePedido({ id: pedido.id, estado: nuevoEstado }));
      toast.success('Estado del pedido actualizado.');
      dispatch(getEntities({ page: activePage - 1, size: 20, sort: 'numeroPedido,desc' }));
    } catch {
      toast.error('No se pudo actualizar el pedido.');
    } finally {
      setUpdatingId(null);
    }
  };

  return (
    <div className="p-4 kn-fade-in">
      <h1 className="h2 fw-bold mb-4">Operación · Pedidos</h1>
      <Card>
        <Card.Body className="p-0">
          {loading ? (
            <LoadingSpinner />
          ) : (
            <Table responsive className="align-middle mb-0">
              <thead>
                <tr>
                  <th>Pedido</th>
                  <th>Cliente</th>
                  <th>Total</th>
                  <th>Estado</th>
                  <th>Cambiar estado</th>
                </tr>
              </thead>
              <tbody>
                {pedidos.map(pedido => (
                  <tr key={pedido.id}>
                    <td className="fw-semibold">#{pedido.numeroPedido || pedido.id}</td>
                    <td>{pedido.cuenta?.nombres || pedido.cuenta?.user?.login || '-'}</td>
                    <td>{formatCOP(pedido.total)}</td>
                    <td>
                      <Badge bg={ORDER_STATUS_COLORS[pedido.estado || 'PENDING'] || 'secondary'}>
                        {ORDER_STATUS_LABELS[pedido.estado || 'PENDING'] || pedido.estado}
                      </Badge>
                    </td>
                    <td>
                      <Form.Select
                        size="sm"
                        value={pedido.estado || ''}
                        onChange={e => handleChangeStatus(pedido, e.target.value as EstadoPedidoKey)}
                        disabled={updatingId === pedido.id}
                        style={{ minWidth: '160px' }}
                      >
                        <option value={pedido.estado || ''} disabled>
                          {ORDER_STATUS_LABELS[pedido.estado || 'PENDING']}
                        </option>
                        {(VALID_TRANSITIONS[pedido.estado || 'PENDING'] ?? []).map(estado => (
                          <option key={estado} value={estado}>
                            {ORDER_STATUS_LABELS[estado] || estado}
                          </option>
                        ))}
                      </Form.Select>
                    </td>
                  </tr>
                ))}
                {pedidos.length === 0 && (
                  <tr>
                    <td colSpan={5} className="text-center text-muted py-4">
                      No hay pedidos para gestionar.
                    </td>
                  </tr>
                )}
              </tbody>
            </Table>
          )}
        </Card.Body>
      </Card>
      <Pagination activePage={activePage} itemsPerPage={20} totalItems={totalItems} onPageChange={setActivePage} />
    </div>
  );
};

export default AdminOrdersPage;

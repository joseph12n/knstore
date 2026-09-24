import React, { useEffect, useState } from 'react';
import { Badge, Button, Card, Form, Modal, Table } from 'react-bootstrap';
import { toast } from 'react-toastify';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities, partialUpdateEntity as partialUpdatePago } from 'app/entities/pago/pago.reducer';
import Pagination from 'app/landing/components/Pagination';
import LoadingSpinner from 'app/landing/components/LoadingSpinner';
import { PAYMENT_STATUS_LABELS } from 'app/landing/utils/constants';
import { formatCOP } from 'app/landing/utils/format';
import { IPago } from 'app/shared/model/pago.model';

const AdminRefundsPage = () => {
  const dispatch = useAppDispatch();
  const pagos = useAppSelector(state => state.pago.entities) ?? [];
  const totalItems = useAppSelector(state => state.pago.totalItems ?? 0);
  const loading = useAppSelector(state => state.pago.loading);
  const [activePage, setActivePage] = useState(1);
  const [refunding, setRefunding] = useState<IPago | null>(null);
  const [motivo, setMotivo] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);

  useEffect(() => {
    dispatch(getEntities({ page: activePage - 1, size: 20, sort: 'id,desc' }));
  }, [dispatch, activePage]);

  const handleRefund = async () => {
    if (!refunding?.id) return;
    setIsSubmitting(true);
    try {
      await dispatch(
        partialUpdatePago({
          id: refunding.id,
          estado: 'REFUNDED',
          descripcionRespuesta: `Reembolso procesado. Motivo: ${motivo || 'No especificado'}`,
        }),
      );
      toast.success('Reembolso registrado.');
      setRefunding(null);
      setMotivo('');
      dispatch(getEntities({ page: activePage - 1, size: 20, sort: 'id,desc' }));
    } catch {
      toast.error('No se pudo registrar el reembolso.');
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="p-4 kn-fade-in">
      <h1 className="h2 fw-bold mb-4">Operación · Reembolsos</h1>
      <Card>
        <Card.Body className="p-0">
          {loading ? (
            <LoadingSpinner />
          ) : (
            <Table responsive className="align-middle mb-0">
              <thead>
                <tr>
                  <th>Pago</th>
                  <th>Pedido</th>
                  <th>Monto</th>
                  <th>Estado</th>
                  <th>Acciones</th>
                </tr>
              </thead>
              <tbody>
                {pagos.map(pago => (
                  <tr key={pago.id}>
                    <td className="fw-semibold">{pago.id}</td>
                    <td>#{pago.pedido?.numeroPedido || pago.pedido?.id}</td>
                    <td>{formatCOP(pago.monto)}</td>
                    <td>
                      <Badge bg={pago.estado === 'APPROVED' ? 'success' : pago.estado === 'REFUNDED' ? 'secondary' : 'warning'}>
                        {PAYMENT_STATUS_LABELS[pago.estado || 'PENDING'] || pago.estado}
                      </Badge>
                    </td>
                    <td>
                      <Button variant="outline-danger" size="sm" disabled={pago.estado !== 'APPROVED'} onClick={() => setRefunding(pago)}>
                        Reembolsar
                      </Button>
                    </td>
                  </tr>
                ))}
                {pagos.length === 0 && (
                  <tr>
                    <td colSpan={5} className="text-center text-muted py-4">
                      No hay pagos para reembolsar.
                    </td>
                  </tr>
                )}
              </tbody>
            </Table>
          )}
        </Card.Body>
      </Card>
      <Pagination activePage={activePage} itemsPerPage={20} totalItems={totalItems} onPageChange={setActivePage} />

      <Modal show={!!refunding} onHide={() => setRefunding(null)}>
        <Modal.Header closeButton>
          <Modal.Title>Confirmar reembolso</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <p>
            Vas a reembolsar el pago <strong>{refunding?.id}</strong> del pedido{' '}
            <strong>#{refunding?.pedido?.numeroPedido || refunding?.pedido?.id}</strong> por <strong>{formatCOP(refunding?.monto)}</strong>.
          </p>
          <Form.Group>
            <Form.Label>Motivo del reembolso</Form.Label>
            <Form.Control as="textarea" rows={3} value={motivo} onChange={e => setMotivo(e.target.value)} />
          </Form.Group>
        </Modal.Body>
        <Modal.Footer>
          <Button variant="outline-secondary" onClick={() => setRefunding(null)} disabled={isSubmitting}>
            Cancelar
          </Button>
          <Button variant="danger" onClick={handleRefund} disabled={isSubmitting}>
            {isSubmitting ? 'Procesando...' : 'Confirmar reembolso'}
          </Button>
        </Modal.Footer>
      </Modal>
    </div>
  );
};

export default AdminRefundsPage;

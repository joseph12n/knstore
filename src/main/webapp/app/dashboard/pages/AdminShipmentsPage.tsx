import React, { useEffect, useState } from 'react';
import { Badge, Button, Card, Form, Modal, Table } from 'react-bootstrap';
import { toast } from 'react-toastify';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities as getEnvios, partialUpdateEntity as partialUpdateEnvio } from 'app/entities/envio/envio.reducer';
import Pagination from 'app/landing/components/Pagination';
import LoadingSpinner from 'app/landing/components/LoadingSpinner';
import { SHIPPING_STATUS_LABELS } from 'app/landing/utils/constants';
import { IEnvio } from 'app/shared/model/envio.model';
import { EstadoEnvio } from 'app/shared/model/enumerations/estado-envio.model';

const AdminShipmentsPage = () => {
  const dispatch = useAppDispatch();
  const envios = useAppSelector(state => state.envio.entities) ?? [];
  const totalItems = useAppSelector(state => state.envio.totalItems ?? 0);
  const loading = useAppSelector(state => state.envio.loading);
  const [activePage, setActivePage] = useState(1);
  const [editingEnvio, setEditingEnvio] = useState<IEnvio | null>(null);
  const [formData, setFormData] = useState<{ transportadora: string; numeroRastreo: string; urlRastreo: string }>({
    transportadora: '',
    numeroRastreo: '',
    urlRastreo: '',
  });

  useEffect(() => {
    dispatch(getEnvios({ page: activePage - 1, size: 20, sort: 'id,desc' }));
  }, [dispatch, activePage]);

  const openEdit = (envio: IEnvio) => {
    setEditingEnvio(envio);
    setFormData({
      transportadora: envio.transportadora || '',
      numeroRastreo: envio.numeroRastreo || '',
      urlRastreo: envio.urlRastreo || '',
    });
  };

  const handleSave = async () => {
    if (!editingEnvio?.id) return;
    try {
      await dispatch(
        partialUpdateEnvio({
          id: editingEnvio.id,
          transportadora: formData.transportadora,
          numeroRastreo: formData.numeroRastreo,
          urlRastreo: formData.urlRastreo,
        }),
      );
      toast.success('Envío actualizado.');
      setEditingEnvio(null);
      dispatch(getEnvios({ page: activePage - 1, size: 20, sort: 'id,desc' }));
    } catch {
      toast.error('No se pudo actualizar el envío.');
    }
  };

  const handleChangeStatus = async (envio: IEnvio, nuevoEstado: keyof typeof EstadoEnvio) => {
    if (!envio.id) return;
    try {
      await dispatch(partialUpdateEnvio({ id: envio.id, estado: nuevoEstado }));
      toast.success('Estado del envío actualizado.');
      dispatch(getEnvios({ page: activePage - 1, size: 20, sort: 'id,desc' }));
    } catch {
      toast.error('No se pudo actualizar el envío.');
    }
  };

  return (
    <div className="p-4 kn-fade-in">
      <h1 className="h2 fw-bold mb-4">Operación · Envíos</h1>
      <Card>
        <Card.Body className="p-0">
          {loading ? (
            <LoadingSpinner />
          ) : (
            <Table responsive className="align-middle mb-0">
              <thead>
                <tr>
                  <th>Pedido</th>
                  <th>Transportadora</th>
                  <th>Rastreo</th>
                  <th>Estado</th>
                  <th>Acciones</th>
                </tr>
              </thead>
              <tbody>
                {envios.map(envio => (
                  <tr key={envio.id}>
                    <td className="fw-semibold">#{envio.pedido?.numeroPedido || envio.pedido?.id}</td>
                    <td>{envio.transportadora || 'Por asignar'}</td>
                    <td>{envio.numeroRastreo || 'Pendiente'}</td>
                    <td>
                      <Badge bg={envio.estado === 'DELIVERED' ? 'success' : envio.estado === 'RETURNED' ? 'secondary' : 'info'}>
                        {SHIPPING_STATUS_LABELS[envio.estado || 'PENDING'] || envio.estado}
                      </Badge>
                    </td>
                    <td>
                      <Button variant="outline-primary" size="sm" className="me-2" onClick={() => openEdit(envio)}>
                        Editar
                      </Button>
                      <Form.Select
                        size="sm"
                        className="d-inline-block"
                        style={{ width: '140px' }}
                        value={envio.estado || ''}
                        onChange={e => handleChangeStatus(envio, e.target.value as keyof typeof EstadoEnvio)}
                      >
                        {Object.keys(EstadoEnvio).map(key => (
                          <option key={key} value={key}>
                            {SHIPPING_STATUS_LABELS[key] || key}
                          </option>
                        ))}
                      </Form.Select>
                    </td>
                  </tr>
                ))}
                {envios.length === 0 && (
                  <tr>
                    <td colSpan={5} className="text-center text-muted py-4">
                      No hay envíos para gestionar.
                    </td>
                  </tr>
                )}
              </tbody>
            </Table>
          )}
        </Card.Body>
      </Card>
      <Pagination activePage={activePage} itemsPerPage={20} totalItems={totalItems} onPageChange={setActivePage} />

      <Modal show={!!editingEnvio} onHide={() => setEditingEnvio(null)}>
        <Modal.Header closeButton>
          <Modal.Title>Editar envío</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <Form.Group className="mb-3">
            <Form.Label>Transportadora</Form.Label>
            <Form.Control value={formData.transportadora} onChange={e => setFormData({ ...formData, transportadora: e.target.value })} />
          </Form.Group>
          <Form.Group className="mb-3">
            <Form.Label>Número de rastreo</Form.Label>
            <Form.Control value={formData.numeroRastreo} onChange={e => setFormData({ ...formData, numeroRastreo: e.target.value })} />
          </Form.Group>
          <Form.Group>
            <Form.Label>URL de rastreo</Form.Label>
            <Form.Control value={formData.urlRastreo} onChange={e => setFormData({ ...formData, urlRastreo: e.target.value })} />
          </Form.Group>
        </Modal.Body>
        <Modal.Footer>
          <Button variant="outline-secondary" onClick={() => setEditingEnvio(null)}>
            Cancelar
          </Button>
          <Button variant="primary" onClick={handleSave}>
            Guardar
          </Button>
        </Modal.Footer>
      </Modal>
    </div>
  );
};

export default AdminShipmentsPage;

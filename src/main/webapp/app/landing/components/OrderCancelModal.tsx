import React, { useState } from 'react';
import { Button, Form, Modal } from 'react-bootstrap';

interface OrderCancelModalProps {
  show: boolean;
  onHide: () => void;
  onConfirm: (motivo: string) => void | Promise<void>;
  isSubmitting?: boolean;
  numeroPedido?: string;
}

export const OrderCancelModal = ({ show, onHide, onConfirm, isSubmitting = false, numeroPedido }: OrderCancelModalProps) => {
  const [motivo, setMotivo] = useState('');

  const handleClose = () => {
    if (isSubmitting) return;
    setMotivo('');
    onHide();
  };

  const handleConfirm = async () => {
    await onConfirm(motivo.trim());
    setMotivo('');
  };

  return (
    <Modal show={show} onHide={handleClose} centered backdrop="static">
      <Modal.Header closeButton={!isSubmitting}>
        <Modal.Title>Cancelar pedido</Modal.Title>
      </Modal.Header>
      <Modal.Body>
        <p>
          ¿Estás seguro de que deseas cancelar el pedido
          {numeroPedido ? <strong> #{numeroPedido}</strong> : ''}?
        </p>
        <p className="text-muted small">
          Esta acción no se puede deshacer. Si ya realizaste un pago, el reembolso será gestionado por nuestro equipo.
        </p>
        <Form.Group>
          <Form.Label>Motivo de la cancelación (opcional)</Form.Label>
          <Form.Control
            as="textarea"
            rows={3}
            value={motivo}
            onChange={e => setMotivo(e.target.value)}
            placeholder="Cuéntanos por qué cancelas tu pedido"
            disabled={isSubmitting}
          />
        </Form.Group>
      </Modal.Body>
      <Modal.Footer>
        <Button variant="outline-secondary" onClick={handleClose} disabled={isSubmitting}>
          Volver
        </Button>
        <Button variant="danger" onClick={handleConfirm} disabled={isSubmitting}>
          {isSubmitting ? 'Cancelando...' : 'Sí, cancelar pedido'}
        </Button>
      </Modal.Footer>
    </Modal>
  );
};

export default OrderCancelModal;

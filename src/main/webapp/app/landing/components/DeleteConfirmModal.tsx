import React from 'react';
import { Button, Modal } from 'react-bootstrap';

interface DeleteConfirmModalProps {
  show: boolean;
  onHide: () => void;
  onConfirm: () => void | Promise<void>;
  isSubmitting?: boolean;
  title?: string;
  message?: string;
  confirmLabel?: string;
  cancelLabel?: string;
}

export const DeleteConfirmModal = ({
  show,
  onHide,
  onConfirm,
  isSubmitting = false,
  title = 'Confirmar eliminación',
  message = '¿Estás seguro de eliminar este elemento? Esta acción no se puede deshacer.',
  confirmLabel = 'Eliminar',
  cancelLabel = 'Cancelar',
}: DeleteConfirmModalProps) => {
  const handleClose = () => {
    if (isSubmitting) return;
    onHide();
  };

  return (
    <Modal show={show} onHide={handleClose} centered backdrop="static">
      <Modal.Header closeButton={!isSubmitting}>
        <Modal.Title>{title}</Modal.Title>
      </Modal.Header>
      <Modal.Body>
        <p className="mb-0">{message}</p>
      </Modal.Body>
      <Modal.Footer>
        <Button variant="outline-secondary" onClick={handleClose} disabled={isSubmitting}>
          {cancelLabel}
        </Button>
        <Button variant="danger" onClick={onConfirm} disabled={isSubmitting}>
          {isSubmitting ? 'Eliminando...' : confirmLabel}
        </Button>
      </Modal.Footer>
    </Modal>
  );
};

export default DeleteConfirmModal;

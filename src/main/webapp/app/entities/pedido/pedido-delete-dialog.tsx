import React, { useEffect, useState } from 'react';
import { Button, Modal, ModalBody, ModalFooter, ModalHeader } from 'react-bootstrap';
import { useLocation, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { deleteEntity, getEntity } from './pedido.reducer';

export const PedidoDeleteDialog = () => {
  const dispatch = useAppDispatch();
  const pageLocation = useLocation();
  const navigate = useNavigate();
  const { id } = useParams<'id'>();

  const [loadModal, setLoadModal] = useState(false);

  useEffect(() => {
    dispatch(getEntity(id!));
    setLoadModal(true);
  }, []);

  const pedidoEntity = useAppSelector(state => state.pedido.entity);
  const updateSuccess = useAppSelector(state => state.pedido.updateSuccess);

  const handleClose = () => {
    navigate(`/pedido${pageLocation.search}`);
  };

  useEffect(() => {
    if (updateSuccess && loadModal) {
      handleClose();
      setLoadModal(false);
    }
  }, [updateSuccess]);

  const confirmDelete = () => {
    dispatch(deleteEntity(pedidoEntity.id));
  };

  return (
    <Modal show onHide={handleClose}>
      <ModalHeader data-cy="pedidoDeleteDialogHeading" closeButton>
        Confirmar operación de borrado
      </ModalHeader>
      <ModalBody id="knstoreApp.pedido.delete.question">¿Seguro que quiere eliminar Pedido {pedidoEntity.id}?</ModalBody>
      <ModalFooter>
        <Button variant="secondary" onClick={handleClose}>
          <FontAwesomeIcon icon="ban" />
          &nbsp; Cancelar
        </Button>
        <Button id="project-confirm-delete-pedido" data-cy="entityConfirmDeleteButton" variant="danger" onClick={confirmDelete}>
          <FontAwesomeIcon icon="trash" />
          &nbsp; Eliminar
        </Button>
      </ModalFooter>
    </Modal>
  );
};

export default PedidoDeleteDialog;

import React, { useEffect, useState } from 'react';
import { Button, Modal, ModalBody, ModalFooter, ModalHeader } from 'react-bootstrap';
import { useLocation, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { deleteEntity, getEntity } from './direccion.reducer';

export const DireccionDeleteDialog = () => {
  const dispatch = useAppDispatch();
  const pageLocation = useLocation();
  const navigate = useNavigate();
  const { id } = useParams<'id'>();

  const [loadModal, setLoadModal] = useState(false);

  useEffect(() => {
    dispatch(getEntity(id!));
    setLoadModal(true);
  }, []);

  const direccionEntity = useAppSelector(state => state.direccion.entity);
  const updateSuccess = useAppSelector(state => state.direccion.updateSuccess);

  const handleClose = () => {
    navigate(`/direccion${pageLocation.search}`);
  };

  useEffect(() => {
    if (updateSuccess && loadModal) {
      handleClose();
      setLoadModal(false);
    }
  }, [updateSuccess]);

  const confirmDelete = () => {
    dispatch(deleteEntity(direccionEntity.id));
  };

  return (
    <Modal show onHide={handleClose}>
      <ModalHeader data-cy="direccionDeleteDialogHeading" closeButton>
        Confirmar operación de borrado
      </ModalHeader>
      <ModalBody id="knstoreApp.direccion.delete.question">¿Seguro que quiere eliminar Direccion {direccionEntity.id}?</ModalBody>
      <ModalFooter>
        <Button variant="secondary" onClick={handleClose}>
          <FontAwesomeIcon icon="ban" />
          &nbsp; Cancelar
        </Button>
        <Button id="project-confirm-delete-direccion" data-cy="entityConfirmDeleteButton" variant="danger" onClick={confirmDelete}>
          <FontAwesomeIcon icon="trash" />
          &nbsp; Eliminar
        </Button>
      </ModalFooter>
    </Modal>
  );
};

export default DireccionDeleteDialog;

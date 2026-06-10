import React, { useEffect } from 'react';
import { Button, Modal, ModalBody, ModalFooter, ModalHeader } from 'react-bootstrap';
import { useNavigate, useParams } from 'react-router';

import { faBan, faTrash } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { deleteUser, getUser } from './user-management.reducer';

export const UserManagementDeleteDialog = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();
  const { login } = useParams<'login'>();

  useEffect(() => {
    dispatch(getUser(login!));
  }, []);

  const handleClose = () => {
    navigate('/admin/user-management');
  };

  const user = useAppSelector(state => state.userManagement.user);

  const confirmDelete = () => {
    dispatch(deleteUser(user.login));
    handleClose();
  };

  return (
    <Modal show onHide={handleClose}>
      <ModalHeader data-cy="userManagementDeleteDialogHeading" closeButton>
        Confirmar operación de borrado
      </ModalHeader>
      <ModalBody>¿Seguro que quieres eliminar el usuario {user.login}?</ModalBody>
      <ModalFooter>
        <Button variant="secondary" onClick={handleClose}>
          <FontAwesomeIcon icon={faBan} />
          &nbsp; Cancelar
        </Button>
        <Button variant="danger" onClick={confirmDelete} data-cy="entityConfirmDeleteButton">
          <FontAwesomeIcon icon={faTrash} />
          &nbsp; Eliminar
        </Button>
      </ModalFooter>
    </Modal>
  );
};

export default UserManagementDeleteDialog;

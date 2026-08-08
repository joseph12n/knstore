import React, { useEffect, useMemo, useState } from 'react';
import { Alert, Button, Card, Col, Modal, Row } from 'react-bootstrap';
import { Link, useNavigate } from 'react-router';
import { toast } from 'react-toastify';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getSession } from 'app/shared/reducers/authentication';
import {
  createEntity as createDireccion,
  deleteEntity as deleteDireccion,
  getEntities as getDireccions,
  updateEntity as updateDireccion,
  setPredeterminada,
} from 'app/entities/direccion/direccion.reducer';
import { createEntity as createCuenta, getCuentaByLogin, reset as resetCuenta } from 'app/entities/cuenta/cuenta.reducer';
import { ICuenta } from 'app/shared/model/cuenta.model';
import { IDireccion } from 'app/shared/model/direccion.model';
import AddressCard from 'app/landing/components/AddressCard';
import AddressForm from 'app/landing/components/AddressForm';
import DeleteConfirmModal from 'app/landing/components/DeleteConfirmModal';
import LoadingSpinner from 'app/landing/components/LoadingSpinner';

export const AddressesPage = () => {
  const dispatch = useAppDispatch();
  const navigate = useNavigate();
  const [showForm, setShowForm] = useState(false);
  const [editingAddress, setEditingAddress] = useState<IDireccion | undefined>(undefined);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [deletingAddress, setDeletingAddress] = useState<IDireccion | null>(null);
  const [isDeleting, setIsDeleting] = useState(false);

  const account = useAppSelector(state => state.authentication.account);
  const direcciones = useAppSelector(state => state.direccion.entities) ?? [];
  const cuenta = useAppSelector(state => state.cuenta.entity);
  const loading = useAppSelector(state => state.direccion.loading || state.cuenta.loading);

  useEffect(() => {
    dispatch(getSession());
    if (account.login) {
      dispatch(getCuentaByLogin(account.login));
    }
    dispatch(getDireccions({ page: 0, size: 100, sort: 'activo,desc' }));
    return () => {
      dispatch(resetCuenta());
    };
  }, [dispatch, account.login]);

  const direccionesUsuario = useMemo(() => direcciones.filter(d => d.cuenta?.id === cuenta?.id), [direcciones, cuenta]);

  const ensureCuenta = async (): Promise<ICuenta> => {
    if (cuenta?.id) {
      return cuenta;
    }

    const minimalCuenta: ICuenta = {
      primerNombre: account.firstName || account.login || 'Usuario',
      primerApellido: account.lastName || '',
      activo: true,
      user: { id: account.id, login: account.login },
    };

    const result = await dispatch(createCuenta(minimalCuenta)).unwrap();
    return result.data;
  };

  const handleOpenForm = (direccion?: IDireccion) => {
    setEditingAddress(direccion);
    setShowForm(true);
  };

  const handleCloseForm = () => {
    setShowForm(false);
    setEditingAddress(undefined);
  };

  const handleSubmit = async (data: any) => {
    setIsSubmitting(true);
    try {
      const targetCuenta = await ensureCuenta();
      const createdProfile = !cuenta?.id;

      const payload = {
        ...data,
        cuenta: { id: targetCuenta.id },
      };

      if (editingAddress?.id) {
        await dispatch(updateDireccion({ ...payload, id: editingAddress.id }));
        toast.success('Dirección actualizada correctamente.');
      } else {
        await dispatch(createDireccion(payload));
        toast.success('Dirección creada correctamente.');
        if (createdProfile) {
          toast.info('Creamos tu perfil automáticamente. Recuerda completarlo cuando quieras.');
        }
      }
      handleCloseForm();
    } catch {
      toast.error('No pudimos guardar la dirección. Inténtalo de nuevo.');
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleDelete = (direccion: IDireccion) => {
    setDeletingAddress(direccion);
  };

  const handleConfirmDelete = async () => {
    if (!deletingAddress?.id) {
      return;
    }
    setIsDeleting(true);
    try {
      await dispatch(deleteDireccion(deletingAddress.id));
      toast.success('Dirección eliminada correctamente.');
      setDeletingAddress(null);
    } catch {
      toast.error('No pudimos eliminar la dirección. Inténtalo de nuevo.');
    } finally {
      setIsDeleting(false);
    }
  };

  const handleSetDefault = async (direccion: IDireccion) => {
    try {
      await dispatch(setPredeterminada(direccion.id!));
      toast.success('Dirección predeterminada actualizada.');
    } catch {
      toast.error('No pudimos actualizar la dirección predeterminada.');
    }
  };

  return (
    <div className="kn-fade-in">
      <div className="d-flex justify-content-between align-items-center mb-4">
        <h1 className="h2 fw-bold mb-0">Mis direcciones</h1>
        <Button variant="primary" onClick={() => handleOpenForm()}>
          + Nueva dirección
        </Button>
      </div>

      <Link to="/mi-cuenta" className="text-muted small d-block mb-4">
        ← Volver a mi cuenta
      </Link>

      {!cuenta?.id && (
        <Alert variant="warning" className="d-flex justify-content-between align-items-center">
          <div className="me-3">
            <strong>Perfil incompleto.</strong> Puedes guardar direcciones, pero te recomendamos completar tu perfil para disfrutar de toda
            la experiencia.
          </div>
          <Button variant="outline-primary" size="sm" className="flex-shrink-0" onClick={() => navigate('/mi-cuenta/perfil/editar')}>
            Completar perfil
          </Button>
        </Alert>
      )}

      {loading ? (
        <LoadingSpinner fullScreen />
      ) : direccionesUsuario.length === 0 ? (
        <Card className="p-5 text-center">
          <p className="text-muted">No tienes direcciones guardadas.</p>
          <Button variant="primary" onClick={() => handleOpenForm()}>
            Agregar mi primera dirección
          </Button>
        </Card>
      ) : (
        <Row className="g-4">
          {direccionesUsuario.map(d => (
            <Col md={6} key={d.id}>
              <AddressCard
                direccion={d}
                isDefault={d.activo}
                onEdit={handleOpenForm}
                onDelete={handleDelete}
                onSetDefault={handleSetDefault}
              />
            </Col>
          ))}
        </Row>
      )}

      <Modal show={showForm} onHide={handleCloseForm} size="lg" centered>
        <Modal.Header closeButton>
          <Modal.Title className="fw-bold">{editingAddress ? 'Editar dirección' : 'Nueva dirección'}</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <AddressForm initialData={editingAddress} onSubmit={handleSubmit} onCancel={handleCloseForm} isSubmitting={isSubmitting} />
        </Modal.Body>
      </Modal>

      <DeleteConfirmModal
        show={!!deletingAddress}
        onHide={() => setDeletingAddress(null)}
        onConfirm={handleConfirmDelete}
        isSubmitting={isDeleting}
        title="Eliminar dirección"
        message="¿Estás seguro de eliminar esta dirección? Esta acción no se puede deshacer."
        confirmLabel="Sí, eliminar"
      />
    </div>
  );
};

export default AddressesPage;

import React, { useEffect, useMemo, useState } from 'react';
import { Button, Card, Col, Modal, Row } from 'react-bootstrap';
import { Link, useNavigate } from 'react-router';
import { toast } from 'react-toastify';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getSession } from 'app/shared/reducers/authentication';
import {
  createEntity as createDireccion,
  deleteEntity as deleteDireccion,
  getEntities as getDireccions,
  updateEntity as updateDireccion,
} from 'app/entities/direccion/direccion.reducer';
import { getEntities as getCuentas } from 'app/entities/cuenta/cuenta.reducer';
import { IDireccion } from 'app/shared/model/direccion.model';
import AddressCard from 'app/landing/components/AddressCard';
import AddressForm from 'app/landing/components/AddressForm';
import LoadingSpinner from 'app/landing/components/LoadingSpinner';

export const AddressesPage = () => {
  const dispatch = useAppDispatch();
  const navigate = useNavigate();
  const [showForm, setShowForm] = useState(false);
  const [editingAddress, setEditingAddress] = useState<IDireccion | undefined>(undefined);
  const [isSubmitting, setIsSubmitting] = useState(false);

  const account = useAppSelector(state => state.authentication.account);
  const direcciones = useAppSelector(state => state.direccion.entities) ?? [];
  const cuentas = useAppSelector(state => state.cuenta.entities) ?? [];
  const loading = useAppSelector(state => state.direccion.loading || state.cuenta.loading);

  useEffect(() => {
    dispatch(getSession());
    dispatch(getCuentas({ page: 0, size: 100, sort: 'primerNombre,asc' }));
    dispatch(getDireccions({ page: 0, size: 100, sort: 'activo,desc' }));
  }, [dispatch]);

  const cuentaUsuario = useMemo(() => cuentas.find(c => c.user?.login === account.login), [cuentas, account.login]);

  useEffect(() => {
    if (!loading && cuentaUsuario === undefined) {
      toast.info('Completa tu perfil para poder gestionar direcciones.');
      navigate('/cuenta/perfil');
    }
  }, [loading, cuentaUsuario, navigate]);

  const direccionesUsuario = useMemo(() => direcciones.filter(d => d.cuenta?.id === cuentaUsuario?.id), [direcciones, cuentaUsuario]);

  const handleOpenForm = (direccion?: IDireccion) => {
    setEditingAddress(direccion);
    setShowForm(true);
  };

  const handleCloseForm = () => {
    setShowForm(false);
    setEditingAddress(undefined);
  };

  const handleSubmit = async (data: any) => {
    if (!cuentaUsuario) {
      toast.error('No se encontró tu perfil de cliente.');
      return;
    }

    setIsSubmitting(true);
    try {
      const payload = {
        ...data,
        cuenta: { id: cuentaUsuario.id },
      };

      if (editingAddress?.id) {
        await dispatch(updateDireccion({ ...payload, id: editingAddress.id }));
        toast.success('Dirección actualizada correctamente.');
      } else {
        await dispatch(createDireccion(payload));
        toast.success('Dirección creada correctamente.');
      }
      handleCloseForm();
    } catch {
      toast.error('No pudimos guardar la dirección. Inténtalo de nuevo.');
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleDelete = async (direccion: IDireccion) => {
    if (!window.confirm('¿Estás seguro de eliminar esta dirección?')) {
      return;
    }
    try {
      await dispatch(deleteDireccion(direccion.id!));
      toast.success('Dirección eliminada correctamente.');
    } catch {
      toast.error('No pudimos eliminar la dirección. Inténtalo de nuevo.');
    }
  };

  const handleSetDefault = async (direccion: IDireccion) => {
    try {
      // Desactivar otras y activar la seleccionada
      for (const d of direccionesUsuario) {
        if (d.id !== direccion.id && d.activo) {
          await dispatch(updateDireccion({ ...d, activo: false, cuenta: { id: cuentaUsuario?.id } }));
        }
      }
      await dispatch(
        updateDireccion({
          ...direccion,
          activo: true,
          cuenta: { id: cuentaUsuario?.id },
        }),
      );
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

      <Link to="/cuenta" className="text-muted small d-block mb-4">
        ← Volver a mi cuenta
      </Link>

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
    </div>
  );
};

export default AddressesPage;

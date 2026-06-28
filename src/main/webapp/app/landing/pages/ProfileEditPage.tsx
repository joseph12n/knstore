import React, { useEffect, useMemo, useState } from 'react';
import { Card } from 'react-bootstrap';
import { toast } from 'react-toastify';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getSession } from 'app/shared/reducers/authentication';
import { getEntities as getCuentas, updateEntity as updateCuenta } from 'app/entities/cuenta/cuenta.reducer';
import { getEntities as getTiposDocumento } from 'app/entities/tipo-documento/tipo-documento.reducer';
import { saveAccountSettings, reset as resetSettings } from 'app/modules/account/settings/settings.reducer';
import ProfileForm, { ProfileFormData } from 'app/landing/components/ProfileForm';
import LoadingSpinner from 'app/landing/components/LoadingSpinner';
import dayjs from 'dayjs';

export const ProfileEditPage = () => {
  const dispatch = useAppDispatch();
  const [isSubmitting, setIsSubmitting] = useState(false);

  const account = useAppSelector(state => state.authentication.account);
  const cuentas = useAppSelector(state => state.cuenta.entities) ?? [];
  const tiposDocumento = useAppSelector(state => state.tipoDocumento.entities) ?? [];
  const loadingCuenta = useAppSelector(state => state.cuenta.loading);
  const loadingTipoDocumento = useAppSelector(state => state.tipoDocumento.loading);

  useEffect(() => {
    dispatch(getSession());
    dispatch(getCuentas({ page: 0, size: 100, sort: 'primerNombre,asc' }));
    dispatch(getTiposDocumento({ page: 0, size: 100, sort: 'nombreTipo,asc' }));

    return () => {
      dispatch(resetSettings());
    };
  }, [dispatch]);

  const cuenta = useMemo(() => cuentas.find(c => c.user?.login === account.login), [cuentas, account.login]);

  const handleSubmit = async (data: ProfileFormData) => {
    if (!cuenta?.id) {
      toast.error('No se encontró tu perfil de cliente.');
      return;
    }

    setIsSubmitting(true);

    try {
      const tipoDocumento = data.tipoDocumentoId ? { id: data.tipoDocumentoId } : null;

      await dispatch(
        updateCuenta({
          ...cuenta,
          primerNombre: data.primerNombre,
          segundoNombre: data.segundoNombre || undefined,
          primerApellido: data.primerApellido,
          segundoApellido: data.segundoApellido || undefined,
          numDocumento: data.numDocumento || undefined,
          genero: data.genero || undefined,
          fechaNacimiento: data.fechaNacimiento ? dayjs(data.fechaNacimiento) : undefined,
          celular: data.celular || undefined,
          telefono: data.telefono || undefined,
          tipoDocumento,
          user: { id: account.id, login: account.login },
        }),
      );

      dispatch(
        saveAccountSettings({
          ...account,
          firstName: data.primerNombre,
          lastName: data.primerApellido,
        }),
      );

      toast.success('Perfil actualizado correctamente.');
    } catch {
      toast.error('No pudimos actualizar tu perfil. Inténtalo de nuevo.');
    } finally {
      setIsSubmitting(false);
    }
  };

  const loading = loadingCuenta || loadingTipoDocumento;

  if (loading && !cuenta) {
    return <LoadingSpinner fullScreen />;
  }

  return (
    <div className="kn-fade-in">
      <h1 className="h2 fw-bold mb-4">Editar perfil</h1>
      <Card>
        <Card.Body className="p-4">
          <ProfileForm
            initialData={cuenta}
            tiposDocumento={tiposDocumento}
            onSubmit={handleSubmit}
            onCancel={() => window.history.back()}
            isSubmitting={isSubmitting}
          />
        </Card.Body>
      </Card>
    </div>
  );
};

export default ProfileEditPage;

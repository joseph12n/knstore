import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { ValidatedField, ValidatedForm, isNumber } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { EstadoIVA } from 'app/shared/model/enumerations/estado-iva.model';

import { createEntity, getEntity, reset, updateEntity } from './categoria-iva.reducer';

export const CategoriaIVAUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const categoriaIVAEntity = useAppSelector(state => state.categoriaIVA.entity);
  const loading = useAppSelector(state => state.categoriaIVA.loading);
  const updating = useAppSelector(state => state.categoriaIVA.updating);
  const updateSuccess = useAppSelector(state => state.categoriaIVA.updateSuccess);
  const estadoIVAValues = Object.keys(EstadoIVA);

  const handleClose = () => {
    navigate('/categoria-iva');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    if (values.porcentaje !== undefined && typeof values.porcentaje !== 'number') {
      values.porcentaje = Number(values.porcentaje);
    }

    const entity = {
      ...categoriaIVAEntity,
      ...values,
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {}
      : {
          estado: 'ACTIVO',
          ...categoriaIVAEntity,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="knstoreApp.categoriaIVA.home.createOrEditLabel" data-cy="CategoriaIVACreateUpdateHeading">
            Crear o editar Categoria IVA
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew && <ValidatedField name="id" required readOnly id="categoria-iva-id" label="ID" validate={{ required: true }} />}
              <ValidatedField
                label="Nombre"
                id="categoria-iva-nombre"
                name="nombre"
                data-cy="nombre"
                type="text"
                validate={{
                  required: { value: true, message: 'Este campo es obligatorio.' },
                  maxLength: { value: 60, message: 'Este campo no puede superar más de 60 caracteres.' },
                }}
              />
              <ValidatedField
                label="Porcentaje"
                id="categoria-iva-porcentaje"
                name="porcentaje"
                data-cy="porcentaje"
                type="text"
                validate={{
                  required: { value: true, message: 'Este campo es obligatorio.' },
                  min: { value: 0, message: 'Este campo debe ser mayor que 0.' },
                  max: { value: 100, message: 'Este campo no puede ser mayor que 100.' },
                  validate: v => isNumber(v) || 'Este campo debe ser un número.',
                }}
              />
              <ValidatedField label="Estado" id="categoria-iva-estado" name="estado" data-cy="estado" type="select">
                {estadoIVAValues.map(estadoIVA => (
                  <option value={estadoIVA} key={estadoIVA}>
                    {estadoIVA}
                  </option>
                ))}
              </ValidatedField>
              <Button as={Link as any} id="cancel-save" data-cy="entityCreateCancelButton" to="/categoria-iva" replace variant="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">Volver</span>
              </Button>
              &nbsp;
              <Button variant="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp; Guardar
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default CategoriaIVAUpdate;

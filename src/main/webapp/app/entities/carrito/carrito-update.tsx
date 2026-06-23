import React, { useEffect } from 'react';
import { Button, Col, FormText, Row } from 'react-bootstrap';
import { ValidatedField, ValidatedForm, isNumber } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities as getCuentas } from 'app/entities/cuenta/cuenta.reducer';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';

import { createEntity, getEntity, reset, updateEntity } from './carrito.reducer';

export const CarritoUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const cuentas = useAppSelector(state => state.cuenta.entities);
  const carritoEntity = useAppSelector(state => state.carrito.entity);
  const loading = useAppSelector(state => state.carrito.loading);
  const updating = useAppSelector(state => state.carrito.updating);
  const updateSuccess = useAppSelector(state => state.carrito.updateSuccess);

  const handleClose = () => {
    navigate('/carrito');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getCuentas({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    if (values.subtotal !== undefined && typeof values.subtotal !== 'number') {
      values.subtotal = Number(values.subtotal);
    }
    values.fechaActualizacion = convertDateTimeToServer(values.fechaActualizacion);

    const entity = {
      ...carritoEntity,
      ...values,
      cuenta: cuentas.find(it => it.id.toString() === values.cuenta?.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {
          fechaActualizacion: displayDefaultDateTime(),
        }
      : {
          ...carritoEntity,
          fechaActualizacion: convertDateTimeFromServer(carritoEntity.fechaActualizacion),
          cuenta: carritoEntity?.cuenta?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="knstoreApp.carrito.home.createOrEditLabel" data-cy="CarritoCreateUpdateHeading">
            Crear o editar Carrito
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew && <ValidatedField name="id" required readOnly id="carrito-id" label="ID" validate={{ required: true }} />}
              <ValidatedField
                label="Subtotal"
                id="carrito-subtotal"
                name="subtotal"
                data-cy="subtotal"
                type="text"
                validate={{
                  min: { value: 0, message: 'Este campo debe ser mayor que 0.' },
                  validate: v => isNumber(v) || 'Este campo debe ser un número.',
                }}
              />
              <ValidatedField
                label="Fecha Actualizacion"
                id="carrito-fechaActualizacion"
                name="fechaActualizacion"
                data-cy="fechaActualizacion"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField id="carrito-cuenta" name="cuenta" data-cy="cuenta" label="Cuenta" type="select" required>
                <option value="" key="0" />
                {cuentas
                  ? cuentas.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>Este campo es obligatorio.</FormText>
              <Button as={Link as any} id="cancel-save" data-cy="entityCreateCancelButton" to="/carrito" replace variant="info">
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

export default CarritoUpdate;

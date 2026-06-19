import React, { useEffect } from 'react';
import { Button, Col, FormText, Row } from 'react-bootstrap';
import { ValidatedField, ValidatedForm } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities as getCuentas } from 'app/entities/cuenta/cuenta.reducer';
import { Authority } from 'app/shared/jhipster/constants';

import { createEntity, getEntity, reset, updateEntity } from './direccion.reducer';

export const DireccionUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const cuentas = useAppSelector(state => state.cuenta.entities);
  const direccionEntity = useAppSelector(state => state.direccion.entity);
  const loading = useAppSelector(state => state.direccion.loading);
  const updating = useAppSelector(state => state.direccion.updating);
  const updateSuccess = useAppSelector(state => state.direccion.updateSuccess);
  const authorities = useAppSelector(state => state.authentication.account.authorities || []);
  const isCliente = authorities.includes(Authority.CLIENTE);

  const handleClose = () => {
    navigate(`/direccion${location.search}`);
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
    const entity = {
      ...direccionEntity,
      ...values,
      cuenta: isCliente ? direccionEntity?.cuenta : cuentas.find(it => it.id.toString() === values.cuenta?.toString()),
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
          ...direccionEntity,
          cuenta: direccionEntity?.cuenta?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="knstoreApp.direccion.home.createOrEditLabel" data-cy="DireccionCreateUpdateHeading">
            Crear o editar Direccion
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew && <ValidatedField name="id" required readOnly id="direccion-id" label="ID" validate={{ required: true }} />}
              <ValidatedField
                label="Direccion"
                id="direccion-direccion"
                name="direccion"
                data-cy="direccion"
                type="text"
                validate={{
                  required: { value: true, message: 'Este campo es obligatorio.' },
                  maxLength: { value: 100, message: 'Este campo no puede superar más de 100 caracteres.' },
                }}
              />
              <ValidatedField
                label="Barrio"
                id="direccion-barrio"
                name="barrio"
                data-cy="barrio"
                type="text"
                validate={{
                  maxLength: { value: 100, message: 'Este campo no puede superar más de 100 caracteres.' },
                }}
              />
              <ValidatedField
                label="Localidad"
                id="direccion-localidad"
                name="localidad"
                data-cy="localidad"
                type="text"
                validate={{
                  maxLength: { value: 100, message: 'Este campo no puede superar más de 100 caracteres.' },
                }}
              />
              <ValidatedField
                label="Municipio"
                id="direccion-municipio"
                name="municipio"
                data-cy="municipio"
                type="text"
                validate={{
                  required: { value: true, message: 'Este campo es obligatorio.' },
                  maxLength: { value: 100, message: 'Este campo no puede superar más de 100 caracteres.' },
                }}
              />
              <ValidatedField
                label="Departamento"
                id="direccion-departamento"
                name="departamento"
                data-cy="departamento"
                type="text"
                validate={{
                  required: { value: true, message: 'Este campo es obligatorio.' },
                  maxLength: { value: 100, message: 'Este campo no puede superar más de 100 caracteres.' },
                }}
              />
              <ValidatedField label="Activo" id="direccion-activo" name="activo" data-cy="activo" check type="checkbox" />
              <ValidatedField
                id="direccion-cuenta"
                name="cuenta"
                data-cy="cuenta"
                label="Cuenta"
                type="select"
                required
                disabled={isCliente}
              >
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
              <Button as={Link as any} id="cancel-save" data-cy="entityCreateCancelButton" to="/direccion" replace variant="info">
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

export default DireccionUpdate;

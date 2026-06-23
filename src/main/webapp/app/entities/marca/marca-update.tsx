import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { ValidatedField, ValidatedForm } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { createEntity, getEntity, reset, updateEntity } from './marca.reducer';

export const MarcaUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const marcaEntity = useAppSelector(state => state.marca.entity);
  const loading = useAppSelector(state => state.marca.loading);
  const updating = useAppSelector(state => state.marca.updating);
  const updateSuccess = useAppSelector(state => state.marca.updateSuccess);

  const handleClose = () => {
    navigate('/marca');
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
    const entity = {
      ...marcaEntity,
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
          ...marcaEntity,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="knstoreApp.marca.home.createOrEditLabel" data-cy="MarcaCreateUpdateHeading">
            Crear o editar Marca
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew && <ValidatedField name="id" required readOnly id="marca-id" label="ID" validate={{ required: true }} />}
              <ValidatedField
                label="Nombre"
                id="marca-nombre"
                name="nombre"
                data-cy="nombre"
                type="text"
                validate={{
                  required: { value: true, message: 'Este campo es obligatorio.' },
                  maxLength: { value: 100, message: 'Este campo no puede superar más de 100 caracteres.' },
                }}
              />
              <ValidatedField
                label="Slug"
                id="marca-slug"
                name="slug"
                data-cy="slug"
                type="text"
                validate={{
                  required: { value: true, message: 'Este campo es obligatorio.' },
                  maxLength: { value: 120, message: 'Este campo no puede superar más de 120 caracteres.' },
                }}
              />
              <Button as={Link as any} id="cancel-save" data-cy="entityCreateCancelButton" to="/marca" replace variant="info">
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

export default MarcaUpdate;

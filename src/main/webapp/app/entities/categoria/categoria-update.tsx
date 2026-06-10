import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { ValidatedBlobField, ValidatedField, ValidatedForm } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { createEntity, getEntity, reset, updateEntity } from './categoria.reducer';

export const CategoriaUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const categoriaEntity = useAppSelector(state => state.categoria.entity);
  const loading = useAppSelector(state => state.categoria.loading);
  const updating = useAppSelector(state => state.categoria.updating);
  const updateSuccess = useAppSelector(state => state.categoria.updateSuccess);

  const handleClose = () => {
    navigate(`/categoria${location.search}`);
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
      ...categoriaEntity,
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
          ...categoriaEntity,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="knstoreApp.categoria.home.createOrEditLabel" data-cy="CategoriaCreateUpdateHeading">
            Crear o editar Categoria
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew && <ValidatedField name="id" required readOnly id="categoria-id" label="ID" validate={{ required: true }} />}
              <ValidatedField
                label="Nombre"
                id="categoria-nombre"
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
                id="categoria-slug"
                name="slug"
                data-cy="slug"
                type="text"
                validate={{
                  required: { value: true, message: 'Este campo es obligatorio.' },
                  maxLength: { value: 120, message: 'Este campo no puede superar más de 120 caracteres.' },
                }}
              />
              <ValidatedField label="Descripcion" id="categoria-descripcion" name="descripcion" data-cy="descripcion" type="textarea" />
              <ValidatedBlobField label="Imagen" id="categoria-imagen" name="imagen" data-cy="imagen" isImage accept="image/*" />
              <ValidatedField label="Activo" id="categoria-activo" name="activo" data-cy="activo" check type="checkbox" />
              <Button as={Link as any} id="cancel-save" data-cy="entityCreateCancelButton" to="/categoria" replace variant="info">
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

export default CategoriaUpdate;

import React, { useEffect } from 'react';
import { Button, Col, FormText, Row } from 'react-bootstrap';
import { ValidatedBlobField, ValidatedField, ValidatedForm } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities as getCategorias } from 'app/entities/categoria/categoria.reducer';

import { createEntity, getEntity, reset, updateEntity } from './subcategoria.reducer';

export const SubcategoriaUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const categorias = useAppSelector(state => state.categoria.entities);
  const subcategoriaEntity = useAppSelector(state => state.subcategoria.entity);
  const loading = useAppSelector(state => state.subcategoria.loading);
  const updating = useAppSelector(state => state.subcategoria.updating);
  const updateSuccess = useAppSelector(state => state.subcategoria.updateSuccess);

  const handleClose = () => {
    navigate(`/subcategoria${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getCategorias({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    const entity = {
      ...subcategoriaEntity,
      ...values,
      categoria: categorias.find(it => it.id.toString() === values.categoria?.toString()),
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
          ...subcategoriaEntity,
          categoria: subcategoriaEntity?.categoria?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="knstoreApp.subcategoria.home.createOrEditLabel" data-cy="SubcategoriaCreateUpdateHeading">
            Crear o editar Subcategoria
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew && <ValidatedField name="id" required readOnly id="subcategoria-id" label="ID" validate={{ required: true }} />}
              <ValidatedField
                label="Nombre"
                id="subcategoria-nombre"
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
                id="subcategoria-slug"
                name="slug"
                data-cy="slug"
                type="text"
                validate={{
                  required: { value: true, message: 'Este campo es obligatorio.' },
                  maxLength: { value: 120, message: 'Este campo no puede superar más de 120 caracteres.' },
                }}
              />
              <ValidatedField label="Descripcion" id="subcategoria-descripcion" name="descripcion" data-cy="descripcion" type="textarea" />
              <ValidatedBlobField label="Imagen" id="subcategoria-imagen" name="imagen" data-cy="imagen" isImage accept="image/*" />
              <ValidatedField label="Activo" id="subcategoria-activo" name="activo" data-cy="activo" check type="checkbox" />
              <ValidatedField id="subcategoria-categoria" name="categoria" data-cy="categoria" label="Categoria" type="select" required>
                <option value="" key="0" />
                {categorias
                  ? categorias.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.nombre}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>Este campo es obligatorio.</FormText>
              <Button as={Link as any} id="cancel-save" data-cy="entityCreateCancelButton" to="/subcategoria" replace variant="info">
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

export default SubcategoriaUpdate;

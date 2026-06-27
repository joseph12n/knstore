import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { ValidatedBlobField, ValidatedField, ValidatedForm } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities as getProductos } from 'app/entities/producto/producto.reducer';

import { createEntity, getEntity, reset, updateEntity } from './producto-imagen.reducer';

export const ProductoImagenUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const productos = useAppSelector(state => state.producto.entities);
  const productoImagenEntity = useAppSelector(state => state.productoImagen.entity);
  const loading = useAppSelector(state => state.productoImagen.loading);
  const updating = useAppSelector(state => state.productoImagen.updating);
  const updateSuccess = useAppSelector(state => state.productoImagen.updateSuccess);

  const handleClose = () => {
    navigate('/producto-imagen');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getProductos({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    const entity = {
      ...productoImagenEntity,
      ...values,
      producto: productos.find(it => it.id.toString() === values.producto?.toString()),
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
          ...productoImagenEntity,
          producto: productoImagenEntity?.producto?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="knstoreApp.productoImagen.home.createOrEditLabel" data-cy="ProductoImagenCreateUpdateHeading">
            Crear o editar Producto Imagen
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew && <ValidatedField name="id" required readOnly id="producto-imagen-id" label="ID" validate={{ required: true }} />}
              <ValidatedBlobField
                label="Imagen"
                id="producto-imagen-imagen"
                name="imagen"
                data-cy="imagen"
                isImage
                accept="image/*"
                validate={{
                  required: { value: true, message: 'Este campo es obligatorio.' },
                }}
              />
              <ValidatedField
                label="Imagen Alt"
                id="producto-imagen-imagenAlt"
                name="imagenAlt"
                data-cy="imagenAlt"
                type="text"
                validate={{
                  maxLength: { value: 200, message: 'Este campo no puede superar más de 200 caracteres.' },
                }}
              />
              <ValidatedField
                label="Es Principal"
                id="producto-imagen-esPrincipal"
                name="esPrincipal"
                data-cy="esPrincipal"
                check
                type="checkbox"
              />
              <ValidatedField id="producto-imagen-producto" name="producto" data-cy="producto" label="Producto" type="select">
                <option value="" key="0" />
                {productos
                  ? productos.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.nombre}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button as={Link as any} id="cancel-save" data-cy="entityCreateCancelButton" to="/producto-imagen" replace variant="info">
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

export default ProductoImagenUpdate;

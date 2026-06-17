import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { ValidatedField, ValidatedForm, isNumber } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { createEntity, getEntity, reset, updateEntity } from './producto-precio.reducer';

export const ProductoPrecioUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const productoPrecioEntity = useAppSelector(state => state.productoPrecio.entity);
  const loading = useAppSelector(state => state.productoPrecio.loading);
  const updating = useAppSelector(state => state.productoPrecio.updating);
  const updateSuccess = useAppSelector(state => state.productoPrecio.updateSuccess);

  const handleClose = () => {
    navigate('/producto-precio');
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
    if (values.precioCompra !== undefined && typeof values.precioCompra !== 'number') {
      values.precioCompra = Number(values.precioCompra);
    }
    if (values.precioVenta !== undefined && typeof values.precioVenta !== 'number') {
      values.precioVenta = Number(values.precioVenta);
    }
    if (values.precioAdicional !== undefined && typeof values.precioAdicional !== 'number') {
      values.precioAdicional = Number(values.precioAdicional);
    }
    if (values.ganancia !== undefined && typeof values.ganancia !== 'number') {
      values.ganancia = Number(values.ganancia);
    }

    const entity = {
      ...productoPrecioEntity,
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
          ...productoPrecioEntity,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="knstoreApp.productoPrecio.home.createOrEditLabel" data-cy="ProductoPrecioCreateUpdateHeading">
            Crear o editar Producto Precio
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew && <ValidatedField name="id" required readOnly id="producto-precio-id" label="ID" validate={{ required: true }} />}
              <ValidatedField
                label="Precio Compra"
                id="producto-precio-precioCompra"
                name="precioCompra"
                data-cy="precioCompra"
                type="text"
                validate={{
                  required: { value: true, message: 'Este campo es obligatorio.' },
                  min: { value: 0, message: 'Este campo debe ser mayor que 0.' },
                  validate: v => isNumber(v) || 'Este campo debe ser un número.',
                }}
              />
              <ValidatedField
                label="Precio Venta"
                id="producto-precio-precioVenta"
                name="precioVenta"
                data-cy="precioVenta"
                type="text"
                validate={{
                  required: { value: true, message: 'Este campo es obligatorio.' },
                  min: { value: 0, message: 'Este campo debe ser mayor que 0.' },
                  validate: v => isNumber(v) || 'Este campo debe ser un número.',
                }}
              />
              <ValidatedField
                label="Precio Adicional"
                id="producto-precio-precioAdicional"
                name="precioAdicional"
                data-cy="precioAdicional"
                type="text"
                validate={{
                  min: { value: 0, message: 'Este campo debe ser mayor que 0.' },
                  validate: v => isNumber(v) || 'Este campo debe ser un número.',
                }}
              />
              <ValidatedField label="Ganancia" id="producto-precio-ganancia" name="ganancia" data-cy="ganancia" type="text" />
              <Button as={Link as any} id="cancel-save" data-cy="entityCreateCancelButton" to="/producto-precio" replace variant="info">
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

export default ProductoPrecioUpdate;

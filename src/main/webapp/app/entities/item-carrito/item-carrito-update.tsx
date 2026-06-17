import React, { useEffect } from 'react';
import { Button, Col, FormText, Row } from 'react-bootstrap';
import { ValidatedField, ValidatedForm, isNumber } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities as getCarritos } from 'app/entities/carrito/carrito.reducer';
import { getEntities as getProductos } from 'app/entities/producto/producto.reducer';

import { createEntity, getEntity, reset, updateEntity } from './item-carrito.reducer';

export const ItemCarritoUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const carritos = useAppSelector(state => state.carrito.entities);
  const productos = useAppSelector(state => state.producto.entities);
  const itemCarritoEntity = useAppSelector(state => state.itemCarrito.entity);
  const loading = useAppSelector(state => state.itemCarrito.loading);
  const updating = useAppSelector(state => state.itemCarrito.updating);
  const updateSuccess = useAppSelector(state => state.itemCarrito.updateSuccess);

  const handleClose = () => {
    navigate('/item-carrito');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getCarritos({}));
    dispatch(getProductos({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    if (values.cantidad !== undefined && typeof values.cantidad !== 'number') {
      values.cantidad = Number(values.cantidad);
    }
    if (values.precioUnitario !== undefined && typeof values.precioUnitario !== 'number') {
      values.precioUnitario = Number(values.precioUnitario);
    }
    if (values.subtotal !== undefined && typeof values.subtotal !== 'number') {
      values.subtotal = Number(values.subtotal);
    }

    const entity = {
      ...itemCarritoEntity,
      ...values,
      carrito: carritos.find(it => it.id.toString() === values.carrito?.toString()),
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
          ...itemCarritoEntity,
          carrito: itemCarritoEntity?.carrito?.id,
          producto: itemCarritoEntity?.producto?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="knstoreApp.itemCarrito.home.createOrEditLabel" data-cy="ItemCarritoCreateUpdateHeading">
            Crear o editar Item Carrito
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew && <ValidatedField name="id" required readOnly id="item-carrito-id" label="ID" validate={{ required: true }} />}
              <ValidatedField
                label="Cantidad"
                id="item-carrito-cantidad"
                name="cantidad"
                data-cy="cantidad"
                type="text"
                validate={{
                  required: { value: true, message: 'Este campo es obligatorio.' },
                  min: { value: 1, message: 'Este campo debe ser mayor que 1.' },
                  validate: v => isNumber(v) || 'Este campo debe ser un número.',
                }}
              />
              <ValidatedField
                label="Precio Unitario"
                id="item-carrito-precioUnitario"
                name="precioUnitario"
                data-cy="precioUnitario"
                type="text"
                validate={{
                  required: { value: true, message: 'Este campo es obligatorio.' },
                  min: { value: 0, message: 'Este campo debe ser mayor que 0.' },
                  validate: v => isNumber(v) || 'Este campo debe ser un número.',
                }}
              />
              <ValidatedField label="Subtotal" id="item-carrito-subtotal" name="subtotal" data-cy="subtotal" type="text" />
              <ValidatedField id="item-carrito-carrito" name="carrito" data-cy="carrito" label="Carrito" type="select" required>
                <option value="" key="0" />
                {carritos
                  ? carritos.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>Este campo es obligatorio.</FormText>
              <ValidatedField id="item-carrito-producto" name="producto" data-cy="producto" label="Producto" type="select" required>
                <option value="" key="0" />
                {productos
                  ? productos.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.nombre}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>Este campo es obligatorio.</FormText>
              <Button as={Link as any} id="cancel-save" data-cy="entityCreateCancelButton" to="/item-carrito" replace variant="info">
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

export default ItemCarritoUpdate;

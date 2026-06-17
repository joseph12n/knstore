import React, { useEffect } from 'react';
import { Button, Col, FormText, Row } from 'react-bootstrap';
import { ValidatedField, ValidatedForm, isNumber } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities as getPedidos } from 'app/entities/pedido/pedido.reducer';
import { getEntities as getProductos } from 'app/entities/producto/producto.reducer';

import { createEntity, getEntity, reset, updateEntity } from './item-pedido.reducer';

export const ItemPedidoUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const pedidos = useAppSelector(state => state.pedido.entities);
  const productos = useAppSelector(state => state.producto.entities);
  const itemPedidoEntity = useAppSelector(state => state.itemPedido.entity);
  const loading = useAppSelector(state => state.itemPedido.loading);
  const updating = useAppSelector(state => state.itemPedido.updating);
  const updateSuccess = useAppSelector(state => state.itemPedido.updateSuccess);

  const handleClose = () => {
    navigate('/item-pedido');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getPedidos({}));
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
    if (values.porcentajeIva !== undefined && typeof values.porcentajeIva !== 'number') {
      values.porcentajeIva = Number(values.porcentajeIva);
    }
    if (values.valorIva !== undefined && typeof values.valorIva !== 'number') {
      values.valorIva = Number(values.valorIva);
    }
    if (values.descuento !== undefined && typeof values.descuento !== 'number') {
      values.descuento = Number(values.descuento);
    }
    if (values.subtotal !== undefined && typeof values.subtotal !== 'number') {
      values.subtotal = Number(values.subtotal);
    }

    const entity = {
      ...itemPedidoEntity,
      ...values,
      pedido: pedidos.find(it => it.id.toString() === values.pedido?.toString()),
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
          ...itemPedidoEntity,
          pedido: itemPedidoEntity?.pedido?.id,
          producto: itemPedidoEntity?.producto?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="knstoreApp.itemPedido.home.createOrEditLabel" data-cy="ItemPedidoCreateUpdateHeading">
            Crear o editar Item Pedido
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew && <ValidatedField name="id" required readOnly id="item-pedido-id" label="ID" validate={{ required: true }} />}
              <ValidatedField
                label="Nombre Producto"
                id="item-pedido-nombreProducto"
                name="nombreProducto"
                data-cy="nombreProducto"
                type="text"
                validate={{
                  required: { value: true, message: 'Este campo es obligatorio.' },
                  maxLength: { value: 200, message: 'Este campo no puede superar más de 200 caracteres.' },
                }}
              />
              <ValidatedField
                label="Slug Producto"
                id="item-pedido-slugProducto"
                name="slugProducto"
                data-cy="slugProducto"
                type="text"
                validate={{
                  maxLength: { value: 220, message: 'Este campo no puede superar más de 220 caracteres.' },
                }}
              />
              <ValidatedField
                label="Marca Producto"
                id="item-pedido-marcaProducto"
                name="marcaProducto"
                data-cy="marcaProducto"
                type="text"
                validate={{
                  maxLength: { value: 100, message: 'Este campo no puede superar más de 100 caracteres.' },
                }}
              />
              <ValidatedField
                label="Sku Producto"
                id="item-pedido-skuProducto"
                name="skuProducto"
                data-cy="skuProducto"
                type="text"
                validate={{
                  maxLength: { value: 100, message: 'Este campo no puede superar más de 100 caracteres.' },
                }}
              />
              <ValidatedField
                label="Color Producto"
                id="item-pedido-colorProducto"
                name="colorProducto"
                data-cy="colorProducto"
                type="text"
                validate={{
                  maxLength: { value: 50, message: 'Este campo no puede superar más de 50 caracteres.' },
                }}
              />
              <ValidatedField
                label="Talla Producto"
                id="item-pedido-tallaProducto"
                name="tallaProducto"
                data-cy="tallaProducto"
                type="text"
                validate={{
                  maxLength: { value: 30, message: 'Este campo no puede superar más de 30 caracteres.' },
                }}
              />
              <ValidatedField
                label="Cantidad"
                id="item-pedido-cantidad"
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
                id="item-pedido-precioUnitario"
                name="precioUnitario"
                data-cy="precioUnitario"
                type="text"
                validate={{
                  required: { value: true, message: 'Este campo es obligatorio.' },
                  min: { value: 0, message: 'Este campo debe ser mayor que 0.' },
                  validate: v => isNumber(v) || 'Este campo debe ser un número.',
                }}
              />
              <ValidatedField
                label="Porcentaje Iva"
                id="item-pedido-porcentajeIva"
                name="porcentajeIva"
                data-cy="porcentajeIva"
                type="text"
                validate={{
                  min: { value: 0, message: 'Este campo debe ser mayor que 0.' },
                  validate: v => isNumber(v) || 'Este campo debe ser un número.',
                }}
              />
              <ValidatedField
                label="Valor Iva"
                id="item-pedido-valorIva"
                name="valorIva"
                data-cy="valorIva"
                type="text"
                validate={{
                  min: { value: 0, message: 'Este campo debe ser mayor que 0.' },
                  validate: v => isNumber(v) || 'Este campo debe ser un número.',
                }}
              />
              <ValidatedField
                label="Descuento"
                id="item-pedido-descuento"
                name="descuento"
                data-cy="descuento"
                type="text"
                validate={{
                  min: { value: 0, message: 'Este campo debe ser mayor que 0.' },
                  validate: v => isNumber(v) || 'Este campo debe ser un número.',
                }}
              />
              <ValidatedField label="Subtotal" id="item-pedido-subtotal" name="subtotal" data-cy="subtotal" type="text" />
              <ValidatedField id="item-pedido-pedido" name="pedido" data-cy="pedido" label="Pedido" type="select" required>
                <option value="" key="0" />
                {pedidos
                  ? pedidos.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>Este campo es obligatorio.</FormText>
              <ValidatedField id="item-pedido-producto" name="producto" data-cy="producto" label="Producto" type="select" required>
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
              <Button as={Link as any} id="cancel-save" data-cy="entityCreateCancelButton" to="/item-pedido" replace variant="info">
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

export default ItemPedidoUpdate;

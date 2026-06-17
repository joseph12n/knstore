import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { Link, useParams } from 'react-router';
import {} from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './item-carrito.reducer';

export const ItemCarritoDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const itemCarritoEntity = useAppSelector(state => state.itemCarrito.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="itemCarritoDetailsHeading">Item Carrito</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{itemCarritoEntity.id}</dd>
          <dt>
            <span id="cantidad">Cantidad</span>
          </dt>
          <dd>{itemCarritoEntity.cantidad}</dd>
          <dt>
            <span id="precioUnitario">Precio Unitario</span>
          </dt>
          <dd>{itemCarritoEntity.precioUnitario}</dd>
          <dt>
            <span id="subtotal">Subtotal</span>
          </dt>
          <dd>{itemCarritoEntity.subtotal}</dd>
          <dt>Carrito</dt>
          <dd>{itemCarritoEntity.carrito ? itemCarritoEntity.carrito.id : ''}</dd>
          <dt>Producto</dt>
          <dd>{itemCarritoEntity.producto ? itemCarritoEntity.producto.nombre : ''}</dd>
        </dl>
        <Button as={Link as any} to="/item-carrito" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Volver</span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/item-carrito/${itemCarritoEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Editar</span>
        </Button>
      </Col>
    </Row>
  );
};

export default ItemCarritoDetail;

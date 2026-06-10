import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { Link, useParams } from 'react-router';
import {} from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './item-pedido.reducer';

export const ItemPedidoDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const itemPedidoEntity = useAppSelector(state => state.itemPedido.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="itemPedidoDetailsHeading">Item Pedido</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{itemPedidoEntity.id}</dd>
          <dt>
            <span id="cantidad">Cantidad</span>
          </dt>
          <dd>{itemPedidoEntity.cantidad}</dd>
          <dt>
            <span id="precioUnitario">Precio Unitario</span>
          </dt>
          <dd>{itemPedidoEntity.precioUnitario}</dd>
          <dt>
            <span id="porcentajeIva">Porcentaje Iva</span>
          </dt>
          <dd>{itemPedidoEntity.porcentajeIva}</dd>
          <dt>
            <span id="valorIva">Valor Iva</span>
          </dt>
          <dd>{itemPedidoEntity.valorIva}</dd>
          <dt>
            <span id="descuento">Descuento</span>
          </dt>
          <dd>{itemPedidoEntity.descuento}</dd>
          <dt>
            <span id="subtotal">Subtotal</span>
          </dt>
          <dd>{itemPedidoEntity.subtotal}</dd>
          <dt>Pedido</dt>
          <dd>{itemPedidoEntity.pedido ? itemPedidoEntity.pedido.id : ''}</dd>
          <dt>Producto</dt>
          <dd>{itemPedidoEntity.producto ? itemPedidoEntity.producto.nombre : ''}</dd>
          <dt>Variante</dt>
          <dd>{itemPedidoEntity.variante ? itemPedidoEntity.variante.sku : ''}</dd>
        </dl>
        <Button as={Link as any} to="/item-pedido" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Volver</span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/item-pedido/${itemPedidoEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Editar</span>
        </Button>
      </Col>
    </Row>
  );
};

export default ItemPedidoDetail;

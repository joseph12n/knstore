import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { Link, useParams } from 'react-router';
import {} from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './pedido.reducer';

export const PedidoDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const pedidoEntity = useAppSelector(state => state.pedido.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="pedidoDetailsHeading">Pedido</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{pedidoEntity.id}</dd>
          <dt>
            <span id="numeroPedido">Numero Pedido</span>
          </dt>
          <dd>{pedidoEntity.numeroPedido}</dd>
          <dt>
            <span id="estado">Estado</span>
          </dt>
          <dd>{pedidoEntity.estado}</dd>
          <dt>
            <span id="subtotal">Subtotal</span>
          </dt>
          <dd>{pedidoEntity.subtotal}</dd>
          <dt>
            <span id="descuento">Descuento</span>
          </dt>
          <dd>{pedidoEntity.descuento}</dd>
          <dt>
            <span id="ivaTotal">Iva Total</span>
          </dt>
          <dd>{pedidoEntity.ivaTotal}</dd>
          <dt>
            <span id="costoEnvio">Costo Envio</span>
          </dt>
          <dd>{pedidoEntity.costoEnvio}</dd>
          <dt>
            <span id="total">Total</span>
          </dt>
          <dd>{pedidoEntity.total}</dd>
          <dt>
            <span id="notasCliente">Notas Cliente</span>
          </dt>
          <dd>{pedidoEntity.notasCliente}</dd>
          <dt>
            <span id="notasInternas">Notas Internas</span>
          </dt>
          <dd>{pedidoEntity.notasInternas}</dd>
          <dt>
            <span id="ipOrigen">Ip Origen</span>
          </dt>
          <dd>{pedidoEntity.ipOrigen}</dd>
          <dt>
            <span id="userAgent">User Agent</span>
          </dt>
          <dd>{pedidoEntity.userAgent}</dd>
          <dt>Direccion</dt>
          <dd>{pedidoEntity.direccion ? pedidoEntity.direccion.id : ''}</dd>
          <dt>Cuenta</dt>
          <dd>{pedidoEntity.cuenta ? pedidoEntity.cuenta.id : ''}</dd>
        </dl>
        <Button as={Link as any} to="/pedido" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Volver</span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/pedido/${pedidoEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Editar</span>
        </Button>
      </Col>
    </Row>
  );
};

export default PedidoDetail;

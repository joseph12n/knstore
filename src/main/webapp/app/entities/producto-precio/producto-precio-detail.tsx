import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { Link, useParams } from 'react-router';
import {} from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './producto-precio.reducer';

export const ProductoPrecioDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const productoPrecioEntity = useAppSelector(state => state.productoPrecio.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="productoPrecioDetailsHeading">Producto Precio</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{productoPrecioEntity.id}</dd>
          <dt>
            <span id="precioCompra">Precio Compra</span>
          </dt>
          <dd>{productoPrecioEntity.precioCompra}</dd>
          <dt>
            <span id="precioVenta">Precio Venta</span>
          </dt>
          <dd>{productoPrecioEntity.precioVenta}</dd>
          <dt>
            <span id="precioAdicional">Precio Adicional</span>
          </dt>
          <dd>{productoPrecioEntity.precioAdicional}</dd>
          <dt>
            <span id="ganancia">Ganancia</span>
          </dt>
          <dd>{productoPrecioEntity.ganancia}</dd>
        </dl>
        <Button as={Link as any} to="/producto-precio" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Volver</span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/producto-precio/${productoPrecioEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Editar</span>
        </Button>
      </Col>
    </Row>
  );
};

export default ProductoPrecioDetail;

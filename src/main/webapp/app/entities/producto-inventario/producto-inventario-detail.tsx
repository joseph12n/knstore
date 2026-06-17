import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { Link, useParams } from 'react-router';
import {} from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './producto-inventario.reducer';

export const ProductoInventarioDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const productoInventarioEntity = useAppSelector(state => state.productoInventario.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="productoInventarioDetailsHeading">Producto Inventario</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{productoInventarioEntity.id}</dd>
          <dt>
            <span id="stock">Stock</span>
          </dt>
          <dd>{productoInventarioEntity.stock}</dd>
          <dt>
            <span id="stockMinimo">Stock Minimo</span>
          </dt>
          <dd>{productoInventarioEntity.stockMinimo}</dd>
          <dt>
            <span id="ubicacionBodega">Ubicacion Bodega</span>
          </dt>
          <dd>{productoInventarioEntity.ubicacionBodega}</dd>
          <dt>
            <span id="garantiaMeses">Garantia Meses</span>
          </dt>
          <dd>{productoInventarioEntity.garantiaMeses}</dd>
        </dl>
        <Button as={Link as any} to="/producto-inventario" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Volver</span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/producto-inventario/${productoInventarioEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Editar</span>
        </Button>
      </Col>
    </Row>
  );
};

export default ProductoInventarioDetail;

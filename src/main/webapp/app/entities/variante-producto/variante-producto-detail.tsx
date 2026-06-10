import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { Link, useParams } from 'react-router';
import {} from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './variante-producto.reducer';

export const VarianteProductoDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const varianteProductoEntity = useAppSelector(state => state.varianteProducto.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="varianteProductoDetailsHeading">Variante Producto</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{varianteProductoEntity.id}</dd>
          <dt>
            <span id="sku">Sku</span>
          </dt>
          <dd>{varianteProductoEntity.sku}</dd>
          <dt>
            <span id="color">Color</span>
          </dt>
          <dd>{varianteProductoEntity.color}</dd>
          <dt>
            <span id="talla">Talla</span>
          </dt>
          <dd>{varianteProductoEntity.talla}</dd>
          <dt>
            <span id="codigoBarras">Codigo Barras</span>
          </dt>
          <dd>{varianteProductoEntity.codigoBarras}</dd>
          <dt>
            <span id="stock">Stock</span>
          </dt>
          <dd>{varianteProductoEntity.stock}</dd>
          <dt>
            <span id="stockMinimo">Stock Minimo</span>
          </dt>
          <dd>{varianteProductoEntity.stockMinimo}</dd>
          <dt>
            <span id="ubicacionBodega">Ubicacion Bodega</span>
          </dt>
          <dd>{varianteProductoEntity.ubicacionBodega}</dd>
          <dt>
            <span id="precioAdicional">Precio Adicional</span>
          </dt>
          <dd>{varianteProductoEntity.precioAdicional}</dd>
          <dt>
            <span id="activo">Activo</span>
          </dt>
          <dd>{varianteProductoEntity.activo ? 'true' : 'false'}</dd>
          <dt>Producto</dt>
          <dd>{varianteProductoEntity.producto ? varianteProductoEntity.producto.nombre : ''}</dd>
        </dl>
        <Button as={Link as any} to="/variante-producto" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Volver</span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/variante-producto/${varianteProductoEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Editar</span>
        </Button>
      </Col>
    </Row>
  );
};

export default VarianteProductoDetail;

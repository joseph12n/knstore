import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './producto.reducer';

export const ProductoDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const productoEntity = useAppSelector(state => state.producto.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="productoDetailsHeading">Producto</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{productoEntity.id}</dd>
          <dt>
            <span id="nombre">Nombre</span>
          </dt>
          <dd>{productoEntity.nombre}</dd>
          <dt>
            <span id="slug">Slug</span>
          </dt>
          <dd>{productoEntity.slug}</dd>
          <dt>
            <span id="referencia">Referencia</span>
          </dt>
          <dd>{productoEntity.referencia}</dd>
          <dt>
            <span id="sku">Sku</span>
          </dt>
          <dd>{productoEntity.sku}</dd>
          <dt>
            <span id="color">Color</span>
          </dt>
          <dd>{productoEntity.color}</dd>
          <dt>
            <span id="talla">Talla</span>
          </dt>
          <dd>{productoEntity.talla}</dd>
          <dt>
            <span id="codigoBarras">Codigo Barras</span>
          </dt>
          <dd>{productoEntity.codigoBarras}</dd>
          <dt>
            <span id="unidadMedida">Unidad Medida</span>
          </dt>
          <dd>{productoEntity.unidadMedida}</dd>
          <dt>
            <span id="descripcion">Descripcion</span>
          </dt>
          <dd>{productoEntity.descripcion}</dd>
          <dt>
            <span id="destacado">Destacado</span>
          </dt>
          <dd>{productoEntity.destacado ? 'true' : 'false'}</dd>
          <dt>
            <span id="activo">Activo</span>
          </dt>
          <dd>{productoEntity.activo ? 'true' : 'false'}</dd>
          <dt>Precio</dt>
          <dd>{productoEntity.precio ? productoEntity.precio.id : ''}</dd>
          <dt>Inventario</dt>
          <dd>{productoEntity.inventario ? productoEntity.inventario.id : ''}</dd>
          <dt>Categoria</dt>
          <dd>{productoEntity.categoria ? productoEntity.categoria.nombre : ''}</dd>
          <dt>Subcategoria</dt>
          <dd>{productoEntity.subcategoria ? productoEntity.subcategoria.nombre : ''}</dd>
          <dt>Marca</dt>
          <dd>{productoEntity.marca ? productoEntity.marca.id : ''}</dd>
          <dt>Categoria Iva</dt>
          <dd>{productoEntity.categoriaIva ? productoEntity.categoriaIva.id : ''}</dd>
        </dl>
        <Button as={Link as any} to="/producto" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Volver</span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/producto/${productoEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Editar</span>
        </Button>
      </Col>
    </Row>
  );
};

export default ProductoDetail;

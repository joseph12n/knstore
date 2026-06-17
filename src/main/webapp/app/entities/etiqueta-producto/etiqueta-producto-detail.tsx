import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { Link, useParams } from 'react-router';
import {} from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './etiqueta-producto.reducer';

export const EtiquetaProductoDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const etiquetaProductoEntity = useAppSelector(state => state.etiquetaProducto.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="etiquetaProductoDetailsHeading">Etiqueta Producto</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{etiquetaProductoEntity.id}</dd>
          <dt>
            <span id="etiqueta">Etiqueta</span>
          </dt>
          <dd>{etiquetaProductoEntity.etiqueta}</dd>
          <dt>Producto</dt>
          <dd>{etiquetaProductoEntity.producto ? etiquetaProductoEntity.producto.nombre : ''}</dd>
        </dl>
        <Button as={Link as any} to="/etiqueta-producto" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Volver</span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/etiqueta-producto/${etiquetaProductoEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Editar</span>
        </Button>
      </Col>
    </Row>
  );
};

export default EtiquetaProductoDetail;

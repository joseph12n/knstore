import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { Link, useParams } from 'react-router';
import {} from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './categoria-iva.reducer';

export const CategoriaIVADetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const categoriaIVAEntity = useAppSelector(state => state.categoriaIVA.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="categoriaIVADetailsHeading">Categoria IVA</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{categoriaIVAEntity.id}</dd>
          <dt>
            <span id="nombre">Nombre</span>
          </dt>
          <dd>{categoriaIVAEntity.nombre}</dd>
          <dt>
            <span id="porcentaje">Porcentaje</span>
          </dt>
          <dd>{categoriaIVAEntity.porcentaje}</dd>
          <dt>
            <span id="estado">Estado</span>
          </dt>
          <dd>{categoriaIVAEntity.estado}</dd>
        </dl>
        <Button as={Link as any} to="/categoria-iva" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Volver</span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/categoria-iva/${categoriaIVAEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Editar</span>
        </Button>
      </Col>
    </Row>
  );
};

export default CategoriaIVADetail;

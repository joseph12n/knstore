import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { Link, useParams } from 'react-router';
import {} from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './direccion.reducer';

export const DireccionDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const direccionEntity = useAppSelector(state => state.direccion.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="direccionDetailsHeading">Direccion</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{direccionEntity.id}</dd>
          <dt>
            <span id="direccion">Direccion</span>
          </dt>
          <dd>{direccionEntity.direccion}</dd>
          <dt>
            <span id="barrio">Barrio</span>
          </dt>
          <dd>{direccionEntity.barrio}</dd>
          <dt>
            <span id="localidad">Localidad</span>
          </dt>
          <dd>{direccionEntity.localidad}</dd>
          <dt>
            <span id="municipio">Municipio</span>
          </dt>
          <dd>{direccionEntity.municipio}</dd>
          <dt>
            <span id="departamento">Departamento</span>
          </dt>
          <dd>{direccionEntity.departamento}</dd>
          <dt>
            <span id="activo">Activo</span>
          </dt>
          <dd>{direccionEntity.activo ? 'true' : 'false'}</dd>
          <dt>Cuenta</dt>
          <dd>{direccionEntity.cuenta ? direccionEntity.cuenta.id : ''}</dd>
        </dl>
        <Button as={Link as any} to="/direccion" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Volver</span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/direccion/${direccionEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Editar</span>
        </Button>
      </Col>
    </Row>
  );
};

export default DireccionDetail;

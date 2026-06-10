import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { TextFormat } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './carrito.reducer';

export const CarritoDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const carritoEntity = useAppSelector(state => state.carrito.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="carritoDetailsHeading">Carrito</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{carritoEntity.id}</dd>
          <dt>
            <span id="subtotal">Subtotal</span>
          </dt>
          <dd>{carritoEntity.subtotal}</dd>
          <dt>
            <span id="fechaActualizacion">Fecha Actualizacion</span>
          </dt>
          <dd>
            {carritoEntity.fechaActualizacion ? (
              <TextFormat value={carritoEntity.fechaActualizacion} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>Cuenta</dt>
          <dd>{carritoEntity.cuenta ? carritoEntity.cuenta.id : ''}</dd>
        </dl>
        <Button as={Link as any} to="/carrito" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Volver</span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/carrito/${carritoEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Editar</span>
        </Button>
      </Col>
    </Row>
  );
};

export default CarritoDetail;

import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { TextFormat } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './pago.reducer';

export const PagoDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const pagoEntity = useAppSelector(state => state.pago.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="pagoDetailsHeading">Pago</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{pagoEntity.id}</dd>
          <dt>
            <span id="metodoPago">Metodo Pago</span>
          </dt>
          <dd>{pagoEntity.metodoPago}</dd>
          <dt>
            <span id="estado">Estado</span>
          </dt>
          <dd>{pagoEntity.estado}</dd>
          <dt>
            <span id="monto">Monto</span>
          </dt>
          <dd>{pagoEntity.monto}</dd>
          <dt>
            <span id="referenciaPasarela">Referencia Pasarela</span>
          </dt>
          <dd>{pagoEntity.referenciaPasarela}</dd>
          <dt>
            <span id="codigoAutorizacion">Codigo Autorizacion</span>
          </dt>
          <dd>{pagoEntity.codigoAutorizacion}</dd>
          <dt>
            <span id="descripcionRespuesta">Descripcion Respuesta</span>
          </dt>
          <dd>{pagoEntity.descripcionRespuesta}</dd>
          <dt>
            <span id="fechaPago">Fecha Pago</span>
          </dt>
          <dd>{pagoEntity.fechaPago ? <TextFormat value={pagoEntity.fechaPago} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="intentos">Intentos</span>
          </dt>
          <dd>{pagoEntity.intentos}</dd>
          <dt>Pedido</dt>
          <dd>{pagoEntity.pedido ? pagoEntity.pedido.id : ''}</dd>
        </dl>
        <Button as={Link as any} to="/pago" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Volver</span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/pago/${pagoEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Editar</span>
        </Button>
      </Col>
    </Row>
  );
};

export default PagoDetail;

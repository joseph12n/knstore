import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { TextFormat } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './factura.reducer';

export const FacturaDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const facturaEntity = useAppSelector(state => state.factura.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="facturaDetailsHeading">Factura</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{facturaEntity.id}</dd>
          <dt>
            <span id="prefijo">Prefijo</span>
          </dt>
          <dd>{facturaEntity.prefijo}</dd>
          <dt>
            <span id="cufe">Cufe</span>
          </dt>
          <dd>{facturaEntity.cufe}</dd>
          <dt>
            <span id="subtotal">Subtotal</span>
          </dt>
          <dd>{facturaEntity.subtotal}</dd>
          <dt>
            <span id="descuentos">Descuentos</span>
          </dt>
          <dd>{facturaEntity.descuentos}</dd>
          <dt>
            <span id="baseGravableIva">Base Gravable Iva</span>
          </dt>
          <dd>{facturaEntity.baseGravableIva}</dd>
          <dt>
            <span id="valorIva">Valor Iva</span>
          </dt>
          <dd>{facturaEntity.valorIva}</dd>
          <dt>
            <span id="total">Total</span>
          </dt>
          <dd>{facturaEntity.total}</dd>
          <dt>
            <span id="notasAdicionales">Notas Adicionales</span>
          </dt>
          <dd>{facturaEntity.notasAdicionales}</dd>
          <dt>
            <span id="codigoQr">Codigo Qr</span>
          </dt>
          <dd>{facturaEntity.codigoQr}</dd>
          <dt>
            <span id="enviada">Enviada</span>
          </dt>
          <dd>{facturaEntity.enviada ? 'true' : 'false'}</dd>
          <dt>
            <span id="fechaEmision">Fecha Emision</span>
          </dt>
          <dd>
            {facturaEntity.fechaEmision ? <TextFormat value={facturaEntity.fechaEmision} type="date" format={APP_DATE_FORMAT} /> : null}
          </dd>
          <dt>
            <span id="fechaVencimiento">Fecha Vencimiento</span>
          </dt>
          <dd>
            {facturaEntity.fechaVencimiento ? (
              <TextFormat value={facturaEntity.fechaVencimiento} type="date" format={APP_LOCAL_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="fechaEnvioEmail">Fecha Envio Email</span>
          </dt>
          <dd>
            {facturaEntity.fechaEnvioEmail ? (
              <TextFormat value={facturaEntity.fechaEnvioEmail} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>Pago</dt>
          <dd>{facturaEntity.pago ? facturaEntity.pago.id : ''}</dd>
        </dl>
        <Button as={Link as any} to="/factura" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Volver</span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/factura/${facturaEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Editar</span>
        </Button>
      </Col>
    </Row>
  );
};

export default FacturaDetail;

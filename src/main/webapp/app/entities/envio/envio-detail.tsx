import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { TextFormat } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './envio.reducer';

export const EnvioDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const envioEntity = useAppSelector(state => state.envio.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="envioDetailsHeading">Envio</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{envioEntity.id}</dd>
          <dt>
            <span id="transportadora">Transportadora</span>
          </dt>
          <dd>{envioEntity.transportadora}</dd>
          <dt>
            <span id="numeroRastreo">Numero Rastreo</span>
          </dt>
          <dd>{envioEntity.numeroRastreo}</dd>
          <dt>
            <span id="tipoServicio">Tipo Servicio</span>
          </dt>
          <dd>{envioEntity.tipoServicio}</dd>
          <dt>
            <span id="estado">Estado</span>
          </dt>
          <dd>{envioEntity.estado}</dd>
          <dt>
            <span id="costoEnvio">Costo Envio</span>
          </dt>
          <dd>{envioEntity.costoEnvio}</dd>
          <dt>
            <span id="pesoKg">Peso Kg</span>
          </dt>
          <dd>{envioEntity.pesoKg}</dd>
          <dt>
            <span id="valorDeclarado">Valor Declarado</span>
          </dt>
          <dd>{envioEntity.valorDeclarado}</dd>
          <dt>
            <span id="urlRastreo">Url Rastreo</span>
          </dt>
          <dd>{envioEntity.urlRastreo}</dd>
          <dt>
            <span id="observaciones">Observaciones</span>
          </dt>
          <dd>{envioEntity.observaciones}</dd>
          <dt>
            <span id="fechaDespacho">Fecha Despacho</span>
          </dt>
          <dd>
            {envioEntity.fechaDespacho ? <TextFormat value={envioEntity.fechaDespacho} type="date" format={APP_DATE_FORMAT} /> : null}
          </dd>
          <dt>
            <span id="fechaEntregaEstimada">Fecha Entrega Estimada</span>
          </dt>
          <dd>
            {envioEntity.fechaEntregaEstimada ? (
              <TextFormat value={envioEntity.fechaEntregaEstimada} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="fechaEntrega">Fecha Entrega</span>
          </dt>
          <dd>{envioEntity.fechaEntrega ? <TextFormat value={envioEntity.fechaEntrega} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>Pedido</dt>
          <dd>{envioEntity.pedido ? envioEntity.pedido.id : ''}</dd>
        </dl>
        <Button as={Link as any} to="/envio" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Volver</span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/envio/${envioEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Editar</span>
        </Button>
      </Col>
    </Row>
  );
};

export default EnvioDetail;

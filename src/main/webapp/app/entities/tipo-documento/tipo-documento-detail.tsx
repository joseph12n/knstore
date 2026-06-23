import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { Link, useParams } from 'react-router';
import {} from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './tipo-documento.reducer';

export const TipoDocumentoDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const tipoDocumentoEntity = useAppSelector(state => state.tipoDocumento.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="tipoDocumentoDetailsHeading">Tipo Documento</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{tipoDocumentoEntity.id}</dd>
          <dt>
            <span id="sigla">Sigla</span>
          </dt>
          <dd>{tipoDocumentoEntity.sigla}</dd>
          <dt>
            <span id="nombreTipo">Nombre Tipo</span>
          </dt>
          <dd>{tipoDocumentoEntity.nombreTipo}</dd>
          <dt>
            <span id="estado">Estado</span>
          </dt>
          <dd>{tipoDocumentoEntity.estado}</dd>
        </dl>
        <Button as={Link as any} to="/tipo-documento" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Volver</span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/tipo-documento/${tipoDocumentoEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Editar</span>
        </Button>
      </Col>
    </Row>
  );
};

export default TipoDocumentoDetail;

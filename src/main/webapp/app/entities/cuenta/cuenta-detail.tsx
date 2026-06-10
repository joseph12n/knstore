import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { TextFormat, byteSize, openFile } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './cuenta.reducer';

export const CuentaDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const cuentaEntity = useAppSelector(state => state.cuenta.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="cuentaDetailsHeading">Cuenta</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{cuentaEntity.id}</dd>
          <dt>
            <span id="numDocumento">Num Documento</span>
          </dt>
          <dd>{cuentaEntity.numDocumento}</dd>
          <dt>
            <span id="tipoPersona">Tipo Persona</span>
          </dt>
          <dd>{cuentaEntity.tipoPersona}</dd>
          <dt>
            <span id="primerNombre">Primer Nombre</span>
          </dt>
          <dd>{cuentaEntity.primerNombre}</dd>
          <dt>
            <span id="segundoNombre">Segundo Nombre</span>
          </dt>
          <dd>{cuentaEntity.segundoNombre}</dd>
          <dt>
            <span id="primerApellido">Primer Apellido</span>
          </dt>
          <dd>{cuentaEntity.primerApellido}</dd>
          <dt>
            <span id="segundoApellido">Segundo Apellido</span>
          </dt>
          <dd>{cuentaEntity.segundoApellido}</dd>
          <dt>
            <span id="celular">Celular</span>
          </dt>
          <dd>{cuentaEntity.celular}</dd>
          <dt>
            <span id="telefono">Telefono</span>
          </dt>
          <dd>{cuentaEntity.telefono}</dd>
          <dt>
            <span id="fechaNacimiento">Fecha Nacimiento</span>
          </dt>
          <dd>
            {cuentaEntity.fechaNacimiento ? (
              <TextFormat value={cuentaEntity.fechaNacimiento} type="date" format={APP_LOCAL_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="genero">Genero</span>
          </dt>
          <dd>{cuentaEntity.genero}</dd>
          <dt>
            <span id="fotoPerfil">Foto Perfil</span>
          </dt>
          <dd>
            {cuentaEntity.fotoPerfil ? (
              <div>
                {cuentaEntity.fotoPerfilContentType ? (
                  <a onClick={openFile(cuentaEntity.fotoPerfilContentType, cuentaEntity.fotoPerfil)}>
                    <img
                      src={`data:${cuentaEntity.fotoPerfilContentType};base64,${cuentaEntity.fotoPerfil}`}
                      style={{ maxHeight: '30px' }}
                    />
                  </a>
                ) : null}
                <span>
                  {cuentaEntity.fotoPerfilContentType}, {byteSize(cuentaEntity.fotoPerfil)}
                </span>
              </div>
            ) : null}
          </dd>
          <dt>
            <span id="activo">Activo</span>
          </dt>
          <dd>{cuentaEntity.activo ? 'true' : 'false'}</dd>
          <dt>User</dt>
          <dd>{cuentaEntity.user ? cuentaEntity.user.login : ''}</dd>
          <dt>Tipo Documento</dt>
          <dd>{cuentaEntity.tipoDocumento ? cuentaEntity.tipoDocumento.sigla : ''}</dd>
        </dl>
        <Button as={Link as any} to="/cuenta" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Volver</span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/cuenta/${cuentaEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Editar</span>
        </Button>
      </Col>
    </Row>
  );
};

export default CuentaDetail;

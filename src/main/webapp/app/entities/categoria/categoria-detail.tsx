import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { byteSize, openFile } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './categoria.reducer';

export const CategoriaDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const categoriaEntity = useAppSelector(state => state.categoria.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="categoriaDetailsHeading">Categoria</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{categoriaEntity.id}</dd>
          <dt>
            <span id="nombre">Nombre</span>
          </dt>
          <dd>{categoriaEntity.nombre}</dd>
          <dt>
            <span id="slug">Slug</span>
          </dt>
          <dd>{categoriaEntity.slug}</dd>
          <dt>
            <span id="descripcion">Descripcion</span>
          </dt>
          <dd>{categoriaEntity.descripcion}</dd>
          <dt>
            <span id="imagen">Imagen</span>
          </dt>
          <dd>
            {categoriaEntity.imagen ? (
              <div>
                {categoriaEntity.imagenContentType ? (
                  <a onClick={openFile(categoriaEntity.imagenContentType, categoriaEntity.imagen)}>
                    <img src={`data:${categoriaEntity.imagenContentType};base64,${categoriaEntity.imagen}`} style={{ maxHeight: '30px' }} />
                  </a>
                ) : null}
                <span>
                  {categoriaEntity.imagenContentType}, {byteSize(categoriaEntity.imagen)}
                </span>
              </div>
            ) : null}
          </dd>
          <dt>
            <span id="activo">Activo</span>
          </dt>
          <dd>{categoriaEntity.activo ? 'true' : 'false'}</dd>
        </dl>
        <Button as={Link as any} to="/categoria" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Volver</span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/categoria/${categoriaEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Editar</span>
        </Button>
      </Col>
    </Row>
  );
};

export default CategoriaDetail;

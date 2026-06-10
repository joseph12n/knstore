import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { byteSize, openFile } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './subcategoria.reducer';

export const SubcategoriaDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const subcategoriaEntity = useAppSelector(state => state.subcategoria.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="subcategoriaDetailsHeading">Subcategoria</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{subcategoriaEntity.id}</dd>
          <dt>
            <span id="nombre">Nombre</span>
          </dt>
          <dd>{subcategoriaEntity.nombre}</dd>
          <dt>
            <span id="slug">Slug</span>
          </dt>
          <dd>{subcategoriaEntity.slug}</dd>
          <dt>
            <span id="descripcion">Descripcion</span>
          </dt>
          <dd>{subcategoriaEntity.descripcion}</dd>
          <dt>
            <span id="imagen">Imagen</span>
          </dt>
          <dd>
            {subcategoriaEntity.imagen ? (
              <div>
                {subcategoriaEntity.imagenContentType ? (
                  <a onClick={openFile(subcategoriaEntity.imagenContentType, subcategoriaEntity.imagen)}>
                    <img
                      src={`data:${subcategoriaEntity.imagenContentType};base64,${subcategoriaEntity.imagen}`}
                      style={{ maxHeight: '30px' }}
                    />
                  </a>
                ) : null}
                <span>
                  {subcategoriaEntity.imagenContentType}, {byteSize(subcategoriaEntity.imagen)}
                </span>
              </div>
            ) : null}
          </dd>
          <dt>
            <span id="activo">Activo</span>
          </dt>
          <dd>{subcategoriaEntity.activo ? 'true' : 'false'}</dd>
          <dt>Categoria</dt>
          <dd>{subcategoriaEntity.categoria ? subcategoriaEntity.categoria.nombre : ''}</dd>
        </dl>
        <Button as={Link as any} to="/subcategoria" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Volver</span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/subcategoria/${subcategoriaEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Editar</span>
        </Button>
      </Col>
    </Row>
  );
};

export default SubcategoriaDetail;

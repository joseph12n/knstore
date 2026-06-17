import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { byteSize, openFile } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './producto-imagen.reducer';

export const ProductoImagenDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const productoImagenEntity = useAppSelector(state => state.productoImagen.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="productoImagenDetailsHeading">Producto Imagen</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{productoImagenEntity.id}</dd>
          <dt>
            <span id="imagen">Imagen</span>
          </dt>
          <dd>
            {productoImagenEntity.imagen ? (
              <div>
                {productoImagenEntity.imagenContentType ? (
                  <a onClick={openFile(productoImagenEntity.imagenContentType, productoImagenEntity.imagen)}>
                    <img
                      src={`data:${productoImagenEntity.imagenContentType};base64,${productoImagenEntity.imagen}`}
                      style={{ maxHeight: '30px' }}
                    />
                  </a>
                ) : null}
                <span>
                  {productoImagenEntity.imagenContentType}, {byteSize(productoImagenEntity.imagen)}
                </span>
              </div>
            ) : null}
          </dd>
          <dt>
            <span id="imagenAlt">Imagen Alt</span>
          </dt>
          <dd>{productoImagenEntity.imagenAlt}</dd>
          <dt>
            <span id="esPrincipal">Es Principal</span>
          </dt>
          <dd>{productoImagenEntity.esPrincipal ? 'true' : 'false'}</dd>
          <dt>Producto</dt>
          <dd>{productoImagenEntity.producto ? productoImagenEntity.producto.id : ''}</dd>
        </dl>
        <Button as={Link as any} to="/producto-imagen" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Volver</span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/producto-imagen/${productoImagenEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Editar</span>
        </Button>
      </Col>
    </Row>
  );
};

export default ProductoImagenDetail;

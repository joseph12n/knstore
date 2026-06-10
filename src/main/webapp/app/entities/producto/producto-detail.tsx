import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { byteSize, openFile } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './producto.reducer';

export const ProductoDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const productoEntity = useAppSelector(state => state.producto.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="productoDetailsHeading">Producto</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{productoEntity.id}</dd>
          <dt>
            <span id="nombre">Nombre</span>
          </dt>
          <dd>{productoEntity.nombre}</dd>
          <dt>
            <span id="slug">Slug</span>
          </dt>
          <dd>{productoEntity.slug}</dd>
          <dt>
            <span id="descripcion">Descripcion</span>
          </dt>
          <dd>{productoEntity.descripcion}</dd>
          <dt>
            <span id="imagen">Imagen</span>
          </dt>
          <dd>
            {productoEntity.imagen ? (
              <div>
                {productoEntity.imagenContentType ? (
                  <a onClick={openFile(productoEntity.imagenContentType, productoEntity.imagen)}>
                    <img src={`data:${productoEntity.imagenContentType};base64,${productoEntity.imagen}`} style={{ maxHeight: '30px' }} />
                  </a>
                ) : null}
                <span>
                  {productoEntity.imagenContentType}, {byteSize(productoEntity.imagen)}
                </span>
              </div>
            ) : null}
          </dd>
          <dt>
            <span id="imagenAlt">Imagen Alt</span>
          </dt>
          <dd>{productoEntity.imagenAlt}</dd>
          <dt>
            <span id="marca">Marca</span>
          </dt>
          <dd>{productoEntity.marca}</dd>
          <dt>
            <span id="referencia">Referencia</span>
          </dt>
          <dd>{productoEntity.referencia}</dd>
          <dt>
            <span id="codigoBarras">Codigo Barras</span>
          </dt>
          <dd>{productoEntity.codigoBarras}</dd>
          <dt>
            <span id="unidadMedida">Unidad Medida</span>
          </dt>
          <dd>{productoEntity.unidadMedida}</dd>
          <dt>
            <span id="pesoKg">Peso Kg</span>
          </dt>
          <dd>{productoEntity.pesoKg}</dd>
          <dt>
            <span id="largoCm">Largo Cm</span>
          </dt>
          <dd>{productoEntity.largoCm}</dd>
          <dt>
            <span id="anchoCm">Ancho Cm</span>
          </dt>
          <dd>{productoEntity.anchoCm}</dd>
          <dt>
            <span id="altoCm">Alto Cm</span>
          </dt>
          <dd>{productoEntity.altoCm}</dd>
          <dt>
            <span id="categoriaIva">Categoria Iva</span>
          </dt>
          <dd>{productoEntity.categoriaIva}</dd>
          <dt>
            <span id="precioCompra">Precio Compra</span>
          </dt>
          <dd>{productoEntity.precioCompra}</dd>
          <dt>
            <span id="precioVenta">Precio Venta</span>
          </dt>
          <dd>{productoEntity.precioVenta}</dd>
          <dt>
            <span id="ganancia">Ganancia</span>
          </dt>
          <dd>{productoEntity.ganancia}</dd>
          <dt>
            <span id="margen">Margen</span>
          </dt>
          <dd>{productoEntity.margen}</dd>
          <dt>
            <span id="garantiaMeses">Garantia Meses</span>
          </dt>
          <dd>{productoEntity.garantiaMeses}</dd>
          <dt>
            <span id="destacado">Destacado</span>
          </dt>
          <dd>{productoEntity.destacado ? 'true' : 'false'}</dd>
          <dt>
            <span id="activo">Activo</span>
          </dt>
          <dd>{productoEntity.activo ? 'true' : 'false'}</dd>
          <dt>Subcategoria</dt>
          <dd>{productoEntity.subcategoria ? productoEntity.subcategoria.nombre : ''}</dd>
        </dl>
        <Button as={Link as any} to="/producto" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Volver</span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/producto/${productoEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Editar</span>
        </Button>
      </Col>
    </Row>
  );
};

export default ProductoDetail;

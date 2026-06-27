import React, { useEffect } from 'react';
import { Button, Col, FormText, Row } from 'react-bootstrap';
import { ValidatedField, ValidatedForm } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities as getCategorias } from 'app/entities/categoria/categoria.reducer';
import { getEntities as getCategoriaIVAS } from 'app/entities/categoria-iva/categoria-iva.reducer';
import { getEntities as getMarcas } from 'app/entities/marca/marca.reducer';
import { getEntities as getProductoInventarios } from 'app/entities/producto-inventario/producto-inventario.reducer';
import { getEntities as getProductoPrecios } from 'app/entities/producto-precio/producto-precio.reducer';
import { getEntities as getSubcategorias } from 'app/entities/subcategoria/subcategoria.reducer';

import { createEntity, getEntity, reset, updateEntity } from './producto.reducer';

export const ProductoUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const productoPrecios = useAppSelector(state => state.productoPrecio.entities);
  const productoInventarios = useAppSelector(state => state.productoInventario.entities);
  const categorias = useAppSelector(state => state.categoria.entities);
  const subcategorias = useAppSelector(state => state.subcategoria.entities);
  const marcas = useAppSelector(state => state.marca.entities);
  const categoriaIVAS = useAppSelector(state => state.categoriaIVA.entities);
  const productoEntity = useAppSelector(state => state.producto.entity);
  const loading = useAppSelector(state => state.producto.loading);
  const updating = useAppSelector(state => state.producto.updating);
  const updateSuccess = useAppSelector(state => state.producto.updateSuccess);

  const handleClose = () => {
    navigate(`/producto${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getProductoPrecios({}));
    dispatch(getProductoInventarios({}));
    dispatch(getCategorias({}));
    dispatch(getSubcategorias({}));
    dispatch(getMarcas({}));
    dispatch(getCategoriaIVAS({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    const entity = {
      ...productoEntity,
      ...values,
      precio: productoPrecios.find(it => it.id.toString() === values.precio?.toString()),
      inventario: productoInventarios.find(it => it.id.toString() === values.inventario?.toString()),
      categoria: categorias.find(it => it.id.toString() === values.categoria?.toString()),
      subcategoria: subcategorias.find(it => it.id.toString() === values.subcategoria?.toString()),
      marca: marcas.find(it => it.id.toString() === values.marca?.toString()),
      categoriaIva: categoriaIVAS.find(it => it.id.toString() === values.categoriaIva?.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {}
      : {
          ...productoEntity,
          precio: productoEntity?.precio?.id,
          inventario: productoEntity?.inventario?.id,
          categoria: productoEntity?.categoria?.id,
          subcategoria: productoEntity?.subcategoria?.id,
          marca: productoEntity?.marca?.id,
          categoriaIva: productoEntity?.categoriaIva?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="knstoreApp.producto.home.createOrEditLabel" data-cy="ProductoCreateUpdateHeading">
            Crear o editar Producto
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew && <ValidatedField name="id" required readOnly id="producto-id" label="ID" validate={{ required: true }} />}
              <ValidatedField
                label="Nombre"
                id="producto-nombre"
                name="nombre"
                data-cy="nombre"
                type="text"
                validate={{
                  required: { value: true, message: 'Este campo es obligatorio.' },
                  maxLength: { value: 200, message: 'Este campo no puede superar más de 200 caracteres.' },
                }}
              />
              <ValidatedField
                label="Slug"
                id="producto-slug"
                name="slug"
                data-cy="slug"
                type="text"
                validate={{
                  required: { value: true, message: 'Este campo es obligatorio.' },
                  maxLength: { value: 220, message: 'Este campo no puede superar más de 220 caracteres.' },
                }}
              />
              <ValidatedField
                label="Referencia"
                id="producto-referencia"
                name="referencia"
                data-cy="referencia"
                type="text"
                validate={{
                  maxLength: { value: 60, message: 'Este campo no puede superar más de 60 caracteres.' },
                }}
              />
              <ValidatedField
                label="Sku"
                id="producto-sku"
                name="sku"
                data-cy="sku"
                type="text"
                validate={{
                  required: { value: true, message: 'Este campo es obligatorio.' },
                  maxLength: { value: 100, message: 'Este campo no puede superar más de 100 caracteres.' },
                }}
              />
              <ValidatedField
                label="Color"
                id="producto-color"
                name="color"
                data-cy="color"
                type="text"
                validate={{
                  maxLength: { value: 50, message: 'Este campo no puede superar más de 50 caracteres.' },
                }}
              />
              <ValidatedField
                label="Talla"
                id="producto-talla"
                name="talla"
                data-cy="talla"
                type="text"
                validate={{
                  maxLength: { value: 30, message: 'Este campo no puede superar más de 30 caracteres.' },
                }}
              />
              <ValidatedField
                label="Codigo Barras"
                id="producto-codigoBarras"
                name="codigoBarras"
                data-cy="codigoBarras"
                type="text"
                validate={{
                  maxLength: { value: 50, message: 'Este campo no puede superar más de 50 caracteres.' },
                }}
              />
              <ValidatedField
                label="Unidad Medida"
                id="producto-unidadMedida"
                name="unidadMedida"
                data-cy="unidadMedida"
                type="text"
                validate={{
                  maxLength: { value: 20, message: 'Este campo no puede superar más de 20 caracteres.' },
                }}
              />
              <ValidatedField label="Descripcion" id="producto-descripcion" name="descripcion" data-cy="descripcion" type="textarea" />
              <ValidatedField label="Destacado" id="producto-destacado" name="destacado" data-cy="destacado" check type="checkbox" />
              <ValidatedField label="Activo" id="producto-activo" name="activo" data-cy="activo" check type="checkbox" />
              <ValidatedField id="producto-precio" name="precio" data-cy="precio" label="Precio" type="select">
                <option value="" key="0" />
                {productoPrecios
                  ? productoPrecios.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.precioVenta}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField id="producto-inventario" name="inventario" data-cy="inventario" label="Inventario" type="select">
                <option value="" key="0" />
                {productoInventarios
                  ? productoInventarios.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.stock}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField id="producto-categoria" name="categoria" data-cy="categoria" label="Categoria" type="select" required>
                <option value="" key="0" />
                {categorias
                  ? categorias.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.nombre}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>Este campo es obligatorio.</FormText>
              <ValidatedField
                id="producto-subcategoria"
                name="subcategoria"
                data-cy="subcategoria"
                label="Subcategoria"
                type="select"
                required
              >
                <option value="" key="0" />
                {subcategorias
                  ? subcategorias.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.nombre}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>Este campo es obligatorio.</FormText>
              <ValidatedField id="producto-marca" name="marca" data-cy="marca" label="Marca" type="select">
                <option value="" key="0" />
                {marcas
                  ? marcas.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.nombre}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField id="producto-categoriaIva" name="categoriaIva" data-cy="categoriaIva" label="Categoria Iva" type="select">
                <option value="" key="0" />
                {categoriaIVAS
                  ? categoriaIVAS.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.nombre}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button as={Link as any} id="cancel-save" data-cy="entityCreateCancelButton" to="/producto" replace variant="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">Volver</span>
              </Button>
              &nbsp;
              <Button variant="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp; Guardar
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default ProductoUpdate;

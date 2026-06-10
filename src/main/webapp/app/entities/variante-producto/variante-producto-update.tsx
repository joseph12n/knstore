import React, { useEffect } from 'react';
import { Button, Col, FormText, Row } from 'react-bootstrap';
import { ValidatedField, ValidatedForm, isNumber } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities as getProductos } from 'app/entities/producto/producto.reducer';

import { createEntity, getEntity, reset, updateEntity } from './variante-producto.reducer';

export const VarianteProductoUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const productos = useAppSelector(state => state.producto.entities);
  const varianteProductoEntity = useAppSelector(state => state.varianteProducto.entity);
  const loading = useAppSelector(state => state.varianteProducto.loading);
  const updating = useAppSelector(state => state.varianteProducto.updating);
  const updateSuccess = useAppSelector(state => state.varianteProducto.updateSuccess);

  const handleClose = () => {
    navigate(`/variante-producto${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getProductos({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    if (values.stock !== undefined && typeof values.stock !== 'number') {
      values.stock = Number(values.stock);
    }
    if (values.stockMinimo !== undefined && typeof values.stockMinimo !== 'number') {
      values.stockMinimo = Number(values.stockMinimo);
    }
    if (values.precioAdicional !== undefined && typeof values.precioAdicional !== 'number') {
      values.precioAdicional = Number(values.precioAdicional);
    }

    const entity = {
      ...varianteProductoEntity,
      ...values,
      producto: productos.find(it => it.id.toString() === values.producto?.toString()),
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
          ...varianteProductoEntity,
          producto: varianteProductoEntity?.producto?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="knstoreApp.varianteProducto.home.createOrEditLabel" data-cy="VarianteProductoCreateUpdateHeading">
            Crear o editar Variante Producto
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew && <ValidatedField name="id" required readOnly id="variante-producto-id" label="ID" validate={{ required: true }} />}
              <ValidatedField
                label="Sku"
                id="variante-producto-sku"
                name="sku"
                data-cy="sku"
                type="text"
                validate={{
                  maxLength: { value: 100, message: 'Este campo no puede superar más de 100 caracteres.' },
                }}
              />
              <ValidatedField
                label="Color"
                id="variante-producto-color"
                name="color"
                data-cy="color"
                type="text"
                validate={{
                  maxLength: { value: 50, message: 'Este campo no puede superar más de 50 caracteres.' },
                }}
              />
              <ValidatedField
                label="Talla"
                id="variante-producto-talla"
                name="talla"
                data-cy="talla"
                type="text"
                validate={{
                  maxLength: { value: 30, message: 'Este campo no puede superar más de 30 caracteres.' },
                }}
              />
              <ValidatedField
                label="Codigo Barras"
                id="variante-producto-codigoBarras"
                name="codigoBarras"
                data-cy="codigoBarras"
                type="text"
                validate={{
                  maxLength: { value: 50, message: 'Este campo no puede superar más de 50 caracteres.' },
                }}
              />
              <ValidatedField
                label="Stock"
                id="variante-producto-stock"
                name="stock"
                data-cy="stock"
                type="text"
                validate={{
                  required: { value: true, message: 'Este campo es obligatorio.' },
                  min: { value: 0, message: 'Este campo debe ser mayor que 0.' },
                  validate: v => isNumber(v) || 'Este campo debe ser un número.',
                }}
              />
              <ValidatedField
                label="Stock Minimo"
                id="variante-producto-stockMinimo"
                name="stockMinimo"
                data-cy="stockMinimo"
                type="text"
                validate={{
                  min: { value: 0, message: 'Este campo debe ser mayor que 0.' },
                  validate: v => isNumber(v) || 'Este campo debe ser un número.',
                }}
              />
              <ValidatedField
                label="Ubicacion Bodega"
                id="variante-producto-ubicacionBodega"
                name="ubicacionBodega"
                data-cy="ubicacionBodega"
                type="text"
                validate={{
                  maxLength: { value: 80, message: 'Este campo no puede superar más de 80 caracteres.' },
                }}
              />
              <ValidatedField
                label="Precio Adicional"
                id="variante-producto-precioAdicional"
                name="precioAdicional"
                data-cy="precioAdicional"
                type="text"
                validate={{
                  min: { value: 0, message: 'Este campo debe ser mayor que 0.' },
                  validate: v => isNumber(v) || 'Este campo debe ser un número.',
                }}
              />
              <ValidatedField label="Activo" id="variante-producto-activo" name="activo" data-cy="activo" check type="checkbox" />
              <ValidatedField id="variante-producto-producto" name="producto" data-cy="producto" label="Producto" type="select" required>
                <option value="" key="0" />
                {productos
                  ? productos.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.nombre}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>Este campo es obligatorio.</FormText>
              <Button as={Link as any} id="cancel-save" data-cy="entityCreateCancelButton" to="/variante-producto" replace variant="info">
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

export default VarianteProductoUpdate;

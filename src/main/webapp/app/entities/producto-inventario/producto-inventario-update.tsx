import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { ValidatedField, ValidatedForm, isNumber } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { UbicacionBodega } from 'app/shared/model/enumerations/ubicacion-bodega.model';

import { createEntity, getEntity, reset, updateEntity } from './producto-inventario.reducer';

export const ProductoInventarioUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const productoInventarioEntity = useAppSelector(state => state.productoInventario.entity);
  const loading = useAppSelector(state => state.productoInventario.loading);
  const updating = useAppSelector(state => state.productoInventario.updating);
  const updateSuccess = useAppSelector(state => state.productoInventario.updateSuccess);
  const ubicacionBodegaValues = Object.keys(UbicacionBodega);

  const handleClose = () => {
    navigate('/producto-inventario');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }
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
    if (values.garantiaMeses !== undefined && typeof values.garantiaMeses !== 'number') {
      values.garantiaMeses = Number(values.garantiaMeses);
    }

    const entity = {
      ...productoInventarioEntity,
      ...values,
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
          ubicacionBodega: 'BODEGA_PRINCIPAL',
          ...productoInventarioEntity,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="knstoreApp.productoInventario.home.createOrEditLabel" data-cy="ProductoInventarioCreateUpdateHeading">
            Crear o editar Producto Inventario
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew && (
                <ValidatedField name="id" required readOnly id="producto-inventario-id" label="ID" validate={{ required: true }} />
              )}
              <ValidatedField
                label="Stock"
                id="producto-inventario-stock"
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
                id="producto-inventario-stockMinimo"
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
                id="producto-inventario-ubicacionBodega"
                name="ubicacionBodega"
                data-cy="ubicacionBodega"
                type="select"
              >
                {ubicacionBodegaValues.map(ubicacionBodega => (
                  <option value={ubicacionBodega} key={ubicacionBodega}>
                    {ubicacionBodega}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label="Garantia Meses"
                id="producto-inventario-garantiaMeses"
                name="garantiaMeses"
                data-cy="garantiaMeses"
                type="text"
                validate={{
                  min: { value: 0, message: 'Este campo debe ser mayor que 0.' },
                  validate: v => isNumber(v) || 'Este campo debe ser un número.',
                }}
              />
              <Button as={Link as any} id="cancel-save" data-cy="entityCreateCancelButton" to="/producto-inventario" replace variant="info">
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

export default ProductoInventarioUpdate;

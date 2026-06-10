import React, { useEffect } from 'react';
import { Button, Col, FormText, Row } from 'react-bootstrap';
import { ValidatedBlobField, ValidatedField, ValidatedForm, isNumber } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities as getSubcategorias } from 'app/entities/subcategoria/subcategoria.reducer';
import { CategoriaIVA } from 'app/shared/model/enumerations/categoria-iva.model';

import { createEntity, getEntity, reset, updateEntity } from './producto.reducer';

export const ProductoUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const subcategorias = useAppSelector(state => state.subcategoria.entities);
  const productoEntity = useAppSelector(state => state.producto.entity);
  const loading = useAppSelector(state => state.producto.loading);
  const updating = useAppSelector(state => state.producto.updating);
  const updateSuccess = useAppSelector(state => state.producto.updateSuccess);
  const categoriaIVAValues = Object.keys(CategoriaIVA);

  const handleClose = () => {
    navigate(`/producto${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getSubcategorias({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    if (values.pesoKg !== undefined && typeof values.pesoKg !== 'number') {
      values.pesoKg = Number(values.pesoKg);
    }
    if (values.largoCm !== undefined && typeof values.largoCm !== 'number') {
      values.largoCm = Number(values.largoCm);
    }
    if (values.anchoCm !== undefined && typeof values.anchoCm !== 'number') {
      values.anchoCm = Number(values.anchoCm);
    }
    if (values.altoCm !== undefined && typeof values.altoCm !== 'number') {
      values.altoCm = Number(values.altoCm);
    }
    if (values.precioCompra !== undefined && typeof values.precioCompra !== 'number') {
      values.precioCompra = Number(values.precioCompra);
    }
    if (values.precioVenta !== undefined && typeof values.precioVenta !== 'number') {
      values.precioVenta = Number(values.precioVenta);
    }
    if (values.ganancia !== undefined && typeof values.ganancia !== 'number') {
      values.ganancia = Number(values.ganancia);
    }
    if (values.margen !== undefined && typeof values.margen !== 'number') {
      values.margen = Number(values.margen);
    }
    if (values.garantiaMeses !== undefined && typeof values.garantiaMeses !== 'number') {
      values.garantiaMeses = Number(values.garantiaMeses);
    }

    const entity = {
      ...productoEntity,
      ...values,
      subcategoria: subcategorias.find(it => it.id.toString() === values.subcategoria?.toString()),
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
          categoriaIva: 'EXCLUIDO',
          ...productoEntity,
          subcategoria: productoEntity?.subcategoria?.id,
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
              <ValidatedField label="Descripcion" id="producto-descripcion" name="descripcion" data-cy="descripcion" type="textarea" />
              <ValidatedBlobField label="Imagen" id="producto-imagen" name="imagen" data-cy="imagen" isImage accept="image/*" />
              <ValidatedField
                label="Imagen Alt"
                id="producto-imagenAlt"
                name="imagenAlt"
                data-cy="imagenAlt"
                type="text"
                validate={{
                  maxLength: { value: 200, message: 'Este campo no puede superar más de 200 caracteres.' },
                }}
              />
              <ValidatedField
                label="Marca"
                id="producto-marca"
                name="marca"
                data-cy="marca"
                type="text"
                validate={{
                  maxLength: { value: 100, message: 'Este campo no puede superar más de 100 caracteres.' },
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
              <ValidatedField
                label="Peso Kg"
                id="producto-pesoKg"
                name="pesoKg"
                data-cy="pesoKg"
                type="text"
                validate={{
                  min: { value: 0, message: 'Este campo debe ser mayor que 0.' },
                  validate: v => isNumber(v) || 'Este campo debe ser un número.',
                }}
              />
              <ValidatedField
                label="Largo Cm"
                id="producto-largoCm"
                name="largoCm"
                data-cy="largoCm"
                type="text"
                validate={{
                  min: { value: 0, message: 'Este campo debe ser mayor que 0.' },
                  validate: v => isNumber(v) || 'Este campo debe ser un número.',
                }}
              />
              <ValidatedField
                label="Ancho Cm"
                id="producto-anchoCm"
                name="anchoCm"
                data-cy="anchoCm"
                type="text"
                validate={{
                  min: { value: 0, message: 'Este campo debe ser mayor que 0.' },
                  validate: v => isNumber(v) || 'Este campo debe ser un número.',
                }}
              />
              <ValidatedField
                label="Alto Cm"
                id="producto-altoCm"
                name="altoCm"
                data-cy="altoCm"
                type="text"
                validate={{
                  min: { value: 0, message: 'Este campo debe ser mayor que 0.' },
                  validate: v => isNumber(v) || 'Este campo debe ser un número.',
                }}
              />
              <ValidatedField label="Categoria Iva" id="producto-categoriaIva" name="categoriaIva" data-cy="categoriaIva" type="select">
                {categoriaIVAValues.map(categoriaIVA => (
                  <option value={categoriaIVA} key={categoriaIVA}>
                    {categoriaIVA}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label="Precio Compra"
                id="producto-precioCompra"
                name="precioCompra"
                data-cy="precioCompra"
                type="text"
                validate={{
                  required: { value: true, message: 'Este campo es obligatorio.' },
                  min: { value: 0, message: 'Este campo debe ser mayor que 0.' },
                  validate: v => isNumber(v) || 'Este campo debe ser un número.',
                }}
              />
              <ValidatedField
                label="Precio Venta"
                id="producto-precioVenta"
                name="precioVenta"
                data-cy="precioVenta"
                type="text"
                validate={{
                  required: { value: true, message: 'Este campo es obligatorio.' },
                  min: { value: 0, message: 'Este campo debe ser mayor que 0.' },
                  validate: v => isNumber(v) || 'Este campo debe ser un número.',
                }}
              />
              <ValidatedField label="Ganancia" id="producto-ganancia" name="ganancia" data-cy="ganancia" type="text" />
              <ValidatedField label="Margen" id="producto-margen" name="margen" data-cy="margen" type="text" />
              <ValidatedField
                label="Garantia Meses"
                id="producto-garantiaMeses"
                name="garantiaMeses"
                data-cy="garantiaMeses"
                type="text"
                validate={{
                  min: { value: 0, message: 'Este campo debe ser mayor que 0.' },
                  validate: v => isNumber(v) || 'Este campo debe ser un número.',
                }}
              />
              <ValidatedField label="Destacado" id="producto-destacado" name="destacado" data-cy="destacado" check type="checkbox" />
              <ValidatedField label="Activo" id="producto-activo" name="activo" data-cy="activo" check type="checkbox" />
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

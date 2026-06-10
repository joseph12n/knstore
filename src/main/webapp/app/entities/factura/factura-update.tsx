import React, { useEffect } from 'react';
import { Button, Col, FormText, Row } from 'react-bootstrap';
import { ValidatedField, ValidatedForm, isNumber } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities as getPedidos } from 'app/entities/pedido/pedido.reducer';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';

import { createEntity, getEntity, reset, updateEntity } from './factura.reducer';

export const FacturaUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const pedidos = useAppSelector(state => state.pedido.entities);
  const facturaEntity = useAppSelector(state => state.factura.entity);
  const loading = useAppSelector(state => state.factura.loading);
  const updating = useAppSelector(state => state.factura.updating);
  const updateSuccess = useAppSelector(state => state.factura.updateSuccess);

  const handleClose = () => {
    navigate(`/factura${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getPedidos({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    if (values.consecutivo !== undefined && typeof values.consecutivo !== 'number') {
      values.consecutivo = Number(values.consecutivo);
    }
    if (values.subtotal !== undefined && typeof values.subtotal !== 'number') {
      values.subtotal = Number(values.subtotal);
    }
    if (values.descuentos !== undefined && typeof values.descuentos !== 'number') {
      values.descuentos = Number(values.descuentos);
    }
    if (values.baseGravableIva !== undefined && typeof values.baseGravableIva !== 'number') {
      values.baseGravableIva = Number(values.baseGravableIva);
    }
    if (values.valorIva !== undefined && typeof values.valorIva !== 'number') {
      values.valorIva = Number(values.valorIva);
    }
    if (values.retefuente !== undefined && typeof values.retefuente !== 'number') {
      values.retefuente = Number(values.retefuente);
    }
    if (values.reteIva !== undefined && typeof values.reteIva !== 'number') {
      values.reteIva = Number(values.reteIva);
    }
    if (values.reteIca !== undefined && typeof values.reteIca !== 'number') {
      values.reteIca = Number(values.reteIca);
    }
    if (values.total !== undefined && typeof values.total !== 'number') {
      values.total = Number(values.total);
    }
    values.fechaEmision = convertDateTimeToServer(values.fechaEmision);
    values.fechaEnvioEmail = convertDateTimeToServer(values.fechaEnvioEmail);

    const entity = {
      ...facturaEntity,
      ...values,
      pedido: pedidos.find(it => it.id.toString() === values.pedido?.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {
          fechaEmision: displayDefaultDateTime(),
          fechaEnvioEmail: displayDefaultDateTime(),
        }
      : {
          ...facturaEntity,
          fechaEmision: convertDateTimeFromServer(facturaEntity.fechaEmision),
          fechaEnvioEmail: convertDateTimeFromServer(facturaEntity.fechaEnvioEmail),
          pedido: facturaEntity?.pedido?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="knstoreApp.factura.home.createOrEditLabel" data-cy="FacturaCreateUpdateHeading">
            Crear o editar Factura
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew && <ValidatedField name="id" required readOnly id="factura-id" label="ID" validate={{ required: true }} />}
              <ValidatedField
                label="Referencia"
                id="factura-referencia"
                name="referencia"
                data-cy="referencia"
                type="text"
                validate={{
                  required: { value: true, message: 'Este campo es obligatorio.' },
                  maxLength: { value: 50, message: 'Este campo no puede superar más de 50 caracteres.' },
                }}
              />
              <ValidatedField
                label="Cufe"
                id="factura-cufe"
                name="cufe"
                data-cy="cufe"
                type="text"
                validate={{
                  maxLength: { value: 96, message: 'Este campo no puede superar más de 96 caracteres.' },
                }}
              />
              <ValidatedField
                label="Resolucion Dian"
                id="factura-resolucionDian"
                name="resolucionDian"
                data-cy="resolucionDian"
                type="text"
                validate={{
                  maxLength: { value: 50, message: 'Este campo no puede superar más de 50 caracteres.' },
                }}
              />
              <ValidatedField
                label="Fecha Vigencia Resolucion"
                id="factura-fechaVigenciaResolucion"
                name="fechaVigenciaResolucion"
                data-cy="fechaVigenciaResolucion"
                type="date"
              />
              <ValidatedField
                label="Prefijo"
                id="factura-prefijo"
                name="prefijo"
                data-cy="prefijo"
                type="text"
                validate={{
                  maxLength: { value: 10, message: 'Este campo no puede superar más de 10 caracteres.' },
                }}
              />
              <ValidatedField label="Consecutivo" id="factura-consecutivo" name="consecutivo" data-cy="consecutivo" type="text" />
              <ValidatedField
                label="Subtotal"
                id="factura-subtotal"
                name="subtotal"
                data-cy="subtotal"
                type="text"
                validate={{
                  required: { value: true, message: 'Este campo es obligatorio.' },
                  min: { value: 0, message: 'Este campo debe ser mayor que 0.' },
                  validate: v => isNumber(v) || 'Este campo debe ser un número.',
                }}
              />
              <ValidatedField
                label="Descuentos"
                id="factura-descuentos"
                name="descuentos"
                data-cy="descuentos"
                type="text"
                validate={{
                  min: { value: 0, message: 'Este campo debe ser mayor que 0.' },
                  validate: v => isNumber(v) || 'Este campo debe ser un número.',
                }}
              />
              <ValidatedField
                label="Base Gravable Iva"
                id="factura-baseGravableIva"
                name="baseGravableIva"
                data-cy="baseGravableIva"
                type="text"
                validate={{
                  min: { value: 0, message: 'Este campo debe ser mayor que 0.' },
                  validate: v => isNumber(v) || 'Este campo debe ser un número.',
                }}
              />
              <ValidatedField
                label="Valor Iva"
                id="factura-valorIva"
                name="valorIva"
                data-cy="valorIva"
                type="text"
                validate={{
                  min: { value: 0, message: 'Este campo debe ser mayor que 0.' },
                  validate: v => isNumber(v) || 'Este campo debe ser un número.',
                }}
              />
              <ValidatedField
                label="Retefuente"
                id="factura-retefuente"
                name="retefuente"
                data-cy="retefuente"
                type="text"
                validate={{
                  min: { value: 0, message: 'Este campo debe ser mayor que 0.' },
                  validate: v => isNumber(v) || 'Este campo debe ser un número.',
                }}
              />
              <ValidatedField
                label="Rete Iva"
                id="factura-reteIva"
                name="reteIva"
                data-cy="reteIva"
                type="text"
                validate={{
                  min: { value: 0, message: 'Este campo debe ser mayor que 0.' },
                  validate: v => isNumber(v) || 'Este campo debe ser un número.',
                }}
              />
              <ValidatedField
                label="Rete Ica"
                id="factura-reteIca"
                name="reteIca"
                data-cy="reteIca"
                type="text"
                validate={{
                  min: { value: 0, message: 'Este campo debe ser mayor que 0.' },
                  validate: v => isNumber(v) || 'Este campo debe ser un número.',
                }}
              />
              <ValidatedField
                label="Total"
                id="factura-total"
                name="total"
                data-cy="total"
                type="text"
                validate={{
                  required: { value: true, message: 'Este campo es obligatorio.' },
                  min: { value: 0, message: 'Este campo debe ser mayor que 0.' },
                  validate: v => isNumber(v) || 'Este campo debe ser un número.',
                }}
              />
              <ValidatedField
                label="Fecha Emision"
                id="factura-fechaEmision"
                name="fechaEmision"
                data-cy="fechaEmision"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label="Fecha Vencimiento"
                id="factura-fechaVencimiento"
                name="fechaVencimiento"
                data-cy="fechaVencimiento"
                type="date"
              />
              <ValidatedField
                label="Notas Adicionales"
                id="factura-notasAdicionales"
                name="notasAdicionales"
                data-cy="notasAdicionales"
                type="text"
                validate={{
                  maxLength: { value: 500, message: 'Este campo no puede superar más de 500 caracteres.' },
                }}
              />
              <ValidatedField label="Codigo Qr" id="factura-codigoQr" name="codigoQr" data-cy="codigoQr" type="textarea" />
              <ValidatedField label="Enviada" id="factura-enviada" name="enviada" data-cy="enviada" check type="checkbox" />
              <ValidatedField
                label="Fecha Envio Email"
                id="factura-fechaEnvioEmail"
                name="fechaEnvioEmail"
                data-cy="fechaEnvioEmail"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField id="factura-pedido" name="pedido" data-cy="pedido" label="Pedido" type="select" required>
                <option value="" key="0" />
                {pedidos
                  ? pedidos.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>Este campo es obligatorio.</FormText>
              <Button as={Link as any} id="cancel-save" data-cy="entityCreateCancelButton" to="/factura" replace variant="info">
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

export default FacturaUpdate;

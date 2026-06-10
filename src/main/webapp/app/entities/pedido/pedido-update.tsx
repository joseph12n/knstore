import React, { useEffect } from 'react';
import { Button, Col, FormText, Row } from 'react-bootstrap';
import { ValidatedField, ValidatedForm, isNumber } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities as getCuentas } from 'app/entities/cuenta/cuenta.reducer';
import { getEntities as getDireccions } from 'app/entities/direccion/direccion.reducer';
import { getEntities as getEnvios } from 'app/entities/envio/envio.reducer';
import { EstadoPedido } from 'app/shared/model/enumerations/estado-pedido.model';

import { createEntity, getEntity, reset, updateEntity } from './pedido.reducer';

export const PedidoUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const direccions = useAppSelector(state => state.direccion.entities);
  const envios = useAppSelector(state => state.envio.entities);
  const cuentas = useAppSelector(state => state.cuenta.entities);
  const pedidoEntity = useAppSelector(state => state.pedido.entity);
  const loading = useAppSelector(state => state.pedido.loading);
  const updating = useAppSelector(state => state.pedido.updating);
  const updateSuccess = useAppSelector(state => state.pedido.updateSuccess);
  const estadoPedidoValues = Object.keys(EstadoPedido);

  const handleClose = () => {
    navigate(`/pedido${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getDireccions({}));
    dispatch(getEnvios({}));
    dispatch(getCuentas({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    if (values.subtotal !== undefined && typeof values.subtotal !== 'number') {
      values.subtotal = Number(values.subtotal);
    }
    if (values.descuento !== undefined && typeof values.descuento !== 'number') {
      values.descuento = Number(values.descuento);
    }
    if (values.ivaTotal !== undefined && typeof values.ivaTotal !== 'number') {
      values.ivaTotal = Number(values.ivaTotal);
    }
    if (values.costoEnvio !== undefined && typeof values.costoEnvio !== 'number') {
      values.costoEnvio = Number(values.costoEnvio);
    }
    if (values.total !== undefined && typeof values.total !== 'number') {
      values.total = Number(values.total);
    }

    const entity = {
      ...pedidoEntity,
      ...values,
      direccion: direccions.find(it => it.id.toString() === values.direccion?.toString()),
      envio: envios.find(it => it.id.toString() === values.envio?.toString()),
      cuenta: cuentas.find(it => it.id.toString() === values.cuenta?.toString()),
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
          estado: 'PENDING',
          ...pedidoEntity,
          direccion: pedidoEntity?.direccion?.id,
          envio: pedidoEntity?.envio?.id,
          cuenta: pedidoEntity?.cuenta?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="knstoreApp.pedido.home.createOrEditLabel" data-cy="PedidoCreateUpdateHeading">
            Crear o editar Pedido
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew && <ValidatedField name="id" required readOnly id="pedido-id" label="ID" validate={{ required: true }} />}
              <ValidatedField
                label="Numero Pedido"
                id="pedido-numeroPedido"
                name="numeroPedido"
                data-cy="numeroPedido"
                type="text"
                validate={{
                  required: { value: true, message: 'Este campo es obligatorio.' },
                  maxLength: { value: 30, message: 'Este campo no puede superar más de 30 caracteres.' },
                }}
              />
              <ValidatedField label="Estado" id="pedido-estado" name="estado" data-cy="estado" type="select">
                {estadoPedidoValues.map(estadoPedido => (
                  <option value={estadoPedido} key={estadoPedido}>
                    {estadoPedido}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label="Subtotal"
                id="pedido-subtotal"
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
                label="Descuento"
                id="pedido-descuento"
                name="descuento"
                data-cy="descuento"
                type="text"
                validate={{
                  min: { value: 0, message: 'Este campo debe ser mayor que 0.' },
                  validate: v => isNumber(v) || 'Este campo debe ser un número.',
                }}
              />
              <ValidatedField
                label="Iva Total"
                id="pedido-ivaTotal"
                name="ivaTotal"
                data-cy="ivaTotal"
                type="text"
                validate={{
                  min: { value: 0, message: 'Este campo debe ser mayor que 0.' },
                  validate: v => isNumber(v) || 'Este campo debe ser un número.',
                }}
              />
              <ValidatedField
                label="Costo Envio"
                id="pedido-costoEnvio"
                name="costoEnvio"
                data-cy="costoEnvio"
                type="text"
                validate={{
                  min: { value: 0, message: 'Este campo debe ser mayor que 0.' },
                  validate: v => isNumber(v) || 'Este campo debe ser un número.',
                }}
              />
              <ValidatedField
                label="Total"
                id="pedido-total"
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
                label="Notas Cliente"
                id="pedido-notasCliente"
                name="notasCliente"
                data-cy="notasCliente"
                type="text"
                validate={{
                  maxLength: { value: 500, message: 'Este campo no puede superar más de 500 caracteres.' },
                }}
              />
              <ValidatedField
                label="Notas Internas"
                id="pedido-notasInternas"
                name="notasInternas"
                data-cy="notasInternas"
                type="text"
                validate={{
                  maxLength: { value: 500, message: 'Este campo no puede superar más de 500 caracteres.' },
                }}
              />
              <ValidatedField
                label="Ip Origen"
                id="pedido-ipOrigen"
                name="ipOrigen"
                data-cy="ipOrigen"
                type="text"
                validate={{
                  maxLength: { value: 45, message: 'Este campo no puede superar más de 45 caracteres.' },
                }}
              />
              <ValidatedField
                label="User Agent"
                id="pedido-userAgent"
                name="userAgent"
                data-cy="userAgent"
                type="text"
                validate={{
                  maxLength: { value: 300, message: 'Este campo no puede superar más de 300 caracteres.' },
                }}
              />
              <ValidatedField id="pedido-direccion" name="direccion" data-cy="direccion" label="Direccion" type="select" required>
                <option value="" key="0" />
                {direccions
                  ? direccions.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>Este campo es obligatorio.</FormText>
              <ValidatedField id="pedido-envio" name="envio" data-cy="envio" label="Envio" type="select" required>
                <option value="" key="0" />
                {envios
                  ? envios.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>Este campo es obligatorio.</FormText>
              <ValidatedField id="pedido-cuenta" name="cuenta" data-cy="cuenta" label="Cuenta" type="select" required>
                <option value="" key="0" />
                {cuentas
                  ? cuentas.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>Este campo es obligatorio.</FormText>
              <Button as={Link as any} id="cancel-save" data-cy="entityCreateCancelButton" to="/pedido" replace variant="info">
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

export default PedidoUpdate;

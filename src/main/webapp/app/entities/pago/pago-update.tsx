import React, { useEffect } from 'react';
import { Button, Col, FormText, Row } from 'react-bootstrap';
import { ValidatedField, ValidatedForm, isNumber } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities as getPedidos } from 'app/entities/pedido/pedido.reducer';
import { EstadoPago } from 'app/shared/model/enumerations/estado-pago.model';
import { MetodoPago } from 'app/shared/model/enumerations/metodo-pago.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';

import { createEntity, getEntity, reset, updateEntity } from './pago.reducer';

export const PagoUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const pedidos = useAppSelector(state => state.pedido.entities);
  const pagoEntity = useAppSelector(state => state.pago.entity);
  const loading = useAppSelector(state => state.pago.loading);
  const updating = useAppSelector(state => state.pago.updating);
  const updateSuccess = useAppSelector(state => state.pago.updateSuccess);
  const metodoPagoValues = Object.keys(MetodoPago);
  const estadoPagoValues = Object.keys(EstadoPago);

  const handleClose = () => {
    navigate(`/pago${location.search}`);
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
    if (values.monto !== undefined && typeof values.monto !== 'number') {
      values.monto = Number(values.monto);
    }
    if (values.intentos !== undefined && typeof values.intentos !== 'number') {
      values.intentos = Number(values.intentos);
    }
    values.fechaPago = convertDateTimeToServer(values.fechaPago);

    const entity = {
      ...pagoEntity,
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
          fechaPago: displayDefaultDateTime(),
        }
      : {
          metodoPago: 'CREDIT_CARD',
          estado: 'PENDING',
          ...pagoEntity,
          fechaPago: convertDateTimeFromServer(pagoEntity.fechaPago),
          pedido: pagoEntity?.pedido?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="knstoreApp.pago.home.createOrEditLabel" data-cy="PagoCreateUpdateHeading">
            Crear o editar Pago
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew && <ValidatedField name="id" required readOnly id="pago-id" label="ID" validate={{ required: true }} />}
              <ValidatedField label="Metodo Pago" id="pago-metodoPago" name="metodoPago" data-cy="metodoPago" type="select">
                {metodoPagoValues.map(metodoPago => (
                  <option value={metodoPago} key={metodoPago}>
                    {metodoPago}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField label="Estado" id="pago-estado" name="estado" data-cy="estado" type="select">
                {estadoPagoValues.map(estadoPago => (
                  <option value={estadoPago} key={estadoPago}>
                    {estadoPago}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label="Monto"
                id="pago-monto"
                name="monto"
                data-cy="monto"
                type="text"
                validate={{
                  required: { value: true, message: 'Este campo es obligatorio.' },
                  min: { value: 0, message: 'Este campo debe ser mayor que 0.' },
                  validate: v => isNumber(v) || 'Este campo debe ser un número.',
                }}
              />
              <ValidatedField
                label="Referencia Pasarela"
                id="pago-referenciaPasarela"
                name="referenciaPasarela"
                data-cy="referenciaPasarela"
                type="text"
                validate={{
                  maxLength: { value: 200, message: 'Este campo no puede superar más de 200 caracteres.' },
                }}
              />
              <ValidatedField
                label="Codigo Autorizacion"
                id="pago-codigoAutorizacion"
                name="codigoAutorizacion"
                data-cy="codigoAutorizacion"
                type="text"
                validate={{
                  maxLength: { value: 100, message: 'Este campo no puede superar más de 100 caracteres.' },
                }}
              />
              <ValidatedField
                label="Descripcion Respuesta"
                id="pago-descripcionRespuesta"
                name="descripcionRespuesta"
                data-cy="descripcionRespuesta"
                type="text"
                validate={{
                  maxLength: { value: 300, message: 'Este campo no puede superar más de 300 caracteres.' },
                }}
              />
              <ValidatedField
                label="Intentos"
                id="pago-intentos"
                name="intentos"
                data-cy="intentos"
                type="text"
                validate={{
                  min: { value: 0, message: 'Este campo debe ser mayor que 0.' },
                  validate: v => isNumber(v) || 'Este campo debe ser un número.',
                }}
              />
              <ValidatedField
                label="Fecha Pago"
                id="pago-fechaPago"
                name="fechaPago"
                data-cy="fechaPago"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField id="pago-pedido" name="pedido" data-cy="pedido" label="Pedido" type="select" required>
                <option value="" key="0" />
                {pedidos
                  ? pedidos.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.numeroPedido}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>Este campo es obligatorio.</FormText>
              <Button as={Link as any} id="cancel-save" data-cy="entityCreateCancelButton" to="/pago" replace variant="info">
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

export default PagoUpdate;

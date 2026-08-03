import React, { useEffect } from 'react';
import { Button, Col, FormText, Row } from 'react-bootstrap';
import { ValidatedField, ValidatedForm, isNumber } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities as getPedidos } from 'app/entities/pedido/pedido.reducer';
import { EstadoEnvio } from 'app/shared/model/enumerations/estado-envio.model';
import { TipoServicioEnvio } from 'app/shared/model/enumerations/tipo-servicio-envio.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';

import { createEntity, getEntity, reset, updateEntity } from './envio.reducer';

export const EnvioUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const pedidos = useAppSelector(state => state.pedido.entities);
  const envioEntity = useAppSelector(state => state.envio.entity);
  const loading = useAppSelector(state => state.envio.loading);
  const updating = useAppSelector(state => state.envio.updating);
  const updateSuccess = useAppSelector(state => state.envio.updateSuccess);
  const tipoServicioEnvioValues = Object.keys(TipoServicioEnvio);
  const estadoEnvioValues = Object.keys(EstadoEnvio);

  const handleClose = () => {
    navigate(`/envio${location.search}`);
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
    if (values.costoEnvio !== undefined && typeof values.costoEnvio !== 'number') {
      values.costoEnvio = Number(values.costoEnvio);
    }
    if (values.pesoKg !== undefined && typeof values.pesoKg !== 'number') {
      values.pesoKg = Number(values.pesoKg);
    }
    if (values.valorDeclarado !== undefined && typeof values.valorDeclarado !== 'number') {
      values.valorDeclarado = Number(values.valorDeclarado);
    }
    values.fechaDespacho = convertDateTimeToServer(values.fechaDespacho);
    values.fechaEntregaEstimada = convertDateTimeToServer(values.fechaEntregaEstimada);
    values.fechaEntrega = convertDateTimeToServer(values.fechaEntrega);

    const entity = {
      ...envioEntity,
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
          fechaDespacho: displayDefaultDateTime(),
          fechaEntregaEstimada: displayDefaultDateTime(),
          fechaEntrega: displayDefaultDateTime(),
        }
      : {
          tipoServicio: 'ESTANDAR',
          estado: 'PENDING',
          ...envioEntity,
          fechaDespacho: convertDateTimeFromServer(envioEntity.fechaDespacho),
          fechaEntregaEstimada: convertDateTimeFromServer(envioEntity.fechaEntregaEstimada),
          fechaEntrega: convertDateTimeFromServer(envioEntity.fechaEntrega),
          pedido: envioEntity?.pedido?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="knstoreApp.envio.home.createOrEditLabel" data-cy="EnvioCreateUpdateHeading">
            Crear o editar Envio
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew && <ValidatedField name="id" required readOnly id="envio-id" label="ID" validate={{ required: true }} />}
              <ValidatedField
                label="Transportadora"
                id="envio-transportadora"
                name="transportadora"
                data-cy="transportadora"
                type="text"
                validate={{
                  maxLength: { value: 100, message: 'Este campo no puede superar más de 100 caracteres.' },
                }}
              />
              <ValidatedField
                label="Numero Rastreo"
                id="envio-numeroRastreo"
                name="numeroRastreo"
                data-cy="numeroRastreo"
                type="text"
                validate={{
                  maxLength: { value: 100, message: 'Este campo no puede superar más de 100 caracteres.' },
                }}
              />
              <ValidatedField label="Tipo Servicio" id="envio-tipoServicio" name="tipoServicio" data-cy="tipoServicio" type="select">
                {tipoServicioEnvioValues.map(tipoServicioEnvio => (
                  <option value={tipoServicioEnvio} key={tipoServicioEnvio}>
                    {tipoServicioEnvio}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField label="Estado" id="envio-estado" name="estado" data-cy="estado" type="select">
                {estadoEnvioValues.map(estadoEnvio => (
                  <option value={estadoEnvio} key={estadoEnvio}>
                    {estadoEnvio}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label="Costo Envio"
                id="envio-costoEnvio"
                name="costoEnvio"
                data-cy="costoEnvio"
                type="text"
                validate={{
                  min: { value: 0, message: 'Este campo debe ser mayor que 0.' },
                  validate: v => isNumber(v) || 'Este campo debe ser un número.',
                }}
              />
              <ValidatedField
                label="Peso Kg"
                id="envio-pesoKg"
                name="pesoKg"
                data-cy="pesoKg"
                type="text"
                validate={{
                  min: { value: 0, message: 'Este campo debe ser mayor que 0.' },
                  validate: v => isNumber(v) || 'Este campo debe ser un número.',
                }}
              />
              <ValidatedField
                label="Valor Declarado"
                id="envio-valorDeclarado"
                name="valorDeclarado"
                data-cy="valorDeclarado"
                type="text"
                validate={{
                  min: { value: 0, message: 'Este campo debe ser mayor que 0.' },
                  validate: v => isNumber(v) || 'Este campo debe ser un número.',
                }}
              />
              <ValidatedField
                label="Url Rastreo"
                id="envio-urlRastreo"
                name="urlRastreo"
                data-cy="urlRastreo"
                type="text"
                validate={{
                  maxLength: { value: 300, message: 'Este campo no puede superar más de 300 caracteres.' },
                }}
              />
              <ValidatedField
                label="Observaciones"
                id="envio-observaciones"
                name="observaciones"
                data-cy="observaciones"
                type="text"
                validate={{
                  maxLength: { value: 300, message: 'Este campo no puede superar más de 300 caracteres.' },
                }}
              />
              <ValidatedField
                label="Fecha Despacho"
                id="envio-fechaDespacho"
                name="fechaDespacho"
                data-cy="fechaDespacho"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label="Fecha Entrega Estimada"
                id="envio-fechaEntregaEstimada"
                name="fechaEntregaEstimada"
                data-cy="fechaEntregaEstimada"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label="Fecha Entrega"
                id="envio-fechaEntrega"
                name="fechaEntrega"
                data-cy="fechaEntrega"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField id="envio-pedido" name="pedido" data-cy="pedido" label="Pedido" type="select" required>
                <option value="" key="0" />
                {pedidos
                  ? pedidos.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        #{otherEntity.numeroPedido || otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>Este campo es obligatorio.</FormText>
              <Button as={Link as any} id="cancel-save" data-cy="entityCreateCancelButton" to="/envio" replace variant="info">
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

export default EnvioUpdate;

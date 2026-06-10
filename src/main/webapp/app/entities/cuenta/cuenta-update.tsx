import React, { useEffect } from 'react';
import { Button, Col, FormText, Row } from 'react-bootstrap';
import { ValidatedBlobField, ValidatedField, ValidatedForm } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities as getTipoDocumentos } from 'app/entities/tipo-documento/tipo-documento.reducer';
import { getUsers } from 'app/modules/administration/user-management/user-management.reducer';
import { Genero } from 'app/shared/model/enumerations/genero.model';
import { TipoPersona } from 'app/shared/model/enumerations/tipo-persona.model';

import { createEntity, getEntity, reset, updateEntity } from './cuenta.reducer';

export const CuentaUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const users = useAppSelector(state => state.userManagement.users);
  const tipoDocumentos = useAppSelector(state => state.tipoDocumento.entities);
  const cuentaEntity = useAppSelector(state => state.cuenta.entity);
  const loading = useAppSelector(state => state.cuenta.loading);
  const updating = useAppSelector(state => state.cuenta.updating);
  const updateSuccess = useAppSelector(state => state.cuenta.updateSuccess);
  const tipoPersonaValues = Object.keys(TipoPersona);
  const generoValues = Object.keys(Genero);

  const handleClose = () => {
    navigate(`/cuenta${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getUsers({}));
    dispatch(getTipoDocumentos({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    const entity = {
      ...cuentaEntity,
      ...values,
      user: users.find(it => it.id.toString() === values.user?.toString()),
      tipoDocumento: tipoDocumentos.find(it => it.id.toString() === values.tipoDocumento?.toString()),
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
          tipoPersona: 'NATURAL',
          genero: 'MASCULINO',
          ...cuentaEntity,
          user: cuentaEntity?.user?.id,
          tipoDocumento: cuentaEntity?.tipoDocumento?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="knstoreApp.cuenta.home.createOrEditLabel" data-cy="CuentaCreateUpdateHeading">
            Crear o editar Cuenta
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew && <ValidatedField name="id" required readOnly id="cuenta-id" label="ID" validate={{ required: true }} />}
              <ValidatedField
                label="Num Documento"
                id="cuenta-numDocumento"
                name="numDocumento"
                data-cy="numDocumento"
                type="text"
                validate={{
                  maxLength: { value: 20, message: 'Este campo no puede superar más de 20 caracteres.' },
                }}
              />
              <ValidatedField label="Tipo Persona" id="cuenta-tipoPersona" name="tipoPersona" data-cy="tipoPersona" type="select">
                {tipoPersonaValues.map(tipoPersona => (
                  <option value={tipoPersona} key={tipoPersona}>
                    {tipoPersona}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label="Primer Nombre"
                id="cuenta-primerNombre"
                name="primerNombre"
                data-cy="primerNombre"
                type="text"
                validate={{
                  required: { value: true, message: 'Este campo es obligatorio.' },
                  maxLength: { value: 50, message: 'Este campo no puede superar más de 50 caracteres.' },
                }}
              />
              <ValidatedField
                label="Segundo Nombre"
                id="cuenta-segundoNombre"
                name="segundoNombre"
                data-cy="segundoNombre"
                type="text"
                validate={{
                  maxLength: { value: 50, message: 'Este campo no puede superar más de 50 caracteres.' },
                }}
              />
              <ValidatedField
                label="Primer Apellido"
                id="cuenta-primerApellido"
                name="primerApellido"
                data-cy="primerApellido"
                type="text"
                validate={{
                  required: { value: true, message: 'Este campo es obligatorio.' },
                  maxLength: { value: 50, message: 'Este campo no puede superar más de 50 caracteres.' },
                }}
              />
              <ValidatedField
                label="Segundo Apellido"
                id="cuenta-segundoApellido"
                name="segundoApellido"
                data-cy="segundoApellido"
                type="text"
                validate={{
                  maxLength: { value: 50, message: 'Este campo no puede superar más de 50 caracteres.' },
                }}
              />
              <ValidatedField
                label="Celular"
                id="cuenta-celular"
                name="celular"
                data-cy="celular"
                type="text"
                validate={{
                  maxLength: { value: 15, message: 'Este campo no puede superar más de 15 caracteres.' },
                }}
              />
              <ValidatedField
                label="Telefono"
                id="cuenta-telefono"
                name="telefono"
                data-cy="telefono"
                type="text"
                validate={{
                  maxLength: { value: 15, message: 'Este campo no puede superar más de 15 caracteres.' },
                }}
              />
              <ValidatedField
                label="Fecha Nacimiento"
                id="cuenta-fechaNacimiento"
                name="fechaNacimiento"
                data-cy="fechaNacimiento"
                type="date"
              />
              <ValidatedField label="Genero" id="cuenta-genero" name="genero" data-cy="genero" type="select">
                {generoValues.map(genero => (
                  <option value={genero} key={genero}>
                    {genero}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedBlobField
                label="Foto Perfil"
                id="cuenta-fotoPerfil"
                name="fotoPerfil"
                data-cy="fotoPerfil"
                isImage
                accept="image/*"
              />
              <ValidatedField label="Activo" id="cuenta-activo" name="activo" data-cy="activo" check type="checkbox" />
              <ValidatedField id="cuenta-user" name="user" data-cy="user" label="User" type="select" required>
                <option value="" key="0" />
                {users
                  ? users.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.login}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>Este campo es obligatorio.</FormText>
              <ValidatedField id="cuenta-tipoDocumento" name="tipoDocumento" data-cy="tipoDocumento" label="Tipo Documento" type="select">
                <option value="" key="0" />
                {tipoDocumentos
                  ? tipoDocumentos.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.sigla}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button as={Link as any} id="cancel-save" data-cy="entityCreateCancelButton" to="/cuenta" replace variant="info">
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

export default CuentaUpdate;

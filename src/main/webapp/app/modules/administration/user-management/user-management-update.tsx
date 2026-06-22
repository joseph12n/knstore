import React, { useEffect, useMemo, useState } from 'react';
import { Button, Col, FormText, Row } from 'react-bootstrap';
import { ValidatedField, ValidatedForm, isEmail } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { faArrowLeft, faSave } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import PasswordStrengthBar from 'app/shared/layout/password/password-strength-bar';

import { createUser, getRoles, getUser, reset, updateUser } from './user-management.reducer';

export const UserManagementUpdate = () => {
  const [password, setPassword] = useState('');
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { login } = useParams<'login'>();
  const isNew = login === undefined;

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getUser(login));
    }
    dispatch(getRoles());
    return () => {
      dispatch(reset());
    };
  }, [login]);

  const handleClose = () => {
    navigate('/admin/user-management');
  };

  const saveUser = values => {
    const userEntity = {
      id: user.id,
      login: values.login,
      firstName: values.firstName,
      lastName: values.lastName,
      email: values.email,
      activated: values.activated,
      langKey: values.langKey?.trim() || user.langKey || 'es',
      authorities: values.authorities,
    };

    if (isNew) {
      dispatch(
        createUser({
          ...userEntity,
          password,
        }),
      );
    } else {
      dispatch(updateUser(userEntity));
    }
    handleClose();
  };

  const updatePassword = event => setPassword(event.target.value);

  const user = useAppSelector(state => state.userManagement.user);
  const loading = useAppSelector(state => state.userManagement.loading);
  const updating = useAppSelector(state => state.userManagement.updating);
  const authorities = useAppSelector(state => state.userManagement.authorities);
  const defaultValues = useMemo(() => ({ ...user, id: undefined, langKey: user.langKey || 'es' }), [user]);

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h1 data-cy="UserManagementCreateUpdateHeading">Crear o editar un usuario</h1>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm onSubmit={saveUser} defaultValues={defaultValues}>
              <ValidatedField
                type="text"
                name="login"
                data-cy="login"
                label="Login"
                validate={{
                  required: {
                    value: true,
                    message: 'Su nombre de usuario es obligatorio.',
                  },
                  pattern: {
                    value: /^[a-zA-Z0-9!$&*+=?^_`{|}~.-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$|^[_.@A-Za-z0-9-]+$/,
                    message: 'Su nombre de usuario no es válido.',
                  },
                  minLength: {
                    value: 1,
                    message: 'Su nombre de usuario debe tener al menos 1 caracter.',
                  },
                  maxLength: {
                    value: 50,
                    message: 'Su nombre de usuario no puede tener más de 50 caracteres.',
                  },
                }}
              />
              {isNew && (
                <>
                  <ValidatedField
                    name="firstPassword"
                    label="Contraseña"
                    placeholder="Contraseña"
                    type="password"
                    onChange={updatePassword}
                    validate={{
                      required: { value: true, message: 'Se requiere una contraseña.' },
                      minLength: { value: 4, message: 'La contraseña debe tener al menos 4 caracteres.' },
                      maxLength: { value: 50, message: 'La contraseña no puede tener más de 50 caracteres.' },
                    }}
                    data-cy="firstPassword"
                  />
                  <PasswordStrengthBar password={password} />
                  <ValidatedField
                    name="secondPassword"
                    label="Confirmación de la contraseña"
                    placeholder="Confirmación de la contraseña"
                    type="password"
                    validate={{
                      required: { value: true, message: 'Debe confirmar la contraseña.' },
                      minLength: { value: 4, message: 'La confirmación debe tener al menos 4 caracteres.' },
                      maxLength: { value: 50, message: 'La confirmación no puede tener más de 50 caracteres.' },
                      validate: v => v === password || 'La contraseña y la confirmación no coinciden.',
                    }}
                    data-cy="secondPassword"
                  />
                </>
              )}
              <ValidatedField
                type="text"
                name="firstName"
                data-cy="firstName"
                label="Nombre"
                validate={{
                  maxLength: {
                    value: 50,
                    message: 'Este campo no puede superar más de 50 caracteres.',
                  },
                }}
              />
              <ValidatedField
                type="text"
                name="lastName"
                data-cy="lastName"
                label="Apellidos"
                validate={{
                  maxLength: {
                    value: 50,
                    message: 'Este campo no puede superar más de 50 caracteres.',
                  },
                }}
              />
              <FormText>This field cannot be longer than 50 characters.</FormText>
              <ValidatedField
                name="email"
                data-cy="email"
                label="Correo electrónico"
                placeholder="Su correo electrónico"
                type="email"
                validate={{
                  required: {
                    value: true,
                    message: 'Se requiere un correo electrónico.',
                  },
                  minLength: {
                    value: 5,
                    message: 'Se requiere que su correo electrónico tenga por lo menos 5 caracteres',
                  },
                  maxLength: {
                    value: 254,
                    message: 'Su correo electrónico no puede tener más de 50 caracteres',
                  },
                  validate: v => isEmail(v) || 'Su correo electrónico no es válido.',
                }}
              />
              <ValidatedField
                type="checkbox"
                name="activated"
                data-cy="activated"
                check
                value={true}
                disabled={!user.id}
                label="Activado"
              />
              <ValidatedField type="select" name="authorities" data-cy="profiles" multiple label="Perfiles">
                {authorities.map(role => (
                  <option value={role} key={role}>
                    {role}
                  </option>
                ))}
              </ValidatedField>
              <Button as={Link as any} to="/admin/user-management" replace variant="info" data-cy="entityCreateCancelButton">
                <FontAwesomeIcon icon={faArrowLeft} />
                &nbsp;
                <span className="d-none d-md-inline">Volver</span>
              </Button>
              &nbsp;
              <Button variant="primary" type="submit" disabled={updating} data-cy="entityCreateSaveButton">
                <FontAwesomeIcon icon={faSave} />
                &nbsp; Guardar
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default UserManagementUpdate;

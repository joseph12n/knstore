import { MiddlewareAPI } from '@reduxjs/toolkit';
import { AxiosError, AxiosResponse, isAxiosError } from 'axios';
import { toast } from 'react-toastify';

import { getMessageFromHeaders } from 'app/shared/jhipster/headers';
import { FieldErrorVM, isProblemWithMessage } from 'app/shared/jhipster/problem-details';
import { isFulfilledAction, isRejectedAction } from 'app/shared/reducers/reducer.utils';

type ToastMessage = {
  message?: string;
};

const ERROR_TRANSLATIONS: Record<string, string> = {
  'error.emailexists': 'El correo electrónico ya está registrado.',
  'error.userexists': 'El usuario ya está registrado.',
  'error.login': 'El nombre de usuario ya está registrado.',
  'error.validation': 'Verifica los datos del formulario.',
  'error.documentoduplicado': 'El tipo y número de documento ya están registrados.',
  'error.badcredentials': 'Usuario o contraseña incorrectos.',
  'error.password': 'La contraseña no es válida.',
  'error.idexists': 'El identificador ya está registrado.',
};

const ENTITY_TECHNICAL_ALERT = /^A (new )?[a-z]+ is (created|updated|deleted) with identifier /i;

export const translateErrorKey = (key?: string | null): string | undefined => (key ? (ERROR_TRANSLATIONS[key] ?? key) : undefined);

const isAdmin = (store: MiddlewareAPI) => {
  const account = (store.getState() as any)?.authentication?.account;
  return (
    Array.isArray(account?.authorities) && (account.authorities.includes('ROLE_ADMIN') || account.authorities.includes('ROLE_MANAGER'))
  );
};

const addErrorAlert = (message: ToastMessage) => {
  toast.error(message.message);
};

const isIgnoredAuthUrl = (url?: string) => url?.endsWith('api/account') || url?.endsWith('api/authenticate');

const getFieldErrorsToasts = (fieldErrors: FieldErrorVM[]): ToastMessage[] =>
  fieldErrors.map(fieldError => {
    if (['Min', 'Max', 'DecimalMin', 'DecimalMax', 'Size'].includes(fieldError.message)) {
      return { message: 'El valor del campo no cumple el formato esperado.' };
    }
    const convertedField = fieldError.field.replace(/\[\d*\]/g, '[]');
    const fieldName = convertedField.charAt(0).toUpperCase() + convertedField.slice(1);
    // Los mensajes de Bean Validation llegan en espanol (p.ej. "Solo se permiten letras");
    // mostrar el mensaje real en lugar de la clave generica del campo.
    return { message: fieldError.message ? translateErrorKey(fieldError.message) : `Error on field "${fieldName}"` };
  });

const handleFulfilled = (store: MiddlewareAPI, payload: any) => {
  if (!payload?.headers) {
    return;
  }
  const { alertMessage } = getMessageFromHeaders(payload.headers);
  if (alertMessage && (isAdmin(store) || !ENTITY_TECHNICAL_ALERT.test(alertMessage))) {
    toast.success(alertMessage);
  }
};

const getErrorBodyMessage = (store: MiddlewareAPI, data: any): string => {
  const messageFirst = data?.message ?? data?.detail ?? data?.error ?? data?.title ?? 'Unknown error!';
  const detailFirst = data?.detail ?? messageFirst;
  return translateErrorKey(isAdmin(store) ? detailFirst : messageFirst) ?? 'Unknown error!';
};

const handleProblemData = (store: MiddlewareAPI, data: any, headers: any) => {
  const problem = isProblemWithMessage(data) ? data : null;
  if (problem?.fieldErrors) {
    getFieldErrorsToasts(problem.fieldErrors).forEach(message => addErrorAlert(message));
    return;
  }
  const { errorMessage } = getMessageFromHeaders(headers ?? {});
  if (errorMessage) {
    addErrorAlert({ message: translateErrorKey(errorMessage) });
  } else if (typeof data === 'string' && data !== '') {
    addErrorAlert({ message: data });
  } else {
    toast.error(getErrorBodyMessage(store, data));
  }
};

const handleErrorResponse = (store: MiddlewareAPI, response: AxiosResponse) => {
  const { status } = response;
  if (status === 401 || isIgnoredAuthUrl(response.config?.url)) {
    // Ignore: the 401 redirects to login and auth checks are treated separately.
    return;
  }
  if (status === 0) {
    // connection refused, server not reachable
    addErrorAlert({ message: 'Server not reachable' });
    return;
  }
  if (status === 404) {
    addErrorAlert({ message: 'Not found' });
    return;
  }
  handleProblemData(store, response.data, response.headers);
};

const handleRejectedError = (store: MiddlewareAPI, error: AxiosError) => {
  if (error.response) {
    handleErrorResponse(store, error.response);
  } else if (isIgnoredAuthUrl(error.config?.url) && error.config?.method === 'get') {
    /* eslint-disable no-console */
    console.log('Authentication Error: Trying to access url api/account with GET.');
  } else {
    addErrorAlert({ message: error.message ?? 'Unknown error!' });
  }
};

export default (store: MiddlewareAPI) => next => action => {
  const { error, payload } = action;

  if (isFulfilledAction(action)) {
    handleFulfilled(store, payload);
  }

  if (isRejectedAction(action) && isAxiosError(error)) {
    handleRejectedError(store, error);
  } else if (error) {
    addErrorAlert({ message: error.message ?? 'Unknown error!' });
  }

  return next(action);
};

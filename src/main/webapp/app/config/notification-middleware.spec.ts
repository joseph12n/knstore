import { describe, expect, it, vi } from 'vitest';

import { configureStore } from '@reduxjs/toolkit';
import { toast } from 'react-toastify';

import {
  INVALID_PASSWORD_TYPE,
  MESSAGE_ALERT_HEADER_NAME,
  MESSAGE_ERROR_HEADER_NAME,
  MESSAGE_PARAM_HEADER_NAME,
} from 'app/shared/jhipster/constants';
import { ProblemWithMessageType } from 'app/shared/jhipster/problem-details';

import notificationMiddleware, { translateErrorKey } from './notification-middleware';

vi.mock('react-toastify', () => ({
  toast: {
    error: vi.fn(),
    success: vi.fn(),
  },
}));

describe('Notification Middleware', () => {
  let store: ReturnType<typeof makeStore>;

  const SUCCESS_TYPE = 'SUCCESS/fulfilled';
  const ERROR_TYPE = 'ERROR/rejected';

  // Default action for use in local tests
  const DEFAULT = {
    type: SUCCESS_TYPE,
    payload: 'foo',
  };
  const HEADER_SUCCESS = {
    type: SUCCESS_TYPE,
    payload: {
      status: 201,
      statusText: 'Created',
      headers: { [MESSAGE_ALERT_HEADER_NAME]: 'foo.created', [MESSAGE_PARAM_HEADER_NAME]: 'foo' },
    },
  };

  const HEADER_TECHNICAL_SUCCESS = {
    type: SUCCESS_TYPE,
    payload: {
      status: 201,
      statusText: 'Created',
      headers: { [MESSAGE_ALERT_HEADER_NAME]: 'A producto is created with identifier 5' },
    },
  };

  const DEFAULT_ERROR = {
    type: ERROR_TYPE,
    error: { message: 'foo' },
  };
  const VALIDATION_ERROR = {
    type: ERROR_TYPE,
    error: {
      isAxiosError: true,
      response: {
        data: {
          type: ProblemWithMessageType,
          title: 'Method argument not valid',
          status: 400,
          path: '/api/foos',
          message: 'error.validation',
          fieldErrors: [{ objectName: 'foos', field: 'minField', message: 'Min' }],
        },
        status: 400,
        statusText: 'Bad Request',
        headers: { expires: '0' },
      },
    },
  };
  const HEADER_ERRORS = {
    type: ERROR_TYPE,
    error: {
      isAxiosError: true,
      response: {
        status: 400,
        statusText: 'Bad Request',
        headers: { [MESSAGE_ERROR_HEADER_NAME]: 'foo.creation', [MESSAGE_PARAM_HEADER_NAME]: 'foo' },
      },
    },
  };
  const NOT_FOUND_ERROR = {
    type: ERROR_TYPE,
    error: {
      isAxiosError: true,
      response: {
        data: {
          status: 404,
          message: 'Not found',
        },
        status: 404,
      },
    },
  };
  const NO_SERVER_ERROR = {
    type: ERROR_TYPE,
    error: {
      isAxiosError: true,
      response: {
        status: 0,
      },
    },
  };
  const GENERIC_ERROR = {
    type: ERROR_TYPE,
    error: {
      isAxiosError: true,
      response: {
        data: {
          message: 'Error',
        },
      },
    },
  };
  const LOGIN_REJECTED_ERROR = {
    type: ERROR_TYPE,
    error: {
      isAxiosError: true,
      response: {
        data: '',
        config: {
          url: 'api/authenticate',
        },
        status: 401,
      },
    },
  };

  const TITLE_ERROR = {
    type: ERROR_TYPE,
    error: {
      isAxiosError: true,
      response: {
        data: {
          title: 'Incorrect password',
          status: 400,
          type: INVALID_PASSWORD_TYPE,
        },
        status: 400,
      },
    },
  };

  const STRING_DATA_ERROR = {
    type: ERROR_TYPE,
    error: {
      isAxiosError: true,
      response: {
        data: 'Incorrect password string',
        status: 400,
      },
    },
  };

  const UNKNOWN_400_ERROR = {
    type: ERROR_TYPE,
    error: {
      isAxiosError: true,
      response: {
        status: 400,
      },
    },
  };

  const UNKNOWN_ERROR = {
    type: ERROR_TYPE,
    error: {
      isAxiosError: true,
    },
  };

  const CLIENTE_STATE = { authentication: { account: { authorities: ['ROLE_CLIENTE'] } } };
  const ADMIN_STATE = { authentication: { account: { authorities: ['ROLE_ADMIN'] } } };

  const makeStore = (initialState: any = CLIENTE_STATE) =>
    configureStore({
      reducer: (state: any = initialState) => state,
      middleware: getDefaultMiddleware => getDefaultMiddleware().concat(notificationMiddleware),
    });

  beforeEach(() => {
    store = makeStore();
    vi.clearAllMocks();
    // ignore console errors
    vi.spyOn(globalThis.console, 'error').mockImplementation();
  });

  afterEach(() => {
    vi.restoreAllMocks();
  });

  it('should not trigger a toast message but should return action', () => {
    expect(store.dispatch(DEFAULT).payload).toEqual('foo');
    expect(toast.error).not.toHaveBeenCalled();
    expect(toast.success).not.toHaveBeenCalled();
  });

  it('should trigger a success toast message for header alerts', () => {
    expect(store.dispatch(HEADER_SUCCESS).payload.status).toEqual(201);
    expect(toast.success).toHaveBeenCalledWith(expect.stringContaining('foo.created'));
  });

  it('should not toast technical entity success alerts for non-admin users', () => {
    expect(store.dispatch(HEADER_TECHNICAL_SUCCESS).payload.status).toEqual(201);
    expect(toast.success).not.toHaveBeenCalled();
  });

  it('should toast technical entity success alerts for admin users', () => {
    store = makeStore(ADMIN_STATE);
    expect(store.dispatch(HEADER_TECHNICAL_SUCCESS).payload.status).toEqual(201);
    expect(toast.success).toHaveBeenCalledWith('A producto is created with identifier 5');
  });

  it('should trigger an error toast message and return error', () => {
    expect(store.dispatch(DEFAULT_ERROR).error.message).toEqual('foo');
    expect(toast.error).toHaveBeenCalledWith('foo');
  });

  it('should trigger an error toast message and return error for generic message', () => {
    expect(store.dispatch(GENERIC_ERROR).error.response.data.message).toEqual('Error');
    expect(toast.error).toHaveBeenCalledWith('Error');
  });

  it('should trigger an error toast message and return error for 400 response code', () => {
    expect(store.dispatch(VALIDATION_ERROR).error.response.data.message).toEqual('error.validation');
    expect(toast.error).toHaveBeenCalledWith(expect.stringContaining('El valor del campo no cumple el formato esperado.'));
  });

  it('should trigger an error toast message and return error for 404 response code', () => {
    expect(store.dispatch(NOT_FOUND_ERROR).error.response.data.message).toEqual('Not found');
    expect(toast.error).toHaveBeenCalledWith(expect.stringContaining('Not found'));
  });

  it('should trigger an error toast message and return error for 0 response code', () => {
    expect(store.dispatch(NO_SERVER_ERROR).error.response.status).toEqual(0);
    expect(toast.error).toHaveBeenCalledWith(expect.stringContaining('Server not reachable'));
  });

  it('should trigger an error toast message and return error for headers containing errors', () => {
    expect(store.dispatch(HEADER_ERRORS).error.response.status).toEqual(400);
    expect(toast.error).toHaveBeenCalledWith(expect.stringContaining('foo.creation'));
  });

  it('should not trigger an error toast message and return error for 401 response code', () => {
    expect(store.dispatch(LOGIN_REJECTED_ERROR).error.response.status).toEqual(401);
    expect(toast.error).not.toHaveBeenCalled();
    expect(toast.success).not.toHaveBeenCalled();
  });

  it('should trigger an error toast message and return error for 400 response code', () => {
    expect(store.dispatch(TITLE_ERROR).error.response.status).toEqual(400);
    expect(toast.error).toHaveBeenCalledWith('Incorrect password');
  });

  it('should trigger an error toast message and return error for string in data', () => {
    expect(store.dispatch(STRING_DATA_ERROR).error.response.status).toEqual(400);
    expect(toast.error).toHaveBeenCalledWith('Incorrect password string');
  });

  it('should trigger an error toast message and return error for unknown 400 error', () => {
    expect(store.dispatch(UNKNOWN_400_ERROR).error.response.status).toEqual(400);
    expect(toast.error).toHaveBeenCalledWith('Unknown error!');
  });

  it('should trigger an error toast message and return error for unknown error', () => {
    expect(store.dispatch(UNKNOWN_ERROR).error.isAxiosError).toEqual(true);
    expect(toast.error).toHaveBeenCalledWith('Unknown error!');
  });

  it('should translate backend error keys to spanish in error toast messages', () => {
    store = makeStore(CLIENTE_STATE);
    const BACKEND_ERROR = {
      type: ERROR_TYPE,
      error: {
        isAxiosError: true,
        response: {
          data: {
            message: 'error.emailexists',
            status: 400,
          },
          status: 400,
        },
      },
    };
    expect(store.dispatch(BACKEND_ERROR).error.response.data.message).toEqual('error.emailexists');
    expect(toast.error).toHaveBeenCalledWith('El correo electrónico ya está registrado.');
  });

  it('should prefer the message key over the detail for non-admin users', () => {
    store = makeStore(CLIENTE_STATE);
    const BACKEND_ERROR = {
      type: ERROR_TYPE,
      error: {
        isAxiosError: true,
        response: {
          data: {
            detail: 'Internal server detail',
            message: 'error.emailexists',
            status: 400,
          },
          status: 400,
        },
      },
    };
    expect(store.dispatch(BACKEND_ERROR).error.response.data.message).toEqual('error.emailexists');
    expect(toast.error).toHaveBeenCalledWith('El correo electrónico ya está registrado.');
  });

  it('should prefer the technical detail over the message key for admin users', () => {
    store = makeStore(ADMIN_STATE);
    const BACKEND_ERROR = {
      type: ERROR_TYPE,
      error: {
        isAxiosError: true,
        response: {
          data: {
            detail: 'Internal server detail',
            message: 'error.emailexists',
            status: 400,
          },
          status: 400,
        },
      },
    };
    expect(store.dispatch(BACKEND_ERROR).error.response.data.message).toEqual('error.emailexists');
    expect(toast.error).toHaveBeenCalledWith('Internal server detail');
  });

  it('should translate known backend error keys and leave unknown keys untouched', () => {
    expect(translateErrorKey('error.emailexists')).toEqual('El correo electrónico ya está registrado.');
    expect(translateErrorKey('error.userexists')).toEqual('El usuario ya está registrado.');
    expect(translateErrorKey('error.login')).toEqual('El nombre de usuario ya está registrado.');
    expect(translateErrorKey('error.validation')).toEqual('Verifica los datos del formulario.');
    expect(translateErrorKey('error.documentoduplicado')).toEqual('El tipo y número de documento ya están registrados.');
    expect(translateErrorKey('error.badcredentials')).toEqual('Usuario o contraseña incorrectos.');
    expect(translateErrorKey('error.password')).toEqual('La contraseña no es válida.');
    expect(translateErrorKey('error.idexists')).toEqual('El identificador ya está registrado.');
    expect(translateErrorKey('foo.unknown')).toEqual('foo.unknown');
    expect(translateErrorKey(undefined)).toBeUndefined();
  });
});

import { loadIcons } from './config/icon-loader';

loadIcons();

Object.assign(globalThis as any, {
  IS_REACT_ACT_ENVIRONMENT: true,
  SERVER_API_URL: '',
  VERSION: '0.0.1-test',
  DEVELOPMENT: false,
});

if (!(globalThis as any)._virtualConsole) {
  (globalThis as any)._virtualConsole = { emit: () => false };
}

// Mock de localStorage/sessionStorage para entornos de prueba sin soporte completo
const createStorageMock = () => {
  const store: Record<string, string> = {};
  return {
    getItem(key: string) {
      return store[key] || null;
    },
    setItem(key: string, value: string) {
      store[key] = String(value);
    },
    removeItem(key: string) {
      delete store[key];
    },
    clear() {
      Object.keys(store).forEach(k => delete store[k]);
    },
  };
};

if (typeof window !== 'undefined' && !window.localStorage) {
  Object.defineProperty(window, 'localStorage', { value: createStorageMock() });
}
if (typeof window !== 'undefined' && !window.sessionStorage) {
  Object.defineProperty(window, 'sessionStorage', { value: createStorageMock() });
}

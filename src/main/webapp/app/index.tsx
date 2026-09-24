import React from 'react';

import { createRoot } from 'react-dom/client';
import { Provider } from 'react-redux';
import { bindActionCreators } from 'redux';

import AppComponent from 'app/app';
import setupAxiosInterceptors from 'app/config/axios-interceptor';
import { loadIcons } from 'app/config/icon-loader';
import getStore from 'app/config/store';
import ErrorBoundary from 'app/shared/error/error-boundary';
import { clearAuthentication } from 'app/shared/reducers/authentication';
import { registerServiceWorker } from 'app/serviceWorkerRegistration';

const store = getStore();

const actions = bindActionCreators({ clearAuthentication }, store.dispatch);
setupAxiosInterceptors(() => actions.clearAuthentication('login.error.unauthorized'));

loadIcons();

if (process.env.NODE_ENV === 'production') {
  registerServiceWorker();
} else if ('serviceWorker' in navigator) {
  // En desarrollo el service worker cachea el bundle y rompe el HMR (bucle de
  // recargas); se desregistra y se limpian las cachés para liberar al navegador.
  navigator.serviceWorker.getRegistrations().then(registrations => registrations.forEach(registration => registration.unregister()));
  if (window.caches) {
    caches.keys().then(keys => keys.forEach(key => caches.delete(key)));
  }
}

const rootEl = document.getElementById('root');
const root = createRoot(rootEl!);

const render = Component =>
  root.render(
    <ErrorBoundary>
      <Provider store={store}>
        <div>
          <Component />
        </div>
      </Provider>
    </ErrorBoundary>,
  );

render(AppComponent);

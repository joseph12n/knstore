/**
 * Registra el Service Worker generado por Workbox para habilitar PWA.
 * Solo se registra en entornos con soporte (HTTPS o localhost) y si el
 * archivo service-worker.js existe (generado en build de producción).
 */
export function registerServiceWorker(): void {
  if ('serviceWorker' in navigator) {
    window.addEventListener('load', () => {
      navigator.serviceWorker
        .register('/service-worker.js')
        .then(registration => {
          console.warn('Service Worker registrado:', registration.scope);
        })
        .catch(error => {
          console.warn('No se pudo registrar el Service Worker:', error);
        });
    });
  }
}

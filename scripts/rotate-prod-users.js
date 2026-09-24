/**
 * Rotación de credenciales para el lanzamiento a producción.
 *
 * Uso:
 *   node scripts/rotate-prod-users.js [BASE_URL]
 *
 * Ejemplos:
 *   node scripts/rotate-prod-users.js
 *   node scripts/rotate-prod-users.js https://app.knstore.duckdns.org
 *
 * Variables de entorno:
 *   - KNSTORE_BASE_URL      (default: http://localhost:8080)
 *   - KNSTORE_OLD_USERNAME  (default: admin)
 *   - KNSTORE_OLD_PASSWORD  (default: admin)
 *   - KNSTORE_NEW_LOGIN     (default: admin)
 *   - KNSTORE_NEW_PASSWORD  (OBLIGATORIO)
 *   - KNSTORE_NEW_EMAIL     (opcional, actualiza el correo del administrador)
 *
 * Acciones:
 *   1. Autentica con las credenciales actuales del administrador.
 *   2. Rota la contraseña (o crea un administrador nuevo si cambia el login).
 *   3. Actualiza el correo del administrador si KNSTORE_NEW_EMAIL está definido.
 *   4. Desactiva los usuarios demo (user, manager, cliente).
 */

import axios from 'axios';

const BASE_URL = process.argv[2] || process.env.KNSTORE_BASE_URL || 'http://localhost:8080';
const OLD_USERNAME = process.env.KNSTORE_OLD_USERNAME || 'admin';
const OLD_PASSWORD = process.env.KNSTORE_OLD_PASSWORD || 'admin';
const NEW_LOGIN = process.env.KNSTORE_NEW_LOGIN || OLD_USERNAME;
const NEW_PASSWORD = process.env.KNSTORE_NEW_PASSWORD;
const NEW_EMAIL = process.env.KNSTORE_NEW_EMAIL;
const DEMO_LOGINS = ['user', 'manager', 'cliente'];

if (!NEW_PASSWORD) {
  console.error('ERROR: defina KNSTORE_NEW_PASSWORD con la nueva contraseña del administrador.');
  process.exit(1);
}

const api = axios.create({
  baseURL: `${BASE_URL}/api`,
  headers: { 'Content-Type': 'application/json' },
});

async function authenticate() {
  const response = await api.post('/authenticate', {
    username: OLD_USERNAME,
    password: OLD_PASSWORD,
    rememberMe: false,
  });
  api.defaults.headers.common.Authorization = `Bearer ${response.data.id_token}`;
  console.log(`Autenticado como ${OLD_USERNAME}`);
}

async function findUser(login) {
  const response = await api.get('/admin/users', { params: { 'login.equals': login, size: 1 } });
  return Array.isArray(response.data) ? response.data[0] : undefined;
}

async function rotateExistingAdmin() {
  await api.post('/account/change-password', { currentPassword: OLD_PASSWORD, newPassword: NEW_PASSWORD });
  console.log(`Contraseña del administrador "${OLD_USERNAME}" actualizada`);
}

async function createNewAdmin() {
  await api.post('/admin/users', {
    login: NEW_LOGIN,
    email: NEW_EMAIL || `${NEW_LOGIN}@knstore.local`,
    firstName: 'admin',
    lastName: 'Administrator',
    langKey: 'es',
    activated: true,
    authorities: ['ROLE_ADMIN', 'ROLE_USER'],
    password: NEW_PASSWORD,
  });
  console.log(`Administrador "${NEW_LOGIN}" creado`);
}

async function updateAdminEmail(admin) {
  if (!NEW_EMAIL || admin.email === NEW_EMAIL) return;
  await api.put('/admin/users', { ...admin, email: NEW_EMAIL });
  console.log(`Correo del administrador actualizado a ${NEW_EMAIL}`);
}

async function deactivateDemoUsers() {
  for (const login of DEMO_LOGINS) {
    const user = await findUser(login);
    if (!user || user.activated === false) continue;
    await api.put('/admin/users', { ...user, activated: false });
    console.log(`Usuario demo "${login}" desactivado`);
  }
}

async function deactivateOldAdmin() {
  const oldAdmin = await findUser(OLD_USERNAME);
  if (!oldAdmin || OLD_USERNAME === NEW_LOGIN || oldAdmin.activated === false) return;
  await api.put('/admin/users', { ...oldAdmin, activated: false });
  console.log(`Administrador anterior "${OLD_USERNAME}" desactivado`);
}

(async () => {
  try {
    await authenticate();

    if (NEW_LOGIN === OLD_USERNAME) {
      await rotateExistingAdmin();
      const admin = await findUser(OLD_USERNAME);
      if (admin) await updateAdminEmail(admin);
    } else {
      await createNewAdmin();
      await deactivateOldAdmin();
    }

    await deactivateDemoUsers();
    console.log('Rotación completada. Verifique el acceso con las nuevas credenciales y elimine este historial del shell.');
  } catch (error) {
    const detalle = error.response?.data?.detail || error.response?.data?.title || error.message;
    console.error(`ERROR: ${detalle}`);
    process.exit(1);
  }
})();

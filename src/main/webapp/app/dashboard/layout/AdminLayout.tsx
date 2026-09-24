import React, { useEffect, useState } from 'react';
import { Dropdown } from 'react-bootstrap';
import { Link, useLocation, useNavigate } from 'react-router';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import {
  faBars,
  faBox,
  faChartLine,
  faClipboardList,
  faCogs,
  faCoins,
  faFileInvoice,
  faHeartbeat,
  faImage,
  faList,
  faMapMarkerAlt,
  faMoon,
  faPercentage,
  faReceipt,
  faSignOutAlt,
  faStore,
  faSun,
  faTags,
  faTachometerAlt,
  faTruck,
  faUndo,
  faUser,
  faUsers,
} from '@fortawesome/free-solid-svg-icons';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { logout } from 'app/shared/reducers/authentication';

type Tema = 'light' | 'dark';

const TEMA_KEY = 'kn-theme';

const getTemaInicial = (): Tema => {
  const guardado = window.localStorage.getItem(TEMA_KEY);
  if (guardado === 'light' || guardado === 'dark') {
    return guardado;
  }
  return window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light';
};

const NAVEGACION = [
  {
    grupo: 'Panel',
    items: [{ to: '/admin', label: 'Dashboard', icon: faTachometerAlt }],
  },
  {
    grupo: 'Operación',
    items: [
      { to: '/admin/operacion/pedidos', label: 'Pedidos', icon: faClipboardList },
      { to: '/admin/operacion/envios', label: 'Envíos', icon: faTruck },
      { to: '/admin/operacion/reembolsos', label: 'Reembolsos', icon: faUndo },
    ],
  },
  {
    grupo: 'Catálogo',
    items: [
      { to: '/producto', label: 'Productos', icon: faBox },
      { to: '/producto-inventario', label: 'Inventario', icon: faList },
      { to: '/producto-precio', label: 'Precios', icon: faCoins },
      { to: '/producto-imagen', label: 'Imágenes', icon: faImage },
      { to: '/categoria', label: 'Categorías', icon: faTags },
      { to: '/subcategoria', label: 'Subcategorías', icon: faTags },
      { to: '/marca', label: 'Marcas', icon: faTags },
      { to: '/etiqueta-producto', label: 'Etiquetas', icon: faTags },
      { to: '/categoria-iva', label: 'Categoría IVA', icon: faPercentage },
    ],
  },
  {
    grupo: 'Clientes',
    items: [
      { to: '/cuenta', label: 'Cuentas', icon: faUsers },
      { to: '/direccion', label: 'Direcciones', icon: faMapMarkerAlt },
      { to: '/carrito', label: 'Carritos', icon: faStore },
      { to: '/item-carrito', label: 'Ítems de carrito', icon: faStore },
    ],
  },
  {
    grupo: 'Ventas',
    items: [
      { to: '/pedido', label: 'Pedidos (CRUD)', icon: faClipboardList },
      { to: '/item-pedido', label: 'Ítems de pedido', icon: faClipboardList },
      { to: '/pago', label: 'Pagos', icon: faCoins },
      { to: '/factura', label: 'Facturas', icon: faFileInvoice },
      { to: '/envio', label: 'Envíos (CRUD)', icon: faTruck },
    ],
  },
  {
    grupo: 'Administración',
    items: [
      { to: '/admin/user-management', label: 'Usuarios', icon: faUser },
      { to: '/admin/health', label: 'Salud', icon: faHeartbeat },
      { to: '/admin/metrics', label: 'Métricas', icon: faChartLine },
      { to: '/admin/configuration', label: 'Configuración', icon: faCogs },
      { to: '/admin/logs', label: 'Logs', icon: faList },
      { to: '/admin/docs', label: 'API', icon: faReceipt },
    ],
  },
];

interface AdminLayoutProps {
  children: React.ReactNode;
}

const esActivo = (pathname: string, to: string) =>
  to === '/admin' ? pathname === '/admin' : pathname === to || pathname.startsWith(`${to}/`);

export const AdminLayout = ({ children }: AdminLayoutProps) => {
  const [tema, setTema] = useState<Tema>(getTemaInicial);
  const [menuAbierto, setMenuAbierto] = useState(false);
  const location = useLocation();
  const navigate = useNavigate();
  const dispatch = useAppDispatch();
  const account = useAppSelector(state => state.authentication.account);

  useEffect(() => {
    window.localStorage.setItem(TEMA_KEY, tema);
  }, [tema]);

  useEffect(() => {
    setMenuAbierto(false);
  }, [location.pathname]);

  const itemActivo = NAVEGACION.flatMap(g => g.items).find(item => esActivo(location.pathname, item.to));

  const handleLogout = () => {
    dispatch(logout());
    navigate('/login');
  };

  return (
    <div className="admin-shell" data-theme={tema}>
      {menuAbierto && <div className="admin-overlay d-lg-none" onClick={() => setMenuAbierto(false)} aria-hidden="true" />}

      <aside className={`admin-sidebar ${menuAbierto ? 'abierto' : ''}`}>
        <div className="admin-brand">
          <Link to="/admin">
            Knstore <span>Admin</span>
          </Link>
        </div>
        <nav className="admin-nav">
          {NAVEGACION.map(grupo => (
            <div key={grupo.grupo} className="admin-nav__grupo">
              <span className="admin-nav__titulo">{grupo.grupo}</span>
              {grupo.items.map(item => (
                <Link key={item.to} to={item.to} className={`admin-nav__item ${esActivo(location.pathname, item.to) ? 'activo' : ''}`}>
                  <FontAwesomeIcon icon={item.icon} fixedWidth />
                  <span>{item.label}</span>
                </Link>
              ))}
            </div>
          ))}
        </nav>
      </aside>

      <div className="admin-main">
        <header className="admin-topbar">
          <button type="button" className="admin-topbar__boton d-lg-none" onClick={() => setMenuAbierto(true)} aria-label="Abrir menú">
            <FontAwesomeIcon icon={faBars} />
          </button>
          <span className="admin-topbar__titulo">{itemActivo?.label ?? 'Administración'}</span>
          <div className="admin-topbar__acciones">
            <Link to="/" className="admin-topbar__boton" title="Ir a la tienda">
              <FontAwesomeIcon icon={faStore} />
              <span className="d-none d-md-inline ms-2">Tienda</span>
            </Link>
            <button
              type="button"
              className="admin-topbar__boton"
              onClick={() => setTema(actual => (actual === 'dark' ? 'light' : 'dark'))}
              aria-label={tema === 'dark' ? 'Activar modo claro' : 'Activar modo oscuro'}
            >
              <FontAwesomeIcon icon={tema === 'dark' ? faSun : faMoon} />
            </button>
            <Dropdown align="end">
              <Dropdown.Toggle as="button" type="button" className="admin-topbar__boton" id="admin-user-menu">
                <FontAwesomeIcon icon={faUser} />
                <span className="d-none d-md-inline ms-2">{account.login || 'Cuenta'}</span>
              </Dropdown.Toggle>
              <Dropdown.Menu>
                <Dropdown.Item onClick={handleLogout}>
                  <FontAwesomeIcon icon={faSignOutAlt} className="me-2" />
                  Cerrar sesión
                </Dropdown.Item>
              </Dropdown.Menu>
            </Dropdown>
          </div>
        </header>

        <main className="admin-content">{children}</main>
      </div>
    </div>
  );
};

export default AdminLayout;

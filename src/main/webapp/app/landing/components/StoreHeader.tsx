import React, { useState } from 'react';
import { Badge, Container, Nav, Navbar, NavDropdown, Offcanvas } from 'react-bootstrap';
import { Link, useNavigate } from 'react-router';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import {
  faBars,
  faBox,
  faHome,
  faMapMarkerAlt,
  faShoppingBag,
  faShoppingCart,
  faSignInAlt,
  faSignOutAlt,
  faUser,
  faCreditCard,
  faTruck,
  faFileInvoice,
  faLock,
} from '@fortawesome/free-solid-svg-icons';

import { ICategoria } from 'app/shared/model/categoria.model';
import { ISubcategoria } from 'app/shared/model/subcategoria.model';
import { logout } from 'app/shared/reducers/authentication';
import { STORE_NAME } from 'app/landing/utils/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { Authority } from 'app/shared/jhipster/constants';
import { hasAnyAuthority } from 'app/shared/auth/private-route';
import SearchBox from './SearchBox';
import CartDrawer from './CartDrawer';
import useCart from 'app/landing/hooks/useCart';

interface StoreHeaderProps {
  categorias: ICategoria[];
  subcategorias: ISubcategoria[];
}

export const StoreHeader = ({ categorias, subcategorias }: StoreHeaderProps) => {
  const categoriasList = categorias ?? [];
  const subcategoriasList = subcategorias ?? [];
  const { count } = useCart();
  const [showCart, setShowCart] = useState(false);
  const [showMobileMenu, setShowMobileMenu] = useState(false);
  const dispatch = useAppDispatch();
  const navigate = useNavigate();

  const account = useAppSelector(state => state.authentication.account);
  const isAuthenticated = useAppSelector(state => state.authentication.isAuthenticated);
  const isAdminOrManager = hasAnyAuthority(account.authorities, [Authority.ADMIN, Authority.MANAGER]);

  const cartCount = count;

  const handleLogout = () => {
    dispatch(logout());
    navigate('/');
  };

  const getSubcategorias = (categoriaId?: string) => subcategoriasList.filter(s => s.categoria?.id === categoriaId);

  return (
    <>
      <header className="storefront-header sticky-top" style={{ zIndex: 1030 }}>
        {/* Top bar */}
        <div
          className="py-2 text-center small"
          style={{ backgroundColor: 'var(--kn-color-primary)', color: 'var(--kn-color-text-inverse)' }}
        >
          Envío gratis en compras superiores a $150.000 · Pagos seguros
        </div>

        {/* Main navbar */}
        <Navbar expand="lg" className="py-2 border-bottom" style={{ backgroundColor: 'var(--kn-color-background)' }}>
          <Container>
            <button type="button" className="btn btn-link d-lg-none p-0 me-2" onClick={() => setShowMobileMenu(true)} aria-label="Menú">
              <FontAwesomeIcon icon={faBars} size="lg" />
            </button>

            <Navbar.Brand as={Link as any} to="/" className="fw-bold fs-4" style={{ color: 'var(--kn-color-text)' }}>
              {STORE_NAME}
            </Navbar.Brand>

            <div className="d-none d-lg-block flex-grow-1 mx-5" style={{ maxWidth: '520px' }}>
              <SearchBox />
            </div>

            <Nav className="flex-row align-items-center gap-3">
              {isAuthenticated ? (
                <NavDropdown
                  title={
                    <span>
                      <FontAwesomeIcon icon={faUser} className="me-1" />
                      <span className="d-none d-md-inline">Mi cuenta</span>
                    </span>
                  }
                  id="account-dropdown"
                  align="end"
                >
                  <NavDropdown.Item as={Link as any} to="/cuenta">
                    <FontAwesomeIcon icon={faUser} className="me-2" />
                    Perfil
                  </NavDropdown.Item>
                  <NavDropdown.Item as={Link as any} to="/cuenta/pedidos">
                    <FontAwesomeIcon icon={faShoppingBag} className="me-2" />
                    Mis pedidos
                  </NavDropdown.Item>
                  <NavDropdown.Item as={Link as any} to="/cuenta/direcciones">
                    <FontAwesomeIcon icon={faMapMarkerAlt} className="me-2" />
                    Direcciones
                  </NavDropdown.Item>
                  {isAdminOrManager && (
                    <>
                      <NavDropdown.Divider />
                      <NavDropdown.Item as={Link as any} to="/admin/user-management">
                        <FontAwesomeIcon icon={faBox} className="me-2" />
                        Panel administrativo
                      </NavDropdown.Item>
                    </>
                  )}
                  <NavDropdown.Divider />
                  <NavDropdown.Item onClick={handleLogout}>
                    <FontAwesomeIcon icon={faSignOutAlt} className="me-2" />
                    Cerrar sesión
                  </NavDropdown.Item>
                </NavDropdown>
              ) : (
                <Nav.Link as={Link as any} to="/login" className="d-flex align-items-center gap-1">
                  <FontAwesomeIcon icon={faSignInAlt} />
                  <span className="d-none d-md-inline">Ingresar</span>
                </Nav.Link>
              )}

              <button
                type="button"
                className="btn btn-link position-relative p-0"
                onClick={() => setShowCart(true)}
                aria-label="Abrir carrito"
                style={{ color: 'var(--kn-color-text)' }}
              >
                <FontAwesomeIcon icon={faShoppingCart} size="lg" />
                {cartCount > 0 && (
                  <Badge bg="danger" pill className="position-absolute top-0 start-100 translate-middle" style={{ fontSize: '0.65rem' }}>
                    {cartCount}
                  </Badge>
                )}
              </button>
            </Nav>
          </Container>
        </Navbar>

        {/* Category nav desktop */}
        <div className="d-none d-lg-block border-bottom" style={{ backgroundColor: 'var(--kn-color-surface)' }}>
          <Container>
            <Nav className="justify-content-center gap-4">
              <Nav.Link as={Link as any} to="/" className="fw-medium py-2">
                <FontAwesomeIcon icon={faHome} className="me-1" />
                Inicio
              </Nav.Link>
              {categoriasList.map(categoria => {
                const subs = getSubcategorias(categoria.id);
                if (subs.length === 0) {
                  return (
                    <Nav.Link key={categoria.id} as={Link as any} to={`/categorias/${categoria.slug}`} className="fw-medium py-2">
                      {categoria.nombre}
                    </Nav.Link>
                  );
                }
                return (
                  <NavDropdown
                    key={categoria.id}
                    title={categoria.nombre}
                    id={`store-cat-${categoria.id}`}
                    className="fw-medium"
                    renderMenuOnMount
                  >
                    <NavDropdown.Item as={Link as any} to={`/categorias/${categoria.slug}`}>
                      Ver todo {categoria.nombre}
                    </NavDropdown.Item>
                    <NavDropdown.Divider />
                    {subs.map(sub => (
                      <NavDropdown.Item key={sub.id} as={Link as any} to={`/categorias/${categoria.slug}/${sub.slug}`}>
                        {sub.nombre}
                      </NavDropdown.Item>
                    ))}
                  </NavDropdown>
                );
              })}
            </Nav>
          </Container>
        </div>
      </header>

      {/* Mobile menu */}
      <Offcanvas show={showMobileMenu} onHide={() => setShowMobileMenu(false)} placement="start">
        <Offcanvas.Header closeButton>
          <Offcanvas.Title className="fw-bold">Menú</Offcanvas.Title>
        </Offcanvas.Header>
        <Offcanvas.Body>
          <div className="mb-3">
            <SearchBox />
          </div>
          <Nav className="flex-column gap-2">
            <Nav.Link as={Link as any} to="/" onClick={() => setShowMobileMenu(false)}>
              <FontAwesomeIcon icon={faHome} className="me-2" />
              Inicio
            </Nav.Link>
            {categoriasList.map(categoria => (
              <Nav.Link key={categoria.id} as={Link as any} to={`/categorias/${categoria.slug}`} onClick={() => setShowMobileMenu(false)}>
                {categoria.nombre}
              </Nav.Link>
            ))}
            {isAuthenticated && (
              <>
                <hr className="my-2" />
                <Nav.Link as={Link as any} to="/cuenta" onClick={() => setShowMobileMenu(false)}>
                  <FontAwesomeIcon icon={faUser} className="me-2" />
                  Mi cuenta
                </Nav.Link>
                <Nav.Link as={Link as any} to="/cuenta/pedidos" onClick={() => setShowMobileMenu(false)}>
                  <FontAwesomeIcon icon={faShoppingBag} className="me-2" />
                  Mis pedidos
                </Nav.Link>
                <Nav.Link as={Link as any} to="/cuenta/direcciones" onClick={() => setShowMobileMenu(false)}>
                  <FontAwesomeIcon icon={faMapMarkerAlt} className="me-2" />
                  Direcciones
                </Nav.Link>
                <Nav.Link as={Link as any} to="/cuenta/pagos" onClick={() => setShowMobileMenu(false)}>
                  <FontAwesomeIcon icon={faCreditCard} className="me-2" />
                  Pagos
                </Nav.Link>
                <Nav.Link as={Link as any} to="/cuenta/envios" onClick={() => setShowMobileMenu(false)}>
                  <FontAwesomeIcon icon={faTruck} className="me-2" />
                  Envíos
                </Nav.Link>
                <Nav.Link as={Link as any} to="/cuenta/facturas" onClick={() => setShowMobileMenu(false)}>
                  <FontAwesomeIcon icon={faFileInvoice} className="me-2" />
                  Facturas
                </Nav.Link>
                <Nav.Link as={Link as any} to="/cuenta/seguridad" onClick={() => setShowMobileMenu(false)}>
                  <FontAwesomeIcon icon={faLock} className="me-2" />
                  Seguridad
                </Nav.Link>
                <Nav.Link as={Link as any} to="/carrito" onClick={() => setShowMobileMenu(false)}>
                  <FontAwesomeIcon icon={faShoppingCart} className="me-2" />
                  Mi carrito
                </Nav.Link>
                {isAdminOrManager && (
                  <Nav.Link as={Link as any} to="/admin/user-management" onClick={() => setShowMobileMenu(false)}>
                    <FontAwesomeIcon icon={faBox} className="me-2" />
                    Panel administrativo
                  </Nav.Link>
                )}
                <hr className="my-2" />
                <Nav.Link
                  onClick={() => {
                    handleLogout();
                    setShowMobileMenu(false);
                  }}
                >
                  <FontAwesomeIcon icon={faSignOutAlt} className="me-2" />
                  Cerrar sesión
                </Nav.Link>
              </>
            )}
            {!isAuthenticated && (
              <Nav.Link as={Link as any} to="/login" onClick={() => setShowMobileMenu(false)}>
                <FontAwesomeIcon icon={faSignInAlt} className="me-2" />
                Iniciar sesión
              </Nav.Link>
            )}
          </Nav>
        </Offcanvas.Body>
      </Offcanvas>

      {/* Cart drawer */}
      <CartDrawer show={showCart} onHide={() => setShowCart(false)} />
    </>
  );
};

export default StoreHeader;

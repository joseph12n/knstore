import React, { useEffect, useState } from 'react';
import { Link, Navigate, useLocation } from 'react-router';
import { Card, Col, Container, Form, InputGroup, Row } from 'react-bootstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faArrowLeft, faMoon, faShieldAlt, faSun, faTruck, faUndoAlt, faUser } from '@fortawesome/free-solid-svg-icons';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { useIsMobileView } from 'app/landing/hooks/useIsMobileView';
import { hasAnyAuthority } from 'app/shared/auth/private-route';
import { Authority } from 'app/shared/jhipster/constants';
import { login } from 'app/shared/reducers/authentication';

type Tema = 'light' | 'dark';

const TEMA_KEY = 'kn-theme';

const getTemaInicial = (): Tema => {
  const guardado = window.localStorage.getItem(TEMA_KEY);
  if (guardado === 'light' || guardado === 'dark') {
    return guardado;
  }
  return window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light';
};

export const LoginPage = () => {
  const dispatch = useAppDispatch();
  const isAuthenticated = useAppSelector(state => state.authentication.isAuthenticated);
  const loginError = useAppSelector(state => state.authentication.loginError);
  const accountAuthorities = useAppSelector(state => state.authentication.account.authorities);
  const pageLocation = useLocation();
  const isMobile = useIsMobileView();

  const [tema, setTema] = useState<Tema>(getTemaInicial);
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [rememberMe, setRememberMe] = useState(false);
  const [pending, setPending] = useState(false);

  useEffect(() => {
    window.localStorage.setItem(TEMA_KEY, tema);
  }, [tema]);

  const handleSubmit = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setPending(true);
    Promise.resolve(dispatch(login(username, password, rememberMe))).finally(() => setPending(false));
  };

  const { from } = pageLocation.state || {};

  if (isAuthenticated) {
    // En móvil el panel admin no existe: cualquier intento de entrar a /admin/*
    // (venga del `from` o del rol) cae en la tienda con el aviso como feedback.
    const intentaPanelAdmin = Boolean(from?.pathname?.startsWith('/admin'));
    if (isMobile && (intentaPanelAdmin || hasAnyAuthority(accountAuthorities, [Authority.ADMIN]))) {
      return <Navigate to="/" replace state={{ avisoPanelEscritorio: true }} />;
    }

    if (from?.pathname && from.pathname !== '/') {
      return <Navigate to={from} replace />;
    }

    if (hasAnyAuthority(accountAuthorities, [Authority.ADMIN])) {
      return <Navigate to="/admin/user-management" replace />;
    }
    if (hasAnyAuthority(accountAuthorities, [Authority.MANAGER])) {
      return <Navigate to="/mi-cuenta" replace />;
    }

    return <Navigate to="/" replace />;
  }

  return (
    <div className="storefront d-flex flex-column min-vh-100" data-theme={tema}>
      <div className="d-flex justify-content-end p-3">
        <button
          type="button"
          className="btn btn-outline-secondary btn-sm"
          onClick={() => setTema(actual => (actual === 'dark' ? 'light' : 'dark'))}
          aria-label={tema === 'dark' ? 'Activar modo claro' : 'Activar modo oscuro'}
        >
          <FontAwesomeIcon icon={tema === 'dark' ? faSun : faMoon} />
        </button>
      </div>

      <Container className="flex-grow-1 d-flex align-items-center">
        <Row className="justify-content-center w-100 my-4">
          <Col xs={12} md={10} lg={8} xl={7}>
            <Card className="overflow-hidden border-0 shadow-sm">
              <Row className="g-0">
                <Col
                  lg={5}
                  className="d-none d-lg-flex flex-column justify-content-between text-white p-4 p-xl-5"
                  style={{ backgroundColor: '#111111' }}
                >
                  <div>
                    <span className="fw-bold fs-3 d-block mb-3">Knstore</span>
                    <p className="mb-4 opacity-75">Moda y retail con envíos a todo Colombia.</p>
                    <ul className="list-unstyled mb-0">
                      <li className="d-flex align-items-start mb-3">
                        <FontAwesomeIcon icon={faTruck} className="mt-1 me-3" />
                        <span>Envío gratis desde $150.000</span>
                      </li>
                      <li className="d-flex align-items-start mb-3">
                        <FontAwesomeIcon icon={faShieldAlt} className="mt-1 me-3" />
                        <span>Pagos seguros</span>
                      </li>
                      <li className="d-flex align-items-start">
                        <FontAwesomeIcon icon={faUndoAlt} className="mt-1 me-3" />
                        <span>Garantía y devoluciones</span>
                      </li>
                    </ul>
                  </div>
                  <Link to="/" className="text-white small text-decoration-none d-inline-flex align-items-center gap-2 mt-5">
                    <FontAwesomeIcon icon={faArrowLeft} />
                    Volver a la tienda
                  </Link>
                </Col>

                <Col lg={7} className="p-4 p-md-5">
                  <h3 className="fw-bold mb-2">Inicia sesión</h3>
                  <p className="text-muted mb-4">Ingresa con tu cuenta de Knstore.</p>

                  {loginError ? (
                    <div className="alert alert-danger" role="alert">
                      Usuario o contraseña incorrectos.
                    </div>
                  ) : null}

                  <Form onSubmit={handleSubmit} noValidate>
                    <Form.Group className="mb-3" controlId="username">
                      <Form.Label>Usuario o correo</Form.Label>
                      <InputGroup>
                        <InputGroup.Text>
                          <FontAwesomeIcon icon={faUser} />
                        </InputGroup.Text>
                        <Form.Control
                          type="text"
                          value={username}
                          onChange={event => setUsername(event.target.value)}
                          autoComplete="username"
                          required
                          data-cy="username"
                        />
                      </InputGroup>
                    </Form.Group>

                    <Form.Group className="mb-3" controlId="password">
                      <Form.Label>Contraseña</Form.Label>
                      <Form.Control
                        type="password"
                        value={password}
                        onChange={event => setPassword(event.target.value)}
                        autoComplete="current-password"
                        required
                        data-cy="password"
                      />
                    </Form.Group>

                    <Form.Group className="mb-4">
                      <Form.Check
                        type="checkbox"
                        id="rememberMe"
                        label="Recordarme"
                        checked={rememberMe}
                        onChange={event => setRememberMe(event.target.checked)}
                      />
                    </Form.Group>

                    <button type="submit" className="btn btn-primary w-100" disabled={pending} data-cy="submit">
                      {pending ? 'Ingresando...' : 'Iniciar sesión'}
                    </button>
                  </Form>

                  <div className="d-flex flex-wrap justify-content-between gap-2 mt-4 small">
                    <Link to="/account/reset/request">¿Olvidaste tu contraseña?</Link>
                    <Link to="/account/register">Crear una cuenta</Link>
                  </div>

                  <Link to="/" className="d-lg-none d-inline-flex align-items-center gap-2 small mt-3 text-secondary text-decoration-none">
                    <FontAwesomeIcon icon={faArrowLeft} />
                    Volver a la tienda
                  </Link>
                </Col>
              </Row>
            </Card>
          </Col>
        </Row>
      </Container>
    </div>
  );
};

export default LoginPage;

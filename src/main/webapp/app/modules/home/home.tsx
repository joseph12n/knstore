import './home.scss';

import React from 'react';
import { Alert, Col, Row } from 'react-bootstrap';
import { Link } from 'react-router';

import { useAppSelector } from 'app/config/store';

export const Home = () => {
  const account = useAppSelector(state => state.authentication.account);

  return (
    <Row>
      <Col md="3" className="pad">
        <span className="hipster rounded" />
      </Col>
      <Col md="9">
        <h1 className="display-4">¡Bienvenido, Java Hipster!</h1>
        <p className="lead">Esta es su página de inicio</p>
        {account?.login ? (
          <div>
            <Alert variant="success">Está conectado como &quot;{account.login}&quot;.</Alert>
          </div>
        ) : (
          <div>
            <Alert variant="warning">
              Si desea
              <span>&nbsp;</span>
              <Link to="/login" className="alert-link">
                iniciar sesión
              </Link>
              , puede intentar con las cuentas predeterminadas:
              <br />- Administrador (usuario=&quot;admin&quot; y contraseña=&quot;admin&quot;) <br />- Usuario (usuario=&quot;user&quot; y
              contraseña=&quot;user&quot;).
            </Alert>

            <Alert variant="warning">
              ¿Aún no tienes una cuenta?&nbsp;
              <Link to="/account/register" className="alert-link">
                Crea una cuenta
              </Link>
            </Alert>
          </div>
        )}
        <p>Si tiene preguntas sobre JHipster:</p>

        <ul>
          <li>
            <a href="https://www.jhipster.tech/" target="_blank" rel="noopener noreferrer">
              Página de inicio de JHipster
            </a>
          </li>
          <li>
            <a href="https://stackoverflow.com/tags/jhipster/info" target="_blank" rel="noopener noreferrer">
              JHipster en Stack Overflow
            </a>
          </li>
          <li>
            <a href="https://github.com/jhipster/generator-jhipster/issues?state=open" target="_blank" rel="noopener noreferrer">
              JHipster seguimiento de errores
            </a>
          </li>
          <li>
            <a href="https://gitter.im/jhipster/generator-jhipster" target="_blank" rel="noopener noreferrer">
              Sala de chat pública de JHipster
            </a>
          </li>
          <li>
            <a href="https://twitter.com/jhipster" target="_blank" rel="noopener noreferrer">
              contacto @jhipster en Twitter
            </a>
          </li>
        </ul>

        <p>
          Si te gusta JHipster, danos una estrella en{' '}
          <a href="https://github.com/jhipster/generator-jhipster" target="_blank" rel="noopener noreferrer">
            GitHub
          </a>
          !
        </p>
      </Col>
    </Row>
  );
};

export default Home;

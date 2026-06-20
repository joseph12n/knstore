import React from 'react';
import { Col, Container, Row } from 'react-bootstrap';
import { Link } from 'react-router';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faCamera, faCreditCard, faEnvelope, faPhone, faTruck } from '@fortawesome/free-solid-svg-icons';

import { STORE_NAME } from 'app/storefront/utils/constants';

export const StoreFooter = () => (
  <footer className="storefront-footer mt-auto pt-5 pb-4" style={{ backgroundColor: 'var(--kn-color-surface)' }}>
    <Container>
      <Row className="gy-4">
        <Col lg={4} md={6}>
          <h5 className="fw-bold mb-3">{STORE_NAME}</h5>
          <p className="text-muted small">
            Tu tienda de moda y retail en Colombia. Encuentra lo último en estilo con envíos a todo el país y pagos seguros.
          </p>
          <div className="d-flex gap-3 mt-3">
            <a href="#" className="text-muted" aria-label="Instagram">
              <FontAwesomeIcon icon={faCamera} size="lg" />
            </a>
            <a href="#" className="text-muted" aria-label="Facebook">
              <FontAwesomeIcon icon={faCamera} size="lg" />
            </a>
            <a href="#" className="text-muted" aria-label="WhatsApp">
              <FontAwesomeIcon icon={faPhone} size="lg" />
            </a>
          </div>
        </Col>

        <Col lg={2} md={6}>
          <h6 className="fw-bold mb-3">Comprar</h6>
          <ul className="list-unstyled small">
            <li className="mb-2">
              <Link to="/" className="text-muted text-decoration-none">
                Inicio
              </Link>
            </li>
            <li className="mb-2">
              <Link to="/buscar" className="text-muted text-decoration-none">
                Catálogo
              </Link>
            </li>
            <li className="mb-2">
              <Link to="/carrito" className="text-muted text-decoration-none">
                Carrito
              </Link>
            </li>
            <li className="mb-2">
              <Link to="/cuenta/pedidos" className="text-muted text-decoration-none">
                Mis pedidos
              </Link>
            </li>
          </ul>
        </Col>

        <Col lg={2} md={6}>
          <h6 className="fw-bold mb-3">Ayuda</h6>
          <ul className="list-unstyled small">
            <li className="mb-2">
              <Link to="#" className="text-muted text-decoration-none">
                Preguntas frecuentes
              </Link>
            </li>
            <li className="mb-2">
              <Link to="#" className="text-muted text-decoration-none">
                Envíos y entregas
              </Link>
            </li>
            <li className="mb-2">
              <Link to="#" className="text-muted text-decoration-none">
                Cambios y garantías
              </Link>
            </li>
            <li className="mb-2">
              <Link to="#" className="text-muted text-decoration-none">
                Contacto
              </Link>
            </li>
          </ul>
        </Col>

        <Col lg={4} md={6}>
          <h6 className="fw-bold mb-3">Contacto</h6>
          <ul className="list-unstyled small text-muted">
            <li className="mb-2">
              <FontAwesomeIcon icon={faPhone} className="me-2" />
              +57 300 123 4567
            </li>
            <li className="mb-2">
              <FontAwesomeIcon icon={faEnvelope} className="me-2" />
              servicioalcliente@knstore.com
            </li>
            <li className="mb-2">
              <FontAwesomeIcon icon={faTruck} className="me-2" />
              Envíos a todo Colombia
            </li>
            <li className="mb-2">
              <FontAwesomeIcon icon={faCreditCard} className="me-2" />
              PSE, Nequi, Daviplata, Efecty, tarjetas y contraentrega
            </li>
          </ul>
        </Col>
      </Row>

      <hr className="my-4" style={{ borderColor: 'var(--kn-color-border)' }} />

      <div className="d-flex flex-wrap justify-content-between align-items-center gap-2 small text-muted">
        <span>© {STORE_NAME}. Todos los derechos reservados.</span>
        <div className="d-flex gap-3">
          <Link to="#" className="text-muted text-decoration-none">
            Términos y condiciones
          </Link>
          <Link to="#" className="text-muted text-decoration-none">
            Política de privacidad
          </Link>
        </div>
      </div>
    </Container>
  </footer>
);

export default StoreFooter;

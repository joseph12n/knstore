import React from 'react';
import { Col, Container, Row } from 'react-bootstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faHeadset, faShieldAlt, faTruck, faUndoAlt } from '@fortawesome/free-solid-svg-icons';
import type { IconDefinition } from '@fortawesome/fontawesome-svg-core';

interface Benefit {
  icon: IconDefinition;
  title: string;
  description: string;
}

const benefits: Benefit[] = [
  { icon: faTruck, title: 'Envío gratis', description: 'En compras desde $150.000' },
  { icon: faShieldAlt, title: 'Pagos seguros', description: 'Nequi, tarjetas y contraentrega' },
  { icon: faUndoAlt, title: 'Garantía', description: 'Hasta 12 meses' },
  { icon: faHeadset, title: 'Soporte', description: 'Acompañamiento en tu compra' },
];

export const BenefitsBar = () => (
  <section className="py-4" style={{ backgroundColor: 'var(--kn-color-surface)' }}>
    <Container>
      <Row className="g-4">
        {benefits.map(benefit => (
          <Col key={benefit.title} xs={6} md={3}>
            <div className="d-flex flex-column align-items-center text-center h-100">
              <div
                className="d-flex align-items-center justify-content-center mb-3"
                style={{
                  width: '3rem',
                  height: '3rem',
                  borderRadius: '50%',
                  backgroundColor: 'var(--kn-color-accent)',
                  color: 'var(--kn-color-text-inverse)',
                }}
              >
                <FontAwesomeIcon icon={benefit.icon} />
              </div>
              <h3 className="h6 fw-semibold mb-1">{benefit.title}</h3>
              <p className="small mb-0" style={{ color: 'var(--kn-color-text-secondary)' }}>
                {benefit.description}
              </p>
            </div>
          </Col>
        ))}
      </Row>
    </Container>
  </section>
);

export default BenefitsBar;

import React, { useState } from 'react';
import { Button, Col, Container, Form, Row } from 'react-bootstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faEnvelope } from '@fortawesome/free-solid-svg-icons';
import { toast } from 'react-toastify';

const EMAIL_PATTERN = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

export const NewsletterCta = () => {
  const [email, setEmail] = useState('');

  const handleSubmit = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();

    if (!EMAIL_PATTERN.test(email.trim())) {
      toast.error('Ingresa un correo válido.');
      return;
    }

    toast.success('¡Gracias! Te contactaremos pronto.');
    setEmail('');
  };

  return (
    <section className="kn-newsletter py-5">
      <Container>
        <Row className="align-items-center g-4">
          <Col xs={12} md={6}>
            <h3 className="fw-bold mb-2">
              <FontAwesomeIcon icon={faEnvelope} className="me-2" />
              Recibe ofertas y lanzamientos
            </h3>
            <p className="mb-0">Suscríbete y entérate primero de descuentos, novedades y promociones exclusivas.</p>
          </Col>
          <Col xs={12} md={6}>
            <Form onSubmit={handleSubmit} className="d-flex flex-column flex-md-row gap-2">
              <Form.Control
                type="email"
                value={email}
                placeholder="Tu correo electrónico"
                aria-label="Tu correo electrónico"
                onChange={event => setEmail(event.target.value)}
              />
              <Button
                type="submit"
                className="fw-semibold"
                style={{
                  backgroundColor: 'var(--kn-color-accent)',
                  borderColor: 'var(--kn-color-accent)',
                  color: '#fff',
                }}
              >
                Suscribirme
              </Button>
            </Form>
          </Col>
        </Row>
      </Container>
    </section>
  );
};

export default NewsletterCta;

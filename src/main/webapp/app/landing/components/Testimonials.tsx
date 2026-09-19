import React from 'react';
import { Card, Col, Container, Row } from 'react-bootstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faStar } from '@fortawesome/free-solid-svg-icons';

interface Testimonial {
  nombre: string;
  ciudad: string;
  texto: string;
}

const testimonials: Testimonial[] = [
  {
    nombre: 'María Fernanda Ríos',
    ciudad: 'Bogotá',
    texto: 'Pedí unos tenis y llegaron en dos días. La calidad es excelente y el acompañamiento fue muy rápido.',
  },
  {
    nombre: 'Andrés Felipe Gómez',
    ciudad: 'Medellín',
    texto: 'Compré con contraentrega y todo el proceso fue claro. Volveré a pedir sin dudarlo.',
  },
  {
    nombre: 'Laura Sofía Martínez',
    ciudad: 'Cali',
    texto: 'Me encantó poder pagar con Nequi y seguir mi pedido. La talla fue exacta gracias a la guía.',
  },
];

export const Testimonials = () => (
  <section className="py-5">
    <Container>
      <h3 className="fw-bold text-center mb-4">Lo que dicen nuestros clientes</h3>
      <Row className="g-4">
        {testimonials.map(testimonial => (
          <Col key={testimonial.nombre} xs={12} md={4}>
            <Card className="h-100 border-0 shadow-sm">
              <Card.Body className="d-flex flex-column">
                <div className="mb-2" style={{ color: 'var(--kn-color-warning)' }} aria-label="5 de 5 estrellas">
                  {[0, 1, 2, 3, 4].map(index => (
                    <FontAwesomeIcon key={index} icon={faStar} className="me-1" />
                  ))}
                </div>
                <Card.Text className="flex-grow-1">{testimonial.texto}</Card.Text>
                <div className="fw-semibold">{testimonial.nombre}</div>
                <div className="small" style={{ color: 'var(--kn-color-text-secondary)' }}>
                  {testimonial.ciudad}
                </div>
                <div className="small" style={{ color: 'var(--kn-color-text-muted)' }}>
                  Compra verificada
                </div>
              </Card.Body>
            </Card>
          </Col>
        ))}
      </Row>
    </Container>
  </section>
);

export default Testimonials;

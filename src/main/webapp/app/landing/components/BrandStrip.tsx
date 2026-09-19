import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { Container } from 'react-bootstrap';
import { Link } from 'react-router';

interface MarcaResumen {
  id?: string;
  nombre?: string;
}

export const BrandStrip = () => {
  const [marcas, setMarcas] = useState<MarcaResumen[]>([]);

  useEffect(() => {
    axios
      .get<MarcaResumen[]>('api/marcas', { params: { size: 50, sort: 'nombre,asc' } })
      .then(response => setMarcas(response.data))
      .catch(() => setMarcas([]));
  }, []);

  if (marcas.length === 0) {
    return null;
  }

  return (
    <section className="py-4">
      <Container>
        <h5 className="fw-semibold text-center mb-3">Marcas aliadas</h5>
        <div className="d-flex flex-wrap justify-content-center gap-2 gap-md-3">
          {marcas.map(marca => (
            <Link
              key={marca.id ?? marca.nombre}
              to={`/buscar?q=${encodeURIComponent(marca.nombre ?? '')}`}
              className="fw-semibold text-decoration-none"
              style={{
                border: '1px solid var(--kn-color-border)',
                padding: '.4rem 1rem',
                borderRadius: 'var(--kn-border-radius-pill)',
                color: 'var(--kn-color-text)',
              }}
            >
              {marca.nombre}
            </Link>
          ))}
        </div>
      </Container>
    </section>
  );
};

export default BrandStrip;

import React from 'react';
import { Col, Container, Row } from 'react-bootstrap';
import { Link } from 'react-router';

import HeroBanner from 'app/landing/components/HeroBanner';
import ProductCard from 'app/landing/components/ProductCard';
import LoadingSpinner from 'app/landing/components/LoadingSpinner';
import ErrorAlert from 'app/landing/components/ErrorAlert';
import EmptyState from 'app/landing/components/EmptyState';
import { useCatalog } from 'app/landing/hooks/useCatalog';
import { buildImageUrl } from 'app/landing/utils/format';
import useCart from 'app/landing/hooks/useCart';

export const StoreHome = () => {
  const { addItem: onAddToCart } = useCart();
  const {
    categorias: rawCategorias,
    productos: rawProductos,
    loading,
    errorMessage,
  } = useCatalog({ page: 0, size: 12, sort: 'nombre,asc' });
  const categorias = rawCategorias ?? [];
  const productos = rawProductos ?? [];

  const destacados = productos.filter(p => p.destacado).slice(0, 8);
  const novedades = productos.slice(0, 8);

  return (
    <div className="kn-fade-in">
      <HeroBanner
        title="Nueva colección 2026"
        subtitle="Descubre las últimas tendencias en moda y retail. Envíos a todo Colombia."
        ctaText="Explorar ahora"
        ctaLink="/buscar"
      />

      {/* Categorías */}
      <section className="py-5">
        <Container>
          <h2 className="h3 fw-bold mb-4 text-center">Compra por categoría</h2>
          {categorias.length === 0 ? (
            <EmptyState title="No hay categorías disponibles" description="Vuelve más tarde para ver novedades." />
          ) : (
            <Row className="g-4">
              {categorias.slice(0, 4).map(categoria => (
                <Col key={categoria.id} xs={6} md={3}>
                  <Link to={`/categorias/${categoria.slug}`} className="text-decoration-none">
                    <div className="position-relative overflow-hidden rounded" style={{ aspectRatio: '1/1', backgroundColor: '#f8f9fa' }}>
                      {categoria.imagen ? (
                        <img
                          src={buildImageUrl(categoria.imagenContentType, categoria.imagen)}
                          alt={categoria.nombre}
                          className="w-100 h-100 object-fit-cover kn-img-transition"
                        />
                      ) : (
                        <div className="w-100 h-100 d-flex align-items-center justify-content-center text-muted">{categoria.nombre}</div>
                      )}
                      <div
                        className="position-absolute bottom-0 start-0 end-0 p-3"
                        style={{ background: 'linear-gradient(transparent, rgba(0,0,0,0.6))' }}
                      >
                        <h3 className="h6 text-white fw-bold mb-0">{categoria.nombre}</h3>
                      </div>
                    </div>
                  </Link>
                </Col>
              ))}
            </Row>
          )}
        </Container>
      </section>

      {/* Destacados */}
      <section className="py-5" style={{ backgroundColor: 'var(--kn-color-surface)' }}>
        <Container>
          <div className="d-flex justify-content-between align-items-center mb-4">
            <h2 className="h3 fw-bold mb-0">Destacados</h2>
            <Link to="/buscar" className="text-decoration-none small fw-semibold">
              Ver todo →
            </Link>
          </div>
          {loading ? (
            <LoadingSpinner />
          ) : errorMessage ? (
            <ErrorAlert message="No pudimos cargar los productos. Inténtalo de nuevo." />
          ) : destacados.length === 0 ? (
            <EmptyState title="Aún no hay productos destacados" />
          ) : (
            <Row className="g-4">
              {destacados.map(producto => (
                <Col key={producto.id} xs={6} md={4} lg={3}>
                  <ProductCard producto={producto} onAddToCart={onAddToCart} />
                </Col>
              ))}
            </Row>
          )}
        </Container>
      </section>

      {/* Novedades */}
      <section className="py-5">
        <Container>
          <div className="d-flex justify-content-between align-items-center mb-4">
            <h2 className="h3 fw-bold mb-0">Novedades</h2>
            <Link to="/buscar" className="text-decoration-none small fw-semibold">
              Ver todo →
            </Link>
          </div>
          {loading ? (
            <LoadingSpinner />
          ) : (
            <Row className="g-4">
              {novedades.map(producto => (
                <Col key={producto.id} xs={6} md={4} lg={3}>
                  <ProductCard producto={producto} onAddToCart={onAddToCart} />
                </Col>
              ))}
            </Row>
          )}
        </Container>
      </section>
    </div>
  );
};

export default StoreHome;

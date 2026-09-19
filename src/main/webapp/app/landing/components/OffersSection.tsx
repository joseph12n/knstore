import React, { useMemo } from 'react';
import { Col, Container, Row } from 'react-bootstrap';
import { Link } from 'react-router';

import { IProductoStorefront } from 'app/landing/model/storefront.model';
import type { AddItemResult } from 'app/landing/context/CartContext';
import { calculateDiscountPercent } from 'app/landing/utils/format';
import ProductCard from './ProductCard';
import LoadingSpinner from './LoadingSpinner';

interface OffersSectionProps {
  productos: IProductoStorefront[];
  onAddToCart?: (producto: IProductoStorefront) => Promise<AddItemResult>;
  loading?: boolean;
}

export const OffersSection = ({ productos, onAddToCart, loading = false }: OffersSectionProps) => {
  const ofertas = useMemo(() => {
    const conDescuento = productos
      .map(producto => ({
        producto,
        descuento: calculateDiscountPercent(producto.precio?.precioCompra, producto.precio?.precioVenta) ?? 0,
      }))
      .filter(item => item.descuento > 0)
      .sort((a, b) => b.descuento - a.descuento)
      .map(item => item.producto);

    if (conDescuento.length >= 4) {
      return conDescuento.slice(0, 8);
    }

    const seleccionados = new Set(conDescuento.map(producto => producto.id));
    const masBaratos = productos
      .filter(producto => !seleccionados.has(producto.id))
      .sort((a, b) => (a.precio?.precioVenta ?? 0) - (b.precio?.precioVenta ?? 0));

    return [...conDescuento, ...masBaratos].slice(0, 8);
  }, [productos]);

  if (loading) {
    return (
      <section className="py-5" style={{ backgroundColor: 'var(--kn-color-surface)' }}>
        <Container>
          <LoadingSpinner />
        </Container>
      </section>
    );
  }

  if (ofertas.length === 0) {
    return null;
  }

  return (
    <section className="py-5" style={{ backgroundColor: 'var(--kn-color-surface)' }}>
      <Container>
        <div className="d-flex justify-content-between align-items-center mb-4">
          <h2 className="h3 fw-bold mb-0">Ofertas</h2>
          <Link to="/buscar" className="text-decoration-none small fw-semibold">
            Ver todo →
          </Link>
        </div>
        <Row className="g-4">
          {ofertas.map(producto => (
            <Col key={producto.id ?? producto.slug} xs={6} md={4} lg={3}>
              <ProductCard producto={producto} onAddToCart={onAddToCart} />
            </Col>
          ))}
        </Row>
      </Container>
    </section>
  );
};

export default OffersSection;

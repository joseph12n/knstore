import React, { useMemo, useState } from 'react';
import { Badge, Card } from 'react-bootstrap';
import { Link, useNavigate } from 'react-router';
import { toast } from 'react-toastify';

import { IProductoStorefront } from 'app/landing/model/storefront.model';
import type { AddItemResult } from 'app/landing/context/CartContext';
import { buildImageUrl, calculateDiscountPercent, formatCOP, truncateText } from 'app/landing/utils/format';

interface ProductCardProps {
  producto: IProductoStorefront;
  onAddToCart?: (producto: IProductoStorefront) => Promise<AddItemResult>;
}

export const ProductCard = ({ producto, onAddToCart }: ProductCardProps) => {
  const navigate = useNavigate();
  const [isHovered, setIsHovered] = useState(false);

  const imagenPrincipal = useMemo(() => producto.imagenes?.find(img => img.esPrincipal) || producto.imagenes?.[0], [producto.imagenes]);

  const imagenSecundaria = useMemo(
    () => producto.imagenes?.find(img => img.id !== imagenPrincipal?.id),
    [producto.imagenes, imagenPrincipal],
  );

  const precioVenta = producto.precio?.precioVenta;
  // TODO backend: agregar campo precioAnterior/base si se requiere mostrar descuento real.
  const discountPercent = calculateDiscountPercent(producto.precio?.precioCompra, precioVenta);

  const handleAddToCart = async (e: React.MouseEvent) => {
    e.preventDefault();
    e.stopPropagation();

    const stock = producto.inventario?.stock ?? 0;
    if (stock <= 0) {
      toast.error('Producto sin stock disponible.');
      return;
    }

    if (!onAddToCart) return;
    const result = await onAddToCart(producto);
    if (result.ok) {
      toast.success('Producto añadido al carrito');
    } else if (result.reason === 'no-cuenta') {
      toast.warn('Completa tu perfil para poder agregar productos al carrito.');
      navigate('/mi-cuenta/perfil/editar');
    } else {
      toast.error('No se pudo agregar el producto al carrito.');
    }
  };

  return (
    <Card
      className="h-100 border-0 shadow-sm kn-product-card"
      onMouseEnter={() => setIsHovered(true)}
      onMouseLeave={() => setIsHovered(false)}
    >
      <Link to={`/productos/${producto.slug}`} className="text-decoration-none">
        <div className="position-relative overflow-hidden" style={{ aspectRatio: '3/4', backgroundColor: '#f8f9fa' }}>
          <img
            src={buildImageUrl(imagenPrincipal?.imagenContentType, imagenPrincipal?.imagen, undefined, imagenPrincipal?.imagenUrl)}
            alt={producto.nombre}
            className="w-100 h-100 object-fit-cover kn-img-transition"
            style={{
              opacity: isHovered && imagenSecundaria ? 0 : 1,
              position: 'absolute',
              inset: 0,
            }}
            loading="lazy"
          />
          {imagenSecundaria && (
            <img
              src={buildImageUrl(imagenSecundaria.imagenContentType, imagenSecundaria.imagen, undefined, imagenSecundaria.imagenUrl)}
              alt={`${producto.nombre} - vista alternativa`}
              className="w-100 h-100 object-fit-cover kn-img-transition"
              style={{
                opacity: isHovered ? 1 : 0,
                position: 'absolute',
                inset: 0,
              }}
              loading="lazy"
            />
          )}
          {producto.destacado && (
            <Badge bg="dark" className="position-absolute top-0 start-0 m-2 text-uppercase">
              Destacado
            </Badge>
          )}
          {discountPercent && discountPercent > 0 ? (
            <Badge bg="danger" className="position-absolute top-0 end-0 m-2">
              -{discountPercent}%
            </Badge>
          ) : null}
        </div>
      </Link>
      <Card.Body className="d-flex flex-column p-3">
        <div className="text-muted small text-uppercase mb-1">{producto.marca?.nombre || 'Knstore'}</div>
        <Link to={`/productos/${producto.slug}`} className="text-decoration-none stretched-link">
          <Card.Title className="h6 fw-semibold mb-2" style={{ minHeight: '2.5em' }}>
            {truncateText(producto.nombre, 55)}
          </Card.Title>
        </Link>
        <div className="mt-auto d-flex align-items-center justify-content-between">
          <span className="h5 mb-0 fw-bold">{formatCOP(precioVenta)}</span>
          {onAddToCart && (
            <button type="button" className="btn btn-primary btn-sm position-relative z-3" onClick={handleAddToCart}>
              Añadir
            </button>
          )}
        </div>
      </Card.Body>
    </Card>
  );
};

export default ProductCard;

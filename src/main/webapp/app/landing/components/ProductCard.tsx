import React, { useMemo, useState } from 'react';
import { Badge, Card } from 'react-bootstrap';
import { Link } from 'react-router';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faHeart } from '@fortawesome/free-solid-svg-icons';
import { toast } from 'react-toastify';

import { IProductoStorefront } from 'app/landing/model/storefront.model';
import { buildImageUrl, calculateDiscountPercent, formatCOP, truncateText } from 'app/landing/utils/format';

interface ProductCardProps {
  producto: IProductoStorefront;
  onAddToCart?: (producto: IProductoStorefront) => void;
}

export const ProductCard = ({ producto, onAddToCart }: ProductCardProps) => {
  const [isHovered, setIsHovered] = useState(false);

  const imagenPrincipal = useMemo(() => producto.imagenes?.find(img => img.esPrincipal) || producto.imagenes?.[0], [producto.imagenes]);

  const imagenSecundaria = useMemo(
    () => producto.imagenes?.find(img => img.id !== imagenPrincipal?.id),
    [producto.imagenes, imagenPrincipal],
  );

  const precioVenta = producto.precio?.precioVenta;
  // TODO backend: agregar campo precioAnterior/base si se requiere mostrar descuento real.
  const discountPercent = calculateDiscountPercent(producto.precio?.precioCompra, precioVenta);

  const handleAddToCart = (e: React.MouseEvent) => {
    e.preventDefault();
    e.stopPropagation();

    const stock = producto.inventario?.stock ?? 0;
    if (stock <= 0) {
      toast.error('Producto sin stock disponible.');
      return;
    }

    onAddToCart?.(producto);
    toast.success('Producto añadido al carrito');
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
            src={buildImageUrl(imagenPrincipal?.imagenContentType, imagenPrincipal?.imagen)}
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
              src={buildImageUrl(imagenSecundaria.imagenContentType, imagenSecundaria.imagen)}
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
          <button
            type="button"
            className="btn btn-light btn-sm position-absolute bottom-0 end-0 m-2 rounded-circle"
            aria-label="Añadir a favoritos"
            onClick={e => {
              e.preventDefault();
              e.stopPropagation();
            }}
          >
            <FontAwesomeIcon icon={faHeart} />
          </button>
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
            <button type="button" className="btn btn-primary btn-sm" onClick={handleAddToCart}>
              Añadir
            </button>
          )}
        </div>
      </Card.Body>
    </Card>
  );
};

export default ProductCard;

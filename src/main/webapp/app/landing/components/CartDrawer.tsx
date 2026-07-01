import React from 'react';
import { Button, Offcanvas, Stack } from 'react-bootstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faTrash, faShoppingBag, faArrowRight } from '@fortawesome/free-solid-svg-icons';
import { Link } from 'react-router';

import { buildImageUrl, formatCOP } from 'app/landing/utils/format';
import QuantitySelector from './QuantitySelector';
import EmptyState from './EmptyState';
import useCart from 'app/landing/hooks/useCart';

interface CartDrawerProps {
  show: boolean;
  onHide: () => void;
}

export const CartDrawer = ({ show, onHide }: CartDrawerProps) => {
  const { items: cartItems, total, updateQuantity, removeItem, clearCart } = useCart();

  const itemsCount = cartItems.reduce((sum, item) => sum + item.cantidad, 0);

  return (
    <Offcanvas show={show} onHide={onHide} placement="end" className="kn-cart-drawer" style={{ width: '420px', maxWidth: '100%' }}>
      <Offcanvas.Header className="kn-cart-drawer__header border-bottom">
        <div className="d-flex align-items-center gap-2">
          <FontAwesomeIcon icon={faShoppingBag} className="text-primary" />
          <Offcanvas.Title className="h5 fw-bold mb-0">Tu carrito</Offcanvas.Title>
          {itemsCount > 0 && (
            <span className="badge bg-light text-dark rounded-pill">
              {itemsCount} producto{itemsCount !== 1 ? 's' : ''}
            </span>
          )}
        </div>
        <button type="button" className="btn-close" aria-label="Cerrar" onClick={onHide} />
      </Offcanvas.Header>

      <Offcanvas.Body className="kn-cart-drawer__body d-flex flex-column p-0">
        {cartItems.length === 0 ? (
          <div className="p-4">
            <EmptyState
              title="Tu carrito está vacío"
              description="Explora el catálogo y encuentra lo que más te gusta."
              action={
                <Button variant="primary" onClick={onHide} as={Link as any} to="/">
                  Ver productos
                </Button>
              }
            />
          </div>
        ) : (
          <>
            <div className="flex-grow-1 overflow-auto p-3">
              <Stack gap={3}>
                {cartItems.map(item => {
                  const imagen = item.producto.imagenes?.find(img => img.esPrincipal) || item.producto.imagenes?.[0];
                  const subtotal = item.precioUnitario * item.cantidad;
                  return (
                    <div key={item.id} className="kn-cart-drawer__item d-flex gap-3 p-2 rounded">
                      <Link to={`/productos/${item.producto.slug}`} onClick={onHide} className="flex-shrink-0">
                        <img
                          src={buildImageUrl(imagen?.imagenContentType, imagen?.imagen)}
                          alt={item.producto.nombre}
                          className="rounded"
                          style={{ width: '80px', height: '100px', objectFit: 'cover' }}
                        />
                      </Link>
                      <div className="flex-grow-1 min-width-0">
                        <Link
                          to={`/productos/${item.producto.slug}`}
                          onClick={onHide}
                          className="fw-semibold d-block mb-1 text-truncate kn-cart-drawer__item-name"
                        >
                          {item.producto.nombre}
                        </Link>
                        <div className="text-muted small mb-2">
                          {item.producto.marca?.nombre}
                          {item.producto.color && ` · ${item.producto.color}`}
                          {item.producto.talla && ` · Talla ${item.producto.talla}`}
                        </div>
                        <div className="d-flex align-items-center justify-content-between gap-2">
                          <QuantitySelector
                            value={item.cantidad}
                            min={1}
                            max={item.producto.inventario?.stock || 99}
                            onChange={qty => updateQuantity(item.id!, qty)}
                            size="sm"
                          />
                          <div className="text-end flex-shrink-0">
                            <div className="fw-bold">{formatCOP(subtotal)}</div>
                            <div className="text-muted small">{formatCOP(item.precioUnitario)} c/u</div>
                          </div>
                        </div>
                      </div>
                      <button
                        type="button"
                        className="btn btn-link text-danger p-0 align-self-start flex-shrink-0 kn-cart-drawer__remove"
                        aria-label="Eliminar producto"
                        onClick={() => removeItem(item.id!)}
                      >
                        <FontAwesomeIcon icon={faTrash} />
                      </button>
                    </div>
                  );
                })}
              </Stack>

              <button type="button" className="btn btn-link text-muted p-0 mt-3 small" onClick={clearCart}>
                Vaciar carrito
              </button>
            </div>

            <div className="kn-cart-drawer__footer border-top p-3">
              <div className="d-flex justify-content-between align-items-center mb-3">
                <span className="text-muted">Total estimado</span>
                <span className="h5 fw-bold mb-0">{formatCOP(total)}</span>
              </div>
              <Button variant="accent" className="w-100 mb-2" onClick={onHide} as={Link as any} to="/checkout">
                Finalizar compra
                <FontAwesomeIcon icon={faArrowRight} className="ms-2" />
              </Button>
              <Button variant="outline-primary" className="w-100" onClick={onHide} as={Link as any} to="/carrito">
                Ver carrito detallado
              </Button>
            </div>
          </>
        )}
      </Offcanvas.Body>
    </Offcanvas>
  );
};

export default CartDrawer;

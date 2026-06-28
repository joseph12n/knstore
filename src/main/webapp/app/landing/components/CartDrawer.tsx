import React from 'react';
import { Button, Offcanvas, Stack } from 'react-bootstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faTrash } from '@fortawesome/free-solid-svg-icons';
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

  return (
    <Offcanvas show={show} onHide={onHide} placement="end" className="kn-cart-drawer">
      <Offcanvas.Header closeButton className="border-bottom">
        <Offcanvas.Title className="h5 fw-bold">Tu carrito</Offcanvas.Title>
      </Offcanvas.Header>
      <Offcanvas.Body className="d-flex flex-column p-0">
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
                  return (
                    <div key={item.id} className="d-flex gap-3 border-bottom pb-3">
                      <Link to={`/productos/${item.producto.slug}`} onClick={onHide}>
                        <img
                          src={buildImageUrl(imagen?.imagenContentType, imagen?.imagen)}
                          alt={item.producto.nombre}
                          className="rounded"
                          style={{ width: '80px', height: '100px', objectFit: 'cover' }}
                        />
                      </Link>
                      <div className="flex-grow-1">
                        <Link to={`/productos/${item.producto.slug}`} onClick={onHide} className="fw-semibold d-block mb-1">
                          {item.producto.nombre}
                        </Link>
                        <div className="text-muted small mb-2">
                          {item.producto.marca?.nombre}
                          {item.producto.color && ` · ${item.producto.color}`}
                          {item.producto.talla && ` · Talla ${item.producto.talla}`}
                        </div>
                        <div className="d-flex align-items-center justify-content-between">
                          <QuantitySelector
                            value={item.cantidad}
                            min={1}
                            max={item.producto.inventario?.stock || 99}
                            onChange={qty => updateQuantity(item.id!, qty)}
                            size="sm"
                          />
                          <div className="text-end">
                            <div className="fw-bold">{formatCOP(item.precioUnitario * item.cantidad)}</div>
                            <div className="text-muted small">{formatCOP(item.precioUnitario)} c/u</div>
                          </div>
                        </div>
                      </div>
                      <button
                        type="button"
                        className="btn btn-link text-danger p-0 align-self-start"
                        aria-label="Eliminar producto"
                        onClick={() => removeItem(item.id!)}
                      >
                        <FontAwesomeIcon icon={faTrash} />
                      </button>
                    </div>
                  );
                })}
              </Stack>
              <button type="button" className="btn btn-link text-muted p-0 mt-3" onClick={clearCart}>
                Vaciar carrito
              </button>
            </div>
            <div className="border-top p-3 bg-white">
              <div className="d-flex justify-content-between mb-3">
                <span className="fw-semibold">Total</span>
                <span className="h5 fw-bold mb-0">{formatCOP(total)}</span>
              </div>
              <Button variant="primary" className="w-100 mb-2" onClick={onHide} as={Link as any} to="/carrito">
                Ver carrito
              </Button>
              <Button variant="accent" className="w-100" onClick={onHide} as={Link as any} to="/checkout">
                Finalizar compra
              </Button>
            </div>
          </>
        )}
      </Offcanvas.Body>
    </Offcanvas>
  );
};

export default CartDrawer;

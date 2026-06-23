import React from 'react';
import { Button, Col, Container, Row, Table } from 'react-bootstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faTrash } from '@fortawesome/free-solid-svg-icons';
import { Link, useNavigate } from 'react-router';

import { CartItem } from 'app/storefront/model/storefront.model';
import { buildImageUrl, formatCOP } from 'app/storefront/utils/format';
import QuantitySelector from 'app/storefront/components/QuantitySelector';
import EmptyState from 'app/storefront/components/EmptyState';

interface CartPageProps {
  cartItems: CartItem[];
  onUpdateQuantity: (itemId: string, quantity: number) => void;
  onRemoveItem: (itemId: string) => void;
  onClearCart: () => void;
}

export const CartPage = ({ cartItems, onUpdateQuantity, onRemoveItem, onClearCart }: CartPageProps) => {
  const navigate = useNavigate();
  const items = cartItems ?? [];
  const subtotal = items.reduce((sum, item) => sum + item.precioUnitario * item.cantidad, 0);
  const envio = subtotal > 150000 ? 0 : 9900;
  const total = subtotal + envio;

  if (items.length === 0) {
    return (
      <Container className="py-5 kn-fade-in">
        <EmptyState
          title="Tu carrito está vacío"
          description="Agrega productos para continuar con tu compra."
          action={
            <Link to="/" className="btn btn-primary">
              Ver productos
            </Link>
          }
        />
      </Container>
    );
  }

  return (
    <Container className="py-4 kn-fade-in">
      <h1 className="h2 fw-bold mb-4">Carrito de compras</h1>
      <Row>
        <Col lg={8}>
          <div className="d-none d-md-block">
            <Table responsive className="align-middle">
              <thead>
                <tr>
                  <th>Producto</th>
                  <th className="text-center">Precio</th>
                  <th className="text-center">Cantidad</th>
                  <th className="text-end">Subtotal</th>
                  <th></th>
                </tr>
              </thead>
              <tbody>
                {items.map(item => {
                  const imagen = item.producto.imagenes?.find(img => img.esPrincipal) || item.producto.imagenes?.[0];
                  return (
                    <tr key={item.id}>
                      <td>
                        <div className="d-flex align-items-center gap-3">
                          <img
                            src={buildImageUrl(imagen?.imagenContentType, imagen?.imagen)}
                            alt={item.producto.nombre}
                            style={{ width: '80px', height: '100px', objectFit: 'cover' }}
                            className="rounded"
                          />
                          <div>
                            <Link to={`/productos/${item.producto.slug}`} className="fw-semibold d-block">
                              {item.producto.nombre}
                            </Link>
                            <small className="text-muted">
                              {item.producto.marca?.nombre}
                              {item.producto.color && ` · ${item.producto.color}`}
                              {item.producto.talla && ` · Talla ${item.producto.talla}`}
                            </small>
                          </div>
                        </div>
                      </td>
                      <td className="text-center">{formatCOP(item.precioUnitario)}</td>
                      <td className="text-center">
                        <QuantitySelector
                          value={item.cantidad}
                          min={1}
                          max={item.producto.inventario?.stock || 99}
                          onChange={qty => onUpdateQuantity(item.id!, qty)}
                          size="sm"
                        />
                      </td>
                      <td className="text-end fw-bold">{formatCOP(item.precioUnitario * item.cantidad)}</td>
                      <td className="text-end">
                        <button
                          type="button"
                          className="btn btn-link text-danger p-0"
                          onClick={() => onRemoveItem(item.id!)}
                          aria-label="Eliminar"
                        >
                          <FontAwesomeIcon icon={faTrash} />
                        </button>
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </Table>
          </div>

          <div className="d-md-none">
            {items.map(item => {
              const imagen = item.producto.imagenes?.find(img => img.esPrincipal) || item.producto.imagenes?.[0];
              return (
                <div key={item.id} className="d-flex gap-3 border-bottom py-3">
                  <img
                    src={buildImageUrl(imagen?.imagenContentType, imagen?.imagen)}
                    alt={item.producto.nombre}
                    style={{ width: '80px', height: '100px', objectFit: 'cover' }}
                    className="rounded"
                  />
                  <div className="flex-grow-1">
                    <Link to={`/productos/${item.producto.slug}`} className="fw-semibold d-block">
                      {item.producto.nombre}
                    </Link>
                    <div className="text-muted small mb-2">
                      {item.producto.marca?.nombre}
                      {item.producto.color && ` · ${item.producto.color}`}
                    </div>
                    <QuantitySelector
                      value={item.cantidad}
                      min={1}
                      max={item.producto.inventario?.stock || 99}
                      onChange={qty => onUpdateQuantity(item.id!, qty)}
                      size="sm"
                    />
                    <div className="d-flex justify-content-between align-items-center mt-2">
                      <span className="fw-bold">{formatCOP(item.precioUnitario * item.cantidad)}</span>
                      <button type="button" className="btn btn-link text-danger p-0" onClick={() => onRemoveItem(item.id!)}>
                        <FontAwesomeIcon icon={faTrash} />
                      </button>
                    </div>
                  </div>
                </div>
              );
            })}
          </div>

          <button type="button" className="btn btn-link text-muted p-0 mt-3" onClick={onClearCart}>
            Vaciar carrito
          </button>
        </Col>

        <Col lg={4}>
          <div className="p-4 rounded border" style={{ backgroundColor: 'var(--kn-color-surface)' }}>
            <h5 className="fw-bold mb-3">Resumen del pedido</h5>
            <div className="d-flex justify-content-between mb-2">
              <span>Subtotal</span>
              <span>{formatCOP(subtotal)}</span>
            </div>
            <div className="d-flex justify-content-between mb-2">
              <span>Envío</span>
              <span>{envio === 0 ? 'Gratis' : formatCOP(envio)}</span>
            </div>
            <hr />
            <div className="d-flex justify-content-between mb-4">
              <span className="fw-bold">Total</span>
              <span className="h4 fw-bold mb-0">{formatCOP(total)}</span>
            </div>
            <Button variant="accent" className="w-100 mb-2" onClick={() => navigate('/checkout')}>
              Finalizar compra
            </Button>
            <Button variant="outline-primary" className="w-100" as={Link as any} to="/">
              Seguir comprando
            </Button>
          </div>
        </Col>
      </Row>
    </Container>
  );
};

export default CartPage;

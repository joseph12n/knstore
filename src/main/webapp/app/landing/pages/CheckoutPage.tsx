import React, { useEffect, useMemo, useState } from 'react';
import { Button, Card, Col, Container, Form, Row } from 'react-bootstrap';
import { Link, useNavigate } from 'react-router';
import { toast } from 'react-toastify';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getSession } from 'app/shared/reducers/authentication';
import { createEntity as createPedido } from 'app/entities/pedido/pedido.reducer';
import { createEntity as createPago } from 'app/entities/pago/pago.reducer';
import { getEntities as getDireccions } from 'app/entities/direccion/direccion.reducer';
import { getEntities as getCuentas } from 'app/entities/cuenta/cuenta.reducer';
import { createEntity as createItemPedido } from 'app/entities/item-pedido/item-pedido.reducer';
import { CHECKOUT_STEPS, PAYMENT_METHODS, SHIPPING_METHODS } from 'app/landing/utils/constants';
import { formatCOP } from 'app/landing/utils/format';
import CheckoutStepper from 'app/landing/components/CheckoutStepper';
import AddressCard from 'app/landing/components/AddressCard';
import LoadingSpinner from 'app/landing/components/LoadingSpinner';
import useCart from 'app/landing/hooks/useCart';

export const CheckoutPage = () => {
  const navigate = useNavigate();
  const dispatch = useAppDispatch();
  const { items, clearCart: onCheckoutComplete } = useCart();
  const [step, setStep] = useState(0);
  const [selectedDireccionId, setSelectedDireccionId] = useState('');
  const [selectedEnvio, setSelectedEnvio] = useState('ESTANDAR');
  const [selectedPago, setSelectedPago] = useState('PSE');
  const [notas, setNotas] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);

  const account = useAppSelector(state => state.authentication.account);
  const direcciones = useAppSelector(state => state.direccion.entities) ?? [];
  const cuentas = useAppSelector(state => state.cuenta.entities) ?? [];
  const loadingDirecciones = useAppSelector(state => state.direccion.loading);

  useEffect(() => {
    dispatch(getSession());
    dispatch(getDireccions({ page: 0, size: 100, sort: 'activo,desc' }));
    dispatch(getCuentas({ page: 0, size: 100, sort: 'primerNombre,asc' }));
  }, [dispatch]);

  const cuentaUsuario = useMemo(() => cuentas.find(c => c.user?.login === account.login), [cuentas, account.login]);

  useEffect(() => {
    if (!loadingDirecciones && !cuentaUsuario) {
      toast.info('Completa tu perfil para poder finalizar la compra.');
      navigate('/cuenta/perfil');
    }
  }, [loadingDirecciones, cuentaUsuario, navigate]);

  const direccionesUsuario = useMemo(() => direcciones.filter(d => d.cuenta?.id === cuentaUsuario?.id), [direcciones, cuentaUsuario]);

  const subtotal = items.reduce((sum, item) => sum + item.precioUnitario * item.cantidad, 0);
  const costoEnvio = useMemo(() => SHIPPING_METHODS.find(s => s.key === selectedEnvio)?.cost || 0, [selectedEnvio]);
  const total = subtotal + costoEnvio;

  useEffect(() => {
    const defaultAddress = direccionesUsuario.find(d => d.activo) || direccionesUsuario[0];
    if (defaultAddress && !selectedDireccionId) {
      setSelectedDireccionId(defaultAddress.id!);
    }
  }, [direccionesUsuario, selectedDireccionId]);

  if (items.length === 0) {
    return (
      <Container className="py-5 text-center kn-fade-in">
        <h2 className="h3 fw-bold mb-3">Tu carrito está vacío</h2>
        <p className="text-muted">Agrega productos antes de continuar con el pago.</p>
        <Button variant="primary" as={Link as any} to="/">
          Ver productos
        </Button>
      </Container>
    );
  }

  if (!cuentaUsuario) {
    return (
      <Container className="py-5 text-center kn-fade-in">
        <h2 className="h3 fw-bold mb-3">Completa tu perfil</h2>
        <p className="text-muted">Necesitas un perfil de cliente para continuar con la compra.</p>
        <Button variant="primary" as={Link as any} to="/cuenta/perfil">
          Completar perfil
        </Button>
      </Container>
    );
  }

  const handleNext = () => {
    if (step === 0 && !selectedDireccionId) {
      toast.error('Selecciona una dirección de envío.');
      return;
    }
    setStep(prev => Math.min(prev + 1, CHECKOUT_STEPS.length - 1));
  };

  const handleBack = () => setStep(prev => Math.max(prev - 1, 0));

  const handleSubmit = async () => {
    if (!cuentaUsuario) {
      toast.error('No se encontró tu perfil de cliente. Completa tu cuenta.');
      return;
    }

    setIsSubmitting(true);

    try {
      // TODO backend: reemplazar por endpoint atómico de checkout cuando esté disponible.
      // TODO backend: integrar callback de pasarela de pagos para cambiar estado a APROVED/REJECTED (RF-055).
      const pedidoResult = await dispatch(
        createPedido({
          estado: 'PENDING',
          subtotal,
          costoEnvio,
          total,
          notasCliente: notas,
          direccion: { id: selectedDireccionId },
          cuenta: { id: cuentaUsuario.id },
        }),
      );

      const pedidoCreado = (pedidoResult.payload as any)?.data;

      if (!pedidoCreado?.id) {
        throw new Error('No se pudo crear el pedido');
      }

      // Crear items del pedido
      for (const item of items) {
        await dispatch(
          createItemPedido({
            cantidad: item.cantidad,
            precioUnitario: item.precioUnitario,
            subtotal: item.precioUnitario * item.cantidad,
            nombreProducto: item.producto.nombre,
            slugProducto: item.producto.slug,
            marcaProducto: item.producto.marca?.nombre,
            skuProducto: item.producto.sku,
            colorProducto: item.producto.color,
            tallaProducto: item.producto.talla,
            pedido: { id: pedidoCreado.id },
            producto: { id: item.producto.id },
          }),
        );
      }

      // Crear pago inicial
      await dispatch(
        createPago({
          metodoPago: selectedPago as any,
          estado: 'PENDING' as any,
          monto: total,
          pedido: { id: pedidoCreado.id },
        }),
      );

      toast.success('¡Pedido creado exitosamente!');
      onCheckoutComplete();
      navigate(`/cuenta/pedidos/${pedidoCreado.id}`);
    } catch {
      toast.error('No pudimos procesar tu pedido. Inténtalo de nuevo.');
    } finally {
      setIsSubmitting(false);
    }
  };

  const renderStepContent = () => {
    switch (step) {
      case 0:
        return (
          <div>
            <h5 className="fw-bold mb-3">Selecciona una dirección de envío</h5>
            {loadingDirecciones ? (
              <LoadingSpinner />
            ) : direccionesUsuario.length === 0 ? (
              <Card className="p-4 text-center">
                <p className="text-muted">No tienes direcciones guardadas.</p>
                <Button variant="primary" onClick={() => navigate('/cuenta/direcciones')}>
                  Agregar dirección
                </Button>
              </Card>
            ) : (
              <Row className="g-3">
                {direccionesUsuario.map(d => (
                  <Col md={6} key={d.id}>
                    <AddressCard
                      direccion={d}
                      isDefault={d.activo}
                      selectable
                      selected={selectedDireccionId === d.id}
                      onSelect={() => setSelectedDireccionId(d.id!)}
                    />
                  </Col>
                ))}
              </Row>
            )}
          </div>
        );
      case 1:
        return (
          <div>
            <h5 className="fw-bold mb-3">Método de envío</h5>
            <Row className="g-3">
              {SHIPPING_METHODS.map(method => (
                <Col md={6} key={method.key}>
                  <Card
                    className={`h-100 ${selectedEnvio === method.key ? 'border-primary' : ''}`}
                    onClick={() => setSelectedEnvio(method.key)}
                    style={{ cursor: 'pointer' }}
                  >
                    <Card.Body>
                      <div className="d-flex justify-content-between align-items-start">
                        <div>
                          <Form.Check
                            type="radio"
                            name="shipping"
                            id={`shipping-${method.key}`}
                            checked={selectedEnvio === method.key}
                            onChange={() => setSelectedEnvio(method.key)}
                            label={<span className="fw-semibold">{method.label}</span>}
                          />
                          <p className="text-muted small mb-0 mt-1">{method.description}</p>
                        </div>
                        <span className="fw-bold">{method.cost === 0 ? 'Gratis' : formatCOP(method.cost)}</span>
                      </div>
                    </Card.Body>
                  </Card>
                </Col>
              ))}
            </Row>
          </div>
        );
      case 2:
        return (
          <div>
            <h5 className="fw-bold mb-3">Método de pago</h5>
            <Row className="g-3">
              {PAYMENT_METHODS.map(method => (
                <Col md={6} key={method.key}>
                  <Card
                    className={`h-100 ${selectedPago === method.key ? 'border-primary' : ''}`}
                    onClick={() => setSelectedPago(method.key)}
                    style={{ cursor: 'pointer' }}
                  >
                    <Card.Body>
                      <Form.Check
                        type="radio"
                        name="payment"
                        id={`payment-${method.key}`}
                        checked={selectedPago === method.key}
                        onChange={() => setSelectedPago(method.key)}
                        label={<span className="fw-semibold">{method.label}</span>}
                      />
                    </Card.Body>
                  </Card>
                </Col>
              ))}
            </Row>
            <Form.Group className="mt-4">
              <Form.Label>Notas adicionales</Form.Label>
              <Form.Control
                as="textarea"
                rows={3}
                placeholder="Instrucciones de entrega, referencias, etc."
                value={notas}
                onChange={e => setNotas(e.target.value)}
              />
            </Form.Group>
          </div>
        );
      case 3:
        return (
          <div>
            <h5 className="fw-bold mb-3">Confirmación</h5>
            <Card className="mb-3">
              <Card.Body>
                <h6 className="fw-bold">Resumen</h6>
                <div className="d-flex justify-content-between mb-1">
                  <span>Subtotal</span>
                  <span>{formatCOP(subtotal)}</span>
                </div>
                <div className="d-flex justify-content-between mb-1">
                  <span>Envío ({SHIPPING_METHODS.find(s => s.key === selectedEnvio)?.label})</span>
                  <span>{costoEnvio === 0 ? 'Gratis' : formatCOP(costoEnvio)}</span>
                </div>
                <hr />
                <div className="d-flex justify-content-between">
                  <span className="fw-bold">Total a pagar</span>
                  <span className="h4 fw-bold">{formatCOP(total)}</span>
                </div>
                <div className="mt-3 small text-muted">
                  <div>
                    <strong>Método de pago:</strong> {PAYMENT_METHODS.find(p => p.key === selectedPago)?.label}
                  </div>
                  <div>
                    <strong>Dirección:</strong> {direccionesUsuario.find(d => d.id === selectedDireccionId)?.direccion}
                  </div>
                </div>
              </Card.Body>
            </Card>
            <p className="small text-muted">
              Al confirmar, crearás tu pedido. Serás redirigido al detalle para completar el pago cuando la pasarela esté integrada.
            </p>
          </div>
        );
      default:
        return null;
    }
  };

  return (
    <Container className="py-4 kn-fade-in" style={{ maxWidth: '900px' }}>
      <h1 className="h2 fw-bold mb-4 text-center">Finalizar compra</h1>
      <CheckoutStepper currentStep={step} />
      <Card className="p-4">
        {renderStepContent()}
        <div className="d-grid gap-2 d-sm-flex justify-content-sm-between mt-4">
          <Button variant="outline-secondary" onClick={handleBack} disabled={step === 0 || isSubmitting}>
            Atrás
          </Button>
          {step < CHECKOUT_STEPS.length - 1 ? (
            <Button variant="primary" onClick={handleNext}>
              Continuar
            </Button>
          ) : (
            <Button variant="accent" onClick={handleSubmit} disabled={isSubmitting}>
              {isSubmitting ? 'Procesando...' : 'Confirmar pedido'}
            </Button>
          )}
        </div>
      </Card>
    </Container>
  );
};

export default CheckoutPage;

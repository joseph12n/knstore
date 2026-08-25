import React, { useEffect, useMemo, useRef, useState } from 'react';
import { Button, Card, Col, Container, Form, Row } from 'react-bootstrap';
import { Link, useNavigate } from 'react-router';
import { toast } from 'react-toastify';
import axios from 'axios';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getSession } from 'app/shared/reducers/authentication';
import { getEntities as getDireccions } from 'app/entities/direccion/direccion.reducer';
import { getCuentaByLogin, reset as resetCuenta } from 'app/entities/cuenta/cuenta.reducer';
import { CHECKOUT_STEPS, FREE_SHIPPING_MESSAGE, PAYMENT_METHODS, SHIPPING_METHODS } from 'app/landing/utils/constants';
import { formatCOP } from 'app/landing/utils/format';
import CheckoutStepper from 'app/landing/components/CheckoutStepper';
import AddressCard from 'app/landing/components/AddressCard';
import LoadingSpinner from 'app/landing/components/LoadingSpinner';
import useCart from 'app/landing/hooks/useCart';
import useCuentaActual from 'app/landing/hooks/useCuentaActual';
import { buildCheckoutPayload, checkout, getPreview, iniciarPago, CheckoutPreview } from 'app/landing/services/checkout.service';
import { getApiErrorMessage } from 'app/landing/utils/apiError';

export const CheckoutPage = () => {
  const navigate = useNavigate();
  const dispatch = useAppDispatch();
  const { items, refresh: onCheckoutComplete } = useCart();
  const { account, cuenta } = useCuentaActual();
  const [step, setStep] = useState(0);
  const [selectedDireccionId, setSelectedDireccionId] = useState('');
  const [selectedEnvio, setSelectedEnvio] = useState('ESTANDAR');
  const [selectedPago, setSelectedPago] = useState('NEQUI');
  const [notas, setNotas] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [previewLoading, setPreviewLoading] = useState(false);
  const [previewError, setPreviewError] = useState<string | null>(null);
  const [preview, setPreview] = useState<CheckoutPreview | null>(null);

  const notasRef = useRef('');
  notasRef.current = notas;
  const previewRequestRef = useRef(0);

  const direcciones = useAppSelector(state => state.direccion.entities) ?? [];
  const loadingDirecciones = useAppSelector(state => state.direccion.loading);

  useEffect(() => {
    dispatch(getSession());
    dispatch(getDireccions({ page: 0, size: 100, sort: 'activo,desc' }));
    if (account.login) {
      dispatch(getCuentaByLogin(account.login));
    }
    return () => {
      dispatch(resetCuenta());
    };
  }, [dispatch, account.login]);

  useEffect(() => {
    if (!loadingDirecciones && !cuenta) {
      toast.info('Completa tu perfil para poder finalizar la compra.');
      navigate('/mi-cuenta/perfil');
    }
  }, [loadingDirecciones, cuenta, navigate]);

  const direccionesUsuario = useMemo(() => direcciones.filter(d => d.cuenta?.id === cuenta?.id), [direcciones, cuenta]);

  useEffect(() => {
    const defaultAddress = direccionesUsuario.find(d => d.activo) || direccionesUsuario[0];
    if (defaultAddress && !selectedDireccionId) {
      setSelectedDireccionId(defaultAddress.id!);
    }
  }, [direccionesUsuario, selectedDireccionId]);

  useEffect(() => {
    if (step !== 3) {
      setPreview(null);
      setPreviewError(null);
      return;
    }

    if (!cuenta || !selectedDireccionId || items.length === 0) {
      return;
    }

    // AbortController + token de request: descarta respuestas fuera de orden
    // y cancela el request anterior al cambiar la seleccion.
    const controller = new AbortController();
    const requestId = ++previewRequestRef.current;

    const loadPreview = async () => {
      setPreviewLoading(true);
      setPreviewError(null);
      try {
        const payload = buildCheckoutPayload(items, selectedDireccionId, selectedPago, selectedEnvio, notasRef.current);
        const data = await getPreview(payload, controller.signal);
        if (requestId !== previewRequestRef.current) {
          return;
        }
        setPreview(data);
      } catch (error) {
        if (axios.isCancel(error) || requestId !== previewRequestRef.current) {
          return;
        }
        setPreviewError(`No pudimos calcular los totales: ${getApiErrorMessage(error)}`);
      } finally {
        if (requestId === previewRequestRef.current) {
          setPreviewLoading(false);
        }
      }
    };

    void loadPreview();
    return () => controller.abort();
    // notas se lee via ref para no disparar un preview por cada tecla escrita.
  }, [step, selectedDireccionId, selectedEnvio, selectedPago, items, cuenta]);

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

  if (!cuenta) {
    return (
      <Container className="py-5 text-center kn-fade-in">
        <h2 className="h3 fw-bold mb-3">Completa tu perfil</h2>
        <p className="text-muted">Necesitas un perfil de cliente para continuar con la compra.</p>
        <Button variant="primary" as={Link as any} to="/mi-cuenta/perfil">
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
    if (!cuenta) {
      toast.error('No se encontró tu perfil de cliente. Completa tu cuenta.');
      return;
    }

    setIsSubmitting(true);

    try {
      const payload = buildCheckoutPayload(items, selectedDireccionId, selectedPago, selectedEnvio, notasRef.current);
      const result = await checkout(payload);
      const pedidoCreado = result.pedido;

      if (!pedidoCreado?.id) {
        throw new Error('No se pudo crear el pedido');
      }

      // RF-076: el pago nace dentro del checkout (pasarela simulada), así que ya
      // no hace falta una segunda llamada. Si el servidor aún no devuelve el pago,
      // se usa iniciarPago solo como compatibilidad.
      let pago = result.pago;
      if (!pago) {
        pago = await iniciarPago(pedidoCreado.id);
      }

      // El backend ya vacio el carrito del servidor al crear el pedido:
      // se sincroniza el carrito local en ambas ramas para evitar duplicados.
      await onCheckoutComplete();

      if (pago.estado === 'APPROVED') {
        toast.success('¡Pago aprobado y pedido creado exitosamente!');
        navigate(`/mi-cuenta/pedidos/${pedidoCreado.id}`);
      } else {
        // Defensa: con la pasarela simulada el pago siempre queda APPROVED. Esta rama
        // quedara como gestion de rechazos para la pasarela real futura.
        toast.error(pago.descripcionRespuesta || 'El pago no pudo ser aprobado.');
        navigate(`/mi-cuenta/pedidos/${pedidoCreado.id}`);
      }
    } catch (error) {
      toast.error(`No pudimos procesar tu pedido: ${getApiErrorMessage(error)}`);
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
                <Button variant="primary" onClick={() => navigate('/mi-cuenta/direcciones')}>
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
            <p className="text-muted small mb-3">{FREE_SHIPPING_MESSAGE}</p>
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
            {previewLoading || !preview ? (
              <LoadingSpinner />
            ) : previewError ? (
              <div className="alert alert-danger">{previewError}</div>
            ) : (
              <Card className="mb-3">
                <Card.Body>
                  <h6 className="fw-bold">Resumen</h6>
                  <div className="d-flex justify-content-between mb-1">
                    <span>Subtotal</span>
                    <span>{formatCOP(preview.subtotal)}</span>
                  </div>
                  <div className="d-flex justify-content-between mb-1">
                    <span>Envío ({SHIPPING_METHODS.find(s => s.key === selectedEnvio)?.label})</span>
                    <span>{preview.envio === 0 ? 'Gratis' : formatCOP(preview.envio)}</span>
                  </div>
                  <div className="d-flex justify-content-between mb-1">
                    <span>IVA</span>
                    <span>{formatCOP(preview.iva)}</span>
                  </div>
                  <hr />
                  <div className="d-flex justify-content-between">
                    <span className="fw-bold">Total a pagar</span>
                    <span className="h4 fw-bold">{formatCOP(preview.total)}</span>
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
            )}
            <p className="small text-muted">
              Al confirmar, se procesará tu pago con la pasarela y se creará tu pedido con envío y factura.
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
            <Button variant="accent" onClick={handleSubmit} disabled={isSubmitting || previewLoading || !preview || !!previewError}>
              {isSubmitting ? 'Procesando...' : 'Confirmar pedido'}
            </Button>
          )}
        </div>
      </Card>
    </Container>
  );
};

export default CheckoutPage;

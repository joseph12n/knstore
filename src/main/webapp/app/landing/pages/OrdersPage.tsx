import React, { useEffect, useMemo, useState } from 'react';

import { Link } from 'react-router';
import { Badge, Button, Card, Tabs, Tab } from 'react-bootstrap';
import { toast } from 'react-toastify';
import axios from 'axios';
import dayjs from 'dayjs';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities as getPedidos } from 'app/entities/pedido/pedido.reducer';
import { getEntities as getPagos } from 'app/entities/pago/pago.reducer';
import { getEntities as getEnvios } from 'app/entities/envio/envio.reducer';
import { getEntities as getFacturas } from 'app/entities/factura/factura.reducer';
import useCuentaActual from 'app/landing/hooks/useCuentaActual';
import OrderCard from 'app/landing/components/OrderCard';
import LoadingSpinner from 'app/landing/components/LoadingSpinner';
import EmptyState from 'app/landing/components/EmptyState';
import OrderCancelModal from 'app/landing/components/OrderCancelModal';
import Pagination from 'app/landing/components/Pagination';
import { getApiErrorMessage } from 'app/landing/utils/apiError';
import { PAYMENT_STATUS_LABELS, SHIPPING_STATUS_LABELS } from 'app/landing/utils/constants';
import { formatCOP } from 'app/landing/utils/format';
import { downloadFacturaPdf } from 'app/landing/utils/invoice';

const ITEMS_PER_PAGE = 10;

const pagoBadgeVariant = (estado?: string) => {
  if (estado === 'APPROVED') return 'success';
  if (estado === 'REFUNDED') return 'secondary';
  if (estado === 'REJECTED' || estado === 'CANCELLED' || estado === 'EXPIRED') return 'danger';
  return 'warning';
};

const envioBadgeVariant = (estado?: string) => {
  if (estado === 'DELIVERED') return 'success';
  if (estado === 'PENDING') return 'warning';
  if (estado === 'LOST' || estado === 'RETURNED') return 'danger';
  return 'info';
};

export const OrdersPage = () => {
  const dispatch = useAppDispatch();
  const { account } = useCuentaActual();
  const pedidos = useAppSelector(state => state.pedido.entities) ?? [];
  const pagos = useAppSelector(state => state.pago.entities) ?? [];
  const envios = useAppSelector(state => state.envio.entities) ?? [];
  const facturas = useAppSelector(state => state.factura.entities) ?? [];
  const totalItems = useAppSelector(state => state.pedido.totalItems ?? 0);
  const loading = useAppSelector(state => state.pedido.loading);

  const [activePage, setActivePage] = useState(1);
  const [activeTab, setActiveTab] = useState('pedidos');
  const [pedidoToCancel, setPedidoToCancel] = useState<string | undefined>();
  const [isCancelling, setIsCancelling] = useState(false);
  const [downloadingFactura, setDownloadingFactura] = useState<string | undefined>();

  useEffect(() => {
    // El backend pagina y filtra por la cuenta del cliente autenticado.
    dispatch(getPedidos({ page: activePage - 1, size: ITEMS_PER_PAGE, sort: 'numeroPedido,desc' }));
    // Listados propios: envios, facturas y pagos reflejan sus estados reales.
    dispatch(getPagos({ page: 0, size: 100, sort: 'id,desc' }));
    dispatch(getEnvios({ page: 0, size: 100, sort: 'id,desc' }));
    dispatch(getFacturas({ page: 0, size: 100, sort: 'id,desc' }));
  }, [dispatch, activePage, account.login]);

  const pedidoPago = (pedidoId: string) => pagos.find(p => p.pedido?.id === pedidoId);

  const selectedPedido = useMemo(() => pedidos.find(p => p.id === pedidoToCancel), [pedidos, pedidoToCancel]);
  const selectedPago = useMemo(() => (selectedPedido ? pedidoPago(selectedPedido.id) : undefined), [selectedPedido, pagos]);

  const recargarListados = () => {
    dispatch(getPedidos({ page: activePage - 1, size: ITEMS_PER_PAGE, sort: 'numeroPedido,desc' }));
    dispatch(getPagos({ page: 0, size: 100, sort: 'id,desc' }));
    dispatch(getEnvios({ page: 0, size: 100, sort: 'id,desc' }));
    dispatch(getFacturas({ page: 0, size: 100, sort: 'id,desc' }));
  };

  const handleCancel = async (motivo: string) => {
    if (!pedidoToCancel) return;
    setIsCancelling(true);
    try {
      await axios.post(`api/pedidos/${pedidoToCancel}/cancelar`, { motivo });
      toast.success(
        selectedPago?.estado === 'APPROVED' ? 'Pedido cancelado y reembolso solicitado correctamente' : 'Pedido cancelado correctamente',
      );
      recargarListados();
    } catch (error) {
      toast.error(getApiErrorMessage(error, 'No pudimos cancelar el pedido. Inténtalo de nuevo.'));
    } finally {
      setIsCancelling(false);
      setPedidoToCancel(undefined);
    }
  };

  const handleDownloadFactura = async (factura: { id?: string; prefijo?: string | null }) => {
    if (!factura.id) return;
    setDownloadingFactura(factura.id);
    try {
      await downloadFacturaPdf(factura.id, factura.prefijo);
    } catch {
      window.location.href = `api/facturas/${factura.id}/download`;
    } finally {
      setDownloadingFactura(undefined);
    }
  };

  return (
    <div className="kn-fade-in">
      <h1 className="h2 fw-bold mb-4">Mis pedidos</h1>
      <Link to="/mi-cuenta" className="text-muted small d-block mb-4">
        ← Volver a mi cuenta
      </Link>

      <Tabs activeKey={activeTab} onSelect={key => key && setActiveTab(key)} className="mb-4">
        <Tab eventKey="pedidos" title={`Pedidos (${pedidos.length})`}>
          {loading ? (
            <LoadingSpinner fullScreen />
          ) : pedidos.length === 0 ? (
            <EmptyState
              title="Aún no tienes pedidos"
              description="Cuando realices una compra, podrás verla aquí."
              action={
                <Link to="/" className="btn btn-primary">
                  Ver productos
                </Link>
              }
            />
          ) : (
            <div style={{ maxWidth: '800px' }}>
              {pedidos.map(pedido => (
                <OrderCard key={pedido.id} pedido={pedido} pago={pedidoPago(pedido.id)} onCancel={() => setPedidoToCancel(pedido.id)} />
              ))}
              <Pagination activePage={activePage} itemsPerPage={ITEMS_PER_PAGE} totalItems={totalItems} onPageChange={setActivePage} />
            </div>
          )}
        </Tab>

        <Tab eventKey="envios" title={`Envíos (${envios.length})`}>
          <div style={{ maxWidth: '800px' }}>
            {envios.length === 0 ? (
              <EmptyState title="No tienes envíos" description="Tus envíos aparecerán aquí junto a cada pedido." />
            ) : (
              envios.map(envio => {
                const estado = envio.estado || 'PENDIENTE';
                return (
                  <Card key={envio.id} className="mb-3">
                    <Card.Body>
                      <div className="d-flex flex-wrap justify-content-between align-items-start gap-2 mb-2">
                        <div className="fw-bold">Envío #{envio.numeroRastreo || envio.id}</div>
                        <Badge bg={envioBadgeVariant(estado)}>{SHIPPING_STATUS_LABELS[estado] || estado}</Badge>
                      </div>
                      <div className="text-muted small">
                        Transportadora: <strong>{envio.transportadora || 'Por asignar'}</strong> · Número de rastreo:{' '}
                        {envio.numeroRastreo || 'Pendiente'}
                        {envio.fechaEntregaEstimada ? ` · Entrega estimada: ${dayjs(envio.fechaEntregaEstimada).format('DD/MM/YYYY')}` : ''}
                      </div>
                      {envio.urlRastreo && (
                        <a href={envio.urlRastreo} target="_blank" rel="noopener noreferrer" className="btn btn-outline-info btn-sm mt-3">
                          Rastrear envío
                        </a>
                      )}
                    </Card.Body>
                  </Card>
                );
              })
            )}
          </div>
        </Tab>

        <Tab eventKey="facturas" title={`Facturas (${facturas.length})`}>
          <div style={{ maxWidth: '800px' }}>
            {facturas.length === 0 ? (
              <EmptyState title="No tienes facturas" description="Tus facturas aparecerán aquí después de cada compra." />
            ) : (
              facturas.map(factura => (
                <Card key={factura.id} className="mb-3">
                  <Card.Body>
                    <div className="d-flex flex-wrap justify-content-between align-items-start gap-2 mb-2">
                      <div className="fw-bold">
                        Factura {factura.prefijo || 'FE'}-{factura.id?.slice(-6).toUpperCase()}
                      </div>
                      {factura.enviada && <Badge bg="info">Emisión por correo: enviada</Badge>}
                    </div>
                    <div className="text-muted small">
                      Fecha de emisión: {factura.fechaEmision ? dayjs(factura.fechaEmision).format('DD/MM/YYYY HH:mm') : '-'} · Total:{' '}
                      <strong>{formatCOP(factura.total)}</strong>
                    </div>
                    <Button
                      variant="outline-primary"
                      size="sm"
                      className="mt-3"
                      onClick={() => handleDownloadFactura(factura)}
                      disabled={downloadingFactura === factura.id}
                    >
                      {downloadingFactura === factura.id ? 'Descargando...' : 'Descargar factura'}
                    </Button>
                  </Card.Body>
                </Card>
              ))
            )}
          </div>
        </Tab>

        <Tab eventKey="pagos" title={`Pagos (${pagos.length})`}>
          <div style={{ maxWidth: '800px' }}>
            {pagos.length === 0 ? (
              <EmptyState title="No tienes pagos" description="Tus pagos aparecerán aquí junto a cada pedido." />
            ) : (
              pagos.map(pago => {
                const estado = pago.estado || 'PENDIENTE';
                return (
                  <Card key={pago.id} className="mb-3">
                    <Card.Body>
                      <div className="d-flex flex-wrap justify-content-between align-items-start gap-2 mb-2">
                        <div className="fw-bold">{pago.metodoPago || 'Pago'}</div>
                        <Badge bg={pagoBadgeVariant(estado)}>{PAYMENT_STATUS_LABELS[estado] || estado}</Badge>
                      </div>
                      <div className="text-muted small">
                        Monto: <strong>{formatCOP(pago.monto)}</strong>
                        {pago.fechaPago ? ` · Fecha: ${dayjs(pago.fechaPago).format('DD/MM/YYYY HH:mm')}` : ''}
                        {pago.descripcionRespuesta ? ` · ${pago.descripcionRespuesta}` : ''}
                      </div>
                    </Card.Body>
                  </Card>
                );
              })
            )}
          </div>
        </Tab>
      </Tabs>

      <OrderCancelModal
        show={!!pedidoToCancel}
        onHide={() => setPedidoToCancel(undefined)}
        onConfirm={handleCancel}
        isSubmitting={isCancelling}
        numeroPedido={selectedPedido?.numeroPedido}
        reembolsoIncluido={selectedPago?.estado === 'APPROVED'}
      />
    </div>
  );
};

export default OrdersPage;

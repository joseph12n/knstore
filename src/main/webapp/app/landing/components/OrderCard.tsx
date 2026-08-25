import React from 'react';
import { Badge, Button, Card } from 'react-bootstrap';
import { Link } from 'react-router';
import dayjs from 'dayjs';

import { IPedido } from 'app/shared/model/pedido.model';
import { IPago } from 'app/shared/model/pago.model';
import { ORDER_STATUS_COLORS, ORDER_STATUS_LABELS, esCancelablePedido } from 'app/landing/utils/constants';
import { formatCOP } from 'app/landing/utils/format';

interface OrderCardProps {
  pedido: IPedido;
  pago?: IPago;
  onCancel?: () => void;
}

// El estado visual sale siempre del estado real en MongoDB: si la cancelacion
// ya fue procesada el pedido aparece CANCELLED ("Cancelado"); el pago refleja
// su propio estado ("No aprobado" si se rechazo, "Reembolsado" si se solicito).
const pagoBadge = (pedido: IPedido, pago?: IPago): { label: string; variant: string } | null => {
  if (pedido.estado === 'CANCELLED' || pedido.estado === 'RETURNED') {
    return null;
  }
  if (!pago) {
    return pedido.estado === 'PENDING' ? { label: 'Pago pendiente', variant: 'warning' } : null;
  }
  if (pago.estado === 'APPROVED') {
    return null;
  }
  if (pago.estado === 'REFUNDED') {
    return { label: 'Reembolsado', variant: 'secondary' };
  }
  if (pago.estado === 'REJECTED' || pago.estado === 'CANCELLED' || pago.estado === 'EXPIRED') {
    return { label: 'Pago no aprobado', variant: 'danger' };
  }
  return { label: 'Pago pendiente', variant: 'warning' };
};

export const OrderCard = ({ pedido, pago, onCancel }: OrderCardProps) => {
  const estado = pedido.estado || 'Pendiente';
  const badgeVariant = ORDER_STATUS_COLORS[estado] || 'secondary';
  const canCancel = esCancelablePedido(pedido);
  const pagoInfo = pagoBadge(pedido, pago);

  return (
    <Card className="mb-3">
      <Card.Body>
        <div className="d-flex flex-wrap justify-content-between align-items-start gap-2 mb-3">
          <div>
            <div className="text-muted small">Pedido</div>
            <div className="fw-bold">#{pedido.numeroPedido || pedido.id}</div>
          </div>
          <div className="d-flex gap-2">
            {pagoInfo && <Badge bg={pagoInfo.variant}>{pagoInfo.label}</Badge>}
            <Badge bg={badgeVariant}>{ORDER_STATUS_LABELS[estado] || estado}</Badge>
          </div>
        </div>
        <div className="d-flex flex-wrap justify-content-between gap-3 mb-3">
          <div>
            <div className="text-muted small">Fecha</div>
            <div>{pedido.createdDate ? dayjs(pedido.createdDate).format('DD/MM/YYYY') : '-'}</div>
          </div>
          <div>
            <div className="text-muted small">Total</div>
            <div className="fw-bold h5">{formatCOP(pedido.total)}</div>
          </div>
          <div>
            <div className="text-muted small">Envío</div>
            <div>{pedido.costoEnvio ? formatCOP(pedido.costoEnvio) : 'Por calcular'}</div>
          </div>
        </div>
        <div className="d-flex gap-2">
          <Link to={`/mi-cuenta/pedidos/${pedido.id}`} className="btn btn-outline-primary btn-sm flex-grow-1">
            Ver detalle
          </Link>
          {canCancel && onCancel && (
            <Button variant="outline-danger" size="sm" onClick={onCancel}>
              Cancelar
            </Button>
          )}
        </div>
      </Card.Body>
    </Card>
  );
};

export default OrderCard;

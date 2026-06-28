import React from 'react';
import { Badge, Button, Card } from 'react-bootstrap';
import { Link } from 'react-router';
import dayjs from 'dayjs';

import { IPedido } from 'app/shared/model/pedido.model';
import { ORDER_STATUS_COLORS, ORDER_STATUS_LABELS } from 'app/landing/utils/constants';
import { formatCOP } from 'app/landing/utils/format';

interface OrderCardProps {
  pedido: IPedido;
  onCancel?: () => void;
}

export const OrderCard = ({ pedido, onCancel }: OrderCardProps) => {
  const estado = pedido.estado || 'Pendiente';
  const badgeVariant = ORDER_STATUS_COLORS[estado] || 'secondary';
  const canCancel = estado === 'PENDING';

  return (
    <Card className="mb-3">
      <Card.Body>
        <div className="d-flex flex-wrap justify-content-between align-items-start gap-2 mb-3">
          <div>
            <div className="text-muted small">Pedido</div>
            <div className="fw-bold">#{pedido.numeroPedido || pedido.id}</div>
          </div>
          <Badge bg={badgeVariant}>{ORDER_STATUS_LABELS[estado] || estado}</Badge>
        </div>
        <div className="d-flex flex-wrap justify-content-between gap-3 mb-3">
          <div>
            <div className="text-muted small">Fecha</div>
            <div>{pedido.id ? dayjs().format('DD/MM/YYYY') : '-'}</div>
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
          <Link to={`/cuenta/pedidos/${pedido.id}`} className="btn btn-outline-primary btn-sm flex-grow-1">
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

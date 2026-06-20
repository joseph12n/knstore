import React from 'react';
import { Badge, Card } from 'react-bootstrap';
import { Link } from 'react-router';
import dayjs from 'dayjs';

import { IPedido } from 'app/shared/model/pedido.model';
import { ORDER_STATUS_COLORS, ORDER_STATUS_LABELS } from 'app/storefront/utils/constants';
import { formatCOP } from 'app/storefront/utils/format';

interface OrderCardProps {
  pedido: IPedido;
}

export const OrderCard = ({ pedido }: OrderCardProps) => {
  const estado = pedido.estado || 'Pendiente';
  const badgeVariant = ORDER_STATUS_COLORS[estado] || 'secondary';

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
        <Link to={`/cuenta/pedidos/${pedido.id}`} className="btn btn-outline-primary btn-sm w-100">
          Ver detalle
        </Link>
      </Card.Body>
    </Card>
  );
};

export default OrderCard;

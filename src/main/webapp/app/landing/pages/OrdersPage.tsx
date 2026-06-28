import React, { useEffect, useMemo, useState } from 'react';

import { Link } from 'react-router';
import { toast } from 'react-toastify';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getSession } from 'app/shared/reducers/authentication';
import { getEntities as getPedidos, partialUpdateEntity as partialUpdatePedido } from 'app/entities/pedido/pedido.reducer';
import { getEntities as getCuentas } from 'app/entities/cuenta/cuenta.reducer';
import OrderCard from 'app/landing/components/OrderCard';
import LoadingSpinner from 'app/landing/components/LoadingSpinner';
import EmptyState from 'app/landing/components/EmptyState';
import OrderCancelModal from 'app/landing/components/OrderCancelModal';

export const OrdersPage = () => {
  const dispatch = useAppDispatch();
  const account = useAppSelector(state => state.authentication.account);
  const pedidos = useAppSelector(state => state.pedido.entities) ?? [];
  const cuentas = useAppSelector(state => state.cuenta.entities) ?? [];
  const loading = useAppSelector(state => state.pedido.loading);

  const [pedidoToCancel, setPedidoToCancel] = useState<string | undefined>();
  const [isCancelling, setIsCancelling] = useState(false);

  useEffect(() => {
    dispatch(getSession());
    dispatch(getCuentas({ page: 0, size: 100, sort: 'primerNombre,asc' }));
    dispatch(getPedidos({ page: 0, size: 100, sort: 'numeroPedido,desc' }));
  }, [dispatch]);

  const cuentaUsuario = useMemo(() => cuentas.find(c => c.user?.login === account.login), [cuentas, account.login]);

  const pedidosUsuario = useMemo(
    () => pedidos.filter(p => p.cuenta?.id === cuentaUsuario?.id).sort((a, b) => (b.id || '').localeCompare(a.id || '')),
    [pedidos, cuentaUsuario],
  );

  const selectedPedido = useMemo(() => pedidosUsuario.find(p => p.id === pedidoToCancel), [pedidosUsuario, pedidoToCancel]);

  const handleCancel = async () => {
    if (!pedidoToCancel) return;
    setIsCancelling(true);
    try {
      await dispatch(
        partialUpdatePedido({
          id: pedidoToCancel,
          estado: 'CANCELLED',
        }),
      );
      toast.success('Pedido cancelado correctamente');
      dispatch(getPedidos({ page: 0, size: 100, sort: 'numeroPedido,desc' }));
    } catch {
      toast.error('No pudimos cancelar el pedido. Inténtalo de nuevo.');
    } finally {
      setIsCancelling(false);
      setPedidoToCancel(undefined);
    }
  };

  return (
    <div className="kn-fade-in">
      <h1 className="h2 fw-bold mb-4">Mis pedidos</h1>
      <Link to="/cuenta" className="text-muted small d-block mb-4">
        ← Volver a mi cuenta
      </Link>

      {loading ? (
        <LoadingSpinner fullScreen />
      ) : pedidosUsuario.length === 0 ? (
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
          {pedidosUsuario.map(pedido => (
            <OrderCard key={pedido.id} pedido={pedido} onCancel={() => setPedidoToCancel(pedido.id)} />
          ))}
        </div>
      )}

      <OrderCancelModal
        show={!!pedidoToCancel}
        onHide={() => setPedidoToCancel(undefined)}
        onConfirm={handleCancel}
        isSubmitting={isCancelling}
        numeroPedido={selectedPedido?.numeroPedido}
      />
    </div>
  );
};

export default OrdersPage;

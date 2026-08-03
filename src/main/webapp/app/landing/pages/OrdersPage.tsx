import React, { useEffect, useMemo, useState } from 'react';

import { Link } from 'react-router';
import { toast } from 'react-toastify';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getSession } from 'app/shared/reducers/authentication';
import { getEntities as getPedidos, partialUpdateEntity as partialUpdatePedido } from 'app/entities/pedido/pedido.reducer';
import { getCuentaByLogin, reset as resetCuenta } from 'app/entities/cuenta/cuenta.reducer';
import OrderCard from 'app/landing/components/OrderCard';
import LoadingSpinner from 'app/landing/components/LoadingSpinner';
import EmptyState from 'app/landing/components/EmptyState';
import OrderCancelModal from 'app/landing/components/OrderCancelModal';
import Pagination from 'app/landing/components/Pagination';

const ITEMS_PER_PAGE = 10;

export const OrdersPage = () => {
  const dispatch = useAppDispatch();
  const account = useAppSelector(state => state.authentication.account);
  const pedidos = useAppSelector(state => state.pedido.entities) ?? [];
  const totalItems = useAppSelector(state => state.pedido.totalItems ?? 0);
  const cuenta = useAppSelector(state => state.cuenta.entity);
  const loading = useAppSelector(state => state.pedido.loading || state.cuenta.loading);

  const [activePage, setActivePage] = useState(1);
  const [pedidoToCancel, setPedidoToCancel] = useState<string | undefined>();
  const [isCancelling, setIsCancelling] = useState(false);

  useEffect(() => {
    dispatch(getSession());
    if (account.login) {
      dispatch(getCuentaByLogin(account.login));
    }
    return () => {
      dispatch(resetCuenta());
    };
  }, [dispatch, account.login]);

  useEffect(() => {
    dispatch(
      getPedidos({
        page: activePage - 1,
        size: ITEMS_PER_PAGE,
        sort: 'numeroPedido,desc',
      }),
    );
  }, [dispatch, activePage]);

  // TODO backend: filtrar pedidos por cuentaId para evitar filtrar en cliente.
  const pedidosUsuario = useMemo(
    () => pedidos.filter(p => p.cuenta?.id === cuenta?.id).sort((a, b) => (b.id || '').localeCompare(a.id || '')),
    [pedidos, cuenta],
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
      dispatch(getPedidos({ page: activePage - 1, size: ITEMS_PER_PAGE, sort: 'numeroPedido,desc' }));
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
      <Link to="/mi-cuenta" className="text-muted small d-block mb-4">
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
          <Pagination activePage={activePage} itemsPerPage={ITEMS_PER_PAGE} totalItems={totalItems} onPageChange={setActivePage} />
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

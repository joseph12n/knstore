import React, { useEffect, useMemo, useState } from 'react';

import { Link } from 'react-router';
import { toast } from 'react-toastify';
import axios from 'axios';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities as getPedidos } from 'app/entities/pedido/pedido.reducer';
import useCuentaActual from 'app/landing/hooks/useCuentaActual';
import OrderCard from 'app/landing/components/OrderCard';
import LoadingSpinner from 'app/landing/components/LoadingSpinner';
import EmptyState from 'app/landing/components/EmptyState';
import OrderCancelModal from 'app/landing/components/OrderCancelModal';
import Pagination from 'app/landing/components/Pagination';
import { getApiErrorMessage } from 'app/landing/utils/apiError';

const ITEMS_PER_PAGE = 10;

export const OrdersPage = () => {
  const dispatch = useAppDispatch();
  const { account } = useCuentaActual();
  const pedidos = useAppSelector(state => state.pedido.entities) ?? [];
  const totalItems = useAppSelector(state => state.pedido.totalItems ?? 0);
  const loading = useAppSelector(state => state.pedido.loading);

  const [activePage, setActivePage] = useState(1);
  const [pedidoToCancel, setPedidoToCancel] = useState<string | undefined>();
  const [isCancelling, setIsCancelling] = useState(false);

  useEffect(() => {
    // El backend pagina y filtra por la cuenta del cliente autenticado.
    dispatch(
      getPedidos({
        page: activePage - 1,
        size: ITEMS_PER_PAGE,
        sort: 'numeroPedido,desc',
      }),
    );
  }, [dispatch, activePage, account.login]);

  const selectedPedido = useMemo(() => pedidos.find(p => p.id === pedidoToCancel), [pedidos, pedidoToCancel]);

  const handleCancel = async () => {
    if (!pedidoToCancel) return;
    setIsCancelling(true);
    try {
      await axios.post(`api/pedidos/${pedidoToCancel}/cancelar`);
      toast.success('Pedido cancelado correctamente');
      dispatch(getPedidos({ page: activePage - 1, size: ITEMS_PER_PAGE, sort: 'numeroPedido,desc' }));
    } catch (error) {
      toast.error(getApiErrorMessage(error, 'No pudimos cancelar el pedido. Inténtalo de nuevo.'));
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

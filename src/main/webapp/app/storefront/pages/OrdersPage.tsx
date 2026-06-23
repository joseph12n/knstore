import React, { useEffect, useMemo } from 'react';
import { Container } from 'react-bootstrap';
import { Link } from 'react-router';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getSession } from 'app/shared/reducers/authentication';
import { getEntities as getPedidos } from 'app/entities/pedido/pedido.reducer';
import { getEntities as getCuentas } from 'app/entities/cuenta/cuenta.reducer';
import OrderCard from 'app/storefront/components/OrderCard';
import LoadingSpinner from 'app/storefront/components/LoadingSpinner';
import EmptyState from 'app/storefront/components/EmptyState';

export const OrdersPage = () => {
  const dispatch = useAppDispatch();
  const account = useAppSelector(state => state.authentication.account);
  const pedidos = useAppSelector(state => state.pedido.entities) ?? [];
  const cuentas = useAppSelector(state => state.cuenta.entities) ?? [];
  const loading = useAppSelector(state => state.pedido.loading);

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

  return (
    <Container className="py-4 kn-fade-in">
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
            <OrderCard key={pedido.id} pedido={pedido} />
          ))}
        </div>
      )}
    </Container>
  );
};

export default OrdersPage;

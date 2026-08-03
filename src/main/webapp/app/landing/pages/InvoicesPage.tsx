import React, { useEffect, useMemo, useState } from 'react';
import { Badge, Button, Card, Table } from 'react-bootstrap';
import { Link } from 'react-router';
import dayjs from 'dayjs';
import axios from 'axios';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getSession } from 'app/shared/reducers/authentication';
import { getEntities as getFacturas } from 'app/entities/factura/factura.reducer';
import { getEntities as getPagos } from 'app/entities/pago/pago.reducer';
import { getEntities as getPedidos } from 'app/entities/pedido/pedido.reducer';
import { getCuentaByLogin, reset as resetCuenta } from 'app/entities/cuenta/cuenta.reducer';
import { IFactura } from 'app/shared/model/factura.model';
import LoadingSpinner from 'app/landing/components/LoadingSpinner';
import EmptyState from 'app/landing/components/EmptyState';
import Pagination from 'app/landing/components/Pagination';
import { formatCOP } from 'app/landing/utils/format';

const ITEMS_PER_PAGE = 10;

export const InvoicesPage = () => {
  const dispatch = useAppDispatch();
  const account = useAppSelector(state => state.authentication.account);
  const facturas = useAppSelector(state => state.factura.entities) ?? [];
  const totalItems = useAppSelector(state => state.factura.totalItems ?? 0);
  const pagos = useAppSelector(state => state.pago.entities) ?? [];
  const pedidos = useAppSelector(state => state.pedido.entities) ?? [];
  const cuenta = useAppSelector(state => state.cuenta.entity);
  const loading = useAppSelector(state => state.factura.loading || state.pago.loading || state.pedido.loading || state.cuenta.loading);

  const [activePage, setActivePage] = useState(1);
  const [downloadingId, setDownloadingId] = useState<string | null>(null);

  const handleDownload = async (factura: IFactura) => {
    if (!factura.id) return;
    setDownloadingId(factura.id);
    try {
      const response = await axios.get<Blob>(`api/facturas/${factura.id}/pdf`, { responseType: 'blob' });
      const url = window.URL.createObjectURL(response.data);
      const link = document.createElement('a');
      const filename = `${factura.prefijo || 'FAC'}-${factura.id}.pdf`;
      link.href = url;
      link.setAttribute('download', filename);
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      window.URL.revokeObjectURL(url);
    } catch {
      // fall back to legacy JSON download
      window.location.href = `api/facturas/${factura.id}/download`;
    } finally {
      setDownloadingId(null);
    }
  };

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
    // TODO backend: agregar filtro por cuentaId para paginar facturas del usuario directamente.
    dispatch(getPedidos({ page: 0, size: 1000, sort: 'numeroPedido,desc' }));
    dispatch(getPagos({ page: 0, size: 1000, sort: 'id,desc' }));
  }, [dispatch]);

  useEffect(() => {
    dispatch(getFacturas({ page: activePage - 1, size: ITEMS_PER_PAGE, sort: 'id,desc' }));
  }, [dispatch, activePage]);

  const pedidosUsuarioIds = useMemo(() => new Set(pedidos.filter(p => p.cuenta?.id === cuenta?.id).map(p => p.id)), [pedidos, cuenta]);

  const pagosUsuarioIds = useMemo(
    () => new Set(pagos.filter(p => p.pedido?.id && pedidosUsuarioIds.has(p.pedido.id)).map(p => p.id)),
    [pagos, pedidosUsuarioIds],
  );

  const facturasUsuario = useMemo(
    () => facturas.filter(f => f.pago?.id && pagosUsuarioIds.has(f.pago.id)).sort((a, b) => (b.id || '').localeCompare(a.id || '')),
    [facturas, pagosUsuarioIds],
  );

  if (loading) {
    return <LoadingSpinner fullScreen />;
  }

  if (facturasUsuario.length === 0) {
    return (
      <div className="kn-fade-in">
        <h1 className="h2 fw-bold mb-4">Mis facturas</h1>
        <EmptyState
          title="Aún no tienes facturas registradas"
          description="Cuando se emita una factura para tus pagos, podrás consultarla aquí."
          action={
            <Link to="/mi-cuenta/pagos" className="btn btn-primary">
              Ver mis pagos
            </Link>
          }
        />
      </div>
    );
  }

  return (
    <div className="kn-fade-in">
      <h1 className="h2 fw-bold mb-4">Mis facturas</h1>
      <Card>
        <Card.Body className="p-0">
          <Table responsive className="align-middle mb-0">
            <thead>
              <tr>
                <th>Factura</th>
                <th>Pedido</th>
                <th>Estado</th>
                <th>Total</th>
                <th>Fecha de emisión</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              {facturasUsuario.map(factura => (
                <tr key={factura.id}>
                  <td className="fw-semibold">{factura.prefijo || factura.id}</td>
                  <td>
                    <Link to={`/mi-cuenta/pedidos/${factura.pago?.pedido?.id}`}>
                      #{factura.pago?.pedido?.numeroPedido || factura.pago?.pedido?.id}
                    </Link>
                  </td>
                  <td>
                    <Badge bg={factura.enviada ? 'success' : 'warning'}>{factura.enviada ? 'Enviada' : 'Pendiente'}</Badge>
                  </td>
                  <td className="fw-semibold">{formatCOP(factura.total)}</td>
                  <td>{factura.fechaEmision ? dayjs(factura.fechaEmision).format('DD/MM/YYYY') : '-'}</td>
                  <td>
                    {factura.enviada !== undefined && (
                      <Button
                        variant="outline-primary"
                        size="sm"
                        onClick={() => handleDownload(factura)}
                        disabled={downloadingId === factura.id}
                      >
                        {downloadingId === factura.id ? 'Descargando...' : 'Descargar PDF'}
                      </Button>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        </Card.Body>
      </Card>
      <Pagination activePage={activePage} itemsPerPage={ITEMS_PER_PAGE} totalItems={totalItems} onPageChange={setActivePage} />
    </div>
  );
};

export default InvoicesPage;

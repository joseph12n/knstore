import React, { useEffect, useState } from 'react';
import { Badge, Button, Card, Table } from 'react-bootstrap';
import { Link } from 'react-router';
import dayjs from 'dayjs';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities as getFacturas } from 'app/entities/factura/factura.reducer';
import { IFactura } from 'app/shared/model/factura.model';
import useCuentaActual from 'app/landing/hooks/useCuentaActual';
import LoadingSpinner from 'app/landing/components/LoadingSpinner';
import EmptyState from 'app/landing/components/EmptyState';
import Pagination from 'app/landing/components/Pagination';
import { formatCOP } from 'app/landing/utils/format';
import { downloadFacturaPdf } from 'app/landing/utils/invoice';

const ITEMS_PER_PAGE = 10;

export const InvoicesPage = () => {
  const dispatch = useAppDispatch();
  const { account } = useCuentaActual();
  const facturas = useAppSelector(state => state.factura.entities) ?? [];
  const totalItems = useAppSelector(state => state.factura.totalItems ?? 0);
  const loading = useAppSelector(state => state.factura.loading);

  const [activePage, setActivePage] = useState(1);
  const [downloadingId, setDownloadingId] = useState<string | null>(null);

  const handleDownload = async (factura: IFactura) => {
    if (!factura.id) return;
    setDownloadingId(factura.id);
    try {
      await downloadFacturaPdf(factura.id, factura.prefijo);
    } catch {
      // fall back to legacy JSON download
      window.location.href = `api/facturas/${factura.id}/download`;
    } finally {
      setDownloadingId(null);
    }
  };

  useEffect(() => {
    // El backend pagina y filtra por la cuenta del cliente autenticado.
    dispatch(getFacturas({ page: activePage - 1, size: ITEMS_PER_PAGE, sort: 'id,desc' }));
  }, [dispatch, activePage, account.login]);

  if (loading) {
    return <LoadingSpinner fullScreen />;
  }

  if (facturas.length === 0) {
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
              {facturas.map(factura => (
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

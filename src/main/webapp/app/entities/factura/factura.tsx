import React, { useEffect, useState } from 'react';
import { Button, Table } from 'react-bootstrap';
import { JhiItemCount, JhiPagination, TextFormat, getPaginationState } from 'react-jhipster';
import { Link, useLocation, useNavigate } from 'react-router';

import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { overridePaginationStateWithQueryParams } from 'app/shared/util/entity-utils';
import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/shared/util/pagination.constants';

import { getEntities } from './factura.reducer';

export const Factura = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  const facturaList = useAppSelector(state => state.factura.entities);
  const loading = useAppSelector(state => state.factura.loading);
  const totalItems = useAppSelector(state => state.factura.totalItems);

  const getAllEntities = () => {
    dispatch(
      getEntities({
        page: paginationState.activePage - 1,
        size: paginationState.itemsPerPage,
        sort: `${paginationState.sort},${paginationState.order}`,
      }),
    );
  };

  const sortEntities = () => {
    getAllEntities();
    const endURL = `?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`;
    if (pageLocation.search !== endURL) {
      navigate(`${pageLocation.pathname}${endURL}`);
    }
  };

  useEffect(() => {
    sortEntities();
  }, [paginationState.activePage, paginationState.order, paginationState.sort]);

  useEffect(() => {
    const params = new URLSearchParams(pageLocation.search);
    const page = params.get('page');
    const sort = params.get(SORT);
    if (page && sort) {
      const sortSplit = sort.split(',');
      setPaginationState({
        ...paginationState,
        activePage: +page,
        sort: sortSplit[0],
        order: sortSplit[1],
      });
    }
  }, [pageLocation.search]);

  const sort = p => () => {
    setPaginationState({
      ...paginationState,
      order: paginationState.order === ASC ? DESC : ASC,
      sort: p,
    });
  };

  const handlePagination = currentPage =>
    setPaginationState({
      ...paginationState,
      activePage: currentPage,
    });

  const handleSyncList = () => {
    sortEntities();
  };

  const getSortIconByFieldName = (fieldName: string) => {
    const sortFieldName = paginationState.sort;
    const { order } = paginationState;
    if (sortFieldName !== fieldName) {
      return faSort;
    }
    return order === ASC ? faSortUp : faSortDown;
  };

  return (
    <div>
      <h2 id="factura-heading" data-cy="FacturaHeading">
        Facturas
        <div className="d-flex justify-content-end">
          <Button className="me-2" variant="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} /> Refrescar lista
          </Button>
          <Link to="/factura/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp; Crear nuevo Factura
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {facturaList?.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  ID <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('prefijo')}>
                  Prefijo <FontAwesomeIcon icon={getSortIconByFieldName('prefijo')} />
                </th>
                <th className="hand" onClick={sort('cufe')}>
                  Cufe <FontAwesomeIcon icon={getSortIconByFieldName('cufe')} />
                </th>
                <th className="hand" onClick={sort('subtotal')}>
                  Subtotal <FontAwesomeIcon icon={getSortIconByFieldName('subtotal')} />
                </th>
                <th className="hand" onClick={sort('descuentos')}>
                  Descuentos <FontAwesomeIcon icon={getSortIconByFieldName('descuentos')} />
                </th>
                <th className="hand" onClick={sort('baseGravableIva')}>
                  Base Gravable Iva <FontAwesomeIcon icon={getSortIconByFieldName('baseGravableIva')} />
                </th>
                <th className="hand" onClick={sort('valorIva')}>
                  Valor Iva <FontAwesomeIcon icon={getSortIconByFieldName('valorIva')} />
                </th>
                <th className="hand" onClick={sort('total')}>
                  Total <FontAwesomeIcon icon={getSortIconByFieldName('total')} />
                </th>
                <th className="hand" onClick={sort('notasAdicionales')}>
                  Notas Adicionales <FontAwesomeIcon icon={getSortIconByFieldName('notasAdicionales')} />
                </th>
                <th className="hand" onClick={sort('codigoQr')}>
                  Codigo Qr <FontAwesomeIcon icon={getSortIconByFieldName('codigoQr')} />
                </th>
                <th className="hand" onClick={sort('enviada')}>
                  Enviada <FontAwesomeIcon icon={getSortIconByFieldName('enviada')} />
                </th>
                <th className="hand" onClick={sort('fechaEmision')}>
                  Fecha Emision <FontAwesomeIcon icon={getSortIconByFieldName('fechaEmision')} />
                </th>
                <th className="hand" onClick={sort('fechaVencimiento')}>
                  Fecha Vencimiento <FontAwesomeIcon icon={getSortIconByFieldName('fechaVencimiento')} />
                </th>
                <th className="hand" onClick={sort('fechaEnvioEmail')}>
                  Fecha Envio Email <FontAwesomeIcon icon={getSortIconByFieldName('fechaEnvioEmail')} />
                </th>
                <th>
                  Pago <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {facturaList.map(factura => (
                <tr key={`entity-${factura.id}`} data-cy="entityTable">
                  <td>
                    <Button as={Link as any} to={`/factura/${factura.id}`} variant="link" size="sm">
                      {factura.id}
                    </Button>
                  </td>
                  <td>{factura.prefijo}</td>
                  <td>{factura.cufe}</td>
                  <td>{factura.subtotal}</td>
                  <td>{factura.descuentos}</td>
                  <td>{factura.baseGravableIva}</td>
                  <td>{factura.valorIva}</td>
                  <td>{factura.total}</td>
                  <td>{factura.notasAdicionales}</td>
                  <td>{factura.codigoQr}</td>
                  <td>{factura.enviada ? 'true' : 'false'}</td>
                  <td>{factura.fechaEmision ? <TextFormat type="date" value={factura.fechaEmision} format={APP_DATE_FORMAT} /> : null}</td>
                  <td>
                    {factura.fechaVencimiento ? (
                      <TextFormat type="date" value={factura.fechaVencimiento} format={APP_LOCAL_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td>
                    {factura.fechaEnvioEmail ? <TextFormat type="date" value={factura.fechaEnvioEmail} format={APP_DATE_FORMAT} /> : null}
                  </td>
                  <td>{factura.pago ? <Link to={`/pago/${factura.pago.id}`}>{factura.pago.id}</Link> : ''}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button as={Link as any} to={`/factura/${factura.id}`} variant="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" /> <span className="d-none d-md-inline">Vista</span>
                      </Button>
                      <Button
                        as={Link as any}
                        to={`/factura/${factura.id}/edit?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
                        variant="primary"
                        size="sm"
                        data-cy="entityEditButton"
                      >
                        <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Editar</span>
                      </Button>
                      <Button
                        onClick={() =>
                          (globalThis.location.href = `/factura/${factura.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`)
                        }
                        variant="danger"
                        size="sm"
                        data-cy="entityDeleteButton"
                      >
                        <FontAwesomeIcon icon="trash" /> <span className="d-none d-md-inline">Eliminar</span>
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        ) : (
          !loading && <div className="alert alert-warning">Ningún Facturas encontrado</div>
        )}
      </div>
      {totalItems ? (
        <div className={facturaList && facturaList.length > 0 ? '' : 'd-none'}>
          <div className="justify-content-center d-flex">
            <JhiItemCount page={paginationState.activePage} total={totalItems} itemsPerPage={paginationState.itemsPerPage} />
          </div>
          <div className="justify-content-center d-flex">
            <JhiPagination
              activePage={paginationState.activePage}
              onSelect={handlePagination}
              maxButtons={5}
              itemsPerPage={paginationState.itemsPerPage}
              totalItems={totalItems}
            />
          </div>
        </div>
      ) : (
        ''
      )}
    </div>
  );
};

export default Factura;

import React, { useEffect, useState } from 'react';
import { Button, Table } from 'react-bootstrap';
import { JhiItemCount, JhiPagination, getPaginationState } from 'react-jhipster';
import { Link, useLocation, useNavigate } from 'react-router';

import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { overridePaginationStateWithQueryParams } from 'app/shared/util/entity-utils';
import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/shared/util/pagination.constants';

import { getEntities } from './pedido.reducer';

export const Pedido = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  const pedidoList = useAppSelector(state => state.pedido.entities);
  const loading = useAppSelector(state => state.pedido.loading);
  const totalItems = useAppSelector(state => state.pedido.totalItems);

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
      <h2 id="pedido-heading" data-cy="PedidoHeading">
        Pedidos
        <div className="d-flex justify-content-end">
          <Button className="me-2" variant="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} /> Refrescar lista
          </Button>
          <Link to="/pedido/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp; Crear nuevo Pedido
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {pedidoList?.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  ID <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('numeroPedido')}>
                  Numero Pedido <FontAwesomeIcon icon={getSortIconByFieldName('numeroPedido')} />
                </th>
                <th className="hand" onClick={sort('estado')}>
                  Estado <FontAwesomeIcon icon={getSortIconByFieldName('estado')} />
                </th>
                <th className="hand" onClick={sort('subtotal')}>
                  Subtotal <FontAwesomeIcon icon={getSortIconByFieldName('subtotal')} />
                </th>
                <th className="hand" onClick={sort('descuento')}>
                  Descuento <FontAwesomeIcon icon={getSortIconByFieldName('descuento')} />
                </th>
                <th className="hand" onClick={sort('ivaTotal')}>
                  Iva Total <FontAwesomeIcon icon={getSortIconByFieldName('ivaTotal')} />
                </th>
                <th className="hand" onClick={sort('costoEnvio')}>
                  Costo Envio <FontAwesomeIcon icon={getSortIconByFieldName('costoEnvio')} />
                </th>
                <th className="hand" onClick={sort('total')}>
                  Total <FontAwesomeIcon icon={getSortIconByFieldName('total')} />
                </th>
                <th className="hand" onClick={sort('notasCliente')}>
                  Notas Cliente <FontAwesomeIcon icon={getSortIconByFieldName('notasCliente')} />
                </th>
                <th className="hand" onClick={sort('notasInternas')}>
                  Notas Internas <FontAwesomeIcon icon={getSortIconByFieldName('notasInternas')} />
                </th>
                <th className="hand" onClick={sort('ipOrigen')}>
                  Ip Origen <FontAwesomeIcon icon={getSortIconByFieldName('ipOrigen')} />
                </th>
                <th className="hand" onClick={sort('userAgent')}>
                  User Agent <FontAwesomeIcon icon={getSortIconByFieldName('userAgent')} />
                </th>
                <th>
                  Direccion <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  Cuenta <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {pedidoList.map(pedido => (
                <tr key={`entity-${pedido.id}`} data-cy="entityTable">
                  <td>
                    <Button as={Link as any} to={`/pedido/${pedido.id}`} variant="link" size="sm">
                      {pedido.id}
                    </Button>
                  </td>
                  <td>{pedido.numeroPedido}</td>
                  <td>{pedido.estado}</td>
                  <td>{pedido.subtotal}</td>
                  <td>{pedido.descuento}</td>
                  <td>{pedido.ivaTotal}</td>
                  <td>{pedido.costoEnvio}</td>
                  <td>{pedido.total}</td>
                  <td>{pedido.notasCliente}</td>
                  <td>{pedido.notasInternas}</td>
                  <td>{pedido.ipOrigen}</td>
                  <td>{pedido.userAgent}</td>
                  <td>{pedido.direccion ? <Link to={`/direccion/${pedido.direccion.id}`}>{pedido.direccion.id}</Link> : ''}</td>
                  <td>{pedido.cuenta ? <Link to={`/cuenta/${pedido.cuenta.id}`}>{pedido.cuenta.id}</Link> : ''}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button as={Link as any} to={`/pedido/${pedido.id}`} variant="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" /> <span className="d-none d-md-inline">Vista</span>
                      </Button>
                      <Button
                        as={Link as any}
                        to={`/pedido/${pedido.id}/edit?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
                        variant="primary"
                        size="sm"
                        data-cy="entityEditButton"
                      >
                        <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Editar</span>
                      </Button>
                      <Button
                        onClick={() =>
                          (globalThis.location.href = `/pedido/${pedido.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`)
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
          !loading && <div className="alert alert-warning">Ningún Pedidos encontrado</div>
        )}
      </div>
      {totalItems ? (
        <div className={pedidoList && pedidoList.length > 0 ? '' : 'd-none'}>
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

export default Pedido;

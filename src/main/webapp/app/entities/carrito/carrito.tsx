import React, { useEffect, useState } from 'react';
import { Button, Table } from 'react-bootstrap';
import { TextFormat, getSortState } from 'react-jhipster';
import { Link, useLocation, useNavigate } from 'react-router';

import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { overrideSortStateWithQueryParams } from 'app/shared/util/entity-utils';
import { ASC, DESC } from 'app/shared/util/pagination.constants';

import { getEntities } from './carrito.reducer';

export const Carrito = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [sortState, setSortState] = useState(overrideSortStateWithQueryParams(getSortState(pageLocation, 'id'), pageLocation.search));

  const carritoList = useAppSelector(state => state.carrito.entities);
  const loading = useAppSelector(state => state.carrito.loading);

  const getAllEntities = () => {
    dispatch(
      getEntities({
        sort: `${sortState.sort},${sortState.order}`,
      }),
    );
  };

  const sortEntities = () => {
    getAllEntities();
    const endURL = `?sort=${sortState.sort},${sortState.order}`;
    if (pageLocation.search !== endURL) {
      navigate(`${pageLocation.pathname}${endURL}`);
    }
  };

  useEffect(() => {
    sortEntities();
  }, [sortState.order, sortState.sort]);

  const sort = p => () => {
    setSortState({
      ...sortState,
      order: sortState.order === ASC ? DESC : ASC,
      sort: p,
    });
  };

  const handleSyncList = () => {
    sortEntities();
  };

  const getSortIconByFieldName = (fieldName: string) => {
    const sortFieldName = sortState.sort;
    const { order } = sortState;
    if (sortFieldName !== fieldName) {
      return faSort;
    }
    return order === ASC ? faSortUp : faSortDown;
  };

  return (
    <div>
      <h2 id="carrito-heading" data-cy="CarritoHeading">
        Carritos
        <div className="d-flex justify-content-end">
          <Button className="me-2" variant="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} /> Refrescar lista
          </Button>
          <Link to="/carrito/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp; Crear nuevo Carrito
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {carritoList?.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  ID <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('subtotal')}>
                  Subtotal <FontAwesomeIcon icon={getSortIconByFieldName('subtotal')} />
                </th>
                <th className="hand" onClick={sort('fechaActualizacion')}>
                  Fecha Actualizacion <FontAwesomeIcon icon={getSortIconByFieldName('fechaActualizacion')} />
                </th>
                <th>
                  Cuenta <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {carritoList.map(carrito => (
                <tr key={`entity-${carrito.id}`} data-cy="entityTable">
                  <td>
                    <Button as={Link as any} to={`/carrito/${carrito.id}`} variant="link" size="sm">
                      {carrito.id}
                    </Button>
                  </td>
                  <td>{carrito.subtotal}</td>
                  <td>
                    {carrito.fechaActualizacion ? (
                      <TextFormat type="date" value={carrito.fechaActualizacion} format={APP_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td>{carrito.cuenta ? <Link to={`/cuenta/${carrito.cuenta.id}`}>{carrito.cuenta.id}</Link> : ''}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button as={Link as any} to={`/carrito/${carrito.id}`} variant="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" /> <span className="d-none d-md-inline">Vista</span>
                      </Button>
                      <Button as={Link as any} to={`/carrito/${carrito.id}/edit`} variant="primary" size="sm" data-cy="entityEditButton">
                        <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Editar</span>
                      </Button>
                      <Button
                        onClick={() => (globalThis.location.href = `/carrito/${carrito.id}/delete`)}
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
          !loading && <div className="alert alert-warning">Ningún Carritos encontrado</div>
        )}
      </div>
    </div>
  );
};

export default Carrito;
